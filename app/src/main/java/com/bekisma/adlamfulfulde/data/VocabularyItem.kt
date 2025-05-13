package com.bekisma.adlamfulfulde.data

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class VocabularyItem(
    val id: Int,
    val adlamWord: String,
    val translation: String, // Par exemple, en français
    @RawRes val soundResId: Int,
    @DrawableRes val imageResId: Int? = null, // Image optionnelle
    val exampleSentenceAdlam: String? = null, // Phrase d'exemple en Adlam (optionnel)
    val exampleSentenceTranslation: String? = null, // Traduction de la phrase (optionnel)
    val category: String? = null // Catégorie optionnelle (ex: "Salutations", "Animaux")
)