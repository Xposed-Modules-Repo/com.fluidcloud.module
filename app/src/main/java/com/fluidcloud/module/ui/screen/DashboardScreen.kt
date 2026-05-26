package com.fluidcloud.module.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fluidcloud.module.BuildConfig
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import com.fluidcloud.module.ui.theme.HorizontalMargin
import com.fluidcloud.module.ui.theme.SectionSpacing
import com.fluidcloud.module.ui.theme.statusCardBgActiveDark
import com.fluidcloud.module.ui.theme.statusCardBgActiveLight
import com.fluidcloud.module.ui.theme.statusCardBgInactiveDark
import com.fluidcloud.module.ui.theme.statusCardBgInactiveLight
import com.fluidcloud.module.ui.theme.statusIconActive
import com.fluidcloud.module.ui.theme.statusIconDeactivated
import com.fluidcloud.module.ui.TestNotificationService

@Composable
fun DashboardScreen(
    viewModel: SettingsViewModel,
    bottomPadding: Dp = 0.dp
) {
    val moduleLoaded = viewModel.isModuleLoaded
    val hookEnabled = viewModel.hookEnabled
    val isActive = moduleLoaded && hookEnabled
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                color = MiuixTheme.colorScheme.surface,
                title = "主页",
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
                FluidStatusCard(
                    isActive = isActive,
                    isModuleLoaded = moduleLoaded,
                    onClick = { viewModel.toggleHook() }
                )
            }
            item { Spacer(Modifier.height(SectionSpacing)) }
            item {
                Card {
                    SwitchPreference(
                        title = "启用 Hook",
                        summary = "控制流体云 UI 修改是否生效",
                        checked = hookEnabled,
                        onCheckedChange = { viewModel.applyHookEnabled(it) }
                    )
                    ArrowPreference(
                        title = "重启系统界面",
                        summary = "立即杀死两个作用域进程 使设置生效",
                        onClick = { restartSystemUI() }
                    )
                }
            }
            item { Spacer(Modifier.height(SectionSpacing)) }
            item {
                Card {
                    ArrowPreference(
                        title = "发送测试通知",
                        summary = "发送一条测试通知来预览流体云效果",
                        onClick = { TestNotificationService.start(context) }
                    )
                    ArrowPreference(
                        title = "关闭测试通知",
                        summary = "停止测试",
                        onClick = { TestNotificationService.stop(context) }
                    )
                }
            }
            item { Spacer(Modifier.height(SectionSpacing)) }
            item {
                Card {
                    BasicComponent(title = "应用名称", summary = "FluidCloud")
                    BasicComponent(title = "作者", summary = "Coolapk@那泛滥的思绪")
                    BasicComponent(title = "功能", summary = "调整 ColorOS 16 流体云 UI 布局、尺寸、颜色等参数，实现个性化定制。")
                    BasicComponent(title = "使用说明", summary = "修改参数后，点击「重启系统界面」或手动重启 SystemUI 即可生效。")
                }
            }
        }
    }
}

@Composable
private fun FluidStatusCard(
    isActive: Boolean,
    isModuleLoaded: Boolean,
    onClick: () -> Unit
) {
    val isDark = MiuixTheme.colorScheme.surface.luminance() < 0.5f

    val containerColor = if (isModuleLoaded) {
        if (isDark) statusCardBgActiveDark else statusCardBgActiveLight
    } else {
        if (isDark) statusCardBgInactiveDark else statusCardBgInactiveLight
    }

    val textContentColor = MiuixTheme.colorScheme.onSurface
    val descTextColor = textContentColor.copy(alpha = 0.8f)

    val iconTint = if (isModuleLoaded) statusIconActive else statusIconDeactivated
    val statusIcon = if (isModuleLoaded) Icons.Rounded.CheckCircleOutline else Icons.Rounded.ErrorOutline

    val (titleText, descText, detailText) = when {
        isActive -> Triple("已激活", "Hook 已启用，所有修改已生效", "ColorOS 16  ·  版本 ${BuildConfig.VERSION_NAME}")
        isModuleLoaded -> Triple("已加载", "模块已加载，但 Hook 尚未开启", "请先启用 Hook 开关")
        else -> Triple("未激活", "模块尚未加载到 SystemUI 进程", "请在 LSPosed 中勾选模块并重启系统界面")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(color = containerColor),
        onClick = onClick,
        showIndication = true,
        pressFeedbackType = PressFeedbackType.Tilt
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.matchParentSize().offset(50.dp, 38.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    modifier = Modifier.size(160.dp),
                    imageVector = statusIcon,
                    tint = iconTint,
                    contentDescription = null
                )
            }
            Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                Text(text = titleText, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = textContentColor)
                Spacer(Modifier.height(4.dp))
                Text(text = descText, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = descTextColor)
                Spacer(Modifier.height(24.dp))
                Text(text = detailText, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = textContentColor)
            }
        }
    }
}

private fun Color.luminance(): Float = 0.299f * red + 0.587f * green + 0.114f * blue

fun restartSystemUI() {
    try {
        Runtime.getRuntime().exec(arrayOf("su", "-c",
            "pkill -f com.android.systemui; pkill -f com.oplus.systemui.plugins"))
    } catch (_: Exception) {}
}
