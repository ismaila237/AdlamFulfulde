package com.bekisma.adlamfulfulde

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.screens.*
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import com.google.android.gms.ads.*
import com.bekisma.adlamfulfulde.model.mainMenuItems
import com.bekisma.adlamfulfulde.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import java.util.Calendar

// Imports for vocabulary screens
import com.bekisma.adlamfulfulde.screens.vocabulary.VocabularyListScreen
import com.bekisma.adlamfulfulde.screens.vocabulary.VocabularyDetailScreen

// Imports for guided reading screens
import com.bekisma.adlamfulfulde.screens.reading.ReadingPassageListScreen
import com.bekisma.adlamfulfulde.screens.reading.ReadingPlayerScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        setContent {
            val context = LocalContext.current
            val themeManager = remember { ThemeManager(context) }
            val scope = rememberCoroutineScope()

            val currentTheme by themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val currentColorTheme by themeManager.colorTheme.collectAsState(initial = ColorTheme.DEFAULT)

            val mainViewModel: MainViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return MainViewModel(getString(R.string.ad_mob_interstitial_id), applicationContext) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            })

            AdlamFulfuldeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") { MainScreen(navController, mainViewModel) }
                    composable("alphabet") { AlphabetScreen(navController) }
                    composable("numbers") { NumbersScreen(navController) }
                    composable("writing") { WritingScreen(navController) }
                    composable("quiz") { QuizScreen(navController) }
                    composable("about") { AboutScreen(navController) }
                    composable(
                        route = "writingPractice/{writingType}",
                        arguments = listOf(navArgument("writingType") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val typeString = backStackEntry.arguments?.getString("writingType")
                        val writingType = try {
                            WritingType.valueOf(typeString ?: WritingType.UPPERCASE.name)
                        } catch (e: IllegalArgumentException) {
                            WritingType.UPPERCASE
                        }
                        WritingPracticeScreen(navController = navController, writingType = writingType)
                    }

                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            currentTheme = currentTheme,
                            currentColorTheme = currentColorTheme,
                            onThemeChanged = { theme ->
                                scope.launch { themeManager.saveThemeMode(theme) }
                            },
                            onColorThemeChanged = { colorTheme ->
                                scope.launch { themeManager.saveColorTheme(colorTheme) }
                            }
                        )
                    }
                    composable(
                        route = "DetailAlphabetScreen/{letter}",
                        arguments = listOf(navArgument("letter") { type = NavType.StringType; nullable = true })
                    ) { backStackEntry ->
                        val letter = backStackEntry.arguments?.getString("letter")
                        DetailAlphabetScreen(letter = letter ?: "", navController = navController)
                    }

                    // Routes for Vocabulary
                    composable("vocabulary_list") {
                        VocabularyListScreen(navController = navController)
                    }
                    composable(
                        route = "vocabulary_detail/{itemId}",
                        arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getInt("itemId")
                        VocabularyDetailScreen(navController = navController, itemId = itemId)
                    }

                    // NEW ROUTES FOR GUIDED READING
                    composable("reading_passage_list") {
                        ReadingPassageListScreen(navController = navController)
                    }
                    composable(
                        route = "reading_player/{passageId}",
                        arguments = listOf(navArgument("passageId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val passageId = backStackEntry.arguments?.getInt("passageId")
                        ReadingPlayerScreen(navController = navController, passageId = passageId)
                    }
                    // END OF NEW ROUTES
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, mainViewModel: MainViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        kotlinx.coroutines.delay(1500)
        showSplash = false
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.iconapp),
                                contentDescription = stringResource(R.string.app_logo_description),
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.about)) },
                    icon = { Icon(Icons.Default.Info, contentDescription = stringResource(R.string.about)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("about") {
                                popUpTo("main") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.settings)) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("settings") {
                                popUpTo("main") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.privacy_policy)) },
                    icon = { Icon(painterResource(id = R.drawable.ic_privacy), contentDescription = stringResource(R.string.privacy_policy)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            try {
                                val privacyPolicyUrl = context.getString(R.string.privacy_policy_url)
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)))
                            } catch (e: Exception) {
                                Log.e("DrawerNav", "Could not open privacy policy URL", e)
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.rate_app)) },
                    icon = { Icon(Icons.Default.Star, contentDescription = stringResource(R.string.rate_app)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            try {
                                val playStoreUrl = "market://details?id=${context.packageName}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)))
                            } catch (e: Exception) {
                                val webUrl = "https://play.google.com/store/apps/details?id=${context.packageName}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
                                Log.e("DrawerNav", "Could not open Play Store URL", e)
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.share_app)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_app_text, context.packageName))
                                type = "text/plain"
                            }
                            try {
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            } catch (e: Exception) {
                                Log.e("DrawerNav", "Could not share app", e)
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    icon = { Icon(imageVector = Icons.Default.Share, contentDescription = stringResource(R.string.share_app)) }
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "© ${Calendar.getInstance().get(Calendar.YEAR)} Bekisma",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    val packageInfo = remember { context.packageManager.getPackageInfo(context.packageName, 0) }
                    Text(
                        text = stringResource(R.string.version, packageInfo.versionName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    ) {
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val scaleAnim by animateFloatAsState(
                        targetValue = if (showSplash) 1f else 0.8f,
                        animationSpec = tween(1000), label = "SplashScaleAnimation"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.iconapp),
                        contentDescription = stringResource(R.string.app_logo_description),
                        modifier = Modifier.size(150.dp).scale(scaleAnim),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.learn_adlam_script_ease),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !showSplash,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut()
        ) {
            Scaffold(
                topBar = {
                    LargeTopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.learn_adlam_script_ease_short),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = stringResource(R.string.menu_icon_desc),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
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
                Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 20.dp, end = 8.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.start_adlam_journey_title),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = stringResource(R.string.start_adlam_journey_description),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(
                                                onClick = { mainViewModel.handleNavigation(navController::navigate, "alphabet") },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(stringResource(R.string.get_started))
                                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, modifier = Modifier.padding(start = 4.dp))
                                            }
                                        }
                                        Image(
                                            painter = painterResource(id = R.drawable.abc64),
                                            contentDescription = stringResource(R.string.featured_image_desc),
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .size(100.dp)
                                                .graphicsLayer { rotationZ = -5f },
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.learning_modules),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(mainMenuItems) { item ->
                                    ElevatedMenuCard(
                                        image = painterResource(id = item.imageRes),
                                        title = stringResource(id = item.titleRes),
                                        subtitle = stringResource(id = item.subtitleRes),
                                        onClick = { mainViewModel.handleNavigation(navController::navigate, item.destination) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun ElevatedMenuCard(
    image: Painter,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    AdlamFulfuldeTheme {
        // For preview, we can provide a dummy MainViewModel
        val dummyMainViewModel = MainViewModel("test_ad_unit_id", LocalContext.current) // Use LocalContext for preview
        MainScreen(rememberNavController(), dummyMainViewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun MenuCardPreview() {
    AdlamFulfuldeTheme {
        ElevatedMenuCard(
            image = painterResource(id = R.drawable.abc64),
            title = "Alphabet Learning",
            subtitle = "Discover the Adlam Alphabet",
            onClick = {}
        )
    }
}
