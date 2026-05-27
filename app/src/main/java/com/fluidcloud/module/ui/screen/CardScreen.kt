package com.fluidcloud.module.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import com.fluidcloud.module.ui.theme.HorizontalMargin
import com.fluidcloud.module.ui.theme.SectionSpacing

@Composable
fun CardScreen(
    viewModel: SettingsViewModel,
    bottomPadding: Dp = 0.dp,
) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                color = MiuixTheme.colorScheme.surface,
                title = "卡片",
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
            item(key = "layout_card") {
                Card {
                    ArrowPreference(
                        title = "卡片高度", summary = viewModel.cardHeight.toInt().toString(),
                        onClick = { viewModel.showDialog(DialogType.CARD_HEIGHT) }
                    )
                    ArrowPreference(
                        title = "卡片间距", summary = viewModel.cardDividerHeight.toInt().toString(),
                        onClick = { viewModel.showDialog(DialogType.CARD_DIVIDER_HEIGHT) }
                    )
                    ArrowPreference(
                        title = "卡片圆角", summary = viewModel.cardCornerRadius.toInt().toString(),
                        onClick = { viewModel.showDialog(DialogType.CARD_CORNER_RADIUS) }
                    )
                }
            }
            item(key = "spacer1") { Spacer(Modifier.height(SectionSpacing)) }
            item(key = "color_card") {
                Card {
                    ArrowPreference(
                        title = "媒体卡片背景色", summary = viewModel.artworkBgColor,
                        onClick = { viewModel.showDialog(DialogType.ARTWORK_BG_COLOR) }
                    )
                    ArrowPreference(
                        title = "媒体卡片边框色", summary = viewModel.mediaCardStrokeColor,
                        onClick = { viewModel.showDialog(DialogType.MEDIA_CARD_STROKE_COLOR) }
                    )
                }
            }
        }
    }
}
