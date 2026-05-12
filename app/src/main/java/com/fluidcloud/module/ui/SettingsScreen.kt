package com.fluidcloud.module.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ColorPalette
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.fluidcloud.module.Settings as ModuleSettings

private val H_MARGIN = 16.dp

@Composable
fun SettingsScreen() {
    ModuleSettings.ensureLoaded()
    var hookEnabled by remember { mutableStateOf(ModuleSettings.hookEnabled) }
    var leftValue by remember { mutableStateOf(ModuleSettings.leftValue) }
    var rightValue by remember { mutableStateOf(ModuleSettings.rightValue) }
    var capsuleHeight by remember { mutableStateOf(ModuleSettings.capsuleHeight.toFloat()) }
    var coverRoundRect by remember { mutableStateOf(ModuleSettings.coverRoundRect) }
    var bgCornerRadius by remember { mutableStateOf(ModuleSettings.bgCornerRadius.toFloat()) }
    var cardDividerHeight by remember { mutableStateOf(ModuleSettings.cardDividerHeight.toFloat()) }
    var cardHeight by remember { mutableStateOf(ModuleSettings.cardHeight.toFloat()) }
    var cardCornerRadius by remember { mutableStateOf(ModuleSettings.cardCornerRadius.toFloat()) }
    var artworkBgColor by remember { mutableStateOf(ModuleSettings.artworkBgColor) }
    var capsuleBgColor by remember { mutableStateOf(ModuleSettings.capsuleBgColor) }
    var capsuleStrokeColor by remember { mutableStateOf(ModuleSettings.capsuleStrokeColor) }
    var mediaCardStrokeColor by remember { mutableStateOf(ModuleSettings.mediaCardStrokeColor) }
    var showLeftDialog by remember { mutableStateOf(false) }
    var showRightDialog by remember { mutableStateOf(false) }
    var showCardDividerHeightDialog by remember { mutableStateOf(false) }
    var showCardHeightDialog by remember { mutableStateOf(false) }
    var showCardCornerRadiusDialog by remember { mutableStateOf(false) }
    var showArtworkBgColorDialog by remember { mutableStateOf(false) }
    var showCapsuleBgColorDialog by remember { mutableStateOf(false) }
    var showCapsuleStrokeColorDialog by remember { mutableStateOf(false) }
    var showMediaCardStrokeColorDialog by remember { mutableStateOf(false) }
    var hotspotCapsuleDurationMin by remember { mutableStateOf(ModuleSettings.hotspotCapsuleDuration / 60000f) }
    var musicCapsuleDurationMin by remember { mutableStateOf(ModuleSettings.musicCapsuleDuration / 60000f) }
    var showHotspotCapsuleDurationDialog by remember { mutableStateOf(false) }
    var showMusicCapsuleDurationDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(title = "\u6D41\u4F53\u4E91\u8C03\u6574")
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = H_MARGIN)
                    .verticalScroll(rememberScrollState())
            ) {
            Card {
                SwitchPreference(
                    title = "\u542F\u7528 Hook",
                    summary = "\u63A7\u5236\u6D41\u4F53\u4E91 UI \u4FEE\u6539\u662F\u5426\u751F\u6548",
                    checked = hookEnabled,
                    onCheckedChange = {
                        hookEnabled = it
                        ModuleSettings.hookEnabled = it
                        ModuleSettings.save()
                    }
                )
                ArrowPreference(
                    title = "\u91CD\u542F\u7CFB\u7EDF\u754C\u9762",
                    summary = "\u7ACB\u5373\u6740\u6B7B\u4E24\u4E2A\u4F5C\u7528\u57DF\u8FDB\u7A0B \u4F7F\u8BBE\u7F6E\u751F\u6548",
                    onClick = { restartSystemUI() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card {
                ArrowPreference(
                    title = "\u5DE6\u8FB9\u8DDD",
                    summary = leftValue.toInt().toString(),
                    onClick = { showLeftDialog = true }
                )
                ArrowPreference(
                    title = "\u53F3\u8FB9\u8DDD",
                    summary = rightValue.toInt().toString(),
                    onClick = { showRightDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card {
                SliderEntry(
                    label = "\u80F6\u56CA\u9AD8\u5EA6",
                    value = capsuleHeight,
                    valueRange = 0f..100f,
                    steps = 100,
                    onValueChange = {
                        capsuleHeight = it
                        ModuleSettings.capsuleHeight = it.roundToInt()
                        ModuleSettings.save()
                    }
                )
                SliderEntry(
                    label = "\u80F6\u56CA\u5706\u89D2",
                    value = bgCornerRadius,
                    valueRange = 0f..20f,
                    steps = 20,
                    onValueChange = {
                        bgCornerRadius = it
                        ModuleSettings.bgCornerRadius = it.roundToInt()
                        ModuleSettings.save()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card {
                ArrowPreference(
                    title = "\u70ED\u70B9\u80F6\u56CA\u6301\u7EED\u65F6\u957F",
                    summary = formatMin(hotspotCapsuleDurationMin),
                    onClick = { showHotspotCapsuleDurationDialog = true }
                )
                ArrowPreference(
                    title = "\u97F3\u4E50\u80F6\u56CA\u6301\u7EED\u65F6\u957F",
                    summary = formatMin(musicCapsuleDurationMin),
                    onClick = { showMusicCapsuleDurationDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card {
                ArrowPreference(
                    title = "\u5361\u7247\u9AD8\u5EA6",
                    summary = cardHeight.roundToInt().toString(),
                    onClick = { showCardHeightDialog = true }
                )
                ArrowPreference(
                    title = "\u5361\u7247\u95F4\u8DDD",
                    summary = cardDividerHeight.roundToInt().toString(),
                    onClick = { showCardDividerHeightDialog = true }
                )
                ArrowPreference(
                    title = "\u5361\u7247\u5706\u89D2",
                    summary = cardCornerRadius.roundToInt().toString(),
                    onClick = { showCardCornerRadiusDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card {
                SwitchPreference(
                    title = "\u5C01\u9762\u5706\u89D2",
                    summary = "\u5C06\u6B4C\u66F2\u5C01\u9762\u4ECE\u5706\u5F62\u6539\u4E3A\u5706\u89D2\u77E9\u5F62",
                    checked = coverRoundRect,
                    onCheckedChange = {
                        coverRoundRect = it
                        ModuleSettings.coverRoundRect = it
                        ModuleSettings.save()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card {
                ArrowPreference(
                    title = "\u5A92\u4F53\u5361\u7247\u80CC\u666F\u8272",
                    summary = artworkBgColor,
                    onClick = { showArtworkBgColorDialog = true }
                )
                ArrowPreference(
                    title = "\u5A92\u4F53\u5361\u7247\u8FB9\u6846\u8272",
                    summary = mediaCardStrokeColor,
                    onClick = { showMediaCardStrokeColorDialog = true }
                )
                ArrowPreference(
                    title = "\u80F6\u56CA\u80CC\u666F\u8272",
                    summary = capsuleBgColor,
                    onClick = { showCapsuleBgColorDialog = true }
                )
                ArrowPreference(
                    title = "\u80F6\u56CA\u8FB9\u6846\u8272",
                    summary = capsuleStrokeColor,
                    onClick = { showCapsuleStrokeColorDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card {
                BasicComponent(
                    title = "\u7248\u672C",
                    summary = "26.1"
                )
                BasicComponent(
                    title = "\u76EE\u6807\u7CFB\u7EDF",
                    summary = "ColorOS 16"
                )
                BasicComponent(
                    title = "\u4F5C\u8005",
                    summary = "Coolapk@\u90A3\u6CDB\u6EE5\u7684\u601D\u7EEA"
                )
            }
        }
    }

        InputValueDialog(
            title = "\u5DE6\u8FB9\u8DDD",
            hint = "\u9ED8\u8BA4 540",
            value = leftValue,
            defaultValue = 540f,
            show = showLeftDialog,
            onConfirm = {
                leftValue = it
                ModuleSettings.leftValue = it
                ModuleSettings.save()
                showLeftDialog = false
            },
            onDismiss = { showLeftDialog = false }
        )
        InputValueDialog(
            title = "\u53F3\u8FB9\u8DDD",
            hint = "\u9ED8\u8BA4 540",
            value = rightValue,
            defaultValue = 540f,
            show = showRightDialog,
            onConfirm = {
                rightValue = it
                ModuleSettings.rightValue = it
                ModuleSettings.save()
                showRightDialog = false
            },
            onDismiss = { showRightDialog = false }
        )
        InputValueDialog(
            title = "\u5361\u7247\u95F4\u8DDD",
            hint = "\u9ED8\u8BA4 8",
            value = cardDividerHeight,
            defaultValue = 8f,
            show = showCardDividerHeightDialog,
            onConfirm = {
                cardDividerHeight = it
                ModuleSettings.cardDividerHeight = it.roundToInt()
                ModuleSettings.save()
                showCardDividerHeightDialog = false
            },
            onDismiss = { showCardDividerHeightDialog = false }
        )
        InputValueDialog(
            title = "\u5361\u7247\u9AD8\u5EA6",
            hint = "\u9ED8\u8BA4 8",
            value = cardHeight,
            defaultValue = 8f,
            show = showCardHeightDialog,
            onConfirm = {
                cardHeight = it
                ModuleSettings.cardHeight = it.roundToInt()
                ModuleSettings.save()
                showCardHeightDialog = false
            },
            onDismiss = { showCardHeightDialog = false }
        )
        InputValueDialog(
            title = "\u5361\u7247\u5706\u89D2",
            hint = "\u9ED8\u8BA4 20",
            value = cardCornerRadius,
            defaultValue = 20f,
            show = showCardCornerRadiusDialog,
            onConfirm = {
                cardCornerRadius = it
                ModuleSettings.cardCornerRadius = it.roundToInt()
                ModuleSettings.save()
                showCardCornerRadiusDialog = false
            },
            onDismiss = { showCardCornerRadiusDialog = false }
        )
        InputValueDialog(
            title = "\u70ED\u70B9\u80F6\u56CA\u6301\u7EED\u65F6\u957F (min)",
            hint = "\u9ED8\u8BA4 1",
            value = hotspotCapsuleDurationMin,
            defaultValue = 1f,
            show = showHotspotCapsuleDurationDialog,
            onConfirm = {
                hotspotCapsuleDurationMin = it
                ModuleSettings.hotspotCapsuleDuration = (it * 60000).roundToInt()
                ModuleSettings.save()
                showHotspotCapsuleDurationDialog = false
            },
            onDismiss = { showHotspotCapsuleDurationDialog = false }
        )
        InputValueDialog(
            title = "\u97F3\u4E50\u80F6\u56CA\u6301\u7EED\u65F6\u957F (min)",
            hint = "\u9ED8\u8BA4 5",
            value = musicCapsuleDurationMin,
            defaultValue = 5f,
            show = showMusicCapsuleDurationDialog,
            onConfirm = {
                musicCapsuleDurationMin = it
                ModuleSettings.musicCapsuleDuration = (it * 60000).roundToInt()
                ModuleSettings.save()
                showMusicCapsuleDurationDialog = false
            },
            onDismiss = { showMusicCapsuleDurationDialog = false }
        )
        if (showArtworkBgColorDialog) {
            ColorPaletteDialog(
                title = "\u5A92\u4F53\u5361\u7247\u80CC\u666F\u8272",
                initialColor = Color(android.graphics.Color.parseColor(artworkBgColor)),
                defaultColor = Color(android.graphics.Color.parseColor("#4D000000")),
                onDismiss = { showArtworkBgColorDialog = false },
                onConfirm = { color ->
                    val hex = "#%08X".format(color.toArgb())
                    artworkBgColor = hex
                    ModuleSettings.artworkBgColor = hex
                    ModuleSettings.save()
                    showArtworkBgColorDialog = false
                }
            )
        }
        if (showCapsuleBgColorDialog) {
            ColorPaletteDialog(
                title = "\u80F6\u56CA\u80CC\u666F\u8272",
                initialColor = Color(android.graphics.Color.parseColor(capsuleBgColor)),
                defaultColor = Color(android.graphics.Color.parseColor("#ff000000")),
                onDismiss = { showCapsuleBgColorDialog = false },
                onConfirm = { color ->
                    val hex = "#%08X".format(color.toArgb())
                    capsuleBgColor = hex
                    ModuleSettings.capsuleBgColor = hex
                    ModuleSettings.save()
                    showCapsuleBgColorDialog = false
                }
            )
        }
        if (showMediaCardStrokeColorDialog) {
            ColorPaletteDialog(
                title = "\u5A92\u4F53\u5361\u7247\u8FB9\u6846\u8272",
                initialColor = Color(android.graphics.Color.parseColor(mediaCardStrokeColor)),
                defaultColor = Color(android.graphics.Color.parseColor("#4D000000")),
                onDismiss = { showMediaCardStrokeColorDialog = false },
                onConfirm = { color ->
                    val hex = "#%08X".format(color.toArgb())
                    mediaCardStrokeColor = hex
                    ModuleSettings.mediaCardStrokeColor = hex
                    ModuleSettings.save()
                    showMediaCardStrokeColorDialog = false
                }
            )
        }
        if (showCapsuleStrokeColorDialog) {
            ColorPaletteDialog(
                title = "\u80F6\u56CA\u8FB9\u6846\u8272",
                initialColor = Color(android.graphics.Color.parseColor(capsuleStrokeColor)),
                defaultColor = Color(android.graphics.Color.parseColor("#40959595")),
                onDismiss = { showCapsuleStrokeColorDialog = false },
                onConfirm = { color ->
                    val hex = "#%08X".format(color.toArgb())
                    capsuleStrokeColor = hex
                    ModuleSettings.capsuleStrokeColor = hex
                    ModuleSettings.save()
                    showCapsuleStrokeColorDialog = false
                }
            )
        }
    }
}

@Composable
private fun InputValueDialog(
    title: String,
    value: Float,
    defaultValue: Float,
    show: Boolean,
    hint: String = "\u8F93\u5165\u6570\u503C",
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
                    Text(
                        text = title,
                        style = MiuixTheme.textStyles.title3
                    )
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
                            text = "\u91CD\u7F6E",
                            onClick = {
                                val resetText = if (defaultValue == defaultValue.toInt().toFloat())
                                    defaultValue.toInt().toString()
                                else
                                    defaultValue.toString()
                                text = TextFieldValue(text = resetText, selection = TextRange(resetText.length))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "\u53D6\u6D88",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(20.dp))
                        TextButton(
                            text = "\u4FDD\u5B58",
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

@Composable
private fun SliderEntry(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    BasicComponent(
        endActions = {
            Text(
                text = "\u00A0\u00A0%.0f".format(value),
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

@Composable
private fun ColorPaletteDialog(
    title: String,
    initialColor: Color,
    defaultColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (Color) -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.title3
                )
                Spacer(Modifier.height(12.dp))
                ColorPalette(
                    color = selectedColor,
                    onColorChanged = { selectedColor = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        text = "\u91CD\u7F6E",
                        onClick = { selectedColor = defaultColor },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(20.dp))
                    TextButton(
                        text = "\u53D6\u6D88",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(20.dp))
                    TextButton(
                        text = "\u4FDD\u5B58",
                        onClick = { onConfirm(selectedColor) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
    }
}

private fun formatMin(value: Float): String {
    return if (value == value.roundToInt().toFloat()) {
        "${value.roundToInt()}min"
    } else {
        "%.1fmin".format(value)
    }
}

private fun restartSystemUI() {
    try {
        Runtime.getRuntime().exec(arrayOf("su", "-c",
            "pkill -f com.android.systemui; pkill -f com.oplus.systemui.plugins"))
    } catch (_: Exception) {}
}
