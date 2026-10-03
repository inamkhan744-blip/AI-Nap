package com.example.util

import android.content.Context
import android.graphics.*
import java.io.File
import java.io.FileOutputStream

object ClothingOverlayEngine {

    /**
     * Composites a digital clothing item over a user photo with proportion-based positioning,
     * optional custom scaling, offsets, and smooth edge blending.
     */
    fun compositeGarmentOnPhoto(
        userPhoto: Bitmap,
        garmentBitmap: Bitmap,
        scaleFactor: Float = 1.0f,
        offsetXPct: Float = 0.0f, // -0.5f to 0.5f of photo width
        offsetYPct: Float = 0.0f, // -0.5f to 0.5f of photo height
        alpha: Float = 1.0f,
        gender: String = "Male",
        heightFt: Float = 5.8f,
        weightKg: Float = 70f
    ): Bitmap {
        val width = userPhoto.width
        val height = userPhoto.height

        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // 1. Draw original user photo
        val basePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(userPhoto, 0f, 0f, basePaint)

        // 2. Calculate garment placement based on body proportions
        // Typically in portrait/bust shots, shoulders start around 28-35% down the photo height
        val isMale = gender.equals("Male", ignoreCase = true)
        val shoulderWidthRatio = if (isMale) 0.82f else 0.76f
        val bodyScaleBonus = (weightKg - 50f) * 0.002f // slight expansion for broader builds

        val baseGarmentWidth = (width * (shoulderWidthRatio + bodyScaleBonus)).coerceIn(width * 0.6f, width * 1.1f)
        val targetGarmentWidth = (baseGarmentWidth * scaleFactor).toInt().coerceAtLeast(50)
        val garmentAspect = garmentBitmap.height.toFloat() / garmentBitmap.width.toFloat()
        val targetGarmentHeight = (targetGarmentWidth * garmentAspect).toInt().coerceAtLeast(50)

        // Scale garment
        val scaledGarment = Bitmap.createScaledBitmap(garmentBitmap, targetGarmentWidth, targetGarmentHeight, true)

        // Horizontal centering + user offset
        val garmentLeft = ((width - targetGarmentWidth) / 2f) + (offsetXPct * width)

        // Vertical position: collar aligns near chest/shoulders (approx 25-35% down)
        val baseTop = height * 0.22f
        val garmentTop = baseTop + (offsetYPct * height)

        // 3. Draw garment with desired alpha & smooth filter
        val garmentPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            this.alpha = (alpha.coerceIn(0.1f, 1.0f) * 255).toInt()
        }

        canvas.drawBitmap(scaledGarment, garmentLeft, garmentTop, garmentPaint)

        return resultBitmap
    }

    /**
     * Renders a split Before & After view where the left side is the raw photo
     * and the right side is the virtual try-on composite.
     */
    fun renderBeforeAfterSplit(
        rawPhoto: Bitmap,
        compositePhoto: Bitmap,
        splitFraction: Float
    ): Bitmap {
        val width = rawPhoto.width
        val height = rawPhoto.height

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val splitX = (width * splitFraction.coerceIn(0f, 1f)).toInt()

        // Draw left side (raw)
        val leftSrc = Rect(0, 0, splitX, height)
        val leftDst = Rect(0, 0, splitX, height)
        canvas.drawBitmap(rawPhoto, leftSrc, leftDst, null)

        // Draw right side (composite)
        if (splitX < width) {
            val rightSrc = Rect(splitX, 0, width, height)
            val rightDst = Rect(splitX, 0, width, height)
            canvas.drawBitmap(compositePhoto, rightSrc, rightDst, null)
        }

        // Draw split divider line
        val linePaint = Paint().apply {
            color = Color.WHITE
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
            setShadowLayer(8f, 0f, 0f, Color.BLACK)
        }
        canvas.drawLine(splitX.toFloat(), 0f, splitX.toFloat(), height.toFloat(), linePaint)

        return output
    }

    /**
     * Saves composited bitmap to app's pictures directory.
     */
    fun saveCompositeBitmap(context: Context, bitmap: Bitmap): String? {
        return try {
            val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES) ?: context.filesDir
            val file = File(dir, "Pehno_Virtual_TryOn_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }
}
