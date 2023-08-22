package com.atakan.detectionserver.utilities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.face.Face

fun applyBlurToBitmap(inputBitmap: Bitmap, faces: List<Face>): Bitmap {
    val outputBitmap = inputBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(outputBitmap)

    val blurPaint = Paint()
    blurPaint.color = Color.argb(128, 0, 0, 0)
    blurPaint.style = Paint.Style.FILL_AND_STROKE
    blurPaint.style = Paint.Style.FILL_AND_STROKE

    for (face in faces) {
        val boundingBox = face.boundingBox
        canvas.drawRect(boundingBox, blurPaint)
    }

    return outputBitmap
}