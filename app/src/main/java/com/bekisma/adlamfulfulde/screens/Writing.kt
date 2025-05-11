package com.bekisma.adlamfulfulde.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create // Icône pour Majuscules
import androidx.compose.material.icons.filled.Edit // Icône pour Minuscules
import androidx.compose.material.icons.outlined.Numbers // Icône pour Chiffres
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R
import com.bekisma.adlamfulfulde.ads.BannerAdView
// Assurez-vous que l'enum WritingType est accessible ici
// (il est défini dans WritingPracticeScreen.kt maintenant)
// import com.bekisma.adlamfulfulde.screens.WritingType // Décommentez si nécessaire

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            WritingTopAppBar(navController)
        }
    ) { innerPadding ->
        WritingScreenContent(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WritingTopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.learn_to_write),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
private fun WritingScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp) // Espace pour la bannière publicitaire en bas
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            AlphabetCard()
            WritingActions(navController)
            Spacer(modifier = Modifier.height(16.dp))
        }

        BannerAdView(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(50.dp)
        )
    }
}

@Composable
private fun AlphabetCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.the_alphabet),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.trace_and_learn_all_adlam_letters),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.th_adl_alp).trimIndent(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun WritingActions(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WritingActionButton(
            navController = navController,
            route = "writingPractice/${WritingType.UPPERCASE.name}", // Utilise l'enum de l'autre fichier
            text = stringResource(R.string.upper_case),
            icon = Icons.Default.Create
        )
        WritingActionButton(
            navController = navController,
            route = "writingPractice/${WritingType.LOWERCASE.name}",
            text = stringResource(R.string.lower_case),
            icon = Icons.Default.Edit
        )
        WritingActionButton(
            navController = navController,
            route = "writingPractice/${WritingType.NUMBERS.name}",
            text = stringResource(R.string.numbers),
            icon = Icons.Outlined.Numbers
        )
    }
}

@Composable
private fun WritingActionButton(
    navController: NavController,
    route: String,
    text: String,
    icon: ImageVector
) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "WritingScreen Dark")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "WritingScreen Light")
@Composable
fun WritingScreenPreview() {
    val navController = rememberNavController()
    // Theme { // Décommentez et utilisez votre thème si nécessaire pour la preview
    WritingScreen(navController)
    // }
}