package com.atakan.detectionserver.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.ui.graphics.asImageBitmap
import com.atakan.detectionserver.IIPCExample
import com.atakan.detectionserver.constants.Constants.ACTION
import com.atakan.detectionserver.constants.Constants.IMAGE
import com.atakan.detectionserver.constants.Constants.IMG_RECREATED

import com.atakan.detectionserver.data.model.ImageData
import com.atakan.detectionserver.presentation.ImageViewModel
import com.atakan.detectionserver.utilities.applyBlurToBitmap
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ServerService : Service() {

    @Inject
    lateinit var viewModel: ImageViewModel

    // How many connection requests have been received since the service started
    var connectionCount: Int = 0

    private val detector = FaceDetection.getClient();

    // Client might have sent an empty data
    private val NOT_SENT = "Not sent!"

    lateinit var incData: ImageData

    lateinit var mutableBitmap: Bitmap

    //val viewModel =

    // Messenger IPC - Messenger object contains binder to send to client
    private val mMessenger = Messenger(IncomingHandler())

    // Messenger IPC - Message Handler
    internal inner class IncomingHandler : Handler() {



        override fun handleMessage(msgx: Message) {
            super.handleMessage(msgx)
            // Get message from client. Save recent connected client info.
            val receivedBundle = msgx.data
             incData = ImageData(
                image = receivedBundle.getParcelable(IMAGE)!!,
                action = receivedBundle.getString(ACTION)!!,
                method = "Messenger"
            )
            viewModel.refreshData(incData.image)

            // Send message to the client. The message contains server info


            /*
            bundle.putInt(CONNECTION_COUNT, connectionCount)

            bundle.putInt(PID, Process.myPid())

             */
            // The service can save the msg.replyTo object as a local variable
            // so that it can send a message to the client at any time
            val image = InputImage.fromBitmap(incData.image, 0)


            val message = Message.obtain(this@IncomingHandler, 0)
            val bundle = Bundle()

            bundle.putString(ACTION, "çalış")

            println("atakan")

            runBlocking {
                println("inside")
                mutableBitmap = applyBlurToBitmap(incData.image, faceDetector())
                viewModel.refreshData(mutableBitmap)
                println("inside2")
            }

            println(mutableBitmap == null)

            println("after")

            bundle.putParcelable(IMG_RECREATED, mutableBitmap)
            message.data = bundle
            println(msgx.data)
            println(msgx.replyTo)
            msgx.replyTo.send(message)

            println("completed")
            Log.d("Messenger", "Package Received.")
        }
    }

    suspend fun faceDetector (): MutableList<Face> {
        val image = InputImage.fromBitmap(incData.image, 0)

        return detector.process(image).await()
    }

    // AIDL IPC - Binder object to pass to the client
    private val aidlBinder = object : IIPCExample.Stub() {

        override fun getPid(): Int = Process.myPid()

        override fun getConnectionCount(): Int = connectionCount

        override fun postVal(packageName: String?, pid: Int, clientCurr1 : String?, clientCurr2 : String?, clientCurr3 : String?, clientRate1 : Double, clientRate2 : Double, clientRate3 : Double, time : String?) {
            // Get message from client. Save recent connected client info.
            /*
            RecentClient.client = Client(
                packageName ?: NOT_SENT,
                pid.toString(),
                clientCurr1,
                clientCurr2,
                clientCurr3,
                clientRate1,
                clientRate2,
                clientRate3,
                time,
                "AIDL"
            )

             */
            //viewModel.clientDataLiveData.postValue(RecentClient.client)
            Log.d("AIDL", "Package Received.")
        }
    }

    // Pass the binder object to clients so they can communicate with this service
    override fun onBind(intent: Intent?): IBinder? {
        // Choose which binder we need to return based on the type of IPC the client makes
        return when (intent?.action) {
            "aidl" -> aidlBinder
            "messenger" -> mMessenger.binder


            else -> null
        }
    }

    // A client has unbound from the service
    override fun onUnbind(intent: Intent?): Boolean {
        //RecentClient.client = null
        return super.onUnbind(intent)
    }

}