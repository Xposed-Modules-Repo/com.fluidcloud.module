package com.fluidcloud.module.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import com.fluidcloud.module.ui.component.SliderEntry
import com.fluidcloud.module.ui.theme.HorizontalMargin
import com.fluidcloud.module.ui.theme.SectionSpacing

@Composable
fun CapsuleScreen(
    viewModel: SettingsViewModel,
    bottomPadding: Dp = 0.dp,
) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                color = MiuixTheme.colorScheme.surface,
                title = "胶囊",
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .overScrollVertical(),
            contentPadding = PaddingValues(
                start = HorizontalMargin,
                top = innerPadding.calculateTopPadding() + 12.dp,
                end = HorizontalMargin,
                bottom = bottomPadding
            )
        ) {
            item {
                Card {
                    ArrowPreference(
                        title = "左边距",
                        summary = viewModel.leftValue.toInt().toString(),
                        onClick = { viewModel.showDialog(DialogType.LEFT_VALUE) }
                    )
                    ArrowPreference(
                        title = "右边距",
                        summary = viewModel.rightValue.toInt().toString(),
                        onClick = { viewModel.showDialog(DialogType.RIGHT_VALUE) }
                    )
                }
            }
            item { Spacer(Modifier.height(SectionSpacing)) }
            item {
                Card {
                    SliderEntry(
                        label = "胶囊宽度", value = viewModel.capsuleHeight,
                        valueRange = 0f..200f, steps = 200,
                        onValueChange = { viewModel.applyCapsuleHeight(it) }
                    )
                    SliderEntry(
                        label = "胶囊圆角", value = viewModel.bgCornerRadius,
                        valueRange = 0f..20f, steps = 20,
                        onValueChange = { viewModel.applyBgCornerRadius(it) }
                    )
                    SliderEntry(
                        label = "专辑图标大小", value = viewModel.capsuleIconSize,
                        valueRange = 0f..30f, steps = 30,
                        onValueChange = { viewModel.applyCapsuleIconSize(it) }
                    )
                    SwitchPreference(
                        title = "封面圆角", summary = "将歌曲封面从圆形改为圆角矩形",
                        checked = viewModel.coverRoundRect,
                        onCheckedChange = { viewModel.applyCoverRoundRect(it) }
                    )
                }
            }
            item { Spacer(Modifier.height(SectionSpacing)) }
            item {
                Card {
                    ArrowPreference(
                        title = "胶囊背景色", summary = viewModel.capsuleBgColor,
                        onClick = { viewModel.showDialog(DialogType.CAPSULE_BG_COLOR) }
                    )
                    ArrowPreference(
                        title = "胶囊边框色", summary = viewModel.capsuleStrokeColor,
                        onClick = { viewModel.showDialog(DialogType.CAPSULE_STROKE_COLOR) }
                    )
                }
            }
            item { Spacer(Modifier.height(SectionSpacing)) }
            item {
                Card {
                    SwitchPreference(
                        title = "GPU 边缘光效",
                        summary = if (viewModel.capsuleGlowEnabled) "流光溢彩效果已开启" else "关闭",
                        checked = viewModel.capsuleGlowEnabled,
                        onCheckedChange = { viewModel.applyCapsuleGlowEnabled(it) }
                    )
                }
            }
            if (viewModel.capsuleGlowEnabled) {
                item { Spacer(Modifier.height(6.dp)) }
                item {
                    Card {
                        SliderEntry(
                            label = "流光速度", value = viewModel.capsuleGlowSpeed,
                            valueRange = 0f..1f, steps = 100, format = "%.2f",
                            onValueChange = { viewModel.applyCapsuleGlowSpeed(it) }
                        )
                        SliderEntry(
                            label = "光效强度", value = viewModel.capsuleGlowIntensity,
                            valueRange = 0.0f..3.0f, steps = 300, format = "%.2f",
                            onValueChange = { viewModel.applyCapsuleGlowIntensity(it) }
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(SectionSpacing)) }
            item {
                Card {
                    ArrowPreference(
                        title = "热点胶囊持续时长", summary = formatMin(viewModel.hotspotDurationMin),
                        onClick = { viewModel.showDialog(DialogType.HOTSPOT_DURATION) }
                    )
                    ArrowPreference(
                        title = "音乐胶囊持续时长", summary = formatMin(viewModel.musicDurationMin),
                        onClick = { viewModel.showDialog(DialogType.MUSIC_DURATION) }
                    )
                }
            }
        }
    }
}

private fun formatMin(value: Float): String {
    return if (value == value.toInt().toFloat()) "${value.toInt()}min" else "%.1fmin".format(value)
}
