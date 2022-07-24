package com.jatinvashisht.letscookit.core

sealed class Screen(val route: String){
    object HomeScreen: Screen(route = "homescreen")
    object RecipeScreen: Screen(route = "recipescreen")
    object RecipeListScreen: Screen(route = "recipelistscreen")
}
