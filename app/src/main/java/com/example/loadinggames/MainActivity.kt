package com.example.loadinggames

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.JogoResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var containerJogos: LinearLayout
    private lateinit var edtPesquisa: EditText
    private lateinit var txtTituloJogos: TextView

    private lateinit var btnCarrinhoTopo: TextView
    private lateinit var btnCarrinhoMenu: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // =====================================================
        // COMPONENTES DA TELA
        // =====================================================

        containerJogos =
            findViewById(R.id.containerJogos)

        edtPesquisa =
            findViewById(R.id.edtPesquisa)

        txtTituloJogos =
            findViewById(R.id.txtTituloJogos)

        btnCarrinhoTopo =
            findViewById(R.id.btnCarrinhoTopo)

        btnCarrinhoMenu =
            findViewById(R.id.btnCarrinhoMenu)


        // =====================================================
        // CARRINHO DO TOPO
        // =====================================================

        btnCarrinhoTopo.setOnClickListener {
            abrirCarrinho()
        }


        // =====================================================
        // CARRINHO DO MENU INFERIOR
        // =====================================================

        btnCarrinhoMenu.setOnClickListener {
            abrirCarrinho()
        }


        // =====================================================
        // CARREGAR JOGOS
        // =====================================================

        buscarJogosDaApi()


        // =====================================================
        // PESQUISA
        // =====================================================

        edtPesquisa.addTextChangedListener(

            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val pesquisa =
                        s?.toString()?.trim()

                    if (pesquisa.isNullOrEmpty()) {

                        txtTituloJogos.text =
                            "Destaques"

                        buscarJogosDaApi()

                    } else {

                        txtTituloJogos.text =
                            "Resultados"

                        buscarJogosDaApi(
                            pesquisa
                        )
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }


    // =====================================================
    // ABRIR CARRINHO
    // =====================================================

    private fun abrirCarrinho() {

        val intent =
            Intent(
                this,
                CarrinhoActivity::class.java
            )

        startActivity(intent)
    }


    // =====================================================
    // BUSCAR JOGOS NA API
    // =====================================================

    private fun buscarJogosDaApi(
        pesquisa: String? = null
    ) {

        RetrofitClient.api
            .buscarJogos(pesquisa)
            .enqueue(

                object : Callback<List<JogoResponse>> {

                    override fun onResponse(
                        call: Call<List<JogoResponse>>,
                        response: Response<List<JogoResponse>>
                    ) {

                        if (response.isSuccessful) {

                            val jogos =
                                response.body()
                                    ?: emptyList()

                            Log.d(
                                "JOGOS_API",
                                "Quantidade: ${jogos.size}"
                            )

                            exibirJogos(jogos)

                        } else {

                            Log.e(
                                "JOGOS_API",
                                "Erro HTTP: ${response.code()}"
                            )

                            Toast.makeText(
                                this@MainActivity,
                                "Erro ao carregar os jogos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<List<JogoResponse>>,
                        t: Throwable
                    ) {

                        Log.e(
                            "JOGOS_API",
                            "Erro ao conectar com a API",
                            t
                        )

                        Toast.makeText(
                            this@MainActivity,
                            "Erro ao conectar com o servidor",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // =====================================================
    // EXIBIR JOGOS
    // =====================================================

    private fun exibirJogos(
        jogos: List<JogoResponse>
    ) {

        containerJogos.removeAllViews()

        if (jogos.isEmpty()) {

            val mensagem =
                TextView(this)

            mensagem.text =
                "Nenhum jogo encontrado"

            mensagem.setTextColor(
                Color.WHITE
            )

            mensagem.textSize =
                16f

            mensagem.setPadding(
                dp(10),
                dp(30),
                dp(30),
                dp(30)
            )

            containerJogos.addView(
                mensagem
            )

            return
        }


        jogos.forEach { jogo ->

            val card =
                criarCardJogo(jogo)

            containerJogos.addView(
                card
            )
        }
    }


    // =====================================================
    // CRIAR CARD DO JOGO
    // =====================================================

    private fun criarCardJogo(
        jogo: JogoResponse
    ): LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setBackgroundColor(
            Color.parseColor("#111421")
        )

        card.isClickable =
            true

        card.isFocusable =
            true


        val parametrosCard =
            LinearLayout.LayoutParams(
                dp(175),
                dp(235)
            )

        parametrosCard.setMargins(
            0,
            0,
            dp(12),
            0
        )

        card.layoutParams =
            parametrosCard


        // =================================================
        // IMAGEM
        // =================================================

        val imagem =
            ImageView(this)

        imagem.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(105)
            )

        imagem.scaleType =
            ImageView.ScaleType.CENTER_CROP

        imagem.contentDescription =
            jogo.nome

        imagem.setImageResource(
            descobrirImagem(jogo.nome)
        )

        card.addView(
            imagem
        )


        // =================================================
        // NOME
        // =================================================

        val nome =
            TextView(this)

        nome.text =
            jogo.nome

        nome.setTextColor(
            Color.WHITE
        )

        nome.textSize =
            14f

        nome.setTypeface(
            nome.typeface,
            android.graphics.Typeface.BOLD
        )

        nome.setPadding(
            dp(10),
            dp(9),
            dp(10),
            0
        )

        card.addView(
            nome
        )


        // =================================================
        // CLASSIFICAÇÃO
        // =================================================

        val classificacao =
            TextView(this)

        classificacao.text =
            if (
                jogo.classificacaoIndicativa == 0
            ) {

                "Classificação: Livre"

            } else {

                "Classificação: " +
                        "${jogo.classificacaoIndicativa} anos"
            }

        classificacao.setTextColor(
            Color.parseColor("#A7A9B5")
        )

        classificacao.textSize =
            11f

        classificacao.setPadding(
            dp(10),
            dp(5),
            dp(10),
            0
        )

        card.addView(
            classificacao
        )


        // =================================================
        // PREÇO
        // =================================================

        val preco =
            TextView(this)

        preco.text =
            formatarPreco(
                jogo.preco
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

        preco.gravity =
            Gravity.START

        preco.setPadding(
            dp(10),
            dp(8),
            dp(10),
            0
        )

        card.addView(
            preco
        )


        // =================================================
        // CLIQUE NO JOGO
        // =================================================

        card.setOnClickListener {

            abrirDetalhes(
                idJogo = jogo.id,
                nome = jogo.nome,
                imagem = descobrirImagem(jogo.nome),
                preco = formatarPreco(jogo.preco),
                descricao = jogo.descricao
                    ?: "Sem descrição disponível.",
                classificacao = jogo.classificacaoIndicativa,
                dataLancamento = jogo.dataLancamento
            )
        }


        return card
    }


    // =====================================================
    // DESCOBRIR CAPA
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

        val formato =
            NumberFormat.getCurrencyInstance(
                Locale("pt", "BR")
            )

        return formato.format(
            preco
        )
    }


    // =====================================================
    // CONVERTER DP
    // =====================================================

    private fun dp(
        valor: Int
    ): Int {

        return (
                valor *
                        resources.displayMetrics.density
                ).toInt()
    }


    // =====================================================
    // ABRIR DETALHES
    // =====================================================

    private fun abrirDetalhes(
        idJogo: Int,
        nome: String,
        imagem: Int,
        preco: String,
        descricao: String,
        classificacao: Int,
        dataLancamento: String?
    ) {

        val intent =
            Intent(
                this,
                DetalhesActivity::class.java
            )

        intent.putExtra(
            "idJogo",
            idJogo
        )

        intent.putExtra(
            "nome",
            nome
        )

        intent.putExtra(
            "imagem",
            imagem
        )

        intent.putExtra(
            "preco",
            preco
        )

        intent.putExtra(
            "descricao",
            descricao
        )

        intent.putExtra(
            "classificacao",
            classificacao
        )

        intent.putExtra(
            "dataLancamento",
            dataLancamento
        )

        startActivity(
            intent
        )
    }
}