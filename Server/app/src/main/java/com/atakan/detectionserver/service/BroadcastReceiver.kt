package com.atakan.detectionserver.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class BroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //val viewModel =
        /*
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

         */
        //viewModel.clientDataLiveData.postValue(RecentClient.client)
        Log.d("Broadcast", "Package Received.")
    }
}
