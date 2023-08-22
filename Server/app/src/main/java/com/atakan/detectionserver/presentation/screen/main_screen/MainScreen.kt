package com.atakan.detectionserver.presentation.screen.main_screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.atakan.detectionserver.presentation.ImageViewModel

@Composable
fun MainScreen(viewModel: ImageViewModel = hiltViewModel(), context: Context) {
    val imageState by viewModel.imageLive.observeAsState()
    val emptyState by viewModel.isEmpty.observeAsState()

    Column() {

        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {
            if (emptyState!!) {
                Image(
                    bitmap = imageState!!.asImageBitmap(),
                    contentDescription = "Chosen Image",
                    modifier = Modifier.fillMaxSize() // adjust the modifier as needed
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black)
                ) {
                    Text(
                        text = "No Image",
                        modifier = Modifier.align(Alignment.Center),
                        style = TextStyle(color = Color.White)
                    )
                }
            }

        }
    }
    
}