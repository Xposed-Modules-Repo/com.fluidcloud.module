package com.fluidcloud.module.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// Design tokens
val CornerRadius = 16.dp
val ConnectionRadius = 5.dp
val HorizontalMargin = 16.dp
val SectionSpacing = 12.dp
val ContentPadding = 16.dp

// Segmented list shapes (for preference groups)
val topShape = RoundedCornerShape(
    topStart = CornerRadius,
    topEnd = CornerRadius,
    bottomStart = ConnectionRadius,
    bottomEnd = ConnectionRadius
)
val middleShape = RoundedCornerShape(ConnectionRadius)
val bottomShape = RoundedCornerShape(
    topStart = ConnectionRadius,
    topEnd = ConnectionRadius,
    bottomStart = CornerRadius,
    bottomEnd = CornerRadius
)
val singleShape = RoundedCornerShape(CornerRadius)
