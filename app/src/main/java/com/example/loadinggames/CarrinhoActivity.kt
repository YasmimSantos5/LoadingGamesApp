package com.example.loadinggames

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.CarrinhoResponse
import com.example.loadinggames.api.FinalizarCompraRequest
import com.example.loadinggames.api.FinalizarCompraResponse
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

        containerItensCarrinho =
            findViewById(R.id.containerItensCarrinho)

        txtQuantidadeItens =
            findViewById(R.id.txtQuantidadeItens)

        txtSubtotal =
            findViewById(R.id.txtSubtotal)

        txtDesconto =
            findViewById(R.id.txtDesconto)

        txtTotalCarrinho =
            findViewById(R.id.txtTotalCarrinho)

        btnContinuarComprando =
            findViewById(R.id.btnContinuarComprando)

        btnFinalizarCompra =
            findViewById(R.id.btnFinalizarCompra)

        btnVoltar =
            findViewById(R.id.btnVoltar)

        // VOLTAR
        btnVoltar.setOnClickListener {
            finish()
        }

        // CONTINUAR COMPRANDO
        btnContinuarComprando.setOnClickListener {
            finish()
        }

        // FINALIZAR COMPRA
        btnFinalizarCompra.setOnClickListener {
            finalizarCompra()
        }

        // CARREGAR CARRINHO
        carregarCarrinho()
    }

    // =====================================================
    // CARREGAR CARRINHO
    // =====================================================

    private fun carregarCarrinho() {

        val preferencias =
            getSharedPreferences(
                "LoadingGamesPrefs",
                MODE_PRIVATE
            )

        val idUsuario =
            preferencias.getInt(
                "idUsuario",
                -1
            )

        if (idUsuario == -1) {

            Toast.makeText(
                this,
                "Usuário não identificado. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        RetrofitClient.api
            .buscarCarrinho(idUsuario)
            .enqueue(
                object : Callback<CarrinhoResponse> {

                    override fun onResponse(
                        call: Call<CarrinhoResponse>,
                        response: Response<CarrinhoResponse>
                    ) {

                        if (response.isSuccessful) {

                            val carrinho =
                                response.body()

                            if (carrinho != null) {

                                exibirCarrinho(
                                    carrinho
                                )
                            }

                        } else {

                            Toast.makeText(
                                this@CarrinhoActivity,
                                "Erro ao carregar o carrinho: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<CarrinhoResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@CarrinhoActivity,
                            "Erro ao conectar com o servidor: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    // =====================================================
    // EXIBIR CARRINHO
    // =====================================================

    private fun exibirCarrinho(
        carrinho: CarrinhoResponse
    ) {

        containerItensCarrinho.removeAllViews()

        val quantidade =
            carrinho.itens.size

        txtQuantidadeItens.text =
            if (quantidade == 1) {
                "1 item"
            } else {
                "$quantidade itens"
            }

        txtSubtotal.text =
            formatarPreco(
                carrinho.total
            )

        txtDesconto.text =
            formatarPreco(
                0.0
            )

        txtTotalCarrinho.text =
            formatarPreco(
                carrinho.total
            )

        // CARRINHO VAZIO
        if (carrinho.itens.isEmpty()) {

            val mensagem =
                TextView(this)

            mensagem.text =
                "Seu carrinho está vazio."

            mensagem.setTextColor(
                Color.parseColor("#A7A9B5")
            )

            mensagem.textSize =
                16f

            mensagem.gravity =
                Gravity.CENTER

            mensagem.setPadding(
                dp(20),
                dp(60),
                dp(20),
                dp(60)
            )

            containerItensCarrinho.addView(
                mensagem
            )

            btnFinalizarCompra.isEnabled =
                false

            return
        }

        btnFinalizarCompra.isEnabled =
            true

        // CRIAR CARDS
        carrinho.itens.forEach { item ->

            val card =
                criarCardItem(
                    item
                )

            containerItensCarrinho.addView(
                card
            )
        }
    }

    // =====================================================
    // CRIAR CARD
    // =====================================================

    private fun criarCardItem(
        item: ItemCarrinhoResponse
    ): LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.HORIZONTAL

        card.gravity =
            Gravity.CENTER_VERTICAL

        card.setPadding(
            dp(10),
            dp(10),
            dp(10),
            dp(10)
        )

        card.setBackgroundColor(
            Color.parseColor("#111421")
        )

        val parametrosCard =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(120)
            )

        parametrosCard.setMargins(
            0,
            0,
            0,
            dp(12)
        )

        card.layoutParams =
            parametrosCard

        // CAPA
        val imagem =
            ImageView(this)

        imagem.layoutParams =
            LinearLayout.LayoutParams(
                dp(75),
                dp(100)
            )

        imagem.scaleType =
            ImageView.ScaleType.CENTER_CROP

        imagem.contentDescription =
            item.nome

        imagem.setImageResource(
            descobrirImagem(
                item.nome
            )
        )

        card.addView(
            imagem
        )

        // INFORMAÇÕES
        val informacoes =
            LinearLayout(this)

        informacoes.orientation =
            LinearLayout.VERTICAL

        informacoes.gravity =
            Gravity.CENTER_VERTICAL

        informacoes.setPadding(
            dp(12),
            0,
            dp(8),
            0
        )

        informacoes.layoutParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )

        // NOME
        val nome =
            TextView(this)

        nome.text =
            item.nome

        nome.setTextColor(
            Color.WHITE
        )

        nome.textSize =
            14f

        nome.setTypeface(
            nome.typeface,
            android.graphics.Typeface.BOLD
        )

        informacoes.addView(
            nome
        )

        // CLASSIFICAÇÃO
        val classificacao =
            TextView(this)

        classificacao.text =
            if (item.classificacaoIndicativa == 0) {

                "Classificação: Livre"

            } else {

                "Classificação: ${item.classificacaoIndicativa} anos"
            }

        classificacao.setTextColor(
            Color.parseColor("#888DA0")
        )

        classificacao.textSize =
            11f

        classificacao.setPadding(
            0,
            dp(5),
            0,
            0
        )

        informacoes.addView(
            classificacao
        )

        // PREÇO
        val preco =
            TextView(this)

        preco.text =
            formatarPreco(
                item.preco
            )

        preco.setTextColor(
            Color.WHITE
        )

        preco.textSize =
            15f

        preco.setTypeface(
            preco.typeface,
            android.graphics.Typeface.BOLD
        )

        preco.setPadding(
            0,
            dp(8),
            0,
            0
        )

        informacoes.addView(
            preco
        )

        card.addView(
            informacoes
        )

        // BOTÃO REMOVER
        val remover =
            TextView(this)

        remover.text =
            "🗑"

        remover.textSize =
            20f

        remover.gravity =
            Gravity.CENTER

        remover.setTextColor(
            Color.WHITE
        )

        remover.layoutParams =
            LinearLayout.LayoutParams(
                dp(45),
                dp(50)
            )

        remover.setOnClickListener {

            removerItemCarrinho(
                item
            )
        }

        card.addView(
            remover
        )

        return card
    }

    // =====================================================
    // REMOVER ITEM DO CARRINHO
    // =====================================================

    private fun removerItemCarrinho(
        item: ItemCarrinhoResponse
    ) {

        val preferencias =
            getSharedPreferences(
                "LoadingGamesPrefs",
                MODE_PRIVATE
            )

        val idUsuario =
            preferencias.getInt(
                "idUsuario",
                -1
            )

        if (idUsuario == -1) {

            Toast.makeText(
                this,
                "Usuário não identificado.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        RetrofitClient.api
            .removerItemCarrinho(
                item.idItemCarrinho,
                idUsuario
            )
            .enqueue(
                object : Callback<RemoverCarrinhoResponse> {

                    override fun onResponse(
                        call: Call<RemoverCarrinhoResponse>,
                        response: Response<RemoverCarrinhoResponse>
                    ) {

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@CarrinhoActivity,
                                "Jogo removido do carrinho!",
                                Toast.LENGTH_SHORT
                            ).show()

                            carregarCarrinho()

                        } else {

                            Toast.makeText(
                                this@CarrinhoActivity,
                                "Erro ao remover. Código: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<RemoverCarrinhoResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@CarrinhoActivity,
                            "Erro de conexão: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    // =====================================================
    // FINALIZAR COMPRA
    // =====================================================

    private fun finalizarCompra() {

        val preferencias =
            getSharedPreferences(
                "LoadingGamesPrefs",
                MODE_PRIVATE
            )

        val idUsuario =
            preferencias.getInt(
                "idUsuario",
                -1
            )

        if (idUsuario == -1) {

            Toast.makeText(
                this,
                "Usuário não identificado. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // Impede clique duplo enquanto processa
        btnFinalizarCompra.isEnabled = false

        val request =
            FinalizarCompraRequest(
                idUsuario = idUsuario,
                formaPagamento = "Pix"
            )

        RetrofitClient.api
            .finalizarCompra(request)
            .enqueue(
                object : Callback<FinalizarCompraResponse> {

                    override fun onResponse(
                        call: Call<FinalizarCompraResponse>,
                        response: Response<FinalizarCompraResponse>
                    ) {

                        if (response.isSuccessful) {

                            val compra =
                                response.body()

                            if (compra != null) {

                                Toast.makeText(
                                    this@CarrinhoActivity,
                                    "Compra realizada com sucesso! Pedido #${compra.idCompra}",
                                    Toast.LENGTH_LONG
                                ).show()

                                // A API já limpou os itens do carrinho.
                                // Agora atualizamos a tela.
                                carregarCarrinho()

                            } else {

                                btnFinalizarCompra.isEnabled = true

                                Toast.makeText(
                                    this@CarrinhoActivity,
                                    "A compra foi processada, mas a resposta do servidor veio vazia.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {

                            btnFinalizarCompra.isEnabled = true

                            Toast.makeText(
                                this@CarrinhoActivity,
                                "Não foi possível finalizar a compra. Código: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<FinalizarCompraResponse>,
                        t: Throwable
                    ) {

                        btnFinalizarCompra.isEnabled = true

                        Toast.makeText(
                            this@CarrinhoActivity,
                            "Erro de conexão ao finalizar compra: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    // =====================================================
    // CAPAS
    // =====================================================

    private fun descobrirImagem(
        nomeJogo: String
    ): Int {

        return when {

            nomeJogo.contains(
                "Grand Theft Auto",
                ignoreCase = true
            ) -> R.drawable.capagtav

            nomeJogo.contains(
                "Cyberpunk",
                ignoreCase = true
            ) -> R.drawable.capacyberpunk

            nomeJogo.contains(
                "Red Dead Redemption",
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

            else ->
                android.R.drawable.ic_menu_gallery
        }
    }

    // =====================================================
    // FORMATAR PREÇO
    // =====================================================

    private fun formatarPreco(
        preco: Double
    ): String {

        return NumberFormat
            .getCurrencyInstance(
                Locale("pt", "BR")
            )
            .format(
                preco
            )
    }

    // =====================================================
    // DP
    // =====================================================

    private fun dp(
        valor: Int
    ): Int {

        return (
                valor *
                        resources.displayMetrics.density
                ).toInt()
    }
}