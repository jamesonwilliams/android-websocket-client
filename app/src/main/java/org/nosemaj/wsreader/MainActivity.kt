package org.nosemaj.wsreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import org.nosemaj.wsreader.R.string
import org.nosemaj.wsreader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var ws: WebSocket
    private var lineNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener { sendCommand("turn_on_lights") }
        setupWebSocket()
    }

    private fun sendCommand(@Suppress("SameParameterValue") command: String) {
        ws.send(JSONObject()
                .put("action", "sendmessage")
                .put("data", command)
                .toString())
        appendStatus(getString(string.websocket_sent_command, command))
    }

    private fun setupWebSocket() {
        val request = Request.Builder()
                .url(getString(string.websocket_url))
                .build()
        ws = OkHttpClient().newWebSocket(request, object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                appendStatus(getString(string.websocket_opened))
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                appendStatus(getString(string.websocket_closed))
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                appendStatus(getString(string.websocket_failure, t.message))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                appendStatus(getString(string.websocket_received_message, text))
            }
        })
    }

    private fun appendStatus(message: String) {
        binding.status.append(getString(string.status_line, lineNumber, message))
        lineNumber++
    }
}