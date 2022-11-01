package edu.sapi.justpongapp.backend

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MessageSenderUDP(SERVER_IP: String, private val PORT: Int) : MessageSender() {

    private companion object {
        const val TAG = "MESSAGE SENDER UDP"
    }

    private val socket: DatagramSocket = DatagramSocket()
    private val address: InetAddress = InetAddress.getByName(SERVER_IP)

    private lateinit var buffer: ByteArray

    override fun sendMessage(msg: String) {
        async {
            Log.d(TAG, "{$msg} sent")
            buffer = msg.toByteArray()

            val datagramPacket = DatagramPacket(buffer, buffer.size, address, PORT)
            withContext(Dispatchers.IO) {
                socket.send(datagramPacket)
            }
        }
    }

    override fun close() {
        socket.close()
    }
}