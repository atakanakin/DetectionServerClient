package com.atakan.detectionclient.presentation.screen.main_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.detectionclient.presentation.screen.messenger.MessengerServiceComposable
import com.atakan.detectionclient.presentation.view_model.ImageViewModel
import com.atakan.detectionclient.presentation.view_model.ServiceViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MainScreen(
    viewModel: ImageViewModel = hiltViewModel(),
    serviceViewModel: ServiceViewModel = hiltViewModel()
) {
    val imageState by viewModel.imageLive.observeAsState()
    val emptyState by viewModel.isEmpty.observeAsState()

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    // Observe changes to imageUri and process the image when it changes
    LaunchedEffect(imageUri) {
        if (imageUri != null) {
            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                viewModel.refreshData(bitmap)

            } else {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
                val bitmap = ImageDecoder.decodeBitmap(source)
                viewModel.refreshData(bitmap)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Choose service
        //MessengerServiceComposable(context = context)
        //AIDLServiceComposable(context = context)
        MessengerServiceComposable(context = context)
        Box(modifier = Modifier.height(600.dp)){

            if(emptyState == true){
                Image(
                    imageState!!.asImageBitmap(), contentDescription = "null",
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
            launcher.launch("image/*")
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