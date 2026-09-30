package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "User",
    val gender: String = "Male", // "Male" or "Female"
    val heightFt: Float = 5.6f,
    val weightKg: Float = 68f,
    val language: String = "ur", // "ur" or "en"
    val bodyType: String = "Medium", // "Slim", "Medium", "Chubby", "Muscular"
    val skinTone: String = "Medium Wheatish", // "Fair", "Medium Wheatish", "Dark"
    val city: String = "Lahore",
    val photoUri: String? = null,
    val chestInches: Float = 38f,
    val waistInches: Float = 32f,
    val isSetupComplete: Boolean = false,
    val isPremium: Boolean = false,
    val dailyLooksLeft: Int = 2
)

@Entity(tableName = "seller_dresses")
data class SellerDressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shopName: String,
    val city: String,
    val dressName: String,
    val frontPhotoUrl: String,
    val backPhotoUrl: String? = null,
    val priceRs: Int,
    val whatsappNumber: String,
    val gender: String, // "Male" or "Female"
    val category: String = "Jora / Dress",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_looks")
data class SavedLookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dressName: String,
    val hairName: String,
    val jewelleryName: String,
    val shoesName: String,
    val mehndiName: String? = null,
    val userHeightFt: Float,
    val calculatedHeightFt: Float,
    val bodyType: String,
    val skinTone: String,
    val city: String = "Quetta",
    val weatherText: String = "12°C Chilly",
    val aiVerdict: String,
    val aiStylingTip: String,
    val aiColorAdvice: String,
    val budgetRs: Int = 4500,
    val assignedDay: String? = null, // "Monday", "Tuesday", etc.
    val isFavorite: Boolean = false,
    val hasVideo: Boolean = true,
    val imagePath: String? = null,
    val drawableResId: Int = 0
)
