package com.clementcorporation.jettrivia.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TriviaHome(viewModel: QuestionsViewModel = hiltViewModel()) {
    Questions(viewModel = viewModel)
}