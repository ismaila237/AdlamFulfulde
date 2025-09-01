package com.bekisma.adlamfulfulde.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView // Assuming this is correctly implemented elsewhere
import com.bekisma.adlamfulfulde.model.AlphabetItem
import com.bekisma.adlamfulfulde.model.AlphabetType
import com.bekisma.adlamfulfulde.model.Category
import com.bekisma.adlamfulfulde.model.alphabetCategories
import com.bekisma.adlamfulfulde.viewmodel.AlphabetViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Constants ---
private val CARD_SIZE = 100.dp
private val CARD_HEIGHT = 110.dp // Maintain slightly larger height for rounded rect
private val CARD_CORNER_RADIUS = 16.dp
private val GRID_SPACING = 12.dp
private val SCREEN_PADDING = 16.dp
private val CARD_PADDING_INSIDE = 4.dp
private val TAB_PADDING_HORIZONTAL = 16.dp // Padding for ScrollableTabRow edges
private val TAB_FONT_SIZE = 14.sp
private val ADLAM_LETTER_FONT_SIZE = 36.sp
private val LATIN_EQ_FONT_SIZE = 16.sp

/**
 * Main screen composable for displaying and interacting with the Adlam alphabet.
 * Allows filtering by category (Vowels, Consonants, Combined).
 */
@Composable
fun AlphabetScreen(navController: NavController, viewModel: AlphabetViewModel = viewModel()) {

    val haptic = LocalHapticFeedback.current
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredList by viewModel.filteredAlphabetList.collectAsState()

    Scaffold(
        topBar = {
            AlphabetTopBar(navController = navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CategoryTabs(
                    categories = alphabetCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { type ->
                        viewModel.selectCategory(type)
                    }
                )

                if (filteredList.isEmpty()) {
                    EmptyResultGeneric(selectedCategory, alphabetCategories)
                } else {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(minSize = CARD_SIZE + GRID_SPACING),
                        contentPadding = PaddingValues(SCREEN_PADDING),
                        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
                        verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
                        content = {
                            items(
                                items = filteredList,
                                key = { it.letter }
                            ) { letter ->
                                AlphabetItemCard(
                                    letter = letter,
                                    navController = navController,
                                    haptic = haptic
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(GRID_SPACING))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(GRID_SPACING))
                BannerAdView()
                Spacer(modifier = Modifier.height(GRID_SPACING))
            }
        }
    )
}

/**
 * Top AppBar for the Alphabet Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetTopBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.learn_alphabet),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate("AboutAdlamScreen") {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.adlam_info_desc),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

/**
 * Scrollable row of tabs for selecting alphabet categories.
 */
@Composable
fun CategoryTabs(
    categories: List<Category>,
    selectedCategory: AlphabetType,
    onCategorySelected: (AlphabetType) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = categories.indexOfFirst { it.type == selectedCategory }.coerceAtLeast(0),
        edgePadding = TAB_PADDING_HORIZONTAL,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category.type
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category.type) },
                text = {
                    Text(
                        text = stringResource(category.displayNameResId),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = TAB_FONT_SIZE
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Composable card to display a single Adlam alphabet item.
 * Handles visual and haptic feedback on click and navigates to detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetItemCard(
    letter: AlphabetItem,
    navController: NavController,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val cardShape = remember { RoundedCornerShape(CARD_CORNER_RADIUS) }

    val baseContainerColor = when (letter.type) {
        AlphabetType.VOWEL -> MaterialTheme.colorScheme.primaryContainer
        AlphabetType.COMBINED -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val onContainerColor = when (letter.type) {
        AlphabetType.VOWEL -> MaterialTheme.colorScheme.onPrimaryContainer
        AlphabetType.COMBINED -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 12.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CardElevationAnimation"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CardScaleAnimation"
    )

    Card(
        modifier = Modifier
            .size(width = CARD_SIZE, height = CARD_HEIGHT)
            .shadow(animatedElevation, cardShape)
            .scale(animatedScale),
        shape = cardShape,
        onClick = {
            scope.launch {
                isPressed = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(100)
                isPressed = false
                navController.navigate("DetailAlphabetScreen/${letter.letter}") {
                    launchSingleTop = true
                }
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = baseContainerColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(CARD_PADDING_INSIDE)
            ) {
                Text(
                    text = letter.letter,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    fontSize = ADLAM_LETTER_FONT_SIZE,
                    textAlign = TextAlign.Center,
                    color = onContainerColor,
                    lineHeight = ADLAM_LETTER_FONT_SIZE * 1.2f
                )

                if (letter.latinEquivalent.isNotBlank()) {
                    Text(
                        text = letter.latinEquivalent,
                        fontSize = LATIN_EQ_FONT_SIZE,
                        color = onContainerColor.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 2.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Composable to display a generic empty state message.
 * Shows different messages/titles based on the selected category.
 */
@Composable
fun EmptyResultGeneric(selectedCategory: AlphabetType, categories: List<Category>) {
    val message = when (selectedCategory) {
        AlphabetType.ALL -> stringResource(R.string.no_results_all)
        AlphabetType.VOWEL -> stringResource(R.string.no_results_vowels)
        AlphabetType.CONSONANT -> stringResource(R.string.no_results_consonants)
        AlphabetType.COMBINED -> stringResource(R.string.no_results_combined)
    }

    val title = when (selectedCategory) {
        AlphabetType.ALL -> stringResource(R.string.empty_title_generic)
        else -> {
            val categoryDisplayName = categories.firstOrNull { it.type == selectedCategory }
                ?.let { stringResource(it.displayNameResId) }
                ?: ""

            stringResource(R.string.empty_title_category, categoryDisplayName)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(R.string.no_result_icon_desc),
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(SCREEN_PADDING))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (selectedCategory != AlphabetType.ALL) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.try_other_category_suggestion),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
fun PreviewAlphabetScreen() {
    MaterialTheme {
        AlphabetScreen(rememberNavController())
    }
}

@Preview
@Composable
fun PreviewAlphabetItemCardVowel() {
    MaterialTheme {
        AlphabetItemCard(
            letter = AlphabetItem("𞤀", "a", type = AlphabetType.VOWEL),
            navController = rememberNavController(),
            haptic = LocalHapticFeedback.current
        )
    }
}

@Preview
@Composable
fun PreviewAlphabetItemCardConsonant() {
    MaterialTheme {
        AlphabetItemCard(
            letter = AlphabetItem("𞤁", "d", type = AlphabetType.CONSONANT),
            navController = rememberNavController(),
            haptic = LocalHapticFeedback.current
        )
    }
}

@Preview
@Composable
fun PreviewAlphabetItemCardCombined() {
    MaterialTheme {
        AlphabetItemCard(
            letter = AlphabetItem("𞤐𞤁", "nd", type = AlphabetType.COMBINED),
            navController = rememberNavController(),
            haptic = LocalHapticFeedback.current
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyResultGenericAll() {
    MaterialTheme {
        EmptyResultGeneric(
            selectedCategory = AlphabetType.ALL,
            categories = alphabetCategories
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyResultGenericVowel() {
    MaterialTheme {
        EmptyResultGeneric(
            selectedCategory = AlphabetType.VOWEL,
            categories = alphabetCategories
        )
    }
}