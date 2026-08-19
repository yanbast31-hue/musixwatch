package com.exemple.fauxappel

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var callButton: Button

    private val NOTIF_PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        callButton = findViewById(R.id.callButton)
        statusText.text = "Appuie sur le bouton pour déclencher le faux appel dans 5 secondes"

        callButton.setOnClickListener {
            // Sur Android 13 et plus, il faut demander la permission d'afficher des notifications
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIF_PERMISSION_REQUEST_CODE
                )
            } else {
                lancerCompteARebours()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIF_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lancerCompteARebours()
            } else {
                statusText.text = "La permission de notification est nécessaire pour que ça fonctionne"
            }
        }
    }

    private fun lancerCompteARebours() {
        statusText.text = "Faux appel dans 5 secondes..."
        callButton.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            statusText.text = "Appuie sur le bouton pour recommencer"
            callButton.isEnabled = true
            NotificationHelper.afficherAppelEntrant(this)
        }, 5000)
    }
}
