package com.bekisma.adlamfulfulde.screens

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingUpperCaseScreen(navController: NavController) {
    val adlamAlphabet = listOf(
        "𞤀", "𞤁", "𞤂", "𞤃", "𞤄", "𞤅", "𞤆",
        "𞤇", "𞤈", "𞤉", "𞤊", "𞤋", "𞤌", "𞤍", "𞤎",
        "𞤏", "𞤐", "𞤑", "𞤒", "𞤓", "𞤔", "𞤕", "𞤖",
        "𞤗", "𞤘", "𞤙", "𞤚", "𞤛",
    )
    var currentIndex by remember { mutableStateOf(0) }
    val paintColor = remember { mutableStateOf(Color.Black) }
    val brushSize = remember { mutableStateOf(20f) }
    val brushAlpha = remember { mutableStateOf(1f) }
    val paths = remember { mutableStateListOf<Pair<Path, PaintAttributes>>() }
    val currentPath = remember { mutableStateOf(Path()) }
    var showBrushSettingsDialog by remember { mutableStateOf(false) }
    var showStrokeGuide by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.writing_uppercase), fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showStrokeGuide = showStrokeGuide }) {
                        Icon(
                            painter = painterResource(
                                id = if (showStrokeGuide) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                            ),
                            contentDescription = stringResource(
                                if (showStrokeGuide) R.string.hide_guide else R.string.show_guide
                            )
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
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                paths.clear()
                            }
                        },
                        enabled = currentIndex > 0
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.previous))
                    }
                    IconButton(onClick = { showBrushSettingsDialog = true }) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.brush_settings_title))
                    }
                    IconButton(onClick = { paths.clear() }) {
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear))
                    }
                    IconButton(
                        onClick = {
                            if (currentIndex < adlamAlphabet.size - 1) {
                                currentIndex++
                                paths.clear()
                            }
                        },
                        enabled = currentIndex < adlamAlphabet.size - 1
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = stringResource(R.string.next))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BannerAdView()
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Background letter
                Text(
                    text = adlamAlphabet[currentIndex],
                    fontSize = 300.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.align(Alignment.Center)
                )

                // Stroke guide
                if (showStrokeGuide) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawStrokeGuide(adlamAlphabet[currentIndex])
                    }
                }

                // Drawing canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath.value.moveTo(offset.x, offset.y)
                                },
                                onDrag = { change, _ ->
                                    currentPath.value.lineTo(change.position.x, change.position.y)
                                },
                                onDragEnd = {
                                    paths.add(Pair(currentPath.value, PaintAttributes(paintColor.value, brushSize.value, brushAlpha.value)))
                                    currentPath.value = Path()
                                }
                            )
                        }
                ) {
                    drawPaths(paths)
                    drawPath(
                        path = currentPath.value,
                        color = paintColor.value.copy(alpha = brushAlpha.value),
                        style = Stroke(width = brushSize.value)
                    )
                }
            }
        }

        if (showBrushSettingsDialog) {
            BrushSettingsDialog(
                initialBrushSize = brushSize.value,
                initialColor = paintColor.value,
                initialAlpha = brushAlpha.value,
                onBrushSizeChange = { newSize -> brushSize.value = newSize },
                onColorChange = { newColor -> paintColor.value = newColor },
                onAlphaChange = { newAlpha -> brushAlpha.value = newAlpha },
                onDismiss = { showBrushSettingsDialog = false }
            )
        }
    }
}

fun DrawScope.drawStrokeGuide(letter: String) {
    // This is a simplified example. You would need to define actual stroke paths for each letter.
    val path = Path().apply {
        // Example stroke guide for "𞤀" (first letter of Adlam)
        moveTo(size.width * 0.3f, size.height * 0.2f)
        lineTo(size.width * 0.7f, size.height * 0.2f)
        lineTo(size.width * 0.5f, size.height * 0.8f)
    }

    drawPath(
        path = path,
        color = Color.Red.copy(alpha = 0.5f),
        style = Stroke(width = 10f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f)))
    )
}

// ... (rest of the code remains the same)

fun saveDrawingToGallery(context: Context, currentLetter: String) {
    val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)

    // Draw current letter and paths on canvas
    // Adapt code to draw the current letter and paths

    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "Drawing_${System.currentTimeMillis()}.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    if (uri != null) {
        val outputStream: OutputStream? = resolver.openOutputStream(uri)
        outputStream.use {
            if (it != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
        Toast.makeText(context, "Drawing saved to gallery", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Error saving drawing", Toast.LENGTH_SHORT).show()
    }
}

fun DrawScope.drawPaths(paths: List<Pair<Path, PaintAttributes>>) {
    paths.forEach { (path, attributes) ->
        drawPath(
            path = path,
            color = attributes.color.copy(alpha = attributes.alpha),
            style = Stroke(width = attributes.brushSize)
        )
    }
}

data class PaintAttributes(val color: Color, val brushSize: Float, val alpha: Float)

@Composable
fun BrushSettingsDialog(
    initialBrushSize: Float,
    initialColor: Color,
    initialAlpha: Float,
    onBrushSizeChange: (Float) -> Unit,
    onColorChange: (Color) -> Unit,
    onAlphaChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var brushSize by remember { mutableStateOf(initialBrushSize) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var brushAlpha by remember { mutableStateOf(initialAlpha) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.brush_settings_title), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(stringResource(R.string.brush_size, brushSize.toInt()))
                Slider(
                    value = brushSize,
                    onValueChange = { brushSize = it },
                    valueRange = 30f..50f // Updated value range for brush size
                )
                Text(stringResource(R.string.brush_color))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedColor == color) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
                Text(stringResource(R.string.brush_alpha, (brushAlpha * 100).toInt()))
                Slider(
                    value = brushAlpha,
                    onValueChange = { brushAlpha = it },
                    valueRange = 0f..1f
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onBrushSizeChange(brushSize)
                    onColorChange(selectedColor)
                    onAlphaChange(brushAlpha)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WritingUpperCaseScreenPreview() {
    WritingUpperCaseScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun WritingUpperCaseScreenPreviewLight() {
    WritingUpperCaseScreen(rememberNavController())
}
