package com.fluidcloud.module.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SliderEntry(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    format: String = "%.0f",
    onValueChange: (Float) -> Unit
) {
    BasicComponent(
        endActions = {
            Text(
                text = "\u00A0\u00A0${format.format(value)}",
                style = MiuixTheme.textStyles.body2
            )
        }
    ) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.body1
        )
        Slider(
            value = value,
            valueRange = valueRange,
            steps = steps,
            onValueChange = onValueChange,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
