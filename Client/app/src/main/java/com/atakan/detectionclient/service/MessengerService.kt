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
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.atakan.detectionclient.common.Constants.ACTION
import com.atakan.detectionclient.common.Constants.IMAGE
import com.atakan.detectionclient.common.Constants.IMG_RECREATED
import com.atakan.detectionclient.common.Constants.PACKAGE_NAME
import com.atakan.detectionclient.data.ImageData
import com.atakan.detectionclient.presentation.MainActivity
import com.atakan.detectionclient.presentation.view_model.ImageViewModel
import com.atakan.detectionclient.presentation.view_model.ServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MessengerService : Service(){

    @Inject
    lateinit var viewModel: ImageViewModel

    @Inject
    lateinit var clickViewModel: ServiceViewModel


    private var serverMessenger: Messenger? = null

    // Messenger on the client
    private var clientMessenger: Messenger? = null

    // Service Connection
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Called when the connection to the server service is established
            serverMessenger = Messenger(service)
            clientMessenger = Messenger(handler)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Called when the connection to the server service is disconnected
            serverMessenger = null
            clientMessenger = null
        }
    }

    // Handle messages from the remote service (server app)
    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // Update UI with remote process info

            val bundle = msg.data

            val modifiedImage = bundle.getParcelable<Bitmap>(IMG_RECREATED)
            if (modifiedImage != null) {
                viewModel.refreshData(modifiedImage)
            } else {
                // Handle the case where modifiedImage is null
                println("null came")
            }
        }
    }

    private val clickObserver = Observer<Int> {
        if(it > 0){
            sendMessageToServer()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        // ?
        return null
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

        // Start the service as a foreground service with a notification
        val notification = createNotification() // Implement the createNotification() method
        startForeground(2, notification)
        clickViewModel.isServiceConnected.observeForever(clickObserver)
        doBindService()
    }
    private fun createNotification(): Notification {
        // Create and return the notification for the foreground service
        // You can customize the notification as needed
        // For example:
        val notificationTitle = "Messenger Service"
        val notificationText = "Service is running in the background"
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                // Add the extra value for fragment identification
                //putExtra("FRAGMENT_ID", R.id.navigation_messenger)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentIntent(pendingIntent)
            .build()
    }


    override fun onDestroy() {
        doUnbindService()
        //viewModel.imageLive.removeObserver()
        clickViewModel.isServiceConnected.removeObserver(clickObserver)
        // Stop the foreground service and remove the notification
        stopForeground(true)
        super.onDestroy()
    }

    // Start service according to the button
    private fun doBindService(){
        // Start the server service
        try{
            Intent("messenger").also { intent ->
                intent.`package` = "com.atakan.detectionserver"
                startService(intent)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
            Log.d("Messenger", "SUCCESSFULLY connected")
        }catch (e: Exception){
            Log.e("Messenger", "Error")
            e.printStackTrace()
        }

    }

    // Stop service activity according to the button activity
    private fun doUnbindService(){
        try {
            unbindService(serviceConnection)
            Log.d("Messenger", "SUCCESSFULLY disconnected")

        }catch (e : Exception)
        {
            Log.w("Messenger", e.toString())
        }


        try {
            unbindService(serviceConnection)
        } catch (e: Exception){
            Log.e("Messenger", "Failed to disconnect.")
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Start the periodic API calls and send data if the service is bounded

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d("Messenger", "Task Removed, Restarting Service")
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun sendMessageToServer(){
        if (serverMessenger == null) {
            // Server service connection is lost or not available
            doUnbindService()
            doBindService()
            Log.e("Messenger", "Server service connection lost. Cannot send message.")
            Log.d("Messenger", "Trying to reconnect.")
            return
        }
        val message = Message.obtain(handler)
        val bundle = Bundle()
        val resource = ImageData(image = viewModel.imageLive.value!!, action = viewModel.action)

        Log.d("Messenger", "Sending Data")
        bundle.putParcelable(IMAGE, resource.image)
        bundle.putString(ACTION, resource.action)
        bundle.putString(PACKAGE_NAME, applicationContext.packageName)


        message.data = bundle

        message.replyTo = clientMessenger //for communication to be two-way

        try {
            serverMessenger?.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            message.recycle()
        }


    }
}