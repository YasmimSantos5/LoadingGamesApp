
package com.example.loadinggames

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.AdicionarCarrinhoRequest
import com.example.loadinggames.api.AdicionarCarrinhoResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalhesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalhes)

        // BOTÃO VOLTAR
        val btnVoltarDetalhes =
            findViewById<TextView>(R.id.btnVoltarDetalhes)

        btnVoltarDetalhes.setOnClickListener {
            finish()
        }

        // COMPONENTES
        val imgJogo =
            findViewById<ImageView>(R.id.imgJogo)

        val txtNomeJogo =
            findViewById<TextView>(R.id.txtNomeJogo)

        val txtClassificacao =
            findViewById<TextView>(R.id.txtClassificacao)

        val txtDataLancamento =
            findViewById<TextView>(R.id.txtDataLancamento)

        val txtDescricao =
            findViewById<TextView>(R.id.txtDescricao)

        val txtPreco =
            findViewById<TextView>(R.id.txtPreco)

        val txtPlataforma =
            findViewById<TextView>(R.id.txtPlataforma)

        val btnAdicionarCarrinho =
            findViewById<Button>(R.id.btnAdicionarCarrinho)

        val btnFavorito =
            findViewById<Button>(R.id.btnFavorito)

        // RECEBER DADOS DO JOGO
        val idJogo = intent.getIntExtra("idJogo", -1)

        val nome =
            intent.getStringExtra("nome") ?: "Jogo"

        val imagem =
            intent.getIntExtra("imagem", 0)

        val preco =
            intent.getStringExtra("preco") ?: "R$ 0,00"

        val descricao =
            intent.getStringExtra("descricao")
                ?: "Sem descrição disponível."

        val classificacao =
            intent.getIntExtra("classificacao", 0)

        val dataLancamento =
            intent.getStringExtra("dataLancamento")

        // PREENCHER INFORMAÇÕES
        txtNomeJogo.text = nome
        txtPreco.text = preco
        txtDescricao.text = descricao

        txtPlataforma.text = "Disponível no catálogo"

        txtClassificacao.text =
            if (classificacao == 0) {
                "Classificação: Livre"
            } else {
                "Classificação: $classificacao anos"
            }

        txtDataLancamento.text =
            formatarData(dataLancamento)

        if (imagem != 0) {
            imgJogo.setImageResource(imagem)
        }

        // BOTÃO ADICIONAR AO CARRINHO
        btnAdicionarCarrinho.setOnClickListener {
            adicionarAoCarrinho(
                idJogo = idJogo,
                nomeJogo = nome,
                botao = btnAdicionarCarrinho
            )
        }

        // BOTÃO FAVORITOS
        btnFavorito.setOnClickListener {
            Toast.makeText(
                this,
                "$nome adicionado aos favoritos!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun adicionarAoCarrinho(
        idJogo: Int,
        nomeJogo: String,
        botao: Button
    ) {
        val preferencias = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        val idUsuario =
            preferencias.getInt("idUsuario", -1)

        // EXIGIR LOGIN
        if (idUsuario == -1) {
            Toast.makeText(
                this,
                "Faça login ou cadastre-se para adicionar jogos ao carrinho.",
                Toast.LENGTH_LONG
            ).show()

            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            return
        }

        // VALIDAR ID DO JOGO
        if (idJogo == -1) {
            Toast.makeText(
                this,
                "Não foi possível identificar o jogo.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // EVITAR CLIQUES REPETIDOS
        botao.isEnabled = false
        botao.text = "ADICIONANDO..."

        val request = AdicionarCarrinhoRequest(
            idUsuario = idUsuario,
            idJogo = idJogo
        )

        RetrofitClient.api
            .adicionarAoCarrinho(request)
            .enqueue(
                object : Callback<AdicionarCarrinhoResponse> {

                    override fun onResponse(
                        call: Call<AdicionarCarrinhoResponse>,
                        response: Response<AdicionarCarrinhoResponse>
                    ) {
                        if (isFinishing || isDestroyed) return

                        botao.isEnabled = true
                        botao.text = "ADICIONAR AO CARRINHO"

                        if (response.isSuccessful) {
                            mostrarPopupCarrinho(nomeJogo)
                            return
                        }

                        if (response.code() == 409) {
                            Toast.makeText(
                                this@DetalhesActivity,
                                "Este jogo já está no carrinho.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }

                        Toast.makeText(
                            this@DetalhesActivity,
                            "Não foi possível adicionar o jogo. Erro ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailure(
                        call: Call<AdicionarCarrinhoResponse>,
                        t: Throwable
                    ) {
                        if (call.isCanceled || isFinishing || isDestroyed) {
                            return
                        }

                        botao.isEnabled = true
                        botao.text = "ADICIONAR AO CARRINHO"

                        Toast.makeText(
                            this@DetalhesActivity,
                            "Erro ao conectar com o servidor: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    // POP-UP PERSONALIZADO DO CARRINHO
    private fun mostrarPopupCarrinho(nomeJogo: String) {

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
        }

        // ÍCONE DE SUCESSO
        val icone = TextView(this).apply {
            text = "✓"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#7C3AED"))
            }
        }

        container.addView(
            icone,
            LinearLayout.LayoutParams(dp(64), dp(64))
        )

        // TÍTULO
        val titulo = TextView(this).apply {
            text = "Adicionado ao carrinho!"
            textSize = 20f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        container.addView(
            titulo,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(20)
            }
        )

        // MENSAGEM
        val mensagem = TextView(this).apply {
            text = "$nomeJogo foi adicionado com sucesso.\n\n" +
                    "O que deseja fazer agora?"
            textSize = 14f
            setTextColor(Color.parseColor("#B7BAC7"))
            gravity = Gravity.CENTER
        }

        container.addView(
            mensagem,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
        )

        // BOTÃO FINALIZAR
        val btnFinalizar = Button(this).apply {
            text = "Finalizar compra"
            isAllCaps = false
            textSize = 15f
            setTextColor(Color.WHITE)
            backgroundTintList =
                ColorStateList.valueOf(
                    Color.parseColor("#7C3AED")
                )
        }

        container.addView(
            btnFinalizar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(54)
            ).apply {
                topMargin = dp(24)
            }
        )

        // BOTÃO CONTINUAR
        val btnContinuar = Button(this).apply {
            text = "Continuar comprando"
            isAllCaps = false
            textSize = 14f
            setTextColor(Color.WHITE)
            backgroundTintList =
                ColorStateList.valueOf(
                    Color.parseColor("#292D43")
                )
        }

        container.addView(
            btnContinuar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            ).apply {
                topMargin = dp(10)
            }
        )

        // CRIAR DIÁLOGO
        val dialog = AlertDialog.Builder(this)
            .setView(container)
            .setCancelable(true)
            .create()

        btnFinalizar.setOnClickListener {
            dialog.dismiss()

            startActivity(
                Intent(
                    this,
                    CarrinhoActivity::class.java
                )
            )
        }

        btnContinuar.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()

        // FUNDO ESCURO ARREDONDADO
        dialog.window?.apply {
            setBackgroundDrawableResource(
                R.drawable.bg_popup_auth
            )

            setLayout(
                (resources.displayMetrics.widthPixels * 0.88f).toInt(),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    // FORMATAR DATA DE LANÇAMENTO
    private fun formatarData(data: String?): String {

        if (data.isNullOrBlank()) {
            return "Lançamento: não informado"
        }

        return try {
            val somenteData = data.substringBefore("T")
            val partes = somenteData.split("-")

            if (partes.size == 3) {
                val ano = partes[0]
                val mes = partes[1]
                val dia = partes[2]

                "Lançamento: $dia/$mes/$ano"
            } else {
                "Lançamento: $somenteData"
            }

        } catch (e: Exception) {
            "Lançamento: não informado"
        }
    }

    // CONVERTER DP
    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }
}
