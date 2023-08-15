package com.example.ecrintegrationapp.ui.features.erc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ecrintegrationapp.theme.ECRIntegrationAppTheme
import com.vfi.bri_ecr_lib.BriEcrLib
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ECRActivity : ComponentActivity() {

    private lateinit var ecrLib: BriEcrLib
    val PORT = 9001

    private var traceNo = mutableStateOf<String>("")
    private var responseResult = mutableStateOf<String>("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: ERCViewModel by viewModels()

        setContent {
            val context = LocalContext.current
            initLib()
            val ipAddress = remember { mutableStateOf("10.7.6.157") }
            val connectionStateMessage = viewModel.socketConnectionState.collectAsState()
            val isConnected = remember { mutableStateOf(false) }
            val trxAmount = remember { mutableStateOf("") }

            ECRIntegrationAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text("ERC")
                        })
                    }
                ) { contentPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        OutlinedTextField(
                            value = ipAddress.value,
                            onValueChange = { ipAddress.value = it },
                            label = { Text("Input ERC IP Address") }
                        )

                        Button(onClick = {
                            viewModel.connect(ecrLib, ipAddress.value, PORT)

                        }) {
                            Text(text = "SEND SOCKET")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Connection Status: ${isConnected.value}")

                        if (connectionStateMessage.value.isNotEmpty()) {
                            Toast.makeText(
                                context,
                                connectionStateMessage.value,
                                Toast.LENGTH_SHORT
                            ).show()
                            isConnected.value = ecrLib.isConnected()
                            Text(text = "Connection Response: ${ecrLib.getMessage()}")
                        }

                        if (isConnected.value || ecrLib.getMessage().contains("already opened")) {
                            OutlinedTextField(
                                value = trxAmount.value,
                                onValueChange = { trxAmount.value = it },
                                label = { Text("0") }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                               sendRequest()
                            }) {
                                Text(text = "SendRequest")
                            }

                            if (responseResult.value.isNotEmpty()) {
                                Text(text = responseResult.value)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun initLib() {
        ecrLib = BriEcrLib(this)
    }

    private fun sendTrxAmount(trxAmount: MutableState<String>) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.action = "com.verifone.transaction.bri"
        intent.putExtra("version", "V0.3")
        intent.putExtra("transType", "SALE")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("amt", "0000000000100")
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // Verify if there's an app available to handle the intent
        if (intent.resolveActivity(packageManager) != null) {
            Toast.makeText(this, "Data sent to the target app", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        } else {
            // Handle the case when no app is available to handle the intent
            // ...
            Toast.makeText(this, "No app with the specific package found!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun voidTrxAmount() {
        if (traceNo.value.isEmpty()) {
            Toast.makeText(this, "No Trace Number Found!", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.action = "com.verifone.transaction.bri"
        intent.putExtra("version", "V0.3")
        intent.putExtra("transType", "VOID")
        val jsonObject = JSONObject()
        try {
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        intent.putExtra("orgTraceNo", traceNo.value)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, resultCode.toString(), Toast.LENGTH_SHORT).show()
        val result = data?.getStringExtra("result")
        val transType = data?.getStringExtra("transType")
        val resultMsg = data?.getStringExtra("resultMsg")
        val transData = data?.getStringExtra("transData")
        if (result == "0") {
            try {
                val jsonObject = JSONObject(transData)
                val amount = jsonObject.getString("amt")
                val traceNumber = jsonObject.getString("traceNo")
                traceNo.value = traceNumber

                //if you want parse JSONArray, please use below approach.
                val jsonArrayStr = jsonObject.getString("***")
                val jsonArray = JSONArray(jsonArrayStr)
                for (i in 0 until jsonArray.length()) {
                    val jsonObj: JSONObject = jsonArray[i] as JSONObject
                    val value = jsonObj.getString("***")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        Toast.makeText(this, "$resultMsg", Toast.LENGTH_SHORT).show()
        print("ECR callback response: $transData")
    }


    private fun sendRequest() {
        val request = JSONObject()
        request.put("transType", "SALE")
        request.put("transAmount", "0000000000100")
        val requestStr: String = request.toString()
        println("Request: $requestStr")
        val packRequest = ecrLib.packRequest(requestStr)
        val isSent = ecrLib.sendSocket(packRequest)
        receiveResponse()
    }

    private fun receiveResponse(){
        val responseMsg = try {
            val socketMessage = ecrLib.recvSocket()
            ecrLib.parseResponse(socketMessage) ?: ""
        } catch (e: StringIndexOutOfBoundsException) {
            println("Exception: ${e.message}")
        } finally {
            ecrLib.closeSocket()
        }
        println("Parsed response message: $responseMsg")
    }
}