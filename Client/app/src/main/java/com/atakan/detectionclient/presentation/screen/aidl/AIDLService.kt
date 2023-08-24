package com.atakan.detectionclient.presentation.screen.aidl

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.content.ContextCompat
import com.atakan.detectionclient.service.AIDLService

@Composable
fun AIDLService(context: Context) {
    DisposableEffect(Unit) {
        // Start the foreground service when the Composable is first activated
        val intent = Intent(context, AIDLService::class.java)
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