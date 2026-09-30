package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.SavedLookEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.FashionCatalog
import com.example.data.model.FashionCategory
import com.example.data.model.FashionItem
import com.example.data.model.HeightSuitabilityResult
import com.example.data.remote.GeminiStylingService
import kotlinx.coroutines.flow.Flow

class FashionRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.lookDao()
    private val geminiService = GeminiStylingService(context)

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val savedLooks: Flow<List<SavedLookEntity>> = dao.getAllSavedLooks()
    val sellerDresses: Flow<List<com.example.data.local.SellerDressEntity>> = dao.getAllSellerDresses()

    suspend fun saveProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun addSellerDress(dress: com.example.data.local.SellerDressEntity): Long {
        return dao.insertSellerDress(dress)
    }

    suspend fun deleteSellerDress(id: Long) {
        dao.deleteSellerDress(id)
    }

    suspend fun saveFinalLook(look: SavedLookEntity): Long {
        return dao.insertSavedLook(look)
    }

    suspend fun deleteLook(id: Long) {
        dao.deleteSavedLook(id)
    }

    suspend fun assignDayToLook(id: Long, day: String?) {
        dao.updateAssignedDay(id, day)
    }

    suspend fun toggleFavorite(id: Long, isFav: Boolean) {
        dao.toggleFavorite(id, isFav)
    }

    suspend fun setPremium(isPremium: Boolean) {
        dao.updatePremiumStatus(isPremium)
    }

    fun getCatalogItems(category: FashionCategory, gender: String): List<FashionItem> {
        return FashionCatalog.getItemsForCategory(category, gender)
    }

    fun getAllCatalogItems(): List<FashionItem> = FashionCatalog.items

    suspend fun evaluateSuitability(
        item: FashionItem,
        gender: String,
        heightFt: Float,
        weightKg: Float,
        bodyType: String,
        skinTone: String
    ): HeightSuitabilityResult {
        return geminiService.evaluateItemSuitability(item, gender, heightFt, weightKg, bodyType, skinTone)
    }

    suspend fun generateLookImage(
        dress: FashionItem,
        shoes: FashionItem,
        hair: FashionItem,
        jewellery: FashionItem,
        gender: String,
        heightFt: Float,
        bodyType: String,
        skinTone: String,
        userPhotoUri: String?
    ): Pair<String, Int> {
        return geminiService.generateRealHDLookImage(
            dress, shoes, hair, jewellery, gender, heightFt, bodyType, skinTone, userPhotoUri
        )
    }

    suspend fun evaluateCompleteLook(
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
    ): Triple<String, String, String> {
        return geminiService.generateCompleteLookCritique(
            dress, shoes, hair, jewellery, mehndi, heightFt, bodyType, skinTone, gender, photoUri
        )
    }
}
