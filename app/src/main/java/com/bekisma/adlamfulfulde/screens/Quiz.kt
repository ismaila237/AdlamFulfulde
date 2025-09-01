package com.bekisma.adlamfulfulde.screens

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.model.AdlamLetter
import com.bekisma.adlamfulfulde.model.DifficultyLevel
import com.bekisma.adlamfulfulde.model.QuizState
import com.bekisma.adlamfulfulde.model.QuestionType
import com.bekisma.adlamfulfulde.viewmodel.QuizViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController, viewModel: QuizViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE) }

    val quizState by viewModel.quizState.collectAsState()
    val quizStarted by viewModel.quizStarted.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val showHint by viewModel.showHint.collectAsState()
    val enableSound by viewModel.enableSound.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBestScore(sharedPreferences)
    }

    DisposableEffect(quizCompleted) {
        onDispose {
            if (quizCompleted) {
                viewModel.saveBestScore(sharedPreferences)
            }
        }
    }

    Scaffold(
        topBar = {
            QuizTopBar(
                navController = navController,
                enableSound = enableSound,
                onToggleSound = { viewModel.toggleSound() }
            )
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
                        onStart = { viewModel.startQuiz(context) },
                        selectedDifficulty = selectedDifficulty,
                        onDifficultyChange = { viewModel.selectDifficulty(it) }
                    )
                } else if (quizCompleted) {
                    QuizCompletedScreen(
                        navController = navController,
                        score = quizState.score,
                        totalQuestions = quizState.totalQuestions,
                        bestScore = quizState.maxStreak, // maxStreak in QuizState now holds bestScore
                        correctAnswers = quizState.correctAnswers,
                        incorrectAnswers = quizState.incorrectAnswers,
                        averageResponseTime = if (quizState.totalQuestions > 0) quizState.totalResponseTime / quizState.totalQuestions else 0f,
                        maxStreak = quizState.maxStreak,
                        onRestart = { viewModel.resetQuiz(); viewModel.startQuiz(context) }
                    )
                } else {
                    QuizInProgressScreen(
                        context = context,
                        quizState = quizState,
                        playSound = { soundResId -> viewModel.playSound(context, soundResId) },
                        onOptionClick = { selectedOption -> viewModel.onOptionSelected(context, selectedOption) },
                        selectedDifficulty = selectedDifficulty,
                        showHint = showHint,
                        onShowHint = { viewModel.toggleHint() },
                        updateElapsedTime = { viewModel.updateElapsedTime() }
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
fun QuizTopBar(
    navController: NavController,
    enableSound: Boolean,
    onToggleSound: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.quiz_title)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        actions = {
            IconButton(onClick = onToggleSound) {
                Icon(
                    imageVector = if (enableSound) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = if (enableSound) "Mute Sound" else "Enable Sound"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun StartQuizDialog(
    onStart: () -> Unit,
    selectedDifficulty: DifficultyLevel,
    onDifficultyChange: (DifficultyLevel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.quiz_instructions_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.quiz_instructions_text),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            DifficultySelector(selectedDifficulty) { difficulty ->
                onDifficultyChange(difficulty)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedDifficulty.color
                )
            ) {
                Text(
                    stringResource(R.string.start),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DifficultyLevel.values().forEach { difficulty ->
                DifficultyButton(
                    difficulty = difficulty,
                    isSelected = currentDifficulty == difficulty,
                    onClick = { onDifficultySelected(difficulty) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DifficultyButton(
    difficulty: DifficultyLevel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .scale(scale),
        shape = RoundedCornerShape(percent = 50),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) difficulty.color else MaterialTheme.colorScheme.outline
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) difficulty.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = difficulty.label,
            color = if (isSelected) difficulty.color else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun QuizCompletedScreen(
    navController: NavController,
    score: Int,
    totalQuestions: Int,
    bestScore: Int,
    correctAnswers: Int,
    incorrectAnswers: Int,
    averageResponseTime: Float,
    maxStreak: Int,
    onRestart: () -> Unit
) {
    val accuracyPercentage = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.quiz_completed),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatsItem(
                            icon = Icons.Default.Star,
                            value = "$score",
                            label = "Score",
                            modifier = Modifier.weight(1f)
                        )

                        StatsItem(
                            icon = Icons.Default.EmojiEvents,
                            value = "$bestScore",
                            label = "Best",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatsItem(
                            icon = Icons.Default.Speed,
                            value = String.format("%.1fs", averageResponseTime),
                            label = "Avg Time",
                            modifier = Modifier.weight(1f)
                        )

                        StatsItem(
                            icon = Icons.Default.Bolt,
                            value = "$maxStreak",
                            label = "Best Streak",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatsItem(
                            icon = Icons.Rounded.Check,
                            value = "$correctAnswers",
                            label = "Correct",
                            modifier = Modifier.weight(1f),
                            valueColor = Color(0xFF4CAF50)
                        )

                        StatsItem(
                            icon = Icons.Rounded.Close,
                            value = "$incorrectAnswers",
                            label = "Incorrect",
                            modifier = Modifier.weight(1f),
                            valueColor = Color(0xFFF44336)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = accuracyPercentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = when {
                            accuracyPercentage >= 80 -> Color(0xFF4CAF50)
                            accuracyPercentage >= 60 -> Color(0xFFFFA000)
                            else -> Color(0xFFF44336)
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Text(
                        text = "$accuracyPercentage% Accuracy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    stringResource(R.string.play_again),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    stringResource(R.string.return_to_home),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun StatsItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun QuizInProgressScreen(
    context: Context,
    quizState: QuizState,
    playSound: (Int) -> Unit,
    onOptionClick: (AdlamLetter) -> Unit,
    selectedDifficulty: DifficultyLevel,
    showHint: Boolean,
    onShowHint: () -> Unit,
    updateElapsedTime: () -> Unit
) {
    val currentQuestion = quizState.questions[quizState.currentQuestionIndex]
    var remainingTime by remember { mutableStateOf(selectedDifficulty.timeLimit) }
    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val scale by pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse scale"
    )

    LaunchedEffect(quizState.currentQuestionIndex) {
        remainingTime = selectedDifficulty.timeLimit
    }

    LaunchedEffect(remainingTime, quizState.showFeedback) {
        if (remainingTime > 0 && !quizState.showFeedback) {
            delay(1000)
            remainingTime -= 1
            updateElapsedTime()
        } else if (remainingTime <= 0 && !quizState.showFeedback) {
            onOptionClick(AdlamLetter("", -1, "")) // Pass a dummy AdlamLetter for timeout
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section with score and streak
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${quizState.score}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.question_number, quizState.currentQuestionIndex + 1),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (quizState.streak > 0) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFA000)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.scale(scale)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${quizState.streak}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timer section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                LinearProgressIndicator(
                    progress = remainingTime.toFloat() / selectedDifficulty.timeLimit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = when {
                        remainingTime > selectedDifficulty.timeLimit * 0.6f -> selectedDifficulty.color
                        remainingTime > selectedDifficulty.timeLimit * 0.3f -> Color(0xFFFFA000)
                        else -> Color(0xFFF44336)
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Text(
                    text = "$remainingTime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 16.dp)
                )
            }

            // Letter display and sound button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    val displayValue = when (currentQuestion.questionType) {
                        QuestionType.ADLAM_TO_LATIN -> currentQuestion.correctAnswer.symbol
                        QuestionType.LATIN_TO_ADLAM -> currentQuestion.correctAnswer.latinEquivalent
                        QuestionType.SOUND_TO_ADLAM -> ""
                    }

                    if (displayValue.isNotBlank()) {
                        Text(
                            text = displayValue,
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { playSound(currentQuestion.correctAnswer.soundResId) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = stringResource(R.string.play_sound),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentQuestion.options.size) { index ->
                    val option = currentQuestion.options[index]
                    ElevatedButton(
                        onClick = { onOptionClick(option) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        ),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            when (currentQuestion.questionType) {
                                QuestionType.ADLAM_TO_LATIN -> option.latinEquivalent
                                QuestionType.LATIN_TO_ADLAM, QuestionType.SOUND_TO_ADLAM -> option.symbol
                            },
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hint section
            if (!showHint) {
                OutlinedButton(
                    onClick = onShowHint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        stringResource(R.string.show_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.hint),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        Text(
                            text = currentQuestion.correctAnswer.symbol,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Feedback overlay
        AnimatedVisibility(
            visible = quizState.showFeedback,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (quizState.lastAnswerWasCorrect == true) Color(0xFF4CAF50) else Color(0xFFF44336)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.size(180.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (quizState.lastAnswerWasCorrect == true) Icons.Rounded.Check else Icons.Rounded.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )

                        Text(
                            text = if (quizState.lastAnswerWasCorrect == true) stringResource(R.string.correct) else stringResource(R.string.incorrect),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        if (quizState.lastAnswerWasCorrect == false) {
                            Text(
                                text = currentQuestion.correctAnswer.symbol,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun PreviewQuizScreen() {
    AdlamFulfuldeTheme {
        QuizScreen(navController = rememberNavController())
    }
}