package com.atakan.detectionclient.presentation.screen.aidl

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.atakan.detectionclient.service.AIDLService

@Composable
fun AIDLServiceComposable(context: Context) {
    DisposableEffect(Unit) {
        // Start the service when the Composable is first activated
        val intent = Intent(context, AIDLService::class.java)
        context.startService(intent)

        onDispose {
            // Stop the service when the Composable is no longer active
            context.stopService(intent)
        }
    }
}
