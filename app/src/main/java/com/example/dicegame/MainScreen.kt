// Saifullah Haaris - w1902235 | 20212163

package com.example.dicegame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("new_game") { DiceGameScreen(navController = navController) } //navController = navController
        composable("about") { AboutScreen() }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("new_game") }) {
            Text("New Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("about") }) {
            Text("About")
        }
    }
}
