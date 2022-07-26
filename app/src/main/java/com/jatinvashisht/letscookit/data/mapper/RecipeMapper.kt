package com.jatinvashisht.letscookit.data.mapper

import com.jatinvashisht.letscookit.data.local.LocalRecipeEntity
import com.jatinvashisht.letscookit.data.local.RecipeEntity
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem
import com.jatinvashisht.letscookit.domain.model.ModelLocalRecipe

fun RecipeEntity.toRecipeDtoItem(): RecipeDtoItem = RecipeDtoItem(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title,
)

fun RecipeDtoItem.toRecipeEntity(): RecipeEntity = RecipeEntity(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title
)

fun RecipeDtoItem.toLocalRecipeEntity(): LocalRecipeEntity = LocalRecipeEntity(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title
)

fun LocalRecipeEntity.toModelLocalRecipe(): ModelLocalRecipe = ModelLocalRecipe(
    imageUrl = imageUrl,
    ingredient = ingredient,
    method = method,
    tag = tag,
    title = title
)
