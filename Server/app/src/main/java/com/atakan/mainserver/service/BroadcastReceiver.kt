package com.atakan.mainserver.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.atakan.mainserver.constants.CURR1
import com.atakan.mainserver.constants.CURR2
import com.atakan.mainserver.constants.CURR3
import com.atakan.mainserver.constants.PACKAGE_NAME
import com.atakan.mainserver.constants.PID
import com.atakan.mainserver.constants.RATE1
import com.atakan.mainserver.constants.RATE2
import com.atakan.mainserver.constants.RATE3
import com.atakan.mainserver.constants.TIME
import com.atakan.mainserver.data.model.Client
import com.atakan.mainserver.data.model.RecentClient

class BroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //val viewModel =
        RecentClient.client = Client(
            intent?.getStringExtra(PACKAGE_NAME),
            intent?.getStringExtra(PID),
            intent?.getStringExtra(CURR1),
            intent?.getStringExtra(CURR2),
            intent?.getStringExtra(CURR3),
            intent?.getDoubleExtra(RATE1, 0.0),
            intent?.getDoubleExtra(RATE2, 0.0),
            intent?.getDoubleExtra(RATE3, 0.0),
            intent?.getStringExtra(TIME),
            "Broadcast"
        )
        //viewModel.clientDataLiveData.postValue(RecentClient.client)
        Log.d("Broadcast", "Package Received.")
    }
}
