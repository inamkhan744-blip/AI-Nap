package com.example.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.R
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
import java.io.File
import java.io.FileOutputStream
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
            You are an expert Pakistani fashion stylist for "AI NAP - Har Jism Ka Libaas".
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
            getRuleBasedSuitability(item, heightFt, bodyType, skinTone)
        } catch (e: Exception) {
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
            You are an expert Pakistani fashion stylist for AI NAP - Har Jism Ka Libaas.
            The user created their Complete Look:
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
            3. "colorAdvice": Best color tone recommendation for their $skinTone skin tone.

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
            getRuleBasedLookCritique(dress, shoes, hair, heightFt, calculatedHeight, bodyType, skinTone)
        }
    }

    /**
     * REAL HD IMAGE GENERATION:
     * Calls Gemini 2.5 Flash Image Model (gemini-2.5-flash-image) or falls back to
     * a high-resolution authentic Pakistani fashion model photo for that exact dress.
     * Saves the real JPG file to disk and returns Pair(filePath, drawableId).
     */
    suspend fun generateRealHDLookImage(
        dress: FashionItem,
        shoes: FashionItem,
        hair: FashionItem,
        jewellery: FashionItem,
        gender: String,
        heightFt: Float,
        bodyType: String,
        skinTone: String,
        userPhotoUri: String?
    ): Pair<String, Int> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val fallbackDrawableId = getMatchingModelDrawable(gender, dress.name)

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=$apiKey"
                val promptText = "Full body 9:16 high-fashion editorial portrait of a $gender Pakistani model standing full length in an illuminated photography studio, exact ${"%.1f".format(heightFt)}ft height proportion, $bodyType body build, $skinTone complexion. Wearing tailored ${dress.name} (${dress.description}), matching ${shoes.name}, styled ${hair.name}, adorned with ${jewellery.name}. Photorealistic 8k, elegant drapery, flawless head to toe fashion editorial photography, cinematic studio lighting."

                val partsArray = JSONArray()

                // If user provided a face photo, include it for facial reference
                if (!userPhotoUri.isNullOrBlank()) {
                    val base64 = readImageAsBase64(userPhotoUri)
                    if (base64 != null) {
                        partsArray.put(JSONObject().apply {
                            put("inlineData", JSONObject().apply {
                                put("mimeType", "image/jpeg")
                                put("data", base64)
                            })
                        })
                    }
                }

                partsArray.put(JSONObject().apply { put("text", promptText) })

                val jsonPayload = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply { put("parts", partsArray) })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("responseModalities", JSONArray().apply {
                            put("TEXT")
                            put("IMAGE")
                        })
                        put("imageConfig", JSONObject().apply {
                            put("aspectRatio", "9:16")
                            put("imageSize", "1K")
                        })
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonPayload.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val root = JSONObject(responseBody)
                    val candidates = root.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val parts = firstCandidate?.optJSONObject("content")?.optJSONArray("parts")

                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.optJSONObject(i)
                            val inlineData = part?.optJSONObject("inlineData")
                            if (inlineData != null) {
                                val b64Data = inlineData.optString("data")
                                if (!b64Data.isNullOrBlank()) {
                                    val imageBytes = Base64.decode(b64Data, Base64.DEFAULT)
                                    val outFile = File(context.filesDir, "ai_look_${System.currentTimeMillis()}.jpg")
                                    val fos = FileOutputStream(outFile)
                                    fos.write(imageBytes)
                                    fos.flush()
                                    fos.close()
                                    return@withContext Pair(outFile.absolutePath, fallbackDrawableId)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Gemini image generation failed, using high-res Pakistani fashion model photo", e)
            }
        }

        // Generate real JPG file from the high-res Pakistani model drawable
        val realModelFile = saveDrawableAsJpgFile(fallbackDrawableId, dress.name)
        Pair(realModelFile.absolutePath, fallbackDrawableId)
    }

    fun getMatchingModelDrawable(gender: String, dressName: String): Int {
        val nameLower = dressName.lowercase()
        return if (gender.equals("Female", ignoreCase = true)) {
            when {
                nameLower.contains("lehenga") || nameLower.contains("bridal") || nameLower.contains("wedding") || nameLower.contains("gharara") ->
                    R.drawable.model_bridal_lehenga_female
                else ->
                    R.drawable.model_emerald_kurti_female
            }
        } else {
            when {
                nameLower.contains("sherwani") || nameLower.contains("dulha") ->
                    R.drawable.model_sherwani_male
                else ->
                    R.drawable.model_black_shalwar_male
            }
        }
    }

    private fun saveDrawableAsJpgFile(drawableId: Int, label: String): File {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        val file = File(context.filesDir, "model_${label.take(8).replace(" ", "_")}_${System.currentTimeMillis()}.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, fos)
        fos.flush()
        fos.close()
        return file
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
            null
        }
    }

    private fun getRuleBasedSuitability(
        item: FashionItem,
        heightFt: Float,
        bodyType: String,
        skinTone: String
    ): HeightSuitabilityResult {
        val isShort = heightFt < 5.4f
        val match = if (isShort && item.heightBoostInches > 0f) 98 else 95
        val verdict = "YES - Bilkul Suit Karega!"
        val reason = "Aapki ${"%.1f".format(heightFt)}ft height aur $bodyType body type ke hisab se iski vertical lines body proportions ko khubsurat harmony deti hain. ${item.heightTip}"
        val styling = if (isShort) "Trousers/pants ko ankle se bilkul neat rakhein taakay legs lambi show hon." else "Straight posture ke saath carry karein, cut aap par perfect hai."
        val color = "Aapke $skinTone skin tone par white aur deep jewel shades bohot royal lagenge."

        return HeightSuitabilityResult(
            isRecommended = true,
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
        val verdict = "Shandar Look! Original ${"%.1f".format(heightFt)}ft par ${shoes.name} pehanne se aap real me ${"%.2f".format(calculatedHeight)}ft lagenge. Dress aur hair ka combination bilkul majestic lag raha hai."
        val tip = "Is height ke liye ${dress.name} ki clean vertical fall aur ${shoes.name} ka elevation legs ko 2 inch elongated look deta hai."
        val color = "Aapke $skinTone skin tone ke liye Royal Gold accents aur deep tones ka match sab se premium lagega."
        return Triple(verdict, tip, color)
    }
}
