package com.bekisma.adlamfulfulde.data

import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.model.NumberItem

class NumbersRepository {

    fun getNumberItems(): List<NumberItem> {
        return listOf(
            NumberItem("𞥐", "0", "Sifir", "𞤅𞤭𞤬𞤭𞤪", R.raw.ad0),             // Sifir
            NumberItem("𞥑", "1", "Go'o", "𞤘𞤮𞥋𞤮", R.raw.ad1),               // Go'o
            NumberItem("𞥒", "2", "Ɗiɗi", "𞤁𞤭𞤯𞤭", R.raw.ad2),               // Ɗiɗi
            NumberItem("𞥓", "3", "Tati", "𞤚𞤢𞤼𞤭", R.raw.ad3),               // Tati
            NumberItem("𞥔", "4", "Nayi", "𞤐𞤢𞤴𞤭", R.raw.ad4),               // Nayi
            NumberItem("𞥕", "5", "Jowi", "𞤔𞤮𞤱𞤭", R.raw.ad5),               // Jowi
            NumberItem("𞥖", "6", "Jeegom", "𞤔𞤫𞥅𞤺𞤮𞤥", R.raw.ad6),           // Jeegom (long ee)
            NumberItem("𞥗", "7", "Jeeɗiɗi", "𞤔𞤫𞥅𞤯𞤭𞤯𞤭", R.raw.ad7),         // Jeeɗiɗi (long ee, ɗ)
            NumberItem("𞥘", "8", "Jeetati", "𞤔𞤫𞥅𞤼𞤢𞤼𞤭", R.raw.ad8),         // Jeetati (long ee)
            NumberItem("𞥙", "9", "Jeenayi", "𞤔𞤫𞥅𞤲𞤢𞤴𞤭", R.raw.ad9),         // Jeenayi (long ee)
            NumberItem("𞥑𞥐", "10", "Sappo", "𞤅𞤢𞤨𞥆𞤮", R.raw.ad0),           // Sappo (gemination pp)
            NumberItem("𞥑𞥑", "11", "Sappo e go'o", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤘𞤮𞥋𞤮", R.raw.ad1), // Sappo e go'o
            NumberItem("𞥑𞥒", "12", "Sappo e ɗiɗi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤁𞤭𞤯𞤭", R.raw.ad2), // Sappo e ɗiɗi
            NumberItem("𞥑𞥓", "13", "Sappo e tati", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤚𞤢𞤼𞤭", R.raw.ad3), // Sappo e tati
            NumberItem("𞥑𞥔", "14", "Sappo e nayi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤐𞤢𞤴𞤭", R.raw.ad4), // Sappo e nayi
            NumberItem("𞥑𞥕", "15", "Sappo e jowi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤮𞤱𞤭", R.raw.ad5), // Sappo e jowi
            NumberItem("𞥑𞥖", "16", "Sappo e jeegom", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤺𞤮𞤥", R.raw.ad6), // Sappo e jeegom
            NumberItem("𞥑𞥗", "17", "Sappo e jeeɗiɗi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤯𞤭𞤯𞤭", R.raw.ad7), // Sappo e jeeɗiɗi
            NumberItem("𞥑𞥘", "18", "Sappo e jeetati", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤼𞤢𞤼𞤭", R.raw.ad8), // Sappo e jeetati
            NumberItem("𞥑𞥙", "19", "Sappo e jeenayi", "𞤅𞤢𞤨𞥆𞤮 𞤫 𞤔𞤫𞥅𞤲𞤢𞤴𞤭", R.raw.ad9) // Sappo e jeenayi
        )
    }
}
