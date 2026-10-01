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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

/**
 * Service to map, scale, and align captured camera portrait images onto the 3D model head,
 * dynamically adjusting facial proportions and alignment according to the user's height and weight settings.
 */
object FaceOverlayService {

    data class HeadAlignmentConfig(
        val headWidthDp: Dp,
        val headHeightDp: Dp,
        val topPaddingDp: Dp,
        val scaleFactorX: Float,
        val scaleFactorY: Float,
        val cornerRadiusDp: Dp,
        val borderWidthDp: Dp
    )

    /**
     * Calculates the exact head alignment, dimensions, and dynamic scaling metrics
     * according to user height (ft) and weight (kg).
     *
     * - Height (4.5ft to 6.5ft): Drives vertical head placement and slight vertical proportion stretch.
     * - Weight (40kg to 120kg): Drives horizontal jawline and cheek width scaling (broader silhouette for higher weight).
     * - Gender ("Male" vs "Female"): Drives jawline taper and corner curvature.
     */
    fun calculateHeadAlignment(
        heightFt: Float,
        weightKg: Float,
        gender: String = "Male"
    ): HeadAlignmentConfig {
        val clampedWeight = weightKg.coerceIn(40f, 120f)
        val clampedHeight = heightFt.coerceIn(4.5f, 6.5f)

        // Weight scaling factor: 0.88x (40kg) to 1.24x (120kg)
        val weightFactor = 0.88f + ((clampedWeight - 40f) / 80f) * 0.36f
        // Height scaling factor: 0.90x (4.5ft) to 1.15x (6.5ft)
        val heightFactor = 0.90f + ((clampedHeight - 4.5f) / 2.0f) * 0.25f

        val isFemale = gender.equals("Female", ignoreCase = true)

        // Base head size on 3D model viewport:
        val baseWidth = if (isFemale) 82f else 88f
        val computedWidthDp = (baseWidth * weightFactor).dp

        val baseHeight = if (isFemale) 102f else 108f
        val computedHeightDp = (baseHeight * (heightFactor * 0.75f + weightFactor * 0.25f)).dp

        // Vertical alignment / top padding on 3D model:
        // Taller users have slightly adjusted neck/head placement relative to model collar
        val baseTopPadding = 24f
        val computedTopPaddingDp = (baseTopPadding * (2.0f - heightFactor * 0.85f).coerceIn(0.85f, 1.25f)).dp

        // Oval curvature: Female slightly softer curve, Male slightly more defined
        val cornerRadius = if (isFemale) 48.dp else 44.dp

        return HeadAlignmentConfig(
            headWidthDp = computedWidthDp,
            headHeightDp = computedHeightDp,
            topPaddingDp = computedTopPaddingDp,
            scaleFactorX = weightFactor,
            scaleFactorY = heightFactor,
            cornerRadiusDp = cornerRadius,
            borderWidthDp = 2.5.dp
        )
    }

    /**
     * Standard Image Processing on captured camera portrait, factoring in height and weight:
     * 1. Dynamic facial center crop tailored to user's body aspect ratio.
     * 2. ColorMatrix enhancement (contrast, skin vibrancy, natural studio lighting).
     * 3. Elliptical feather-masked alpha blending with PorterDuff.Mode.DST_IN.
     * 4. Saves processed PNG for instant overlay on the 3D model.
     */
    fun processAndMapFace(
        context: Context,
        rawBitmap: Bitmap,
        heightFt: Float,
        weightKg: Float,
        gender: String = "Male"
    ): String? {
        return try {
            val alignment = calculateHeadAlignment(heightFt, weightKg, gender)

            val targetWidth = (440 * alignment.scaleFactorX).toInt().coerceIn(360, 600)
            val targetHeight = (540 * alignment.scaleFactorY).toInt().coerceIn(440, 720)

            val srcWidth = rawBitmap.width
            val srcHeight = rawBitmap.height

            val cropRatio = (targetWidth.toFloat() / targetHeight.toFloat()).coerceIn(0.65f, 0.95f)
            val cropHeight = minOf(srcHeight, (srcWidth / cropRatio).toInt()).coerceAtLeast(40)
            val cropWidth = (cropHeight * cropRatio).toInt().coerceAtMost(srcWidth).coerceAtLeast(40)

            val cropLeft = ((srcWidth - cropWidth) / 2).coerceIn(0, (srcWidth - 1).coerceAtLeast(0))
            val cropTop = ((srcHeight - cropHeight) / 3).coerceIn(0, (srcHeight - 1).coerceAtLeast(0))

            val safeWidth = minOf(cropWidth, srcWidth - cropLeft).coerceAtLeast(1)
            val safeHeight = minOf(cropHeight, srcHeight - cropTop).coerceAtLeast(1)

            val cropped = Bitmap.createBitmap(rawBitmap, cropLeft, cropTop, safeWidth, safeHeight)

            val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            // ColorMatrix for enhanced studio lighting and skin vibrancy
            val colorMatrix = ColorMatrix().apply {
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

            val destRect = Rect(0, 0, targetWidth, targetHeight)
            canvas.drawBitmap(cropped, Rect(0, 0, cropped.width, cropped.height), destRect, imagePaint)

            // Elliptical portrait mask for seamless mapping onto model head
            val maskBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val maskCanvas = Canvas(maskBitmap)
            val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                style = Paint.Style.FILL
            }

            val ovalRect = RectF(10f, 10f, targetWidth - 10f, targetHeight - 10f)
            maskCanvas.drawOval(ovalRect, maskPaint)

            val compositePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            }
            canvas.drawBitmap(maskBitmap, 0f, 0f, compositePaint)

            // Outer border matching Pehno Emerald green
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0F766E")
                style = Paint.Style.STROKE
                strokeWidth = 6f
            }
            canvas.drawOval(ovalRect, borderPaint)

            val cacheDir = context.cacheDir
            val portraitFile = File(cacheDir, "aligned_portrait_${System.currentTimeMillis()}.png")
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

    /**
     * Composites a processed user face directly onto a model bitmap at the exact head location,
     * scaled according to the user's height and weight settings.
     */
    fun overlayFaceOnModelBitmap(
        baseModelBitmap: Bitmap,
        faceBitmap: Bitmap,
        heightFt: Float,
        weightKg: Float,
        gender: String = "Male"
    ): Bitmap {
        val result = baseModelBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val alignment = calculateHeadAlignment(heightFt, weightKg, gender)

        val modelWidth = result.width
        val modelHeight = result.height

        val faceTargetWidth = (modelWidth * 0.22f * alignment.scaleFactorX).toInt()
        val faceTargetHeight = (faceTargetWidth * 1.25f * (alignment.scaleFactorY / alignment.scaleFactorX)).toInt()

        val faceLeft = (modelWidth - faceTargetWidth) / 2
        val faceTop = (modelHeight * 0.12f * (2.0f - alignment.scaleFactorY * 0.8f).coerceIn(0.85f, 1.2f)).toInt()

        val destRect = Rect(faceLeft, faceTop, faceLeft + faceTargetWidth, faceTop + faceTargetHeight)
        canvas.drawBitmap(faceBitmap, Rect(0, 0, faceBitmap.width, faceBitmap.height), destRect, Paint(Paint.ANTI_ALIAS_FLAG))

        return result
    }
}
