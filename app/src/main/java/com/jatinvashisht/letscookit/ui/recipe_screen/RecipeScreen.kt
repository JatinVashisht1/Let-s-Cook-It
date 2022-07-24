package com.jatinvashisht.letscookit.ui.recipe_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.jatinvashisht.letscookit.core.MyPadding
import com.jatinvashisht.letscookit.core.lemonMilkFonts
import com.jatinvashisht.letscookit.ui.custom_view.CustomShape

@Composable
fun RecipeScreen(
    navController: NavHostController,
    viewModel: RecipeScreenViewModel = hiltViewModel()
) {
    val screenState = viewModel.recipeState.value

    when {
        screenState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
            }
        }
        screenState.error.isNotBlank() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = screenState.error, color = MaterialTheme.colors.secondary)
            }
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(LocalConfiguration.current.screenHeightDp.dp / 2)
                            .graphicsLayer {
                                shadowElevation = 8.dp.toPx()
                                shape = CustomShape()
                                clip = true
                            }
                            .drawBehind {
                                drawRect(color = Color(0xFF000000))
                            },
//                        contentAlignment = Alignment.Center
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .drawBehind { drawRect(color = Color.Transparent) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
//                            Text(
//                                text = "Recipe Screen",
//                                fontFamily = lemonMilkFonts,
//                                fontWeight = FontWeight.Normal,
//                                style = MaterialTheme.typography.h6,
//                            )
                            IconButton(onClick = { navController.navigateUp() }, modifier = Modifier.padding(4.dp)) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "goto home screen",)
                            }

                            IconButton(onClick = {  }, modifier = Modifier.padding(4.dp)) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = "Save Recipe",)
                            }
                        }
                        SubcomposeAsyncImage(
                            model = screenState.recipe.imageUrl,
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(50.dp),
                                    color = MaterialTheme.colors.primaryVariant
                                )
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    this.alpha = 0.25f
                                    shadowElevation = 8.dp.toPx()
                                    clip = true
                                }
                                .align(Alignment.Center),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = screenState.recipe.title,
                            style = MaterialTheme.typography.h4,
                            fontWeight = FontWeight.ExtraLight,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onSurface,
                            fontFamily = lemonMilkFonts,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(MyPadding.medium))
                }

                item {
                    Text(
                        text = "Ingredients",
                        fontFamily = lemonMilkFonts,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.padding(horizontal = MyPadding.medium)
                    )
                    Spacer(modifier = Modifier.height(MyPadding.medium))
                }

                items(screenState.recipe.ingredient) { ingredient ->
                    val ingredientInFloat =
                        rememberSaveable { mutableStateOf(ingredient.quantity.toFloatOrNull()) }
                    val ingredientInString = if (ingredientInFloat.value == null) {
                        ""
                    } else {
                        "${ingredientInFloat.value} "
                    }
                    Text(
                        text = "$ingredientInString${ingredient.description}",
                        fontFamily = lemonMilkFonts,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(horizontal = MyPadding.medium)
                    )
                    Spacer(modifier = Modifier.height(MyPadding.medium))
                }

                item {
                    Text(
                        text = "Method",
                        fontFamily = lemonMilkFonts,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.padding(horizontal = MyPadding.medium)
                    )
                    Spacer(modifier = Modifier.height(MyPadding.medium))
                }

                items(screenState.recipe.method) { method ->
                    Text(
                        text = method,
                        fontFamily = lemonMilkFonts,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(horizontal = MyPadding.medium)
                    )
                    Spacer(modifier = Modifier.height(MyPadding.medium))
                }
            }
        }
    }
}