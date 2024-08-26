package com.bekisma.adlamfulfulde.screens

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import java.util.Locale

// ... (Garder les mappings et autres variables globales inchangés)
val adlamToLatinMap = mapOf(
    "𞤀" to "A", "𞤁" to "DA", "𞤂" to "LA", "𞤃" to "MA", "𞤄" to "BA", "𞤅" to "SA", "𞤆" to "PA",
    "𞤇" to "ƁA", "𞤈" to "RA", "𞤉" to "E", "𞤊" to "FA", "𞤋" to "I", "𞤌" to "O", "𞤍" to "ƊA", "𞤎" to "ƳA",
    "𞤏" to "WA", "𞤐" to "NA", "𞤑" to "KA", "𞤒" to "YA", "𞤓" to "U", "𞤔" to "JA", "𞤕" to "CA", "𞤖" to "HA",
    "𞤗" to "QA", "𞤘" to "GA", "𞤙" to "ÑA", "𞤚" to "TA", "𞤛" to "NHA",
    "𞤐𞤁" to "NDA", "𞤐𞤄" to "MBA", "𞤐𞤔" to "NJA", "𞤐𞤘" to "NGA"
)
val adlamAlphabet = listOf(
    "𞤀", "𞤁", "𞤂", "𞤃", "𞤄", "𞤅", "𞤆",
    "𞤇", "𞤈", "𞤉", "𞤊", "𞤋", "𞤌", "𞤍", "𞤎",
    "𞤏", "𞤐", "𞤑", "𞤒", "𞤓", "𞤔", "𞤕", "𞤖",
    "𞤗", "𞤘", "𞤙", "𞤚", "𞤛",
    "𞤐𞤁", "𞤐𞤄", "𞤐𞤔", "𞤐𞤘"
)
val exampleWordsMap = mapOf(
    "𞤀" to listOf("𞤢𞤲𞥋𞤣𞤢𞤤"), "𞤁" to listOf("𞤁𞤫𞤦𞥆𞤮"), "𞤂" to listOf("𞤂𞤫𞤱𞤪𞤵"),
    "𞤃" to listOf("𞤃𞤢𞤱𞤲𞤣𞤫"), "𞤄" to listOf("𞤄𞤢𞥄𞤤𞤭"), "𞤅" to listOf("𞤅𞤵𞥅𞤣𞤵"),
    "𞤆" to listOf("𞤆𞤵𞤷𞥆𞤵"), "𞤇" to listOf("𞤇𞤢𞥄𞤱𞤮"), "𞤈" to listOf("𞤈𞤫𞤱𞤩𞤫"),
    "𞤉" to listOf("𞤉𞤺𞥆𞤮𞤤"), "𞤊" to listOf("𞤊𞤮𞤱𞤪𞤵"), "𞤋" to listOf("𞤋𞤲𞥋𞤣𞤫𞥅𞤪𞤭"),
    "𞤌" to listOf("𞤌𞥅𞤤𞤣𞤭"), "𞤍" to listOf("𞤍𞤢𞤯𞤭"), "𞤎" to listOf("𞤎𞤢𞤥𞤮𞥅𞤱𞤤"),
    "𞤏" to listOf("𞤏𞤢𞥄𞤲𞤣𞤵"), "𞤐" to listOf("𞤐𞤢𞤺𞥆𞤫"), "𞤑" to listOf("𞤑𞤵𞤪𞤮𞤤⁏"),
    "𞤒" to listOf("𞤒𞤢𞥄𞤪𞤫"), "𞤓" to listOf("𞤓𞤲𞤮𞤪𞤣𞤵"), "𞤔" to listOf("𞤔𞤢𞤲𞥋𞤺𞤮"),
    "𞤕" to listOf("𞤕𞤭𞤪𞤺𞤵"), "𞤖" to listOf("𞤖𞤮𞤪𞤣𞤫"), "𞤗" to listOf("𞤗𞤮𞤴𞤭𞥅𞤪"),
    "𞤘" to listOf("𞤘𞤮𞤪𞤳𞤮"), "𞤙" to listOf("𞤙𞤢𞥄𞤳𞤵"), "𞤚" to listOf("𞤚𞤢𞤼𞤭"),
    "𞤛" to listOf("𞤛𞤢𞤪𞤮𞤤"), "𞤐𞤁" to listOf("𞤐𞤣𞤢𞥄𞤥𞤲𞤣𞤭"), "𞤐𞤄" to listOf("𞤐𞤄𞤫𞥅𞤱𞤢"),
    "𞤐𞤔" to listOf("𞤐𞤔𞤵𞤥𞤪𞤭"), "𞤐𞤘" to listOf("𞤐𞤺𞤢𞥄𞤲𞤣𞤭")
)
val adlamAudioMap = mapOf(
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

object MediaPlayerSingleton {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(context: Context, soundResourceId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, soundResourceId).apply {
            start()
            setOnCompletionListener { mp -> mp.release() }
        }
    }
}

fun playSoundForLetter(context: Context, letter: String) {
    adlamAudioMap[letter]?.let { soundResourceId ->
        MediaPlayerSingleton.playSound(context, soundResourceId)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetailAlphabetScreen(
    letter: String,
    navController: NavController,
    alphabetList: List<String> = adlamAlphabet
) {
    var currentIndex by remember { mutableStateOf(alphabetList.indexOf(letter)) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val transitionState = remember { MutableTransitionState(currentIndex) }

    LaunchedEffect(key1 = currentIndex) {
        playSoundForLetter(context, alphabetList[currentIndex])
    }

    Scaffold(
        topBar = { DetailTopBar(navController, onInfoClick = { showDialog = true }) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                BannerAdView()
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                change.consume()
                                when {
                                    dragAmount > 0 && currentIndex > 0 -> {
                                        transitionState.targetState = currentIndex - 1
                                        currentIndex--
                                    }
                                    dragAmount < 0 && currentIndex < alphabetList.size - 1 -> {
                                        transitionState.targetState = currentIndex + 1
                                        currentIndex++
                                    }
                                }
                                playSoundForLetter(context, alphabetList[currentIndex])
                            }
                        }
                ) {
                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } + fadeIn() with
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {
                                slideInHorizontally { width -> -width } + fadeIn() with
                                        slideOutHorizontally { width -> width } + fadeOut()
                            }.using(SizeTransform(clip = false))
                        }
                    ) { targetIndex ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DisplayLetters(alphabetList, targetIndex)
                            Spacer(modifier = Modifier.height(24.dp))
                            DisplayExamples(targetIndex, alphabetList)
                        }
                    }
                }

                NavigationButtons(currentIndex, alphabetList.size) { newIndex ->
                    transitionState.targetState = newIndex
                    currentIndex = newIndex
                    playSoundForLetter(context, alphabetList[currentIndex])
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )

    if (showDialog) {
        ReadingRulesDialog(onDismiss = { showDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(navController: NavController, onInfoClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.letter_details)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = stringResource(R.string.info)
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
fun DisplayLetters(alphabetList: List<String>, currentIndex: Int) {
    val currentLetter = alphabetList[currentIndex]
    val lowerCaseLetter = currentLetter.lowercase(Locale.ROOT)
    val latinEquivalent = adlamToLatinMap[currentLetter] ?: "?"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentLetter,
                        fontSize = 100.sp,
                        style = MaterialTheme.typography.displayLarge.copy(
                            shadow = Shadow(
                                color = Color.Gray,
                                offset = Offset(2f, 2f),
                                blurRadius = 8f
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = "-",
                        fontSize = 50.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Text(
                        text = lowerCaseLetter,
                        fontSize = 80.sp,
                        style = MaterialTheme.typography.displayLarge.copy(
                            shadow = Shadow(
                                color = Color.Gray,
                                offset = Offset(2f, 2f),
                                blurRadius = 8f
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = latinEquivalent,
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DisplayExamples(currentIndex: Int, alphabetList: List<String>) {
    val currentLetter = alphabetList[currentIndex]
    val vowels = listOf("𞤀", "𞤉", "𞤋", "𞤌", "𞤓")

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.examples),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            exampleWordsMap[currentLetter]?.forEach { example ->
                val formattedExample = buildAnnotatedString {
                    example.forEach { char ->
                        when {
                            char.toString() == currentLetter -> {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                    append(char)
                                }
                            }
                            char.toString() in vowels -> {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                                    append(char)
                                }
                            }
                            else -> {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                    append(char)
                                }
                            }
                        }
                    }
                }
                Text(
                    text = formattedExample,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(4.dp),
                    fontSize = 30.sp
                )
            }
        }
    }
}

@Composable
fun NavigationButtons(
    currentIndex: Int,
    alphabetSize: Int,
    onLetterChange: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = {
                if (currentIndex > 0) {
                    onLetterChange(currentIndex - 1)
                }
            },
            enabled = currentIndex > 0,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.previous))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (currentIndex < alphabetSize - 1) {
                    onLetterChange(currentIndex + 1)
                }
            },
            enabled = currentIndex < alphabetSize - 1,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.next))
        }
    }
}

@Composable
fun ReadingRulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reading_rules_title)) },
        text = { Text(stringResource(R.string.reading_rules_text)) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun DetailAlphabetScreenPreview() {
    val navController = rememberNavController()
    AdlamFulfuldeTheme {
        DetailAlphabetScreen(letter = adlamAlphabet.first(), navController = navController)
    }
}