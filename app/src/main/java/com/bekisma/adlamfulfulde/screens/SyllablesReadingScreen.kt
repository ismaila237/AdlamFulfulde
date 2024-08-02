import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllablesReadingScreen(navController: NavController) {
    // Liste des consonnes Adlam
    val adlamConsonants = listOf(
        "𞤁", "𞤂", "𞤃", "𞤄", "𞤅", "𞤆", "𞤇", "𞤈",
        "𞤊", "𞤍", "𞤎", "𞤏", "𞤐", "𞤑", "𞤒",
        "𞤔", "𞤕", "𞤖", "𞤗", "𞤘", "𞤙", "𞤚", "𞤛", "𞤜", "𞤝",
        "𞤞", "𞤟", "𞤠", "𞤡",
    )

    // Liste des voyelles Adlam
        val adlamVowels = listOf("𞤀", "𞤉", "𞤋", "𞤌", "𞤓")

    var currentConsonantIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ADLAM SYLLABLES") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Affiche la consonne actuelle
            Text(
                text = stringResource(R.string.consonne, adlamConsonants[currentConsonantIndex]),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
            // Génère et affiche les syllabes pour la consonne actuelle
            adlamVowels.forEach { vowel ->
                Text(
                    text = "${adlamConsonants[currentConsonantIndex]}$vowel",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { if (currentConsonantIndex > 0) currentConsonantIndex-- },
                    enabled = currentConsonantIndex > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.previous))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if (currentConsonantIndex < adlamConsonants.size - 1) currentConsonantIndex++ },
                    enabled = currentConsonantIndex < adlamConsonants.size - 1,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.next))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SyllablesReadingScreenPreview() {
    val navController = rememberNavController()
    SyllablesReadingScreen(navController)
}
