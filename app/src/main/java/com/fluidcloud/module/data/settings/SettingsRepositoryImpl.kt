package com.fluidcloud.module.data.settings

import android.util.Log
import com.fluidcloud.module.domain.settings.FluidSettings
import com.fluidcloud.module.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class SettingsRepositoryImpl : SettingsRepository {

    companion object {
        const val PRIVATE_PATH = "/data/data/com.fluidcloud.module/files/config.json"
        const val SHARED_PATH = "/data/local/tmp/fluidcloud_config.json"
    }

    private val _settings = MutableStateFlow(FluidSettings())
    private val _isModuleLoaded = MutableStateFlow(false)

    override val settings: Flow<FluidSettings> = _settings.asStateFlow()
    override val isModuleLoaded: Flow<Boolean> = _isModuleLoaded.asStateFlow()

    private var loaded = false

    init {
        ensureLoaded()
    }

    override suspend fun getSettings(): FluidSettings = _settings.value

    override suspend fun updateSettings(transform: (FluidSettings) -> FluidSettings) {
        _settings.value = transform(_settings.value)
        persistToFile()
        syncToShared()
    }

    override suspend fun forceReload() {
        loaded = false
        ensureLoaded()
    }

    private fun ensureLoaded() {
        if (loaded) return
        try {
            val file = File(SHARED_PATH)
            if (file.exists()) {
                _isModuleLoaded.value = true
                val json = JSONObject(file.readText())
                _settings.value = FluidSettings(
                    themeMode = json.optInt("themeMode", 0),
                    enableFloatingBottomBar = json.optBoolean("enableFloatingBottomBar", true),
                    enableFloatingBottomBarBlur = json.optBoolean("enableFloatingBottomBarBlur", true),
                    hookEnabled = json.optBoolean("hookEnabled", false),
                    leftValue = json.optDouble("leftValue", 540.0).toFloat(),
                    rightValue = json.optDouble("rightValue", 540.0).toFloat(),
                    capsuleHeight = json.optInt("capsuleHeight", 30),
                    capsuleIconSize = json.optInt("capsuleIconSize", 20),
                    coverRoundRect = json.optBoolean("coverRoundRect", true),
                    bgCornerRadius = json.optInt("bgCornerRadius", 20),
                    cardDividerHeight = json.optInt("cardDividerHeight", 8),
                    cardHeight = json.optInt("cardHeight", 8),
                    cardCornerRadius = json.optInt("cardCornerRadius", 20),
                    artworkBgColor = json.optString("artworkBgColor", "#4D000000"),
                    capsuleBgColor = json.optString("capsuleBgColor", "#ff000000"),
                    capsuleStrokeColor = json.optString("capsuleStrokeColor", "#40959595"),
                    mediaCardStrokeColor = json.optString("mediaCardStrokeColor", "#4D000000"),
                    hotspotCapsuleDuration = json.optInt("hotspotCapsuleDuration", 60000),
                    musicCapsuleDuration = json.optInt("musicCapsuleDuration", 300000),
                    capsuleGlowEnabled = json.optBoolean("capsuleGlowEnabled", false),
                    capsuleGlowSpeed = json.optDouble("capsuleGlowSpeed", 0.3).toFloat(),
                    capsuleGlowIntensity = json.optDouble("capsuleGlowIntensity", 0.8).toFloat(),
                )
            }
        } catch (_: Exception) {}
        loaded = true
    }

    private fun persistToFile() {
        try {
            val s = _settings.value
            val file = File(PRIVATE_PATH)
            file.parentFile?.mkdirs()
            val json = JSONObject().apply {
                put("themeMode", s.themeMode)
                put("enableFloatingBottomBar", s.enableFloatingBottomBar)
                put("enableFloatingBottomBarBlur", s.enableFloatingBottomBarBlur)
                put("hookEnabled", s.hookEnabled)
                put("leftValue", s.leftValue.toDouble())
                put("rightValue", s.rightValue.toDouble())
                put("capsuleHeight", s.capsuleHeight)
                put("coverRoundRect", s.coverRoundRect)
                put("bgCornerRadius", s.bgCornerRadius)
                put("cardDividerHeight", s.cardDividerHeight)
                put("cardHeight", s.cardHeight)
                put("cardCornerRadius", s.cardCornerRadius)
                put("artworkBgColor", s.artworkBgColor)
                put("capsuleBgColor", s.capsuleBgColor)
                put("capsuleStrokeColor", s.capsuleStrokeColor)
                put("mediaCardStrokeColor", s.mediaCardStrokeColor)
                put("hotspotCapsuleDuration", s.hotspotCapsuleDuration)
                put("musicCapsuleDuration", s.musicCapsuleDuration)
                put("capsuleGlowEnabled", s.capsuleGlowEnabled)
                put("capsuleGlowSpeed", s.capsuleGlowSpeed.toDouble())
                put("capsuleGlowIntensity", s.capsuleGlowIntensity.toDouble())
                put("capsuleIconSize", s.capsuleIconSize)
            }

            FileOutputStream(file).use { os ->
                os.write(json.toString(2).encodeToByteArray())
                os.flush()
                os.fd.sync()
            }
        } catch (_: Exception) {}
    }

    private fun syncToShared() {
        try {
            Runtime.getRuntime().exec(arrayOf("su", "-c",
                "cp $PRIVATE_PATH $SHARED_PATH && chmod 644 $SHARED_PATH")).waitFor()
        } catch (_: Exception) {}
    }
}
