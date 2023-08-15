package com.example.ecrintegrationapp.ui.features.bluetoothscanner

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.ecrintegrationapp.connection.bluetooth.ScanFilterService_UUID
import kotlinx.coroutines.flow.MutableStateFlow

class BluetoothScannerViewModel : ViewModel() {

    private val SCAN_PERIOD_IN_MILLIS: Long = 90_000

    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanCallback: ScanCallback? = null
    private var handler: Handler? = null
    // ui state
    val scanState = MutableStateFlow("")
    val scanResultList = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResult = MutableStateFlow<ScanResult?>(null)

    fun initializeBluetoothScan(context: Context) {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = manager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        handler = Handler(Looper.myLooper()!!)
    }

    fun startScan(context: Context) {
        if (scanCallback != null) {
            return
        }
        handler?.postDelayed({ stopScanning(context) }, SCAN_PERIOD_IN_MILLIS)
        scanCallback = SampleScanCallback()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothLeScanner.startScan(scanCallback)
    }

    fun stopScanning(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothLeScanner?.stopScan(scanCallback)
        scanCallback = null
        // update 'last seen' times even though there are no new results
    }

    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ScanFilterService_UUID)
            .build()
        return listOf(scanFilter)
    }

    private fun buildScanSettings() = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()


    inner class SampleScanCallback : ScanCallback() {

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.let {
                scanResultList.value = it
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            scanResult.value = result
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }
}