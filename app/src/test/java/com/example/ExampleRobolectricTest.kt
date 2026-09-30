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
}
