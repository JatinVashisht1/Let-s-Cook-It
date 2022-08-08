package com.jatinvashisht.letscookit.ui.recipe_screen

import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
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
import com.jatinvashisht.letscookit.data.remote.dto.recipes.Ingredient
import com.jatinvashisht.letscookit.ui.custom_view.CustomShape
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecipeScreen(
    navController: NavHostController,
    viewModel: RecipeScreenViewModel = hiltViewModel()
) {
    val screenState = viewModel.recipeState.value
    val scaffoldState = rememberScaffoldState()
    val numberOfPersons = viewModel.numberOfPersons.value
    val ingredients = screenState.recipe.ingredient
    LaunchedEffect(key1 = Unit){
        viewModel.uiRecipeScreenEvents.collectLatest { event->
            when(event){
                is RecipeScreenEvents.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

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
            Scaffold(scaffoldState = scaffoldState) {
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
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .drawBehind { drawRect(color = Color.Transparent) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "goto home screen",
                                    )
                                }

                                IconButton(
                                    onClick = viewModel::onSaveRecipeButtonClicked,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Save Recipe",
                                    )
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
                        NumberOfPersonSlider(
                            currentValue = numberOfPersons,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MyPadding.medium)
                        ) {
                            viewModel.onSliderValueChanged(it)
                            Log.d("recipescreen", "number of persons $numberOfPersons")
                        }
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


                    items(ingredients) { ingredient ->
                        val ingredientQuantity = ingredient.quantity.toFloatOrNull()
                            ?.times(viewModel.numberOfPersons.value)
                        val modifiedIngredient = if(ingredientQuantity == null){
                            ""
                        }else{
                            "$ingredientQuantity "
                        }
                        Text(
                            text = " ${modifiedIngredient}${ingredient.description}",
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
}

@Composable
fun NumberOfPersonSlider(
    modifier: Modifier = Modifier,
    currentValue: Int,
    onValueChanged: (Float) -> Unit,
) {
    Log.d("recipescreen", "current value is $currentValue")
    Column(modifier = modifier) {
    Text(
        text = "Number of persons",
        fontWeight = FontWeight.Medium,
        fontFamily = lemonMilkFonts,
        style = MaterialTheme.typography.h5)
        Row(
//            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                value = currentValue.toFloat() / 10f,
                onValueChange = onValueChanged,
                modifier = Modifier.fillMaxWidth(0.75f),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.primaryVariant,
                    activeTrackColor = MaterialTheme.colors.primaryVariant,
                )
            )
            Spacer(modifier = Modifier.width(MyPadding.small))
            Text(text = "$currentValue", modifier = Modifier.fillMaxWidth())
        }
    }
}