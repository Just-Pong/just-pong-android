package edu.sapi.justpongapp.backend

import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

object WebSocketManager {

    private val TAG = WebSocketManager::class.java.simpleName
    private  const  val  MAX_NUM  =  5  // Maximum number of reconnections
    private  const  val  MILLIS  =  5000  // Reconnection interval, milliseconds

    private lateinit var _client: OkHttpClient
    private lateinit var _request: Request
    private lateinit var _messageListener: MessageListener
    private lateinit var _webSocket: WebSocket
    private var _isConnected = false
    private val isConnected get() = _isConnected
    private var _connectNum = 0

    fun init(url: String, messageListener: MessageListener) {
        _client = OkHttpClient().newBuilder()
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

        _request = Request.Builder().url(url).build()
        _messageListener = messageListener
    }

    fun connect() {
        if (_isConnected) {
            Log.i(TAG, "Web Socket Connected")
            return
        }
        _client.newWebSocket(
            request =  _request,
            listener = createListener()
        )
    }

    fun reconnect() {
        if (_connectNum <= MAX_NUM) {
            try {
                Thread.sleep(MILLIS.toLong())
                connect()
                _connectNum++
            } catch (e: InterruptedException) {
                e.printStackTrace ()
            }
        } else {
            Log.i(
                TAG,
                "reconnect over $MAX_NUM,please check url or network"
            )
        }
    }

    fun sendMessage(text: String): Boolean {
        return if (!isConnected) false else _webSocket.send(text)
    }

    fun close() {
        if (isConnected) {
            _webSocket.cancel()
            _webSocket.close( 1001 , "The client actively closes the connection " )
        }
    }

    private fun createListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket,
                response: Response
            ) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "open:$response")
                _webSocket = webSocket
                _isConnected = response.code == 101
                if (!isConnected) {
                    reconnect()
                } else {
                    Log.i(TAG, "connect success.")
                    _messageListener.onConnectSuccess()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                _messageListener.onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                _messageListener.onMessage(bytes.base64())
            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                super.onClosing(webSocket, code, reason)
                _isConnected = false
                _messageListener.onClose()
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                super.onClosed(webSocket, code, reason)
                _isConnected = false
                _messageListener.onClose()
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                super.onFailure(webSocket, t, response)
                if (response != null) {
                    Log.i(
                        TAG,
                        "connect failed：" + response.message
                    )
                }
                Log.i(
                    TAG,
                    "connect failed throwable：" + t.message
                )
                _isConnected = false
                _messageListener.onConnectFailed()
                reconnect()
            }
        }
    }
}