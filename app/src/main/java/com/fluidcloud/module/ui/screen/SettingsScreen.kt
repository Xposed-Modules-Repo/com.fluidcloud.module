package com.fluidcloud.module.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.BankCards
import top.yukonga.miuix.kmp.icon.extended.Recent
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.fluidcloud.module.ui.FloatingBottomBar
import com.fluidcloud.module.ui.FloatingBottomBarItem
import com.fluidcloud.module.ui.LocalFloatingBottomBarTabScale
import com.fluidcloud.module.ui.component.ColorPickerDialog
import com.fluidcloud.module.ui.component.ValueInputDialog
import com.fluidcloud.module.ui.component.toComposeColor
import com.fluidcloud.module.ui.component.toArgbHex

private data class TabIcon(
    val imageVector: ImageVector,
    val label: String,
)

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    LaunchedEffect(Unit) { viewModel.load() }

    var selectedTab by remember(viewModel.themeVersion) { mutableStateOf(0) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val scope = rememberCoroutineScope()

    val tabIcons = remember {
        listOf(
            TabIcon(Icons.Rounded.Home, "主页"),
            TabIcon(MiuixIcons.Recent, "胶囊"),
            TabIcon(MiuixIcons.BankCards, "卡片"),
            TabIcon(Icons.Rounded.Settings, "设置"),
        )
    }

    LaunchedEffect(pagerState.settledPage) { selectedTab = pagerState.settledPage }

    val surfaceColor = MiuixTheme.colorScheme.surface
    val bottomBarBackdrop = rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }

    CompositionLocalProvider(
        LocalFloatingBottomBarTabScale provides { 1f },
    ) {
        Scaffold(
            bottomBar = {
                if (viewModel.enableFloatingBar) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        FloatingBottomBar(
                            modifier = Modifier.padding(bottom = 12.dp + navPadding),
                            selectedIndex = { selectedTab },
                            onSelected = { index ->
                                selectedTab = index
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                            backdrop = bottomBarBackdrop,
                            tabsCount = 4,
                            isBlurEnabled = viewModel.enableGlass,
                        ) {
                            tabIcons.forEachIndexed { index, tab ->
                                FloatingBottomBarItem(
                                    onClick = {
                                        selectedTab = index
                                        scope.launch { pagerState.animateScrollToPage(index) }
                                    },
                                    modifier = Modifier.defaultMinSize(minWidth = 76.dp)
                                ) {
                                    Icon(
                                        imageVector = tab.imageVector,
                                        contentDescription = tab.label,
                                        tint = MiuixTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp,
                                        color = MiuixTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Visible
                                    )
                                }
                            }
                        }
                    }
                } else {
                    NavigationBar(
                        modifier = Modifier.fillMaxWidth(),
                        color = MiuixTheme.colorScheme.surface,
                        content = {
                            tabIcons.forEachIndexed { index, tab ->
                                NavigationBarItem(
                                    modifier = Modifier.weight(1f),
                                    icon = tab.imageVector,
                                    label = tab.label,
                                    selected = selectedTab == index,
                                    onClick = {
                                        selectedTab = index
                                        scope.launch { pagerState.animateScrollToPage(index) }
                                    }
                                )
                            }
                        }
                    )
                }
            },
            contentWindowInsets = WindowInsets.systemBars
                .add(WindowInsets.displayCutout)
                .only(WindowInsetsSides.Horizontal)
        ) { innerPadding ->
            HorizontalPager(
                modifier = Modifier.fillMaxSize().layerBackdrop(bottomBarBackdrop),
                state = pagerState,
                beyondViewportPageCount = 1,
            ) { page ->
                when (page) {
                    0 -> DashboardScreen(viewModel, bottomPadding = innerPadding.calculateBottomPadding())
                    1 -> CapsuleScreen(viewModel, bottomPadding = innerPadding.calculateBottomPadding())
                    2 -> CardScreen(viewModel, bottomPadding = innerPadding.calculateBottomPadding())
                    3 -> AboutScreen(viewModel, bottomPadding = innerPadding.calculateBottomPadding())
                }
            }
        }
    }

    val dialog = viewModel.activeDialog
    when (dialog) {
        DialogType.LEFT_VALUE -> ValueInputDialog(
            title = "左边距", hint = "默认 540", value = viewModel.leftValue, defaultValue = 540f,
            show = true, onConfirm = { viewModel.applyLeftValue(it); viewModel.dismissDialog() },
            onDismiss = { viewModel.dismissDialog() }
        )
        DialogType.RIGHT_VALUE -> ValueInputDialog(
            title = "右边距", hint = "默认 540", value = viewModel.rightValue, defaultValue = 540f,
            show = true, onConfirm = { viewModel.applyRightValue(it); viewModel.dismissDialog() },
            onDismiss = { viewModel.dismissDialog() }
        )
        DialogType.CARD_DIVIDER_HEIGHT -> ValueInputDialog(
            title = "卡片间距", hint = "默认 8", value = viewModel.cardDividerHeight, defaultValue = 8f,
            show = true, onConfirm = { viewModel.applyCardDividerHeight(it); viewModel.dismissDialog() },
            onDismiss = { viewModel.dismissDialog() }
        )
        DialogType.CARD_HEIGHT -> ValueInputDialog(
            title = "卡片高度", hint = "默认 8", value = viewModel.cardHeight, defaultValue = 8f,
            show = true, onConfirm = { viewModel.applyCardHeight(it); viewModel.dismissDialog() },
            onDismiss = { viewModel.dismissDialog() }
        )
        DialogType.CARD_CORNER_RADIUS -> ValueInputDialog(
            title = "卡片圆角", hint = "默认 20", value = viewModel.cardCornerRadius, defaultValue = 20f,
            show = true, onConfirm = { viewModel.applyCardCornerRadius(it); viewModel.dismissDialog() },
            onDismiss = { viewModel.dismissDialog() }
        )
        DialogType.HOTSPOT_DURATION -> ValueInputDialog(
            title = "热点胶囊持续时间 (min)", hint = "默认 1", value = viewModel.hotspotDurationMin, defaultValue = 1f,
            show = true, onConfirm = { viewModel.applyHotspotDurationMin(it); viewModel.dismissDialog() },
            onDismiss = { viewModel.dismissDialog() }
        )
        DialogType.MUSIC_DURATION -> ValueInputDialog(
            title = "音乐胶囊持续时间 (min)", hint = "默认 5", value = viewModel.musicDurationMin, defaultValue = 5f,
            show = true, onConfirm = { viewModel.applyMusicDurationMin(it); viewModel.dismissDialog() },
            onDismiss = { viewModel.dismissDialog() }
        )
        DialogType.ARTWORK_BG_COLOR -> ColorPickerDialog(
            title = "媒体卡片背景色",
            initialColor = viewModel.artworkBgColor.toComposeColor(),
            defaultColor = "#4D000000".toComposeColor(),
            onDismiss = { viewModel.dismissDialog() },
            onConfirm = { viewModel.applyArtworkBgColor(it.toArgbHex()); viewModel.dismissDialog() }
        )
        DialogType.CAPSULE_BG_COLOR -> ColorPickerDialog(
            title = "胶囊背景色",
            initialColor = viewModel.capsuleBgColor.toComposeColor(),
            defaultColor = "#ff000000".toComposeColor(),
            onDismiss = { viewModel.dismissDialog() },
            onConfirm = { viewModel.applyCapsuleBgColor(it.toArgbHex()); viewModel.dismissDialog() }
        )
        DialogType.CAPSULE_STROKE_COLOR -> ColorPickerDialog(
            title = "胶囊边框色",
            initialColor = viewModel.capsuleStrokeColor.toComposeColor(),
            defaultColor = "#40959595".toComposeColor(),
            onDismiss = { viewModel.dismissDialog() },
            onConfirm = { viewModel.applyCapsuleStrokeColor(it.toArgbHex()); viewModel.dismissDialog() }
        )
        DialogType.MEDIA_CARD_STROKE_COLOR -> ColorPickerDialog(
            title = "媒体卡片边框色",
            initialColor = viewModel.mediaCardStrokeColor.toComposeColor(),
            defaultColor = "#4D000000".toComposeColor(),
            onDismiss = { viewModel.dismissDialog() },
            onConfirm = { viewModel.applyMediaCardStrokeColor(it.toArgbHex()); viewModel.dismissDialog() }
        )
        null -> {}
    }
}
