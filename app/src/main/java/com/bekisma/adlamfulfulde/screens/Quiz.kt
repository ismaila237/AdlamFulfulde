package com.bekisma.adlamfulfulde.screens

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Constants for game setup
val adlamLetters = listOf(
    "𞤀" to R.raw.adlam1_1, "𞤁" to R.raw.adlam2_1, "𞤂" to R.raw.adlam3_1,
    "𞤃" to R.raw.adlam4_1, "𞤄" to R.raw.adlam5_1, "𞤅" to R.raw.adlam6_1,
    "𞤆" to R.raw.adlam7_1, "𞤇" to R.raw.adlam8_1, "𞤈" to R.raw.adlam9_1,
    "𞤉" to R.raw.adlam10_1, "𞤊" to R.raw.adlam11_1, "𞤋" to R.raw.adlam12_1,
    "𞤌" to R.raw.adlam13_1, "𞤍" to R.raw.adlam14_1, "𞤎" to R.raw.adlam15_1,
    "𞤏" to R.raw.adlam16_1, "𞤐" to R.raw.adlam17_1, "𞤑" to R.raw.adlam18_1,
    "𞤒" to R.raw.adlam19_1, "𞤓" to R.raw.adlam20_1, "𞤔" to R.raw.adlam21_1,
    "𞤕" to R.raw.adlam22_1, "𞤖" to R.raw.adlam23_1, "𞤗" to R.raw.adlam24_1,
    "𞤘" to R.raw.adlam25_1, "𞤙" to R.raw.adlam26_1, "𞤚" to R.raw.adlam27_1,
    "𞤛" to R.raw.adlam28_1, "𞤐𞤁" to R.raw.son_nul, "𞤐𞤄" to R.raw.son_nul,
    "𞤐𞤶" to R.raw.son_nul, "𞤐𞤘" to R.raw.son_nul
)


val adlamToLatinMapQuiz = mapOf(
    "𞤀" to "A", "𞤁" to "DA", "𞤂" to "LA", "𞤃" to "MA", "𞤄" to "BA", "𞤅" to "SA", "𞤆" to "PA",
    "𞤇" to "ƁA", "𞤈" to "RA", "𞤉" to "E", "𞤊" to "FA", "𞤋" to "I", "𞤌" to "O", "𞤍" to "ƊA", "𞤎" to "ƳA",
    "𞤏" to "WA", "𞤐" to "NA", "𞤑" to "KA", "𞤒" to "YA", "𞤓" to "U", "𞤔" to "JA", "𞤕" to "CA", "𞤖" to "HA",
    "𞤗" to "QA", "𞤘" to "GA", "𞤙" to "ÑA", "𞤚" to "TA", "𞤛" to "NHA",
    "𞤐𞤁" to "NDA", "𞤐𞤄" to "MBA", "𞤐𞤔" to "NJA", "𞤐𞤘" to "NGA"
)


val difficultyLevels = mapOf(
    15 to R.string.beginner,
    8 to R.string.intermediate,
    3 to R.string.advanced
)

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
    var difficulty by rememberSaveable { mutableStateOf(15) } // 15 seconds for beginner by default
    var bestScore by remember { mutableStateOf(sharedPreferences.getInt("bestScore", 0)) }

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
        playSound(context, adlamLetters[currentLetterIndex].second)
        quizStarted = true
    }

    fun generateOptions(correctIndex: Int, size: Int = 4): List<Pair<String, Int>> {
        val options = mutableSetOf(correctIndex)
        while (options.size < size) {
            val randomIndex = Random.nextInt(adlamLetters.size)
            options.add(randomIndex)
        }
        return options.map { adlamLetters[it] }.shuffled()
    }

    var options by remember { mutableStateOf(generateOptions(currentLetterIndex)) }

    if (allIndices.isEmpty()) {
        quizCompleted = true
    }

    fun updateBestScore() {
        if (score > bestScore) {
            bestScore = score
            with(sharedPreferences.edit()) {
                putInt("bestScore", score)
                apply()
            }
        }
    }

    if (!quizStarted) {
        StartQuizDialog(
            onStart = {
                startQuiz()
                options = generateOptions(currentLetterIndex)
            },
            difficulty = difficulty,
            onDifficultyChange = { difficulty = it }
        )
    }

    Scaffold(
        topBar = {
            QuizTopBar(navController)
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (quizCompleted) {
                    updateBestScore()
                    QuizCompletedScreen(
                        navController = navController,
                        score = score,
                        totalQuestions = totalQuestions,
                        bestScore = bestScore
                    )
                } else if (quizStarted) {
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
                                    playSound(context, adlamLetters[currentLetterIndex].second)
                                } else {
                                    quizCompleted = true
                                }
                            }
                        },
                        difficulty = difficulty,
                        quizCompleted = { quizCompleted = true }
                    )
                }

                BannerAdView() // Ajoutez la bannière ici
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.quiz_title)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        }
    )
}

@Composable
fun StartQuizDialog(onStart: () -> Unit, difficulty: Int, onDifficultyChange: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Do nothing to force user to acknowledge dialog */ },
        confirmButton = {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(stringResource(R.string.start))
            }
        },
        title = { Text(text = stringResource(R.string.quiz_instructions_title)) },
        text = {
            Column {
                Text(stringResource(R.string.quiz_instructions_text))
                Spacer(modifier = Modifier.height(16.dp))
                DifficultySelector(difficulty) { selectedDifficulty ->
                    onDifficultyChange(selectedDifficulty)
                }
            }
        }
    )
}

@Composable
fun QuizCompletedScreen(
    navController: NavController,
    score: Int,
    totalQuestions: Int,
    bestScore: Int
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
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.final_score, score, adlamLetters.size),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.best_score, bestScore),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        QuizCompletedButtons(navController = navController)
    }
}

@Composable
fun QuizCompletedButtons(navController: NavController) {
    Button(
        onClick = {
            navController.navigateUp()
            // Reset quiz state
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = stringResource(R.string.replay),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Bold
        )
    }
    Button(
        onClick = { navController.navigateUp() },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(
            text = stringResource(R.string.return_to_home),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuizInProgressScreen(
    context: Context,
    score: Int,
    totalQuestions: Int,
    options: List<Pair<String, Int>>,
    currentLetterIndex: Int,
    feedbackMessage: String,
    correctMessage: String,
    incorrectMessage: String,
    playSound: (Context, Int) -> Unit,
    onOptionClick: (Pair<String, Int>) -> Unit,
    difficulty: Int,
    quizCompleted: () -> Unit
) {
    var localQuizCompleted by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf(difficulty) }

    LaunchedEffect(currentLetterIndex) {
        remainingTime = difficulty
    }

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000)
            remainingTime -= 1
        } else {
            onOptionClick(Pair("", -1))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.score, score),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.question, totalQuestions + 1),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Latin: ${adlamToLatinMapQuiz[adlamLetters[currentLetterIndex].first] ?: "?"}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(difficultyLevels[difficulty] ?: R.string.advanced),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.remaining_time, remainingTime),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
            color = if (remainingTime <= 5) Color.Red else Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LinearProgressIndicator(
            progress = totalQuestions.toFloat() / adlamLetters.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = {
                playSound(context, adlamLetters[currentLetterIndex].second)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.play_sound),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(options.size) { index ->
                val option = options[index]
                Button(
                    onClick = { onOptionClick(option) },
                    modifier = Modifier
                        .padding(8.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = option.first,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val animatedColor by animateColorAsState(
            targetValue = if (feedbackMessage == correctMessage) Color.Green else if (feedbackMessage == incorrectMessage) Color.Red else Color.Transparent,
            animationSpec = tween(durationMillis = 500)
        )

        Text(
            text = feedbackMessage,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold,
            color = animatedColor,
            modifier = Modifier
                .background(animatedColor.copy(alpha = 0.3f))
                .padding(8.dp)
        )
    }
}

@Composable
fun DifficultySelector(currentDifficulty: Int, onDifficultySelected: (Int) -> Unit) {
    val options = listOf(
        R.string.beginner to 15,
        R.string.intermediate to 8,
        R.string.advanced to 3
    )
    Column {
        Text(stringResource(R.string.select_difficulty), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        options.forEach { (labelResId, value) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = currentDifficulty == value,
                    onClick = { onDifficultySelected(value) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(labelResId))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme {
        QuizScreen(navController = navController)
    }
}
