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
            Triple(sw * 4f,   25f * intensity,  BlurMaskFilter(sw * 4f,   BlurMaskFilter.Blur.NORMAL)),
            Triple(sw * 2.5f, 50f * intensity,  BlurMaskFilter(sw * 2.5f, BlurMaskFilter.Blur.NORMAL)),
            Triple(sw * 1.5f, 100f * intensity, BlurMaskFilter(sw * 1.5f, BlurMaskFilter.Blur.NORMAL)),
            Triple(sw * 0.8f, 180f * intensity, null),
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
            val blend = (80 * intensity).toInt().coerceIn(0, 200)
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

        float hash(float2 p) {
            return fract(sin(dot(p, float2(127.1, 311.7))) * 43758.5453);
        }

        float noise(float2 p) {
            float2 i = floor(p);
            float2 f = fract(p);
            f = f * f * (3.0 - 2.0 * f);
            float a = hash(i);
            float b = hash(i + float2(1.0, 0.0));
            float c = hash(i + float2(0.0, 1.0));
            float d = hash(i + float2(1.0, 1.0));
            return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
        }

        float fbm(float2 p) {
            float v = 0.0;
            float a = 0.5;
            for (int i = 0; i < 4; i++) {
                v += a * noise(p);
                p *= 2.0;
                a *= 0.5;
            }
            return v;
        }

        half4 grad6(float t) {
            float T = t * 6.0;
            float i = floor(T);
            float f = T - i;
            f = f * f * (3.0 - 2.0 * f);
            if (i < 1.0) return mix(uColor1, uColor2, f);
            if (i < 2.0) return mix(uColor2, uColor3, f);
            if (i < 3.0) return mix(uColor3, uColor4, f);
            if (i < 4.0) return mix(uColor4, uColor5, f);
            if (i < 5.0) return mix(uColor5, uColor6, f);
            return mix(uColor6, uColor1, f);
        }

        half4 main(float2 coord) {
            float2 size = uResolution;
            float  r    = max(uCornerRadius, 1.0);
            float2 ctr  = size * 0.5;
            float2 hsz  = max(size * 0.5 - r, float2(0.0, 0.0));

            float2 d   = abs(coord - ctr) - hsz;
            float2 clp = max(d, float2(0.0, 0.0));
            float  sdf = length(clp) + min(max(d.x, d.y), 0.0);
            float absSdf = abs(sdf);

            float glowWidth = min(size.x, size.y) * 0.06;
            float innerGlow = 1.0 - smoothstep(0.0, glowWidth * 0.3, absSdf);
            float midGlow = 1.0 - smoothstep(0.0, glowWidth * 0.6, absSdf);
            float outerGlow = 1.0 - smoothstep(0.0, glowWidth, absSdf);

            float glow = innerGlow * 1.0 + midGlow * 0.5 + outerGlow * 0.2;
            glow = clamp(glow, 0.0, 1.5);

            float2 dir = coord - ctr;
            float angle = atan(dir.y, dir.x);
            float normAngle = angle * 0.1591549;

            float flowInner = fract(normAngle - uTime * 0.15);
            float flowOuter = fract(normAngle + uTime * 0.1);
            float flowMix = mix(flowInner, flowOuter, innerGlow * 0.6);

            float breathe = sin(uTime * 0.8) * 0.5 + 0.5;
            float wave = sin(normAngle * 6.28318 * 2.0 + uTime * 1.5) * 0.5 + 0.5;
            float flow = fract(flowMix + wave * 0.15 * breathe);

            float2 noiseCoord = coord * 0.008 + float2(uTime * 0.08, uTime * 0.05);
            float n = fbm(noiseCoord) * 0.25 + 0.75;

            half4 color1 = grad6(flow);
            half4 color2 = grad6(fract(flow + 0.5));
            half4 color = mix(color1, color2, breathe * 0.3);

            color *= n;

            float pulse1 = sin(uTime * 2.0) * 0.08 + 0.92;
            float pulse2 = sin(uTime * 4.0 + 2.0) * 0.06 + 0.94;
            float pulse3 = sin(uTime * 0.7) * 0.12 + 0.88;
            color *= pulse1 * pulse2 * pulse3;

            float sparkle1 = noise(coord * 0.15 + uTime * 1.5);
            sparkle1 = pow(sparkle1, 10.0) * 3.0;
            float sparkle2 = noise(coord * 0.2 - uTime * 2.0);
            sparkle2 = pow(sparkle2, 12.0) * 2.0;
            color += half4((sparkle1 + sparkle2) * 0.25);

            float edgeHighlight = exp(-absSdf * 0.4) * 0.35;
            color += half4(edgeHighlight * 0.8);

            float alpha = glow * uIntensity;
            if (alpha < 0.003) return half4(0.0);

            return half4(color.rgb * alpha, alpha);
        }
    """.trimIndent()
}
