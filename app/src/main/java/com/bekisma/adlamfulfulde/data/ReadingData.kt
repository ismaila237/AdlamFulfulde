package com.bekisma.adlamfulfulde.data

import com.bekisma.adlamfulfulde.R // Importez votre R

// NOTE IMPORTANTE :
// 1. ÉCRIVEZ vos textes en Adlam.
// 2. ENREGISTREZ l'audio pour chaque texte et placez les fichiers dans `res/raw/`.
// 3. REMPLACEZ `R.raw.son_nul` par les VRAIS identifiants de vos fichiers audio.
// 4. (Avancé) Si vous voulez le surlignage, vous devrez manuellement créer les `WordTiming`.

fun getReadingPassages(): List<ReadingPassage> {
    return listOf(
        ReadingPassage(
            id = 1,
            title = "𞤁𞤫𞤦𞥆𞤮 𞤫 𞤺𞤢𞤤𞤫", // "Femme et Maison" (Titre en Adlam)
            adlamText = "𞤃𞤭𞤲 𞤱𞤮𞤲𞤭 𞤁𞤫𞤦𞥆𞤮. 𞤃𞤭 𞤶𞤮𞥅𞤯𞤭𞥅 𞤲𞥋𞤣𞤫𞤪 𞤺𞤢𞤤𞤫 𞤢𞤥. 𞤘𞤢𞤤𞤫 𞤢𞤥 𞤲𞤫𞤱𞤭𞥅.",
            // Traduction approximative : "Je suis une femme. J'habite dans ma maison. Ma maison est belle."
            soundResId = R.raw.son_nul, // <<< REMPLACEZ par ex: R.raw.histoire_debbo_galle
            difficulty = "Facile",
            frenchTranslation = TODO(),
            wordTimings = TODO(),
            tags = TODO(),
            isFavorite = TODO()
            // wordTimings = listOf( WordTiming("𞤃𞤭𞤲", 0, 500), ... ) // Pour le surlignage
        ),
        ReadingPassage(
            id = 2,
            title = "𞤂𞤫𞤴𞤯𞤮𞤤 𞤫 𞤲𞤢𞥄𞤺𞤫", // "Le berger et la vache"
            adlamText = "𞤂𞤫𞤴𞤯𞤮𞤤 𞤴𞤢𞤸𞤭𞥅 𞤲𞥋𞤣𞤫𞤪 𞤤𞤢𞤣𞥆𞤫. 𞤌 𞤴𞤢𞤤𞤼𞤭𞤲𞤭𞥅 𞤲𞤢𞥄𞤺𞤫 𞤥𞤢𞤳𞥆𞤮.",
            // Traduction approximative : "Le berger est allé dans la brousse. Il a sorti sa vache."
            soundResId = R.raw.son_nul, // <<< REMPLACEZ
            difficulty = "Moyen",
            frenchTranslation = TODO(),
            wordTimings = TODO(),
            tags = TODO(),
            isFavorite = TODO()
        )
        // Ajoutez plus de passages ici
    )
}