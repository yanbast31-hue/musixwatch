package com.exemple.fauxappel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class IncomingCallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)

        val answerButton = findViewById<Button>(R.id.answerButton)
        val declineButton = findViewById<Button>(R.id.declineButton)

        answerButton.setOnClickListener {
            envoyerAction(NotificationHelper.ACTION_ANSWER)
            finish()
        }

        declineButton.setOnClickListener {
            envoyerAction(NotificationHelper.ACTION_DECLINE)
            finish()
        }
    }

    private fun envoyerAction(action: String) {
        val intent = Intent(this, CallActionReceiver::class.java)
        intent.action = action
        sendBroadcast(intent)
    }
}
