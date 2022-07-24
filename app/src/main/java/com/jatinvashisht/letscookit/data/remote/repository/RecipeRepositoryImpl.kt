package com.jatinvashisht.letscookit.data.remote.repository

import android.util.Log
import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.data.local.RecipeDatabase
import com.jatinvashisht.letscookit.data.local.RecipeEntity
import com.jatinvashisht.letscookit.data.mapper.toRecipeDtoItem
import com.jatinvashisht.letscookit.data.mapper.toRecipeEntity
import com.jatinvashisht.letscookit.data.remote.RecipeApi
import com.jatinvashisht.letscookit.data.remote.dto.categories.CategoryDtoItem
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeApi: RecipeApi,
    private val recipeDatabase: RecipeDatabase
) :
    RecipeRepository {
    private val recipeDao = recipeDatabase.dao
    override suspend fun getFirstFourRecipes(): Resource<List<RecipeDtoItem>> {
        try {
            val shouldJustLoadFromCache = recipeDao.searchRecipe("").isNotEmpty()
            val myRecipes: List<RecipeEntity> = if (!shouldJustLoadFromCache) {
                recipeDao.getFirstFourRecipes()
            } else {
                recipeDao.clearRecipes()
                val remoteEntities = recipeApi.getRecipeList("snacks")
                recipeDao.insertRecipes(remoteEntities.map {
                    it.toRecipeEntity()
                })
                recipeDao.getFirstFourRecipes()
            }
            val recipes = myRecipes.map {
                it.toRecipeDtoItem()
            }
            return Resource.Success<List<RecipeDtoItem>>(data = recipes)
        } catch (e: Exception) {
            Log.d("reciperepository", "unable to find first four recipes $e")
            return Resource.Error<List<RecipeDtoItem>>(error = "unable to find top recipes")
        }

    }

    override suspend fun getRecipeByTitle(title: String, category: String): Flow<Resource<RecipeDtoItem>> = flow{
        try {
            var recipeFromDb = recipeDao.getRecipeByTitle(recipeTitle = title.toString())
            Log.d("reciperepository", "recipeFromDb in get recipe by category is, $category")
            if(recipeFromDb == null){
                recipeDao.clearRecipes()
                val remoteEntities = recipeApi.getRecipeList(category)
                recipeDao.insertRecipes(remoteEntities.map {
                    it.toRecipeEntity()
                })
                recipeFromDb = recipeDao.getRecipeByTitle(recipeTitle = title)
            }
            val recipeEntity = recipeFromDb?.toRecipeDtoItem()?: RecipeDtoItem()
            emit(Resource.Success<RecipeDtoItem>(recipeEntity))
        } catch (e: Exception) {
            Log.d("reciperepository", "unable to get recipe by title, \n $e")
            emit(Resource.Error<RecipeDtoItem>(error = "unable to find recipe by title $title"))
        }
    }

    override suspend fun getCategories(): Flow<Resource<List<CategoryDtoItem>>> = flow {
        try {
            emit(Resource.Loading<List<CategoryDtoItem>>())
            val recipes = recipeApi.getCategory()
            emit(Resource.Success<List<CategoryDtoItem>>(data = recipes))
        } catch (e: Exception) {
            Log.d("reciperepository", "unable to get categories, $e")
            emit(Resource.Error<List<CategoryDtoItem>>("unable to load categories, please try again later"))
        }
    }

    override suspend fun getRecipesByCategory(
        recipe: String,
        category: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
    ): Resource<List<RecipeDtoItem>> {
        try {
            val shouldJustLoadFromCache =
                !fetchFromRemote && recipeDao.getRecipeByTag(category = category, recipe = recipe)
                    .isNotEmpty()
            val myRecipes: List<RecipeEntity> = if (!shouldJustLoadFromCache) {
                recipeDao.clearRecipes()
                val remoteEntities = recipeApi.getRecipeList(category)
                recipeDao.insertRecipes(remoteEntities.map {
                    it.toRecipeEntity()
                })
                recipeDao.getRecipeByTag(category = category, recipe = recipe)
            } else {
                Log.d("reciperepository", "inside should just load from cache")
                recipeDao.getRecipeByTag(category = category, recipe = recipe)
            }
            Log.d("reciperepository", "recipes size are ${myRecipes.size}")
            val recipes = myRecipes.map {
                it.toRecipeDtoItem()
            }
            val startingIndex = page * pageSize
            Log.d(
                "reciperepository",
                "starting index is $startingIndex, page is $page, pageSize is $pageSize, recipes size is ${recipes.size}"
            )
            val endingIndex = startingIndex + pageSize

            if (startingIndex < recipes.size) {
                return if (endingIndex < recipes.size) {
                    Log.d(
                        "reciperepository",
                        "starting index is $startingIndex, ending index is ${startingIndex + pageSize}, recipe is ${
                            recipes.slice(
                                startingIndex until startingIndex + pageSize
                            )
                        }"
                    )
                    Resource.Success<List<RecipeDtoItem>>(data = recipes.slice(startingIndex until startingIndex + pageSize))
                } else {
                    Resource.Success<List<RecipeDtoItem>>(data = recipes.slice(startingIndex until recipes.size))
                }
            } else {
                return Resource.Success<List<RecipeDtoItem>>(data = emptyList())
            }
        } catch (exception: Exception) {
            return Resource.Error<List<RecipeDtoItem>>("unable to load data, please try again later")
        }
    }

    override suspend fun getRecipes(
        recipe: String,
        page: Int,
        pageSize: Int,
        fetchFromRemote: Boolean,
    ): Resource<List<RecipeDtoItem>> {
        try {
            val shouldJustLoadFromCache =
                !fetchFromRemote && recipeDao.searchRecipe("").isNotEmpty()
            val myRecipes: List<RecipeEntity> = if (!shouldJustLoadFromCache) {

                recipeDao.clearRecipes()
                val remoteEntities = recipeApi.getRecipeList(recipe)
                recipeDao.insertRecipes(remoteEntities.map {
                    it.toRecipeEntity()
                })
                recipeDao.searchRecipe("")
            } else {
                Log.d("reciperepository", "inside should just load from cache")
                recipeDao.searchRecipe("")
            }
            val recipes = myRecipes.map {
                it.toRecipeDtoItem()
            }
            val startingIndex = page * pageSize
            Log.d(
                "reciperepository",
                "starting index is $startingIndex, page is $page, pageSize is $pageSize, recipes size is ${recipes.size}"
            )
            val endingIndex = startingIndex + pageSize

            if (startingIndex < recipes.size) {
                return if (endingIndex < recipes.size) {
                    Log.d(
                        "reciperepository",
                        "starting index is $startingIndex, ending index is ${startingIndex + pageSize}, recipe is ${
                            recipes.slice(
                                startingIndex until startingIndex + pageSize
                            )
                        }"
                    )
                    Resource.Success<List<RecipeDtoItem>>(data = recipes.slice(startingIndex until startingIndex + pageSize))
                } else {
                    Resource.Success<List<RecipeDtoItem>>(data = recipes.slice(startingIndex until recipes.size))
                }
            } else {
                return Resource.Success<List<RecipeDtoItem>>(data = emptyList())
            }
        } catch (exception: Exception) {
            return Resource.Error<List<RecipeDtoItem>>("unable to load data, please try again later")
        }
    }
}