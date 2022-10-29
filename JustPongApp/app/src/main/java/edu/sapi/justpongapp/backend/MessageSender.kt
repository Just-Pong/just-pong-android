package edu.sapi.justpongapp.backend

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class MessageSender : CoroutineScope by MainScope() {
    abstract fun sendMessage(msg: String)
    abstract fun close()
}