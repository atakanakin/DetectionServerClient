package com.atakan.mainclient.presentation.screen.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.mainclient.presentation.view_model.ImageViewModel
import com.atakan.mainclient.presentation.view_model.ServiceViewModel


@Composable
fun MainScreen(
    pickImage: () -> Unit,
    viewModel: ImageViewModel = hiltViewModel(),
    serviceViewModel: ServiceViewModel = hiltViewModel()
) {
    val imageState by viewModel.imageLive.observeAsState()
    val emptyState by viewModel.isEmpty.observeAsState()

    Column() {
        Box(modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()){
            if(emptyState!!){
                androidx.compose.foundation.Image(
                    bitmap = imageState!!.asImageBitmap(),
                    contentDescription = "Chosen Image",
                    modifier = Modifier.fillMaxSize() // adjust the modifier as needed
                )
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
    }
}