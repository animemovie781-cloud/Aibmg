package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ui.detail.DetailScreen
import com.example.ui.home.HomeScreen
import com.example.ui.navigation.Screen
import com.example.ui.player.PlayerScreen
import com.example.ui.search.SearchScreen
import com.example.ui.theme.CineVerseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineVerseTheme {
                CineVerseApp()
            }
        }
    }
}

@Composable
fun CineVerseApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onMediaClick = { id -> navController.navigate(Screen.Detail(id)) },
                onSearchClick = { navController.navigate(Screen.Search) }
            )
        }
        
        composable<Screen.Search> {
            SearchScreen(
                onMediaClick = { id -> navController.navigate(Screen.Detail(id)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.Detail> { backStackEntry ->
            val detail: Screen.Detail = backStackEntry.toRoute()
            DetailScreen(
                mediaId = detail.id,
                onBackClick = { navController.popBackStack() },
                onPlayClick = { id, title -> navController.navigate(Screen.Player(id, title)) }
            )
        }

        composable<Screen.Player> { backStackEntry ->
            val player: Screen.Player = backStackEntry.toRoute()
            PlayerScreen(
                id = player.id,
                title = player.title,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
