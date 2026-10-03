package com.example.util

import com.example.data.model.TailorMeasurements

object TailorMeasurementCalculator {

    fun calculate(
        heightFt: Float,
        weightKg: Float,
        gender: String = "Male"
    ): TailorMeasurements {
        val totalInches = heightFt * 12f
        val isMale = gender.equals("Male", ignoreCase = true)

        val chest = if (isMale) {
            32f + ((weightKg - 45f) * 0.22f).coerceIn(0f, 20f)
        } else {
            30f + ((weightKg - 40f) * 0.20f).coerceIn(0f, 18f)
        }

        val shoulder = if (isMale) {
            15.5f + ((weightKg - 45f) * 0.065f).coerceIn(0f, 5f)
        } else {
            13.5f + ((weightKg - 40f) * 0.055f).coerceIn(0f, 4f)
        }

        val kameezLength = if (isMale) {
            (totalInches * 0.58f).coerceIn(36f, 48f)
        } else {
            (totalInches * 0.54f).coerceIn(34f, 46f)
        }

        val sleeveLength = if (isMale) {
            (totalInches * 0.35f).coerceIn(21f, 27f)
        } else {
            (totalInches * 0.31f).coerceIn(18f, 24f)
        }

        val waist = (chest - if (weightKg > 85f) -1f else 2.5f).coerceAtLeast(26f)
        val daman = (chest + if (isMale) 1.5f else 3.0f).coerceAtLeast(20f)
        val collar = if (isMale) (14f + (weightKg - 45f) * 0.045f).coerceIn(13.5f, 18f) else 0f
        val trouserLength = (totalInches * 0.54f).coerceIn(34f, 44f)
        val pauncha = if (isMale) (7.5f + (weightKg - 45f) * 0.02f).coerceIn(7f, 9.5f) else (6.5f + (weightKg - 40f) * 0.02f).coerceIn(6f, 8.5f)

        val size = when {
            chest < 36f -> "S (Small)"
            chest < 40f -> "M (Medium)"
            chest < 44f -> "L (Large)"
            else -> "XL (Extra Large)"
        }

        return TailorMeasurements(
            kameezLengthInches = (kameezLength * 2).toInt() / 2f,
            shoulderInches = (shoulder * 2).toInt() / 2f,
            chestInches = (chest * 2).toInt() / 2f,
            waistInches = (waist * 2).toInt() / 2f,
            damanInches = (daman * 2).toInt() / 2f,
            sleeveLengthInches = (sleeveLength * 2).toInt() / 2f,
            collarInches = (collar * 2).toInt() / 2f,
            trouserLengthInches = (trouserLength * 2).toInt() / 2f,
            paunchaInches = (pauncha * 2).toInt() / 2f,
            gender = gender,
            standardSize = size
        )
    }

    fun generateDarziSlipUrdu(
        customerName: String,
        m: TailorMeasurements,
        notes: String = ""
    ): String {
        return buildString {
            appendLine("📜 *پہنو - درزی کا ناپ سلپ (Pehno Smart Darzi)*")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("👤 کسٹمر کا نام: $customerName")
            appendLine("📏 سائز: ${m.standardSize}")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("• قمیض / کُرتی لمبائی: ${m.kameezLengthInches}\" انچ")
            appendLine("• تیرہ / شولڈر: ${m.shoulderInches}\" انچ")
            appendLine("• چھاتی / چیسٹ: ${m.chestInches}\" انچ")
            appendLine("• کمر / ویسٹ: ${m.waistInches}\" انچ")
            appendLine("• دامن گھیرا: ${m.damanInches}\" انچ")
            appendLine("• آستین لمبائی: ${m.sleeveLengthInches}\" انچ")
            if (m.collarInches > 0) {
                appendLine("• بین / کالر: ${m.collarInches}\" انچ")
            }
            appendLine("• شلوار / پاجامہ لمبائی: ${m.trouserLengthInches}\" انچ")
            appendLine("• پانچہ: ${m.paunchaInches}\" انچ")
            if (notes.isNotBlank()) {
                appendLine("━━━━━━━━━━━━━━━━━━━")
                appendLine("📝 خصوصی ہدایات: $notes")
            }
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("✨ تیار کردہ بذریعہ پہنو ایپ (Pehno AI-Tailor)")
        }
    }
}
