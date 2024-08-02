// package com.bekisma.adlamfulfulde.screens

package com.bekisma.adlamfulfulde.screens

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme

@Composable
fun AlphabetScreen(navController: NavController) {
    val alphabetList = listOf(
        AlphabetItem("𞤀"), AlphabetItem("𞤁"), AlphabetItem("𞤂"),
        AlphabetItem("𞤃"), AlphabetItem("𞤄"), AlphabetItem("𞤅"),
        AlphabetItem("𞤆"), AlphabetItem("𞤇"), AlphabetItem("𞤈"),
        AlphabetItem("𞤉"), AlphabetItem("𞤊"), AlphabetItem("𞤋"),
        AlphabetItem("𞤌"), AlphabetItem("𞤍"), AlphabetItem("𞤎"),
        AlphabetItem("𞤏"), AlphabetItem("𞤐"), AlphabetItem("𞤑"),
        AlphabetItem("𞤒"), AlphabetItem("𞤓"), AlphabetItem("𞤔"),
        AlphabetItem("𞤕"), AlphabetItem("𞤖"), AlphabetItem("𞤗"),
        AlphabetItem("𞤘"), AlphabetItem("𞤙"), AlphabetItem("𞤚"),
        AlphabetItem("𞤛"), AlphabetItem("𞤐𞤁"), AlphabetItem("𞤐𞤄"),
        AlphabetItem("𞤐𞤔"), AlphabetItem("𞤐𞤘")
    )

    AdlamFulfuldeTheme {
        Scaffold(
            topBar = { AlphabetTopBar(navController) },
            content = { innerPadding ->
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column(modifier = Modifier.padding(innerPadding)) {
                        BannerAdView() // Ajoutez la bannière ici
                        Divider()
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            contentPadding = PaddingValues(8.dp),
                            content = {
                                items(alphabetList) { letter ->
                                    AlphabetItemCard(letter, navController = navController)
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.learn_alphabet)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
fun AlphabetItemCard(letter: AlphabetItem, navController: NavController) {
    var isClicked by remember { mutableStateOf(false) }
    val gradientColors = listOf(
        Color(0xFFFFA500), // Orange
        Color(0xFFFF4500), // Orange Red
        Color(0xFFDA70D6), // Orchid
        Color(0xFF9932CC), // Dark Orchid
        Color(0xFF00CED1), // Dark Turquoise
        Color(0xFF1E90FF), // Dodger Blue
        Color(0xFFADFF2F), // Green Yellow
        Color(0xFF7CFC00), // Lawn Green
        Color(0xFFFF69B4)  // Hot Pink
    )

    val randomGradient = gradientColors.shuffled().take(2)
    val gradientBrush = Brush.linearGradient(colors = randomGradient)

    val cardElevation by animateDpAsState(
        targetValue = if (isClicked) 12.dp else 6.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val isVowel = letter.letter in listOf("𞤀", "𞤉", "𞤋", "𞤌", "𞤓")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    isClicked = !isClicked
                    navController.navigate("DetailAlphabetScreen/${letter.letter}")
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = cardElevation
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                if (isVowel) {
                    Text(
                        text = letter.letter,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = letter.letter,
                        style = MaterialTheme.typography.titleMedium.copy(
                            brush = gradientBrush
                        ),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

data class AlphabetItem(val letter: String)

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun AlphabetScreenPreview() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme {
        AlphabetScreen(navController)
    }
}
