package com.example.ecrintegrationapp.connection.bluetooth

import android.app.Activity
import android.app.AlertDialog
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale

class MyBluetoothPermissionManager {

     fun requestLocationPermission(activity: Activity) {
        if (shouldShowRequestPermissionRationale(activity, LOCATION_FINE_PERM)) {
            val alertDialogBuilder = AlertDialog.Builder(activity)
            with(alertDialogBuilder) {
                setTitle("Request for location permission")
                setMessage("You need to enable location permission to use this feature")
                setPositiveButton("Okay") { _, _ -> makeLocationRequest(activity) }
            }
            alertDialogBuilder.create().show()
        } else {
            makeLocationRequest(activity)
        }
    }

    private fun makeLocationRequest(activity: Activity) = requestPermissions(
        activity,
        arrayOf(LOCATION_FINE_PERM),
        PERMISSION_REQUEST_LOCATION
    )
}