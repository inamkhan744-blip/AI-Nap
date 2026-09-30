package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import java.io.File
import java.io.FileOutputStream

object PortraitProcessor {

    /**
     * Standard Image Processing on captured camera portrait:
     * 1. Aspect ratio normalization & center crop for facial framing.
     * 2. Color & contrast balancing using ColorMatrix (optimizes skin tones).
     * 3. Elliptical portrait mask with smooth anti-aliased edge blending using PorterDuff DST_IN.
     * 4. Saves processed PNG for model overlay.
     */
    fun processAndSavePortrait(
        context: Context,
        rawBitmap: Bitmap
    ): String? {
        return try {
            val targetWidth = 480
            val targetHeight = 580

            // 1. Center Crop square / 4:5 portrait
            val srcWidth = rawBitmap.width
            val srcHeight = rawBitmap.height
            val cropSize = minOf(srcWidth, (srcHeight * 0.85f).toInt()).coerceAtLeast(20)
            val cropLeft = ((srcWidth - cropSize) / 2).coerceIn(0, (srcWidth - 1).coerceAtLeast(0))
            val cropTop = ((srcHeight - cropSize) / 3).coerceIn(0, (srcHeight - 1).coerceAtLeast(0))
            val safeWidth = minOf(cropSize, srcWidth - cropLeft).coerceAtLeast(1)
            val safeHeight = minOf(cropSize, srcHeight - cropTop).coerceAtLeast(1)

            val cropped = Bitmap.createBitmap(
                rawBitmap,
                cropLeft,
                cropTop,
                safeWidth,
                safeHeight
            )

            // 2. Prepare output bitmap with transparency
            val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            // 3. Setup standard image processing: Contrast & Saturation enhancement
            val colorMatrix = ColorMatrix().apply {
                // Slightly enhance contrast & skin vibrance
                val contrast = 1.08f
                val brightness = 8f
                val scale = contrast
                val translate = (-0.5f * scale + 0.5f) * 255f + brightness
                set(floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                ))
            }

            val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                colorFilter = ColorMatrixColorFilter(colorMatrix)
            }

            // Draw cropped image scaled into canvas
            val destRect = Rect(0, 0, targetWidth, targetHeight)
            canvas.drawBitmap(cropped, Rect(0, 0, cropped.width, cropped.height), destRect, imagePaint)

            // 4. Apply oval portrait mask with smooth blending (DST_IN)
            val maskBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val maskCanvas = Canvas(maskBitmap)
            val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                style = Paint.Style.FILL
            }

            // Draw an oval mask that matches human head proportions
            val ovalRect = RectF(12f, 12f, targetWidth - 12f, targetHeight - 12f)
            maskCanvas.drawOval(ovalRect, maskPaint)

            // Composite mask onto output
            val compositePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            }
            canvas.drawBitmap(maskBitmap, 0f, 0f, compositePaint)

            // 5. Add delicate subtle outer border for clean integration on model
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0F766E") // Pehno Green
                style = Paint.Style.STROKE
                strokeWidth = 6f
            }
            canvas.drawOval(ovalRect, borderPaint)

            // Save processed bitmap
            val cacheDir = context.cacheDir
            val portraitFile = File(cacheDir, "processed_portrait_${System.currentTimeMillis()}.png")
            FileOutputStream(portraitFile).use { fos ->
                output.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            }

            portraitFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
