package com.example.ecr_bri.service

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.ecr_bri.broadcastreceiver.LocalBroadCastReceiver
import com.vfi.bri_ecr_lib.BriEcrLib

class TCPClientService : Service() {

    private lateinit var localBroadcastReceiver: LocalBroadCastReceiver

    override fun onCreate() {
        super.onCreate()
        registerBroadcastReceiver()
        var briErc = BriEcrLib(application.applicationContext as Activity)
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun registerBroadcastReceiver() {
        localBroadcastReceiver = LocalBroadCastReceiver()
    }

}