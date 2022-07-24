package com.jatinvashisht.letscookit.ui.recipe_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jatinvashisht.letscookit.core.Constants
import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import com.jatinvashisht.letscookit.domain.usecases.UseCaseGetRecipeByTitle
import com.jatinvashisht.letscookit.ui.recipe_screen.components.RecipeScreenState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class RecipeScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRecipeByTitleUseCase: UseCaseGetRecipeByTitle,
    private val recipeRepository: RecipeRepository,
) : ViewModel(){
    private val _recipeState = mutableStateOf<RecipeScreenState>(RecipeScreenState())
    val recipeState: State<RecipeScreenState> = _recipeState

    private val recipeTitle = mutableStateOf("")
    private val recipeCategory = mutableStateOf("")



    init {
        viewModelScope.launch {
            val recipe = savedStateHandle.get<String>(Constants.RECIPE_SCREEN_RECIPE_TITLE_KEY)
            recipeTitle.value = recipe?: ""

            val category = savedStateHandle.get<String>(Constants.RECIPE_SCREEN_RECIPE_CATEGORY_KEY)
            recipeCategory.value = category?: ""
            Log.d("recipescreenviewmodel", "recipe is $recipe")
            getRecipe()
        }
    }

    private suspend fun getRecipe(){

//        val result = recipeRepository.getRecipeByTitle(title = recipeTitle.value)
//        when(result){
//            is Resource.Error -> {
//                _recipeState.value = _recipeState.value.copy(isLoading = false, error = "Unable to load recipe. Please try again later")
//            }
//            is Resource.Loading -> {
//                _recipeState.value = _recipeState.value.copy(isLoading = true, error = "",)
//            }
//            is Resource.Success -> {
//                _recipeState.value = _recipeState.value.copy(isLoading = false, recipe = result.data?: RecipeDtoItem())
//            }
//        }

        recipeRepository.getRecipeByTitle(title = recipeTitle.value, category = recipeCategory.value).collectLatest { recipeResult->
            when(recipeResult){
                is Resource.Error -> {
                    _recipeState.value = _recipeState.value.copy(isLoading = false, error = "Unable to load recipe. Please try again later")
                }
                is Resource.Loading -> {
                    _recipeState.value = _recipeState.value.copy(isLoading = true, error = "",)
                }
                is Resource.Success -> {
                    _recipeState.value = _recipeState.value.copy(isLoading = false, recipe = recipeResult.data?: RecipeDtoItem())
                }
            }
        }
    }

}