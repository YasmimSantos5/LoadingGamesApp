
package com.example.loadinggames

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MeusDadosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_dados)

        val prefs = getSharedPreferences("LoadingGamesPrefs", MODE_PRIVATE)

        val edtNome = findViewById<EditText>(R.id.edtNomeDados)
        val edtEmail = findViewById<EditText>(R.id.edtEmailDados)

        edtNome.setText(prefs.getString("nomeUsuario", ""))
        edtEmail.setText(prefs.getString("emailUsuario", ""))

        findViewById<Button>(R.id.btnSalvarDados).setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString("nomeUsuario", nome)
                .putString("emailUsuario", email)
                .apply()

            Toast.makeText(this, "Dados atualizados neste dispositivo!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
