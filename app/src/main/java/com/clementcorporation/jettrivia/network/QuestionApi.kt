package com.clementcorporation.jettrivia.network

import com.clementcorporation.jettrivia.model.Question
import com.clementcorporation.jettrivia.model.QuestionItem
import retrofit2.http.GET

interface QuestionApi {
    @GET("world.json")
    suspend fun getAllQuestions(): Question
}