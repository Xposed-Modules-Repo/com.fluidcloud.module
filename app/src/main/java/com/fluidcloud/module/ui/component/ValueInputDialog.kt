package com.fluidcloud.module.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ValueInputDialog(
    title: String,
    value: Float,
    defaultValue: Float,
    show: Boolean,
    hint: String = "请输入数值",
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(show) {
        if (show) {
            val displayText = if (value == value.toInt().toFloat()) value.toInt().toString() else value.toString()
            text = TextFieldValue(text = displayText, selection = TextRange(displayText.length))
        }
    }

    if (show) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .imePadding()
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = title, style = MiuixTheme.textStyles.title3)
                    Spacer(Modifier.height(16.dp))
                    TextField(
                        value = text,
                        onValueChange = { newValue ->
                            if (newValue.text.isEmpty() || newValue.text.matches(Regex("-?\\d{0,4}(\\.\\d{0,2})?"))) {
                                text = newValue
                            }
                        },
                        label = hint,
                        singleLine = true,
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            text = "重置",
                            onClick = {
                                val resetText = if (defaultValue == defaultValue.toInt().toFloat())
                                    defaultValue.toInt().toString() else defaultValue.toString()
                                text = TextFieldValue(text = resetText, selection = TextRange(resetText.length))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "取消",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "保存",
                            onClick = {
                                text.text.toFloatOrNull()?.let { onConfirm(it) }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
}
