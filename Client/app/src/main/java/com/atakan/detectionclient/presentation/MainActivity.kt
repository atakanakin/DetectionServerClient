package com.atakan.detectionclient.presentation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.exifinterface.media.ExifInterface
import com.atakan.detectionclient.presentation.screen.main_screen.MainScreen
import com.atakan.detectionclient.presentation.theme.MainClientTheme
import com.atakan.detectionclient.presentation.view_model.ImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val REQUEST_IMAGE_PICK = 2

    private lateinit var image: Bitmap

    @Inject
    lateinit var viewModel: ImageViewModel

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()

                }
            }
        }
    }

    private fun getExifOrientation(uri: Uri): Int {
        var exifOrientation = 0
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val exifInterface = ExifInterface(inputStream)
            exifOrientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        }
        return exifOrientationToDegrees(exifOrientation)
    }

    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if(selectedImageUri != null){
                /*
                val exifOrientation = getExifOrientation(selectedImageUri)
                val rotatedImage = rotateImageIfNeeded(selectedImageUri, exifOrientation)
                viewModel.refreshData(rotatedImage)
                */
                 viewModel.refreshData(MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri))
            }
            else{
                println("No successful selection.")
            }
        }
    }
    private fun rotateImageIfNeeded(uri: Uri, degrees: Int): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val rotatedBitmap = rotateBitmap(originalBitmap, degrees)
        inputStream?.close()

        return rotatedBitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}