package com.bekisma.adlamfulfulde.data

import androidx.annotation.RawRes

// Optionnel : Pour le surlignage synchronisé des mots
data class WordTiming(
    val word: String,       // Le mot exact tel qu'il apparaît dans le texte
    val startTimeMs: Long,  // Temps de début du mot dans l'audio (en millisecondes)
    val endTimeMs: Long     // Temps de fin du mot dans l'audio (en millisecondes)
)

data class ReadingPassage(
    val id: Int,
    val title: String,              // Titre du passage (ex: "Ma première histoire")
    val adlamText: String,          // Le texte complet en Adlam
    val frenchTranslation: String,  // Traduction en français pour l'apprentissage
    @RawRes val soundResId: Int,    // L'identifiant de la ressource audio pour la narration
    val difficulty: String = "Facile", // Optionnel: Niveau de difficulté
    val wordTimings: List<WordTiming>? = null, // Optionnel: Pour le surlignage synchronisé
    val tags: List<String> = listOf(), // Tags pour la catégorisation (nouveau)
    val isFavorite: Boolean = false // Marquer comme favori (nouveau)
)