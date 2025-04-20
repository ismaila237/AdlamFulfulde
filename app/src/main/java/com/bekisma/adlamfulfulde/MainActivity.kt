package com.bekisma.adlamfulfulde

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var interstitialAdManager: ImprovedInterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AdMob
        MobileAds.initialize(this) {
            interstitialAdManager = ImprovedInterstitialAdManager(this, getString(R.string.ad_mob_interstitial_id))
            interstitialAdManager.preloadAd()
        }

        setContent {
            AdlamFulfuldeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") { MainScreen(navController) }
                    composable("alphabet") { AlphabetScreen(navController) }
                    composable("numbers") { NumbersScreen(navController) }
                    composable("writing") { WritingScreen(navController) }
                    composable("quiz") { QuizScreen(navController) }
                    composable("about") { AboutScreen(navController) }
                    composable("writingUpperCase") { WritingUpperCaseScreen(navController) }
                    composable("writingNumber") { WritingNumberScreen(navController) }
                    composable("writingLowerCase") { WritingLowerCaseScreen(navController) }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            onThemeChanged = { theme ->
                                // Votre logique pour changer le thème
                                // Par exemple, vous pourriez la référencer depuis MainActivity
                            }
                        )
                    }

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

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    // App Logo/Header with Gradient Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.iconapp),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(100.dp)
                            )
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.about)) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("about")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.privacy_policy)) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://adlamfulfulde-8a54a.web.app/privacy/privacy.html")
                                    )
                                )
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
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
                                        "Check out this amazing Adlam Fulfulde learning app: " +
                                                "https://play.google.com/store/apps/details?id=com.bekisma.adlamfulfulde")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, null))
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
//                    NavigationDrawerItem(
//                        label = { Text(stringResource(R.string.settings)) },
//                        selected = false,
//                        onClick = {
//                            scope.launch {
//                                drawerState.close()
//                                navController.navigate("settings")
//                            }
//                        },
//                        modifier = Modifier.padding(horizontal = 12.dp),
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Default.Settings,
//                                contentDescription = stringResource(R.string.settings)
//                            )
//                        }
//                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                bottomBar = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        BannerAdView()
                    }
                }
            ) { paddingValues ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(menuItems) { item ->
                        MenuCard(
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

    @Composable
    fun MenuCard(
        image: Painter,
        title: String,
        subtitle: String,
        onClick: () -> Unit
    ) {
        var isPressed by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = {
                        isPressed = true
                        onClick()
                    }
                ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isPressed) 8.dp else 4.dp,
                pressedElevation = 12.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }

    // Variable pour suivre le nombre de navigations
    private var navigationCounter = 0

    private fun handleNavigation(navController: NavController, destination: String) {
        try {
            // Incrémenter le compteur de navigation
            navigationCounter++

            // Afficher une annonce tous les 4 navigations et si l'annonce est chargée
            if (navigationCounter >= 4 && interstitialAdManager.canShowAd()) {
                // Réinitialiser le compteur
                navigationCounter = 0

                interstitialAdManager.showAd(
                    onAdDismissed = {
                        navController.navigate(destination)
                        // Ne pas recharger immédiatement, le gestionnaire s'en chargera au bon moment
                    }
                )
            } else {
                // Navigation directe sans annonce
                navController.navigate(destination)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Navigation failed", e)
            navController.navigate(destination)
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun MainScreenPreview() {
        AdlamFulfuldeTheme {
            MainScreen(rememberNavController())
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MenuCardPreview() {
        AdlamFulfuldeTheme {
            MenuCard(
                image = painterResource(id = R.drawable.abc64),
                title = "Alphabet Learning",
                subtitle = "Discover the Adlam Alphabet",
                onClick = {}
            )
        }
    }
}

/**
 * Gestionnaire amélioré pour les annonces interstitielles avec des limites de fréquence
 * pour respecter les politiques AdMob
 */
class ImprovedInterstitialAdManager(
    private val context: Context,
    private val adUnitId: String
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    // Temps minimum entre les affichages d'annonces (3 minutes)
    private val MIN_TIME_BETWEEN_ADS_MS = TimeUnit.MINUTES.toMillis(3)

    // Limite le nombre de chargements par session
    private val MAX_AD_LOADS_PER_HOUR = 10

    private var lastAdShowTime: Long = 0
    private var adLoadsThisHour = 0
    private var hourStartTime: Long = System.currentTimeMillis()

    init {
        // Réinitialiser le compteur de chargements toutes les heures
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
        // Vérifier si on peut charger une nouvelle annonce
        if (shouldLoadNewAd()) {
            loadAd()
        }
    }

    private fun shouldLoadNewAd(): Boolean {
        resetAdLoadCounterHourly()

        // Ne pas charger si on est déjà en train de charger ou si on a atteint la limite
        if (isLoading || interstitialAd != null) return false

        // Vérifier la limite de chargements par heure
        if (adLoadsThisHour >= MAX_AD_LOADS_PER_HOUR) {
            Log.d("ImprovedAdManager", "Hourly ad load limit reached")
            return false
        }

        return true
    }

    private fun loadAd() {
        if (isLoading) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        adLoadsThisHour++

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
                    Log.d("ImprovedAdManager", "Ad failed to load: ${error.message}")

                    // En cas d'échec, on retentera après un délai
                    scheduleAdLoadRetry()
                }
            }
        )
    }

    private fun scheduleAdLoadRetry() {
        // Ne pas retry immédiatement, attendre au moins 1 minute
        android.os.Handler(context.mainLooper).postDelayed({
            if (shouldLoadNewAd()) {
                loadAd()
            }
        }, TimeUnit.MINUTES.toMillis(1))
    }

    fun canShowAd(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdShowTime

        // Vérifier si une annonce est chargée et si assez de temps s'est écoulé
        return interstitialAd != null && timeSinceLastAd >= MIN_TIME_BETWEEN_ADS_MS
    }

    fun showAd(onAdDismissed: () -> Unit) {
        val activity = context as? Activity ?: return
        val ad = interstitialAd ?: return

        // Mettre à jour le temps du dernier affichage
        lastAdShowTime = System.currentTimeMillis()

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onAdDismissed()

                // Planifier le chargement de la prochaine annonce avec un délai
                android.os.Handler(context.mainLooper).postDelayed({
                    preloadAd()
                }, TimeUnit.MINUTES.toMillis(1))
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                onAdDismissed()
                Log.e("ImprovedAdManager", "Failed to show ad: ${error.message}")

                // Recharger après un délai en cas d'échec
                android.os.Handler(context.mainLooper).postDelayed({
                    preloadAd()
                }, TimeUnit.MINUTES.toMillis(1))
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("ImprovedAdManager", "Ad shown successfully")
            }
        }

        try {
            ad.show(activity)
        } catch (e: Exception) {
            Log.e("ImprovedAdManager", "Failed to show ad", e)
            interstitialAd = null
            onAdDismissed()
        }
    }
}

data class MenuItem(
    val imageRes: Int,
    val titleRes: Int,
    val subtitleRes: Int,
    val destination: String
)

val menuItems = listOf(
    MenuItem(R.drawable.abc64, R.string.alphabet_learning, R.string.discover_the_adlam_alphabet, "alphabet"),
    MenuItem(R.drawable.number, R.string.numbers, R.string.practice_adlam_numbers, "numbers"),
    MenuItem(R.drawable.writing, R.string.learn_to_write, R.string.improve_your_writing_skills, "writing"),
    MenuItem(R.drawable.quiz, R.string.quiz, R.string.test_your_knowledge, "quiz")
)