package com.example.loadinggames

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.example.loadinggames.api.CadastroRequest
import com.example.loadinggames.api.CadastroResponse
import com.example.loadinggames.api.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroActivity : AppCompatActivity() {

    private val roxo = Color.parseColor("#8700F5")
    private val roxoClaro = Color.parseColor("#A855F7")
    private val branco = Color.WHITE

    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var edtConfirmarSenha: EditText
    private lateinit var btnCadastrar: Button

    private var cadastroEmAndamento = false
    private var chamadaCadastro: Call<CadastroResponse>? = null
    private var dialogCarregamento: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmailCadastro)
        edtSenha = findViewById(R.id.edtSenhaCadastro)
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha)
        btnCadastrar = findViewById(R.id.btnCadastrar)

        val btnMostrarSenha =
            findViewById<ImageView>(R.id.btnMostrarSenha)

        val btnMostrarConfirmarSenha =
            findViewById<ImageView>(R.id.btnMostrarConfirmarSenha)

        val btnVoltar =
            findViewById<TextView>(R.id.btnVoltarCadastro)

        val txtEntrar =
            findViewById<TextView>(R.id.txtVoltarLogin)

        configurarOlho(edtSenha, btnMostrarSenha)
        configurarOlho(
            edtConfirmarSenha,
            btnMostrarConfirmarSenha
        )

        configurarTeclado()

        btnVoltar.setOnClickListener {
            finish()
        }

        txtEntrar.setOnClickListener {
            abrirLogin()
        }

        btnCadastrar.setOnClickListener {
            validarECadastrar()
        }
    }

    private fun configurarTeclado() {
        edtNome.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtEmail.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtSenha.imeOptions = EditorInfo.IME_ACTION_NEXT
        edtConfirmarSenha.imeOptions =
            EditorInfo.IME_ACTION_DONE

        edtConfirmarSenha.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validarECadastrar()
                true
            } else {
                false
            }
        }
    }

    private fun configurarOlho(
        campo: EditText,
        olho: ImageView
    ) {
        var visivel = false

        olho.setOnClickListener {
            visivel = !visivel

            campo.transformationMethod =
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

            campo.setSelection(campo.text.length)
        }
    }

    private fun validarECadastrar() {
        if (cadastroEmAndamento) return

        val nome = edtNome.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()
        val confirmarSenha =
            edtConfirmarSenha.text.toString()

        if (nome.length < 3) {
            mostrarAviso(
                "Nome inválido",
                "Informe seu nome completo para continuar.",
                edtNome
            )
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarAviso(
                "E-mail inválido",
                "Digite um endereço de e-mail válido.",
                edtEmail
            )
            return
        }

        if (senha.length < 8) {
            mostrarAviso(
                "Senha muito curta",
                "Sua senha precisa ter pelo menos 8 caracteres para proteger sua conta.",
                edtSenha
            )
            return
        }

        if (confirmarSenha.isEmpty()) {
            mostrarAviso(
                "Confirme sua senha",
                "Digite novamente sua senha para confirmar.",
                edtConfirmarSenha
            )
            return
        }

        if (senha != confirmarSenha) {
            mostrarAviso(
                "As senhas não coincidem",
                "A senha e a confirmação precisam ser iguais. Confira e tente novamente.",
                edtConfirmarSenha
            )
            return
        }

        enviarCadastro(nome, email, senha)
    }

    private fun enviarCadastro(
        nome: String,
        email: String,
        senha: String
    ) {
        cadastroEmAndamento = true
        btnCadastrar.isEnabled = false
        btnCadastrar.text = "Cadastrando..."

        mostrarCarregamento()

        val request = CadastroRequest(
            nome = nome,
            email = email,
            senha = senha
        )

        val chamada = RetrofitClient.api.cadastrar(request)
        chamadaCadastro = chamada

        chamada.enqueue(object : Callback<CadastroResponse> {

            override fun onResponse(
                call: Call<CadastroResponse>,
                response: Response<CadastroResponse>
            ) {
                if (isFinishing || isDestroyed) return

                finalizarCarregamento()

                if (response.isSuccessful) {
                    mostrarSucesso(
                        response.body()?.mensagem
                            ?: "Sua conta foi criada com sucesso!"
                    )
                } else {
                    mostrarAviso(
                        "Não foi possível cadastrar",
                        if (response.code() == 409) {
                            "Este e-mail já pode estar cadastrado. Tente entrar na sua conta."
                        } else {
                            "Não foi possível criar sua conta. Confira os dados e tente novamente."
                        }
                    )
                }
            }

            override fun onFailure(
                call: Call<CadastroResponse>,
                t: Throwable
            ) {
                if (isFinishing || isDestroyed || call.isCanceled) return

                finalizarCarregamento()

                mostrarAviso(
                    "Erro de conexão",
                    "Não conseguimos conectar ao servidor. Verifique sua conexão e tente novamente."
                )
            }
        })
    }

    private fun finalizarCarregamento() {
        cadastroEmAndamento = false
        btnCadastrar.isEnabled = true
        btnCadastrar.text = "Cadastrar"

        dialogCarregamento?.dismiss()
        dialogCarregamento = null
    }

    private fun mostrarCarregamento() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(28), dp(24), dp(28))
        }

        val progresso = ProgressBar(this).apply {
            isIndeterminate = true
            indeterminateTintList =
                ColorStateList.valueOf(roxoClaro)
        }

        val mensagem = TextView(this).apply {
            text = "Criando sua conta..."
            textSize = 16f
            setTextColor(branco)
            gravity = Gravity.CENTER
            setPadding(0, dp(18), 0, 0)
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

    private fun mostrarAviso(
        titulo: String,
        mensagem: String,
        campo: EditText? = null
    ) {
        campo?.requestFocus()

        mostrarPopup(
            simbolo = "!",
            titulo = titulo,
            mensagem = mensagem,
            textoBotao = "Entendi",
            corIcone = roxoClaro
        )
    }

    private fun mostrarSucesso(mensagem: String) {
        mostrarPopup(
            simbolo = "✓",
            titulo = "Conta criada!",
            mensagem = mensagem,
            textoBotao = "Ir para login",
            corIcone = roxoClaro,
            acao = { abrirLogin() }
        )
    }

    private fun mostrarPopup(
        simbolo: String,
        titulo: String,
        mensagem: String,
        textoBotao: String,
        corIcone: Int,
        acao: (() -> Unit)? = null
    ) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
        }

        val fundoIcone = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#29203D"))
        }

        val icone = TextView(this).apply {
            text = simbolo
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(corIcone)
            gravity = Gravity.CENTER
            background = fundoIcone
        }

        container.addView(
            icone,
            LinearLayout.LayoutParams(dp(58), dp(58))
        )

        val textoTitulo = TextView(this).apply {
            text = titulo
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(branco)
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

        val textoMensagem = TextView(this).apply {
            text = mensagem
            textSize = 14f
            setTextColor(Color.parseColor("#C5C5D3"))
            gravity = Gravity.CENTER
            setLineSpacing(dp(3).toFloat(), 1f)
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

        val botao = Button(this).apply {
            text = textoBotao
            isAllCaps = false
            textSize = 15f
            setTextColor(branco)
            backgroundTintList = ColorStateList.valueOf(roxo)
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

        val dialog = AlertDialog.Builder(this)
            .setView(container)
            .create()

        botao.setOnClickListener {
            dialog.dismiss()
            acao?.invoke()
        }

        dialog.show()
        estilizarJanela(dialog)
    }

    private fun estilizarJanela(dialog: AlertDialog?) {
        dialog?.window?.apply {
            setBackgroundDrawableResource(
                R.drawable.bg_popup_auth
            )
            setLayout(
                (resources.displayMetrics.widthPixels * 0.86f).toInt(),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }

    private fun abrirLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
        finish()
    }

    override fun onDestroy() {
        chamadaCadastro?.cancel()
        dialogCarregamento?.dismiss()
        super.onDestroy()
    }
}
