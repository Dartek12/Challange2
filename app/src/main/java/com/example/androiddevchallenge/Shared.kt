package com.example.androiddevchallenge

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SharedLayout(
    panel: @Composable() BoxScope.() -> Unit,
    content: @Composable() BoxScope.() -> Unit
) {
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        Box(
            Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth()
        ) {
            panel()
        }
    }
}

@Composable
fun ControlPanel(
    modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        content()
    }
}

@Composable
fun IconedButton(
    modifier: Modifier = Modifier,
    caption: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(modifier = modifier.animateContentSize(), onClick = onClick, enabled = enabled) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = caption,
            )
            Box(Modifier.width(8.dp))
            Text(caption)
        }
    }
}