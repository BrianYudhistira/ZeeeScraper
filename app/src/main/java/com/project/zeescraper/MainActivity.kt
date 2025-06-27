package com.project.zeescraper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.*
import com.project.zeescraper.ui.theme.GameCalcTheme
import com.project.zeescraper.navigation.BottomNavigationBar
import com.project.zeescraper.navigation.NavigationHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameCalcTheme(darkTheme = true) {
                MainContent()
            }
        }
    }
}

@Composable
private fun MainContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes where bottom bar should be visible
    val bottomBarRoutes = listOf("Home", "Log")
    val showBottomBar = bottomBarRoutes.any { currentRoute?.startsWith(it) == true }
    val lastHomeId = rememberSaveable { mutableStateOf(3) }


    LaunchedEffect(lastHomeId.value) {
        Log.d("MainContent", "lastHomeId changed to: ${lastHomeId.value}")
    }

    // Log untuk memantau currentRoute
    LaunchedEffect(currentRoute) {
        Log.d("MainContent", "Current route: $currentRoute")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                BottomNavigationBar(navController, lastHomeId)
            }
        }
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            lastHomeId
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    GameCalcTheme(darkTheme = true) {
        MainContent()
    }
}