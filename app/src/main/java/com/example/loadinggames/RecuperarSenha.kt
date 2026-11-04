
package com.example.loadinggames

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EsqueciSenhaActivity : AppCompatActivity() {

    private val roxo = Color.parseColor("#8700F5")
    private val roxoClaro = Color.parseColor("#A855F7")

    private lateinit var edtEmail: EditText
    private lateinit var btnRecuperarSenha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        edtEmail = findViewById(R.id.edtEmailRecuperacao)
        btnRecuperarSenha = findViewById(R.id.btnRecuperarSenha)

        val txtVoltarLogin =
            findViewById<TextView>(R.id.txtVoltarLogin)

        val btnVoltar =
            findViewById<TextView>(R.id.btnVoltarRecuperacao)

        txtVoltarLogin.setOnClickListener {
            finish()
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        btnRecuperarSenha.setOnClickListener {
            recuperarSenha()
        }

        edtEmail.imeOptions = EditorInfo.IME_ACTION_DONE

        edtEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                recuperarSenha()
                true
            } else {
                false
            }
        }
    }

    private fun recuperarSenha() {
        val email = edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            mostrarPopup(
                "E-mail obrigatório",
                "Digite seu e-mail cadastrado para continuar."
            )
            edtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarPopup(
                "E-mail inválido",
                "Informe um endereço de e-mail válido."
            )
            edtEmail.requestFocus()
            return
        }

        // O envio real será implementado no backend.
        mostrarPopup(
            "Recuperação de senha",
            "Seu e-mail foi validado. O envio das instruções será disponibilizado quando a recuperação estiver conectada ao servidor."
        )
    }

    private fun mostrarPopup(
        titulo: String,
        mensagem: String
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
            text = "!"
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(roxoClaro)
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

        val botao = Button(this).apply {
            text = "Entendi"
            isAllCaps = false
            textSize = 15f
            setTextColor(Color.WHITE)
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
        }

        dialog.show()

        dialog.window?.apply {
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
}
