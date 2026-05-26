package com.fluidcloud.module.glow

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.graphics.SweepGradient
import android.os.Build
import android.util.Log
import android.view.View
import com.fluidcloud.module.Settings

object CapsuleGlowEngine {

    private const val TAG = "CapsuleGlowEngine"
    private const val ORIGINAL_RADIUS_DP = 20f

    private var flowOffset = 0f
    private var animationPending = false

    private var agslShader: RuntimeShader? = null
    private val agslPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
    }

    private val glowColors = intArrayOf(
        0xFFFF1744.toInt(), 0xFFFF9100.toInt(), 0xFFFFEA00.toInt(),
        0xFF00E676.toInt(), 0xFF2979FF.toInt(), 0xFFD500F9.toInt(),
    )

    private val sweepMatrix = Matrix()

    fun start() {
        flowOffset = 0f
        tryAGSL()
    }

    fun stop() {}

    private fun tryAGSL() {
        if (agslShader != null || Build.VERSION.SDK_INT < 33) return
        try {
            agslShader = RuntimeShader(AGSL_SOURCE)
            Log.d(TAG, "AGSL compiled OK")
        } catch (e: Throwable) {
            Log.w(TAG, "AGSL failed: ${e.message}")
        }
    }

    fun drawGlow(canvas: Canvas, view: View) {
        if (!Settings.capsuleGlowEnabled) return
        val l = 0f; val t = 0f; val r = view.width.toFloat(); val b = view.height.toFloat()
        if (r <= 0f || b <= 0f) return
        val density = view.resources.displayMetrics.density
        val radiusDp = Settings.bgCornerRadius.toFloat()
        val cr = if (radiusDp == ORIGINAL_RADIUS_DP) {
            ORIGINAL_RADIUS_DP * density
        } else {
            radiusDp * density
        }
        val sw = getStrokeWidth(view).toFloat().coerceAtLeast(2f)
        val intensity = Settings.capsuleGlowIntensity.coerceIn(0f, 3f)
        if (intensity <= 0f) return

        drawCanvasGlow(canvas, l, t, r, b, sw, cr, intensity)
        drawAGSLGlow(canvas, l, t, r, b, cr, intensity, glowColors)

        if (!animationPending) {
            animationPending = true
            view.postOnAnimation {
                animationPending = false
                flowOffset += Settings.capsuleGlowSpeed * 0.016f
                if (flowOffset > 100000f) flowOffset = 0f
                view.invalidate()
            }
        }
    }

    private fun drawCanvasGlow(
        canvas: Canvas, l: Float, t: Float, r: Float, b: Float,
        sw: Float, cr: Float, intensity: Float,
    ) {
        val cx = (l + r) / 2f
        val cy = (t + b) / 2f

        val sweepGrad = SweepGradient(cx, cy, glowColors, null)
        sweepMatrix.reset()
        sweepMatrix.postRotate(flowOffset * 360f, cx, cy)
        sweepGrad.setLocalMatrix(sweepMatrix)
        blurPaint.shader = sweepGrad

        val layers = listOf(
            Triple(sw * 3f,   40f * intensity,  BlurMaskFilter(sw * 3f,   BlurMaskFilter.Blur.NORMAL)),
            Triple(sw * 1.8f, 80f * intensity,  BlurMaskFilter(sw * 1.8f, BlurMaskFilter.Blur.NORMAL)),
            Triple(sw * 0.6f, 160f * intensity, null),
        )
        for ((width, alpha, filter) in layers) {
            blurPaint.strokeWidth = width
            blurPaint.maskFilter = filter
            blurPaint.alpha = (alpha).toInt().coerceIn(0, 255)
            canvas.drawRoundRect(l, t, r, b, cr, cr, blurPaint)
        }
    }

    private fun drawAGSLGlow(
        canvas: Canvas, l: Float, t: Float, r: Float, b: Float,
        cr: Float, intensity: Float, colors: IntArray,
    ) {
        val shader = agslShader ?: return
        val w = r - l; val h = b - t
        if (w <= 0f || h <= 0f) return
        try {
            shader.setFloatUniform("uResolution", w, h)
            shader.setFloatUniform("uTime", flowOffset)
            shader.setFloatUniform("uCornerRadius", cr)
            shader.setFloatUniform("uIntensity", intensity)
            for (i in 0..5) shader.setColorUniform("uColor${i + 1}", colors[i])
            agslPaint.shader = shader
            val blend = (60 * intensity).toInt().coerceIn(0, 180)
            agslPaint.alpha = blend
            canvas.drawRoundRect(l, t, r, b, cr, cr, agslPaint)
        } catch (e: Throwable) {
            if (!agslLogged) { agslLogged = true; Log.w(TAG, "AGSL draw failed: ${e.message}") }
        }
    }

    private var agslLogged = false

    private fun getStrokeWidth(view: View): Int = try {
        view.resources.getDimensionPixelSize(
            view.resources.getIdentifier("capsule_stroke_width", "dimen", "com.oplus.systemui.plugins"))
    } catch (_: Throwable) { 2 }

    private val AGSL_SOURCE = """
        uniform float2  uResolution;
        uniform float   uTime;
        uniform float   uCornerRadius;
        uniform float   uIntensity;
        uniform half4   uColor1;
        uniform half4   uColor2;
        uniform half4   uColor3;
        uniform half4   uColor4;
        uniform half4   uColor5;
        uniform half4   uColor6;

        half4 grad6(float t) {
            float T = t * 5.0;
            float i = floor(T);
            float f = T - i;
            if (i < 1.0) return mix(uColor1, uColor2, f);
            if (i < 2.0) return mix(uColor2, uColor3, f);
            if (i < 3.0) return mix(uColor3, uColor4, f);
            if (i < 4.0) return mix(uColor4, uColor5, f);
            return mix(uColor5, uColor6, f);
        }

        half4 main(float2 coord) {
            float2 size = uResolution;
            float  r    = max(uCornerRadius, 1.0);
            float2 ctr  = size * 0.5;
            float2 hsz  = max(size * 0.5 - r, float2(0.0, 0.0));

            float2 d   = abs(coord - ctr) - hsz;
            float2 clp = max(d, float2(0.0, 0.0));
            float  sdf = length(clp) + min(max(d.x, d.y), 0.0);

            float glowWidth = min(size.x, size.y) * 0.04;
            float alpha = (1.0 - smoothstep(0.0, glowWidth, abs(sdf))) * uIntensity;
            if (alpha < 0.003) return half4(0.0);

            float2 dir  = coord - ctr;
            float angle = atan(dir.y, dir.x);
            float flow  = fract(angle * 0.1591549 + uTime);

            half4 color = grad6(flow);
            color *= (sin(uTime * 2.0) * 0.1 + 0.9);
            return half4(color.rgb * alpha, alpha);
        }
    """.trimIndent()
}
