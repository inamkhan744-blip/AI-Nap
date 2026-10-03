package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.SavedLookEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.*
import com.example.data.repository.FashionRepository
import com.example.util.BajiVoiceStylist
import com.example.util.VideoExporter
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
    val geminiService = com.example.data.remote.GeminiStylingService(application)

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val savedLooks: StateFlow<List<SavedLookEntity>> = repository.savedLooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Form fields
    val formGender = MutableStateFlow("Male")
    val formName = MutableStateFlow("Hamza")
    val currentLanguage = MutableStateFlow("ur")
    val formHeightFt = MutableStateFlow(5.8f)
    val formWeightKg = MutableStateFlow(70f)
    val formBodyType = MutableStateFlow("Medium")
    val formSkinTone = MutableStateFlow("Medium Wheatish")
    val formCity = MutableStateFlow("Lahore")
    val formPhotoUri = MutableStateFlow<String?>(null)
    val formChestInches = MutableStateFlow(40f)
    val formWaistInches = MutableStateFlow(32f)

    // Seller Marketplace
    val sellerDresses: StateFlow<List<com.example.data.local.SellerDressEntity>> = repository.sellerDresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 360 degree rotation and view angles
    val modelRotationAngle = MutableStateFlow(0f)
    val isBackView = MutableStateFlow(false)

    // Layer-by-layer slots
    val selectedDress = MutableStateFlow<FashionItem?>(null)
    val selectedShoes = MutableStateFlow<FashionItem?>(null)
    val selectedHair = MutableStateFlow<FashionItem?>(null)
    val selectedJewellery = MutableStateFlow<FashionItem?>(null)
    val selectedMehndi = MutableStateFlow<FashionItem?>(null)
    val selectedGala = MutableStateFlow<FashionItem?>(null)
    val selectedCap = MutableStateFlow<FashionItem?>(null)
    val selectedDaman = MutableStateFlow<FashionItem?>(null)

    // Wishlist
    val wishlistItems = MutableStateFlow<List<FashionItem>>(emptyList())

    // Orders
    val placedOrders = MutableStateFlow<List<PlacedOrder>>(emptyList())
    val activeOrderDress = MutableStateFlow<FashionItem?>(null)
    val showOrderDialog = MutableStateFlow(false)

    // Style Advisor Chat
    val showStyleAdvisorModal = MutableStateFlow(false)
    val styleAdvisorMessages = MutableStateFlow<List<com.example.data.model.ChatMessage>>(emptyList())
    val isAdvisorLoading = MutableStateFlow(false)
    val selectedAdvisorOccasion = MutableStateFlow<String?>("Wedding / Walima")

    // Category browsing & Module selections
    val selectedCategory = MutableStateFlow(FashionCategory.DRESS_TRY_ON)
    val selectedPehnoCategory = MutableStateFlow(PehnoCategory.DRESS)
    val searchQuery = MutableStateFlow("")
    val showSellerDialog = MutableStateFlow(false)
    val showProfileEdit = MutableStateFlow(false)
    val isWeddingStudioActive = MutableStateFlow(false)

    // Final Look budget filter
    val filterBudgetRs = MutableStateFlow(8000)

    // Live Try-on preview split slider (0.0 = user photo, 1.0 = AI wearing dress)
    val liveTryOnSliderPos = MutableStateFlow(0.5f)

    // Modals
    val showScannerModal = MutableStateFlow(false)
    val showWeightPreviewModal = MutableStateFlow(false)
    val targetWeightKg = MutableStateFlow(65f)

    val showCoupleModal = MutableStateFlow(false)
    val coupleOccasion = MutableStateFlow("Walima")
    val partnerPhotoUri = MutableStateFlow<String?>(null)

    val showCatwalkModal = MutableStateFlow(false)
    val isCatwalkPlaying = MutableStateFlow(false)
    val activeCatwalkLook = MutableStateFlow<SavedLookEntity?>(null)

    val showMemeModal = MutableStateFlow(false)
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
        viewModelScope.launch {
            savedLooks.first { looks ->
                if (looks.isEmpty()) {
                    seedDefaultLook()
                }
                true
            }
        }
    }

    private fun seedDefaultLook() {
        viewModelScope.launch {
            val defaultLook = SavedLookEntity(
                title = "Classic Black Shalwar Kameez Royal Look",
                timestamp = System.currentTimeMillis(),
                dressName = "Classic Black Shalwar Kameez",
                hairName = "Side Parting Fade",
                jewelleryName = "Luxury Gold Watch",
                shoesName = "Handcrafted Black Velvet Khussa",
                userHeightFt = 6.0f,
                calculatedHeightFt = 6.17f,
                bodyType = "Medium",
                skinTone = "Medium Wheatish",
                city = "Quetta",
                weatherText = "12°C Chilly",
                aiVerdict = "YES - 100% Royal Match! 6.0ft frame par Classic Black Shalwar Kameez aur Black Khussa ki fall bilkul majestic lagti hai.",
                aiStylingTip = "Is height ke liye vertical pleats aur straight cut trousers aapki height proportion ko balanced aur authoritative look dete hain.",
                aiColorAdvice = "Black fabric aur Velvet Khussa ka match aapke Medium Wheatish skin tone par sab se royal lagega.",
                budgetRs = 5200,
                assignedDay = "Monday",
                isFavorite = true,
                hasVideo = true,
                imagePath = null,
                drawableResId = R.drawable.model_black_shalwar_male
            )
            repository.saveFinalLook(defaultLook)
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

    fun toggleLanguage() {
        currentLanguage.value = if (currentLanguage.value == "ur") "en" else "ur"
    }

    fun saveInitialProfile(name: String, gender: String, weightKg: Float, heightFt: Float, language: String, photoUri: String? = null) {
        viewModelScope.launch {
            val entity = UserProfileEntity(
                id = 1,
                name = name.ifBlank { "User" },
                gender = gender,
                heightFt = heightFt,
                weightKg = weightKg,
                language = language,
                photoUri = photoUri ?: formPhotoUri.value,
                isSetupComplete = true
            )
            repository.saveProfile(entity)
            formName.value = entity.name
            formGender.value = entity.gender
            formWeightKg.value = entity.weightKg
            formHeightFt.value = entity.heightFt
            formPhotoUri.value = entity.photoUri
            currentLanguage.value = entity.language
            initializeDefaultEnsemble(gender)
            _currentScreen.value = AppScreen.HOME
        }
    }

    fun updateUserPhotoUri(uri: String) {
        viewModelScope.launch {
            formPhotoUri.value = uri
            val current = userProfile.value
            val entity = if (current != null) {
                current.copy(photoUri = uri)
            } else {
                UserProfileEntity(
                    id = 1,
                    gender = formGender.value,
                    heightFt = formHeightFt.value,
                    weightKg = formWeightKg.value,
                    language = currentLanguage.value,
                    photoUri = uri,
                    isSetupComplete = true
                )
            }
            repository.saveProfile(entity)
            userNotice.value = if (currentLanguage.value == "ur") "آپ کا چہرہ ماڈل پر لگ گیا ہے!" else "Your face is now placed on the model!"
        }
    }

    fun processAndSetCameraPortrait(context: android.content.Context, bitmap: Bitmap) {
        viewModelScope.launch {
            val h = userProfile.value?.heightFt ?: formHeightFt.value
            val w = userProfile.value?.weightKg ?: formWeightKg.value
            val g = userProfile.value?.gender ?: formGender.value
            val processedPath = com.example.util.FaceOverlayService.processAndMapFace(
                context = context,
                rawBitmap = bitmap,
                heightFt = h,
                weightKg = w,
                gender = g
            ) ?: com.example.util.PortraitProcessor.processAndSavePortrait(context, bitmap)

            if (processedPath != null) {
                updateUserPhotoUri(processedPath)
                userNotice.value = if (currentLanguage.value == "ur") "پورٹریٹ کامیابی سے ماڈل پر لگ گیا!" else "Portrait captured and overlaid on model!"
            } else {
                userNotice.value = if (currentLanguage.value == "ur") "تصویر پروسیس کرنے میں خرابی" else "Failed to process camera portrait"
            }
        }
    }

    fun publishSellerDress(
        shopName: String,
        city: String,
        dressName: String,
        frontUrl: String,
        backUrl: String?,
        priceRs: Int,
        whatsappNumber: String,
        gender: String
    ) {
        viewModelScope.launch {
            val entity = com.example.data.local.SellerDressEntity(
                shopName = shopName,
                city = city,
                dressName = dressName,
                frontPhotoUrl = frontUrl,
                backPhotoUrl = backUrl,
                priceRs = priceRs,
                whatsappNumber = whatsappNumber,
                gender = gender
            )
            repository.addSellerDress(entity)
            userNotice.value = if (currentLanguage.value == "ur") "جوڑا کامیابی سے مارکیٹ میں شامل ہو گیا!" else "Dress successfully published!"
            showSellerDialog.value = false
        }
    }

    fun selectItemLayer(item: FashionItem) {
        when (item.pehnoCategory) {
            PehnoCategory.DRESS -> selectedDress.value = item
            PehnoCategory.GALA_COLLAR, PehnoCategory.GALA_DESIGN -> selectedGala.value = item
            PehnoCategory.TOPI_CAP -> selectedCap.value = item
            PehnoCategory.DAMAN_DESIGN -> selectedDaman.value = item
            PehnoCategory.MEHNDI -> selectedMehndi.value = item
            PehnoCategory.HAIRSTYLE -> selectedHair.value = item
            PehnoCategory.JEWELLERY -> selectedJewellery.value = item
            PehnoCategory.SHOES -> selectedShoes.value = item
        }
        userNotice.value = "${item.name} applied to model!"
    }

    fun addToWishlist(item: FashionItem) {
        if (wishlistItems.value.none { it.id == item.id }) {
            wishlistItems.value = wishlistItems.value + item
            userNotice.value = if (currentLanguage.value == "ur") "وشلسٹ میں شامل کر دیا گیا" else "Added to wishlist"
        }
    }

    fun removeFromWishlist(itemId: String) {
        wishlistItems.value = wishlistItems.value.filterNot { it.id == itemId }
        userNotice.value = if (currentLanguage.value == "ur") "وشلسٹ سے ہٹا دیا گیا" else "Removed from wishlist"
    }

    fun toggleWishlist(item: FashionItem) {
        if (wishlistItems.value.any { it.id == item.id }) {
            removeFromWishlist(item.id)
        } else {
            addToWishlist(item)
        }
    }

    fun openOrderSheet(item: FashionItem) {
        activeOrderDress.value = item
        showOrderDialog.value = true
    }

    fun recordOrder(order: PlacedOrder) {
        placedOrders.value = listOf(order) + placedOrders.value
        userNotice.value = if (currentLanguage.value == "ur") "آرڈر کامیابی سے محفوظ ہو گیا!" else "Order placed successfully!"
    }

    fun openStyleAdvisor(initialOccasion: String? = null) {
        if (!initialOccasion.isNullOrBlank()) {
            selectedAdvisorOccasion.value = initialOccasion
        }
        if (styleAdvisorMessages.value.isEmpty()) {
            val isUrdu = currentLanguage.value == "ur"
            val h = userProfile.value?.heightFt ?: formHeightFt.value
            val w = userProfile.value?.weightKg ?: formWeightKg.value
            val b = userProfile.value?.bodyType ?: formBodyType.value
            val welcomeText = if (isUrdu) {
                "السلام علیکم! میں آپ کا پہنو اے آئی اسٹائل ایڈوائزر ہوں۔ میں آپ کے قد (${"%.1f".format(h)}ft)، وزن (${w.toInt()}kg) اور جسمانی ساخت ($b) کے مطابق موقع کی مناسبت سے بہترین فیشن مشورہ دینے کے لیے حاضر ہوں۔ اوپر سے موقع منتخب کریں یا نیچے اپنا سوال پوچھیں!"
            } else {
                "Assalam-o-Alaikum! I am your Pehno AI Style Advisor powered by Gemini. I provide personalized Pakistani fashion advice for your height (${"%.1f".format(h)}ft), weight (${w.toInt()}kg), and body type ($b). Select an occasion above or ask me anything!"
            }
            styleAdvisorMessages.value = listOf(
                com.example.data.model.ChatMessage(
                    sender = com.example.data.model.MessageSender.ADVISOR,
                    text = welcomeText,
                    occasion = selectedAdvisorOccasion.value
                )
            )
        }
        showStyleAdvisorModal.value = true
    }

    fun closeStyleAdvisor() {
        showStyleAdvisorModal.value = false
    }

    fun clearAdvisorChat() {
        styleAdvisorMessages.value = emptyList()
        openStyleAdvisor()
    }

    fun sendAdvisorMessage(prompt: String, occasionOverride: String? = null) {
        if (prompt.isBlank() || isAdvisorLoading.value) return
        val currentOccasion = occasionOverride ?: selectedAdvisorOccasion.value
        val userMsg = com.example.data.model.ChatMessage(
            sender = com.example.data.model.MessageSender.USER,
            text = prompt.trim(),
            occasion = currentOccasion
        )
        styleAdvisorMessages.value = styleAdvisorMessages.value + userMsg
        isAdvisorLoading.value = true

        viewModelScope.launch {
            try {
                val profile = userProfile.value
                val gender = profile?.gender ?: formGender.value
                val heightFt = profile?.heightFt ?: formHeightFt.value
                val weightKg = profile?.weightKg ?: formWeightKg.value
                val bodyType = profile?.bodyType ?: formBodyType.value
                val skinTone = profile?.skinTone ?: formSkinTone.value
                val city = profile?.city ?: formCity.value
                val lang = currentLanguage.value

                val responseText = geminiService.askStyleAdvisor(
                    conversationHistory = styleAdvisorMessages.value,
                    userMessage = prompt.trim(),
                    occasion = currentOccasion,
                    gender = gender,
                    heightFt = heightFt,
                    weightKg = weightKg,
                    bodyType = bodyType,
                    skinTone = skinTone,
                    city = city,
                    lang = lang
                )

                val suggested = FashionCatalog.searchMultiStoreItems("", gender).take(2)

                val aiMsg = com.example.data.model.ChatMessage(
                    sender = com.example.data.model.MessageSender.ADVISOR,
                    text = responseText,
                    occasion = currentOccasion,
                    suggestedItems = suggested
                )
                styleAdvisorMessages.value = styleAdvisorMessages.value + aiMsg
            } catch (e: Exception) {
                val errMsg = com.example.data.model.ChatMessage(
                    sender = com.example.data.model.MessageSender.ADVISOR,
                    text = if (currentLanguage.value == "ur") "معذرت، فیشن مشورہ حاصل کرنے میں رکاوٹ آئی۔ براہ کرم دوبارہ کوشش فرمائیں۔" else "Could not retrieve advice at this moment. Please try again.",
                    occasion = currentOccasion
                )
                styleAdvisorMessages.value = styleAdvisorMessages.value + errMsg
            } finally {
                isAdvisorLoading.value = false
            }
        }
    }

    fun initializeDefaultEnsemble(gender: String) {
        val dresses = FashionCatalog.getItemsForPehnoCategory(PehnoCategory.DRESS, gender)
        val shoes = FashionCatalog.getItemsForPehnoCategory(PehnoCategory.SHOES, gender)
        val hair = FashionCatalog.getItemsForPehnoCategory(PehnoCategory.HAIRSTYLE, gender)
        val jewel = FashionCatalog.getItemsForPehnoCategory(PehnoCategory.JEWELLERY, gender)
        val mehndi = FashionCatalog.getItemsForPehnoCategory(PehnoCategory.MEHNDI, gender)
        val gala = if (gender == "Female") FashionCatalog.getItemsForPehnoCategory(PehnoCategory.GALA_DESIGN, gender)
                   else FashionCatalog.getItemsForPehnoCategory(PehnoCategory.GALA_COLLAR, gender)
        val cap = FashionCatalog.getItemsForPehnoCategory(PehnoCategory.TOPI_CAP, gender)
        val daman = FashionCatalog.getItemsForPehnoCategory(PehnoCategory.DAMAN_DESIGN, gender)

        selectedDress.value = dresses.firstOrNull()
        selectedShoes.value = shoes.firstOrNull()
        selectedHair.value = hair.firstOrNull()
        selectedGala.value = gala.firstOrNull()
        if (gender == "Female") {
            selectedJewellery.value = jewel.firstOrNull()
            selectedMehndi.value = mehndi.firstOrNull()
            selectedDaman.value = daman.firstOrNull()
            selectedCap.value = null
        } else {
            selectedCap.value = cap.firstOrNull()
            selectedJewellery.value = null
            selectedMehndi.value = null
            selectedDaman.value = null
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
        val gender = profile?.gender ?: formGender.value
        val photoUri = profile?.photoUri ?: formPhotoUri.value
        val isPremium = profile?.isPremium ?: false

        val shoeBoost = shoes.heightBoostInches
        val calculatedHeight = height + (shoeBoost / 12f)
        val weather = FashionCatalog.getWeatherForCity(city)

        _finalLookState.value = FinalLookGenerationState(
            isLoading = true,
            progressStep = "AI Generating Real HD Photo with Gemini (${"%.1f".format(height)}ft)...",
            generatedLook = null
        )

        viewModelScope.launch {
            // Real HD Image generation with Gemini or authentic Pakistani model photo
            val (realImagePath, modelDrawableId) = repository.generateLookImage(
                dress = dress,
                shoes = shoes,
                hair = hair,
                jewellery = jewellery,
                gender = gender,
                heightFt = height,
                bodyType = bodyType,
                skinTone = skinTone,
                userPhotoUri = photoUri
            )

            _finalLookState.value = _finalLookState.value.copy(
                progressStep = "Balancing Proportion & Fitting Fabric to ${"%.1f".format(height)}ft body..."
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
                hasVideo = true,
                imagePath = realImagePath,
                drawableResId = modelDrawableId
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

            activeCatwalkLook.value = savedWithId
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

    fun exportCatwalkVideo(look: SavedLookEntity, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            VideoExporter.generateCatwalkMp4Video(
                context = getApplication(),
                dressName = look.dressName,
                heightFt = look.userHeightFt,
                onComplete = onComplete
            )
        }
    }

    fun exportHdPhotoToGallery(look: SavedLookEntity, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val width = 1080
                val height = 1920
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                // 1. Draw Real Model / AI Photo as base
                val modelBitmap: Bitmap? = if (!look.imagePath.isNullOrBlank() && File(look.imagePath).exists()) {
                    BitmapFactory.decodeFile(look.imagePath)
                } else if (look.drawableResId != 0) {
                    BitmapFactory.decodeResource(context.resources, look.drawableResId)
                } else {
                    BitmapFactory.decodeResource(context.resources, R.drawable.model_black_shalwar_male)
                }

                if (modelBitmap != null) {
                    val srcRect = Rect(0, 0, modelBitmap.width, modelBitmap.height)
                    val dstRect = Rect(0, 0, width, height)
                    canvas.drawBitmap(modelBitmap, srcRect, dstRect, null)
                } else {
                    canvas.drawColor(AndroidColor.BLACK)
                }

                // Top & Bottom luxury gradient shades so text is crystal clear
                val topGradient = Paint().apply {
                    shader = LinearGradient(0f, 0f, 0f, 320f, AndroidColor.parseColor("#E6000000").toInt(), AndroidColor.TRANSPARENT, Shader.TileMode.CLAMP)
                }
                canvas.drawRect(0f, 0f, width.toFloat(), 320f, topGradient)

                val bottomGradient = Paint().apply {
                    shader = LinearGradient(0f, height - 500f, 0f, height.toFloat(), AndroidColor.TRANSPARENT, AndroidColor.parseColor("#F2000000").toInt(), Shader.TileMode.CLAMP)
                }
                canvas.drawRect(0f, height - 500f, width.toFloat(), height.toFloat(), bottomGradient)

                // Gold Border Frame
                val borderPaint = Paint().apply {
                    color = AndroidColor.parseColor("#D4AF37")
                    style = Paint.Style.STROKE
                    strokeWidth = 8f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(RectF(30f, 40f, width - 30f, height - 40f), 32f, 32f, borderPaint)

                // Header
                val headerPaint = Paint().apply {
                    color = AndroidColor.parseColor("#D4AF37")
                    textSize = 52f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("👑 AI NAP - HAR JISM KA LIBAAS", (width / 2).toFloat(), 130f, headerPaint)

                val subPaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 30f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("${look.dressName} • ${look.shoesName}", (width / 2).toFloat(), 185f, subPaint)

                // Bottom Proportional Height Pill
                val pillRect = RectF(80f, height - 380f, width - 80f, height - 260f)
                val pillPaint = Paint().apply {
                    color = AndroidColor.parseColor("#CC181824").toInt()
                    isAntiAlias = true
                }
                val pillBorder = Paint().apply {
                    color = AndroidColor.parseColor("#D4AF37")
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(pillRect, 20f, 20f, pillPaint)
                canvas.drawRoundRect(pillRect, 20f, 20f, pillBorder)

                val tagPaint = Paint().apply {
                    color = AndroidColor.parseColor("#FFDF73")
                    textSize = 36f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Original: ${"%.1f".format(look.userHeightFt)}ft  ➔  With Shoes: ${"%.2f".format(look.calculatedHeightFt)}ft", (width / 2).toFloat(), height - 325f, tagPaint)

                val bodyPaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 26f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Body: ${look.bodyType} • Tone: ${look.skinTone} • City: ${look.city}", (width / 2).toFloat(), height - 285f, bodyPaint)

                // Verdict snippet
                val verdictPaint = Paint().apply {
                    color = AndroidColor.parseColor("#E0E0E0")
                    textSize = 24f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText(look.aiVerdict.take(70) + "...", (width / 2).toFloat(), height - 200f, verdictPaint)

                // Watermark check
                val isPrem = userProfile.value?.isPremium ?: false
                val wmPaint = Paint().apply {
                    color = if (isPrem) AndroidColor.parseColor("#D4AF37") else AndroidColor.parseColor("#80D4AF37").toInt()
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                val wmText = if (isPrem) "👑 AI NAP VIP Ultra-HD Masterpiece • Zero Watermark" else "Created with AI NAP • Free Edition (Upgrade for No Watermark)"
                canvas.drawText(wmText, (width / 2).toFloat(), height - 90f, wmPaint)

                // Save file to pictures
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val targetDir = if (picturesDir.exists() || picturesDir.mkdirs()) picturesDir else context.filesDir
                val file = File(targetDir, "AINAP_HD_${System.currentTimeMillis()}.jpg")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 96, fos)
                fos.flush()
                fos.close()

                onComplete(true, file.absolutePath)
            } catch (e: Exception) {
                onComplete(false, e.localizedMessage ?: "Error saving photo")
            }
        }
    }

    fun exportPehnoModelLookPng(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val width = 1080
                val height = 1920
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                val dress = selectedDress.value
                val gender = formGender.value
                val shoes = selectedShoes.value
                val h = formHeightFt.value
                val w = formWeightKg.value

                val fallbackRes = if (gender == "Female") {
                    if (dress?.name?.contains("Lehenga", true) == true) R.drawable.model_bridal_lehenga_female
                    else R.drawable.model_emerald_kurti_female
                } else {
                    if (dress?.name?.contains("Sherwani", true) == true) R.drawable.model_sherwani_male
                    else R.drawable.model_black_shalwar_male
                }

                val modelBitmap = BitmapFactory.decodeResource(context.resources, fallbackRes)
                if (modelBitmap != null) {
                    val srcRect = Rect(0, 0, modelBitmap.width, modelBitmap.height)
                    val dstRect = Rect(0, 0, width, height)
                    canvas.drawBitmap(modelBitmap, srcRect, dstRect, null)
                } else {
                    canvas.drawColor(AndroidColor.BLACK)
                }

                // Draw user's own face on model's head if available
                val userPhoto = formPhotoUri.value ?: userProfile.value?.photoUri
                if (!userPhoto.isNullOrBlank()) {
                    try {
                        val userFaceBitmap = if (userPhoto.startsWith("content://") || userPhoto.startsWith("file://")) {
                            val inputStream = context.contentResolver.openInputStream(android.net.Uri.parse(userPhoto))
                            BitmapFactory.decodeStream(inputStream)
                        } else if (File(userPhoto).exists()) {
                            BitmapFactory.decodeFile(userPhoto)
                        } else null

                        if (userFaceBitmap != null) {
                            val alignment = com.example.util.FaceOverlayService.calculateHeadAlignment(h, w, gender)
                            val faceWidth = (240 * alignment.scaleFactorX).toInt()
                            val faceHeight = (290 * alignment.scaleFactorY).toInt()
                            val faceLeft = (width - faceWidth) / 2
                            val faceTop = (230 * (2.0f - alignment.scaleFactorY * 0.85f).coerceIn(0.85f, 1.25f)).toInt()
                            val faceDst = Rect(faceLeft, faceTop, faceLeft + faceWidth, faceTop + faceHeight)
                            canvas.drawBitmap(userFaceBitmap, Rect(0, 0, userFaceBitmap.width, userFaceBitmap.height), faceDst, Paint(Paint.ANTI_ALIAS_FLAG))

                            val faceBorder = Paint().apply {
                                color = AndroidColor.parseColor("#0F766E")
                                style = Paint.Style.STROKE
                                strokeWidth = 8f
                                isAntiAlias = true
                            }
                            canvas.drawRoundRect(RectF(faceDst), 120f, 120f, faceBorder)
                        }
                    } catch (_: Exception) {}
                }

                // Dark gradients top and bottom
                val topGradient = Paint().apply {
                    shader = LinearGradient(0f, 0f, 0f, 340f, AndroidColor.parseColor("#E6000000").toInt(), AndroidColor.TRANSPARENT, Shader.TileMode.CLAMP)
                }
                canvas.drawRect(0f, 0f, width.toFloat(), 340f, topGradient)

                val bottomGradient = Paint().apply {
                    shader = LinearGradient(0f, height - 450f, 0f, height.toFloat(), AndroidColor.TRANSPARENT, AndroidColor.parseColor("#F2000000").toInt(), Shader.TileMode.CLAMP)
                }
                canvas.drawRect(0f, height - 450f, width.toFloat(), height.toFloat(), bottomGradient)

                // Pehno Green frame
                val borderPaint = Paint().apply {
                    color = AndroidColor.parseColor("#0F766E")
                    style = Paint.Style.STROKE
                    strokeWidth = 10f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(RectF(24f, 24f, width - 24f, height - 24f), 32f, 32f, borderPaint)

                // Top Header Text
                val titlePaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 58f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("پہنو • Pehno - Pehen Ke Dekho", (width / 2).toFloat(), 130f, titlePaint)

                val subPaint = Paint().apply {
                    color = AndroidColor.parseColor("#A7F3D0")
                    textSize = 34f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("${dress?.name ?: "Pakistani Jora"} • ${dress?.storeSource ?: "All Pakistan Marketplace"}", (width / 2).toFloat(), 190f, subPaint)

                // Bottom badge
                val pillRect = RectF(60f, height - 320f, width - 60f, height - 160f)
                val pillPaint = Paint().apply {
                    color = AndroidColor.parseColor("#D9042F2E").toInt()
                    isAntiAlias = true
                }
                val pillBorder = Paint().apply {
                    color = AndroidColor.parseColor("#14B8A6")
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(pillRect, 24f, 24f, pillPaint)
                canvas.drawRoundRect(pillRect, 24f, 24f, pillBorder)

                val statPaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 36f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                val shoeLift = shoes?.heightBoostInches ?: 0f
                canvas.drawText("Body: ${w.toInt()}kg • Height: ${"%.1f".format(h)}ft ${if (shoeLift > 0) "(+${shoeLift.toInt()}\" with ${shoes?.name})" else ""}", (width / 2).toFloat(), height - 250f, statPaint)

                val commPaint = Paint().apply {
                    color = AndroidColor.parseColor("#A7F3D0")
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("10% Commission will go to Pehno App • Order via Pehno App", (width / 2).toFloat(), height - 195f, commPaint)

                // Save PNG
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val targetDir = if (picturesDir.exists() || picturesDir.mkdirs()) picturesDir else context.filesDir
                val file = File(targetDir, "Pehno_Look_${System.currentTimeMillis()}.png")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()

                onComplete(true, file.absolutePath)
            } catch (e: Exception) {
                onComplete(false, e.localizedMessage ?: "Failed to save look image")
            }
        }
    }

    fun exportDressPng(item: FashionItem, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val width = 1080
                val height = 1920
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                val fallbackRes = if (item.gender == "Female") {
                    if (item.name.contains("Lehenga", true)) R.drawable.model_bridal_lehenga_female
                    else R.drawable.model_emerald_kurti_female
                } else {
                    if (item.name.contains("Sherwani", true)) R.drawable.model_sherwani_male
                    else R.drawable.model_black_shalwar_male
                }

                val modelBitmap = BitmapFactory.decodeResource(context.resources, fallbackRes)
                if (modelBitmap != null) {
                    val srcRect = Rect(0, 0, modelBitmap.width, modelBitmap.height)
                    val dstRect = Rect(0, 0, width, height)
                    canvas.drawBitmap(modelBitmap, srcRect, dstRect, null)
                } else {
                    canvas.drawColor(AndroidColor.BLACK)
                }

                // Dark gradients
                val topGradient = Paint().apply {
                    shader = LinearGradient(0f, 0f, 0f, 320f, AndroidColor.parseColor("#E6000000").toInt(), AndroidColor.TRANSPARENT, Shader.TileMode.CLAMP)
                }
                canvas.drawRect(0f, 0f, width.toFloat(), 320f, topGradient)

                val bottomGradient = Paint().apply {
                    shader = LinearGradient(0f, height - 380f, 0f, height.toFloat(), AndroidColor.TRANSPARENT, AndroidColor.parseColor("#F2000000").toInt(), Shader.TileMode.CLAMP)
                }
                canvas.drawRect(0f, height - 380f, width.toFloat(), height.toFloat(), bottomGradient)

                val borderPaint = Paint().apply {
                    color = AndroidColor.parseColor("#0F766E")
                    style = Paint.Style.STROKE
                    strokeWidth = 10f
                    isAntiAlias = true
                }
                canvas.drawRoundRect(RectF(24f, 24f, width - 24f, height - 24f), 32f, 32f, borderPaint)

                val titlePaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 54f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("پہنو • Pehno - Pehen Ke Dekho", (width / 2).toFloat(), 120f, titlePaint)

                val namePaint = Paint().apply {
                    color = AndroidColor.parseColor("#A7F3D0")
                    textSize = 34f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("${item.name} (${item.urduName})", (width / 2).toFloat(), 180f, namePaint)

                val pricePaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 42f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("Rs. ${item.priceRs} • ${item.storeSource}", (width / 2).toFloat(), height - 220f, pricePaint)

                val noticePaint = Paint().apply {
                    color = AndroidColor.parseColor("#A7F3D0")
                    textSize = 30f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("10% Commission will go to Pehno App", (width / 2).toFloat(), height - 160f, noticePaint)

                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val targetDir = if (picturesDir.exists() || picturesDir.mkdirs()) picturesDir else context.filesDir
                val file = File(targetDir, "Pehno_${item.id}_${System.currentTimeMillis()}.png")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()

                onComplete(true, file.absolutePath)
            } catch (e: Exception) {
                onComplete(false, e.localizedMessage ?: "Failed to save dress image")
            }
        }
    }
}
