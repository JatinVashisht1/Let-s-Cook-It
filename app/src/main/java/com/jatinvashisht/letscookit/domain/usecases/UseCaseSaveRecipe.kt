package com.jatinvashisht.letscookit.domain.usecases

import com.jatinvashisht.letscookit.core.Resource
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import javax.inject.Inject

class UseCaseSaveRecipe @Inject constructor (private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(recipeDtoItem: RecipeDtoItem): String {
        val recipeFromDatabase = recipeRepository.getLocalRecipeByTitle(title = recipeDtoItem.title)
        return when(recipeFromDatabase){
            is Resource.Error -> {
                "UNABLE to save recipe, try again"
            }
            else -> {
                val recipe = recipeFromDatabase.data
                if(recipe == null){
                    recipeRepository.saveRecipe(recipeDtoItem = recipeDtoItem)
                    "Recipe SAVED successfully"
                }else{
                    "Recipe ALREADY exist"
                }
            }
        }
    }
}