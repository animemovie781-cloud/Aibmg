package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Search : Screen
    
    @Serializable
    data class Detail(val id: String) : Screen
    
    @Serializable
    data class Player(val id: String, val title: String) : Screen
}
