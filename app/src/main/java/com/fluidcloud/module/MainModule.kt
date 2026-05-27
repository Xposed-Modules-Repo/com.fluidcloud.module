package com.fluidcloud.module

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import com.fluidcloud.module.glow.CapsuleGlowEngine

class MainModule : XposedModule() {

    companion object {
        /** capsule foreground GradientDrawable -> target stroke color */
        val gdStrokeColorMap = java.util.WeakHashMap<android.graphics.drawable.GradientDrawable, Int>()
        /** prevent recursive setStroke calls */
        private val inStrokeHook = ThreadLocal<Boolean>()

        private const val CAPSULE_VIEW_CLS =
            "com.oplus.systemui.plugins.seedling.capsule.ui.view.CapsuleView"
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        Settings.ensureLoaded()
        when (param.packageName) {
            "com.android.systemui" -> {
                hookFluidCloud(param.defaultClassLoader)
                hookCapsuleHeight()
                hookCapsuleIconSize()
                hookCardHeight()
                hookCardDividerHeight()
                hookCardCornerRadius()
                hookCardBgCornerRadius()
                hookCoverRoundRect()
                hookBgCornerRadius()
                hookFgCornerRadius()
                hookCapsuleStrokeColor()
                hookCapsuleStrokeGradientDrawable()
                hookMediaCardStrokeColor()
                hookServiceConfigParsing()
                hookSmoothRadiusOff()
                hookCardFgCornerRadius()
                hookCapsuleDraw()
                hookCapsuleAttachState()
                hookArtworkBgColor(param.defaultClassLoader)
                hookMediaCardStrokeGradientDrawable()
            }
            "com.oplus.systemui.plugins" -> {
                hookFluidCloud(param.defaultClassLoader)
            }
        }
    }

    // =========================================================================
    // Existing hooks (unchanged)
    // =========================================================================

    private fun hookFluidCloud(classLoader: ClassLoader) {
        try {
            val clazz = classLoader.loadClass(
                "com.oplus.systemui.statusbar.seeding.SeedlingPluginManager\$holeRectListener\$1"
            )
            val method = clazz.declaredMethods.first {
                it.name == "onRectChanged"
                    && it.parameterTypes.size == 1
                    && it.parameterTypes[0] == RectF::class.java
            }
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val r = chain.getArgs()[0] as? RectF ?: return chain.proceed()
                    r.left = Settings.leftValue
                    r.right = Settings.rightValue
                    return chain.proceed()
                }
            })
        } catch (e: Throwable) {
            Log.w("FluidCloud", "hookFluidCloud failed", e)
        }
    }

    private fun hookCapsuleHeight() {
        val hookBody = object : XposedInterface.Hooker {
            override fun intercept(chain: XposedInterface.Chain): Any? {
                if (!Settings.hookEnabled) return chain.proceed()
                val res = chain.getThisObject() as? android.content.res.Resources
                    ?: return chain.proceed()
                val id = chain.getArgs()[0] as Int
                val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                if (name != "capsule_max_height_size"
                    && name != "capsule_layout_max_height"
                    && name != "capsule_layout_max_height_small") return chain.proceed()
                val px = android.util.TypedValue.applyDimension(
                    android.util.TypedValue.COMPLEX_UNIT_DIP,
                    Settings.capsuleHeight.toFloat(),
                    res.displayMetrics
                )
                return (px + 0.5f).toInt()
            }
        }
        try {
            val m1 = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelOffset", Integer.TYPE)
            hook(m1).intercept(hookBody)
        } catch (_: Throwable) {}
        try {
            val m2 = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)
            hook(m2).intercept(hookBody)
        } catch (_: Throwable) {}
    }

    private fun hookCapsuleIconSize() {
        val hookBody = object : XposedInterface.Hooker {
            override fun intercept(chain: XposedInterface.Chain): Any? {
                if (!Settings.hookEnabled) return chain.proceed()
                val res = chain.getThisObject() as? android.content.res.Resources
                    ?: return chain.proceed()
                val id = chain.getArgs()[0] as Int
                val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                if (name != "capsule_icon_size") return chain.proceed()
                val px = android.util.TypedValue.applyDimension(
                    android.util.TypedValue.COMPLEX_UNIT_DIP,
                    Settings.capsuleIconSize.toFloat(),
                    res.displayMetrics
                )
                return (px + 0.5f).toInt()
            }
        }
        try {
            val m1 = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelOffset", Integer.TYPE)
            hook(m1).intercept(hookBody)
        } catch (_: Throwable) {}
        try {
            val m2 = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)
            hook(m2).intercept(hookBody)
        } catch (_: Throwable) {}
    }

    private fun hookCardHeight() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "offset_between_status_bar_and_card") return chain.proceed()
                    val px = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardHeight.toFloat(),
                        res.displayMetrics
                    )
                    return (px + 0.5f).toInt()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCardDividerHeight() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "capsule_card_divider_height") return chain.proceed()
                    val px = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardDividerHeight.toFloat(),
                        res.displayMetrics
                    )
                    return (px + 0.5f).toInt()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCardCornerRadius() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getDimensionPixelSize", Integer.TYPE)

            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "round_corner_radius_fluid_cloud"
                        && name != "card_corner_radius") return chain.proceed()
                    val px = android.util.TypedValue.applyDimension(
                        android.util.TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardCornerRadius.toFloat(),
                        res.displayMetrics
                    )
                    return (px + 0.5f).toInt()
                }
            })
        } catch (_: Throwable) {}
    }

    /**
     * Hook CardBackgroundView.setBackgroundDrawable to apply user's corner radius
     * to the card background drawable (which has hardcoded 20dp in XML).
     */
    private fun hookCardBgCornerRadius() {
        try {
            val method = View::class.java.getMethod("setBackgroundDrawable", Drawable::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val drawable = chain.getArgs()[0] as? Drawable ?: return chain.proceed()
                    if (!Settings.hookEnabled) return chain.proceed()
                    val view = chain.getThisObject() as? View ?: return chain.proceed()
                    if (view::class.java.name != "com.oplus.systemui.plugins.seedling.card.ui.view.CardBackgroundView") return chain.proceed()
                    val gd = drawable as? GradientDrawable ?: return chain.proceed()
                    val px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardCornerRadius.toFloat(),
                        view.resources.displayMetrics
                    )
                    gd.setCornerRadius(px)
                    return chain.proceed()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCardFgCornerRadius() {
        try {
            val method = FrameLayout::class.java
                .getMethod("setForeground", Drawable::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val drawable = chain.getArgs()[0] as? Drawable ?: return chain.proceed()
                    if (!Settings.hookEnabled) return chain.proceed()
                    val view = chain.getThisObject() as? View ?: return chain.proceed()
                    if (view::class.java.name != "com.oplus.systemui.plugins.seedling.card.ui.view.CardView") return chain.proceed()
                    val gd = drawable as? GradientDrawable ?: return chain.proceed()
                    val px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        Settings.cardCornerRadius.toFloat(),
                        view.resources.displayMetrics
                    )
                    gd.setCornerRadius(px)
                    return chain.proceed()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCoverRoundRect() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getString", Integer.TYPE)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled || !Settings.coverRoundRect) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources
                        ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name == "shapeArtworkCircle" || name == "shapeArtworkRoundRectangle") {
                        return "round-rectangle"
                    }
                    return chain.proceed()
                }
            })
        } catch (_: Throwable) {}
    }


    /**
     * Disable <smoothG2-weight> rendering when capsule radius != default 20dp,
     * preserving smooth effect at the default radius to avoid ghost artifacts.
     */
    private fun hookSmoothRadiusOff() {
        try {
            val clazz = Class.forName("com.oplus.view.OplusSmoothRoundedManager")
            val method = clazz.getMethod("isSmoothRadiusOn")
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    if (Settings.bgCornerRadius == 20) return chain.proceed()
                    return false
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookBgCornerRadius() {
        try {
            val method = View::class.java.getMethod("setBackground", Drawable::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val drawable = chain.getArgs()[0] as? Drawable ?: return chain.proceed()
                    if (!Settings.hookEnabled) return chain.proceed()
                    val view = chain.getThisObject() as? View ?: return chain.proceed()
                    if (view::class.java.name != CAPSULE_VIEW_CLS) return chain.proceed()
                    val gd = drawable as? GradientDrawable ?: return chain.proceed()
                    gd.setCornerRadius(cornerRadiusPx(view))
                    gd.setColor(Color.parseColor(Settings.capsuleBgColor))
                    return chain.proceed()
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookFgCornerRadius() {
        try {
            val method = FrameLayout::class.java
                .getMethod("setForeground", Drawable::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val drawable = chain.getArgs()[0] as? Drawable ?: return chain.proceed()
                    if (!Settings.hookEnabled) return chain.proceed()
                    val view = chain.getThisObject() as? View ?: return chain.proceed()
                    if (view::class.java.name != CAPSULE_VIEW_CLS) return chain.proceed()
                    val gd = drawable as? GradientDrawable ?: return chain.proceed()
                    gd.setCornerRadius(cornerRadiusPx(view))
                    val strokeWidth = getCapsuleStrokeWidthPx(view)
                    if (strokeWidth > 0) {
                        val strokeColor = Color.parseColor(Settings.capsuleStrokeColor)
                        gd.setStroke(strokeWidth, strokeColor)
                    }
                    Companion.gdStrokeColorMap.put(gd, Color.parseColor(Settings.capsuleStrokeColor))
                    return chain.proceed()
                }
            })
        } catch (_: Throwable) {}
    }

    /**
     * Hook GradientDrawable.setStroke(width, color) to prevent CapsuleView's
     * b(float) and bundle handler from overwriting our custom stroke color.
     */
    private fun hookCapsuleStrokeGradientDrawable() {
        try {
            val method = GradientDrawable::class.java.getMethod(
                "setStroke", Integer.TYPE, Integer.TYPE
            )
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    if (Companion.inStrokeHook.get() == true) return chain.proceed()
                    val gd = chain.getThisObject() as? GradientDrawable ?: return chain.proceed()
                    val targetColor = Companion.gdStrokeColorMap[gd] ?: return chain.proceed()
                    Companion.inStrokeHook.set(true)
                    try {
                        val width = chain.getArgs()[0] as Int
                        gd.setStroke(width, targetColor)
                    } finally {
                        Companion.inStrokeHook.set(false)
                    }
                    return null
                }
            })
            Log.d("FluidCloud", "hookCapsuleStrokeGradientDrawable: OK")
        } catch (e: Throwable) {
            Log.w("FluidCloud", "hookCapsuleStrokeGradientDrawable failed", e)
        }
    }

    private fun hookArtworkBgColor(classLoader: ClassLoader) {
        try {
            val clazz = classLoader.loadClass(
                "com.oplus.systemui.seedlingservice.mediaControl.SeedlingMediaData"
            )
            val method = clazz.getDeclaredMethod("getArtworkBgColor")
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    return Settings.artworkBgColor
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookCapsuleStrokeColor() {
        try {
            val method = android.content.res.Resources::class.java
                .getMethod("getColor", Integer.TYPE, android.content.res.Resources.Theme::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    val res = chain.getThisObject() as? android.content.res.Resources ?: return chain.proceed()
                    val id = chain.getArgs()[0] as Int
                    val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                    if (name != "capsule_stroke_default_color") return chain.proceed()
                    return Color.parseColor(Settings.capsuleStrokeColor)
                }
            })
        } catch (_: Throwable) {}
    }

    private fun hookMediaCardStrokeColor() {
        val hookBody: XposedInterface.Hooker = object : XposedInterface.Hooker {
            override fun intercept(chain: XposedInterface.Chain): Any? {
                if (!Settings.hookEnabled) return chain.proceed()
                val res = chain.getThisObject() as? android.content.res.Resources ?: return chain.proceed()
                val id = chain.getArgs()[0] as Int
                val name = try { res.getResourceEntryName(id) } catch (_: Throwable) { null }
                if (name != "default_stroke_color") return chain.proceed()
                return Color.parseColor(Settings.mediaCardStrokeColor)
            }
        }
        try {
            val method2 = android.content.res.Resources::class.java
                .getMethod("getColor", Integer.TYPE, android.content.res.Resources.Theme::class.java)
            hook(method2).intercept(hookBody)
        } catch (_: Throwable) {}
        try {
            val method1 = android.content.res.Resources::class.java
                .getMethod("getColor", Integer.TYPE)
            hook(method1).intercept(hookBody)
        } catch (_: Throwable) {}
    }

    /**
     * Hook SharedPreferencesImpl.getStringSet() to intercept "rus_content_key" reads.
     */
    private fun hookServiceConfigParsing() {
        try {
            Log.d("FluidCloud", "hookServiceConfigParsing: hooking SharedPreferencesImpl.getStringSet...")
            val spClass = Class.forName("android.app.SharedPreferencesImpl")
            val getSsMethod = spClass.getDeclaredMethod(
                "getStringSet", String::class.java, Set::class.java
            )

            hook(getSsMethod).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val key = chain.getArgs()[0] as? String
                    if (key != "rus_content_key" || !Settings.hookEnabled) {
                        return chain.proceed()
                    }
                    @Suppress("UNCHECKED_CAST")
                    val original = chain.proceed() as? Set<String> ?: return chain.proceed()
                    if (original.isEmpty()) return original

                    val modified = HashSet<String>(original.size)
                    for (entry in original) {
                        if (!entry.startsWith("[") || !entry.endsWith("]")) {
                            modified.add(entry)
                            continue
                        }
                        val parts = entry
                            .removeSurrounding("[", "]")
                            .split(",")
                            .map { it.trim() }
                            .toMutableList()
                        if (parts.size <= 10) {
                            modified.add(entry)
                            continue
                        }
                        val serviceId = parts[0]
                        val customDuration = when (serviceId) {
                            "268451843" -> Settings.hotspotCapsuleDuration.toString()
                            "268451910", "268451911", "268452006" -> Settings.musicCapsuleDuration.toString()
                            else -> null
                        }
                        if (customDuration != null) {
                            parts[10] = customDuration
                            modified.add("[" + parts.joinToString(", ") + "]")
                            Log.d("FluidCloud",
                                "SP: override $serviceId duration -> $customDuration")
                        } else {
                            modified.add(entry)
                        }
                    }
                    return modified
                }
            })
            Log.d("FluidCloud", "hookServiceConfigParsing: SP getStringSet hook installed")
        } catch (e: Throwable) {
            Log.e("FluidCloud", "hookServiceConfigParsing: SP getStringSet failed", e)
        }
    }

    // =========================================================================
    // Glow hooks (AGSL overlay)
    // =========================================================================

    /**
     * Hook View.draw(Canvas) for CapsuleView to paint glow on top.
     */
    private fun hookCapsuleDraw() {
        try {
            val method = View::class.java.getMethod("draw", Canvas::class.java)
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    if (!Settings.hookEnabled || !Settings.capsuleGlowEnabled) return result
                    val view = chain.getThisObject() as? View ?: return result
                    if (view::class.java.name != CAPSULE_VIEW_CLS) return result
                    val canvas = chain.getArgs()[0] as? Canvas ?: return result
                    CapsuleGlowEngine.drawGlow(canvas, view)
                    return result
                }
            })
            Log.d("FluidCloud", "hookCapsuleDraw: OK")
        } catch (e: Throwable) {
            Log.w("FluidCloud", "hookCapsuleDraw failed", e)
        }
    }

    /**
     * Hook CapsuleView attach/detach for glow animation.
     */
    private fun hookCapsuleAttachState() {
        try {
            val onAttach = View::class.java.getMethod("onAttachedToWindow")
            hook(onAttach).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    if (!Settings.hookEnabled) return result
                    val view = chain.getThisObject() as? View ?: return result
                    if (view::class.java.name != CAPSULE_VIEW_CLS) return result
                    view.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(v: View, outline: Outline) {
                            outline.setRoundRect(0, 0, v.width, v.height, cornerRadiusPx(v))
                        }
                    }
                    if (Settings.capsuleGlowEnabled) {
                        CapsuleGlowEngine.start()
                    }
                    return result
                }
            })

            val onDetach = View::class.java.getMethod("onDetachedFromWindow")
            hook(onDetach).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val view = chain.getThisObject() as? View ?: return chain.proceed()
                    if (view::class.java.name == CAPSULE_VIEW_CLS) {
                        CapsuleGlowEngine.stop()
                    }
                    return chain.proceed()
                }
            })
            Log.d("FluidCloud", "hookCapsuleAttachState: OK")
        } catch (e: Throwable) {
            Log.w("FluidCloud", "hookCapsuleAttachState failed", e)
        }
    }

    /**
     * Hook GradientDrawable.setStroke(width, color) to intercept media card
     * border strokes from BaseTemplateView.showStroke().
     * Catches both the fallback (Resources.getColor) and primary path
     * (server-provided color string → Color.parseColor).
     */
    private fun hookMediaCardStrokeGradientDrawable() {
        try {
            val method = GradientDrawable::class.java.getMethod(
                "setStroke", Integer.TYPE, Integer.TYPE
            )
            hook(method).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (!Settings.hookEnabled) return chain.proceed()
                    if (!Thread.currentThread().stackTrace.any { frame ->
                            frame.className == "com.oplus.ule.lite.template.base.r"
                            && frame.methodName == "showStroke"
                        }) return chain.proceed()
                    val newColor = Color.parseColor(Settings.mediaCardStrokeColor)
                    chain.getArgs()[1] = newColor
                    return chain.proceed()
                }
            })
            Log.d("FluidCloud", "hookMediaCardStrokeGradientDrawable: OK")
        } catch (e: Throwable) {
            Log.w("FluidCloud", "hookMediaCardStrokeGradientDrawable failed", e)
        }
    }

    // =========================================================================
    // Utility
    // =========================================================================

    private fun getCapsuleStrokeWidthPx(view: View): Int = try {
        view.resources.getDimensionPixelSize(
            view.resources.getIdentifier(
                "capsule_stroke_width", "dimen", "com.oplus.systemui.plugins"
            )
        )
    } catch (_: Throwable) { 0 }

    private fun cornerRadiusPx(view: View): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        Settings.bgCornerRadius.toFloat(),
        view.resources.displayMetrics
    )
}
