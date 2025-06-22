package com.project.ZeeScraper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.*
import com.project.ZeeScraper.ui.theme.GameCalcTheme
import com.project.ZeeScraper.navigation.BottomNavigationBar
import com.project.ZeeScraper.navigation.NavigationHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameCalcTheme(darkTheme = true) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Gunakan AnimatedVisibility untuk menganimasikan BottomNavigationBar
                        AnimatedVisibility(
                            visible = currentRoute in listOf("Home", "Log"),
                            enter = slideInVertically(initialOffsetY = { it }), // Animasi masuk dari bawah
                            exit = slideOutVertically(targetOffsetY = { it })   // Animasi keluar ke bawah
                        ) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavigationHost(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Screens

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    GameCalcTheme(darkTheme = true) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                AnimatedVisibility(
                    visible = currentRoute in listOf("Home", "Log"),
                    enter = slideInVertically(initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 50)),
                    exit = slideOutVertically(targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 50))
                ) {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            NavigationHost(navController = navController, modifier = Modifier.padding(innerPadding))
        }

    }
}