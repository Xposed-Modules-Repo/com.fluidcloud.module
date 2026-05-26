package com.fluidcloud.module.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

private val LocalIsDark = staticCompositionLocalOf { false }
private val LocalThemeMode = staticCompositionLocalOf { ThemeMode.SYSTEM }

object FluidCloudTheme {
    val isDark: Boolean
        @Composable @ReadOnlyComposable get() = LocalIsDark.current
    val themeMode: ThemeMode
        @Composable @ReadOnlyComposable get() = LocalThemeMode.current
}

@Composable
fun FluidCloudTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    CompositionLocalProvider(
        LocalIsDark provides isDark,
        LocalThemeMode provides themeMode
    ) {
        val colors = if (isDark) darkColorScheme() else lightColorScheme()
        MiuixTheme(colors = colors) {
            content()
        }
    }
}
