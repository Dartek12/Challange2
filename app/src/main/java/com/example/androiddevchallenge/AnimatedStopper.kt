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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CountdownCircle(
    modifier: Modifier = Modifier,
    percentage: Float,
    trackColor: Color = Color.White,
    indicatorTrackColor: Color = MaterialTheme.colors.primaryVariant,
) {
    val animatedPercentage by animateFloatAsState(percentage)
    val animatedAngle = animatedPercentage * 360.0f / 100f

    val strokeWidth = with(LocalDensity.current) { 12.dp.toPx() }
    val trackStroke = Stroke(strokeWidth, cap = StrokeCap.Round)
    val indicatorStroke = Stroke(strokeWidth * 1.2f, cap = StrokeCap.Round)

    Canvas(modifier) {
        val radius = (size.minDimension - trackStroke.width * 2f) / 2f

        val topLeft = Offset(strokeWidth, strokeWidth)
        val arcSize = Size(radius * 2f, radius * 2f)

        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            topLeft = topLeft,
            size = arcSize,
            useCenter = true,
            style = trackStroke
        )

        withTransform({
            scale(-1f, 1f)
        }) {
            drawArc(
                color = indicatorTrackColor,
                startAngle = -90f,
                sweepAngle = animatedAngle,
                topLeft = topLeft,
                size = arcSize,
                useCenter = false,
                style = indicatorStroke
            )
        }
    }
}

@Preview
@Composable
fun PreviewCountdownCircle() {
    CountdownCircle(
        modifier = Modifier.size(300.dp),
        percentage = 32.0f,
        trackColor = Color.White,
        indicatorTrackColor = Color.Blue,
    )
}
