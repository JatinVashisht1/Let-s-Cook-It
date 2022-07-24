package com.jatinvashisht.letscookit.di

import android.app.Application
import androidx.room.Room
import com.jatinvashisht.letscookit.core.Constants
import com.jatinvashisht.letscookit.data.local.RecipeDatabase
import com.jatinvashisht.letscookit.data.remote.RecipeApi
import com.jatinvashisht.letscookit.data.remote.repository.RecipeRepositoryImpl
import com.jatinvashisht.letscookit.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRecipeApi(): RecipeApi = Retrofit
        .Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RecipeApi::class.java)

    @Provides
    @Singleton
    fun providesRecipeDatabase(app: Application): RecipeDatabase = Room
        .databaseBuilder(app, RecipeDatabase::class.java, RecipeDatabase.DATABASE_NAME)
        .build()

    @Provides
    @Singleton
    fun providesRecipeRepository(recipeApi: RecipeApi, database: RecipeDatabase): RecipeRepository =
        RecipeRepositoryImpl(recipeApi = recipeApi, recipeDatabase = database)
}