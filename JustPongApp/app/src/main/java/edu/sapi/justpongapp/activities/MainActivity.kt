package edu.sapi.justpongapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import edu.sapi.justpongapp.R
import edu.sapi.justpongapp.backend.*
import edu.sapi.justpongapp.backend.models.Message
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MAIN ACTIVITY"
        //  const val SERVER_IP = "10.0.99.109"
        const val SERVER_IP = "192.168.1.6"
//        const val SERVER_IP = "10.0.74.131"
        const val PORT = 8000
    }

    private lateinit var messageSender: MessageSender
    private lateinit var movementService: MovementService

    lateinit var sendButton: Button
    private lateinit var sendField: EditText

    private fun initComponents() {
        movementService = MovementService(this.applicationContext)

        sendButton = findViewById(R.id.sendButton)
        sendButton.isClickable = false

        sendField = findViewById((R.id.sendEditText))
        sendField.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                sendButton.isEnabled = s.toString().trim{ it <= ' ' }.isNotEmpty()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

//        messageSender = MessageSenderTCP(SERVER_IP, PORT)
//        messageSender = MessageSenderUDP(SERVER_IP, PORT)
        messageSender = MessageSenderWS(SERVER_IP, PORT)

        sendButton.setOnClickListener{
            val msg = sendField.text.toString()
            try {
                val message = Message(msg)
                messageSender.sendMessage(message.toJson())
                Log.d(TAG, "{${message.toJson()}} was sent")
            } catch (e : IOException) {
                Log.d(TAG, "{$msg} was not sent")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()
    }

    override fun onPause() {
        super.onPause()
        movementService.pause()
    }

    override fun onResume() {
        super.onResume()
        movementService.resume()
    }

    override fun onDestroy() {
        messageSender.close()
        super.onDestroy()
    }
}