package com.intelligentbackpack.app.utility

import androidx.compose.ui.graphics.vector.ImageVector

class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val action: () -> Unit
)