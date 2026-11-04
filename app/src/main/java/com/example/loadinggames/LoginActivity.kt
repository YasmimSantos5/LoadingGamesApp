
package com.example.loadinggames

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.LoginRequest
import com.example.loadinggames.api.LoginResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val roxo = Color.parseColor("#8700F5")
    private val roxoClaro = Color.parseColor("#A855F7")

    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var checkLembrar: CheckBox

    private var loginEmAndamento = false
    private var chamadaLogin: Call<LoginResponse>? = null
    private var dialogCarregamento: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // CAMPOS DO LOGIN
        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        checkLembrar = findViewById(R.id.checkLembrar)

        // OUTROS COMPONENTES
        val txtCadastrar =
            findViewById<TextView>(R.id.txtCadastrar)

        val txtEsqueciSenha =
            findViewById<TextView>(R.id.txtEsqueciSenha)

        val btnMostrarSenha =
            findViewById<ImageView>(R.id.btnMostrarSenhaLogin)

        // CORREÇÃO: ATIVAR O BOTÃO ENTRAR
        btnEntrar.setOnClickListener {
            validarLogin()
        }

        // CONFIGURAR MOSTRAR/OCULTAR SENHA
        configurarOlho(btnMostrarSenha)

        // CONFIGURAR TECLADO
        configurarTeclado()

        // ABRIR CADASTRO
        txtCadastrar.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }

        // ESQUECI MINHA SENHA
        txtEsqueciSenha.setOnClickListener {
            startActivity(
                Intent(this, EsqueciSenhaActivity::class.java)
            )
        }
    }

    // MOSTRAR E OCULTAR SENHA
    private fun configurarOlho(olho: ImageView) {
        var visivel = false

        olho.setOnClickListener {
            visivel = !visivel

            edtSenha.transformationMethod =
                if (visivel) {
                    HideReturnsTransformationMethod.getInstance()
                } else {
                    PasswordTransformationMethod.getInstance()
                }

            olho.setImageResource(
                if (visivel) {
                    R.drawable.ic_olho_fechado_auth
                } else {
                    R.drawable.ic_olho_auth
                }
            )

            olho.contentDescription =
                if (visivel) "Ocultar senha"
                else "Mostrar senha"

            edtSenha.setSelection(edtSenha.text.length)
        }
    }

    // CONFIGURAR TECLADO
    private fun configurarTeclado() {
        edtEmail.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtSenha.imeOptions = EditorInfo.IME_ACTION_DONE

        edtSenha.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validarLogin()
                true
            } else {
                false
            }
        }
    }

    // VALIDAR E REALIZAR LOGIN
    private fun validarLogin() {
        if (loginEmAndamento) return

        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()

        // VALIDAR EMAIL
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarPopup(
                "E-mail inválido",
                "Digite um endereço de e-mail válido.",
                edtEmail
            )
            return
        }

        // VALIDAR SENHA
        if (senha.isEmpty()) {
            mostrarPopup(
                "Senha obrigatória",
                "Digite sua senha para entrar na conta.",
                edtSenha
            )
            return
        }

        // PREPARAR REQUISIÇÃO
        val request = LoginRequest(
            email = email,
            senha = senha
        )

        loginEmAndamento = true
        btnEntrar.isEnabled = false
        btnEntrar.text = "Entrando..."

        mostrarCarregamento()

        // CHAMAR API
        val chamada = RetrofitClient.api.login(request)
        chamadaLogin = chamada

        chamada.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (isFinishing || isDestroyed) return

                finalizarCarregamento()

                // ERRO HTTP
                if (!response.isSuccessful) {
                    Log.e(
                        "LOGIN_API",
                        "Erro HTTP: ${response.code()}"
                    )

                    mostrarPopup(
                        "Não foi possível entrar",
                        if (response.code() == 401 ||
                            response.code() == 400
                        ) {
                            "E-mail ou senha incorretos. Confira seus dados."
                        } else {
                            "Não foi possível realizar o login. Tente novamente."
                        }
                    )
                    return
                }

                // RESPOSTA DO SERVIDOR
                val loginResponse = response.body()
                val usuario = loginResponse?.usuario

                if (usuario == null) {
                    mostrarPopup(
                        "Erro no login",
                        "O servidor não retornou os dados do usuário."
                    )
                    return
                }

                // SALVAR USUÁRIO LOGADO
                val preferencias = getSharedPreferences(
                    "LoadingGamesPrefs",
                    MODE_PRIVATE
                )

                preferencias.edit()
                    .putInt("idUsuario", usuario.id)
                    .putString("emailUsuario", usuario.email)
                    .putString("loginUsuario", usuario.nomeUsuario)
                    .putString("nomeUsuario", usuario.nomeUsuario)
                    .putBoolean(
                        "lembrarLogin",
                        checkLembrar.isChecked
                    )
                    .apply()

                Log.d(
                    "LOGIN_USUARIO",
                    "ID usuário: ${usuario.id}"
                )

                // VOLTAR PARA A HOME LOGADO
                startActivity(
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )

                finish()
            }

            override fun onFailure(
                call: Call<LoginResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || isFinishing || isDestroyed) {
                    return
                }

                finalizarCarregamento()

                Log.e(
                    "LOGIN_API",
                    "Erro ao conectar com a API",
                    t
                )

                mostrarPopup(
                    "Erro de conexão",
                    "Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente."
                )
            }
        })
    }

    // FINALIZAR CARREGAMENTO
    private fun finalizarCarregamento() {
        loginEmAndamento = false
        btnEntrar.isEnabled = true
        btnEntrar.text = "Entrar"

        dialogCarregamento?.dismiss()
        dialogCarregamento = null
    }

    // POP-UP DE CARREGAMENTO
    private fun mostrarCarregamento() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            setPadding(
                dp(24),
                dp(28),
                dp(24),
                dp(28)
            )
        }

        val progresso = ProgressBar(this).apply {
            isIndeterminate = true
            indeterminateTintList =
                ColorStateList.valueOf(roxoClaro)
        }

        val mensagem = TextView(this).apply {
            text = "Entrando na sua conta..."
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER

            setPadding(
                0,
                dp(18),
                0,
                0
            )
        }

        container.addView(progresso)
        container.addView(mensagem)

        dialogCarregamento = AlertDialog.Builder(this)
            .setView(container)
            .setCancelable(false)
            .create()

        dialogCarregamento?.show()
        estilizarJanela(dialogCarregamento)
    }

    // POP-UP PERSONALIZADO
    private fun mostrarPopup(
        titulo: String,
        mensagem: String,
        campo: EditText? = null
    ) {
        campo?.requestFocus()

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL

            setPadding(
                dp(24),
                dp(28),
                dp(24),
                dp(24)
            )
        }

        // FUNDO DO ÍCONE
        val fundoIcone = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#29203D"))
        }

        // ÍCONE DE ALERTA
        val icone = TextView(this).apply {
            text = "!"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(roxoClaro)
            gravity = Gravity.CENTER
            background = fundoIcone
        }

        container.addView(
            icone,
            LinearLayout.LayoutParams(
                dp(58),
                dp(58)
            )
        )

        // TÍTULO
        val textoTitulo = TextView(this).apply {
            text = titulo
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        container.addView(
            textoTitulo,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(18)
            }
        )

        // MENSAGEM
        val textoMensagem = TextView(this).apply {
            text = mensagem
            textSize = 14f
            setTextColor(Color.parseColor("#C5C5D3"))
            gravity = Gravity.CENTER
        }

        container.addView(
            textoMensagem,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
        )

        // BOTÃO ENTENDI
        val botao = Button(this).apply {
            text = "Entendi"
            isAllCaps = false
            textSize = 15f
            setTextColor(Color.WHITE)
            backgroundTintList =
                ColorStateList.valueOf(roxo)
        }

        container.addView(
            botao,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            ).apply {
                topMargin = dp(24)
            }
        )

        // CRIAR POP-UP
        val dialog = AlertDialog.Builder(this)
            .setView(container)
            .create()

        botao.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        estilizarJanela(dialog)
    }

    // ESTILIZAR JANELAS
    private fun estilizarJanela(dialog: AlertDialog?) {
        dialog?.window?.apply {
            setBackgroundDrawableResource(
                R.drawable.bg_popup_auth
            )

            setLayout(
                (
                        resources.displayMetrics.widthPixels * 0.86f
                        ).toInt(),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    // CONVERTER DP
    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }

    // LIBERAR RECURSOS
    override fun onDestroy() {
        chamadaLogin?.cancel()
        dialogCarregamento?.dismiss()

        super.onDestroy()
    }
}
