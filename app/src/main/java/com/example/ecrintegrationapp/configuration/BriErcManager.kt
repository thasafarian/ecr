package com.example.ecrintegrationapp.configuration

import android.app.Activity
import com.example.ecrintegrationapp.connection.ConnectionManager
import com.vfi.bri_ecr_lib.BriEcrLib

class BriErcManager(private val activity: Activity) {

    lateinit var ercLib: BriEcrLib

    fun initLib() {
        ercLib = BriEcrLib(activity = activity)
    }

    fun getConnectionManager(ercLib: BriEcrLib)  = ConnectionManager(ercLib)
}