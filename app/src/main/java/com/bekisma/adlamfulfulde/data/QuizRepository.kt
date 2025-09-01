package com.bekisma.adlamfulfulde.data

import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.model.AdlamLetter

class QuizRepository {

    fun getAdlamLetters(): List<AdlamLetter> {
        return listOf(
            AdlamLetter("𞤀", R.raw.adlam1_1, "A", "Aduna"),
            AdlamLetter("𞤁", R.raw.adlam2_1, "BA", "Bano"),
            AdlamLetter("𞤂", R.raw.adlam3_1, "PA", "Paykun"),
            AdlamLetter("𞤃", R.raw.adlam4_1, "MA", "Maro"),
            AdlamLetter("𞤄", R.raw.adlam5_1, "BA", "Bano"),
            AdlamLetter("𞤅", R.raw.adlam6_1, "SA", "Sabo"),
            AdlamLetter("𞤆", R.raw.adlam7_1, "PA", "Pulaar"),
            AdlamLetter("𞤇", R.raw.adlam8_1, "BHA", "Bhalu"), // Implosive B
            AdlamLetter("𞤈", R.raw.adlam9_1, "RA", "Rana"),
            AdlamLetter("𞤉", R.raw.adlam10_1, "E", "Eelo"),
            AdlamLetter("𞤊", R.raw.adlam11_1, "FA", "Fanta"),
            AdlamLetter("𞤋", R.raw.adlam12_1, "I", "Iisa"),
            AdlamLetter("𞤌", R.raw.adlam13_1, "O", "Ode"),
            AdlamLetter("𞤍", R.raw.adlam14_1, "DHA", "Dhalu"), // Implosive D
            AdlamLetter("𞤎", R.raw.adlam15_1, "YA", "Yana"), // Implosive Y
            AdlamLetter("𞤏", R.raw.adlam16_1, "WA", "Wala"),
            AdlamLetter("𞤐", R.raw.adlam17_1, "NA", "Nawru"),
            AdlamLetter("𞤑", R.raw.adlam18_1, "KA", "Kala"),
            AdlamLetter("𞤒", R.raw.adlam19_1, "YA", "Yara"),
            AdlamLetter("𞤓", R.raw.adlam20_1, "U", "Ummaru"),
            AdlamLetter("𞤔", R.raw.adlam21_1, "JA", "Jango"),
            AdlamLetter("𞤕", R.raw.adlam22_1, "CHA", "Chaka"), // Adlam 'c' is often equivalent to Ch
            AdlamLetter("𞤖", R.raw.adlam23_1, "HA", "Hawa"),
            AdlamLetter("𞤗", R.raw.adlam24_1, "GHA", "Ghalu"), // Implosive G
            AdlamLetter("𞤘", R.raw.adlam25_1, "GA", "Gara"),
            AdlamLetter("𞤙", R.raw.adlam26_1, "NYA", "Nyalo"), // Ny
            AdlamLetter("𞤚", R.raw.adlam27_1, "TA", "Tala"),
            AdlamLetter("𞤛", R.raw.adlam28_1, "NGA", "Ngalu") // Ng (velar nasal)
        )
    }
}
