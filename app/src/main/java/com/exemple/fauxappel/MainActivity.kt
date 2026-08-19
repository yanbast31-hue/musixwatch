package com.exemple.fauxappel

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var phoneNumberInput: EditText
    private lateinit var callButton: Button
    private lateinit var prefs: SharedPreferences

    // Code utilisé pour identifier la demande de permission
    private val CALL_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        phoneNumberInput = findViewById(R.id.phoneNumberInput)
        callButton = findViewById(R.id.callButton)

        // On sauvegarde le numéro saisi pour ne pas avoir à le retaper à chaque fois
        prefs = getSharedPreferences("fauxappel_prefs", MODE_PRIVATE)
        val savedNumber = prefs.getString("numero", "")
        phoneNumberInput.setText(savedNumber)

        callButton.setOnClickListener {
            val numero = phoneNumberInput.text.toString().trim()

            if (numero.isEmpty()) {
                Toast.makeText(this, "Merci de saisir un numéro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // On sauvegarde le numéro pour la prochaine fois
            prefs.edit().putString("numero", numero).apply()

            // On vérifie qu'on a bien la permission d'appeler
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

                // On la demande si on ne l'a pas encore
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    CALL_PERMISSION_REQUEST_CODE
                )
            } else {
                // On a déjà la permission : on lance le compte à rebours
                lancerCompteARebours(numero)
            }
        }
    }

    // Cette fonction est appelée automatiquement quand l'utilisateur répond à la demande de permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val numero = phoneNumberInput.text.toString().trim()
                lancerCompteARebours(numero)
            } else {
                Toast.makeText(
                    this,
                    "La permission d'appel est nécessaire pour que ça fonctionne",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun lancerCompteARebours(numero: String) {
        statusText.text = "Appel dans 5 secondes..."
        callButton.isEnabled = false

        // Handler qui attend 5000 millisecondes (5 secondes) avant d'agir
        Handler(Looper.getMainLooper()).postDelayed({
            statusText.text = "Appel en cours..."
            callButton.isEnabled = true
            passerAppel(numero)
        }, 5000)
    }

    private fun passerAppel(numero: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$numero")

        try {
            startActivity(intent)
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Impossible de lancer l'appel : permission refusée",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
