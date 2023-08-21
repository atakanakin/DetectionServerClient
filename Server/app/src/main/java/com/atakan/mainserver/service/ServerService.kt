package com.atakan.mainserver.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atakan.mainserver.IIPCExample
import com.atakan.mainserver.MyApplication
import com.atakan.mainserver.constants.CONNECTION_COUNT
import com.atakan.mainserver.data.model.Client
import com.atakan.mainserver.data.model.RecentClient
import com.atakan.mainserver.constants.CURR1
import com.atakan.mainserver.constants.CURR2
import com.atakan.mainserver.constants.CURR3
import com.atakan.mainserver.constants.PACKAGE_NAME
import com.atakan.mainserver.constants.PID
import com.atakan.mainserver.constants.RATE1
import com.atakan.mainserver.constants.RATE2
import com.atakan.mainserver.constants.RATE3
import com.atakan.mainserver.constants.TIME

class ServerService : Service() {

    companion object {
        // How many connection requests have been received since the service started
        var connectionCount: Int = 0

        // Client might have sent an empty data
        const val NOT_SENT = "Not sent!"
    }

    //val viewModel =

    // Messenger IPC - Messenger object contains binder to send to client
    private val mMessenger = Messenger(IncomingHandler())

    // Messenger IPC - Message Handler
    internal inner class IncomingHandler : Handler() {


        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // Get message from client. Save recent connected client info.
            val receivedBundle = msg.data
            RecentClient.client = Client(
                receivedBundle.getString(PACKAGE_NAME),
                receivedBundle.getInt(PID).toString(),
                receivedBundle.getString(CURR1),
                receivedBundle.getString(CURR2),
                receivedBundle.getString(CURR3),
                receivedBundle.getDouble(RATE1),
                receivedBundle.getDouble(RATE2),
                receivedBundle.getDouble(RATE3),
                receivedBundle.getString(TIME),
                "Messenger"
            )

            //viewModel.clientDataLiveData.postValue(RecentClient.client)
            //clientDataViewModel.clientDataLiveData.postValue(RecentClient.client)

            // Send message to the client. The message contains server info
            val message = Message.obtain(this@IncomingHandler, 0)
            val bundle = Bundle()
            connectionCount++
            bundle.putInt(CONNECTION_COUNT, connectionCount)

            bundle.putInt(PID, Process.myPid())
            message.data = bundle
            // The service can save the msg.replyTo object as a local variable
            // so that it can send a message to the client at any time
            msg.replyTo.send(message)
            Log.d("Messenger", "Package Received.")
        }
    }

    // AIDL IPC - Binder object to pass to the client
    private val aidlBinder = object : IIPCExample.Stub() {

        override fun getPid(): Int = Process.myPid()

        override fun getConnectionCount(): Int = ServerService.connectionCount

        override fun postVal(packageName: String?, pid: Int, clientCurr1 : String?, clientCurr2 : String?, clientCurr3 : String?, clientRate1 : Double, clientRate2 : Double, clientRate3 : Double, time : String?) {
            Companion.connectionCount++
            // Get message from client. Save recent connected client info.
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
        RecentClient.client = null
        return super.onUnbind(intent)
    }

}