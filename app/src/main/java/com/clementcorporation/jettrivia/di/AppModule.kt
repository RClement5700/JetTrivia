package com.clementcorporation.jettrivia.di

import com.clementcorporation.jettrivia.network.QuestionApi
import com.clementcorporation.jettrivia.repository.QuestionRepository
import com.clementcorporation.jettrivia.util.Constants.BASE_URL
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

    @Singleton
    @Provides
    fun provideQuestionApi(): QuestionApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideQuestionRepository(api: QuestionApi): QuestionRepository = QuestionRepository(api)
}