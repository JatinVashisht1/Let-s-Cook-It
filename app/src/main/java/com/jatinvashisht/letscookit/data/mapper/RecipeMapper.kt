package com.jatinvashisht.letscookit.data.mapper

import com.jatinvashisht.letscookit.data.local.RecipeEntity
import com.jatinvashisht.letscookit.data.remote.dto.recipes.RecipeDtoItem

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