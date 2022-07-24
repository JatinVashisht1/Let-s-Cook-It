package com.jatinvashisht.letscookit.ui.recipe_list_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jatinvashisht.letscookit.core.Constants
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.pagination.RecipePaginator
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf<RecipeListScreenState>(RecipeListScreenState())
    val category = mutableStateOf<String>("")
    val imageUrl = mutableStateOf<String>("")
    private val _searchBoxState = mutableStateOf("")
    val searchBoxState: State<String> = _searchBoxState
    var searchJob: Job? = null

    init {
        category.value =
            savedStateHandle.get<String>(Constants.RECIPE_LIST_SCREEN_RECIPE_CATEGORY_KEY)!!
        imageUrl.value =
            savedStateHandle.get<String>(Constants.RECIPE_LIST_SCREEN_RECIPE_IMAGE_URL_KEY)!!
        val temp = URLDecoder.decode(imageUrl.value, StandardCharsets.UTF_8.toString())
        Log.d(
            "recipelistviewmodel",
            "category is ${category.value}, image url is ${imageUrl.value}, temp is $temp"
        )
    }

    val recipePaginator = searchRecipe("")

    init {
        viewModelScope.launch {
            loadNextItems()
        }
    }

    fun onSearchBoxValueChanged(newValue: String) {
        _searchBoxState.value = newValue.trim()
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            searchRecipe(_searchBoxState.value).reset()
            state = state.copy(page = 0, items = emptyList())
            searchRecipe(_searchBoxState.value).loadNextItems()
        }
    }

    suspend fun loadNextItems() {
        searchRecipe(searchBoxState.value).loadNextItems()
    }

    fun searchRecipe(recipe: String): RecipePaginator<Int, RecipeDtoItem> {
        return RecipePaginator<Int, RecipeDtoItem>(
            initialKey = state.page,
            onLoadUpdated = {
                state = state.copy(isLoading = it)
            },
            onRequest = { nextPage ->
                recipeRepository.getRecipesByCategory(
                    category = category.value,
                    page = state.page,
                    pageSize = 20,
                    fetchFromRemote = false,
                    recipe = recipe
                )
            },
            getNextKey = { items ->
                state.page + 1
            },
            onError = { throwable ->
                state = state.copy(
                    error = throwable?.localizedMessage
                        ?: "unable to load items, please try again later"
                )
            }
        ) { newItems, newKey ->
            state = state.copy(
                items = state.items + newItems,
                page = newKey,
                endReached = newItems.isEmpty(),
            )
        }
    }

    fun onClearSearchBoxButtonClicked() {
        _searchBoxState.value = ""
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            state = state.copy(page = 0, items = emptyList())
            val paginator = searchRecipe("")
            paginator.reset()
            paginator.loadNextItems()
        }
    }
}