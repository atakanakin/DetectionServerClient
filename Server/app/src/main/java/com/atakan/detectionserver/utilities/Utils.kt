package com.atakan.detectionserver.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.face.Face
import com.hoko.blur.HokoBlur


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

object ImageUtils {

    fun applyBlur(context: Context, image: Bitmap, faces: List<Face>): Bitmap{
        var finalBitmap = image.copy(image.config, true)

        for(face in faces){
            val boundingBox = face.boundingBox
            val cropped = cropBitmap(originalBitmap = finalBitmap, bound = boundingBox)
            val blurred = blurPart(context, cropped)
            finalBitmap = overlayBitmap(originalBitmap = finalBitmap, blurred = blurred, region = boundingBox)
        }

        return finalBitmap
    }

    private fun cropBitmap(originalBitmap: Bitmap, bound: Rect) : Bitmap{
        val croppedBitmap = Bitmap.createBitmap(
            originalBitmap,
            bound.left,
            bound.top,
            bound.width(),
            bound.height(),
            null,
            false
        )
        return croppedBitmap
    }

    private fun blurPart(context: Context, region: Bitmap): Bitmap {
        return HokoBlur.with(context).radius(20).blur(region)
    }

    private fun overlayBitmap(originalBitmap: Bitmap, blurred: Bitmap, region: Rect): Bitmap {
        val combinedBitmap = originalBitmap.copy(originalBitmap.config, true) // Create a copy to avoid modifying the original

        val canvas = Canvas(combinedBitmap)
        val paint = Paint()
        paint.alpha = 255 // Adjust the transparency of the overlay

        canvas.drawBitmap(blurred, region.left.toFloat(), region.top.toFloat(), paint)

        return combinedBitmap
    }

}
