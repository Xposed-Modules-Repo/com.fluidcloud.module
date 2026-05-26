package com.fluidcloud.module.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.fluidcloud.module.ui.screen.SettingsScreen
import com.fluidcloud.module.ui.screen.SettingsViewModel
import com.fluidcloud.module.ui.theme.FluidCloudTheme

@Composable
fun FluidCloudApp() {
    val context = LocalContext.current
    val viewModel = remember { SettingsViewModel() }
    val themeMode = viewModel.themeMode

    LaunchedEffect(Unit) {
        try {
            Runtime.getRuntime().exec(
                arrayOf("su", "-c", "pm grant ${context.packageName} android.permission.POST_PROMOTED_NOTIFICATIONS")
            ).waitFor()
        } catch (_: Exception) {}
    }

    FluidCloudTheme(themeMode = themeMode) {
        SettingsScreen(viewModel = viewModel)
    }
}
