package com.example.data.model

enum class FashionCategory(val title: String, val urduSubtitle: String) {
    DRESS_TRY_ON("Dress Try-On", "کپڑے پہن کے دیکھو"),
    HAIR_BEARD("Hair & Beard", "ہئیر اور داڑھی"),
    JEWELLERY("Jewellery & Watch", "جیولری اور گھڑی"),
    SHOES("Shoes & Khussa", "ہائٹ بوسٹ جوتے"),
    MEHNDI("Mehndi Designer", "مہندی ڈیزائنر"),
    WEIGHT_PREVIEW("Weight Preview", "وزن کم کرنے کے بعد"),
    COUPLE_MATCHING("Couple Matching", "میاں بیوی میچنگ لک"),
    CATWALK_VIDEO("AI Catwalk Video", "فیشن کیٹ واک ویڈیو")
}

data class FashionItem(
    val id: String,
    val category: FashionCategory,
    val name: String,
    val urduName: String,
    val gender: String, // "Male", "Female", "Both"
    val subType: String = "Daily", // "Daily", "Wedding", "Accessory"
    val description: String,
    val heightBoostInches: Float = 0f,
    val heightTip: String,
    val priceRs: Int = 4500,
    val darazLink: String = "https://www.daraz.pk",
    val rentPerDayRs: Int = 1500,
    val colorHex: Long = 0xFFD4AF37,
    val tag: String = "Trending",
    val suitabilityScore: Int = 95
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
    val type: String, // "Barber", "Tailor", "Rent Shop"
    val distance: String,
    val rating: Float,
    val priceText: String,
    val address: String
)

data class MehndiDesign(
    val id: String,
    val title: String,
    val urduTitle: String,
    val placement: String, // "Front Hand", "Back Hand", "Feet"
    val style: String // "Bridal Kashee", "Arabic Floral", "Mandala", "Minimalist"
)
