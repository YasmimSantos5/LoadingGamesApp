
package com.example.loadinggames

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.AlterarSenhaRequest
import com.example.loadinggames.api.AlterarSenhaResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlterarSenhaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_senha)

        val prefs = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        val edtSenhaAtual =
            findViewById<EditText>(R.id.edtSenhaAtual)

        val edtNovaSenha =
            findViewById<EditText>(R.id.edtNovaSenha)

        val edtConfirmarSenha =
            findViewById<EditText>(R.id.edtConfirmarSenha)

        val btnSalvar =
            findViewById<Button>(R.id.btnAlterarSenhaSalvar)

        val idUsuario = prefs.getInt("idUsuario", -1)

        if (idUsuario <= 0) {
            Toast.makeText(
                this,
                "Faça login para alterar sua senha.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        btnSalvar.setOnClickListener {

            val atual = edtSenhaAtual.text.toString()
            val nova = edtNovaSenha.text.toString()
            val confirmar = edtConfirmarSenha.text.toString()

            when {
                atual.isBlank() ||
                        nova.isBlank() ||
                        confirmar.isBlank() -> {
                    Toast.makeText(
                        this,
                        "Preencha todos os campos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                nova.length < 8 -> {
                    Toast.makeText(
                        this,
                        "A nova senha deve ter pelo menos 8 caracteres.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                nova != confirmar -> {
                    Toast.makeText(
                        this,
                        "As senhas não coincidem.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                nova == atual -> {
                    Toast.makeText(
                        this,
                        "A nova senha deve ser diferente da atual.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    btnSalvar.isEnabled = false

                    val request = AlterarSenhaRequest(
                        idUsuario = idUsuario,
                        senhaAtual = atual,
                        novaSenha = nova
                    )

                    RetrofitClient.api
                        .alterarSenha(request)
                        .enqueue(
                            object : Callback<AlterarSenhaResponse> {

                                override fun onResponse(
                                    call: Call<AlterarSenhaResponse>,
                                    response: Response<AlterarSenhaResponse>
                                ) {
                                    if (isFinishing || isDestroyed) return

                                    btnSalvar.isEnabled = true

                                    if (response.isSuccessful) {
                                        edtSenhaAtual.text.clear()
                                        edtNovaSenha.text.clear()
                                        edtConfirmarSenha.text.clear()

                                        Toast.makeText(
                                            this@AlterarSenhaActivity,
                                            "Senha alterada com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        finish()
                                    } else {
                                        val mensagem = when (response.code()) {
                                            400 -> "Verifique a nova senha."
                                            401 -> "Senha atual incorreta ou conta inativa."
                                            else -> "Não foi possível alterar a senha."
                                        }

                                        Toast.makeText(
                                            this@AlterarSenhaActivity,
                                            mensagem,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<AlterarSenhaResponse>,
                                    t: Throwable
                                ) {
                                    if (isFinishing || isDestroyed) return

                                    btnSalvar.isEnabled = true

                                    Toast.makeText(
                                        this@AlterarSenhaActivity,
                                        "Erro ao conectar com o servidor.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                }
            }
        }
    }
}
