package com.fluidcloud.module.ui.screen

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import com.fluidcloud.module.ui.theme.HorizontalMargin
import com.fluidcloud.module.ui.theme.SectionSpacing
import com.fluidcloud.module.ui.theme.ThemeMode

private val themeModeOptions = listOf("跟随系统", "浅色", "深色")

@Composable
fun AboutScreen(
    viewModel: SettingsViewModel,
    bottomPadding: Dp = 0.dp
) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                color = MiuixTheme.colorScheme.surface,
                title = "设置",
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
            item(key = "theme_card") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        tabs = themeModeOptions,
                        selectedTabIndex = ThemeMode.toInt(viewModel.themeMode),
                        onTabSelected = { viewModel.applyThemeMode(ThemeMode.fromValue(it)) },
                        height = 48.dp,
                    )
                }
            }
            item(key = "spacer1") { Spacer(Modifier.height(SectionSpacing)) }
            item(key = "bar_card") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    SwitchPreference(
                        title = "悬浮底栏", summary = "iOS 风格悬浮导航栏",
                        checked = viewModel.enableFloatingBar,
                        onCheckedChange = { viewModel.applyFloatingBar(it) }
                    )
                    AnimatedVisibility(visible = viewModel.enableFloatingBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        SwitchPreference(
                            title = "液态玻璃", summary = "悬浮底栏毛玻璃模糊效果",
                            checked = viewModel.enableGlass,
                            onCheckedChange = { viewModel.applyGlass(it) }
                        )
                    }
                }
            }
        }
    }
}
