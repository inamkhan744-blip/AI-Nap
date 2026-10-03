package com.example.data.model

import java.util.UUID

enum class MessageSender {
    USER,
    ADVISOR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val occasion: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedItems: List<FashionItem> = emptyList()
)

data class StyleAdvisorOccasion(
    val id: String,
    val englishName: String,
    val urduName: String,
    val emoji: String
) {
    fun label(lang: String): String = if (lang == "ur") "$emoji $urduName" else "$emoji $englishName"
}

object StyleAdvisorPresets {
    val occasions = listOf(
        StyleAdvisorOccasion("wedding", "Wedding / Walima", "شادی اور ولیمہ", "💍"),
        StyleAdvisorOccasion("mehndi", "Mehndi / Sangeet", "مہندی اور مایوں", "🌿"),
        StyleAdvisorOccasion("eid", "Eid / Festive", "عید اور تہوار", "🌙"),
        StyleAdvisorOccasion("formal", "Formal / Office", "دفتر اور انٹرویو", "💼"),
        StyleAdvisorOccasion("casual", "Casual / Daily", "کیژول اور روزمرہ", "☕"),
        StyleAdvisorOccasion("party", "Party / Dinner", "پارٹی اور ڈنر", "✨")
    )

    fun getSuggestions(gender: String, lang: String): List<String> {
        val isUrdu = lang == "ur"
        return if (gender.equals("Female", ignoreCase = true)) {
            if (isUrdu) listOf(
                "میری گندمی رنگت کے لیے شادی کا بہترین سوٹ؟",
                "دبلے جسم پر کیسی کُرتی اور گھیرا جچے گا؟",
                "ولیمہ کے لیے لانگ فراک یا ساڑھی؟",
                "کراچی کے موسم میں کون سا فیبرک پہنوں؟"
            ) else listOf(
                "Best wedding color for wheatish skin tone?",
                "Which silhouette suits a slim body type?",
                "Long maxidress or straight suit for Walima?",
                "Best breathable fabric for warm weather?"
            )
        } else {
            if (isUrdu) listOf(
                "شیروانی یا کلاسک پرنس کوٹ: ولیمہ پر کیا پہنوں؟",
                "شلوار قمیض میں قد کو لمبا کیسے دکھائیں؟",
                "گندمی رنگت پر کون سا رنگ سب سے خوبصورت لگے گا؟",
                "پشاوری چپل کے ساتھ کُرتے کا بہترین امتزاج؟"
            ) else listOf(
                "Sherwani or Prince Coat for a brother's wedding?",
                "How to look taller in Shalwar Kameez?",
                "Best contrast colors for medium wheatish skin?",
                "Peshawari chappal styling for Eid?"
            )
        }
    }
}
