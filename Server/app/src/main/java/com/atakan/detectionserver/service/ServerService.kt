package com.atakan.detectionserver.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import com.atakan.detectionserver.IIPCExample
import com.atakan.detectionserver.constants.Constants.ACTION
import com.atakan.detectionserver.constants.Constants.IMAGE
import com.atakan.detectionserver.constants.Constants.IMG_RECREATED
import com.atakan.detectionserver.data.model.ImageData
import com.atakan.detectionserver.presentation.viewmodel.ImageViewModel
import com.atakan.detectionserver.utilities.ImageUtils.applyBlur
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class ServerService : Service() {

    @Inject
    lateinit var viewModel: ImageViewModel

    private val detector = FaceDetection.getClient();

    lateinit var incData: ImageData

    lateinit var mutableBitmap: Bitmap

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

            // The service can save the msg.replyTo object as a local variable
            // so that it can send a message to the client at any time
            val message = Message.obtain(this@IncomingHandler, 0)
            val bundle = Bundle()

            runBlocking {
                mutableBitmap = applyBlur(applicationContext, incData.image, faceDetector())
                viewModel.refreshData(mutableBitmap)
            }

            bundle.putParcelable(IMG_RECREATED, mutableBitmap)
            message.data = bundle
            msgx.replyTo.send(message)

            Log.d("Messenger", "Package Received.")
        }
    }

    suspend fun faceDetector (): MutableList<Face> {
        val image = InputImage.fromBitmap(incData.image, 0)

        return detector.process(image).await()
    }

    // AIDL IPC - Binder object to pass to the client
    private val aidlBinder = object : IIPCExample.Stub() {

        override fun getImage(): Bitmap {
            runBlocking {
                println("inside")
                mutableBitmap = applyBlur(applicationContext, incData.image , faceDetector())
                viewModel.refreshData(mutableBitmap)
                println("inside2")
            }
            return mutableBitmap
        }

        override fun postVal(image: Bitmap, action: String?) {
            // Get message from client. Save recent connected client info.
            viewModel.refreshData(image)
            incData = ImageData(
                image = image,
                action = "face",
                method = "AIDL"
            )

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
        return super.onUnbind(intent)
    }

}