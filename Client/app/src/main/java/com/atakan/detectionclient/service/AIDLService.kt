package com.atakan.detectionclient.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.atakan.detectionclient.data.ImageData
import com.atakan.detectionclient.presentation.MainActivity
import com.atakan.detectionclient.presentation.view_model.ImageViewModel
import com.atakan.detectionclient.presentation.view_model.ServiceViewModel
import com.atakan.detectionserver.IIPCExample
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AIDLService : Service() {

    // Get viewModel instance
    @Inject
    lateinit var viewModel: ImageViewModel

    @Inject
    lateinit var clickViewModel: ServiceViewModel

    var iRemoteService: IIPCExample? = null

    private val clickObserver = Observer<Int> {
        if(it > 0){
            sendDataToServer()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Gets an instance of the AIDL interface
            Log.d("AIDL", "Connected")
            iRemoteService = IIPCExample.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iRemoteService = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        connectToRemoteService()
        Log.d("AIDL", "Task Removed, Restarting Service")
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun connectToRemoteService() {
        Log.d("AIDL", "Connected lesgo")
        val intent = Intent("aidl")
        val pack = IIPCExample::class.java.`package`
        pack?.let {
            intent.setPackage(pack.name)
            bindService(
                intent, connection, Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun disconnectToRemoteService() {
        try {
            unbindService(connection)
        }catch (e : Exception){
            Log.w("AIDLError", e.toString())
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Foreground Service Channel"
            val descriptionText = "Foreground service channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ForegroundServiceChannel", name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = createNotification()
        startForeground(1, notification)
        clickViewModel.isServiceConnected.observeForever(clickObserver)
        connectToRemoteService()
    }
    private fun createNotification(): Notification {
        val notificationTitle = "AIDL Service"
        val notificationText = "Service is running in the background"
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                // Add the extra value for fragment identification
                //putExtra("FRAGMENT_ID", R.id.navigation_aidl)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        disconnectToRemoteService()
        clickViewModel.isServiceConnected.removeObserver(clickObserver)
        // Stop the foreground service and remove the notification
        stopForeground(true)
        disconnectToRemoteService()
        super.onDestroy()

    }

    // Method to send data to the server application
    fun sendDataToServer() {
        val resource: ImageData =
            ImageData(image = viewModel.imageLive.value!!,
                action = viewModel.action)
        Log.d("AIDL", "Sending Data")
        iRemoteService?.postVal(
            resource.image,
            resource.action
        )

        viewModel.refreshData(iRemoteService?.image!!)
    }
}