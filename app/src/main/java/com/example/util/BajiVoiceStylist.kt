package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class BajiVoiceStylist(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val urduLocale = Locale("ur", "PK")
            val hindiLocale = Locale("hi", "IN")
            val defaultLocale = Locale.getDefault()

            val result = tts?.setLanguage(urduLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to Hindi or English which phonetically pronounce Roman Urdu accurately
                val fallback = tts?.setLanguage(hindiLocale)
                if (fallback == TextToSpeech.LANG_MISSING_DATA || fallback == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.US)
                }
            }

            // Warm, female-pitched stylist voice
            tts?.setPitch(1.15f)
            tts?.setSpeechRate(0.92f)
            isReady = true
        } else {
            Log.e("BajiVoice", "Failed to initialize TextToSpeech")
        }
    }

    fun speak(text: String) {
        if (isReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BAJI_VOICE_UTTERANCE")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
