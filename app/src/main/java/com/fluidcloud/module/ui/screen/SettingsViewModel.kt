package com.fluidcloud.module.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fluidcloud.module.data.settings.SettingsRepositoryImpl
import com.fluidcloud.module.domain.settings.FluidSettings
import com.fluidcloud.module.domain.settings.SettingsRepository
import com.fluidcloud.module.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SettingsViewModel(
    private val repository: SettingsRepository = SettingsRepositoryImpl()
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    var hookEnabled by mutableStateOf(false); private set
    var isModuleLoaded by mutableStateOf(false); private set
    var themeMode by mutableStateOf(ThemeMode.SYSTEM); private set
    var enableFloatingBar by mutableStateOf(true); private set
    var enableGlass by mutableStateOf(true); private set

    var leftValue by mutableFloatStateOf(540f); private set
    var rightValue by mutableFloatStateOf(540f); private set
    var capsuleHeight by mutableFloatStateOf(30f); private set
    var capsuleIconSize by mutableFloatStateOf(20f); private set
    var bgCornerRadius by mutableFloatStateOf(20f); private set
    var coverRoundRect by mutableStateOf(true); private set

    var cardDividerHeight by mutableFloatStateOf(8f); private set
    var cardHeight by mutableFloatStateOf(8f); private set
    var cardCornerRadius by mutableFloatStateOf(20f); private set

    var artworkBgColor by mutableStateOf("#4D000000"); private set
    var capsuleBgColor by mutableStateOf("#ff000000"); private set
    var capsuleStrokeColor by mutableStateOf("#40959595"); private set
    var mediaCardStrokeColor by mutableStateOf("#4D000000"); private set

    var hotspotDurationMin by mutableFloatStateOf(1f); private set
    var musicDurationMin by mutableFloatStateOf(5f); private set

    var capsuleGlowEnabled by mutableStateOf(false); private set
    var capsuleGlowSpeed by mutableFloatStateOf(0.3f); private set
    var capsuleGlowIntensity by mutableFloatStateOf(0.8f); private set

    var activeDialog by mutableStateOf<DialogType?>(null); private set
    var themeVersion by mutableIntStateOf(0); private set

    fun refreshTheme() { themeVersion++ }
    fun showDialog(type: DialogType) { activeDialog = type }
    fun dismissDialog() { activeDialog = null }

    fun load() {
        scope.launch {
            repository.forceReload()
            val s = repository.getSettings()
            isModuleLoaded = repository.isModuleLoaded.first()
            hookEnabled = s.hookEnabled
            themeMode = ThemeMode.fromValue(s.themeMode)
            enableFloatingBar = s.enableFloatingBottomBar
            enableGlass = s.enableFloatingBottomBarBlur
            leftValue = s.leftValue
            rightValue = s.rightValue
            capsuleHeight = s.capsuleHeight.toFloat()
            capsuleIconSize = s.capsuleIconSize.toFloat()
            bgCornerRadius = s.bgCornerRadius.toFloat()
            coverRoundRect = s.coverRoundRect
            cardDividerHeight = s.cardDividerHeight.toFloat()
            cardHeight = s.cardHeight.toFloat()
            cardCornerRadius = s.cardCornerRadius.toFloat()
            artworkBgColor = s.artworkBgColor
            capsuleBgColor = s.capsuleBgColor
            capsuleStrokeColor = s.capsuleStrokeColor
            mediaCardStrokeColor = s.mediaCardStrokeColor
            hotspotDurationMin = s.hotspotCapsuleDuration / 60000f
            musicDurationMin = s.musicCapsuleDuration / 60000f
            capsuleGlowEnabled = s.capsuleGlowEnabled
            capsuleGlowSpeed = s.capsuleGlowSpeed
            capsuleGlowIntensity = s.capsuleGlowIntensity
        }
    }

    fun toggleHook() { hookEnabled = !hookEnabled; persist { it.copy(hookEnabled = hookEnabled) } }
    fun applyHookEnabled(v: Boolean) { hookEnabled = v; persist { it.copy(hookEnabled = v) } }
    fun applyThemeMode(mode: ThemeMode) { themeMode = mode; persist { it.copy(themeMode = ThemeMode.toInt(mode)) }; refreshTheme() }
    fun applyFloatingBar(v: Boolean) {
        enableFloatingBar = v
        if (!v) { enableGlass = false; persist { it.copy(enableFloatingBottomBar = false, enableFloatingBottomBarBlur = false) } }
        else persist { it.copy(enableFloatingBottomBar = true) }
        refreshTheme()
    }
    fun applyGlass(v: Boolean) { enableGlass = v; persist { it.copy(enableFloatingBottomBarBlur = v) }; refreshTheme() }

    fun applyLeftValue(v: Float) { leftValue = v; persist { it.copy(leftValue = v) } }
    fun applyRightValue(v: Float) { rightValue = v; persist { it.copy(rightValue = v) } }
    fun applyCapsuleHeight(v: Float) { capsuleHeight = v; persist { it.copy(capsuleHeight = v.roundToInt()) } }
    fun applyCapsuleIconSize(v: Float) { capsuleIconSize = v; persist { it.copy(capsuleIconSize = v.roundToInt()) } }
    fun applyBgCornerRadius(v: Float) { bgCornerRadius = v; persist { it.copy(bgCornerRadius = v.roundToInt()) } }
    fun applyCoverRoundRect(v: Boolean) { coverRoundRect = v; persist { it.copy(coverRoundRect = v) } }
    fun applyCardDividerHeight(v: Float) { cardDividerHeight = v; persist { it.copy(cardDividerHeight = v.roundToInt()) } }
    fun applyCardHeight(v: Float) { cardHeight = v; persist { it.copy(cardHeight = v.roundToInt()) } }
    fun applyCardCornerRadius(v: Float) { cardCornerRadius = v; persist { it.copy(cardCornerRadius = v.roundToInt()) } }
    fun applyArtworkBgColor(v: String) { artworkBgColor = v; persist { it.copy(artworkBgColor = v) } }
    fun applyCapsuleBgColor(v: String) { capsuleBgColor = v; persist { it.copy(capsuleBgColor = v) } }
    fun applyCapsuleStrokeColor(v: String) { capsuleStrokeColor = v; persist { it.copy(capsuleStrokeColor = v) } }
    fun applyMediaCardStrokeColor(v: String) { mediaCardStrokeColor = v; persist { it.copy(mediaCardStrokeColor = v) } }
    fun applyHotspotDurationMin(v: Float) { hotspotDurationMin = v; persist { it.copy(hotspotCapsuleDuration = (v * 60000).roundToInt()) } }
    fun applyMusicDurationMin(v: Float) { musicDurationMin = v; persist { it.copy(musicCapsuleDuration = (v * 60000).roundToInt()) } }
    fun applyCapsuleGlowEnabled(v: Boolean) { capsuleGlowEnabled = v; persist { it.copy(capsuleGlowEnabled = v) } }
    fun applyCapsuleGlowSpeed(v: Float) { capsuleGlowSpeed = v; persist { it.copy(capsuleGlowSpeed = v) } }
    fun applyCapsuleGlowIntensity(v: Float) { capsuleGlowIntensity = v; persist { it.copy(capsuleGlowIntensity = v) } }

    private fun persist(transform: (FluidSettings) -> FluidSettings) {
        scope.launch { repository.updateSettings(transform) }
    }
}
