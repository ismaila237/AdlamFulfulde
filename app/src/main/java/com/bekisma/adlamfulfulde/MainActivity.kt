// package com.bekisma.adlamfulfulde

package com.bekisma.adlamfulfulde

import SyllablesReadingScreen
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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

class MainActivity : ComponentActivity() {
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
                    composable("writingUpperCase") { WritingUpperCaseScreen(navController) } // Correct spelling
                    composable("writingNumber") { WritingNumberScreen(navController) }
                    composable("writingLowerCase") { WritingLowerCaseScreen(navController) }
                    composable("writing") { WritingScreen(navController) }
                    composable("syllables") { SyllablesScreen(navController) }
                    composable("quiz") { QuizScreen(navController) }
                    composable("syllables_reading") { SyllablesReadingScreen(navController) }
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                Divider()
                // Ajoutez d'autres éléments de navigation ici si nécessaire
            }
        }
    ) {
        Scaffold(
            topBar = { TopBar(drawerState, scope) },
            content = { paddingValues ->
                LazyColumn(modifier = Modifier.padding(paddingValues)) {
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
//                    item {
//                        MenuCard(
//                            image = painterResource(id = R.drawable.syllabus),
//                            title = stringResource(id = R.string.the_syllables),
//                            onClick = { navController.navigate("syllables") }
//                        )
//                    }
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
        targetValue = if (clicked) 12.dp else 4.dp
    )

    val padding by animateDpAsState(
        targetValue = if (clicked) 24.dp else 16.dp
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = {
                clicked = !clicked
                interstitialAdManager.showAd {
                    onClick()
                }
            }),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = image,
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = title,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(drawerState: DrawerState, scope: kotlinx.coroutines.CoroutineScope) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun WritingPreview() {
    val navController = rememberNavController()
    MainScreen(navController)
}
