package com.atakan.detectionclient.presentation.screen.main_screen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.detectionclient.presentation.view_model.ImageViewModel
import com.atakan.detectionclient.presentation.view_model.ServiceViewModel
import com.atakan.detectionclient.service.MessengerService
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.io.IOException
import java.io.InputStream

@Composable
fun MessengerService(context: Context) {
    DisposableEffect(Unit) {
        // Start the foreground service when the Composable is first activated
        val intent = Intent(context, MessengerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
        onDispose {
            // You can stop the service if you do not need to execute background
            context.stopService(intent)
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MainScreen(
    context: Context,
    pickImage: () -> Unit,
    viewModel: ImageViewModel = hiltViewModel(),
    serviceViewModel: ServiceViewModel = hiltViewModel()
) {
    val imageState by viewModel.imageLive.observeAsState()
    val emptyState by viewModel.isEmpty.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MessengerService(context = context)
        Box(modifier = Modifier.height(600.dp)){
            if(emptyState!!){
                GlideImage(model = imageState, contentDescription = "null",
                    modifier = Modifier.fillMaxSize())
            }
            else{
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black)){
                    Text(text = "No Image", modifier = Modifier.align(Alignment.Center), style = TextStyle(color = Color.White))
                }
            }

        }
        Button(onClick = {
            pickImage()
        }) {
            Text("Choose")
        }
        Button(onClick = {
            if(emptyState!!){
                serviceViewModel.updateCount()
            }
            else{
                println("No Image Selected")
            }
        }) {
            Text("Send")
        }
        Button(onClick = {
            if(emptyState!!){
                viewModel.refreshData(viewModel.imageLive.value?.rotate(90f)!!)
            }
            else{
                println("No Image Selected")
            }
        }) {
            Text(text = "Rotate")
        }
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}