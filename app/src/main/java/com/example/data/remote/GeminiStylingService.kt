package com.example.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.FashionItem
import com.example.data.model.HeightSuitabilityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

class GeminiStylingService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun evaluateItemSuitability(
        item: FashionItem,
        gender: String,
        heightFt: Float,
        weightKg: Float,
        bodyType: String,
        skinTone: String
    ): HeightSuitabilityResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val heightCm = (heightFt * 30.48f).toInt()

        val prompt = """
            You are an expert Pakistani fashion stylist for "AI Fit & Look Studio".
            Analyze if this item suits the user:
            - User: Gender: $gender, Height: ${"%.1f".format(heightFt)} ft ($heightCm cm), Weight: ${weightKg.toInt()} kg, Body Type: $bodyType, Skin Tone: $skinTone.
            - Fashion Item: "${item.name}" (${item.urduName}), Category: ${item.category.title}, Tag: ${item.tag}.
            - Item Height Tip: "${item.heightTip}"
            
            Respond in friendly Pakistani Urdu/English mix (Roman Urdu).
            Return ONLY a valid JSON object with these exact keys:
            {
              "isRecommended": boolean,
              "matchPercentage": integer between 65 and 99,
              "shortVerdictUrdu": "Short punchy Roman Urdu title like 'YES - Bilkul Suit Karega!' or 'YES - Height Ko Lamba Dikhayega!'",
              "detailedReason": "2-3 lines in Roman Urdu explaining why it suits this exact height ($heightFt ft) and body type ($bodyType), mentioning vertical lines, cuts, and proportion.",
              "stylingAdvice": "1 practical tip on how to carry it (e.g., shoe pairing or waistline adjustment).",
              "colorAdvice": "Best color recommendation for $skinTone skin tone."
            }
        """.trimIndent()

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("GeminiService", "Gemini API key is placeholder or empty, using smart expert rule engine fallback.")
            return@withContext getRuleBasedSuitability(item, heightFt, bodyType, skinTone)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val jsonPayload = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.4)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonPayload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val rootJson = JSONObject(responseBody)
                val candidates = rootJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    val cleanText = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val parsed = JSONObject(cleanText)
                    return@withContext HeightSuitabilityResult(
                        isRecommended = parsed.optBoolean("isRecommended", true),
                        matchPercentage = parsed.optInt("matchPercentage", 88),
                        shortVerdictUrdu = parsed.optString("shortVerdictUrdu", "YES - 100% Suit Karega!"),
                        detailedReason = parsed.optString("detailedReason", item.heightTip),
                        stylingAdvice = parsed.optString("stylingAdvice", "Isko clean straight posture ke sath carry karein."),
                        colorAdvice = parsed.optString("colorAdvice", "Aapke $skinTone skin tone par deep shades bohot suit karein ge.")
                    )
                }
            }
            Log.e("GeminiService", "API error: ${response.code} ${response.message}")
            getRuleBasedSuitability(item, heightFt, bodyType, skinTone)
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception calling Gemini", e)
            getRuleBasedSuitability(item, heightFt, bodyType, skinTone)
        }
    }

    suspend fun generateCompleteLookCritique(
        dress: FashionItem,
        shoes: FashionItem,
        hair: FashionItem,
        jewellery: FashionItem,
        mehndi: FashionItem?,
        heightFt: Float,
        bodyType: String,
        skinTone: String,
        gender: String,
        photoUri: String?
    ): Triple<String, String, String> = withContext(Dispatchers.IO) {
        val calculatedHeight = heightFt + (shoes.heightBoostInches / 12f)
        val apiKey = BuildConfig.GEMINI_API_KEY

        val prompt = """
            You are a premier Pakistani fashion designer and runway stylist.
            The user created their Complete Look on "AI Fit & Look Studio":
            - Person: $gender, Original Height: ${"%.1f".format(heightFt)}ft, Footwear Boosted Height: ${"%.2f".format(calculatedHeight)}ft, Body Type: $bodyType, Skin Tone: $skinTone.
            - Selected Ensemble:
              * Outfit: ${dress.name} (${dress.urduName})
              * Shoes: ${shoes.name} (+${shoes.heightBoostInches} inch height lift)
              * Hair & Grooming: ${hair.name}
              * Jewellery/Watch: ${jewellery.name}
              ${if (mehndi != null) "* Mehndi: ${mehndi.name}" else ""}

            Provide:
            1. "verdict": Overall look verdict for their ${"%.1f".format(heightFt)}ft frame in Roman Urdu (e.g. "Royal aur perfectly proportionate! Shoes ke lift se aap 5.8ft lagtay hain aur dress ka fall bilkul perfect hai.")
            2. "stylingTip": Specific height proportion tip for this dress & shoes combo.
            3. "colorAdvice": Best color tone recommendation for their $skinTone skin tone (e.g. "Is height aur skin tone ke liye Emerald Green ya Royal Navy Blue best hai").

            Respond in JSON:
            {
               "verdict": "...",
               "stylingTip": "...",
               "colorAdvice": "..."
            }
        """.trimIndent()

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getRuleBasedLookCritique(dress, shoes, hair, heightFt, calculatedHeight, bodyType, skinTone)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val partsArray = JSONArray()

            // If user uploaded a photo, we can pass it as inlineData!
            if (!photoUri.isNullOrBlank()) {
                val base64Image = readImageAsBase64(photoUri)
                if (base64Image != null) {
                    val imagePart = JSONObject().apply {
                        val inlineData = JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", base64Image)
                        }
                        put("inlineData", inlineData)
                    }
                    partsArray.put(imagePart)
                }
            }

            partsArray.put(JSONObject().apply { put("text", prompt) })

            val jsonPayload = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.5)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonPayload.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val rootJson = JSONObject(responseBody)
                val text = rootJson.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrBlank()) {
                    val cleanText = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val parsed = JSONObject(cleanText)
                    return@withContext Triple(
                        parsed.optString("verdict", "Zabardast Look! Aapki body frame par ye bohot proportionate lag raha hai."),
                        parsed.optString("stylingTip", "Straight posture aur balanced waistline se aapka appearance tall lagega."),
                        parsed.optString("colorAdvice", "Is height aur skin tone ke liye contrast aur jewel tones best hain.")
                    )
                }
            }
            getRuleBasedLookCritique(dress, shoes, hair, heightFt, calculatedHeight, bodyType, skinTone)
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception in complete look critique", e)
            getRuleBasedLookCritique(dress, shoes, hair, heightFt, calculatedHeight, bodyType, skinTone)
        }
    }

    private fun readImageAsBase64(uriString: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (bitmap != null) {
                val maxDim = 800
                val ratio = Math.min(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
                val scaled = if (ratio < 1f) {
                    Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
                } else bitmap
                val stream = ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 75, stream)
                Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
            } else null
        } catch (e: Exception) {
            Log.e("GeminiService", "Error reading image", e)
            null
        }
    }

    private fun getRuleBasedSuitability(
        item: FashionItem,
        heightFt: Float,
        bodyType: String,
        skinTone: String
    ): HeightSuitabilityResult {
        val isSlim = bodyType.equals("Slim", ignoreCase = true)
        val isChubby = bodyType.equals("Chubby", ignoreCase = true)
        val isShort = heightFt < 5.4f
        val isTall = heightFt >= 5.9f

        val match = when {
            isShort && item.heightBoostInches > 0f -> 98
            isShort && item.name.contains("Straight", true) -> 95
            isTall && item.name.contains("Heels", true) -> 82
            isChubby && item.name.contains("Fitted", true) -> 75
            else -> 92
        }

        val verdict = if (match >= 85) "YES - Bilkul Suit Karega!" else "MODERATE - Thori Adjustments Ke Sath"
        val reason = buildString {
            append("Aapki ${"%.1f".format(heightFt)}ft height aur $bodyType body type ke hisab se ")
            if (item.heightBoostInches > 0) {
                append("ye footwear aapko +${item.heightBoostInches} inch visual boost dega jisse overall frame elongated dikhega. ")
            } else {
                append("iski vertical lines aur balanced silhouette body proportions ko khubsurat harmony deti hain. ")
            }
            append(item.heightTip)
        }

        val styling = when {
            isShort -> "Trousers/pants ko ankle se bilkul neat rakhein taakay legs lambi show hon."
            isChubby -> "Darker monochrome palette select karein taakay slim aur streamlined look milay."
            else -> "Confidence aur straight posture ke saath carry karein, cut aap par perfect hai."
        }

        val color = when (skinTone.lowercase()) {
            "light", "fair / light" -> "Royal Emerald Green, Ruby Red, Sapphire Blue aur Deep Burgundy aapko radiate kareinge."
            "dark", "dusky / dark" -> "Warm Mustard Gold, Champagne Cream, Olive Green aur Crisp White bohot royal contrast banayeinge."
            else -> "Navy Blue, Peach Blush, Maroon aur Rich Teal tones aapke wheatish complexion par ideal hain."
        }

        return HeightSuitabilityResult(
            isRecommended = match >= 80,
            matchPercentage = match,
            shortVerdictUrdu = verdict,
            detailedReason = reason,
            stylingAdvice = styling,
            colorAdvice = color
        )
    }

    private fun getRuleBasedLookCritique(
        dress: FashionItem,
        shoes: FashionItem,
        hair: FashionItem,
        heightFt: Float,
        calculatedHeight: Float,
        bodyType: String,
        skinTone: String
    ): Triple<String, String, String> {
        val verdict = "Shandar Look! Original ${"%.1f".format(heightFt)}ft par ${shoes.name} pehanne se aap real mein ${"%.2f".format(calculatedHeight)}ft lagenge. Dress aur hair ka combination bilkul majestic lag raha hai."
        val tip = "Is height ke liye ${dress.name} ki clean fall aur ${hair.name} ka profile neck ko visually elevate karta hai."
        val color = "Aapke $skinTone skin tone ke liye Royal Gold accents, Deep Maroon aur Classic Navy ka match sab se premium lagega."
        return Triple(verdict, tip, color)
    }
}
