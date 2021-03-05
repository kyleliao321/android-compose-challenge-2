/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Preview
@Composable
fun MyAppPreview() {
    MyTheme {
        MyApp()
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val originalCounter = 600
    var counterRemain by rememberSaveable { mutableStateOf(600) }
    var counting by rememberSaveable { mutableStateOf(false) }
    var startButtonStateText by rememberSaveable { mutableStateOf("Start") }

    var countDownJob: Job? by rememberSaveable { mutableStateOf(null) }

    if (counterRemain == 0) {
        countDownJob?.cancel()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 20.dp, vertical = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        CountdownTimer(
            timeText = secondToFormattedString(counterRemain),
            progress = counterProgress(counterRemain, originalCounter),
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.background,
            surfaceColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .size(250.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CircleButton(
                text = "Cancel",
                buttonSize = 100.dp,
                buttonColor = MaterialTheme.colors.primaryVariant,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onPrimary,
                onClick = {
                    counting = false
                    countDownJob?.cancel()
                    counterRemain = originalCounter
                }
            )

            CircleButton(
                text = startButtonStateText,
                buttonSize = 100.dp,
                buttonColor = MaterialTheme.colors.primaryVariant,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onPrimary,
                onClick = {
                    counting = !counting

                    if (!counting) {
                        startButtonStateText = "Start"
                        countDownJob?.cancel()
                    } else {
                        // start animation
                        startButtonStateText = "Stop"
                        countDownJob = startCoroutineTimer(0, 1000) {
                            Handler(Looper.getMainLooper()).post {
                                counterRemain -= 1
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CircleButton(
    text: String,
    buttonSize: Dp,
    buttonColor: Color,
    backgroundColor: Color,
    contentColor: Color,
    onClick: (() -> Unit)? = null
) {
    ConstraintLayout(
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .clickable { onClick?.let { it() } }
    ) {
        val (canvasRef, textRef) = createRefs()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawCircle(
                color = buttonColor,
                center = Offset(size.width / 2, size.height / 2),
                radius = size.width / 2
            )

            drawCircle(
                color = backgroundColor,
                center = Offset(size.width / 2, size.height / 2),
                radius = (size.width / 2) * .9f
            )

            drawCircle(
                color = buttonColor,
                center = Offset(size.width / 2, size.height / 2),
                radius = (size.width / 2) * .85f
            )
        }

        Text(
            modifier = Modifier
                .constrainAs(textRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.button,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun CircleButtonLightSample() {
    MyTheme {
        CircleButton(
            text = "Start",
            buttonSize = 100.dp,
            buttonColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onPrimary
        )
    }
}

@Preview
@Composable
fun CircleButtonDarkSample() {
    MyTheme(darkTheme = true) {
        CircleButton(
            text = "Start",
            buttonSize = 100.dp,
            buttonColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun CountdownTimer(
    modifier: Modifier = Modifier,
    timeText: String,
    progress: Float,
    contentColor: Color,
    backgroundColor: Color,
    surfaceColor: Color
) {
    ConstraintLayout(modifier = modifier) {
        val textRef = createRef()

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            drawArc(
                color = surfaceColor,
                useCenter = true,
                startAngle = 0f,
                sweepAngle = 360f,
                size = size
            )

            drawArc(
                color = contentColor,
                useCenter = true,
                startAngle = 0f - 90f,
                sweepAngle = 360f * (1 - progress),
                size = size
            )

            drawCircle(
                color = backgroundColor,
                center = Offset(size.width / 2, size.height / 2),
                radius = size.width * .9f / 2
            )
        }

        Text(
            modifier = Modifier
                .constrainAs(textRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = timeText,
            color = contentColor,
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun CountdownTimerLightSample() {
    MyTheme {
        CountdownTimer(
            timeText = "59:59",
            progress = .125f,
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.background,
            surfaceColor = MaterialTheme.colors.surface,
            modifier = Modifier.size(250.dp)
        )
    }
}

@Preview
@Composable
fun CountdownTimerDarkSample() {
    MyTheme(darkTheme = true) {
        CountdownTimer(
            timeText = "59:59",
            progress = .125f,
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.background,
            surfaceColor = MaterialTheme.colors.surface,
            modifier = Modifier.size(250.dp)
        )
    }
}

fun secondToFormattedString(sec: Int): String {
    val min = sec / 60
    val remainSec = sec % 60

    return if (min < 10 && remainSec < 10) {
        "0$min:0$remainSec"
    } else if (min >= 10 && remainSec < 10) {
        "$min:0$remainSec"
    } else if (min < 10 && remainSec >= 10) {
        "0$min:$remainSec"
    } else {
        "$min:$remainSec"
    }
}

fun counterProgress(current: Int, original: Int): Float {
    return (original - current).toFloat() / original
}

inline fun startCoroutineTimer(
    delayMillis: Long = 0,
    repeatMillis: Long = 0,
    crossinline action: () -> Unit
) = GlobalScope.launch {
    delay(delayMillis)
    if (repeatMillis > 0) {
        while (true) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}
