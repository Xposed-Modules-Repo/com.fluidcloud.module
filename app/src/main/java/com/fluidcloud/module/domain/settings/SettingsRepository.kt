package com.fluidcloud.module.domain.settings

import kotlinx.coroutines.flow.Flow

data class FluidSettings(
    val themeMode: Int = 0,
    val enableFloatingBottomBar: Boolean = true,
    val enableFloatingBottomBarBlur: Boolean = true,
    val hookEnabled: Boolean = false,
    val leftValue: Float = 540f,
    val rightValue: Float = 540f,
    val capsuleHeight: Int = 30,
    val capsuleIconSize: Int = 20,
    val coverRoundRect: Boolean = true,
    val bgCornerRadius: Int = 20,
    val cardDividerHeight: Int = 8,
    val cardHeight: Int = 8,
    val cardCornerRadius: Int = 20,
    val artworkBgColor: String = "#4D000000",
    val capsuleBgColor: String = "#ff000000",
    val capsuleStrokeColor: String = "#40959595",
    val mediaCardStrokeColor: String = "#4D000000",
    val hotspotCapsuleDuration: Int = 60000,
    val musicCapsuleDuration: Int = 300000,
    val capsuleGlowEnabled: Boolean = false,
    val capsuleGlowSpeed: Float = 0.3f,
    val capsuleGlowIntensity: Float = 0.8f,
)

interface SettingsRepository {
    val settings: Flow<FluidSettings>
    val isModuleLoaded: Flow<Boolean>

    suspend fun getSettings(): FluidSettings

    suspend fun updateSettings(transform: (FluidSettings) -> FluidSettings)

    suspend fun forceReload()
}
