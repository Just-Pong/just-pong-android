package edu.sapi.justpongapp.backend

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.async


class MessageSenderWS(private val SERVER_IP: String, private val PORT: Int) : MessageSender() {

    companion object {
        const val TAG = "MESSAGE SENDER WS"
        const val NORMAL_CLOSURE_STATUS = 1000
    }

    private var client : HttpClient = HttpClient(CIO) { install(WebSockets) }

    override fun sendMessage(msg: String) {
        async {
//            val u = "ws://$SERVER_IP:$PORT/ws"

            client.webSocket(method = HttpMethod.Get, host = SERVER_IP, port = PORT, path = "/ws") {
                send(msg)
            }

        }
    }

    override fun close() {
        client.close()
    }
}