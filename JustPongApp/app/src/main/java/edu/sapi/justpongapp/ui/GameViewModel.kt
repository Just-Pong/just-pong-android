package edu.sapi.justpongapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.sapi.justpongapp.backend.MessageListener
import edu.sapi.justpongapp.backend.MovementService
import edu.sapi.justpongapp.backend.WebSocketManager
import edu.sapi.justpongapp.backend.models.MovementData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {

    inner class MessageListenerImpl: MessageListener {
        override fun onConnectSuccess() {
        }

        override fun onConnectFailed() {
        }

        override fun onClose() {
        }

        override fun onMessage(text: String?) {
        }

    }

    companion object {
        val TAG = GameViewModel::class.simpleName
        const val SERVER_IP = "192.168.0.103"
        const val PORT = 8000
    }

    @SuppressLint("StaticFieldLeak")
    private var _movementService: MovementService? = null
    private val movementService get() = _movementService!!

    private var _gameId: String? = null
    private val gameId get() = _gameId
    fun setGameId(gameId: String) {
        _gameId = gameId
    }

    private var _isRunning: Boolean = false
    private val isRunning get() = _isRunning

    private fun combineConnectionUrl(): String = "ws://$SERVER_IP:$PORT/game/$gameId/ws"

    fun startGame(context: Context) {
        if (gameId.isNullOrEmpty())
            return

        _movementService = MovementService(context)
        movementService.resume()

        val connectionUrl = combineConnectionUrl()

        WebSocketManager.init(connectionUrl, MessageListenerImpl())
        _isRunning = true
        gameLoop()
    }

    private fun gameLoop() {
        viewModelScope.launch {

            WebSocketManager.connect()

            while (isRunning) {
                delay(1)
                val position = movementService.getPositionD()/10
                val toSend = MovementData(position).toJson()
                Log.d(TAG, toSend)
                WebSocketManager.sendMessage(toSend)
            }

            WebSocketManager.close()
        }
    }

    fun pauseGame() {
        _isRunning = false
    }

    fun resumeGame() {
        _isRunning = true
        gameLoop()
    }

    fun pauseMovementService() {
        if (_movementService != null)
            movementService.pause()
    }

    fun resumeMovementService() {
        if (_movementService != null) {
            movementService.resume()
        }
    }

    fun closeWebSocket() = WebSocketManager.close()
}