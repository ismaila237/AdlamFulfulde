package com.bekisma.adlamfulfulde.screens.vocabulary

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.data.VocabularyItem
import com.bekisma.adlamfulfulde.data.getVocabularyList
import com.bekisma.adlamfulfulde.screens.MediaPlayerSingleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListScreen(navController: NavController) {
    val vocabularyList = remember { getVocabularyList() }

    // Get the category string resource outside of remember block
    val categoryOtherString = stringResource(R.string.category_other)

    // Now use the string in remember
    val groupedVocabulary = remember(vocabularyList) {
        vocabularyList.groupBy { it.category ?: categoryOtherString }
    }

    // State for search functionality
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Fixed: Move stringResource outside of remember
    // Filtered list based on search
    val filteredVocabulary = remember(searchQuery, vocabularyList, categoryOtherString) {
        if (searchQuery.isEmpty()) {
            groupedVocabulary
        } else {
            vocabularyList
                .filter {
                    it.adlamWord.contains(searchQuery, ignoreCase = true) ||
                            it.translation.contains(searchQuery, ignoreCase = true)
                }
                .groupBy { it.category ?: categoryOtherString }
        }
    }

    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onCloseSearch = {
                        isSearchActive = false
                        searchQuery = ""
                    }
                )
            } else {
                LargeTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.vocabulary_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
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
        },
        floatingActionButton = {
            if (scrollState.canScrollBackward) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.scroll_to_top)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (filteredVocabulary.isEmpty()) {
            EmptySearchResult(
                query = searchQuery,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredVocabulary.forEach { (category, items) ->
                    item {
                        CategoryHeader(
                            categoryName = category,
                            isFirstCategory = category == filteredVocabulary.keys.first()
                        )
                    }

                    items(items, key = { it.id }) { item ->
                        val visibleState = remember { MutableTransitionState(false) }

                        LaunchedEffect(item) {
                            visibleState.targetState = true
                        }

                        AnimatedVisibility(
                            visibleState = visibleState,
                            enter = fadeIn(tween(durationMillis = 300)) +
                                    expandVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            VocabularyListItem(
                                item = item,
                                onClick = {
                                    navController.navigate("vocabulary_detail/${item.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    var isFocused by remember { mutableStateOf(true) }

    // Basic TextField without custom colors to ensure compatibility
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .semantics { contentDescription = "Search text field" },
        placeholder = { Text(stringResource(R.string.search_vocabulary)) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
        // No custom colors - use the defaults
    )

    // Auto-focus the search field
    LaunchedEffect(Unit) {
        isFocused = true
    }
}

// Alternative approach using basic styling that works with all versions
@Composable
fun SearchTopBarAlternative(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    var isFocused by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Search text field" },
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_vocabulary),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        innerTextField()
                    }
                }
            )

            IconButton(onClick = onCloseSearch) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Auto-focus the search field
    LaunchedEffect(Unit) {
        isFocused = true
    }
}

// If you want to determine which version of Material 3 you're using
@Composable
fun DetectMaterial3Version() {
    // Try to import different TextField-related classes and functions
    // and check which ones are available in your project

    // For example, try importing:
    // import androidx.compose.material3.TextFieldDefaults
    // and check if the following are available:
    // - TextFieldDefaults.textFieldColors
    // - TextFieldDefaults.colors
    // - TextFieldDefaults.outlinedTextFieldColors

    // Then use whichever one is available in your version
}

// Alternative version if you're using a newer Material 3 version with different parameter names
@Composable
fun SearchTopBar2(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    var isFocused by remember { mutableStateOf(true) }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .semantics { contentDescription = "Search text field" },
        placeholder = { Text(stringResource(R.string.search_vocabulary)) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        // Using a different set of parameter names that might match your version
        colors = TextFieldDefaults.colors(
            // Try these parameters instead:
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = MaterialTheme.colorScheme.error
        )
    )

    // Auto-focus the search field
    LaunchedEffect(Unit) {
        isFocused = true
    }
}

// Simple fallback option if you're having trouble with the TextField colors
@Composable
fun SearchTopBarSimple(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit
) {
    var isFocused by remember { mutableStateOf(true) }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .semantics { contentDescription = "Search text field" },
        placeholder = { Text(stringResource(R.string.search_vocabulary)) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.close_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
        // No colors specified - use defaults
    )

    // Auto-focus the search field
    LaunchedEffect(Unit) {
        isFocused = true
    }
}

@Composable
fun CategoryHeader(
    categoryName: String,
    isFirstCategory: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = if (isFirstCategory) 0.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .weight(0.15f)
                .padding(end = 8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.7f)
        )

        Divider(
            modifier = Modifier
                .weight(0.15f)
                .padding(start = 8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyListItem(item: VocabularyItem, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = item.adlamWord,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                if (item.imageResId != null) {
                    // Background circle for the image
                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )

                    Image(
                        painter = painterResource(id = item.imageResId),
                        contentDescription = item.translation,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Show a simple icon if no image is available
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Sound indicator if sound is available
                if (item.soundResId != R.raw.son_nul) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomEnd)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptySearchResult(query: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (query.isEmpty())
                stringResource(R.string.vocabulary_empty)
            else stringResource(R.string.no_search_results, query),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyDetailScreen(navController: NavController, itemId: Int?) {
    val vocabularyList = remember { getVocabularyList() }
    val item = remember(itemId) { vocabularyList.find { it.id == itemId } }
    val context = LocalContext.current
    var isPlayingAudio by remember { mutableStateOf(false) }

    // Animation for audio playback visual feedback
    val waveAmplitude by animateFloatAsState(
        targetValue = if (isPlayingAudio) 1f else 0f,
        animationSpec = repeatable(
            iterations = if (isPlayingAudio) Int.MAX_VALUE else 0,
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveAmplitude"
    )

    var isFavorite by remember { mutableStateOf(false) }
    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favoriteScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        item?.adlamWord ?: stringResource(R.string.vocabulary_detail_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        MediaPlayerSingleton.stopSound()
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.graphicsLayer { scaleX = favoriteScale; scaleY = favoriteScale }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(
                                if (isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites
                            ),
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Share button
                    IconButton(onClick = { /* Share functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share)
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
    ) { paddingValues ->
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        stringResource(R.string.vocabulary_item_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigateUp() },
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.go_back))
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image card
            item.imageResId?.let { imageResId ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(contentAlignment = Alignment.BottomStart) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = item.translation,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Category badge
                            item.category?.let { category ->
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(topEnd = 16.dp),
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Main word and translation card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Adlam word with pulse animation when audio is playing
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.adlamWord,
                                fontSize = 60.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = 1f + (waveAmplitude * 0.05f)
                                    scaleY = 1f + (waveAmplitude * 0.05f)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Translation
                        Text(
                            text = item.translation,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Audio button with improved UI
                        AudioPlayButton(
                            isPlaying = isPlayingAudio,
                            isEnabled = item.soundResId != R.raw.son_nul,
                            onClick = {
                                if (item.soundResId != R.raw.son_nul) {
                                    isPlayingAudio = true
                                    MediaPlayerSingleton.playSound(context, item.soundResId) {
                                        isPlayingAudio = false
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Example sentences section
            if (item.exampleSentenceAdlam != null) {
                item {
                    ExampleSentenceCard(
                        adlamSentence = item.exampleSentenceAdlam,
                        translationSentence = item.exampleSentenceTranslation
                    )
                }
            }

            // Related words section (placeholder for future enhancement)
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Clean up audio on screen exit
    DisposableEffect(Unit) {
        onDispose {
            MediaPlayerSingleton.stopSound()
        }
    }
}

@Composable
fun AudioPlayButton(
    isPlaying: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Animation for ripple effect
    var animatedRipple by remember { mutableStateOf(false) }
    val rippleSize by animateFloatAsState(
        targetValue = if (animatedRipple) 80f else 0f,
        animationSpec = tween(1000),
        label = "rippleSize"
    )
    val rippleAlpha by animateFloatAsState(
        targetValue = if (animatedRipple) 0f else 0.2f,
        animationSpec = tween(1000),
        label = "rippleAlpha"
    )

    // Trigger ripple effect when playing
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            animatedRipple = true
            delay(1000)
            animatedRipple = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.semantics {
            contentDescription = if (isPlaying)
                "Playing audio"
            else if (isEnabled)
                "Play audio"
            else
                "Audio unavailable"
        }
    ) {
        // Animated ripple background when pressed
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .size(with(density) { rippleSize.dp })
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = rippleAlpha),
                        CircleShape
                    )
            )
        }

        Button(
            onClick = onClick,
            enabled = isEnabled && !isPlaying,
            shape = CircleShape,
            modifier = Modifier.size(64.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEnabled)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isEnabled)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.VolumeUp else Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = when {
            isPlaying -> stringResource(R.string.playing_sound)
            isEnabled -> stringResource(R.string.play_sound_short)
            else -> stringResource(R.string.no_audio_available)
        },
        style = MaterialTheme.typography.bodyMedium,
        color = if (isEnabled)
            MaterialTheme.colorScheme.onSurface
        else
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
}

@Composable
fun ExampleSentenceCard(
    adlamSentence: String,
    translationSentence: String?
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = stringResource(R.string.example_sentence_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = adlamSentence,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            translationSentence?.let {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}