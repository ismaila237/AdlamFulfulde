package com.bekisma.adlamfulfulde.screens // Ou ton package approprié

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Importe toutes les icônes filled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import kotlinx.coroutines.launch
import kotlin.math.pow

// --- Enum et Données ---

enum class WritingType {
    UPPERCASE,
    LOWERCASE,
    NUMBERS
}

object AdlamCharacters {
    val uppercase = listOf("𞤀", "𞤁", "𞤂", "𞤃", "𞤄", "𞤅", "𞤆", "𞤇", "𞤈", "𞤉", "𞤊", "𞤋", "𞤌", "𞤍", "𞤎", "𞤏", "𞤐", "𞤑", "𞤒", "𞤓", "𞤔", "𞤕", "𞤖", "𞤗", "𞤘", "𞤙", "𞤚", "𞤛")
    val lowercase = listOf("𞤢", "𞤣", "𞤤", "𞤥", "𞤦", "𞤧", "𞤨", "𞤩", "𞤪", "𞤫", "𞤬", "𞤭", "𞤮", "𞤯", "𞤰", "𞤱", "𞤲", "𞤳", "𞤴", "𞤵", "𞤶", "𞤷", "𞤸", "𞤹", "𞤺", "𞤻", "𞤼")
    val numbers = listOf("𞥐", "𞥑", "𞥒", "𞥓", "𞥔", "𞥕", "𞥖", "𞥗", "𞥘", "𞥙")
}

// --- Structures pour les Guides et Checkpoints ---

data class StrokeGuideSegment(
    val path: Path, // Chemin relatif (0f-1f)
    val startPoint: Offset? = null, // Point de départ relatif
    val isDash: Boolean = true
)

data class CharacterGuideData(
    val segments: List<StrokeGuideSegment>,
    val checkpoints: List<Offset> // Liste ORDONNÉE des checkpoints relatifs (0f-1f)
)

// !!! ATTENTION : VOUS DEVEZ REMPLIR CETTE MAP AVEC LES DONNÉES RÉELLES !!!
// Définissez les chemins et checkpoints pour CHAQUE caractère Adlam.
val adlamCharacterGuides: Map<String, CharacterGuideData> = mapOf(
    "𞤀" to CharacterGuideData(
        segments = listOf(
            StrokeGuideSegment(Path().apply { moveTo(0.3f, 0.8f); lineTo(0.3f, 0.2f); lineTo(0.7f, 0.2f); lineTo(0.7f, 0.8f) }, Offset(0.3f, 0.8f)),
            StrokeGuideSegment(Path().apply { moveTo(0.25f, 0.5f); lineTo(0.75f, 0.5f) }, Offset(0.25f, 0.5f))
        ),
        checkpoints = listOf(Offset(0.3f, 0.8f), Offset(0.3f, 0.2f), Offset(0.7f, 0.2f), Offset(0.7f, 0.8f), Offset(0.25f, 0.5f), Offset(0.75f, 0.5f))
    ),
    "𞥑" to CharacterGuideData(
        segments = listOf(StrokeGuideSegment(Path().apply { moveTo(0.5f, 0.2f); lineTo(0.5f, 0.8f) }, Offset(0.5f, 0.2f))),
        checkpoints = listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.8f))
    ),
    // ... AJOUTEZ TOUS LES AUTRES CARACTÈRES (MAJUSCULES, MINUSCULES, CHIFFRES) ICI ...
    // "𞤁" to CharacterGuideData(segments = [...], checkpoints = [...]),
    // "𞤢" to CharacterGuideData(segments = [...], checkpoints = [...]),
    // "𞥐" to CharacterGuideData(segments = [...], checkpoints = [...]),
)

// --- Styles pour le Guide ---
object GuideStyles {
    val GuideColor = Color.Gray.copy(alpha = 0.6f)
    val GuideStrokeWidth = 5f
    val StartIndicatorRadius = 10f
    val StartIndicatorColor = Color.Green.copy(alpha = 0.5f)
    val CheckpointVizRadius = 15f // Pour visualiser les checkpoints (optionnel)
    val CheckpointVizColor = Color.Red.copy(alpha = 0.4f)
    val DashedPathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
    val DashedStroke = Stroke(width = GuideStrokeWidth, pathEffect = DashedPathEffect, cap = StrokeCap.Round)
    val SolidStroke = Stroke(width = GuideStrokeWidth, cap = StrokeCap.Round)
}

// --- Composable Principal ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeScreen(
    navController: NavController,
    writingType: WritingType
) {
    val characters = when (writingType) {
        WritingType.UPPERCASE -> AdlamCharacters.uppercase
        WritingType.LOWERCASE -> AdlamCharacters.lowercase
        WritingType.NUMBERS -> AdlamCharacters.numbers
    }
    val topBarTitleRes = when (writingType) {
        WritingType.UPPERCASE -> R.string.writing_uppercase
        WritingType.LOWERCASE -> R.string.writing_lowercase
        WritingType.NUMBERS -> R.string.writing_numbers
    }
    val topBarTitle = stringResource(topBarTitleRes)

    // --- États ---
    var currentIndex by remember { mutableIntStateOf(0) }
    val paintColor = remember { mutableStateOf(Color.Black) }
    val brushSize = remember { mutableFloatStateOf(20f) }
    val brushAlpha = remember { mutableFloatStateOf(1f) }
    val paths = remember { mutableStateListOf<Pair<Path, PaintAttributes>>() }
    val currentPath = remember { mutableStateOf(Path()) }
    var showBrushSettingsDialog by remember { mutableStateOf(false) }
    var showStrokeGuide by remember { mutableStateOf(true) } // Guide affiché par défaut?
    // États pour l'évaluation
    var evaluationScore by remember { mutableFloatStateOf(-1f) } // -1f = non évalué
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var canvasSize by remember { mutableStateOf(Size.Zero) } // Pour obtenir la taille du canvas

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Ajout pour les messages de score
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    // Bouton pour afficher/masquer le guide (si défini pour le caractère)
                    val currentCharacter = characters.getOrNull(currentIndex)
                    if (currentCharacter != null && adlamCharacterGuides.containsKey(currentCharacter)) {
                        IconButton(onClick = { showStrokeGuide = !showStrokeGuide }) {
                            Icon(
                                imageVector = if (showStrokeGuide) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(if (showStrokeGuide) R.string.hide_guide else R.string.show_guide)
                            )
                        }
                    }
                    // Bouton pour les paramètres du pinceau
                    IconButton(onClick = { showBrushSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Brush,
                            contentDescription = stringResource(R.string.brush_settings_title)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer // Couleur pour les icônes d'action
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentIndex > 0) { currentIndex--; paths.clear(); evaluationScore = -1f } }, // Reset score
                        enabled = currentIndex > 0
                    ) { Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.previous)) }

                    // Bouton Effacer (Clear)
                    IconButton(onClick = { paths.clear(); currentPath.value = Path(); evaluationScore = -1f }) { // Reset score
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear))
                    }

                    // Peut-être un bouton "Vérifier" explicite ici ?
                    // IconButton(onClick = { /* Lancer l'évaluation manuellement */ }) { ... }

                    IconButton(
                        onClick = { if (currentIndex < characters.size - 1) { currentIndex++; paths.clear(); evaluationScore = -1f } }, // Reset score
                        enabled = currentIndex < characters.size - 1
                    ) { Icon(Icons.Filled.ArrowForward, contentDescription = stringResource(R.string.next)) }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Applique le padding du Scaffold
        ) {
            BannerAdView(modifier = Modifier.fillMaxWidth()) // Bannière publicitaire
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    // Récupère la taille disponible pour le Canvas
                    .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
            ) {
                // Caractère en arrière-plan
                val currentCharacter = characters.getOrNull(currentIndex)
                if (currentCharacter != null) {
                    Text(
                        text = currentCharacter,
                        fontSize = 300.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Canvas pour le guide (si activé et défini)
                if (showStrokeGuide && currentCharacter != null && adlamCharacterGuides.containsKey(currentCharacter) && canvasSize != Size.Zero) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawStrokeGuide(currentCharacter) // Appel fonction améliorée
                    }
                }

                // Canvas pour le dessin utilisateur et évaluation
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    evaluationScore = -1f // Réinitialise le score
                                    currentPath.value = Path().apply { moveTo(offset.x, offset.y) }
                                },
                                onDrag = { change, _ ->
                                    // Utilise la méthode de création d'un nouveau Path pour assurer la mise à jour
                                    currentPath.value = Path().apply {
                                        addPath(currentPath.value)
                                        lineTo(change.position.x, change.position.y)
                                    }
                                },
                                onDragEnd = {
                                    val finalPath = currentPath.value
                                    if (!finalPath.isEmpty) { // Seulement si quelque chose a été dessiné
                                        paths.add(Pair(finalPath, PaintAttributes(paintColor.value, brushSize.value, brushAlpha.value)))

                                        // --- Logique d'Évaluation ---
                                        val guideData = currentCharacter?.let { adlamCharacterGuides[it] }
                                        if (guideData != null && guideData.checkpoints.isNotEmpty() && canvasSize != Size.Zero) {
                                            evaluationScore = evaluateTraceWithCheckpoints(
                                                userPath = finalPath,
                                                targetCheckpoints = guideData.checkpoints,
                                                canvasSize = canvasSize,
                                                toleranceRadius = 35f // Ajustable
                                            )
                                            // Affiche le résultat
                                            scope.launch {
                                                val percentage = (evaluationScore * 100).toInt()
                                                snackbarHostState.showSnackbar(
                                                    message = "Précision : $percentage%",
                                                    withDismissAction = true // Permet de fermer
                                                )
                                            }
                                        } else {
                                            evaluationScore = -1f // Pas de données pour évaluer
                                            // Optionnel : message si évaluation non dispo
                                            // scope.launch { snackbarHostState.showSnackbar("Évaluation non disponible.") }
                                        }
                                    }
                                    // Réinitialise le chemin actuel pour le prochain trait ?
                                    // Si l'évaluation se fait sur le dessin complet, ne pas réinitialiser ici.
                                    currentPath.value = Path() // Réinitialise pour le prochain tracé indépendant
                                }
                            )
                        }
                ) {
                    // Dessine les tracés précédents (permanents)
                    drawPaths(paths)
                    // Dessine le tracé en cours (pendant le glissement)
                    if (!currentPath.value.isEmpty) {
                        drawPath(
                            path = currentPath.value,
                            color = paintColor.value.copy(alpha = brushAlpha.value),
                            style = Stroke(width = brushSize.value, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                } // Fin Canvas Dessin

                // Affichage textuel du score (optionnel)
                if (evaluationScore >= 0) {
                    Text(
                        text = "Score: ${(evaluationScore * 100).toInt()}%",
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp), // En haut à droite
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

            } // Fin Box
        } // Fin Column

        // Boîte de dialogue pour les paramètres du pinceau
        if (showBrushSettingsDialog) {
            BrushSettingsDialog(
                initialBrushSize = brushSize.value,
                initialColor = paintColor.value,
                initialAlpha = brushAlpha.value,
                onBrushSizeChange = { brushSize.value = it },
                onColorChange = { paintColor.value = it },
                onAlphaChange = { brushAlpha.value = it },
                onDismiss = { showBrushSettingsDialog = false }
            )
        }
    } // Fin Scaffold
} // Fin Composable WritingPracticeScreen


// --- Fonctions Utilitaires ---

// Data class pour les attributs de peinture (inchangée)
data class PaintAttributes(val color: Color, val brushSize: Float, val alpha: Float)

// Fonction pour dessiner les chemins sauvegardés (inchangée)
fun DrawScope.drawPaths(paths: List<Pair<Path, PaintAttributes>>) {
    paths.forEach { (path, attributes) ->
        drawPath(
            path = path,
            color = attributes.color.copy(alpha = attributes.alpha),
            style = Stroke(width = attributes.brushSize, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

// Fonction pour dessiner le guide (MISE À JOUR)
// Fonction pour dessiner le guide (CORRIGÉE)
fun DrawScope.drawStrokeGuide(letter: String) {
    val guideData = adlamCharacterGuides[letter]
    val canvasWidth = size.width
    val canvasHeight = size.height

    if (guideData != null && canvasWidth > 0 && canvasHeight > 0) {
        val transformationMatrix = Matrix().apply { scale(canvasWidth, canvasHeight) }

        // 1. Dessine les segments du chemin guide
        guideData.segments.forEachIndexed { index, segment ->

            // --- CORRECTION DE LA TRANSFORMATION ---
            // a. Crée un nouveau Path vide pour le résultat transformé.
            val transformedPath = Path()
            // b. Copie le contenu du chemin original du segment dans le nouveau Path.
            transformedPath.addPath(segment.path)
            // c. Applique la transformation sur la *copie* (transformedPath).
            transformedPath.transform(transformationMatrix)
            // --- FIN DE LA CORRECTION ---

            // d. Dessine le chemin qui a été transformé.
            drawPath(
                path = transformedPath, // Utilise maintenant le chemin correctement transformé
                color = GuideStyles.GuideColor,
                style = if (segment.isDash) GuideStyles.DashedStroke else GuideStyles.SolidStroke
            )

            // 2. Dessine l'indicateur de départ (logique inchangée)
            if (index == 0 && segment.startPoint != null) {
                // La transformation du point de départ était déjà correcte
                val absoluteStartPoint = transformationMatrix.map(segment.startPoint)
                drawCircle(
                    color = GuideStyles.StartIndicatorColor,
                    radius = GuideStyles.StartIndicatorRadius,
                    center = absoluteStartPoint
                )
            }
        }

        // 3. Visualisation des checkpoints (logique inchangée)
        val showCheckpointsForDebug = false // Mettre à true pour voir les zones rouges
        if (showCheckpointsForDebug) {
            guideData.checkpoints.forEach { checkpoint ->
                // La transformation du checkpoint était déjà correcte
                val absoluteCheckpoint = transformationMatrix.map(checkpoint)
                drawCircle(
                    color = GuideStyles.CheckpointVizColor,
                    radius = GuideStyles.CheckpointVizRadius,
                    center = absoluteCheckpoint,
                    style = Stroke(2f)
                )
            }
        }
    } else if (guideData == null) {
        println("Warning: Guide non défini pour '$letter'")
    }
}


// Fonction d'évaluation par checkpoints (NOUVELLE)
fun evaluateTraceWithCheckpoints(
    userPath: Path,
    targetCheckpoints: List<Offset>,
    canvasSize: Size,
    toleranceRadius: Float = 35f
): Float {
    if (targetCheckpoints.isEmpty()) return 1f
    if (userPath.isEmpty || canvasSize == Size.Zero) return 0f

    val transformationMatrix = Matrix().apply { scale(canvasSize.width, canvasSize.height) }
    val absoluteCheckpoints = targetCheckpoints.map { transformationMatrix.map(it) }

    // Utilisation de androidx.compose.ui.graphics.PathMeasure
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(userPath, false)
    val pathLength = pathMeasure.length // Accès à la propriété length
    if (pathLength == 0f) return 0f

    var checkpointsReached = 0
    var nextCheckpointIndex = 0
    val checkpointToleranceSquared = toleranceRadius.pow(2)

    var distanceAlongPath = 0f
    val step = 5f // Densité de vérification le long du chemin

    // --- CORRECTION : Utilisation de getPosition ---
    // Pas besoin de 'pos' FloatArray
    while (distanceAlongPath <= pathLength && nextCheckpointIndex < absoluteCheckpoints.size) {

        // Récupère l'Offset directement avec getPosition
        val currentPoint: Offset = pathMeasure.getPosition(distanceAlongPath)

        // La logique de comparaison reste identique
        val targetCheckpoint = absoluteCheckpoints[nextCheckpointIndex]

        val dx = currentPoint.x - targetCheckpoint.x
        val dy = currentPoint.y - targetCheckpoint.y
        val distanceSquared = dx * dx + dy * dy

        if (distanceSquared <= checkpointToleranceSquared) {
            checkpointsReached++
            nextCheckpointIndex++
            // Optionnel : ajouter une logique pour éviter de valider le même checkpoint
            // si le 'step' est très petit et qu'on reste longtemps dans la zone.
        }

        // Pas besoin de vérifier la valeur de retour de getPosition ou de 'break' ici.
        // La boucle s'arrêtera naturellement grâce à `distanceAlongPath <= pathLength`.
        distanceAlongPath += step
    }
    // --- FIN DE LA CORRECTION ---

    return checkpointsReached.toFloat() / targetCheckpoints.size.toFloat()
}


// --- Boîte de dialogue Paramètres Pinceau (inchangée structurellement) ---
@Composable
fun BrushSettingsDialog(
    initialBrushSize: Float, initialColor: Color, initialAlpha: Float,
    onBrushSizeChange: (Float) -> Unit, onColorChange: (Color) -> Unit, onAlphaChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var brushSize by remember { mutableFloatStateOf(initialBrushSize) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var brushAlpha by remember { mutableFloatStateOf(initialAlpha) }
    val colors = remember { listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color(0xFFFFA500) /*Orange*/, Color.Gray) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.brush_settings_title), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Taille
                Text(stringResource(R.string.brush_size, brushSize.toInt()), style = MaterialTheme.typography.titleMedium)
                Slider(value = brushSize, onValueChange = { brushSize = it }, valueRange = 5f..50f, steps = 8)
                // Couleur
                Text(stringResource(R.string.brush_color), style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedColor == color) MaterialTheme.colorScheme.outline else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
                // Opacité
                Text(stringResource(R.string.brush_alpha, (brushAlpha * 100).toInt()), style = MaterialTheme.typography.titleMedium)
                Slider(value = brushAlpha, onValueChange = { brushAlpha = it }, valueRange = 0.1f..1f)
            }
        },
        confirmButton = {
            Button(onClick = {
                onBrushSizeChange(brushSize); onColorChange(selectedColor); onAlphaChange(brushAlpha); onDismiss()
            }) { Text(stringResource(R.string.confirm)) }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
        }
    )
}

// --- Previews ---
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Practice Uppercase Dark")
@Composable
fun WritingPracticeScreenUppercasePreview() {
    // Theme { // Décommentez et utilisez votre thème si nécessaire
    WritingPracticeScreen(rememberNavController(), WritingType.UPPERCASE)
    // }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Practice Lowercase Light")
@Composable
fun WritingPracticeScreenLowercasePreview() {
    WritingPracticeScreen(rememberNavController(), WritingType.LOWERCASE)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Practice Numbers Light")
@Composable
fun WritingPracticeScreenNumbersPreview() {
    WritingPracticeScreen(rememberNavController(), WritingType.NUMBERS)
}