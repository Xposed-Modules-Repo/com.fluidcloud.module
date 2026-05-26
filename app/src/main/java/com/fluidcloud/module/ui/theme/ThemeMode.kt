package com.fluidcloud.module.ui.theme

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromValue(value: Int) = when (value) {
            1 -> LIGHT
            2 -> DARK
            else -> SYSTEM
        }

        fun toInt(mode: ThemeMode) = when (mode) {
            LIGHT -> 1
            DARK -> 2
            SYSTEM -> 0
        }
    }
}
