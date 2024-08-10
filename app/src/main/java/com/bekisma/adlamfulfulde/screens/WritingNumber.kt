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
fun WritingNumberScreen(navController: NavController) {
    // List of Adlam numerals
    val adlamNumerals = listOf(
        "𞥐", "𞥑", "𞥒", "𞥓", "𞥔",
        "𞥕", "𞥖", "𞥗", "𞥘", "𞥙"
    )
    var currentIndex by remember { mutableStateOf(0) }
    val paintColor = remember { mutableStateOf(Color.Black) }
    val brushSize = remember { mutableStateOf(15f) } // Default brush size
    val brushAlpha = remember { mutableStateOf(1f) }
    val paths = remember { mutableStateListOf<Pair<Path, PaintAttributes>>() }
    val currentPath = remember { mutableStateOf(Path()) }
    var showBrushSettingsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                paths.clear() // Clear the paths when navigating
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.previous))
                    }
                    IconButton(onClick = { showBrushSettingsDialog = true }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings))
                    }
                    IconButton(
                        onClick = {
                            if (currentIndex < adlamNumerals.size - 1) {
                                currentIndex++
                                paths.clear() // Clear the paths when navigating
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = stringResource(R.string.next))
                    }
                    IconButton(onClick = { paths.clear() }) {
                        Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.clear))
                    }
//                    IconButton(onClick = {
//                        saveDrawingToGallery(context, adlamNumerals[currentIndex])
//                    }) {
//                        Icon(painterResource(id = R.drawable.save_alt), contentDescription = stringResource(R.string.save))
//                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display banner ad at the top of the content
            BannerAdView()
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Use theme background color
            ) {
                Text(
                    text = adlamNumerals[currentIndex],
                    fontSize = 300.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )

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




@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WritingNumberScreenPreview() {
    WritingNumberScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun WritingNumberScreenPreviewLight() {
    WritingNumberScreen(rememberNavController())
}
