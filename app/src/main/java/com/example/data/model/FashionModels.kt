package com.example.data.model

enum class PehnoCategory(val urdu: String, val english: String, val gender: String) {
    DRESS("جوڑا / Dress", "Dress", "Both"),
    GALA_COLLAR("گلا / Collar", "Gala / Collar", "Male"),
    TOPI_CAP("ٹوپی / Cap", "Topi / Cap", "Male"),
    HAIRSTYLE("ہیئر اسٹائل", "Hairstyle", "Both"),
    SHOES("جوتا / Shoes", "Shoes", "Both"),
    GALA_DESIGN("گلا ڈیزائن", "Gala Design", "Female"),
    DAMAN_DESIGN("دامن ڈیزائن", "Daman Design", "Female"),
    MEHNDI("مہندی آرٹ", "Mehndi", "Female"),
    JEWELLERY("جیولری", "Jewellery", "Female")
}

// Backward-compatible alias
enum class FashionCategory(val title: String, val urduSubtitle: String) {
    DRESS_TRY_ON("Jora / Dress", "جوڑا"),
    HAIR_BEARD("Hairstyle", "ہیئر اسٹائل"),
    JEWELLERY("Jewellery", "جیولری"),
    SHOES("Shoes / Joota", "جوتا"),
    MEHNDI("Mehndi", "مہندی"),
    WEIGHT_PREVIEW("Weight Preview", "وزن کم کرنے کے بعد"),
    COUPLE_MATCHING("Couple Matching", "میاں بیوی میچنگ لک"),
    CATWALK_VIDEO("AI Catwalk Video", "فیشن کیٹ واک ویڈیو")
}

data class FashionItem(
    val id: String,
    val category: FashionCategory = FashionCategory.DRESS_TRY_ON,
    val pehnoCategory: PehnoCategory = PehnoCategory.DRESS,
    val name: String,
    val urduName: String,
    val gender: String, // "Male", "Female", "Both"
    val subType: String = "Daily",
    val description: String,
    val heightBoostInches: Float = 0f,
    val heightTip: String = "",
    val priceRs: Int = 4500,
    val darazLink: String = "https://www.daraz.pk?aff=pehno10",
    val rentPerDayRs: Int = 1500,
    val colorHex: Long = 0xFF0E8A5E,
    val tag: String = "Trending",
    val suitabilityScore: Int = 95,
    val frontImageUrl: String = "",
    val backImageUrl: String? = null,
    val storeSource: String = "Daraz.pk - Rs. 4,500",
    val whatsappNumber: String? = null,
    val isSellerItem: Boolean = false,
    val drawableResId: Int = 0
)

data class HeightSuitabilityResult(
    val isRecommended: Boolean,
    val matchPercentage: Int,
    val shortVerdictUrdu: String,
    val detailedReason: String,
    val stylingAdvice: String,
    val colorAdvice: String
)

data class CityWeather(
    val city: String,
    val tempC: Int,
    val condition: String,
    val wardrobeAdvice: String
)

data class NearShop(
    val id: String,
    val name: String,
    val type: String,
    val distance: String,
    val rating: Float,
    val priceText: String,
    val address: String
)

data class MehndiDesign(
    val id: String,
    val title: String,
    val urduTitle: String,
    val placement: String,
    val style: String
)
