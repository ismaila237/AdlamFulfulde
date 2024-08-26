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
    val numbersInLatin = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

    var isPlaying by remember { mutableStateOf(false) }
    var currentNumberIndex by remember { mutableStateOf(0) }
    var showLatinNumbers by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            NumbersTopAppBar(
                navController = navController,
                isPlaying = isPlaying,
                onPlayPauseClick = { isPlaying = !isPlaying },
                showLatinNumbers = showLatinNumbers,
                onToggleLatinNumbers = { showLatinNumbers = !showLatinNumbers }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                BannerAdView()
                Spacer(modifier = Modifier.height(16.dp))
                NumbersContent(
                    numbersInAdlam = numbersInAdlam,
                    numbersInLatin = numbersInLatin,
                    currentNumberIndex = currentNumberIndex,
                    isPlaying = isPlaying,
                    showLatinNumbers = showLatinNumbers,
                    onItemClick = { index ->
                        isPlaying = false
                        currentNumberIndex = index
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
fun NumbersTopAppBar(
    navController: NavController,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    showLatinNumbers: Boolean,
    onToggleLatinNumbers: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.numbers_in_adlam)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        actions = {
            IconButton(onClick = onToggleLatinNumbers) {
                Icon(
                    painter = painterResource(id = if (showLatinNumbers) R.drawable.adlam_icon else R.drawable.latin_icon),
                    contentDescription = stringResource(if (showLatinNumbers) R.string.show_adlam else R.string.show_latin)
                )
            }
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play),
                    contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
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
    numbersInLatin: List<String>,
    currentNumberIndex: Int,
    isPlaying: Boolean,
    showLatinNumbers: Boolean,
    onItemClick: (Int) -> Unit,
    innerPadding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(numbersInAdlam.size) { index ->
            NumberCard(
                adlamNumber = numbersInAdlam[index],
                latinNumber = numbersInLatin[index],
                isCurrentPlaying = currentNumberIndex == index,
                showLatinNumbers = showLatinNumbers,
                onClick = { onItemClick(index) }
            )
        }
    }
}

@Composable
fun NumberCard(
    adlamNumber: String,
    latinNumber: String,
    isCurrentPlaying: Boolean,
    showLatinNumbers: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentPlaying) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = if (showLatinNumbers) latinNumber else adlamNumber,
                fontSize = 40.sp,
                color = if (isCurrentPlaying)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
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
