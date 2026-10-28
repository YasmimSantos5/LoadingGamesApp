
package com.example.loadinggames

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.BibliotecaResponse
import com.example.loadinggames.api.JogoBibliotecaResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BibliotecaActivity : AppCompatActivity() {

    private lateinit var txtTotal: TextView
    private lateinit var txtVazia: TextView
    private lateinit var progresso: ProgressBar
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biblioteca)

        txtTotal = findViewById(R.id.txtTotalJogosBiblioteca)
        txtVazia = findViewById(R.id.txtBibliotecaVazia)
        progresso = findViewById(R.id.progressoBiblioteca)
        container = findViewById(R.id.containerJogosBiblioteca)

        configurarNavegacao()
    }

    // ==========================================
    // NAVEGAÇÃO
    // ==========================================

    private fun configurarNavegacao() {

        findViewById<View>(R.id.btnVoltarBiblioteca)
            .setOnClickListener {
                finish()
            }

        findViewById<View>(R.id.btnInicioBiblioteca)
            .setOnClickListener {
                abrirMain()
            }

        findViewById<View>(R.id.btnBuscarBiblioteca)
            .setOnClickListener {
                abrirMain(abrirPesquisa = true)
            }

        findViewById<View>(R.id.btnCategoriasBiblioteca)
            .setOnClickListener {
                startActivity(
                    Intent(this, CategoriasActivity::class.java)
                )
            }

        findViewById<View>(R.id.btnFavoritosBiblioteca)
            .setOnClickListener {
                abrirMain()
                Toast.makeText(
                    this,
                    "Favoritos ainda não está disponível.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        findViewById<View>(R.id.btnCarrinhoBiblioteca)
            .setOnClickListener {
                startActivity(
                    Intent(this, CarrinhoActivity::class.java)
                )
            }
    }

    private fun abrirMain(abrirPesquisa: Boolean = false) {

        val intent = Intent(this, MainActivity::class.java).apply {

            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            if (abrirPesquisa) {
                putExtra("abrirPesquisa", true)
            }
        }

        startActivity(intent)
        finish()
    }

    // ==========================================
    // CARREGAR BIBLIOTECA
    // ==========================================

    override fun onResume() {
        super.onResume()
        carregarBiblioteca()
    }

    private fun carregarBiblioteca() {

        val prefs = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        val idUsuario = prefs.getInt("idUsuario", -1)

        if (idUsuario <= 0) {

            Toast.makeText(
                this,
                "Faça login para acessar sua biblioteca.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        progresso.visibility = View.VISIBLE
        txtVazia.visibility = View.GONE
        txtTotal.text = "Carregando seus jogos..."
        container.removeAllViews()

        RetrofitClient.api
            .buscarBiblioteca(idUsuario)
            .enqueue(object : Callback<BibliotecaResponse> {

                override fun onResponse(
                    call: Call<BibliotecaResponse>,
                    response: Response<BibliotecaResponse>
                ) {

                    if (isFinishing || isDestroyed) return

                    progresso.visibility = View.GONE

                    val resultado = response.body()

                    if (!response.isSuccessful || resultado == null) {
                        txtTotal.text =
                            "Não foi possível carregar a biblioteca."
                        return
                    }

                    txtTotal.text =
                        "${resultado.totalJogos} jogo(s) na sua biblioteca"

                    if (resultado.jogos.isEmpty()) {
                        txtVazia.visibility = View.VISIBLE
                        return
                    }

                    resultado.jogos.forEach { jogo ->
                        adicionarCardJogo(jogo)
                    }
                }

                override fun onFailure(
                    call: Call<BibliotecaResponse>,
                    t: Throwable
                ) {

                    if (isFinishing || isDestroyed) return

                    progresso.visibility = View.GONE
                    txtTotal.text =
                        "Erro ao conectar com o servidor."
                }
            })
    }

    // ==========================================
    // CRIAR CARD DO JOGO COMPRADO
    // ==========================================

    private fun adicionarCardJogo(
        jogo: JogoBibliotecaResponse
    ) {

        val card = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
            )

            background = GradientDrawable().apply {
                setColor(Color.parseColor("#151725"))
                cornerRadius = dp(14).toFloat()

                setStroke(
                    dp(1),
                    Color.parseColor("#29243D")
                )
            }
        }

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
        }

        // ======================================
        // CAPA DO JOGO
        // ======================================

        val imagem = ImageView(this).apply {

            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = "Capa de ${jogo.nome}"

            background = GradientDrawable().apply {
                setColor(Color.parseColor("#252336"))
                cornerRadius = dp(8).toFloat()
            }

            clipToOutline = true

            // Mesma imagem utilizada no catálogo
            setImageResource(
                descobrirImagem(jogo.nome)
            )
        }

        val imagemParams = LinearLayout.LayoutParams(
            dp(94),
            dp(118)
        ).apply {
            marginEnd = dp(14)
        }

        card.addView(imagem, imagemParams)

        // ======================================
        // INFORMAÇÕES DO JOGO
        // ======================================

        val informacoes = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val informacoesParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        val nome = TextView(this).apply {

            text = jogo.nome
            textSize = 16f

            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            maxLines = 2
        }

        informacoes.addView(nome)

        // STATUS COMPRADO

        val status = TextView(this).apply {

            text = "✓ COMPRADO"
            textSize = 11f

            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#C58BFF"))

            setPadding(
                0,
                dp(10),
                0,
                0
            )
        }

        informacoes.addView(status)

        // DATA DE AQUISIÇÃO

        val data = TextView(this).apply {

            text = "Adquirido em: ${jogo.dataAquisicao}"
            textSize = 11f

            setTextColor(Color.parseColor("#9696AA"))

            setPadding(
                0,
                dp(8),
                0,
                0
            )
        }

        informacoes.addView(data)

        card.addView(informacoes, informacoesParams)
        container.addView(card, cardParams)
    }

    // ==========================================
    // IMAGENS - MESMAS DO CATÁLOGO
    // ==========================================

    private fun descobrirImagem(nomeJogo: String): Int {

        return when {

            nomeJogo.contains(
                "Grand Theft Auto",
                ignoreCase = true
            ) || nomeJogo.contains(
                "GTA",
                ignoreCase = true
            ) -> R.drawable.capagtav

            nomeJogo.contains(
                "Cyberpunk",
                ignoreCase = true
            ) -> R.drawable.capacyberpunk

            nomeJogo.contains(
                "Red Dead",
                ignoreCase = true
            ) -> R.drawable.capareddead

            nomeJogo.contains(
                "Witcher",
                ignoreCase = true
            ) -> R.drawable.capathewitcher

            nomeJogo.contains(
                "Minecraft",
                ignoreCase = true
            ) -> R.drawable.capaminecraft

            nomeJogo.contains(
                "Elden Ring",
                ignoreCase = true
            ) -> R.drawable.capaeldenring

            nomeJogo.contains(
                "God of War",
                ignoreCase = true
            ) -> R.drawable.god_of_war

            nomeJogo.contains(
                "FC 24",
                ignoreCase = true
            ) -> R.drawable.fc24

            nomeJogo.contains(
                "Hogwarts",
                ignoreCase = true
            ) -> R.drawable.hogwarts

            nomeJogo.contains(
                "Resident Evil",
                ignoreCase = true
            ) -> R.drawable.resident_evil

            else -> android.R.drawable.ic_menu_gallery
        }
    }

    // ==========================================
    // CONVERTER DP
    // ==========================================

    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }
}
