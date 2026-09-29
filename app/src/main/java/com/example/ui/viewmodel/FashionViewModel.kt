package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SavedLookEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.*
import com.example.data.repository.FashionRepository
import com.example.util.BajiVoiceStylist
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class AppScreen {
    ONBOARDING_CROWN,
    ONBOARDING_FORM,
    HOME,
    TRY_ON,
    FINAL_LOOK,
    NEAR_ME,
    GALLERY
}

data class FinalLookGenerationState(
    val isLoading: Boolean = false,
    val progressStep: String = "",
    val generatedLook: SavedLookEntity? = null,
    val showInterstitialAd: Boolean = false,
    val showConfetti: Boolean = false,
    val errorMessage: String? = null
)

class FashionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FashionRepository(application)
    val voiceStylist = BajiVoiceStylist(application)

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val savedLooks: StateFlow<List<SavedLookEntity>> = repository.savedLooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Form fields
    val formGender = MutableStateFlow("Male")
    val formHeightFt = MutableStateFlow(5.6f)
    val formWeightKg = MutableStateFlow(68f)
    val formBodyType = MutableStateFlow("Medium")
    val formSkinTone = MutableStateFlow("Medium Wheatish")
    val formCity = MutableStateFlow("Quetta")
    val formPhotoUri = MutableStateFlow<String?>(null)
    val formChestInches = MutableStateFlow(38f)
    val formWaistInches = MutableStateFlow(32f)

    // Category browsing & Module selections
    val selectedCategory = MutableStateFlow(FashionCategory.DRESS_TRY_ON)
    val isWeddingStudioActive = MutableStateFlow(false)

    // Final Look Slots
    val selectedDress = MutableStateFlow<FashionItem?>(null)
    val selectedShoes = MutableStateFlow<FashionItem?>(null)
    val selectedHair = MutableStateFlow<FashionItem?>(null)
    val selectedJewellery = MutableStateFlow<FashionItem?>(null)
    val selectedMehndi = MutableStateFlow<FashionItem?>(null)

    // Final Look budget filter
    val filterBudgetRs = MutableStateFlow(8000)

    // Modals
    val showScannerModal = MutableStateFlow(false)
    val showWeightPreviewModal = MutableStateFlow(false)
    val targetWeightKg = MutableStateFlow(60f)

    val showCoupleModal = MutableStateFlow(false)
    val coupleOccasion = MutableStateFlow("Walima")
    val partnerPhotoUri = MutableStateFlow<String?>(null)

    val showCatwalkModal = MutableStateFlow(false)
    val isCatwalkPlaying = MutableStateFlow(false)

    val showMemeModal = MutableStateFlow(false) // Shaadi Se Pehle vs Baad
    val memeSliderPosition = MutableStateFlow(0.5f)

    val showSuitabilityDialog = MutableStateFlow(false)
    val activeSuitabilityItem = MutableStateFlow<FashionItem?>(null)
    val activeSuitabilityScore = MutableStateFlow(95)
    val activeSuitabilityReason = MutableStateFlow("")

    val showPremiumDialog = MutableStateFlow(false)
    val userNotice = MutableStateFlow<String?>(null)

    private val _finalLookState = MutableStateFlow(FinalLookGenerationState())
    val finalLookState: StateFlow<FinalLookGenerationState> = _finalLookState.asStateFlow()

    init {
        viewModelScope.launch {
            userProfile.collect { profile ->
                if (profile != null && profile.isSetupComplete) {
                    formGender.value = profile.gender
                    formHeightFt.value = profile.heightFt
                    formWeightKg.value = profile.weightKg
                    formBodyType.value = profile.bodyType
                    formSkinTone.value = profile.skinTone
                    formCity.value = profile.city
                    formPhotoUri.value = profile.photoUri
                    formChestInches.value = profile.chestInches
                    formWaistInches.value = profile.waistInches
                    initializeDefaultEnsemble(profile.gender)
                } else if (profile == null) {
                    _currentScreen.value = AppScreen.ONBOARDING_CROWN
                    initializeDefaultEnsemble("Male")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceStylist.shutdown()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setGender(gender: String) {
        formGender.value = gender
        initializeDefaultEnsemble(gender)
    }

    fun setHeightFt(height: Float) {
        formHeightFt.value = Math.round(height * 10f) / 10f
        autoDetectBodyType()
    }

    fun setWeightKg(weight: Float) {
        formWeightKg.value = Math.round(weight).toFloat()
        autoDetectBodyType()
    }

    fun setBodyType(type: String) {
        formBodyType.value = type
    }

    fun setSkinTone(tone: String) {
        formSkinTone.value = tone
    }

    fun setCity(city: String) {
        formCity.value = city
    }

    fun setPhotoUri(uri: String?) {
        formPhotoUri.value = uri
    }

    private fun autoDetectBodyType() {
        val hMeters = formHeightFt.value * 0.3048f
        if (hMeters > 0) {
            val bmi = formWeightKg.value / (hMeters * hMeters)
            formBodyType.value = when {
                bmi < 19f -> "Slim"
                bmi in 19f..24.9f -> "Medium"
                bmi in 25f..29.9f -> "Chubby"
                else -> "Chubby"
            }
        }
    }

    fun simulateBodyScan() {
        viewModelScope.launch {
            delay(1200)
            val h = formHeightFt.value
            val isMale = formGender.value == "Male"
            formChestInches.value = if (isMale) 38f + (formWeightKg.value - 65f) * 0.15f else 36f + (formWeightKg.value - 60f) * 0.15f
            formWaistInches.value = 30f + (formWeightKg.value - 65f) * 0.2f
            showScannerModal.value = false
            userNotice.value = "AI Scanner: Height ${"%.1f".format(h)}ft, Chest ${formChestInches.value.toInt()}\", Waist ${formWaistInches.value.toInt()}\" Detected!"
        }
    }

    fun saveBodyProfile(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val profile = UserProfileEntity(
                id = 1,
                gender = formGender.value,
                heightFt = formHeightFt.value,
                weightKg = formWeightKg.value,
                bodyType = formBodyType.value,
                skinTone = formSkinTone.value,
                city = formCity.value,
                photoUri = formPhotoUri.value,
                chestInches = formChestInches.value,
                waistInches = formWaistInches.value,
                isSetupComplete = true,
                isPremium = userProfile.value?.isPremium ?: false,
                dailyLooksLeft = 2
            )
            repository.saveProfile(profile)
            _currentScreen.value = AppScreen.HOME
            onDone()
        }
    }

    fun initializeDefaultEnsemble(gender: String) {
        val dresses = FashionCatalog.getItemsForCategory(FashionCategory.DRESS_TRY_ON, gender)
        val shoes = FashionCatalog.getItemsForCategory(FashionCategory.SHOES, gender)
        val hair = FashionCatalog.getItemsForCategory(FashionCategory.HAIR_BEARD, gender)
        val jewel = FashionCatalog.getItemsForCategory(FashionCategory.JEWELLERY, gender)
        val mehndi = FashionCatalog.getItemsForCategory(FashionCategory.MEHNDI, gender)

        if (selectedDress.value == null || selectedDress.value?.gender != gender) {
            selectedDress.value = dresses.firstOrNull()
        }
        if (selectedShoes.value == null || selectedShoes.value?.gender != gender) {
            selectedShoes.value = shoes.firstOrNull()
        }
        if (selectedHair.value == null || selectedHair.value?.gender != gender) {
            selectedHair.value = hair.firstOrNull()
        }
        if (selectedJewellery.value == null || selectedJewellery.value?.gender != gender) {
            selectedJewellery.value = jewel.firstOrNull()
        }
        if (gender == "Female") {
            selectedMehndi.value = mehndi.firstOrNull()
        } else {
            selectedMehndi.value = null
        }
    }

    fun checkItemSuitability(item: FashionItem) {
        activeSuitabilityItem.value = item
        val h = userProfile.value?.heightFt ?: formHeightFt.value
        val isShort = h < 5.4f
        val isTall = h >= 5.9f
        val score = when {
            item.heightBoostInches > 0 && isShort -> 98
            item.name.contains("Straight", true) && isShort -> 96
            item.name.contains("Heels", true) && isTall -> 85
            else -> 94
        }
        activeSuitabilityScore.value = score
        activeSuitabilityReason.value = "Aapki ${"%.1f".format(h)}ft height ke hisab se ${item.heightTip}"
        showSuitabilityDialog.value = true
    }

    fun playBajiVoice(text: String) {
        voiceStylist.speak(text)
        userNotice.value = "AI Voice Stylist Baji bol rahi hain..."
    }

    fun selectForFinalLook(item: FashionItem) {
        when (item.category) {
            FashionCategory.DRESS_TRY_ON -> selectedDress.value = item
            FashionCategory.SHOES -> selectedShoes.value = item
            FashionCategory.HAIR_BEARD -> selectedHair.value = item
            FashionCategory.JEWELLERY -> selectedJewellery.value = item
            FashionCategory.MEHNDI -> selectedMehndi.value = item
            else -> {}
        }
        userNotice.value = "${item.name} Selected for Final Look!"
    }

    fun createFinalLook() {
        val dress = selectedDress.value ?: FashionCatalog.items.first { it.category == FashionCategory.DRESS_TRY_ON }
        val shoes = selectedShoes.value ?: FashionCatalog.items.first { it.category == FashionCategory.SHOES }
        val hair = selectedHair.value ?: FashionCatalog.items.first { it.category == FashionCategory.HAIR_BEARD }
        val jewellery = selectedJewellery.value ?: FashionCatalog.items.first { it.category == FashionCategory.JEWELLERY }
        val mehndi = selectedMehndi.value

        val profile = userProfile.value
        val height = profile?.heightFt ?: formHeightFt.value
        val bodyType = profile?.bodyType ?: formBodyType.value
        val skinTone = profile?.skinTone ?: formSkinTone.value
        val city = profile?.city ?: formCity.value
        val isPremium = profile?.isPremium ?: false

        val shoeBoost = shoes.heightBoostInches
        val calculatedHeight = height + (shoeBoost / 12f)
        val weather = FashionCatalog.getWeatherForCity(city)

        _finalLookState.value = FinalLookGenerationState(
            isLoading = true,
            progressStep = "AI Measuring Height (${"%.1f".format(height)}ft) Proportions...",
            generatedLook = null
        )

        viewModelScope.launch {
            delay(500)
            _finalLookState.value = _finalLookState.value.copy(
                progressStep = "Simulating Fabric Fall & Shoe Lift (+${shoeBoost} inch)..."
            )
            delay(600)
            _finalLookState.value = _finalLookState.value.copy(
                progressStep = "Checking Weather in $city (${weather.tempC}°C) & Color Harmony..."
            )
            delay(500)

            val totalBudget = dress.priceRs + shoes.priceRs + jewellery.priceRs
            val verdict = "Shandar Look! Original ${"%.1f".format(height)}ft par ${shoes.name} pehanne se aap real me ${"%.2f".format(calculatedHeight)}ft lagenge. Dress aur hairstyle ka balance perfect royal look de raha hai."
            val tip = "Aap ${"%.1f".format(height)}ft ho, vertical stripes aur ${shoes.name} aapko lamba dikhayega. ${if (skinTone.contains("Fair")) "Ruby Red aur Emerald Green" else "White, Navy Blue aur Mustard Gold"} best hai for your $skinTone skin tone."
            val colorAdvice = "Weather Tip: ${weather.wardrobeAdvice}"

            val newLook = SavedLookEntity(
                title = "${dress.name} Royal Ensemble",
                timestamp = System.currentTimeMillis(),
                dressName = dress.name,
                hairName = hair.name,
                jewelleryName = jewellery.name,
                shoesName = shoes.name,
                mehndiName = mehndi?.name,
                userHeightFt = height,
                calculatedHeightFt = calculatedHeight,
                bodyType = bodyType,
                skinTone = skinTone,
                city = city,
                weatherText = "${weather.tempC}°C ${weather.condition}",
                aiVerdict = verdict,
                aiStylingTip = tip,
                aiColorAdvice = colorAdvice,
                budgetRs = totalBudget,
                assignedDay = null,
                isFavorite = false,
                hasVideo = true
            )

            val id = repository.saveFinalLook(newLook)
            val savedWithId = newLook.copy(id = id)

            _finalLookState.value = FinalLookGenerationState(
                isLoading = false,
                progressStep = "",
                generatedLook = savedWithId,
                showInterstitialAd = !isPremium,
                showConfetti = true
            )

            // Auto speak Baji's voice recommendation
            voiceStylist.speak("Zabardast! Aapka Mukammal Look tayyar hai. Is footwear se aap ${"%.2f".format(calculatedHeight)}ft lagtay hain!")
        }
    }

    fun dismissInterstitialAd() {
        _finalLookState.value = _finalLookState.value.copy(showInterstitialAd = false)
    }

    fun dismissConfetti() {
        _finalLookState.value = _finalLookState.value.copy(showConfetti = false)
    }

    fun deleteLook(id: Long) {
        viewModelScope.launch {
            repository.deleteLook(id)
            userNotice.value = "Look deleted."
        }
    }

    fun assignDayToLook(id: Long, day: String?) {
        viewModelScope.launch {
            repository.assignDayToLook(id, day)
            userNotice.value = if (day != null) "Assigned to $day!" else "Removed from calendar"
        }
    }

    fun toggleFavorite(id: Long, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !currentFav)
        }
    }

    fun togglePremium(isPremium: Boolean) {
        viewModelScope.launch {
            repository.setPremium(isPremium)
            showPremiumDialog.value = false
            userNotice.value = if (isPremium) "VIP Club Activated (Rs 299/mo)! No Watermark & Unlimited HD Exports!" else "Free plan active."
        }
    }

    fun clearNotice() {
        userNotice.value = null
    }

    fun exportHdPhotoToGallery(look: SavedLookEntity, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val width = 1080
                val height = 1920
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                // Background
                val bgPaint = Paint().apply {
                    shader = LinearGradient(
                        0f, 0f, 0f, height.toFloat(),
                        intArrayOf(AndroidColor.parseColor("#000000"), AndroidColor.parseColor("#14141E"), AndroidColor.parseColor("#000000")),
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

                // Gold Border Frame
                val borderPaint = Paint().apply {
                    color = AndroidColor.parseColor("#D4AF37")
                    style = Paint.Style.STROKE
                    strokeWidth = 6f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(RectF(40f, 60f, width - 40f, height - 60f), 36f, 36f, borderPaint)

                // Crown & Header
                val headerPaint = Paint().apply {
                    color = AndroidColor.parseColor("#D4AF37")
                    textSize = 58f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("👑 AI NAP - HAR JISM KA LIBAAS", (width / 2).toFloat(), 180f, headerPaint)

                val subPaint = Paint().apply {
                    color = AndroidColor.parseColor("#FFFFFF")
                    textSize = 34f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Complete Proportional Look • ${look.city} (${look.weatherText})", (width / 2).toFloat(), 240f, subPaint)

                // Proportional Height Tag
                val tagBoxPaint = Paint().apply {
                    color = AndroidColor.parseColor("#D4AF37")
                    isAntiAlias = true
                }
                val tagRect = RectF(100f, 300f, width - 100f, 420f)
                canvas.drawRoundRect(tagRect, 20f, 20f, tagBoxPaint)

                val tagTextPaint = Paint().apply {
                    color = AndroidColor.BLACK
                    textSize = 38f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Original: ${"%.1f".format(look.userHeightFt)}ft  ➔  With Shoes: ${"%.2f".format(look.calculatedHeightFt)}ft", (width / 2).toFloat(), 360f, tagTextPaint)
                canvas.drawText("Body: ${look.bodyType} • Tone: ${look.skinTone}", (width / 2).toFloat(), 405f, Paint().apply {
                    color = AndroidColor.parseColor("#332200")
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                })

                // Ensemble Items
                val itemHeaderPaint = Paint().apply {
                    color = AndroidColor.parseColor("#FFDF73")
                    textSize = 36f
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                val itemValPaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 34f
                    isAntiAlias = true
                }

                var y = 500f
                val itemsList = listOf(
                    "Outfit / Dress" to look.dressName,
                    "Footwear & Shoes" to look.shoesName,
                    "Hair & Beard" to look.hairName,
                    "Jewellery & Watch" to look.jewelleryName,
                    "Mehndi Art" to (look.mehndiName ?: "N/A"),
                    "Total Budget" to "Rs ${look.budgetRs}"
                )

                for ((label, value) in itemsList) {
                    canvas.drawText("$label:", 100f, y, itemHeaderPaint)
                    canvas.drawText(value, 100f, y + 44f, itemValPaint)
                    y += 110f
                }

                // AI Verdict
                val aiBoxPaint = Paint().apply {
                    color = AndroidColor.parseColor("#181824")
                    isAntiAlias = true
                }
                val aiRect = RectF(90f, y, width - 90f, y + 360f)
                canvas.drawRoundRect(aiRect, 20f, 20f, aiBoxPaint)

                canvas.drawText("✨ Baji AI Recommendation:", 120f, y + 55f, Paint().apply {
                    color = AndroidColor.parseColor("#0E8A5E")
                    textSize = 34f
                    isFakeBoldText = true
                    isAntiAlias = true
                })

                val aiTextPaint = Paint().apply {
                    color = AndroidColor.parseColor("#E5E5E5")
                    textSize = 28f
                    isAntiAlias = true
                }
                drawMultiline(canvas, look.aiStylingTip, 120f, y + 105f, 840f, aiTextPaint)
                drawMultiline(canvas, look.aiColorAdvice, 120f, y + 230f, 840f, aiTextPaint)

                // Watermark check
                val isPrem = userProfile.value?.isPremium ?: false
                if (!isPrem) {
                    val wmPaint = Paint().apply {
                        color = AndroidColor.parseColor("#80D4AF37")
                        textSize = 32f
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.drawText("Created with AI NAP • Free Edition (Upgrade for No Watermark)", (width / 2).toFloat(), height - 120f, wmPaint)
                } else {
                    val vPaint = Paint().apply {
                        color = AndroidColor.parseColor("#D4AF37")
                        textSize = 30f
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.drawText("👑 AI NAP VIP Ultra-HD Masterpiece • Zero Watermark", (width / 2).toFloat(), height - 120f, vPaint)
                }

                val filename = "AINAP_Look_${System.currentTimeMillis()}.jpg"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos)
                fos.flush()
                fos.close()

                onComplete(true, file.absolutePath)
            } catch (e: Exception) {
                onComplete(false, e.localizedMessage ?: "Error saving")
            }
        }
    }

    private fun drawMultiline(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, paint: Paint) {
        var currentY = y
        val words = text.split(" ")
        val currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) < maxWidth) {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            } else {
                canvas.drawText(currentLine.toString(), x, currentY, paint)
                currentY += paint.textSize * 1.35f
                currentLine.setLength(0)
                currentLine.append(word)
            }
        }
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine.toString(), x, currentY, paint)
        }
    }
}
