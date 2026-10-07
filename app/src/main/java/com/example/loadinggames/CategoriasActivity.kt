package com.example.loadinggames

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CategoriasActivity : AppCompatActivity() {

    private lateinit var btnMenu: ImageButton
    private lateinit var btnPesquisar: ImageButton
    private lateinit var btnCarrinho: ImageButton

    private lateinit var cardAcao: FrameLayout
    private lateinit var cardAventura: FrameLayout
    private lateinit var cardRpg: FrameLayout
    private lateinit var cardEsportes: FrameLayout
    private lateinit var cardCorrida: FrameLayout
    private lateinit var cardEstrategia: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_categorias)

        iniciarComponentes()
        configurarCliques()
    }

    // =====================================================
    // COMPONENTES
    // =====================================================

    private fun iniciarComponentes() {

        btnMenu =
            findViewById(R.id.btnMenu)

        btnPesquisar =
            findViewById(R.id.btnPesquisar)

        btnCarrinho =
            findViewById(R.id.btnCarrinho)

        cardAcao =
            findViewById(R.id.cardAcao)

        cardAventura =
            findViewById(R.id.cardAventura)

        cardRpg =
            findViewById(R.id.cardRpg)

        cardEsportes =
            findViewById(R.id.cardEsportes)

        cardCorrida =
            findViewById(R.id.cardCorrida)

        cardEstrategia =
            findViewById(R.id.cardEstrategia)
    }

    // =====================================================
    // CLIQUES
    // =====================================================

    private fun configurarCliques() {

        btnMenu.setOnClickListener {
            abrirMenu()
        }

        btnPesquisar.setOnClickListener {
            abrirHomeComPesquisa()
        }

        btnCarrinho.setOnClickListener {
            abrirCarrinho()
        }

        cardAcao.setOnClickListener {
            abrirCategoria("Ação")
        }

        cardAventura.setOnClickListener {
            abrirCategoria("Aventura")
        }

        cardRpg.setOnClickListener {
            abrirCategoria("RPG")
        }

        cardEsportes.setOnClickListener {
            abrirCategoria("Esportes")
        }

        cardCorrida.setOnClickListener {
            abrirCategoria("Corrida")
        }

        cardEstrategia.setOnClickListener {
            abrirCategoria("Estratégia")
        }
    }

    // =====================================================
    // CATEGORIA
    // =====================================================

    private fun abrirCategoria(
        categoria: String
    ) {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )

        intent.putExtra(
            "categoriaSelecionada",
            categoria
        )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )

        startActivity(intent)

        finish()
    }

    // =====================================================
    // PESQUISA
    // =====================================================

    private fun abrirHomeComPesquisa() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )

        intent.putExtra(
            "abrirPesquisa",
            true
        )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )

        startActivity(intent)

        finish()
    }

    // =====================================================
    // LOGIN
    // =====================================================

    private fun usuarioEstaLogado(): Boolean {

        val preferencias =
            getSharedPreferences(
                "LoadingGamesPrefs",
                MODE_PRIVATE
            )

        return preferencias.getInt(
            "idUsuario",
            -1
        ) != -1
    }

    // =====================================================
    // CARRINHO
    // =====================================================

    private fun abrirCarrinho() {

        if (!usuarioEstaLogado()) {

            mostrarLoginNecessario()

            return
        }

        startActivity(
            Intent(
                this,
                CarrinhoActivity::class.java
            )
        )
    }

    // =====================================================
    // LOGIN NECESSÁRIO
    // =====================================================

    private fun mostrarLoginNecessario() {

        AlertDialog.Builder(this)
            .setTitle("Entre para continuar")
            .setMessage(
                "Entre na sua conta para acessar o carrinho."
            )
            .setPositiveButton(
                "ENTRAR"
            ) { _, _ ->

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
            }
            .setNegativeButton(
                "CRIAR CONTA"
            ) { _, _ ->

                startActivity(
                    Intent(
                        this,
                        CadastroActivity::class.java
                    )
                )
            }
            .setNeutralButton(
                "AGORA NÃO",
                null
            )
            .show()
    }

    // =====================================================
    // MENU
    // =====================================================

    private fun abrirMenu() {

        val opcoes =
            if (usuarioEstaLogado()) {

                arrayOf(
                    "Início",
                    "Categorias",
                    "Minha biblioteca",
                    "Minha conta",
                    "Sair"
                )

            } else {

                arrayOf(
                    "Início",
                    "Categorias",
                    "Entrar",
                    "Criar conta"
                )
            }

        AlertDialog.Builder(this)
            .setTitle("Loading Games")
            .setItems(
                opcoes
            ) { _, posicao ->

                if (usuarioEstaLogado()) {

                    when (posicao) {

                        0 -> abrirInicio()

                        1 -> {
                            // Já estamos em Categorias
                        }

                        2 -> {
                            Toast.makeText(
                                this,
                                "Biblioteca será implementada depois.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        3 -> {
                            Toast.makeText(
                                this,
                                "Minha conta será implementada depois.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        4 -> sairDaConta()
                    }

                } else {

                    when (posicao) {

                        0 -> abrirInicio()

                        1 -> {
                            // Já estamos em Categorias
                        }

                        2 -> {

                            startActivity(
                                Intent(
                                    this,
                                    LoginActivity::class.java
                                )
                            )
                        }

                        3 -> {

                            startActivity(
                                Intent(
                                    this,
                                    CadastroActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
            .show()
    }

    // =====================================================
    // INÍCIO
    // =====================================================

    private fun abrirInicio() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )

        startActivity(intent)

        finish()
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private fun sairDaConta() {

        getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )
            .edit()
            .remove("idUsuario")
            .apply()

        Toast.makeText(
            this,
            "Você saiu da sua conta.",
            Toast.LENGTH_SHORT
        ).show()
    }
}