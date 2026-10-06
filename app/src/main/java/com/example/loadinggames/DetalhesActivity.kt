package com.example.loadinggames

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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


        // ============================================
        // RECEBER DADOS DO JOGO
        // ============================================

        val idJogo =
            intent.getIntExtra(
                "idJogo",
                -1
            )

        val nome =
            intent.getStringExtra("nome")
                ?: "Jogo"

        val imagem =
            intent.getIntExtra(
                "imagem",
                0
            )

        val preco =
            intent.getStringExtra("preco")
                ?: "R$ 0,00"

        val descricao =
            intent.getStringExtra("descricao")
                ?: "Sem descrição disponível."

        val classificacao =
            intent.getIntExtra(
                "classificacao",
                0
            )

        val dataLancamento =
            intent.getStringExtra(
                "dataLancamento"
            )


        // ============================================
        // PREENCHER A TELA
        // ============================================

        txtNomeJogo.text = nome
        txtPreco.text = preco
        txtDescricao.text = descricao

        txtPlataforma.text =
            "Disponível no catálogo"

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


        // ============================================
        // BOTÃO ADICIONAR AO CARRINHO
        // ============================================

        btnAdicionarCarrinho.setOnClickListener {

            adicionarAoCarrinho(
                idJogo = idJogo,
                nomeJogo = nome,
                botao = btnAdicionarCarrinho
            )
        }


        // ============================================
        // FAVORITOS
        // ============================================

        btnFavorito.setOnClickListener {

            Toast.makeText(
                this,
                "$nome adicionado aos favoritos!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // ================================================
    // ADICIONAR AO CARRINHO
    // ================================================

    private fun adicionarAoCarrinho(
        idJogo: Int,
        nomeJogo: String,
        botao: Button
    ) {

        // Recupera o usuário que fez login
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


        // Verifica se temos um usuário válido
        if (idUsuario == -1) {

            Toast.makeText(
                this,
                "Usuário não identificado. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        // Verifica se recebemos o ID do jogo
        if (idJogo == -1) {

            Toast.makeText(
                this,
                "Não foi possível identificar o jogo.",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        // Evita vários cliques enquanto a API responde
        botao.isEnabled = false
        botao.text = "ADICIONANDO..."


        val request =
            AdicionarCarrinhoRequest(
                idUsuario = idUsuario,
                idJogo = idJogo
            )


        RetrofitClient.api
            .adicionarAoCarrinho(request)
            .enqueue(

                object :
                    Callback<AdicionarCarrinhoResponse> {

                    override fun onResponse(
                        call: Call<AdicionarCarrinhoResponse>,
                        response: Response<AdicionarCarrinhoResponse>
                    ) {

                        botao.isEnabled = true
                        botao.text =
                            "ADICIONAR AO CARRINHO"


                        // SUCESSO
                        if (response.isSuccessful) {

                            val resposta =
                                response.body()

                            Toast.makeText(
                                this@DetalhesActivity,
                                resposta?.mensagem
                                    ?: "$nomeJogo adicionado ao carrinho!",
                                Toast.LENGTH_SHORT
                            ).show()

                            return
                        }


                        // JOGO DUPLICADO
                        if (response.code() == 409) {

                            Toast.makeText(
                                this@DetalhesActivity,
                                "Este jogo já está no carrinho.",
                                Toast.LENGTH_SHORT
                            ).show()

                            return
                        }


                        // OUTRO ERRO DA API
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

                        botao.isEnabled = true
                        botao.text =
                            "ADICIONAR AO CARRINHO"

                        Toast.makeText(
                            this@DetalhesActivity,
                            "Erro ao conectar com o servidor: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ================================================
    // FORMATAR DATA
    // ================================================

    private fun formatarData(
        data: String?
    ): String {

        if (data.isNullOrBlank()) {
            return "Lançamento: não informado"
        }

        return try {

            val somenteData =
                data.substringBefore("T")

            val partes =
                somenteData.split("-")

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
}