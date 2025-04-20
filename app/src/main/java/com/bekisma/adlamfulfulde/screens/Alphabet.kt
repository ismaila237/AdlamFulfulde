package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView

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

    Scaffold(
        topBar = { AlphabetTopBar(navController) },
        content = { innerPadding ->
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f),
                        columns = GridCells.Adaptive(minSize = 80.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        content = {
                            items(alphabetList) { letter ->
                                AlphabetItemCard(letter, navController = navController)
                            }
                        }
                    )

                    Divider()
                    BannerAdView()
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetTopBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.learn_alphabet),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetItemCard(letter: AlphabetItem, navController: NavController) {
    var isClicked by remember { mutableStateOf(false) }

    val animatedElevation by animateDpAsState(
        targetValue = if (isClicked) 12.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isClicked) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface
    )

    val isVowel = letter.letter in listOf("𞤀", "𞤉", "𞤋", "𞤌", "𞤓")

    Card(
        modifier = Modifier
            .size(80.dp)
            .shadow(animatedElevation, CircleShape),
        shape = CircleShape,
        onClick = {
            isClicked = !isClicked
            navController.navigate("DetailAlphabetScreen/${letter.letter}")
        },
        colors = CardDefaults.cardColors(
            containerColor = if (isVowel) MaterialTheme.colorScheme.primary else backgroundColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.letter,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isVowel) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                ),
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class AlphabetItem(val letter: String)