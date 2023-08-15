package com.example.ecrintegrationapp.ui.features.bluetoothscanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecrintegrationapp.connection.bluetooth.LOCATION_FINE_PERM
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScannerScreen(
    titleApp: MutableState<String>,
    navController: NavController,
    viewModel: BluetoothScannerViewModel = viewModel(),
    context: Context
) {
    LaunchedEffect(key1 = 1) {
        titleApp.value = "Bluetooth Scanner"
    }

    val allPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
    )

    val permissionState = rememberPermissionState(
        permission = LOCATION_FINE_PERM
    )

    val scanState = viewModel.scanState.collectAsState()
    val listBluetoothDevices = viewModel.scanResultList.collectAsState()
            
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            when (permissionState.status) {
                // If the camera permission is granted, then show screen with the feature enabled
                PermissionStatus.Granted -> {
                    viewModel.initializeBluetoothScan(context)
                    Text("Location permission Granted")
                    Button(onClick = {
                        viewModel.startScan(context)
                    }) {
                        Text("Start Scan")
                    }

                    Button(onClick = {
                        viewModel.stopScanning(context)
                    }) {
                        Text("Stop Scan")
                    }
                    
                    if (listBluetoothDevices.value.isNotEmpty()) {
                        LazyColumn {
                            items(listBluetoothDevices.value) {
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                  return@items
                                }
                                Text(text = it.device.name)
                            }
                        }
                    }
                }
                is PermissionStatus.Denied -> {
                    Column {
                        Button(onClick = { permissionState.launchPermissionRequest() }) {
                            Text("Request Permission Before Start")
                        }
                    }
                }
            }
        }
        else {

            if (allPermissionState.allPermissionsGranted) {
                viewModel.initializeBluetoothScan(context)

                if (scanState.value.isNotEmpty()) {
                    Text(scanState.value)
                } else {
                    Text("All permissions Granted")
                }

                Button(onClick = {
                    viewModel.startScan(context)
                }) {
                    Text("Start Scan")
                }
            } else {
                Column {
                    Button(onClick = { allPermissionState.launchMultiplePermissionRequest() }) {
                        Text("Request Permissions Before Start")
                    }
                }
            }
        }


       /* when (locationPermissionState.permissions) {
            // If the camera permission is granted, then show screen with the feature enabled
            PermissionStatus.Granted -> {

            }

            is PermissionStatus.Denied -> {

            }
        }*/
    }

}