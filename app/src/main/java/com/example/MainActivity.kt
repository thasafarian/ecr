package com.example

import MainScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecrintegrationapp.theme.ECRIntegrationAppTheme
import com.example.ecrintegrationapp.ui.features.bluetoothscanner.BluetoothScannerScreen
import com.example.ecrintegrationapp.ui.features.bluetoothscanner.BluetoothScannerViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize ViewModels
        val bluetoothScannerViewModel : BluetoothScannerViewModel by viewModels()

        setContent {
            val navController = rememberNavController()
            val titleApp = remember { mutableStateOf("Playground APP") }
            ECRIntegrationAppTheme {

                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text(titleApp.value)
                        })
                    }
                ) { contentPadding ->
                    NavHost(modifier = Modifier.padding(contentPadding), navController = navController, startDestination = MAIN_ROUTE) {
                        composable(MAIN_ROUTE) {
                            MainScreen(navController = navController)
                        }
                        composable(BLUETOOTH_ROUTE) {
                            BluetoothScannerScreen(
                                titleApp = titleApp,
                                navController = navController,
                                viewModel = bluetoothScannerViewModel,
                                context = this@MainActivity
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MAIN_ROUTE =  "main_screen"
        const val ERC_ROUTE =  "erc_screen"
        const val BLUETOOTH_ROUTE =  "bluetooth_scanner_screen"
    }
}