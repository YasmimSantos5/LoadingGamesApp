package com.example.loadinggames

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.LoginRequest
import com.example.loadinggames.api.LoginResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var txtCadastrar: TextView
    private lateinit var txtEsqueciSenha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // COMPONENTES
        edtEmail =
            findViewById(R.id.edtEmail)

        edtSenha =
            findViewById(R.id.edtSenha)

        btnEntrar =
            findViewById(R.id.btnEntrar)

        txtCadastrar =
            findViewById(R.id.txtCadastrar)

        txtEsqueciSenha =
            findViewById(R.id.txtEsqueciSenha)


        // BOTÃO ENTRAR
        btnEntrar.setOnClickListener {
            validarLogin()
        }


        // IR PARA CADASTRO
        txtCadastrar.setOnClickListener {

            val intent =
                Intent(
                    this,
                    CadastroActivity::class.java
                )

            startActivity(intent)
        }


        // ESQUECI MINHA SENHA
        txtEsqueciSenha.setOnClickListener {

            val intent =
                Intent(
                    this,
                    EsqueciSenhaActivity::class.java
                )

            startActivity(intent)
        }
    }


    private fun validarLogin() {

        val email =
            edtEmail.text
                .toString()
                .trim()

        val senha =
            edtSenha.text
                .toString()
                .trim()


        // EMAIL VAZIO
        if (email.isEmpty()) {

            edtEmail.error =
                "Digite seu e-mail"

            edtEmail.requestFocus()

            return
        }


        // EMAIL INVÁLIDO
        if (
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            edtEmail.error =
                "Digite um e-mail válido"

            edtEmail.requestFocus()

            return
        }


        // SENHA VAZIA
        if (senha.isEmpty()) {

            edtSenha.error =
                "Digite sua senha"

            edtSenha.requestFocus()

            return
        }


        // TAMANHO DA SENHA
        if (senha.length < 6) {

            edtSenha.error =
                "A senha deve ter pelo menos 6 caracteres"

            edtSenha.requestFocus()

            return
        }


        // DADOS PARA API
        val loginRequest =
            LoginRequest(
                email = email,
                senha = senha
            )


        // CHAMAR API
        RetrofitClient.api
            .login(loginRequest)
            .enqueue(

                object :
                    Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {

                        if (response.isSuccessful) {

                            val loginResponse =
                                response.body()

                            val usuario =
                                loginResponse?.usuario

                            // VERIFICA SE A API RETORNOU O USUÁRIO
                            if (usuario == null) {

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Erro ao obter dados do usuário",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return
                            }


                            // =====================================
                            // SALVAR USUÁRIO LOGADO
                            // =====================================

                            val preferencias =
                                getSharedPreferences(
                                    "LoadingGamesPrefs",
                                    MODE_PRIVATE
                                )

                            preferencias
                                .edit()
                                .putInt(
                                    "idUsuario",
                                    usuario.id
                                )
                                .putString(
                                    "nomeUsuario",
                                    usuario.nome
                                )
                                .putString(
                                    "emailUsuario",
                                    usuario.email
                                )
                                .apply()


                            Log.d(
                                "LOGIN_USUARIO",
                                "ID usuário: ${usuario.id}"
                            )


                            Toast.makeText(
                                this@LoginActivity,
                                loginResponse.mensagem,
                                Toast.LENGTH_SHORT
                            ).show()


                            // ABRIR MAIN ACTIVITY
                            val intent =
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )

                            startActivity(intent)

                            // NÃO VOLTAR PARA LOGIN
                            finish()

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "E-mail ou senha incorretos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<LoginResponse>,
                        t: Throwable
                    ) {

                        Log.e(
                            "LOGIN_API",
                            "ERRO AO CONECTAR COM A API",
                            t
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            "Erro ao conectar com o servidor: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }
}