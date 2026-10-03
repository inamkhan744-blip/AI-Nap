package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pehno", appName)
  }

  @Test
  fun `test pehno translations and catalog`() {
    val urduTitle = com.example.util.PehnoStrings.t("app_name", "ur")
    val enTitle = com.example.util.PehnoStrings.t("app_name", "en")
    assertEquals("پہنو", urduTitle)
    assertEquals("Pehno", enTitle)

    val maleItems = com.example.data.model.FashionCatalog.getItemsForPehnoCategory(
        com.example.data.model.PehnoCategory.DRESS, "Male"
    )
    assert(maleItems.isNotEmpty())
  }

  @Test
  fun `test portrait processing and file generation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val testBitmap = android.graphics.Bitmap.createBitmap(300, 400, android.graphics.Bitmap.Config.ARGB_8888)
    val processedPath = com.example.util.PortraitProcessor.processAndSavePortrait(context, testBitmap)
    assert(processedPath != null)
    val file = java.io.File(processedPath!!)
    assert(file.exists() && file.length() > 0)
  }

  @Test
  fun `test face overlay service height and weight scaling`() {
    val slimConfig = com.example.util.FaceOverlayService.calculateHeadAlignment(heightFt = 5.0f, weightKg = 50f, gender = "Female")
    val heavyConfig = com.example.util.FaceOverlayService.calculateHeadAlignment(heightFt = 6.2f, weightKg = 95f, gender = "Male")

    // Heavier weight should scale head width broader
    assert(heavyConfig.scaleFactorX > slimConfig.scaleFactorX)
    assert(heavyConfig.headWidthDp > slimConfig.headWidthDp)

    // Taller height should scale head height taller
    assert(heavyConfig.scaleFactorY > slimConfig.scaleFactorY)
    assert(heavyConfig.headHeightDp > slimConfig.headHeightDp)
  }

  @Test
  fun `test face overlay service bitmap processing and compositing`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val testFace = android.graphics.Bitmap.createBitmap(320, 420, android.graphics.Bitmap.Config.ARGB_8888)
    
    // Test processAndMapFace
    val mappedPath = com.example.util.FaceOverlayService.processAndMapFace(
        context = context,
        rawBitmap = testFace,
        heightFt = 5.8f,
        weightKg = 72f,
        gender = "Male"
    )
    assert(mappedPath != null)
    val file = java.io.File(mappedPath!!)
    assert(file.exists() && file.length() > 0)

    // Test overlayFaceOnModelBitmap
    val modelBase = android.graphics.Bitmap.createBitmap(500, 800, android.graphics.Bitmap.Config.ARGB_8888)
    val composited = com.example.util.FaceOverlayService.overlayFaceOnModelBitmap(
        baseModelBitmap = modelBase,
        faceBitmap = testFace,
        heightFt = 5.8f,
        weightKg = 72f,
        gender = "Male"
    )
    assertEquals(500, composited.width)
    assertEquals(800, composited.height)
  }

  @Test
  fun `test wishlist addition and removal`() {
    val items = com.example.data.model.FashionCatalog.items
    assert(items.isNotEmpty())
    val sampleItem = items.first()

    var wishlist = listOf<com.example.data.model.FashionItem>()
    wishlist = wishlist + sampleItem
    assert(wishlist.size == 1)
    assertEquals(sampleItem.id, wishlist.first().id)

    wishlist = wishlist.filterNot { it.id == sampleItem.id }
    assert(wishlist.isEmpty())
  }

  @Test
  fun `test tailor measurement calculator logic and slip generation`() {
    val m = com.example.util.TailorMeasurementCalculator.calculate(heightFt = 5.8f, weightKg = 72f, gender = "Male")
    assert(m.chestInches in 34f..44f)
    assert(m.kameezLengthInches in 36f..46f)
    assert(m.shoulderInches in 15f..20f)
    assert(m.trouserLengthInches in 34f..44f)

    val slip = com.example.util.TailorMeasurementCalculator.generateDarziSlipUrdu(
        customerName = "Ali Khan",
        m = m
    )
    assert(slip.contains("Ali Khan"))
    assert(slip.contains("قمیض / کُرتی لمبائی"))
    assert(slip.contains("چھاتی / چیسٹ"))
  }

  @Test
  fun `test placed order entity creation`() {
    val order = com.example.data.model.PlacedOrder(
        id = "ORD-12345",
        dressName = "Classic Black Shalwar Kameez",
        sellerShop = "Daraz.pk",
        priceRs = 4500,
        deliveryCity = "Lahore",
        customerName = "Inam Khan",
        customerPhone = "03001234567",
        deliveryAddress = "Gulberg III, Lahore",
        orderDate = "03 Oct 2026",
        status = "Confirmed (COD)"
    )
    assertEquals("ORD-12345", order.id)
    assertEquals(4500, order.priceRs)
    assertEquals("Lahore", order.deliveryCity)
  }
}
