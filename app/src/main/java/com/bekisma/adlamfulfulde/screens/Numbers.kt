package com.bekisma.adlamfulfulde.screens

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import com.bekisma.adlamfulfulde.model.DisplayMode
import com.bekisma.adlamfulfulde.model.NumberItem
import com.bekisma.adlamfulfulde.model.QuizState
import com.bekisma.adlamfulfulde.model.ScreenMode
import com.bekisma.adlamfulfulde.viewmodel.NumbersViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// --- Main Screen Composable ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumbersScreen(navController: NavController, viewModel: NumbersViewModel = viewModel()) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val currentMode by viewModel.currentMode.collectAsState()
    val displayMode by viewModel.displayMode.collectAsState()
    val currentNumberIndex by viewModel.currentNumberIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val autoPlaySpeed by viewModel.autoPlaySpeed.collectAsState()
    val showInfoDialog by viewModel.showInfoDialog.collectAsState()
    val quizState by viewModel.quizState.collectAsState()
    val showQuizResultDialog by viewModel.showQuizResultDialog.collectAsState()

    // Show info dialog
    if (showInfoDialog) {
        InfoDialog(onDismiss = { viewModel.showInfoDialog(false) })
    }

    Scaffold(
        topBar = {
            NumbersTopAppBar(
                navController = navController,
                currentMode = currentMode,
                onToggleMode = { viewModel.toggleMode() },
                isPlaying = isPlaying,
                onPlayPauseClick = { viewModel.togglePlayPause(context) },
                displayMode = displayMode,
                onDisplayModeChanged = { viewModel.setDisplayMode(it) },
                autoPlaySpeed = autoPlaySpeed,
                onSpeedChanged = { viewModel.setAutoPlaySpeed(it) },
                onInfoClick = { viewModel.showInfoDialog(true) },
                isPlayPauseEnabled = currentMode == ScreenMode.LEARNING,
                isSpeedControlEnabled = currentMode == ScreenMode.LEARNING,
                isDisplayModeEnabled = currentMode == ScreenMode.LEARNING
            )
        },
        content = { innerPadding ->
            when (currentMode) {
                ScreenMode.LEARNING -> LearningContent(
                    numberItems = viewModel.numberItems,
                    currentNumberIndex = currentNumberIndex,
                    isPlaying = isPlaying,
                    displayMode = displayMode,
                    innerPadding = innerPadding,
                    onItemClick = { index ->
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.selectNumber(index, context)
                    }
                )
                ScreenMode.QUIZ -> quizState?.let { state ->
                    QuizContent(
                        quizState = state,
                        innerPadding = innerPadding,
                        onAnswerSelected = { selectedOption ->
                            viewModel.onAnswerSelected(selectedOption)
                        },
                        onNextQuestion = { viewModel.onNextQuestion() }
                    )
                } ?: Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
    )

    // Autoplay handler
    if (currentMode == ScreenMode.LEARNING) {
        AutoPlayHandler(
            isPlaying = isPlaying,
            currentNumberIndex = currentNumberIndex,
            numberItems = viewModel.numberItems,
            context = context,
            autoPlaySpeed = autoPlaySpeed,
            updateIndex = { viewModel.selectNumber(it, context) },
            playSoundFn = { item -> /* Handled by selectNumber */ }
        )
    }

    // Quiz result dialog
    if (showQuizResultDialog && quizState != null) {
        QuizResultDialog(
            score = quizState!!.score,
            totalQuestions = quizState!!.questions.size,
            onDismiss = { viewModel.dismissQuizResult() },
            onPlayAgain = { viewModel.playAgainQuiz() }
        )
    }
}

// --- Learning Mode Content ---

@Composable
fun LearningContent(
    numberItems: List<NumberItem>,
    currentNumberIndex: Int,
    isPlaying: Boolean,
    displayMode: DisplayMode,
    innerPadding: PaddingValues,
    onItemClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedCurrentNumber(
                currentItem = numberItems.getOrElse(currentNumberIndex) { numberItems.first() },
                displayMode = displayMode,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            NumbersGrid(
                numberItems = numberItems,
                currentNumberIndex = currentNumberIndex,
                isPlaying = isPlaying,
                displayMode = displayMode,
                onItemClick = onItemClick
            )
        }
        BannerAdView(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

// --- Quiz Mode Content ---

@Composable
fun QuizContent(
    quizState: QuizState,
    innerPadding: PaddingValues,
    onAnswerSelected: (String) -> Unit,
    onNextQuestion: () -> Unit
) {
    if (quizState.questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Impossible de générer le quiz pour le moment.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    if (quizState.currentQuestionIndex >= quizState.questions.size) {
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Quiz terminé ! Affichage des résultats...",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val currentQuestion = quizState.questions[quizState.currentQuestionIndex]
    var selectedOption by remember(quizState.currentQuestionIndex) { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question ${quizState.currentQuestionIndex + 1}/${quizState.questions.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Score: ${quizState.score}",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = currentQuestion.questionText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        currentQuestion.options.forEach { option ->
            val isCorrectAnswer = option == currentQuestion.correctAnswer
            val isSelected = option == selectedOption
            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = when {
                    quizState.showFeedback && isCorrectAnswer -> Color.Green.copy(alpha = 0.7f)
                    quizState.showFeedback && isSelected && !isCorrectAnswer -> Color.Red.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = when {
                    quizState.showFeedback && (isCorrectAnswer || (isSelected && !isCorrectAnswer)) -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Button(
                onClick = { if (!quizState.showFeedback) { selectedOption = option; onAnswerSelected(option) } },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = buttonColors,
                enabled = !quizState.showFeedback || isSelected || isCorrectAnswer
            ) {
                Text(
                    text = option, fontSize = 18.sp, textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(visible = quizState.showFeedback) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val feedbackText = if (quizState.lastAnswerWasCorrect == true) "Correct !" else "Incorrect."
                val feedbackColor = if (quizState.lastAnswerWasCorrect == true) Color(0xFF008000) else MaterialTheme.colorScheme.error
                Text(
                    text = feedbackText, color = feedbackColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNextQuestion) {
                    Text(if (quizState.currentQuestionIndex < quizState.questions.size - 1) "Question Suivante" else "Voir Résultats")
                }
            }
        }
    }
}

@Composable
fun QuizResultDialog(
    score: Int,
    totalQuestions: Int,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quiz Terminé !") },
        text = {
            Text(
                "Votre score est de $score / $totalQuestions.",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = { Button(onClick = onPlayAgain) { Text("Rejouer") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Retour") } }
    )
}

// --- Top App Bar and Controls ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumbersTopAppBar(
    navController: NavController,
    currentMode: ScreenMode,
    onToggleMode: () -> Unit,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    displayMode: DisplayMode,
    onDisplayModeChanged: (DisplayMode) -> Unit,
    autoPlaySpeed: Float,
    onSpeedChanged: (Float) -> Unit,
    onInfoClick: () -> Unit,
    isPlayPauseEnabled: Boolean,
    isSpeedControlEnabled: Boolean,
    isDisplayModeEnabled: Boolean
) {
    val displayModeOptions = DisplayMode.values()
    var showSpeedDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(stringResource(R.string.numbers_in_adlam), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer) },
        navigationIcon = { IconButton(onClick = { navController.navigateUp() }) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onPrimaryContainer) } },
        actions = {
            IconButton(onClick = onToggleMode) {
                Icon(painter = painterResource(id = if (currentMode == ScreenMode.LEARNING) R.drawable.quiz else R.drawable.writing), contentDescription = if (currentMode == ScreenMode.LEARNING) "Passer au Mode Quiz" else "Passer au Mode Apprentissage", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            AnimatedVisibility(visible = currentMode == ScreenMode.LEARNING) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SpeedControlButton(autoPlaySpeed, { showSpeedDialog = true }, isSpeedControlEnabled)
                    Spacer(Modifier.width(4.dp))
                    ToggleButton(displayModeOptions, displayMode, onDisplayModeChanged, { mode -> when (mode) { DisplayMode.ADLAM -> "𞥐𞥑"; DisplayMode.LATIN -> "01"; DisplayMode.FULFULDE -> "Ff" } }, enabled = isDisplayModeEnabled)
                    Spacer(Modifier.width(4.dp))
                    PlayPauseButton(isPlaying, onPlayPauseClick, isPlayPauseEnabled)
                }
            }
            IconButton(onClick = onInfoClick) { Icon(Icons.Default.Info, contentDescription = stringResource(R.string.info), tint = MaterialTheme.colorScheme.onPrimaryContainer) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer, titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer, actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer, navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer)
    )

    if (showSpeedDialog) {
        SpeedSelectionDialog(autoPlaySpeed, { onSpeedChanged(it); showSpeedDialog = false }, { showSpeedDialog = false })
    }
}

@Composable
fun SpeedControlButton(autoPlaySpeed: Float, onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(onClick = onClick, modifier = Modifier.padding(horizontal = 4.dp), enabled = enabled) {
        Text(text = "${autoPlaySpeed}x", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (enabled) 1f else 0.5f))
    }
}

@Composable
fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit, enabled: Boolean = true) {
    val targetColor = if (isPlaying) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    val animatedColor by animateColorAsState(targetValue = targetColor.copy(alpha = if (enabled) 1f else 0.5f))
    IconButton(onClick = onClick, modifier = Modifier.padding(horizontal = 4.dp).size(40.dp).clip(CircleShape).background(animatedColor), enabled = enabled) {
        Icon(painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play), contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play), tint = if (isPlaying) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary, modifier = Modifier.scale(1.1f))
    }
}

@Composable
fun ToggleButton(options: Array<DisplayMode>, selectedOption: DisplayMode, onOptionSelected: (DisplayMode) -> Unit, getLabel: (DisplayMode) -> String, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val selectedColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val selectedTextColor = MaterialTheme.colorScheme.onPrimary
    Row(modifier = modifier.height(36.dp).clip(RoundedCornerShape(18.dp)).background(backgroundColor).alpha(if (enabled) 1f else 0.5f)) {
        options.forEachIndexed { index, option ->
            val isSelected = option == selectedOption
            val startPercent = if (index == 0) 50 else 0; val endPercent = if (index == options.size - 1) 50 else 0
            val shape = RoundedCornerShape(topStartPercent = startPercent, bottomStartPercent = startPercent, topEndPercent = endPercent, bottomEndPercent = endPercent)
            ToggleOption(getLabel(option), isSelected, shape, selectedColor, if (isSelected) selectedTextColor else textColor, { if (enabled) onOptionSelected(option) })
        }
    }
}

@Composable
fun ToggleOption(label: String, isSelected: Boolean, shape: RoundedCornerShape, selectedColor: Color, textColor: Color, onClick: () -> Unit) {
    val animatedBgColor by animateColorAsState(if (isSelected) selectedColor else Color.Transparent)
    Box(modifier = Modifier.widthIn(min = 40.dp).padding(horizontal = 4.dp).fillMaxHeight().clip(shape).background(animatedBgColor).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text = label, color = textColor, style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal), modifier = Modifier.padding(horizontal = 8.dp))
    }
}

// --- Dialogs ---

@Composable
fun SpeedSelectionDialog(currentSpeed: Float, onSpeedSelected: (Float) -> Unit, onDismiss: () -> Unit) {
    val speedOptions = listOf(0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vitesse de Lecture Auto") },
        text = { Column { speedOptions.forEach { speed -> SpeedOption(speed, speed == currentSpeed) { onSpeedSelected(speed) } } } },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun SpeedOption(speed: Float, isSelected: Boolean, onSelected: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onSelected).padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = isSelected, onClick = onSelected)
        Text(text = "${speed}x", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("À propos des chiffres Adlam") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Adlam est une écriture créée dans les années 1980 par deux adolescents, Ibrahima et Abdoulaye Barry, pour écrire la langue Peule (Fulfulde).", style = MaterialTheme.typography.bodyMedium)
                Text("Le système de numération suit le même schéma décimal que les chiffres arabes, mais avec des symboles uniques.", style = MaterialTheme.typography.bodyMedium)
                Text("Cette application vous aide à apprendre les chiffres de 0 à 19.", style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fermer") } }
    )
}

// --- Learning Mode UI Components ---

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCurrentNumber(currentItem: NumberItem, displayMode: DisplayMode, modifier: Modifier = Modifier) {
    val currentText = when (displayMode) {
        DisplayMode.ADLAM -> currentItem.adlamDigit
        DisplayMode.LATIN -> currentItem.latinDigit
        DisplayMode.FULFULDE -> currentItem.fulfuldeAdlam
    }
    val secondaryText = when (displayMode) {
        DisplayMode.ADLAM -> currentItem.fulfuldeLatin
        DisplayMode.LATIN -> currentItem.fulfuldeLatin
        DisplayMode.FULFULDE -> currentItem.adlamDigit // Show Adlam digit as secondary in Fulfulde mode
    }
    val showSecondaryText = secondaryText.isNotBlank()
    val fontSize = when {
        displayMode == DisplayMode.FULFULDE && currentText.length > 10 -> 36.sp
        displayMode == DisplayMode.FULFULDE -> 42.sp
        (displayMode == DisplayMode.ADLAM || displayMode == DisplayMode.LATIN) && currentText.length > 3 -> 60.sp
        else -> 72.sp
    }

    Box(modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            AnimatedContent(targetState = currentText, transitionSpec = { (slideInVertically { h -> h } + fadeIn()) togetherWith (slideOutVertically { h -> -h } + fadeOut()) using SizeTransform(clip = false) }) { text ->
                Text(text, fontSize = fontSize, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2, softWrap = true)
            }
            AnimatedVisibility(visible = showSecondaryText) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text(secondaryText, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun NumbersGrid(numberItems: List<NumberItem>, currentNumberIndex: Int, isPlaying: Boolean, displayMode: DisplayMode, onItemClick: (Int) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 100.dp), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        items(numberItems.size) { index ->
            NumberCard(numberItems[index], (index == currentNumberIndex), isPlaying && (index == currentNumberIndex), displayMode) { onItemClick(index) }
        }
    }
}

@Composable
fun NumberCard(item: NumberItem, isCurrent: Boolean, isAutoPlaying: Boolean, displayMode: DisplayMode, onClick: () -> Unit) {
    val animatedScale by animateFloatAsState(if (isCurrent) 1.05f else 1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
    val animatedElevation by animateDpAsState(if (isCurrent) 6.dp else 2.dp, tween(300))
    val pulseAlpha = animatePulseEffect(isAutoPlaying)
    val backgroundColor = when { isAutoPlaying -> MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha); isCurrent -> MaterialTheme.colorScheme.primaryContainer; else -> MaterialTheme.colorScheme.surfaceVariant }
    val textColor = when { isAutoPlaying -> MaterialTheme.colorScheme.onPrimary; isCurrent -> MaterialTheme.colorScheme.onPrimaryContainer; else -> MaterialTheme.colorScheme.onSurfaceVariant }
    val border = if (isCurrent && !isAutoPlaying) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null

    Card(modifier = Modifier.aspectRatio(1f).scale(animatedScale).shadow(animatedElevation, RoundedCornerShape(16.dp), clip = false).clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = backgroundColor), shape = RoundedCornerShape(16.dp), border = border) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                val cardText = when (displayMode) { DisplayMode.ADLAM -> item.adlamDigit; DisplayMode.LATIN -> item.latinDigit; DisplayMode.FULFULDE -> item.fulfuldeAdlam }
                val hintText = when (displayMode) { DisplayMode.ADLAM -> item.latinDigit; DisplayMode.LATIN -> item.adlamDigit; DisplayMode.FULFULDE -> item.adlamDigit }
                val showHint = hintText.isNotBlank()
                val fontSize = when { displayMode == DisplayMode.FULFULDE && cardText.length > 8 -> 16.sp; displayMode == DisplayMode.FULFULDE -> 20.sp; (displayMode == DisplayMode.ADLAM || displayMode == DisplayMode.LATIN) && cardText.length > 2 -> 30.sp; else -> 36.sp }

                Text(cardText, fontSize = fontSize, fontWeight = FontWeight.Bold, color = textColor, textAlign = TextAlign.Center, maxLines = 2, softWrap = true)
                AnimatedVisibility(visible = showHint) {
                    Column {
                        Spacer(Modifier.height(4.dp))
                        Text(hintText, fontSize = 12.sp, color = textColor.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun animatePulseEffect(isActive: Boolean): Float {
    if (!isActive) return 1f
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(0.7f, 1.0f, infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse), label = "pulse alpha")
    return pulseAlpha
}

// --- Audio Playback and Autoplay ---

/**
 * Gère la lecture automatique en mode Apprentissage.
 */
@Composable
fun AutoPlayHandler(
    isPlaying: Boolean,
    currentNumberIndex: Int,
    numberItems: List<NumberItem>,
    context: Context,
    autoPlaySpeed: Float,
    updateIndex: (Int) -> Unit,
    playSoundFn: (NumberItem) -> Unit
) {
    LaunchedEffect(isPlaying, autoPlaySpeed) {
        if (isPlaying && numberItems.isNotEmpty()) {
            var internalIndex = currentNumberIndex
            while (isActive && isPlaying) {
                if (internalIndex in numberItems.indices) {
                    val itemToPlay = numberItems[internalIndex]
                    playSoundFn(itemToPlay)
                } else {
                    Log.w("AutoPlayHandler", "Invalid internalIndex: $internalIndex"); break
                }
                val baseDelay = 1500L
                val adjustedDelay = (baseDelay / autoPlaySpeed).toLong().coerceAtLeast(200L)
                delay(adjustedDelay)
                if (isActive && isPlaying) {
                    internalIndex = (internalIndex + 1) % numberItems.size
                    updateIndex(internalIndex)
                }
            }
        } else {
            // No longer need to stop/reset MediaPlayer here as it's managed by ViewModel
        }
    }
}

// --- Preview ---

@Preview(name = "Light Mode - Learning", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode - Learning", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewNumbersScreenLearning() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme { NumbersScreen(navController) }
}

@Preview(name = "Light Mode - Quiz", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewNumbersScreenQuiz() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme {
        // For preview, we can provide a dummy ViewModel or mock its dependencies
        val dummyViewModel = NumbersViewModel() // Using default constructor for simplicity in preview
        Scaffold( topBar = { /* Mock TopAppBar if needed */ } ) { padding ->
            val quizState by dummyViewModel.quizState.collectAsState()
            if (quizState != null && quizState!!.questions.isNotEmpty()) {
                QuizContent(quizState!!.copy(showFeedback = false), padding, {}, {})
            } else {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center){ Text("Could not generate quiz preview.")}
            }
        }
    }
}