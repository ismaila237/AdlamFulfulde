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
// Make sure this import points to your updated theme file:
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var interstitialAdManager: ImprovedInterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AdMob
        MobileAds.initialize(this) {
            // Initialize and preload ad manager after MobileAds is ready
            // Check if interstitialAdManager is not already initialized (e.g., during config change)
            if (!this::interstitialAdManager.isInitialized) {
                interstitialAdManager = ImprovedInterstitialAdManager(this, getString(R.string.ad_mob_interstitial_id))
                interstitialAdManager.preloadAd()
            }
        }

        setContent {
            // Get context and coroutine scope for interacting with DataStore
            val context = LocalContext.current
            // Remember ThemeManager here *only* to pass to SettingsScreen for saving
            val themeManager = remember { ThemeManager(context) }
            val scope = rememberCoroutineScope() // Coroutine scope for launching suspend functions

            // Collect theme states from ThemeManager Flows to display current selection IN SETTINGS SCREEN
            // The AppTheme composable *also* collects these states to apply the theme globally.
            val currentTheme by themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val currentColorTheme by themeManager.colorTheme.collectAsState(initial = ColorTheme.DEFAULT)

            // AdlamFulfuldeTheme (the one in ui.theme) wraps the entire NavHost.
            // It observes the themeManager.themeMode and themeManager.colorTheme
            // internally and applies the correct MaterialTheme, causing the whole UI to recompose with new colors.
            AdlamFulfuldeTheme { // This AdlamFulfuldeTheme should now be the dynamic one
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main" // Or "splash" if you want a dedicated splash route
                ) {
                    composable("main") { MainScreen(navController) }
                    composable("alphabet") { AlphabetScreen(navController) }
                    composable("numbers") { NumbersScreen(navController) }
                    composable("writing") { WritingScreen(navController) }
                    composable("quiz") { QuizScreen(navController) }
                    composable("about") { AboutScreen(navController) }
                    composable(
                        route = "writingPractice/{writingType}", // Route avec argument
                        arguments = listOf(navArgument("writingType") { type = NavType.StringType }) // Définition de l'argument
                    ) { backStackEntry ->
                        // Récupération et conversion de l'argument
                        val typeString = backStackEntry.arguments?.getString("writingType")
                        // Utilise UPPERCASE comme valeur par défaut si l'argument est manquant ou invalide
                        val writingType = try {
                            WritingType.valueOf(typeString ?: WritingType.UPPERCASE.name)
                        } catch (e: IllegalArgumentException) {
                            WritingType.UPPERCASE // Sécurité supplémentaire si valueOf échoue
                        }

                        // Appel du nouvel écran unique
                        WritingPracticeScreen(navController = navController, writingType = writingType)
                    }

                    // --- Settings Screen Implementation ---
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            // Pass collected state to display current selection in SettingsScreen UI
                            currentTheme = currentTheme,
                            currentColorTheme = currentColorTheme,
                            // Provide lambdas that call ThemeManager save functions (triggers DataStore write)
                            onThemeChanged = { theme ->
                                scope.launch {
                                    themeManager.saveThemeMode(theme)
                                }
                            },
                            onColorThemeChanged = { colorTheme ->
                                scope.launch {
                                    themeManager.saveColorTheme(colorTheme)
                                }
                            }
                        )
                    }
                    // --- End Settings Screen Implementation ---


                    composable(
                        route = "DetailAlphabetScreen/{letter}",
                        arguments = listOf(
                            navArgument("letter") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ) { backStackEntry ->
                        val letter = backStackEntry.arguments?.getString("letter")
                        DetailAlphabetScreen(
                            letter = letter ?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(navController: NavController) {
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
                    // App Logo/Header with Gradient Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer // Use primaryContainer for theme consistency
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box( // Using theme color for background
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)), // Use onPrimary for contrast
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.iconapp),
                                    contentDescription = stringResource(R.string.app_logo_description), // Added resource
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary, // Use onPrimary for text color on primary/primaryContainer
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Items
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.about)) },
                        icon = { Icon(Icons.Default.Info, contentDescription = stringResource(R.string.about)) }, // Added content description
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("about") {
                                    popUpTo("main")
                                    launchSingleTop = true
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.settings)) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) }, // Added content description
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("settings") {
                                    popUpTo("main")
                                    launchSingleTop = true
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.privacy_policy)) },
                        icon = { Icon(painterResource(id = R.drawable.ic_privacy), contentDescription = stringResource(R.string.privacy_policy)) }, // Added content description
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                try {
                                    val privacyPolicyUrl = context.getString(R.string.privacy_policy_url) // Get URL from strings.xml
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                                    )
                                } catch (e: Exception) {
                                    Log.e("DrawerNav", "Could not open privacy policy URL", e)
                                    // Optionally show a Toast or SnackBar to the user
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.rate_app)) }, // Added Rate App item
                        icon = { Icon(Icons.Default.Star, contentDescription = stringResource(R.string.rate_app)) }, // Added content description
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                try {
                                    val playStoreUrl = "market://details?id=${context.packageName}"
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)))
                                } catch (e: Exception) {
                                    // Fallback to web browser if Play Store app not available
                                    val webUrl = "https://play.google.com/store/apps/details?id=${context.packageName}"
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
                                    Log.e("DrawerNav", "Could not open Play Store URL", e)
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )


                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
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
                                    putExtra(Intent.EXTRA_TEXT,
                                        context.getString(R.string.share_app_text, context.packageName)) // Use string resource with placeholder
                                    type = "text/plain"
                                }
                                try {
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                } catch (e: Exception) {
                                    Log.e("DrawerNav", "Could not share app", e)
                                    // Optionally show a Toast or SnackBar
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share_app)
                            )
                        }
                    )

                    // Add a Spacer with weight to push the footer to the bottom
                    Spacer(modifier = Modifier.weight(1f))

                    // Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "© 2025 Bekisma", // Or make this a string resource
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        // Get version name dynamically
                        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                        Text(
                            text = stringResource(R.string.version, packageInfo.versionName), // Use string resource
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        ) {
            // Splash Screen
            AnimatedVisibility(
                visible = showSplash,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient( // Use theme colors for splash background
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary // Or primaryContainer, depending on desired look
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val scaleAnim by animateFloatAsState(
                            targetValue = if (showSplash) 1f else 0.8f,
                            animationSpec = tween(1000), label = "SplashScaleAnimation"
                        )
                        Image(
                            painter = painterResource(id = R.drawable.iconapp),
                            contentDescription = stringResource(R.string.app_logo_description), // Added resource
                            modifier = Modifier
                                .size(150.dp)
                                .scale(scaleAnim),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary, // Use onPrimary for text color on primary background
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.learn_adlam_script_ease), // Using string resource
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Use onPrimary for text color
                        )
                    }
                }
            }

            // Main Screen Content (shown after splash)
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
                                        text = stringResource(R.string.learn_adlam_script_ease_short), // Using string resource (maybe a shorter version)
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = stringResource(R.string.menu_icon_desc), // Added content description resource
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                navigationIconContentColor = MaterialTheme.colorScheme.onSurface // Often better contrast than primary for menu icon
                            ),
                            scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                        )
                    },
                    bottomBar = {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface, // Use theme color
                            tonalElevation = 8.dp,
                            shadowElevation = 8.dp
                        ) {
                            BannerAdView() // Ensure BannerAdView also picks up the theme colors if it uses MaterialTheme internally
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient( // Use theme colors for background gradient
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
                            // Featured Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer // Use theme color
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
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
                                                text = stringResource(R.string.start_adlam_journey_title), // Using string resource
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer // Use theme color
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = stringResource(R.string.start_adlam_journey_description), // Using string resource
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) // Use theme color
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(
                                                onClick = { handleNavigation(navController, "alphabet") },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary // Use theme color
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(stringResource(R.string.get_started)) // Using string resource
                                                Icon(
                                                    Icons.Default.KeyboardArrowRight,
                                                    contentDescription = null,
                                                    modifier = Modifier.padding(start = 4.dp)
                                                )
                                            }
                                        }

                                        Image(
                                            painter = painterResource(id = R.drawable.abc64),
                                            contentDescription = stringResource(R.string.featured_image_desc), // Added resource
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .size(100.dp)
                                                .graphicsLayer {
                                                    rotationZ = -5f
                                                },
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.learning_modules), // Using string resource
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Menu Grid
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(menuItems) { item ->
                                    ElevatedMenuCard(
                                        image = painterResource(id = item.imageRes),
                                        title = stringResource(id = item.titleRes),
                                        subtitle = stringResource(id = item.subtitleRes),
                                        onClick = { handleNavigation(navController, item.destination) }
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
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .wrapContentHeight()
                .clickable {
                    onClick()
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface // Use theme color
            )
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
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)), // Use theme color
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = image,
                        contentDescription = title, // Content Description can be the title
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
                    color = MaterialTheme.colorScheme.onSurface // Use theme color
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Use theme color
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }

    private var navigationCounter = 0

    private fun handleNavigation(navController: NavController, destination: String) {
        try {
            // Increment navigation counter
            navigationCounter++

            // Show ad every N navigations if ad is loaded and time limit respected
            val AD_NAVIGATION_FREQUENCY = 4 // Show ad every 4 navigations
            // Ensure interstitialAdManager is initialized
            if (this::interstitialAdManager.isInitialized && navigationCounter >= AD_NAVIGATION_FREQUENCY && interstitialAdManager.canShowAd()) {
                Log.d("MainActivity", "Attempting to show ad on navigation to $destination")
                // Reset counter BEFORE showing the ad
                navigationCounter = 0

                interstitialAdManager.showAd(
                    onAdDismissed = {
                        Log.d("MainActivity", "Ad dismissed, navigating to $destination")
                        navController.navigate(destination)
                    }
                )
            } else {
                if (this::interstitialAdManager.isInitialized) {
                    if (navigationCounter < AD_NAVIGATION_FREQUENCY) {
                        Log.d("MainActivity", "Navigation count ($navigationCounter) below frequency ($AD_NAVIGATION_FREQUENCY), navigating to $destination")
                    } else {
                        Log.d("MainActivity", "Ad not ready or frequency limit (${interstitialAdManager.MIN_TIME_BETWEEN_ADS_MS/1000}s min), navigating directly to $destination")
                    }
                } else {
                    Log.d("MainActivity", "AdManager not initialized, navigating directly to $destination")
                }

                // Direct navigation without ad
                navController.navigate(destination)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Navigation failed", e)
            // Always navigate as a fallback
            navController.navigate(destination)
        }
    }


    // --- Improved Interstitial Ad Manager (Keep as is) ---
    // This class remains the same as provided previously.
    /**
     * Improved manager for interstitial ads with frequency limits
     * to respect AdMob policies
     */
    class ImprovedInterstitialAdManager(
        private val context: Context,
        private val adUnitId: String
    ) {
        private var interstitialAd: InterstitialAd? = null
        private var isLoading = false

        // Minimum time between ad displays (3 minutes)
        val MIN_TIME_BETWEEN_ADS_MS = TimeUnit.MINUTES.toMillis(3) // Made public for logging

        // Limit number of loads per session (e.g., 10 per hour)
        private val MAX_AD_LOADS_PER_HOUR = 10

        private var lastAdShowTime: Long = 0
        private var adLoadsThisHour = 0
        private var hourStartTime: Long = System.currentTimeMillis()

        init {
            // Reset load counter hourly
            resetAdLoadCounterHourly()
        }

        private fun resetAdLoadCounterHourly() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - hourStartTime >= TimeUnit.HOURS.toMillis(1)) {
                adLoadsThisHour = 0
                hourStartTime = currentTime
            }
        }

        fun preloadAd() {
            // Check if we can load a new ad
            if (shouldLoadNewAd()) {
                loadAd()
            }
        }

        private fun shouldLoadNewAd(): Boolean {
            resetAdLoadCounterHourly()

            // Don't load if already loading or if already loaded
            if (isLoading || interstitialAd != null) {
                Log.d("ImprovedAdManager", "Not loading ad: isLoading=$isLoading, adLoaded=${interstitialAd != null}")
                return false
            }

            // Check hourly load limit
            if (adLoadsThisHour >= MAX_AD_LOADS_PER_HOUR) {
                Log.d("ImprovedAdManager", "Hourly ad load limit reached (${adLoadsThisHour})")
                return false
            }
            Log.d("ImprovedAdManager", "Allowed to load ad. Ad loads this hour: ${adLoadsThisHour}")
            return true
        }

        private fun loadAd() {
            if (isLoading) return

            isLoading = true
            val adRequest = AdRequest.Builder().build()

            adLoadsThisHour++ // Increment counter when load is initiated

            InterstitialAd.load(
                context,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isLoading = false
                        Log.d("ImprovedAdManager", "Ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isLoading = false
                        Log.d("ImprovedAdManager", "Ad failed to load: ${error.message}. Retrying in 1 min.")

                        // Retry after delay in case of failure
                        scheduleAdLoadRetry()
                    }
                }
            )
        }

        private fun scheduleAdLoadRetry() {
            // Only schedule retry if not already loading or loaded and within hourly limit
            if (shouldLoadNewAd()) {
                // Use postDelayed on the main looper
                android.os.Handler(context.mainLooper).postDelayed({
                    Log.d("ImprovedAdManager", "Retrying ad load...")
                    loadAd() // This call includes the shouldLoadNewAd check again
                }, TimeUnit.MINUTES.toMillis(1))
            } else {
                Log.d("ImprovedAdManager", "Retry scheduled but conditions not met for load.")
            }
        }


        fun canShowAd(): Boolean {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastAd = currentTime - lastAdShowTime

            // Check if ad is loaded and enough time has passed
            val canShow = interstitialAd != null && timeSinceLastAd >= MIN_TIME_BETWEEN_ADS_MS
            if (!canShow) {
                val timeRemaining = MIN_TIME_BETWEEN_ADS_MS - timeSinceLastAd
                Log.d("ImprovedAdManager", "Cannot show ad. Ad Loaded: ${interstitialAd != null}, Time since last: ${timeSinceLastAd/1000}s (Need ${MIN_TIME_BETWEEN_ADS_MS/1000}s, Remaining: ${timeRemaining/1000}s)")
            }
            return canShow
        }

        fun showAd(onAdDismissed: () -> Unit) {
            val activity = context as? Activity // Safely cast context to Activity
            if (activity == null) {
                Log.e("ImprovedAdManager", "Context is not an Activity, cannot show ad")
                onAdDismissed() // Call dismissal callback immediately
                return
            }

            val ad = interstitialAd
            if (ad == null) {
                Log.e("ImprovedAdManager", "Ad is not loaded, cannot show ad")
                onAdDismissed() // Call dismissal callback immediately
                return
            }

            // Update last display time BEFORE showing the ad
            lastAdShowTime = System.currentTimeMillis()
            Log.d("ImprovedAdManager", "Showing ad. Last ad show time updated.")


            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("ImprovedAdManager", "Ad dismissed fullscreen content")
                    interstitialAd = null // Ad is consumed, set to null
                    onAdDismissed() // Execute the callback provided by the caller

                    // Schedule loading of the NEXT ad with a delay to avoid immediate reload
                    android.os.Handler(context.mainLooper).postDelayed({
                        preloadAd()
                    }, TimeUnit.SECONDS.toMillis(30)) // Wait 30 seconds before attempting to load the next ad
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.e("ImprovedAdManager", "Failed to show ad: ${error.message}")
                    interstitialAd = null // Ad failed, set to null
                    onAdDismissed() // Execute the callback provided by the caller

                    // Schedule a retry load after delay if showing failed
                    android.os.Handler(context.mainLooper).postDelayed({
                        preloadAd()
                    }, TimeUnit.MINUTES.toMillis(1)) // Wait 1 minute before attempting to load again
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d("ImprovedAdManager", "Ad showed fullscreen content.")
                }
            }

            try {
                ad.show(activity)
            } catch (e: Exception) {
                Log.e("ImprovedAdManager", "Exception while trying to show ad", e)
                interstitialAd = null // Clear ad reference on exception
                onAdDismissed() // Call callback on exception
            }
        }
    }
    // --- End Improved Interstitial Ad Manager ---


    data class MenuItem(
        val imageRes: Int,
        val titleRes: Int,
        val subtitleRes: Int,
        val destination: String
    )

    // Moved menuItems list outside the composable to avoid recreating on recomposition
    private val menuItems = listOf(
        MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet"),
        MenuItem(R.drawable.number, R.string.numbers, R.string.practice_adlam_numbers, "numbers"),
        MenuItem(R.drawable.writing, R.string.learn_to_write, R.string.improve_your_writing_skills, "writing"),
        MenuItem(R.drawable.quiz, R.string.quiz, R.string.test_your_knowledge, "quiz")
    )


    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun MainScreenPreview() {
        // Use the actual theme composable for previews
        AdlamFulfuldeTheme {
            // Provide mock navController for preview
            MainScreen(rememberNavController())
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MenuCardPreview() {
        // Use the actual theme composable for previews
        AdlamFulfuldeTheme {
            ElevatedMenuCard(
                image = painterResource(id = R.drawable.abc64),
                title = "Alphabet Learning",
                subtitle = "Discover the Adlam Alphabet",
                onClick = {}
            )
        }
    }
}