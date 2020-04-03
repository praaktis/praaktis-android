package com.mobile.praaktishockey.domain.common.shape

import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath
import com.mobile.praaktishockey.domain.extension.dp

class CurvedEdgeTreatment(val size: Float) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        center: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        shapePath.cubicToPoint(
            length / 4,
            size * interpolation,
            length - length / 4,
            size * interpolation,
            length,
            0f
        )
    }
}