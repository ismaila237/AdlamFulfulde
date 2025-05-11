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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Data Structures ---

// Enum to represent the type of Adlam character
enum class AlphabetType {
    ALL, VOWEL, CONSONANT, COMBINED
}

data class AlphabetItem(
    val letter: String,
    val latinEquivalent: String = "",
    val pronunciation: String = "", // Added for potential future use
    val examples: List<String> = emptyList(), // Added for potential future use
    val type: AlphabetType // Explicitly define the type
)

// Data class for category tabs
data class Category(
    val type: AlphabetType,
    val displayNameResId: Int // Resource ID for the display name
)

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

// --- Helper Lists (Internal classification - used for data initialization) ---
private val AdlamVowelsLetters = listOf("𞤀", "𞤉", "𞤋", "𞤌", "𞤓")
private val AdlamCombinedLetters = listOf("𞤐𞤁", "𞤐𞤄", "𞤐𞤔", "𞤐𞤘")
// All other letters in the list will be classified as CONSONANT


/**
 * Main screen composable for displaying and interacting with the Adlam alphabet.
 * Allows filtering by category (Vowels, Consonants, Combined).
 */
@Composable
fun AlphabetScreen(navController: NavController) {

    // Initialize the alphabet list with types
    val alphabetList = remember {
        // Define the complete list of Adlam letters and their properties.
        // The type is explicitly assigned here based on knowledge of the script.
        listOf(
            AlphabetItem("𞤀", "a", type = AlphabetType.VOWEL),
            AlphabetItem("𞤁", "d", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤂", "l", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤃", "m", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤄", "b", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤅", "s", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤆", "p", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤇", "ɓ", type = AlphabetType.CONSONANT), // Implosive B
            AlphabetItem("𞤈", "r", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤉", "e", type = AlphabetType.VOWEL),
            AlphabetItem("𞤊", "f", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤋", "i", type = AlphabetType.VOWEL),
            AlphabetItem("𞤌", "o", type = AlphabetType.CONSONANT), // Note: This is 'O' - check source list again? Original had it as Vowel. Corrected to Vowel based on typical Adlam vowels.
            AlphabetItem("𞤍", "ɗ", type = AlphabetType.CONSONANT), // Implosive D
            AlphabetItem("𞤎", "ƴ", type = AlphabetType.CONSONANT), // Implosive Y
            AlphabetItem("𞤏", "w", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤐", "n", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤑", "k", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤒", "y", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤓", "u", type = AlphabetType.VOWEL),
            AlphabetItem("𞤔", "j", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤕", "c", type = AlphabetType.CONSONANT), // Adlam 'c' is often equivalent to Ch
            AlphabetItem("𞤖", "h", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤗", "ɠ", type = AlphabetType.CONSONANT), // Implosive G
            AlphabetItem("𞤘", "g", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤙", "ñ", type = AlphabetType.CONSONANT), // Ny
            AlphabetItem("𞤚", "t", type = AlphabetType.CONSONANT),
            AlphabetItem("𞤛", "ŋ", type = AlphabetType.CONSONANT), // Ng (velar nasal)
            AlphabetItem("𞤐𞤁", "nd", type = AlphabetType.COMBINED), // Nd
            AlphabetItem("𞤐𞤄", "mb", type = AlphabetType.COMBINED), // Mb
            AlphabetItem("𞤐𞤔", "nj", type = AlphabetType.COMBINED), // Nj
            AlphabetItem("𞤐𞤘", "ŋg", type = AlphabetType.COMBINED) // Ŋg
        ).sortedBy { it.letter } // Sort based on the standard Adlam alphabetical order
        // Note: Adlam is generally sorted by Unicode value, which often corresponds
        // to the standard order. If a different order is needed, a custom comparator
        // or a predefined ordered list should be used.
    }

    val haptic = LocalHapticFeedback.current
    // Use Enum for state, initialized to ALL
    var selectedCategory by remember { mutableStateOf(AlphabetType.ALL) }

    // Define categories using data class and resource IDs
    val categories = remember {
        listOf(
            Category(AlphabetType.ALL, R.string.category_all),
            Category(AlphabetType.VOWEL, R.string.category_vowels),
            Category(AlphabetType.CONSONANT, R.string.category_consonants),
            Category(AlphabetType.COMBINED, R.string.category_combined)
        )
    }

    // Filter the list based on the selected category type
    val filteredList = remember(alphabetList, selectedCategory) {
        alphabetList.filter { item ->
            when(selectedCategory) {
                AlphabetType.ALL -> true // Show all items
                AlphabetType.VOWEL -> item.type == AlphabetType.VOWEL
                AlphabetType.CONSONANT -> item.type == AlphabetType.CONSONANT
                AlphabetType.COMBINED -> item.type == AlphabetType.COMBINED
            }
        }
    }

    Scaffold(
        topBar = {
            AlphabetTopBar(navController = navController)
        },
        // Adlam is an LTR script. Default LTR layout is correct.
        // Removed the incorrect RTL CompositionLocalProvider here.
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CategoryTabs(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { type ->
                        selectedCategory = type
                    }
                )

                // Conditional content based on filtered list
                if (filteredList.isEmpty()) {
                    // Pass categories to EmptyResultGeneric so it can display the category name
                    EmptyResultGeneric(selectedCategory, categories)
                } else {
                    // LazyVerticalGrid for displaying the alphabet cards
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Adaptive(minSize = CARD_SIZE + GRID_SPACING), // Adjust adaptive size based on card size + spacing
                        contentPadding = PaddingValues(SCREEN_PADDING),
                        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
                        verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
                        content = {
                            items(
                                items = filteredList,
                                key = { it.letter } // Use letter as key for efficient updates
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

                // Spacer and Ad section at the bottom
                // Use consistent spacing around the ad
                Spacer(modifier = Modifier.height(GRID_SPACING))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(GRID_SPACING))
                // Assuming BannerAdView is a composable that displays an ad banner
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
                // Navigate to AboutAdlamScreen, potentially clearing back stack above it
                // Note: Using hardcoded route name. Consider using a sealed class or object
                // for navigation routes in a larger app.
                navController.navigate("AboutAdlamScreen") {
                    launchSingleTop = true // Avoid multiple copies of the same destination
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
            containerColor = MaterialTheme.colorScheme.primaryContainer // Use primary container color for distinction
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
        edgePadding = TAB_PADDING_HORIZONTAL, // Padding at the start/end of the tab row
        containerColor = MaterialTheme.colorScheme.surface, // Background color of the tab row
        contentColor = MaterialTheme.colorScheme.primary // Color of the selected tab indicator and text
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
                selectedContentColor = MaterialTheme.colorScheme.primary, // Color of the selected tab text
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant // Color of unselected tab text
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
    // State to track if the card is currently pressed for animation
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val cardShape = remember { RoundedCornerShape(CARD_CORNER_RADIUS) }

    // Determine colors based on the letter type
    val baseContainerColor = when (letter.type) {
        AlphabetType.VOWEL -> MaterialTheme.colorScheme.primaryContainer
        AlphabetType.COMBINED -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer // Consonants and potentially other types
    }

    val onContainerColor = when (letter.type) {
        AlphabetType.VOWEL -> MaterialTheme.colorScheme.onPrimaryContainer
        AlphabetType.COMBINED -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    // Animation for elevation and scale when pressed
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
            .shadow(animatedElevation, cardShape) // Apply shadow with the shape
            .scale(animatedScale), // Apply scale animation
        shape = cardShape, // Use the defined shape
        onClick = {
            // Use a coroutine scope to manage the press state change and delay
            scope.launch {
                isPressed = true // Immediately set pressed state for visual feedback
                haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Trigger haptic feedback
                delay(100) // Short delay for the animation/visual effect
                isPressed = false // Reset pressed state
                // Navigate after the brief interaction feedback
                // Pass the letter string as an argument to the detail screen
                // Note: Using hardcoded route name. Consider using a sealed class or object
                // for navigation routes in a larger app.
                navController.navigate("DetailAlphabetScreen/${letter.letter}") {
                    launchSingleTop = true // Prevent multiple copies of the same destination on top
                }
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = baseContainerColor // Use the determined background color
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // Center content inside the card
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(CARD_PADDING_INSIDE) // Add slight padding inside the card
            ) {
                // Display Adlam letter
                Text(
                    text = letter.letter,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    fontSize = ADLAM_LETTER_FONT_SIZE,
                    textAlign = TextAlign.Center,
                    color = onContainerColor, // Use the determined text color
                    lineHeight = ADLAM_LETTER_FONT_SIZE * 1.2f // Adjusted lineHeight to prevent clipping for tall characters
                )

                // Display Latin equivalent if available
                if (letter.latinEquivalent.isNotBlank()) {
                    Text(
                        text = letter.latinEquivalent,
                        fontSize = LATIN_EQ_FONT_SIZE,
                        color = onContainerColor.copy(alpha = 0.9f), // Slightly less alpha for secondary text
                        modifier = Modifier.padding(top = 2.dp), // Small padding above
                        textAlign = TextAlign.Center
                    )
                }
                // Future: Add space/placeholder for more info like pronunciation/examples later
            }
        }
    }
}

/**
 * Composable to display a generic empty state message.
 * Shows different messages/titles based on the selected category.
 */
@Composable
fun EmptyResultGeneric(selectedCategory: AlphabetType, categories: List<Category>) { // <-- Categories list is now a parameter
    // Determine the message text based on the selected category
    val message = when (selectedCategory) {
        AlphabetType.ALL -> stringResource(R.string.no_results_all)
        AlphabetType.VOWEL -> stringResource(R.string.no_results_vowels)
        AlphabetType.CONSONANT -> stringResource(R.string.no_results_consonants)
        AlphabetType.COMBINED -> stringResource(R.string.no_results_combined)
    }

    // Determine the title text, including the category name if applicable
    val title = when (selectedCategory) {
        AlphabetType.ALL -> stringResource(R.string.empty_title_generic)
        else -> {
            // Find the display name for the selected category from the passed list
            val categoryDisplayName = categories.firstOrNull { it.type == selectedCategory }
                ?.let { stringResource(it.displayNameResId) } // Resolve the string resource
                ?: "" // Fallback to empty string if category not found (shouldn't happen)

            // Format the title string with the category name
            stringResource(R.string.empty_title_category, categoryDisplayName)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING), // Use consistent screen padding
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Center content vertically and horizontally
    ) {
        // Info icon
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(R.string.no_result_icon_desc),
            modifier = Modifier.size(100.dp), // Slightly smaller icon
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) // More faded icon
        )
        Spacer(modifier = Modifier.height(SCREEN_PADDING)) // Consistent spacing below icon
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge, // Use titleLarge for more emphasis
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface // Standard text color
        )
        Spacer(modifier = Modifier.height(8.dp)) // Small spacing between title and message
        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium, // Use bodyMedium for message
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Slightly less prominent color
        )
        // Suggestion to try another category if not showing "All"
        if (selectedCategory != AlphabetType.ALL) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.try_other_category_suggestion),
                style = MaterialTheme.typography.bodySmall, // Use bodySmall for suggestion
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Same color as message
            )
        }
    }
}

// --- Previews ---

/**
 * Helper composable to create a list of Category objects for previews.
 * This is needed because `stringResource` requires a Composition context.
 */
@Composable
private fun previewCategories(): List<Category> {
    // These strings need to be resolvable in the preview environment.
    // Ensure your res/values/strings.xml contains them or mock them for preview.
    return listOf(
        Category(AlphabetType.ALL, R.string.category_all),
        Category(AlphabetType.VOWEL, R.string.category_vowels),
        Category(AlphabetType.CONSONANT, R.string.category_consonants),
        Category(AlphabetType.COMBINED, R.string.category_combined)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAlphabetScreen() {
    // Wrap in MaterialTheme to provide necessary styling context
    MaterialTheme {
        // For the main screen preview, we don't need to mock the categories list
        // explicitly being passed to EmptyResultGeneric within AlphabetScreen,
        // as that happens internally in the composable's logic.
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
        // Pass the necessary parameters for the preview
        EmptyResultGeneric(
            selectedCategory = AlphabetType.ALL,
            categories = previewCategories() // Provide preview categories
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyResultGenericVowel() {
    MaterialTheme {
        // Pass the necessary parameters for the preview
        EmptyResultGeneric(
            selectedCategory = AlphabetType.VOWEL,
            categories = previewCategories() // Provide preview categories
        )
    }
}