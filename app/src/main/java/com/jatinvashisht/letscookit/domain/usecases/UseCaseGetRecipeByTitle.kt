package com.jatinvashisht.letscookit.domain.usecases

import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UseCaseGetRecipeByTitle @Inject constructor(private val recipeRepo: RecipeRepository) {
//    suspend operator fun invoke(title: String): Flow<Resource<RecipeDtoItem>> = flow{
//        emit(Resource.Loading<RecipeDtoItem>())
//        when(val recipe = recipeRepo.getRecipeByTitle(title = title)){
//            is Resource.Error -> {
//                emit(Resource.Error<RecipeDtoItem>("unable to get recipe"))
//            }
//            is Resource.Loading -> {
//                emit(Resource.Loading<RecipeDtoItem>())
//            }
//            is Resource.Success -> {
//                emit(Resource.Success<RecipeDtoItem>(data = recipe.data?: RecipeDtoItem()))
//            }
//        }
//    }
}