package com.bekisma.adlamfulfulde.screens

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Data classes for better structure
data class AdlamLetter(val symbol: String, val soundResId: Int, val latinEquivalent: String)

// Constants
val adlamLetters = listOf(
    AdlamLetter("𞤀", R.raw.adlam1_1, "A"),
    AdlamLetter("𞤁", R.raw.adlam2_1, "DA"),
    AdlamLetter("𞤂", R.raw.adlam3_1, "LA"),
    AdlamLetter("𞤃", R.raw.adlam4_1, "MA"),
    AdlamLetter("𞤄", R.raw.adlam5_1, "BA"),
    // Ajoutez le reste des lettres ici...
)

// Difficulté du quiz
enum class DifficultyLevel(val timeLimit: Int, val labelResId: Int) {
    BEGINNER(15, R.string.beginner),
    INTERMEDIATE(8, R.string.intermediate),
    ADVANCED(3, R.string.advanced)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE)
    val allIndices = remember { adlamLetters.indices.toMutableList() }
    var currentLetterIndex by rememberSaveable { mutableStateOf(0) }
    var score by rememberSaveable { mutableStateOf(0) }
    var totalQuestions by rememberSaveable { mutableStateOf(0) }
    var feedbackMessage by rememberSaveable { mutableStateOf("") }
    var quizCompleted by rememberSaveable { mutableStateOf(false) }
    var quizStarted by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val mediaPlayer = remember { MediaPlayer() }
    var selectedDifficulty by rememberSaveable { mutableStateOf(DifficultyLevel.BEGINNER) }
    var bestScore by remember { mutableStateOf(sharedPreferences.getInt("bestScore", 0)) }
    var showHint by remember { mutableStateOf(false) }

    val correctMessage = stringResource(R.string.correct)
    val incorrectMessage = stringResource(R.string.incorrect)

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    fun playSound(context: Context, soundResId: Int) {
        mediaPlayer.reset()
        val afd = context.resources.openRawResourceFd(soundResId) ?: return
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun startQuiz() {
        currentLetterIndex = allIndices.random()
        playSound(context, adlamLetters[currentLetterIndex].soundResId)
        quizStarted = true
        score = 0
        totalQuestions = 0
        showHint = false
    }

    fun generateOptions(correctIndex: Int, size: Int = 4): List<AdlamLetter> {
        val options = mutableSetOf(adlamLetters[correctIndex])
        while (options.size < size) {
            val randomIndex = Random.nextInt(adlamLetters.size)
            options.add(adlamLetters[randomIndex])
        }
        return options.shuffled()
    }

    var options by remember { mutableStateOf(generateOptions(currentLetterIndex)) }

    fun updateBestScore() {
        if (score > bestScore) {
            bestScore = score
            with(sharedPreferences.edit()) {
                putInt("bestScore", score)
                apply()
            }
        }
    }

    Scaffold(
        topBar = {
            QuizTopBar(navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (!quizStarted) {
                    StartQuizDialog(
                        onStart = {
                            startQuiz()
                            options = generateOptions(currentLetterIndex)
                        },
                        selectedDifficulty = selectedDifficulty,
                        onDifficultyChange = { selectedDifficulty = it }
                    )
                } else if (quizCompleted) {
                    updateBestScore()
                    QuizCompletedScreen(
                        navController = navController,
                        score = score,
                        totalQuestions = totalQuestions,
                        bestScore = bestScore,
                        onRestart = {
                            quizCompleted = false
                            allIndices.clear()
                            allIndices.addAll(adlamLetters.indices)
                            startQuiz()
                        }
                    )
                } else {
                    QuizInProgressScreen(
                        context = context,
                        score = score,
                        totalQuestions = totalQuestions,
                        options = options,
                        currentLetterIndex = currentLetterIndex,
                        feedbackMessage = feedbackMessage,
                        correctMessage = correctMessage,
                        incorrectMessage = incorrectMessage,
                        playSound = ::playSound,
                        onOptionClick = { selectedOption ->
                            coroutineScope.launch {
                                totalQuestions += 1
                                if (selectedOption == adlamLetters[currentLetterIndex]) {
                                    score += 1
                                    feedbackMessage = correctMessage
                                } else {
                                    feedbackMessage = incorrectMessage
                                }
                                delay(1000)
                                feedbackMessage = ""
                                allIndices.remove(currentLetterIndex)
                                if (allIndices.isNotEmpty()) {
                                    currentLetterIndex = allIndices.random()
                                    options = generateOptions(currentLetterIndex)
                                    playSound(context, adlamLetters[currentLetterIndex].soundResId)
                                    showHint = false
                                } else {
                                    quizCompleted = true
                                }
                            }
                        },
                        selectedDifficulty = selectedDifficulty,
                        showHint = showHint,
                        onShowHint = { showHint = true }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                BannerAdView()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopBar(navController: NavController) {
    TopAppBar(
        title = { Text(stringResource(R.string.quiz_title)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun StartQuizDialog(
    onStart: () -> Unit,
    selectedDifficulty: DifficultyLevel,
    onDifficultyChange: (DifficultyLevel) -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing to force user to acknowledge dialog */ },
        title = { Text(text = stringResource(R.string.quiz_instructions_title)) },
        text = {
            Column {
                Text(stringResource(R.string.quiz_instructions_text))
                Spacer(modifier = Modifier.height(16.dp))
                DifficultySelector(selectedDifficulty) { difficulty ->
                    onDifficultyChange(difficulty)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(stringResource(R.string.start))
            }
        }
    )
}

@Composable
fun DifficultySelector(
    currentDifficulty: DifficultyLevel,
    onDifficultySelected: (DifficultyLevel) -> Unit
) {
    Column {
        Text(
            stringResource(R.string.select_difficulty),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        DifficultyLevel.values().forEach { difficulty ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = currentDifficulty == difficulty,
                    onClick = { onDifficultySelected(difficulty) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(difficulty.labelResId),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (currentDifficulty == difficulty)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun QuizCompletedScreen(
    navController: NavController,
    score: Int,
    totalQuestions: Int,
    bestScore: Int,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.quiz_completed),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.final_score, score, totalQuestions),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.best_score, bestScore),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.play_again))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.return_to_home))
        }
    }
}

@Composable
fun QuizInProgressScreen(
    context: Context,
    score: Int,
    totalQuestions: Int,
    options: List<AdlamLetter>,
    currentLetterIndex: Int,
    feedbackMessage: String,
    correctMessage: String,
    incorrectMessage: String,
    playSound: (Context, Int) -> Unit,
    onOptionClick: (AdlamLetter) -> Unit,
    selectedDifficulty: DifficultyLevel,
    showHint: Boolean,
    onShowHint: () -> Unit
) {
    var remainingTime by remember { mutableStateOf(selectedDifficulty.timeLimit) }

    LaunchedEffect(currentLetterIndex) {
        remainingTime = selectedDifficulty.timeLimit
    }

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000)
            remainingTime -= 1
        } else {
            onOptionClick(AdlamLetter("", -1, ""))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.score, score),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.question_number, totalQuestions + 1),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = adlamLetters[currentLetterIndex].latinEquivalent,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.remaining_time, remainingTime),
            style = MaterialTheme.typography.titleMedium,
            color = if (remainingTime <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
        )

        LinearProgressIndicator(
            progress = remainingTime.toFloat() / selectedDifficulty.timeLimit,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedButton(
            onClick = { playSound(context, adlamLetters[currentLetterIndex].soundResId) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(stringResource(R.string.play_sound), color = MaterialTheme.colorScheme.onPrimaryContainer)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options.size) { index ->
                val option = options[index]
                ElevatedButton(
                    onClick = { onOptionClick(option) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        option.symbol,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!showHint) {
            TextButton(
                onClick = onShowHint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.show_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        } else {
            Text(
                text = adlamLetters[currentLetterIndex].symbol,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = feedbackMessage.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = feedbackMessage,
                style = MaterialTheme.typography.titleMedium,
                color = if (feedbackMessage == correctMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
