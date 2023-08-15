package com.example.ecrintegrationapp.ui.features.erc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecrintegrationapp.connection.ConnectionState
import com.example.ecrintegrationapp.connection.connectSocket
import com.vfi.bri_ecr_lib.BriEcrLib
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ERCViewModel : ViewModel() {

    var socketConnectionState = MutableStateFlow("")

    fun connect(briEcrLib: BriEcrLib, ipAddress: String, port: Int) {
        viewModelScope.launch {
            when(val state = connectSocket(briEcrLib, ipAddress, port)) {
                is ConnectionState.Error -> {
                   socketConnectionState.value = state.errorMessage
                }
                is ConnectionState.Success -> {
                    socketConnectionState.value = state.data
                }
                else -> {
                    socketConnectionState.value = "Connecting"
                }
            }
        }
    }

    private fun retryConnection() {

    }
}