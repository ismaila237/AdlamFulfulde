package com.bekisma.adlamfulfulde.data

import com.bekisma.adlamfulfulde.R

fun getVocabularyList(): List<VocabularyItem> {
    return listOf(
        // CATÉGORIE: LIEUX
        VocabularyItem(
            id = 1,
            adlamWord = "𞤺𞤢𞤤𞤫",
            translation = "Maison",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤺𞤢𞤤𞤫 𞤢𞤥 𞤯𞤮𞥅",
            exampleSentenceTranslation = "Ma maison est ici",
            category = "Lieux"
        ),
        VocabularyItem(
            id = 2,
            adlamWord = "𞤶𞤵𞥅𞤤𞤫𞥅𞤪𞤫",
            translation = "Marché",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭𞤯𞤮 𞤴𞤢𞤸𞤵𞤣𞤫 𞤶𞤵𞥅𞤤𞤫𞥅𞤪𞤫",
            exampleSentenceTranslation = "Je vais au marché",
            category = "Lieux"
        ),
        VocabularyItem(
            id = 3,
            adlamWord = "𞤶𞤢𞤲𞤺𞤭𞤪𞤣𞤫",
            translation = "École",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤦𞤭𞤲𞤣𞤮 𞤭𞤲𞤢 𞤫 𞤶𞤢𞤲𞤺𞤭𞤪𞤣𞤫",
            exampleSentenceTranslation = "L'enfant est à l'école",
            category = "Lieux"
        ),
        VocabularyItem(
            id = 4,
            adlamWord = "𞤶𞤵𞤤𞤣𞤫",
            translation = "Mosquée",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤶𞤵𞤤𞤣𞤫 𞤲𞤣𞤫𞥅 𞤥𞤢𞤱𞤯𞤵𞥅𞤣𞤫",
            exampleSentenceTranslation = "La mosquée est grande",
            category = "Lieux"
        ),
        VocabularyItem(
            id = 5,
            adlamWord = "𞤨𞤫𞥅𞤪𞤮",
            translation = "Route",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤨𞤫𞥅𞤪𞤮 𞤢𞤥 𞤻𞤵𞤯𞤺𞤮𞤤",
            exampleSentenceTranslation = "Mon chemin est long",
            category = "Lieux"
        ),
        VocabularyItem(
            id = 6,
            adlamWord = "𞤲𞤣𞤫𞤪",
            translation = "Champ",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭𞤯𞤮 𞤪𞤮𞥅𞤣𞤵𞤣𞤫 𞤲𞤣𞤫𞤪",
            exampleSentenceTranslation = "Je travaille au champ",
            category = "Lieux"
        ),
        VocabularyItem(
            id = 7,
            adlamWord = "𞤥𞤢𞤴𞤮",
            translation = "Rivière",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤢𞤴𞤮 𞤲𞤺𞤮 𞤱𞤮𞥅𞤣𞤭",
            exampleSentenceTranslation = "Cette rivière est profonde",
            category = "Lieux"
        ),
        VocabularyItem(
            id = 8,
            adlamWord = "𞤸𞤵𞤧𞤣𞤫𞥅𞤪𞤫",
            translation = "Forêt",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤸𞤵𞤧𞤣𞤫𞥅𞤪𞤫 𞤲𞤣𞤫𞥅 𞤸𞤵𞤤𞤭",
            exampleSentenceTranslation = "La forêt est effrayante",
            category = "Lieux"
        ),

        // CATÉGORIE: ANIMAUX
        VocabularyItem(
            id = 9,
            adlamWord = "𞤲𞤢𞥄𞤺𞤫",
            translation = "Vache",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤲𞤢𞥄𞤺𞤫 𞤱𞤮𞤲𞥆𞤣𞤫 𞤲𞥋𞤣𞤫𞤪 𞤸𞤵𞤵𞤥𞤣𞤮",
            exampleSentenceTranslation = "La vache est dans le champ",
            category = "Animaux"
        ),
        VocabularyItem(
            id = 10,
            adlamWord = "𞤥𞤢𞤪𞤮𞥅𞤪𞤭",
            translation = "Serpent",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤢𞤪𞤮𞥅𞤪𞤭 𞤱𞤮𞤯𞤭 𞤫 𞤸𞤵𞤣𞤮",
            exampleSentenceTranslation = "Le serpent rampe sur le sol",
            category = "Animaux"
        ),
        VocabularyItem(
            id = 11,
            adlamWord = "𞤨𞤵𞤼𞤵",
            translation = "Cheval",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤨𞤵𞤼𞤵 𞤢𞤥 𞤮 𞤣𞤮𞤺𞥆𞤢𞤴",
            exampleSentenceTranslation = "Mon cheval court vite",
            category = "Animaux"
        ),
        VocabularyItem(
            id = 12,
            adlamWord = "𞤱𞤮𞥅𞤪𞤵",
            translation = "Oiseau",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤱𞤮𞥅𞤪𞤵 𞤲𞤺𞤵 𞤱𞤭𞤪𞤭𞤭",
            exampleSentenceTranslation = "L'oiseau chante",
            category = "Animaux"
        ),
        VocabularyItem(
            id = 13,
            adlamWord = "𞤪𞤢𞤱𞤢𞥄𞤲𞤣𞤵",
            translation = "Lion",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤪𞤢𞤱𞤢𞥄𞤲𞤣𞤵 𞤱𞤮𞤲𞤭 𞤸𞤵𞤧𞤣𞤫𞥅𞤪𞤫",
            exampleSentenceTranslation = "Le lion vit dans la forêt",
            category = "Animaux"
        ),
        VocabularyItem(
            id = 14,
            adlamWord = "𞤥𞤦𞤢𞤤𞤵",
            translation = "Mouton",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤦𞤢𞤤𞤵 𞤲𞤺𞤵 𞤷𞤵𞤯𞤭𞥅",
            exampleSentenceTranslation = "Le mouton broute",
            category = "Animaux"
        ),
        VocabularyItem(
            id = 15,
            adlamWord = "𞤺𞤫𞤤𞤮𞥅𞤺𞤵",
            translation = "Chameau",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤺𞤫𞤤𞤮𞥅𞤺𞤵 𞤴𞤫𞤸𞤭𞥅 𞤴𞤢𞤸𞤵𞤣𞤫 𞤧𞤢𞤸𞤪𞤢",
            exampleSentenceTranslation = "Le chameau peut traverser le désert",
            category = "Animaux"
        ),
        VocabularyItem(
            id = 16,
            adlamWord = "𞤸𞤫𞥅𞤦𞤵",
            translation = "Éléphant",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤸𞤫𞥅𞤦𞤵 𞤮 𞤥𞤢𞤱𞤯𞤮",
            exampleSentenceTranslation = "L'éléphant est grand",
            category = "Animaux"
        ),

        // CATÉGORIE: SALUTATIONS
        VocabularyItem(
            id = 17,
            adlamWord = "𞤴𞤢𞥄𞤥𞤵𞤲𞤺𞤮𞤤",
            translation = "Bonjour (matin)",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤢𞥄𞤤𞤢 𞤱𞤢𞤤𞤭𞥅 𞤴𞤢𞥄𞤥𞤵𞤲𞤺𞤮𞤤",
            exampleSentenceTranslation = "Passe une bonne matinée",
            category = "Salutations"
        ),
        VocabularyItem(
            id = 18,
            adlamWord = "𞤸𞤭𞤪𞤢𞥄𞤲𞤣𞤫",
            translation = "Au revoir",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤸𞤭𞤪𞤢𞥄𞤲𞤣𞤫 𞤸𞤢𞤪𞤫 𞤶𞤢𞤲𞤺𞤮",
            exampleSentenceTranslation = "Au revoir jusqu'à demain",
            category = "Salutations"
        ),
        VocabularyItem(
            id = 19,
            adlamWord = "𞤶𞤢𞤥 𞤱𞤢𞤤𞤢𞥄",
            translation = "Comment vas-tu?",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤶𞤢𞤥 𞤱𞤢𞤤𞤢𞥄, 𞤺𞤢𞤲𞤣𞤭 𞤱𞤮𞤯𞤭?",
            exampleSentenceTranslation = "Comment vas-tu, comment ça va?",
            category = "Salutations"
        ),
        VocabularyItem(
            id = 20,
            adlamWord = "𞤥𞤭 𞤱𞤮𞤯𞤭 𞤶𞤢𞤥",
            translation = "Je vais bien",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤱𞤮𞤯𞤭 𞤶𞤢𞤥, 𞤢 𞤶𞤢𞥅𞤪𞤢𞤥𞤢",
            exampleSentenceTranslation = "Je vais bien, merci",
            category = "Salutations"
        ),
        VocabularyItem(
            id = 21,
            adlamWord = "𞤢 𞤶𞤢𞥅𞤪𞤢𞤥𞤢",
            translation = "Merci",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤢 𞤶𞤢𞥅𞤪𞤢𞤥𞤢 𞤫 𞤦𞤢𞤤𞤤𞤢𞤤 𞤥𞤢𞥄",
            exampleSentenceTranslation = "Merci pour ton aide",
            category = "Salutations"
        ),
        VocabularyItem(
            id = 22,
            adlamWord = "𞤢 𞤧𞤢𞤸𞤭𞥅",
            translation = "Bienvenue",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤢 𞤧𞤢𞤸𞤭𞥅 𞤫 𞤧𞤵𞤣𞤵 𞤢𞤥",
            exampleSentenceTranslation = "Bienvenue chez moi",
            category = "Salutations"
        ),

        // CATÉGORIE: ACTIONS/VERBES
        VocabularyItem(
            id = 23,
            adlamWord = "𞤲𞤮𞥅𞤣𞥆𞤢",
            translation = "Appeler",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤮 𞤲𞤮𞥅𞤣𞥆𞤭𞥅 𞤭𞤲𞥆𞤣𞤫 𞤢𞤥",
            exampleSentenceTranslation = "Il/Elle a appelé mon nom",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 24,
            adlamWord = "𞤶𞤢𞤲𞤺𞤵𞤣𞤫",
            translation = "Étudier / Apprendre",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤴𞤭𞤯𞤭𞥅 𞤶𞤢𞤲𞤺𞤵𞤣𞤫 𞤀𞤣𞤤𞤢𞤥",
            exampleSentenceTranslation = "J'aime apprendre l'Adlam",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 25,
            adlamWord = "𞤴𞤢𞤪𞤵𞤣𞤫",
            translation = "Boire",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤴𞤢𞤪𞤢𞤴 𞤣𞤭𞤴𞤢𞥄𞤥",
            exampleSentenceTranslation = "Je bois de l'eau",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 26,
            adlamWord = "𞤲𞤢𞤥𞤵𞤣𞤫",
            translation = "Manger",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤸𞤢𞤴𞤪𞤫 𞤥𞤫𞤲 𞤲𞤢𞤥𞤵𞤣𞤫",
            exampleSentenceTranslation = "Il est temps de manger",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 27,
            adlamWord = "𞤱𞤭𞤲𞤣𞤵𞤣𞤫",
            translation = "Écrire",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤱𞤭𞤲𞤣𞤭𞤭 𞤯𞤫𞤪𞤫𞤱𞤮𞤤",
            exampleSentenceTranslation = "J'écris une lettre",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 28,
            adlamWord = "𞤶𞤢𞤲𞤺𞤵𞤣𞤫",
            translation = "Lire",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤶𞤢𞤲𞤺𞤢𞤴 𞤣𞤫𞤬𞤼𞤫𞤪𞤫",
            exampleSentenceTranslation = "Je lis un livre",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 29,
            adlamWord = "𞤪𞤮𞥅𞤣𞤵𞤣𞤫",
            translation = "Travailler",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭𞤯𞤮 𞤪𞤮𞥅𞤣𞤵𞤣𞤫 𞤲𞤣𞤫𞤪",
            exampleSentenceTranslation = "Je travaille au champ",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 30,
            adlamWord = "𞤬𞤭𞤥𞤵𞤣𞤫",
            translation = "Dormir",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤴𞤭𞤯𞤭𞥅 𞤬𞤭𞤥𞤵𞤣𞤫",
            exampleSentenceTranslation = "J'aime dormir",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 31,
            adlamWord = "𞤣𞤮𞤺𞥆𞤵𞤣𞤫",
            translation = "Courir",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤧𞤵𞤳𞤢𞥄𞤦𞤫 𞤯𞤫𞤲 𞤣𞤮𞤺𞥆𞤵𞤣𞤫",
            exampleSentenceTranslation = "Les jeunes hommes courent",
            category = "Verbes"
        ),
        VocabularyItem(
            id = 32,
            adlamWord = "𞤳𞤢𞤤𞤵𞤣𞤫",
            translation = "Parler",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤳𞤢𞤤𞤢𞤴 𞤨𞤵𞤤𞤢𞥄𞤪",
            exampleSentenceTranslation = "Je parle le pulaar",
            category = "Verbes"
        ),

        // CATÉGORIE: NOURRITURE ET BOISSON
        VocabularyItem(
            id = 33,
            adlamWord = "𞤣𞤭𞤴𞤢𞥄𞤥",
            translation = "Eau",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭 𞤴𞤢𞤪𞤢𞤴 𞤣𞤭𞤴𞤢𞥄𞤥",
            exampleSentenceTranslation = "Je bois de l'eau",
            category = "Nourriture et Boisson"
        ),
        VocabularyItem(
            id = 34,
            adlamWord = "𞤥𞤢𞤪𞤮",
            translation = "Riz",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭 𞤲𞤢𞤥𞤭𞥅 𞤥𞤢𞤪𞤮",
            exampleSentenceTranslation = "J'ai mangé du riz",
            category = "Nourriture et Boisson"
        ),
        VocabularyItem(
            id = 35,
            adlamWord = "𞤴𞤢𞥄𞤥",
            translation = "Feu",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤸𞤵𞤦𞤮 𞤴𞤢𞥄𞤥 𞤲𞤺𞤢𞤥 𞤣𞤫𞤬𞤢𞥄𞤣𞤫",
            exampleSentenceTranslation = "Allume le feu pour cuisiner",
            category = "Nourriture et Boisson"
        ),
        VocabularyItem(
            id = 36,
            adlamWord = "𞤻𞤢𞤥𞥆𞤢",
            translation = "Lait",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭 𞤴𞤢𞤪𞤢𞤴 𞤻𞤢𞤥𞥆𞤢 𞤻𞤢𞥄𞤥𞤯𞤢",
            exampleSentenceTranslation = "Je bois du lait frais",
            category = "Nourriture et Boisson"
        ),
        VocabularyItem(
            id = 37,
            adlamWord = "𞤴𞤭𞤪𞤣𞤫",
            translation = "Viande",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤴𞤭𞤪𞤣𞤫 𞤲𞤣𞤫𞥅 𞤦𞤫𞤤𞤯𞤵𞤥",
            exampleSentenceTranslation = "La viande est délicieuse",
            category = "Nourriture et Boisson"
        ),
        VocabularyItem(
            id = 38,
            adlamWord = "𞤥𞤢𞤲𞤺𞤮",
            translation = "Mangue",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤢𞤲𞤺𞤮 𞤲𞤣𞤫𞥅 𞤸𞤢𞥄𞤥𞤯𞤫",
            exampleSentenceTranslation = "La mangue est sucrée",
            category = "Nourriture et Boisson"
        ),
        VocabularyItem(
            id = 39,
            adlamWord = "𞤥𞤦𞤵𞤪𞤵",
            translation = "Pain",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤢𞤥 𞤧𞤮𞤯𞤭𞥅 𞤥𞤦𞤵𞤪𞤵",
            exampleSentenceTranslation = "J'ai acheté du pain",
            category = "Nourriture et Boisson"
        ),
        VocabularyItem(
            id = 40,
            adlamWord = "𞤸𞤢𞥄𞤥𞤯𞤫",
            translation = "Sucre",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤧𞤮𞤪𞤭 𞤫 𞤥𞤢𞤴𞤮 𞤸𞤢𞥄𞤥𞤯𞤫",
            exampleSentenceTranslation = "Mets du sucre dans le café",
            category = "Nourriture et Boisson"
        ),

        // CATÉGORIE: FAMILLE
        // Suite de la CATÉGORIE: FAMILLE
        VocabularyItem(
            id = 41,
            adlamWord = "𞤦𞤢𞤦𞤢",
            translation = "Père",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤦𞤢𞤦𞤢 𞤢𞤥 𞤪𞤮𞥅𞤣𞤢𞤴 𞤸𞤢𞤲𞤣𞤫",
            exampleSentenceTranslation = "Mon père travaille beaucoup",
            category = "Famille"
        ),
        VocabularyItem(
            id = 42,
            adlamWord = "𞤲𞤫𞤲𞤫",
            translation = "Mère",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤲𞤫𞤲𞤫 𞤢𞤥 𞤳𞤮 𞤶𞤭𞤯𞤮",
            exampleSentenceTranslation = "Ma mère est gentille",
            category = "Famille"
        ),
        VocabularyItem(
            id = 43,
            adlamWord = "𞤥𞤭𞤲𞤢𞥄𞤺𞤫",
            translation = "Grand-père",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭𞤲𞤢𞥄𞤺𞤫 𞤢𞤥 𞤸𞤮𞤤𞤭𞥅 𞤸𞤢𞤤𞤢",
            exampleSentenceTranslation = "Mon grand-père a beaucoup d'histoires",
            category = "Famille"
        ),
        VocabularyItem(
            id = 44,
            adlamWord = "𞤥𞤢𞤥𞤢",
            translation = "Grand-mère",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤢𞤥𞤢 𞤢𞤥 𞤤𞤢𞤺𞤲𞤮𞤯𞤮",
            exampleSentenceTranslation = "Ma grand-mère vit loin",
            category = "Famille"
        ),
        VocabularyItem(
            id = 45,
            adlamWord = "𞤥𞤭𞤴𞤢𞥄𞤺𞤮",
            translation = "Fils",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭𞤴𞤢𞥄𞤺𞤮 𞤢𞤥 𞤮 𞤥𞤢𞤱𞤣𞤮",
            exampleSentenceTranslation = "Mon fils grandit",
            category = "Famille"
        ),
        VocabularyItem(
            id = 46,
            adlamWord = "𞤦𞤭𞤣𞥆𞤮",
            translation = "Fille",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤦𞤭𞤣𞥆𞤮 𞤢𞤥 𞤮 𞤶𞤢𞤲𞤺𞤢𞤴",
            exampleSentenceTranslation = "Ma fille étudie",
            category = "Famille"
        ),
        VocabularyItem(
            id = 47,
            adlamWord = "𞤥𞤭𞤲𞤭𞤪𞤢𞥄𞤺𞤮",
            translation = "Frère",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤥𞤭𞤲𞤭𞤪𞤢𞥄𞤺𞤮 𞤢𞤥 𞤮 𞤱𞤮𞤯𞤭 𞤶𞤢𞤲𞤺𞤭𞤪𞤣𞤫",
            exampleSentenceTranslation = "Mon frère est à l'école",
            category = "Famille"
        ),
        VocabularyItem(
            id = 48,
            adlamWord = "𞤦𞤢𞤲𞤣𞤭𞤪𞤢𞥄𞤱𞤮",
            translation = "Sœur",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤦𞤢𞤲𞤣𞤭𞤪𞤢𞥄𞤱𞤮 𞤢𞤥 𞤮 𞤨𞤢𞤥𞤢𞤪𞤮",
            exampleSentenceTranslation = "Ma sœur est intelligente",
            category = "Famille"
        ),

// CATÉGORIE: TEMPS ET NOMBRES
        VocabularyItem(
            id = 49,
            adlamWord = "𞤸𞤢𞤲𞤣𞤫",
            translation = "Jour",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤸𞤢𞤲𞤣𞤫 𞤱𞤮𞤲𞤭 𞤸𞤮𞤮𞤪𞤫",
            exampleSentenceTranslation = "Le jour est chaud",
            category = "Temps et Nombres"
        ),
        VocabularyItem(
            id = 50,
            adlamWord = "𞤶𞤢𞤥𞥆𞤢",
            translation = "Nuit",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤶𞤢𞤥𞥆𞤢 𞤲𞤣𞤫𞥅 𞤯𞤫𞤲𞥆𞤵𞥅𞤣𞤫",
            exampleSentenceTranslation = "La nuit est calme",
            category = "Temps et Nombres"
        ),
        VocabularyItem(
            id = 51,
            adlamWord = "𞤺𞤮𞥅𞤼𞤮",
            translation = "Un",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭 𞤸𞤮𞤤𞤭𞥅 𞤼𞤢𞤲 𞤺𞤮𞥅𞤼𞤮",
            exampleSentenceTranslation = "J'ai un enfant",
            category = "Temps et Nombres"
        ),
        VocabularyItem(
            id = 52,
            adlamWord = "𞤯𞤭𞤯𞤭",
            translation = "Deux",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤮 𞤸𞤮𞤤𞤭𞥅 𞤦𞤢𞤤𞤤𞤫 𞤯𞤭𞤯𞤭",
            exampleSentenceTranslation = "Il a deux champs",
            category = "Temps et Nombres"
        ),
        VocabularyItem(
            id = 53,
            adlamWord = "𞤼𞤢𞤼𞤭",
            translation = "Trois",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤣𞤫𞤬𞤼𞤫𞤪𞤫 𞤼𞤢𞤼𞤭 𞤲𞤢 𞤼𞤢𞤱𞤢𞥄",
            exampleSentenceTranslation = "Trois livres sont perdus",
            category = "Temps et Nombres"
        ),
        VocabularyItem(
            id = 54,
            adlamWord = "𞤴𞤢𞤶𞤶𞤢𞤲𞤣𞤫",
            translation = "Aujourd'hui",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤸𞤮𞤤𞤮 𞤴𞤢𞤶𞤶𞤢𞤲𞤣𞤫 𞤸𞤭𞤪𞤭𞥅",
            exampleSentenceTranslation = "Il fait beau aujourd'hui",
            category = "Temps et Nombres"
        ),
        VocabularyItem(
            id = 55,
            adlamWord = "𞤶𞤢𞤲𞤺𞤮",
            translation = "Demain",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤥𞤭𞤯𞤮 𞤢𞤪𞤼𞤮𞤴𞤣𞤫 𞤶𞤢𞤲𞤺𞤮",
            exampleSentenceTranslation = "Je reviendrai demain",
            category = "Temps et Nombres"
        ),
        VocabularyItem(
            id = 56,
            adlamWord = "𞤸𞤢𞤲𞤳𞤭",
            translation = "Hier",
            soundResId = R.raw.son_nul,
            imageResId = null,
            exampleSentenceAdlam = "𞤸𞤢𞤲𞤳𞤭 𞤲𞤮 𞤲𞤣𞤫𞤤𞤥𞤭 𞤺𞤢𞤤𞤫",
            exampleSentenceTranslation = "Hier, j'ai nettoyé la maison",
            category = "Temps et Nombres"
        ),

// CATÉGORIE: CORPS HUMAIN
        VocabularyItem(
            id = 57,
            adlamWord = "𞤸𞤮𞤪𞤫",
            translation = "Tête",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤸𞤮𞤪𞤫 𞤢𞤥 𞤲𞤢𞤱𞤭𞥅",
            exampleSentenceTranslation = "Ma tête me fait mal",
            category = "Corps Humain"
        ),
        VocabularyItem(
            id = 58,
            adlamWord = "𞤶𞤵𞤲𞤺𞤮",
            translation = "Main",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤶𞤵𞤲𞤺𞤮 𞤢𞤥 𞤤𞤢𞥄𞤦𞤭",
            exampleSentenceTranslation = "Ma main est propre",
            category = "Corps Humain"
        ),
        VocabularyItem(
            id = 59,
            adlamWord = "𞤴𞤭𞤼𞤫𞤪𞤫",
            translation = "Œil",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤴𞤭𞤼𞤫𞤪𞤫 𞤥𞤢𞤳𞤮 𞤤𞤢𞤦𞤭",
            exampleSentenceTranslation = "Les yeux sont beaux",
            category = "Corps Humain"
        ),
        VocabularyItem(
            id = 60,
            adlamWord = "𞤳𞤮𞤴𞤲𞤺𞤢𞤤",
            translation = "Pied",
            soundResId = R.raw.son_nul,
            imageResId = R.drawable.iconapp,
            exampleSentenceAdlam = "𞤳𞤮𞤴𞤲𞤺𞤢𞤤 𞤢𞤥 𞤲𞤢𞤱𞤭𞥅",
            exampleSentenceTranslation = "Mon pied me fait mal",
            category = "Corps Humain"
        )
    )
}