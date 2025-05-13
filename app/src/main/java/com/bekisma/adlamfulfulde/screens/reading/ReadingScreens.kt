package com.bekisma.adlamfulfulde.screens.reading

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.ReadingPassage
import com.bekisma.adlamfulfulde.data.WordTiming
import com.bekisma.adlamfulfulde.data.getReadingPassages
import com.bekisma.adlamfulfulde.screens.MediaPlayerSingleton // Assurez-vous qu'il est accessible
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPassageListScreen(navController: NavController) {
    val passages = remember { getReadingPassages() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reading_passages_title)) }, // Nouvelle string
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (passages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.reading_passages_empty)) // Nouvelle string
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(passages, key = { it.id }) { passage ->
                PassageListItem(passage = passage) {
                    navController.navigate("reading_player/${passage.id}")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassageListItem(passage: ReadingPassage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = passage.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.difficulty_label, passage.difficulty), // Nouvelle string
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingPlayerScreen(navController: NavController, passageId: Int?) {
    val passages = remember { getReadingPassages() }
    val passage = remember(passageId) { passages.find { it.id == passageId } }
    val context = LocalContext.current

    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var currentHighlightedWordIndex by remember { mutableIntStateOf(-1) }

    fun prepareAndPlay() {
        if (passage == null || passage.soundResId == R.raw.son_nul) return
        try {
            mediaPlayer.reset()
            val afd = context.resources.openRawResourceFd(passage.soundResId)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer.prepare()
            totalDurationMs = mediaPlayer.duration.toLong()
            mediaPlayer.start()
            isPlaying = true
        } catch (e: Exception) {
            isPlaying = false
            // Gérer l'erreur, ex: afficher un Toast
        }
    }

    fun pausePlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    fun resumePlayback() {
        if (passage != null && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    fun stopPlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset() // Important pour pouvoir le réutiliser
        isPlaying = false
        currentPositionMs = 0L
        currentHighlightedWordIndex = -1
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer.seekTo(positionMs.toInt())
        currentPositionMs = positionMs
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive && isPlaying && mediaPlayer.isPlaying) {
                currentPositionMs = mediaPlayer.currentPosition.toLong()
                // Logique de surlignage (si wordTimings est disponible)
                passage?.wordTimings?.let { timings ->
                    val newIndex = timings.indexOfFirst { timing ->
                        currentPositionMs >= timing.startTimeMs && currentPositionMs < timing.endTimeMs
                    }
                    if (newIndex != currentHighlightedWordIndex) {
                        currentHighlightedWordIndex = newIndex
                    }
                }
                delay(100) // Mettre à jour la position toutes les 100ms
            }
            if (isPlaying && !mediaPlayer.isPlaying) { // S'est terminé naturellement
                isPlaying = false
                currentPositionMs = totalDurationMs // ou mediaPlayer.duration.toLong()
            }
        }
    }

    mediaPlayer.setOnCompletionListener {
        isPlaying = false
        currentPositionMs = totalDurationMs
        currentHighlightedWordIndex = -1
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(passage?.title ?: stringResource(R.string.reading_player_title)) }, // Nouvelle string
                navigationIcon = {
                    IconButton(onClick = {
                        stopPlayback()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (passage == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.reading_passage_not_found)) // Nouvelle string
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Affichage du Texte Adlam
            Card(
                modifier = Modifier
                    .weight(1f) // Prend l'espace disponible
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                val scrollState = rememberScrollState()
                val annotatedText = remember(passage.adlamText, currentHighlightedWordIndex, passage.wordTimings) {
                    if (passage.wordTimings.isNullOrEmpty() || currentHighlightedWordIndex < 0) {
                        AnnotatedString(passage.adlamText)
                    } else {
                        // Logique de création de l'AnnotatedString pour le surlignage
                        // Ceci est une implémentation simpliste. Une vraie solution
                        // nécessiterait de parser le texte en mots et de les styler.
                        // Pour l'instant, nous ne surlignons pas pour simplifier.
                        // Vous pouvez ajouter cette logique plus tard si les `wordTimings` sont fournis.

                        // Exemple de base de surlignage (si vous avez les timings et les mots séparés)
                        // Si vous avez une liste de mots simple du passage :
                        // val words = passage.adlamText.split(" ") // Simplification
                        // buildAnnotatedString {
                        //    words.forEachIndexed { index, word ->
                        //        if (index == currentHighlightedWordIndex) { // Supposons que wordTimings correspond à l'index des mots
                        //            pushStyle(SpanStyle(background = MaterialTheme.colorScheme.tertiaryContainer))
                        //            append("$word ")
                        //            pop()
                        //        } else {
                        //            append("$word ")
                        //        }
                        //    }
                        //}
                        AnnotatedString(passage.adlamText) // Version sans surlignage pour l'instant
                    }
                }
                Text(
                    text = annotatedText,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize() // Remplir la carte
                        .verticalScroll(scrollState),
                    fontSize = 20.sp, // Ajustez la taille de la police
                    lineHeight = 30.sp // Ajustez l'interligne pour une meilleure lisibilité
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contrôles Audio
            if (totalDurationMs > 0) {
                Slider(
                    value = currentPositionMs.toFloat(),
                    onValueChange = { newPosition -> seekTo(newPosition.toLong()) },
                    valueRange = 0f..totalDurationMs.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { stopPlayback(); prepareAndPlay() }, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Filled.Replay, contentDescription = stringResource(R.string.replay_sound), modifier = Modifier.size(32.dp)) // Nouvelle string
                }
                IconButton(onClick = {
                    if (isPlaying) pausePlayback() else if (currentPositionMs > 0 && currentPositionMs < totalDurationMs) resumePlayback() else prepareAndPlay()
                }, modifier = Modifier.size(72.dp)) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                        contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { stopPlayback() }, modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Filled.StopCircle, contentDescription = stringResource(R.string.stop), modifier = Modifier.size(32.dp)) // Nouvelle string
                }
            }
        }
    }
}