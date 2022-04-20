package com.clementcorporation.jettrivia.repository

import android.util.Log
import com.clementcorporation.jettrivia.data.DataOrException
import com.clementcorporation.jettrivia.model.QuestionItem
import com.clementcorporation.jettrivia.network.QuestionApi
import javax.inject.Inject

const val TAG = "QuestionRepository"
class QuestionRepository @Inject constructor(
    private val api: QuestionApi
    ) {
    private val dataOrException = DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            dataOrException.loading = dataOrException.data.toString().isNotEmpty()
        } catch (exception: Exception) {
            dataOrException.exception = exception
            Log.d(TAG, "getAllQuestions: ${dataOrException.exception?.localizedMessage}")
        }
        return dataOrException
    }
}