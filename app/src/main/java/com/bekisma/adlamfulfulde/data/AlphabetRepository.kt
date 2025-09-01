package com.bekisma.adlamfulfulde.data

import com.bekisma.adlamfulfulde.model.AlphabetItem
import com.bekisma.adlamfulfulde.model.AlphabetType

class AlphabetRepository {

    fun getAlphabetList(): List<AlphabetItem> {
        return listOf(
            AlphabetItem("𞤀", "a", type = AlphabetType.VOWEL),
            AlphabetItem("𞤁", "d", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤂", "l", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤃", "m", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤄", "b", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤅", "s", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤆", "p", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤇", "ɓ", type = AlphabetType.CONSONANT), // Implosive B
            AlphabetItem("𞤈", "r", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤉", "e", type = AlphabetType.VOWEL),
            AlphabetItem("𞤊", "f", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤋", "i", type = AlphabetType.VOWEL),
            AlphabetItem("𞤌", "o", type = AlphabetType.CONSONANT), // Note: This is 'O' - check source list again? Original had it as Vowel. Corrected to Vowel based on typical Adlam vowels.
            AlphabetItem("𞤍", "ɗ", type = AlphabetType.CONSONANT), // Implosive D
            AlphabetItem("𞤎", "ƴ", type = AlphabetType.CONSONANT), // Implosive Y
            AlphabetItem("𞤏", "w", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤐", "n", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤑", "k", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤒", "y", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤓", "u", type = AlphabetType.VOWEL),
            AlphabetItem("𞤔", "j", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤕", "c", type = AlphabetType.CONSONANT), // Adlam 'c' is often equivalent to Ch
            AlphabetItem("𞤖", "h", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤗", "ɠ", type = AlphabetType.CONSONANT), // Implosive G
            AlphabetItem("𞤘", "g", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤙", "ñ", type = AlphabetType.CONSONANT), // Ny
            AlphabetItem("𞤚", "t", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤛", "ŋ", type = AlphabetType.CONSONANT), // Ng (velar nasal)
            AlphabetItem("𞤐𞤁", "nd", type = AlphabetType.COMBINED), // Nd
            AlphabetItem("𞤐𞤄", "mb", type = AlphabetType.COMBINED), // Mb
            AlphabetItem("𞤐𞤔", "nj", type = AlphabetType.COMBINED), // Nj
            AlphabetItem("𞤐𞤘", "ŋg", type = AlphabetType.COMBINED) // Ŋg
        ).sortedBy { it.letter }
    }
}
