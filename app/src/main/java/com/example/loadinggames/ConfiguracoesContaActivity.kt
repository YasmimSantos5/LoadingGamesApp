
package com.example.loadinggames

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ConfiguracoesContaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes_conta)

        val prefs = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        // NOTIFICAÇÕES
        val switchNotificacoes =
            findViewById<Switch>(R.id.switchNotificacoes)

        switchNotificacoes.isChecked =
            prefs.getBoolean("notificacoesAtivas", true)

        switchNotificacoes.setOnCheckedChangeListener { _, ativo ->
            prefs.edit()
                .putBoolean("notificacoesAtivas", ativo)
                .apply()

            Toast.makeText(
                this,
                if (ativo) "Preferência de notificações ativada"
                else "Preferência de notificações desativada",
                Toast.LENGTH_SHORT
            ).show()
        }

        // PRIVACIDADE
        findViewById<TextView>(R.id.btnPrivacidadeConta)
            .setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Privacidade")
                    .setMessage(
                        "As configurações de privacidade " +
                                "serão disponibilizadas em breve."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }

        // IDIOMA
        findViewById<TextView>(R.id.btnIdiomaConta)
            .setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Idioma")
                    .setMessage(
                        "Idioma atual: Português (Brasil).\n\n" +
                                "Outros idiomas ainda não estão disponíveis."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }

        // TEMA
        findViewById<TextView>(R.id.btnTemaConta)
            .setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Tema")
                    .setMessage(
                        "Tema atual: Escuro.\n\n" +
                                "O tema da PLAYZONE segue o visual " +
                                "original do aplicativo."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }

        // EXCLUIR CONTA
        findViewById<TextView>(R.id.btnExcluirConta)
            .setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Excluir conta")
                    .setMessage(
                        "A exclusão permanente da conta " +
                                "ainda não está disponível.\n\n" +
                                "Essa função precisará de confirmação " +
                                "de identidade e integração segura com a API."
                    )
                    .setPositiveButton("ENTENDI", null)
                    .show()
            }

        // SAIR DA CONTA
        findViewById<Button>(R.id.btnSairConfiguracoes)
            .setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Sair da conta")
                    .setMessage("Deseja realmente sair?")
                    .setPositiveButton("SAIR") { _, _ ->

                        prefs.edit()
                            .remove("idUsuario")
                            .remove("nomeUsuario")
                            .remove("emailUsuario")
                            .remove("loginUsuario")
                            .remove("fotoPerfilUrl")
                            .apply()

                        val intent = Intent(
                            this,
                            MainActivity::class.java
                        )

                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("CANCELAR", null)
                    .show()
            }
    }
}
