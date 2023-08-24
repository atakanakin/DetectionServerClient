package com.atakan.detectionclient.presentation.screen.main_screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.detectionclient.presentation.screen.aidl.AIDLService
import com.atakan.detectionclient.presentation.screen.messenger.MessengerService
import com.atakan.detectionclient.presentation.view_model.ImageViewModel
import com.atakan.detectionclient.presentation.view_model.ServiceViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

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
        // Choose service
        //MessengerService(context = context)
        //AIDLService(context = context)
        AIDLService(context = context)
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
        /* Rotate if needed
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

         */
    }
}
/*
fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
 */