package com.jatinvashisht.letscookit.ui.recipe_list_screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
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
//    val focusRequester = remember { FocusRequester() }
//
//    LaunchedEffect(key1 = Unit) {
//        focusRequester.requestFocus()
//    }

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
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
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

        if (state.isLoading) {
            item(1) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
                }
            }
        } 
        else if(state.error.isNullOrBlank()){
            item(2){
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = state.error.toString())
                }
            }
        }
        else {
            items(state.items.size) { index ->
                Log.d("HomeScreen", "item size is ${state.items.size} and index is $index")
                val item = state.items[index]

                // below statement is a part of side effect
                LaunchedEffect(key1 = index >= state.items.size - 5 && !state.endReached && !state.isLoading) {
                    Log.d("If tag", "entered if statement")
                    viewModel.loadNextItems()
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(MyPadding.small)
                ) {
                    SubcomposeAsyncImage(
                        model = item.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                            .graphicsLayer {
                                shape = RoundedCornerShape(MyPadding.medium)
                                clip = true
                            }
                            .clickable {
                                navController.navigate(Screen.RecipeScreen.route + "/${item.title}/${item.tag}") {
                                    launchSingleTop = true
                                }
                            },
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.Medium,
                    )
                    Text(
                        text = item.title,
                        fontFamily = lemonMilkFonts,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}