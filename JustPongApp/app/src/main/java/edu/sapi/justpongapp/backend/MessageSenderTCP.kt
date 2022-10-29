package edu.sapi.justpongapp.backend

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class MessageSenderTCP(private val SERVER_IP: String, private val PORT: Int) : MessageSender() {

    private companion object {
        const val TAG = "MESSAGE SENDER TCP"
    }

    private lateinit var socket: Socket
    private lateinit var dataOutputStream: DataOutputStream

    override fun sendMessage(msg: String)  {
        async {
            try {
                Log.d(TAG, "ITT")

                if (!socket.isConnected) {
                    socket = withContext(Dispatchers.IO) {
                        Socket(SERVER_IP, PORT)
                    }
                    Log.d(TAG, "socket was null")
                }

                dataOutputStream = DataOutputStream(withContext(Dispatchers.IO) {
                    socket.getOutputStream()
                })
            } catch (e: IOException) {
                Log.d(TAG, "IOException")
                throw e
            } catch (e: NullPointerException) {
                Log.d(TAG, "nullptr")
                throw e
            }

            try {
                withContext(Dispatchers.IO) {
                    dataOutputStream.writeBytes(msg)
                    dataOutputStream.flush()
                }
            } catch (e: IOException) {
                Log.d(TAG, "IOException")
                throw e
            }

            try {
                withContext(Dispatchers.IO) {
                    dataOutputStream.close()
                }
                close()
            } catch (e: IOException) {
                Log.d(TAG, "IOException")
                throw e
            }
            Log.d(TAG, "{$msg} sent")
        }
    }

    override fun close() {
        socket.close()
    }

}
