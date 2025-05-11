package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R

// --- REMOVE the duplicate enum definitions from here ---
// enum class ThemeMode { SYSTEM, LIGHT, DARK }
// enum class ColorTheme { DEFAULT, GREEN, BLUE, PURPLE, ORANGE }
// --- End REMOVE ---

// --- ADD Imports for the Enums from their single source of truth ---
import com.bekisma.adlamfulfulde.ThemeMode // Import ThemeMode from its actual package
import com.bekisma.adlamfulfulde.ColorTheme // Import ColorTheme from its actual package
// --- End ADD Imports ---

import com.bekisma.adlamfulfulde.ads.BannerAdView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onThemeChanged: (ThemeMode) -> Unit,
    onColorThemeChanged: (ColorTheme) -> Unit,
    currentTheme: ThemeMode, // This now expects the ThemeMode from the imported package
    currentColorTheme: ColorTheme // This now expects the ColorTheme from the imported package
) {
    val scaffoldState = rememberTopAppBarState()
    val scope = rememberCoroutineScope()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showColorThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back) // Added resource
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(scaffoldState)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                BannerAdView()
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background), // Use theme background
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.appearance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Theme Mode Setting
            item {
                SettingsCard(
                    title = stringResource(R.string.theme_mode),
                    description = when (currentTheme) { // Uses the imported ThemeMode
                        ThemeMode.SYSTEM -> stringResource(R.string.system_default)
                        ThemeMode.LIGHT -> stringResource(R.string.light)
                        ThemeMode.DARK -> stringResource(R.string.dark)
                    },
                    icon = Icons.Default.BrightnessMedium,
                    onClick = { showThemeDialog = true }
                )
            }

            // Color Theme Setting
            item {
                SettingsCard(
                    title = stringResource(R.string.color_theme),
                    description = when (currentColorTheme) { // Uses the imported ColorTheme
                        ColorTheme.DEFAULT -> stringResource(R.string.default_theme)
                        ColorTheme.GREEN -> stringResource(R.string.green)
                        ColorTheme.BLUE -> stringResource(R.string.blue)
                        ColorTheme.PURPLE -> stringResource(R.string.purple)
                        ColorTheme.ORANGE -> stringResource(R.string.orange)
                        ColorTheme.PURple -> TODO()
                    },
                    icon = Icons.Outlined.Palette,
                    onClick = { showColorThemeDialog = true }
                )
            }

            // About App Section Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.about_app),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // App Version
            item {
                val context = LocalContext.current
                val packageInfo = remember {
                    context.packageManager.getPackageInfo(context.packageName, 0)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Use theme color
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.version, packageInfo.versionName), // Use string resource
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "© 2025 Bekisma", // Or make this a string resource
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), // Use theme color
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Theme Mode Dialog
        if (showThemeDialog) {
            ThemeModeDialog(
                currentTheme = currentTheme, // Uses the imported ThemeMode
                onDismissRequest = { showThemeDialog = false },
                onThemeSelected = { theme -> onThemeChanged(theme) } // Passes the imported ThemeMode
            )
        }

        // Color Theme Dialog
        if (showColorThemeDialog) {
            ColorThemeDialog(
                currentColorTheme = currentColorTheme, // Uses the imported ColorTheme
                onDismissRequest = { showColorThemeDialog = false },
                onColorThemeSelected = { colorTheme -> onColorThemeChanged(colorTheme) } // Passes the imported ColorTheme
            )
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.elevatedCardColors( // Use theme colors
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, // Use theme color
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface // Use theme color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Use theme color
                )
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more_options), // Added resource
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Use theme color
            )
        }
    }
}

@Composable
fun ThemeModeDialog(
    currentTheme: ThemeMode, // Uses the imported ThemeMode
    onDismissRequest: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit // Expects the imported ThemeMode
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors( // Use theme colors
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_theme),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface, // Use theme color
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ThemeOption(
                    title = stringResource(R.string.system_default),
                    icon = Icons.Default.BrightnessMedium,
                    isSelected = currentTheme == ThemeMode.SYSTEM, // Compares with imported ThemeMode
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) } // Passes the imported ThemeMode
                )

                Spacer(modifier = Modifier.height(8.dp))

                ThemeOption(
                    title = stringResource(R.string.light),
                    icon = Icons.Outlined.WbSunny,
                    isSelected = currentTheme == ThemeMode.LIGHT, // Compares with imported ThemeMode
                    onClick = { onThemeSelected(ThemeMode.LIGHT) } // Passes the imported ThemeMode
                )

                Spacer(modifier = Modifier.height(8.dp))

                ThemeOption(
                    title = stringResource(R.string.dark),
                    icon = Icons.Outlined.DarkMode,
                    isSelected = currentTheme == ThemeMode.DARK, // Compares with imported ThemeMode
                    onClick = { onThemeSelected(ThemeMode.DARK) } // Passes the imported ThemeMode
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors( // Use theme colors
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animate background color for the option row based on selection and theme
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer // Use theme color
        else
            MaterialTheme.colorScheme.surface, // Use theme color
        animationSpec = tween(300), label = "ThemeOptionBackgroundColorAnimation"
    )

    // Animate icon and text color based on selection and theme
    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer // Use theme color
        else
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Use theme color
        animationSpec = tween(300), label = "ThemeOptionContentColorAnimation"
    )


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = backgroundColor // Uses the animated background color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Decorative icon
                tint = contentColor, // Uses the animated content color
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor, // Uses the animated content color
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.selected), // Added resource
                    tint = MaterialTheme.colorScheme.primary // Use theme color
                )
            }
        }
    }
}


@Composable
fun ColorThemeDialog(
    currentColorTheme: ColorTheme, // Uses the imported ColorTheme
    onDismissRequest: () -> Unit,
    onColorThemeSelected: (ColorTheme) -> Unit // Expects the imported ColorTheme
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors( // Use theme colors
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_color_theme),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface, // Use theme color
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Use the imported ColorTheme enum constants directly
                    ColorThemeOption(
                        color = Color(0xFF6750A4), // Default purple primary
                        isSelected = currentColorTheme == ColorTheme.DEFAULT,
                        onClick = { onColorThemeSelected(ColorTheme.DEFAULT) } // Passes the imported ColorTheme
                    )

                    ColorThemeOption(
                        color = Color(0xFF1B863E), // Green
                        isSelected = currentColorTheme == ColorTheme.GREEN,
                        onClick = { onColorThemeSelected(ColorTheme.GREEN) } // Passes the imported ColorTheme
                    )

                    ColorThemeOption(
                        color = Color(0xFF1976D2), // Blue
                        isSelected = currentColorTheme == ColorTheme.BLUE,
                        onClick = { onColorThemeSelected(ColorTheme.BLUE) } // Passes the imported ColorTheme
                    )

                    ColorThemeOption(
                        color = Color(0xFF9C27B0), // Purple
                        isSelected = currentColorTheme == ColorTheme.PURPLE,
                        onClick = { onColorThemeSelected(ColorTheme.PURPLE) } // Passes the imported ColorTheme
                    )

                    ColorThemeOption(
                        color = Color(0xFFE65100), // Orange
                        isSelected = currentColorTheme == ColorTheme.ORANGE,
                        onClick = { onColorThemeSelected(ColorTheme.ORANGE) } // Passes the imported ColorTheme
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors( // Use theme colors
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        }
    }
}

@Composable
fun ColorThemeOption(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = MaterialTheme.colorScheme.onBackground, // Use theme color for border
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    // Use onBackground or a contrasting theme color for the checkmark center
                    .background(MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}