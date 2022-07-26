package com.jatinvashisht.letscookit.domain.repository

import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.data.remote.dto.categories.CategoryDtoItem
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun getRecipes(
        recipe: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean
    ): Resource<List<RecipeDtoItem>>

    suspend fun getFirstFourRecipes(): Resource<List<RecipeDtoItem>>

    suspend fun getRecipeByTitle(title: String, category: String): Flow<Resource<RecipeDtoItem>>

    suspend fun getCategories(): Flow<Resource<List<CategoryDtoItem>>>

    suspend fun getRecipesByCategory(
        recipe: String,
        category: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
    ): Resource<List<RecipeDtoItem>>


}