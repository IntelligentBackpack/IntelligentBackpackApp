package com.intelligentbackpack.app.ui.shape

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CutOutShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val outlinePath = Path()
        outlinePath.addRect(Rect(Offset(0f, 0f), size))

        val cutoutHeight = size.height * 0.3f
        val cutoutWidth = size.width * 0.75f
        val center = Offset(size.width / 2f, size.height / 2f)

        val cutoutPath = Path()
        cutoutPath.addRoundRect(
            RoundRect(
                Rect(
                    topLeft = center - Offset(
                        cutoutWidth / 2f,
                        cutoutHeight / 2f
                    ),
                    bottomRight = center + Offset(
                        cutoutWidth / 2f,
                        cutoutHeight / 2f
                    )
                ),
                cornerRadius = CornerRadius(16f, 16f)
            )
        )

        val finalPath = Path.combine(
            PathOperation.Difference,
            outlinePath,
            cutoutPath
        )

        return Outline.Generic(finalPath)
    }
}