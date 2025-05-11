package com.bekisma.adlamfulfulde.screens

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons // Importation nécessaire
import androidx.compose.material.icons.filled.GraphicEq // Importation nécessaire
import androidx.compose.material.icons.filled.PlayArrow // Importation nécessaire
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
import androidx.compose.ui.platform.LocalView
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
import com.bekisma.adlamfulfulde.ads.BannerAdView // Assurez-vous que cette importation est correcte
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme // Assurez-vous que cette importation est correcte
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
    "𞤀" to listOf("𞤢𞤲𞥋𞤣𞤢𞤤"), // Example word for A
    "𞤁" to listOf("𞤁𞤫𞤦𞥆𞤮"), // Example word for DA
    "𞤂" to listOf("𞤂𞤫𞤱𞤪𞤵"), // Example word for LA
    "𞤃" to listOf("𞤃𞤢𞤱𞤲𞤣𞤫"), // Example word for MA
    "𞤄" to listOf("𞤄𞤢𞥄𞤤𞤭"), // Example word for BA
    "𞤅" to listOf("𞤅𞤵𞥅𞤣𞤵"), // Example word for SA
    "𞤆" to listOf("𞤆𞤵𞤷𞥆𞤵"), // Example word for PA
    "𞤇" to listOf("𞤇𞤢𞥄𞤱𞤮"), // Example word for ƁA
    "𞤈" to listOf("𞤈𞤫𞤱𞤩𞤫"), // Example word for RA
    "𞤉" to listOf("𞤉𞤺𞥆𞤮𞤤"), // Example word for E
    "𞤊" to listOf("𞤊𞤮𞤱𞤪𞤵"), // Example word for FA
    "𞤋" to listOf("𞤋𞤲𞥋𞤣𞤫𥅱𞤪𞤭"), // Example word for I
    "𞤌" to listOf("𞤌𥅱𞤤𞤣𞤭"), // Example word for O
    "𞤍" to listOf("𞤍𞤢𞤯𞤭"), // Example word for ƊA
    "𞤎" to listOf("𞤎𞤢𞤥𞤮𥅱𞤱𞤤"), // Example word for ƳA
    "𞤏" to listOf("𞤏𞤢𥄲𞤲𞤣𞤵"), // Example word for WA
    "𞤐" to listOf("𞤐𞤢𞤺𞥆𞤫"), // Example word for NA
    "𞤑" to listOf("𞤑𞤵𞤪𞤮𞤤⁏"), // Example word for KA
    "𞤒" to listOf("𞤒𞤢𥄲𞤪𞤫"), // Example word for YA
    "𞤓" to listOf("𞤓𞤲𞤮𞤪𞤣𞤵"), // Example word for U
    "𞤔" to listOf("𞤔𞤢𞤲𞥋𞤺𞤮"), // Example word for JA
    "𞤕" to listOf("𞤕𞤭𞤪𞤺𞤵"), // Example word for CA
    "𞤖" to listOf("𞤖𞤮𞤪𞤣𞤫"), // Example word for HA
    "𞤗" to listOf("𞤗𞤮𞤴𞤭𥅱𞤪"), // Example word for QA
    "𞤘" to listOf("𞤘𞤮𞤪𞤳𞤮"), // Example word for GA
    "𞤙" to listOf("𞤙𞤢𥄲𞤳𞤵"), // Example word for ÑA
    "𞤚" to listOf("𞤚𞤢𞤼𞤭"), // Example word for TA
    "𞤛" to listOf("𞤛𞤢𞤪𞤮𞤤"), // Example word for NHA
    "𞤐𞤁" to listOf("𞤐𞤣𞤢𥄲𞤥𞤲𞤣𞤭"), // Example word for NDA
    "𞤐𞤄" to listOf("𞤐𞤄𞤫𥅱𞤱𞤢"), // Example word for MBA
    "𞤐𞤔" to listOf("𞤐𞤔𞤵𞤥𞤪𞤭"), // Example word for NJA
    "𞤐𞤘" to listOf("𞤐𞤺𞤢𥄲𞤲𞤣𞤭")  // Example word for NGA
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
    "𞤛" to R.raw.adlam28_1,
    // Ces sons pourraient être gérés différemment ou avoir leurs propres enregistrements
    "𞤐𞤁" to R.raw.son_nul,
    "𞤐𞤄" to R.raw.son_nul,
    "𞤐𞤔" to R.raw.son_nul,
    "𞤐𞤘" to R.raw.son_nul
)

// --- Nouvelle Map pour l'audio des mots d'exemple ---
// IMPORTANT: Les entrées R.raw.son_nul sont des placeholders.
// TODO: Remplacer R.raw.son_nul par les identifiants des vrais fichiers audio (.mp3/.wav)
//       pour chaque mot d'exemple dans res/raw/ lors d'une prochaine mise à jour.
val exampleWordAudioMap = mapOf(
    "𞤢𞤲𞥋𞤣𞤢𞤤" to R.raw.son_nul, // Example word: andal
    "𞤁𞤫𞤦𞥆𞤮" to R.raw.son_nul, // Example word: Debbo
    "𞤂𞤫𞤱𞤪𞤵" to R.raw.son_nul, // Example word: Lewruru
    "𞤃𞤢𞤱𞤲𞤣𞤫" to R.raw.son_nul, // Example word: Mawnde
    "𞤄𞤢𞥄𞤤𞤭" to R.raw.son_nul, // Example word: Baali
    "𞤅𞤵𞥅𞤣𞤵" to R.raw.son_nul, // Example word: Suudu
    "𞤆𞤵𞤷𞥆𞤵" to R.raw.son_nul, // Example word: Puccu
    "𞤇𞤢𞥄𞤱𞤮" to R.raw.son_nul, // Example word: Bbaawwo
    "𞤈𞤫𞤱𞤩𞤫" to R.raw.son_nul, // Example word: Rewbbe
    "𞤉" to R.raw.son_nul, // Example word: Eggol
    "𞤊𞤮𞤱𞤪𞤵" to R.raw.son_nul, // Example word: Fowru
    "𞤋𞤲𞥋𞤣𞤫𥅱𞤪𞤭" to R.raw.son_nul, // Example word: Indeeri
    "𞤌𥅱𞤤𞤣𞤭" to R.raw.son_nul, // Example word: Ooldi
    "𞤍𞤢𞤯𞤭" to R.raw.son_nul, // Example word: Dadhi
    "𞤎𞤢𞤥𞤮𥅱𞤱𞤤" to R.raw.son_nul, // Example word: Yyamooŵl
    "𞤏𞤢𥄲𞤲𞤣𞤵" to R.raw.son_nul, // Example word: Waandu
    "𞤐𞤢𞤺𞥆𞤫" to R.raw.son_nul, // Example word: Nagge
    "𞤑𞤵𞤪𞤮𞤤⁏" to R.raw.son_nul, // Example word: Kurol
    "𞤒𞤢𥄲𞤪𞤫" to R.raw.son_nul, // Example word: Yyaare
    "𞤓𞤲𞤮𞤪𞤣𞤵" to R.raw.son_nul, // Example word: Unordu
    "𞤔𞤢𞤲𞥋𞤺𞤮" to R.raw.son_nul, // Example word: Jaango
    "𞤕𞤭𞤪𞤺𞤵" to R.raw.son_nul, // Example word: Cirgu
    "𞤖𞤮𞤪𞤣𞤫" to R.raw.son_nul, // Example word: Hornde
    "𞤗𞤮𞤴𞤭𥅱𞤪" to R.raw.son_nul, // Example word: Qoyiyr
    "𞤘𞤮𞤪𞤳𞤮" to R.raw.son_nul, // Example word: Gorko
    "𞤙𞤢𥄲𞤳𞤵" to R.raw.son_nul, // Example word: Nnyaaku
    "𞤚𞤢𤼭" to R.raw.son_nul, // Example word: Tati (Note: This was "𞤚𞤢𞤼𞤭", corrected typo here)
    "𞤛𞤢𞤪𞤮𞤤" to R.raw.son_nul, // Example word: Nharol
    "𞤐𞤣𞤢𥄲𞤥𞤲𞤣𞤭" to R.raw.son_nul, // Example word: Ndaamndi
    "𞤐𞤄𞤫𥅱𞤱𞤢" to R.raw.son_nul, // Example word: Nbeewa
    "𞤐𞤔𞤵𞤥𞤪𞤭" to R.raw.son_nul, // Example word: Nujumri
    "𞤐𞤘𞤢𥄲𞤲𞤣𞤭" to R.raw.son_nul // Example word: Ngayndi
    // Ajoutez d'autres mots d'exemple et leurs ressources audio ici (initialement avec son_nul)
)


// --- MediaPlayer Singleton amélioré avec callback ---
object MediaPlayerSingleton {
    private var mediaPlayer: MediaPlayer? = null
    private var completionListener: (() -> Unit)? = null

    fun playSound(context: Context, soundResourceId: Int, onComplete: () -> Unit = {}) {
        // Libérer l'ancien lecteur avant de créer un nouveau
        releasePlayer()
        completionListener = onComplete // Stocker le callback

        mediaPlayer = try {
            // Vérifier si la ressource existe et est valide
            if (soundResourceId == 0) { // R.raw.son_nul pourrait être 0 si non défini ou si un autre problème survient.
                // Une meilleure vérification pourrait être nécessaire si son_nul est un fichier vide mais valide.
                // Pour l'instant, on suppose son_nul est traité comme "pas de son".
                null // Traiter comme un échec si c'est son_nul
            } else {
                MediaPlayer.create(context, soundResourceId)?.apply {
                    setOnCompletionListener { mp ->
                        mp.release()
                        completionListener?.invoke() // Appeler le callback de fin
                        completionListener = null
                        mediaPlayer = null // S'assurer que la référence est nulle après libération
                    }
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Gérer l'échec de création
        }

        // Si la création a échoué (y compris si c'était son_nul), appeler immédiatement le callback
        if (mediaPlayer == null) {
            completionListener?.invoke()
            completionListener = null
        }
    }

    fun stopSound() {
        releasePlayer()
        completionListener?.invoke() // Appeler le callback même si arrêté prématurément
        completionListener = null
    }

    private fun releasePlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}

// --- Fonctions utilitaires pour jouer les sons ---
fun playSoundForLetter(context: Context, letter: String, onComplete: () -> Unit = {}) {
    adlamAudioMap[letter]?.let { soundResourceId ->
        MediaPlayerSingleton.playSound(context, soundResourceId, onComplete)
    } ?: onComplete() // Appeler complete immédiatement si aucune ressource sonore
}

fun playSoundForExampleWord(context: Context, word: String, onComplete: () -> Unit = {}) {
    // Utilise la map des mots d'exemple avec son_nul comme placeholder
    exampleWordAudioMap[word]?.let { soundResourceId ->
        MediaPlayerSingleton.playSound(context, soundResourceId, onComplete)
    } ?: onComplete() // Appeler complete immédiatement si aucune ressource sonore (ou si son_nul = 0)
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DetailAlphabetScreen(
    letter: String,
    navController: NavController,
    alphabetList: List<String> = adlamAlphabet
) {
    var currentIndex by remember { mutableStateOf(alphabetList.indexOf(letter).coerceAtLeast(0)) } // Ensure index is valid
    val context = LocalContext.current
    val view = LocalView.current // Pour le feedback haptique
    var showDialog by remember { mutableStateOf(false) }

    // État pour suivre si l'audio de la lettre est en cours de lecture
    var isPlayingLetterAudio by remember { mutableStateOf(false) }

    // Jouer le son de la lettre courante quand l'index change
    LaunchedEffect(key1 = currentIndex) {
        // Arrêter tout son en cours avant de jouer le nouveau
        MediaPlayerSingleton.stopSound()
        isPlayingLetterAudio = true
        playSoundForLetter(context, alphabetList[currentIndex]) {
            isPlayingLetterAudio = false // Mettre à jour l'état à la fin de la lecture
        }
    }

    Scaffold(
        topBar = { DetailTopBar(navController, onInfoClick = { showDialog = true }) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp) // Laisser de l'espace pour la bannière
                        .align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Indicateur de position ---
                    Text(
                        text = "${currentIndex + 1} / ${alphabetList.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))


                    // --- Contenu principal (Lettre + Exemples) avec swipe ---
                    Box(
                        modifier = Modifier
                            .weight(1f) // Prend l'espace restant
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { change, dragAmount ->
                                    change.consume()
                                    val newIndex = when {
                                        dragAmount > 0 && currentIndex > 0 -> currentIndex - 1
                                        dragAmount < 0 && currentIndex < alphabetList.size - 1 -> currentIndex + 1
                                        else -> currentIndex // Pas de changement ou limite atteinte
                                    }

                                    if (newIndex != currentIndex) {
                                        // Feedback haptique
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        // Arrêter le son précédent et mettre à jour l'index
                                        MediaPlayerSingleton.stopSound()
                                        currentIndex = newIndex
                                        // Le LaunchedEffect gérera la lecture du son pour le nouvel index
                                    }
                                }
                            }
                    ) {
                        // --- Animation de transition entre les caractères ---
                        AnimatedContent(
                            targetState = currentIndex,
                            transitionSpec = {
                                // Détermine la direction de l'animation basée sur le changement d'index
                                if (targetState > initialState) {
                                    slideInHorizontally { width -> width } + fadeIn() with
                                            slideOutHorizontally { width -> -width } + fadeOut()
                                } else {
                                    slideInHorizontally { width -> -width } + fadeIn() with
                                            slideOutHorizontally { width -> width } + fadeOut()
                                }.using(SizeTransform(clip = false)) // Important pour que le texte ne soit pas coupé pendant la transition
                            }
                        ) { targetIndex ->
                            // --- Contenu de chaque "page" de caractère ---
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()), // Permet le défilement si le contenu est trop long
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp) // Espacement entre les éléments
                            ) {
                                // --- Affichage de la Lettre + Bouton Play ---
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp) // Padding autour de la ligne
                                ) {
                                    DisplayLetters(alphabetList, targetIndex) // Affiche les caractères
                                    Spacer(modifier = Modifier.width(8.dp)) // Espace entre lettre et bouton

                                    // Bouton Play dédié pour la lettre
                                    IconButton(
                                        onClick = {
                                            MediaPlayerSingleton.stopSound() // Arrête tout son en cours
                                            isPlayingLetterAudio = true // Indique que la lecture commence
                                            playSoundForLetter(context, alphabetList[targetIndex]) {
                                                isPlayingLetterAudio = false // Indique que la lecture est terminée
                                            }
                                        },
                                        modifier = Modifier.size(48.dp) // Taille du bouton
                                    ) {
                                        // --- Utilisation des icônes Material Design STANDARD ---
                                        Icon(
                                            imageVector = if (isPlayingLetterAudio) Icons.Default.GraphicEq // Icône quand l'audio joue (barres d'égaliseur)
                                            else Icons.Default.PlayArrow, // Icône quand l'audio ne joue pas (flèche play)
                                            contentDescription = stringResource(R.string.play_sound), // Utiliser votre string resource
                                            tint = MaterialTheme.colorScheme.primary // Couleur du bouton
                                        )
                                        // --- Fin de l'utilisation des icônes Material Design ---
                                    }
                                }

                                // --- Affichage des Exemples ---
                                DisplayExamples(targetIndex, alphabetList, context) // Passez le contexte aux exemples
                                Spacer(modifier = Modifier.height(16.dp)) // Espacement avant les boutons de navigation (si le scroll ne gère pas ça)
                            }
                        }
                    }

                    // --- Boutons de Navigation ---
                    NavigationButtons(currentIndex, alphabetList.size) { newIndex ->
                        // Feedback haptique
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        // Arrêter le son avant de changer de lettre
                        MediaPlayerSingleton.stopSound()
                        currentIndex = newIndex
                        // Le LaunchedEffect gérera la lecture du son pour le nouvel index
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- Bannière publicitaire alignée en bas ---
                BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    )

    // --- Boîte de dialogue des règles de lecture ---
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
            IconButton(onClick = {
                // Optionnel: arrêter le son en cours quand on quitte l'écran
                MediaPlayerSingleton.stopSound()
                navController.navigateUp()
            }) {
                Icon(
                    // Garde cette icône si elle est un drawable personnalisé que vous avez
                    // Sinon, utilisez Icons.Default.ArrowBack ou similaire
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    // Garde cette icône si elle est un drawable personnalisé que vous avez
                    // Sinon, utilisez Icons.Default.Info ou similaire
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
    // Gérer les caractères combinés qui n'ont pas de version minuscule simple dans le map
    val lowerCaseLetter = when (currentLetter) {
        "𞤐𞤁" -> "𞤲𞤣"
        "𞤐𞤄" -> "𞤲𞤦"
        "𞤐𞤔" -> "𞤲𞤶"
        "𞤐𞤘" -> "𞤲𞤺"
        else -> currentLetter.lowercase(Locale.ROOT)
    }
    val latinEquivalent = adlamToLatinMap[currentLetter] ?: "?"

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp),
//        modifier = Modifier.weight(1f) // Permet au Card de prendre l'espace restant dans le Row
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

@Composable
fun DisplayExamples(currentIndex: Int, alphabetList: List<String>, context: Context) {
    val currentLetter = alphabetList[currentIndex]
    val vowels = listOf("𞤀", "𞤉", "𞤋", "𞤌", "𞤓") // Liste des voyelles Adlam majuscules
    val lowercaseVowels = vowels.map { it.lowercase(Locale.ROOT) } // Convertir en minuscules

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
                            // Mettre en surbrillance le caractère principal (majuscule ou minuscule)
                            char.toString() == currentLetter || char.toString() == currentLetter.lowercase(Locale.ROOT) -> {
                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                    append(char)
                                }
                            }
                            // Mettre en surbrillance les voyelles (majuscules ou minuscules)
                            char.toString() in vowels || char.toString() in lowercaseVowels -> {
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
                // --- Ligne pour le mot d'exemple et son bouton Play ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween, // Espace entre texte et bouton
                    modifier = Modifier.fillMaxWidth() // Remplir la largeur disponible
                ) {
                    Text(
                        text = formattedExample,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f) // Permet au texte de prendre le maximum d'espace
                            .wrapContentWidth(Alignment.Start), // Aligner le texte à gauche si l'espace le permet
                        fontSize = 30.sp
                    )
                    // Bouton Play pour le mot d'exemple
                    IconButton(
                        onClick = {
                            MediaPlayerSingleton.stopSound() // Arrête tout son en cours (lettre ou autre exemple)
                            playSoundForExampleWord(context, example)
                        },
                        modifier = Modifier.size(36.dp) // Taille du bouton
                    ) {
                        // --- Utilisation d'une icône Material Design STANDARD ---
                        Icon(
                            imageVector = Icons.Default.PlayArrow, // Icône standard pour jouer
                            contentDescription = stringResource(R.string.play_example), // Utiliser votre string resource
                            tint = MaterialTheme.colorScheme.secondary // Couleur du bouton (peut-être différente de l'icône de la lettre)
                        )
                        // --- Fin de l'utilisation de l'icône Material Design ---
                    }
                }
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
    val view = LocalView.current // Pour le feedback haptique

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacement entre les boutons
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = {
                if (currentIndex > 0) {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onLetterChange(currentIndex - 1)
                }
            },
            enabled = currentIndex > 0,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.previous))
        }
        Button(
            onClick = {
                if (currentIndex < alphabetSize - 1) {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
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
        text = {
            // Rendre le texte défilable si nécessaire
            Text(
                text = stringResource(R.string.reading_rules_text),
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        },
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