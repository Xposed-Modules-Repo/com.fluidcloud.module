package com.fluidcloud.module.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.isRenderEffectSupported
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Remember a LayerBackdrop with solid background for Miuix theme,
 * preventing alpha-blending artifacts.
 */
@Composable
fun rememberFluidCloudBlurBackdrop(enableBlur: Boolean): LayerBackdrop? {
    if (!enableBlur || !isRenderEffectSupported()) return null
    val surfaceColor = MiuixTheme.colorScheme.surface
    return rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
}

/**
 * Get app bar background color: transparent if blur is active, else surface color.
 */
@Composable
fun LayerBackdrop?.getAppBarColor(): Color =
    this?.let { Color.Transparent } ?: MiuixTheme.colorScheme.surface

/**
 * Apply glassmorphism blur effect using Miuix Backdrop.
 */
@Composable
fun Modifier.fluidCloudBlurEffect(
    backdrop: LayerBackdrop?,
    enabled: Boolean = true,
    blurRadius: Float = 25f,
    shape: Shape = RectangleShape
): Modifier {
    if (!enabled || backdrop == null) return this

    val blendColor = MiuixTheme.colorScheme.surface.copy(alpha = 0.8f)

    return this.then(
        Modifier.textureBlur(
            backdrop = backdrop,
            shape = shape,
            blurRadius = blurRadius,
            colors = BlurColors(
                blendColors = listOf(
                    BlendColorEntry(color = blendColor)
                )
            )
        )
    )
}

/**
 * Remember a LayerBackdrop for Material 3 theme.
 */
@Composable
fun rememberMaterial3BlurBackdrop(enableBlur: Boolean): LayerBackdrop? {
    if (!enableBlur || !isRenderEffectSupported()) return null
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    return rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
}

/**
 * Apply glassmorphism blur using Material 3 color schemes.
 */
@Composable
fun Modifier.fluidCloudMaterial3BlurEffect(
    backdrop: LayerBackdrop?,
    enabled: Boolean = true,
    blurRadius: Float = 25f,
    shape: Shape = RectangleShape
): Modifier {
    if (!enabled || backdrop == null) return this

    val blendColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)

    return this.then(
        Modifier.textureBlur(
            backdrop = backdrop,
            shape = shape,
            blurRadius = blurRadius,
            colors = BlurColors(
                blendColors = listOf(
                    BlendColorEntry(color = blendColor)
                )
            )
        )
    )
}
