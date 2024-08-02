// package com.bekisma.adlamfulfulde.screens

package com.bekisma.adlamfulfulde.screens

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumbersScreen(navController: NavController) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    val numbersInAdlam = listOf("𞥐", "𞥑", "𞥒", "𞥓", "𞥔", "𞥕", "𞥖", "𞥗", "𞥘", "𞥙")

    var isPlaying by remember { mutableStateOf(false) }
    var currentNumberIndex by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Scaffold(
        topBar = { NumbersTopAppBar(navController, isPlaying, onPlayPauseClick = { isPlaying = !isPlaying }) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                BannerAdView() // Ajoutez la bannière ici
                Spacer(modifier = Modifier.height(16.dp))
                NumbersContent(
                    numbersInAdlam = numbersInAdlam,
                    currentNumberIndex = currentNumberIndex,
                    isPlaying = isPlaying,
                    mediaPlayer = mediaPlayer,
                    onItemClick = { index ->
                        isPlaying = false
                        playSoundForNumber(context, numbersInAdlam[index], mediaPlayer)
                    },
                    innerPadding = innerPadding
                )
            }
        }
    )

    HandlePlayback(isPlaying, currentNumberIndex, numbersInAdlam, mediaPlayer, context) {
        currentNumberIndex = it
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumbersTopAppBar(navController: NavController, isPlaying: Boolean, onPlayPauseClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.numbers_in_adlam)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        actions = {
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play),
                    contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun NumbersContent(
    numbersInAdlam: List<String>,
    currentNumberIndex: Int,
    isPlaying: Boolean,
    mediaPlayer: MediaPlayer,
    onItemClick: (Int) -> Unit,
    innerPadding: PaddingValues
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = innerPadding,
            modifier = Modifier.padding(innerPadding)
        ) {
            items(numbersInAdlam) { number ->
                val index = numbersInAdlam.indexOf(number)
                val isCurrentPlaying = currentNumberIndex == index
                var clicked by remember { mutableStateOf(false) }

                NumberCard(
                    number = number,
                    isCurrentPlaying = isCurrentPlaying,
                    clicked = clicked,
                    onClick = {
                        clicked = !clicked
                        onItemClick(index)
                    }
                )
            }
        }
    }
}

@Composable
fun NumberCard(number: String, isCurrentPlaying: Boolean, clicked: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (clicked || isCurrentPlaying) 12.dp else 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(16.dp)
                .size(if (clicked || isCurrentPlaying) 75.dp else 65.dp)
        ) {
            Text(
                text = number,
                fontSize = 50.sp,
                color = if (isCurrentPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

fun playSoundForNumber(context: Context, number: String, mediaPlayer: MediaPlayer) {
    val soundId = when (number) {
        "𞥐" -> R.raw.ad0
        "𞥑" -> R.raw.ad1
        "𞥒" -> R.raw.ad2
        "𞥓" -> R.raw.ad3
        "𞥔" -> R.raw.ad4
        "𞥕" -> R.raw.ad5
        "𞥖" -> R.raw.ad6
        "𞥗" -> R.raw.ad7
        "𞥘" -> R.raw.ad8
        "𞥙" -> R.raw.ad9
        else -> null
    }

    soundId?.let {
        val uri = "android.resource://${context.packageName}/$it"
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, Uri.parse(uri))
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace() // Handle error gracefully
        }
    }
}

@Composable
fun HandlePlayback(
    isPlaying: Boolean,
    currentNumberIndex: Int,
    numbersInAdlam: List<String>,
    mediaPlayer: MediaPlayer,
    context: Context,
    updateIndex: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            coroutineScope.launch {
                var index = currentNumberIndex
                while (index < numbersInAdlam.size && isPlaying) {
                    playSoundForNumber(context, numbersInAdlam[index], mediaPlayer)
                    delay(2000) // Wait for the sound to finish before moving to the next
                    index++
                    updateIndex(index)
                }
                if (index >= numbersInAdlam.size) {
                    updateIndex(0) // Reset for the next playthrough
                }
            }
        } else {
            mediaPlayer.stop()
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Preview(showBackground = true)
@Composable
fun PreviewNumbersScreen() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme {
        NumbersScreen(navController)
    }
}
