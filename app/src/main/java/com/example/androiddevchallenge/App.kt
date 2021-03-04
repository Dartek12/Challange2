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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.CountdownTheme
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.abs

@Composable
fun CountdownApp() {
    var appState: AppState by remember { mutableStateOf(SettingsState("")) }
    val immutableState = appState

    Surface(color = MaterialTheme.colors.background) {
        when (immutableState) {
            is SettingsState -> {
                AppearanceAnimation {
                    CountdownSettings(
                        immutableState,
                        onStartClicked = {
                            appState = CountdownState(false, 0, immutableState.asSeconds())
                        },
                        onDigitPressed = {
                            appState = immutableState.tryPushDigit(it)
                        },
                        onDigitRemove = {
                            appState = immutableState.tryPopDigit()
                        }
                    )
                }
            }
            is CountdownState -> {
                AppearanceAnimation {
                    CountdownClock(
                        immutableState,
                        onPauseClicked = {
                            appState = immutableState.pause()
                        },
                        onResumeClicked = {
                            appState = immutableState.resume()
                        },
                        onStopClicked = {
                            appState = SettingsState("")
                        },
                        onTicked = {
                            appState = immutableState.tick()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppearanceAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
        ) + fadeOut()
    ) {
        content()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CountdownSettings(
    state: SettingsState,
    onStartClicked: () -> Unit,
    onDigitPressed: (Char) -> Unit,
    onDigitRemove: () -> Unit,
) {
    SharedLayout(
        panel = {
            ControlPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                IconedButton(
                    caption = stringResource(id = R.string.start),
                    icon = Icons.Default.PlayArrow,
                    onClick = onStartClicked,
                    enabled = state.isNonZero,
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            FormattedTimer(
                state.sequence,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onDigitRemove = onDigitRemove
            )
            DigitsKeyboard(
                onDigitPressed = onDigitPressed,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun FormattedTimer(sequence: String, modifier: Modifier = Modifier, onDigitRemove: () -> Unit) {
    val padded = sequence.padStart(6, '0')
    val formattedText = buildAnnotatedString {
        val unitStyle = MaterialTheme.typography.h6.toSpanStyle()

        append(padded.substring(0, 2))
        withStyle(unitStyle) {
            append(stringResource(id = R.string.h))
        }
        append(' ')
        append(padded.substring(2, 4))
        withStyle(unitStyle) {
            append(stringResource(id = R.string.m))
        }
        append(' ')
        append(padded.substring(4, 6))
        withStyle(unitStyle) {
            append(stringResource(id = R.string.s))
        }
    }

    val color by animateColorAsState(
        targetValue = if (sequence.isEmpty()) MaterialTheme.colors.onSurface.copy(
            alpha = 0.4f
        ) else MaterialTheme.colors.secondary.copy(alpha = 0.8f),
        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
    )

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            formattedText,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h3.copy(color = color)
        )

        Box(Modifier.width(8.dp))

        IconButton(onClick = onDigitRemove, enabled = sequence.isNotEmpty()) {
            Icon(imageVector = Icons.Default.Backspace, tint = color, contentDescription = null)
        }
    }
}

@Composable
fun DigitsKeyboard(modifier: Modifier = Modifier, onDigitPressed: (Char) -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            CharKeyButton('1', onDigitPressed = onDigitPressed)
            CharKeyButton('2', onDigitPressed = onDigitPressed)
            CharKeyButton('3', onDigitPressed = onDigitPressed)
        }
        Row {
            CharKeyButton('4', onDigitPressed = onDigitPressed)
            CharKeyButton('5', onDigitPressed = onDigitPressed)
            CharKeyButton('6', onDigitPressed = onDigitPressed)
        }
        Row {
            CharKeyButton('7', onDigitPressed = onDigitPressed)
            CharKeyButton('8', onDigitPressed = onDigitPressed)
            CharKeyButton('9', onDigitPressed = onDigitPressed)
        }
        CharKeyButton('0', onDigitPressed = onDigitPressed)
    }
}

@Composable
fun CharKeyButton(char: Char, onDigitPressed: (Char) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .padding(24.dp)
            .clickable(
                onClick = { onDigitPressed(char) },
                enabled = true,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = 64.dp)
            )
            .size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            char.toString(),
            style = MaterialTheme.typography.h4
        )
    }
}

@Composable
fun CountdownClock(
    countdownState: CountdownState,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onStopClicked: () -> Unit,
    onTicked: () -> Unit
) {
    SharedLayout(
        panel = {
            ControlPanel {
                IconedButton(
                    caption = if (countdownState.paused) stringResource(id = R.string.start) else stringResource(
                        id = R.string.pause
                    ),
                    icon = if (countdownState.paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    onClick = if (countdownState.paused) onResumeClicked else onPauseClicked
                )
                IconedButton(
                    caption = stringResource(id = R.string.stop),
                    icon = Icons.Default.Stop,
                    onClick = onStopClicked
                )
            }
        }
    ) {

        Box(
            Modifier
                .padding(PaddingValues(horizontal = 32.dp, vertical = 0.dp))
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {

            CountdownCircle(
                modifier = Modifier
                    .fillMaxSize(),
                percentage = countdownState.progress,
            )

            TickingText(countdownState)
        }

        val currentOnTicked by rememberUpdatedState(onTicked)

        DisposableEffect(countdownState.paused) {
            var timer: Timer? = null
            if (!countdownState.paused) {
                timer = Timer()
                timer.scheduleAtFixedRate(1000L, 1000L) {
                    currentOnTicked()
                }
            }
            onDispose {
                timer?.cancel()
            }
        }
    }
}

@Composable
fun TickingText(state: CountdownState) {
    val formattedText = remember(state.current, state.target) {
        val remaining = state.target - state.current
        formatRemainingSeconds(remaining)
    }

    val containerModifier = Modifier
        .fillMaxSize()

    Column(
        modifier = containerModifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PausedDecorator(running = state.paused) {
            Text(
                formattedText,
                style = MaterialTheme.typography.h2
            )
        }
    }
}

@Composable
fun PausedDecorator(running: Boolean, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(true) }
    val alpha = if (visible) 1.0f else 0.0f

    Box(modifier = Modifier.alpha(alpha)) {
        content()
    }

    DisposableEffect(running) {
        var timer: Timer? = null
        if (running) {
            timer = Timer()
            timer.scheduleAtFixedRate(500L, 500L) {
                visible = !visible
            }
        }
        onDispose {
            timer?.cancel()
        }
    }
}

fun formatRemainingSeconds(seconds: Int): String {
    val sb = StringBuilder()
    val components = abs(seconds).asTimeComponents()

    for (component in components) {
        if (sb.isNotEmpty()) {
            sb.append(":")
            sb.append(component.toString().padStart(2, '0'))
        } else if (component != 0) {
            sb.append(component.toString())
        }
    }

    if (sb.isEmpty()) {
        sb.append('0')
    }

    if (seconds < 0) {
        sb.insert(0, '-')
    }
    return sb.toString()
}

private fun Int.asTimeComponents(): List<Int> {
    var remaining = this
    val hours = remaining / 3600
    remaining -= hours * 3600
    val minutes = remaining / 60
    remaining -= minutes * 60
    return listOf(hours, minutes, remaining)
}

@Composable
fun PausedTimerLayout(
    onResumeClicked: () -> Unit,
    onStopClicked: () -> Unit
) {
    SharedLayout(
        panel = {
            ControlPanel {
                IconedButton(
                    caption = stringResource(id = R.string.resume),
                    icon = Icons.Default.PlayArrow,
                    onClick = onResumeClicked
                )
                IconedButton(
                    caption = stringResource(id = R.string.stop),
                    icon = Icons.Default.Stop,
                    onClick = onStopClicked
                )
            }
        }
    ) {
        // TODO: timer
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    CountdownTheme {
        CountdownApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    CountdownTheme(darkTheme = true) {
        CountdownApp()
    }
}
