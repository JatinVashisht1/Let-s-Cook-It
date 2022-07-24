package com.jatinvashisht.letscookit.ui.home_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.jatinvashisht.letscookit.core.MyPadding
import com.jatinvashisht.letscookit.core.Screen
import com.jatinvashisht.letscookit.core.lemonMilkFonts
import com.jatinvashisht.letscookit.ui.custom_view.CustomShape
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val topRecipesState by viewModel.topRecipes
    val categoriesState by viewModel.categoriesState
    LazyColumn() {
        item(1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=481&q=80",
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
//                                shape = CustomShape()
                                clip = true
                            },
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Seems like you are hungry, let's get you some food",
                        style = MaterialTheme.typography.h4,
                        fontWeight = FontWeight.ExtraLight,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onSurface,
                        fontFamily = lemonMilkFonts
                    )
                }
            }
        }
        item(2) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Top Recipes",
                    fontFamily = lemonMilkFonts,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(MyPadding.medium),
                    style = MaterialTheme.typography.h5
                )
                IconButton(onClick = viewModel::refreshTopRecipes) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh top recipes"
                    )
                }
            }
        }
        item(3) {
            when {
                topRecipesState.loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
                    }
                }
                topRecipesState.error.isNotBlank() -> {
                    Text(text = topRecipesState.error, color = Color.Yellow)
                }
                else -> {
                    LazyRow(verticalAlignment = Alignment.CenterVertically) {
                        items(topRecipesState.recipes) { item ->
                            Column(
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(170.dp)
                                    .padding(horizontal = MyPadding.medium)
                                    .clickable {
                                        navController.navigate(Screen.RecipeScreen.route + "/${item.title}/${item.tag}") {
                                            launchSingleTop = true
                                        }
                                    }
                            )
                            {
                                SubcomposeAsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.6f)
                                        .graphicsLayer {
                                            shape = RoundedCornerShape(MyPadding.medium)
                                            clip = true
                                        },
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colors.primaryVariant
                                        )
                                    },
                                    filterQuality = FilterQuality.Medium,
                                )
                                Spacer(modifier = Modifier.width(MyPadding.small))
                                Text(
                                    text = item.title,
                                    fontFamily = lemonMilkFonts,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                )
                                Spacer(modifier = Modifier.width(MyPadding.small))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(MyPadding.medium))
                }
            }
        }

        item(4) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Categories",
                    fontFamily = lemonMilkFonts,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(MyPadding.medium),
                    style = MaterialTheme.typography.h5
                )
                IconButton(onClick = viewModel::refreshCategories) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh categories"
                    )
                }
            }
        }

        item(5) {
            if (categoriesState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
                }
            } else if (categoriesState.error.isNotBlank()) {
                Text(text = categoriesState.error, color = Color.Yellow)
            } else {
                LazyRow(verticalAlignment = Alignment.CenterVertically) {
                    items(categoriesState.categories) { item ->
//                        val encodedUrl = URLEncoder.encode("http://alphaone.me/", StandardCharsets.UTF_8.toString())
                        Column(
                            modifier = Modifier
                                .width(250.dp)
                                .height(170.dp)
                                .padding(horizontal = MyPadding.medium)
                                .clickable {
                                    navController.navigate(
                                        Screen.RecipeListScreen.route + "/${item.category}/${
                                            URLEncoder.encode(
                                                item.imageUrl,
                                                StandardCharsets.UTF_8.toString()
                                            )
                                        }"
                                    ) { launchSingleTop = true }
                                }
                        )
                        {
                            SubcomposeAsyncImage(
                                model = item.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.6f)
                                    .graphicsLayer {
                                        shape = RoundedCornerShape(MyPadding.medium)
                                        clip = true
                                    },
                                contentScale = ContentScale.Crop,
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colors.primaryVariant
                                    )
                                },
                                filterQuality = FilterQuality.Medium,
                            )
                            Spacer(modifier = Modifier.width(MyPadding.small))
                            Text(
                                text = item.category,
                                fontFamily = lemonMilkFonts,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            )
                            Spacer(modifier = Modifier.width(MyPadding.small))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MyPadding.medium))
            }
        }
    }
}
/*
LazyColumn(modifier = Modifier.fillMaxSize()) {

    items(state.items.size) { index ->
        Log.d("HomeScreen", "item size is ${state.items.size} and index is $index")
        val item = state.items[index]

        // below statement is a part of side effect
        LaunchedEffect(key1 = index >= state.items.size - 5 && !state.endReached && !state.isLoading){
                Log.d("If tag", "entered if statement")
                viewModel.loadNextItems()
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = item.title, fontSize = 20.sp)
        }
    }
    item {
        if (state.isLoading) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                horizontalArrangement = Arrangement.Center){
                CircularProgressIndicator()
            }
        }
    }
}

 */

