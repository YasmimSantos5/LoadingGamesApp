
package com.example.loadinggames

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.CarrinhoResponse
import com.example.loadinggames.api.ItemCarrinhoResponse
import com.example.loadinggames.api.RemoverCarrinhoResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CarrinhoActivity : AppCompatActivity() {

    private lateinit var containerItensCarrinho: LinearLayout
    private lateinit var txtQuantidadeItens: TextView
    private lateinit var txtSubtotal: TextView
    private lateinit var txtDesconto: TextView
    private lateinit var txtTotalCarrinho: TextView
    private lateinit var btnContinuarComprando: Button
    private lateinit var btnFinalizarCompra: Button
    private lateinit var btnVoltar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho)

        containerItensCarrinho = findViewById(R.id.containerItensCarrinho)
        txtQuantidadeItens = findViewById(R.id.txtQuantidadeItens)
        txtSubtotal = findViewById(R.id.txtSubtotal)
        txtDesconto = findViewById(R.id.txtDesconto)
        txtTotalCarrinho = findViewById(R.id.txtTotalCarrinho)
        btnContinuarComprando = findViewById(R.id.btnContinuarComprando)
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra)
        btnVoltar = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            finish()
        }

        btnContinuarComprando.setOnClickListener {
            finish()
        }

        // Abre o pagamento, sem finalizar a compra diretamente.
        btnFinalizarCompra.setOnClickListener {
            val intent = Intent(
                this,
                PagamentoActivity::class.java
            )
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarCarrinho()
    }

    private fun carregarCarrinho() {

        val idUsuario = obterIdUsuario()

        if (idUsuario == -1) {
            btnFinalizarCompra.isEnabled = false
            mostrarMensagem(
                "Usuário não identificado. Faça login novamente."
            )
            return
        }

        // Impede finalizar antes de carregar os itens.
        btnFinalizarCompra.isEnabled = false

        RetrofitClient.api.buscarCarrinho(idUsuario)
            .enqueue(object : Callback<CarrinhoResponse> {

                override fun onResponse(
                    call: Call<CarrinhoResponse>,
                    response: Response<CarrinhoResponse>
                ) {
                    if (response.isSuccessful) {
                        val carrinho = response.body()

                        if (carrinho != null) {
                            exibirCarrinho(carrinho)
                        } else {
                            mostrarMensagem(
                                "O servidor retornou um carrinho vazio."
                            )
                        }
                    } else {
                        mostrarMensagem(
                            "Erro ao carregar o carrinho: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<CarrinhoResponse>,
                    t: Throwable
                ) {
                    mostrarMensagem(
                        "Erro ao conectar com o servidor: ${t.message}"
                    )
                }
            })
    }

    private fun exibirCarrinho(carrinho: CarrinhoResponse) {

        containerItensCarrinho.removeAllViews()

        val quantidade = carrinho.itens.size

        txtQuantidadeItens.text = if (quantidade == 1) {
            "1 item"
        } else {
            "$quantidade itens"
        }

        txtSubtotal.text = formatarPreco(carrinho.total)
        txtDesconto.text = formatarPreco(0.0)
        txtTotalCarrinho.text = formatarPreco(carrinho.total)

        if (carrinho.itens.isEmpty()) {

            val mensagem = TextView(this).apply {
                text = "Seu carrinho está vazio."
                setTextColor(Color.parseColor("#A7A9B5"))
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(
                    dp(20),
                    dp(60),
                    dp(20),
                    dp(60)
                )
            }

            containerItensCarrinho.addView(mensagem)
            btnFinalizarCompra.isEnabled = false
            return
        }

        btnFinalizarCompra.isEnabled = true

        carrinho.itens.forEach { item ->
            containerItensCarrinho.addView(
                criarCardItem(item)
            )
        }
    }

    private fun criarCardItem(
        item: ItemCarrinhoResponse
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                dp(10),
                dp(10),
                dp(10),
                dp(10)
            )
            setBackgroundColor(
                Color.parseColor("#111421")
            )
        }

        val parametrosCard = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(120)
        ).apply {
            setMargins(0, 0, 0, dp(12))
        }

        card.layoutParams = parametrosCard

        // CAPA DO JOGO
        val imagem = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dp(75),
                dp(100)
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = item.nome
            setImageResource(descobrirImagem(item.nome))
        }

        card.addView(imagem)

        // INFORMAÇÕES
        val informacoes = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), 0, dp(8), 0)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        val nome = TextView(this).apply {
            text = item.nome
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        }

        informacoes.addView(nome)

        val classificacao = TextView(this).apply {
            text = if (item.classificacaoIndicativa == 0) {
                "Classificação: Livre"
            } else {
                "Classificação: ${item.classificacaoIndicativa} anos"
            }

            setTextColor(Color.parseColor("#888DA0"))
            textSize = 11f
            setPadding(0, dp(5), 0, 0)
        }

        informacoes.addView(classificacao)

        val preco = TextView(this).apply {
            text = formatarPreco(item.preco)
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(8), 0, 0)
        }

        informacoes.addView(preco)
        card.addView(informacoes)

        // REMOVER ITEM
        val remover = TextView(this).apply {
            text = "🗑"
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                dp(45),
                dp(50)
            )
            contentDescription = "Remover ${item.nome}"
        }

        remover.setOnClickListener {
            remover.isEnabled = false
            removerItemCarrinho(item)
        }

        card.addView(remover)

        return card
    }

    private fun removerItemCarrinho(
        item: ItemCarrinhoResponse
    ) {

        val idUsuario = obterIdUsuario()

        if (idUsuario == -1) {
            mostrarMensagem("Usuário não identificado.")
            return
        }

        RetrofitClient.api.removerItemCarrinho(
            item.idItemCarrinho,
            idUsuario
        ).enqueue(object : Callback<RemoverCarrinhoResponse> {

            override fun onResponse(
                call: Call<RemoverCarrinhoResponse>,
                response: Response<RemoverCarrinhoResponse>
            ) {
                if (response.isSuccessful) {
                    mostrarMensagem(
                        "Jogo removido do carrinho!"
                    )
                    carregarCarrinho()
                } else {
                    mostrarMensagem(
                        "Erro ao remover. Código: ${response.code()}"
                    )
                    carregarCarrinho()
                }
            }

            override fun onFailure(
                call: Call<RemoverCarrinhoResponse>,
                t: Throwable
            ) {
                mostrarMensagem(
                    "Erro de conexão: ${t.message}"
                )
                carregarCarrinho()
            }
        })
    }

    private fun obterIdUsuario(): Int {
        return getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        ).getInt("idUsuario", -1)
    }

    private fun descobrirImagem(nomeJogo: String): Int {
        return when {
            nomeJogo.contains("Grand Theft Auto", true) ||
                    nomeJogo.contains("GTA", true) ->
                R.drawable.capagtav

            nomeJogo.contains("Cyberpunk", true) ->
                R.drawable.capacyberpunk

            nomeJogo.contains("Red Dead", true) ->
                R.drawable.capareddead

            nomeJogo.contains("Witcher", true) ->
                R.drawable.capathewitcher

            nomeJogo.contains("Minecraft", true) ->
                R.drawable.capaminecraft

            nomeJogo.contains("Elden Ring", true) ->
                R.drawable.capaeldenring

            nomeJogo.contains("God of War", true) ->
                R.drawable.god_of_war

            nomeJogo.contains("FC 24", true) ->
                R.drawable.fc24

            nomeJogo.contains("Hogwarts", true) ->
                R.drawable.hogwarts

            nomeJogo.contains("Resident Evil", true) ->
                R.drawable.resident_evil

            else ->
                android.R.drawable.ic_menu_gallery
        }
    }

    private fun formatarPreco(preco: Double): String {
        return NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        ).format(preco)
    }

    private fun mostrarMensagem(mensagem: String) {
        Toast.makeText(
            this,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }
}
