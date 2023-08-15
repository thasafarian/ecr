package com.example.ecrintegrationapp.connection

import com.vfi.bri_ecr_lib.BriEcrLib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


sealed class ConnectionState<out T> {
    object Loading : ConnectionState<Nothing>()
    data class Success<T>(val data: T) : ConnectionState<T>()
    data class Error(val errorMessage: String) : ConnectionState<Nothing>()
}

class ConnectionManager(private val ercLib: BriEcrLib)  {
    fun getConnectionMessage() = ercLib.getMessage()
}
suspend fun connectSocket(
    briEcrLib: BriEcrLib,
    ipAddress: String,
    port: Int,
    retryInterval: Long = 1000L
): ConnectionState<String> {
    var retryCount = 0
    val maxRetries = 3
    var connectionState = false

    while (retryCount < maxRetries) {
        val isConnected = withContext(Dispatchers.Default) {
            briEcrLib.openSocket(
                ip = ipAddress.trim(),
                port = port,
                true
            )
        }

        if (isConnected) {
            connectionState = true
            break
        }
        // Increment the retry count and wait for the specified interval before retrying
        retryCount++
        delay(retryInterval)
    }
    // If the maximum number of retries has been reached, return null to indicate failure
    return if (!connectionState) {
        ConnectionState.Error("Failed to establish connection")
    } else {
        ConnectionState.Success("Connection established successfully")
    }
}
