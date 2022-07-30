@file:OptIn(ExperimentalFoundationApi::class)

package com.jatinvashisht.letscookit.ui.recipe_list_screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.jatinvashisht.letscookit.core.MyPadding
import com.jatinvashisht.letscookit.core.Screen
import com.jatinvashisht.letscookit.core.lemonMilkFonts
import com.jatinvashisht.letscookit.ui.custom_view.CustomShape
import com.jatinvashisht.letscookit.ui.recipe_screen.RecipeScreenViewModel
import com.jatinvashisht.letscookit.ui.recipe_screen.components.RecipeScreenState
import kotlinx.coroutines.flow.collectLatest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state = viewModel.state
    val showSearchBoxState = rememberSaveable { mutableStateOf(false) }
    val searchBoxState = viewModel.searchBoxState.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val isEditModeOn = viewModel.isEditModeOn.value
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        viewModel.toRecipeListScreenEvents.collectLatest {
            when (it) {
                ToRecipeListScreenEvents.NavigateUp -> navController.navigateUp()
                is ToRecipeListScreenEvents.ShowSnackbar -> scaffoldState.snackbarHostState.showSnackbar(
                    it.message
                )
            }
        }
    }

    BackHandler {
        if (isEditModeOn) {
            viewModel.receiveFromRecipeListScreenEvents(FromRecipeListScreenEvents.DisableEditMode)
        } else {
            viewModel.onEvent(ToRecipeListScreenEvents.NavigateUp)
        }
    }

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
                    SubcomposeAsyncImage(
                        model = viewModel.imageUrl.value,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(50.dp)
                                    .size(50.dp),
                                color = MaterialTheme.colors.primaryVariant
                            )
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                this.alpha = 0.25f
                                shadowElevation = 8.dp.toPx()
//                                shape = CustomShape()
                                clip = true
                            }
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .drawBehind {
                                drawRect(Color.Transparent)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(visible = isEditModeOn) {
                            IconButton(
                                onClick = {
                                    viewModel.receiveFromRecipeListScreenEvents(
                                        FromRecipeListScreenEvents.DeleteButtonClicked
                                    )
                                },
                                modifier = Modifier.padding(MyPadding.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "goto home screen"
                                )
                            }
                        }

                        AnimatedVisibility(visible = !isEditModeOn) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = { navController.navigateUp() },
                                    modifier = Modifier.padding(MyPadding.small)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "goto home screen"
                                    )
                                }
                                Spacer(modifier = Modifier.width(MyPadding.medium))
                                AnimatedVisibility(visible = !showSearchBoxState.value) {
                                    IconButton(onClick = { showSearchBoxState.value = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "search recipe"
                                        )
                                    }
                                }
                                AnimatedVisibility(visible = showSearchBoxState.value) {
                                    OutlinedTextField(
                                        value = searchBoxState,
                                        onValueChange = viewModel::onSearchBoxValueChanged,
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f),
//                                .focusRequester(focusRequester = focusRequester),
                                        label = {
                                            Text("Search ${viewModel.category.value} recipes")
                                        },
                                        placeholder = {
                                            Text("Search ${viewModel.category.value} recipes")
                                        },
                                        keyboardActions = KeyboardActions(onSearch = {
                                            keyboardController?.hide()
//                                showSearchBoxState.value = false
                                        }),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                                    )
                                    Spacer(modifier = Modifier.width(MyPadding.medium))

                                }
                                AnimatedVisibility(visible = showSearchBoxState.value) {
                                    IconButton(
                                        onClick = {
                                            showSearchBoxState.value = false
                                            viewModel.onClearSearchBoxButtonClicked()
                                        },
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = viewModel.category.value,
                        style = MaterialTheme.typography.h4,
                        fontWeight = FontWeight.ExtraLight,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface,
                        fontFamily = lemonMilkFonts,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            items(state.items.size) { index ->
                Log.d("HomeScreen", "item size is ${state.items.size} and index is $index")
                val item = state.items[index]

                // below statement is a part of side effect
                LaunchedEffect(key1 = Unit) {
                    if (index >= state.items.size - 5 && !state.endReached && !state.isLoading) {
                        Log.d("recipelistscreen", "entered if statement")
                        viewModel.loadNextItems()
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(MyPadding.small)
                        .combinedClickable(onLongClick = {
                            if (!isEditModeOn && viewModel.getSavedRecipes.value) {
                                viewModel.isEditModeOn.value = true
                            }
                        }) {
                            if (!isEditModeOn) {
                                val title = URLEncoder.encode(item.title, StandardCharsets.UTF_8.toString())
                                val tag = URLEncoder.encode(item.tag, StandardCharsets.UTF_8.toString())
                                navController.navigate(Screen.RecipeScreen.route + "/${title}/${tag}/${viewModel.getSavedRecipes.value}") {
                                    launchSingleTop = true
                                }
                            } else {
                                viewModel.onSelectRadioButtonClicked(item.title)
                            }
                        }
                ) {
                    SubcomposeAsyncImage(
                        model = item.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                            .graphicsLayer {
//                            alpha = if(isEditModeOn) 0.7f else 1.0f
                                shape = RoundedCornerShape(MyPadding.medium)
                                clip = true
                            },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.Medium,
                        colorFilter = if (isEditModeOn) ColorFilter.tint(
                            Color.DarkGray,
                            BlendMode.Multiply
                        ) else null
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = item.title,
                            fontFamily = lemonMilkFonts,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .animateItemPlacement(tween(300))
                        )
                        AnimatedVisibility(isEditModeOn) {
                            RadioButton(
                                selected = viewModel.recipesToBeDeleted.contains(item.title),
                                onClick = {
                                    viewModel.onSelectRadioButtonClicked(title = item.title)
                                })
                        }
                    }
                }
            }
            if (state.isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.primaryVariant,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}