-keep class com.fluidcloud.module.** { *; }
-keep class io.github.libxposed.api.** { *; }
-keepclassmembers class * {
    @io.github.libxposed.api.annotations.* <methods>;
}

# Keep MIUIX overlay/layout classes used via popup
-keep class top.yukonga.miuix.kmp.overlay.** { *; }
-keep class top.yukonga.miuix.kmp.layout.** { *; }
-keep class top.yukonga.miuix.kmp.utils.** { *; }
