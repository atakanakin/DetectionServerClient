package com.atakan.detectionserver.data.model

import android.graphics.Bitmap

data class ImageData(
    val image: Bitmap,
    val action: String,
    val method: String
)