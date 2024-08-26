package com.bekisma.adlamfulfulde

import android.content.Intent
import android.net.Uri
import SyllablesReadingScreen
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bekisma.adlamfulfulde.ads.BannerAdView
import com.bekisma.adlamfulfulde.ads.InterstitialAdManager
import com.bekisma.adlamfulfulde.screens.*
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import kotlinx.coroutines.launch
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope

class MainActivity() : ComponentActivity(), Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)

        setContent {
            AdlamFulfuldeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") { MainScreen(navController) }
                    composable("alphabet") { AlphabetScreen(navController) }
                    composable(
                        route = "DetailAlphabetScreen/{letter}",
                        arguments = listOf(navArgument("letter") { type = NavType.StringType })
                    ) { backStackEntry ->
                        DetailAlphabetScreen(
                            letter = backStackEntry.arguments?.getString("letter") ?: "",
                            navController = navController
                        )
                    }
                    composable("numbers") { NumbersScreen(navController) }
                    composable("writingUpperCase") { WritingUpperCaseScreen(navController) }
                    composable("writingNumber") { WritingNumberScreen(navController) }
                    composable("writingLowerCase") { WritingLowerCaseScreen(navController) }
                    composable("writing") { WritingScreen(navController) }
                    composable("syllables") { SyllablesScreen(navController) }
                    composable("quiz") { QuizScreen(navController) }
                    composable("syllables_reading") { SyllablesReadingScreen(navController) }
                    composable("about") { AboutScreen(navController)
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
                    Text(
                        "ADLAM",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider()
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.about)) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                navController.navigate("about")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.privacy_policys)) },
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.share_app)) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this amazing Adlam Fulfulde learning app: https://play.google.com/store/apps/details?id=com.bekisma.adlamfulfulde")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.share_app)
                            )
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = { TopBar(drawerState, scope) },
                content = { paddingValues ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            BannerAdView()
                        }
                        item {
                            MenuCard(
                                image = painterResource(id = R.drawable.abc64),
                                title = stringResource(id = R.string.alphabet_learning),
                                onClick = { navController.navigate("alphabet") }
                            )
                        }
                        item {
                            MenuCard(
                                image = painterResource(id = R.drawable.number),
                                title = stringResource(id = R.string.numbers),
                                onClick = { navController.navigate("numbers") }
                            )
                        }
                        item {
                            MenuCard(
                                image = painterResource(id = R.drawable.writing),
                                title = stringResource(id = R.string.learn_to_write),
                                onClick = { navController.navigate("writing") }
                            )
                        }
                        item {
                            MenuCard(
                                image = painterResource(id = R.drawable.quiz),
                                title = stringResource(id = R.string.quiz),
                                onClick = { navController.navigate("quiz") }
                            )
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun MenuCard(
        image: Painter,
        title: String,
        onClick: () -> Unit
    ) {
        val context = LocalContext.current
        val adUnitId = stringResource(id = R.string.ad_mob_interstitial_id)
        var clicked by remember { mutableStateOf(false) }
        val interstitialAdManager = remember { InterstitialAdManager(context, adUnitId) }

        LaunchedEffect(Unit) {
            interstitialAdManager.loadAd { }
        }

        val elevation by animateDpAsState(
            targetValue = if (clicked) 8.dp else 4.dp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable(onClick = {
                    clicked = !clicked
                    interstitialAdManager.showAd {
                        onClick()
                    }
                }),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = image,
                    contentDescription = title,
                    modifier = Modifier
                        .size(56.dp)
                        .padding(end = 16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(drawerState: DrawerState, scope: CoroutineScope) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                }) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        )
    }
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun WritingPreview() {
    val navController = rememberNavController()
    MainScreen(navController)
}

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
}
