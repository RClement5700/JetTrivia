package com.clementcorporation.jettrivia.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clementcorporation.jettrivia.TAG
import com.clementcorporation.jettrivia.model.QuestionItem
import com.clementcorporation.jettrivia.util.AppColors
import java.lang.Exception

@Composable
fun Questions(viewModel: QuestionsViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()
    val questionIndex = remember {
        mutableStateOf(0)
    }
    Log.d(TAG, "Questions size: ${questions?.size}")
    viewModel.data.value.loading?.let {
        if (it) {
            CircularProgressIndicator(modifier = Modifier.padding(100.dp))
        }
        else if (!questions.isNullOrEmpty()) {
            val question = try {
                questions[questionIndex.value]
            } catch(ex: Exception) {
                null
            }
            QuestionDisplay(question = question!!, questionIndex = questionIndex, viewModel = viewModel) {
                questionIndex.value++
            }
        }
    }
}

@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionsViewModel,
    onNextClicked:(Int) -> Unit = {}
) {
    val choicesState = remember(question) {
        question.choices.toMutableList()
    }
    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }
    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }
    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        color = AppColors.mDarkPurple
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (questionIndex.value >= 3) {
                ShowProgress(score = questionIndex.value)
            }
            viewModel.getTotalQuestionCount()?.let {
                QuestionTracker(counter = questionIndex.value, it)
            }
            DrawDottedLine(pathEffect = pathEffect)
            Column {
                Text(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxHeight(0.3f),
                    text = question.question,
                    color = AppColors.mOffWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                choicesState.forEachIndexed { index, answerText ->
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)
                            .background(Color.Transparent)
                            .border(
                                width = 4.dp, shape = RoundedCornerShape(15.dp),
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.mOffDarkPurple,
                                        AppColors.mOffDarkPurple
                                    )
                                )
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 50,
                                    topEndPercent = 50,
                                    bottomEndPercent = 50,
                                    bottomStartPercent = 50
                                )
                            )
                    ) {
                        RadioButton(
                            modifier = Modifier.padding(start = 16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (correctAnswerState.value == true && index == answerState.value)
                                    Color.Green else Color.Red
                            ),
                            selected = (answerState.value == index), onClick = { updateAnswer(index) }
                        )
                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(
                                fontWeight = FontWeight.Light,
                                color = if (correctAnswerState.value == true && index == answerState.value) {
                                    Color.Green
                                } else if (correctAnswerState.value == false && index == answerState.value) {
                                    Color.Red
                                } else {
                                    Color.White
                                }
                            )) {
                                append(answerText)
                            }
                        }
                        Text(text = annotatedString, modifier = Modifier.padding(6.dp))
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(3.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.mLightBlue),
                    onClick = {
                        onNextClicked(questionIndex.value)
                    }
                ) {
                    Text(
                        text = "Next",
                        modifier = Modifier.padding(4.dp),
                        color = AppColors.mOffWhite,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionTracker(counter: Int = 0, outOf: Int = 0) {
    Text(
        modifier = Modifier.padding(20.dp),
        text = buildAnnotatedString {
            withStyle(style = ParagraphStyle(textIndent = TextIndent.None)
            ) {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.mLightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp
                    )
                ) {
                    append("Question $counter/")
                    withStyle(
                        style = SpanStyle(
                            color = AppColors.mLightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp)
                    ) {
                        append("$outOf")
                    }
                }
            }
        },
    )
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        onDraw = {
            drawLine(
                color = AppColors.mLightGray,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = pathEffect
            )
        }
    )
}

@Composable
fun ShowProgress(score: Int = 12) {
    val gradient = Brush.linearGradient(listOf(
        Color(0xFFF95075),
        Color(0xFFBE6BE5)
        )
    )
    val progressFactor = remember(score) {
        mutableStateOf(score*0.005f)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(45.dp)
            .background(Color.Transparent)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppColors.mLightPurple,
                        AppColors.mLightPurple
                    )
                ),
                shape = RoundedCornerShape(34.dp)
            )
            .clip(
                RoundedCornerShape(
                    topStartPercent = 50,
                    bottomStartPercent = 50,
                    bottomEndPercent = 50,
                    topEndPercent = 50,
                )
            )
    ) {
        Button(
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier
                .fillMaxWidth(progressFactor.value)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent
            ),
            onClick = { /*TODO*/ }) {
            Text(
                text = (score*10).toString(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .clip(RoundedCornerShape(23.dp)),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center
                )
        }
    }
}
