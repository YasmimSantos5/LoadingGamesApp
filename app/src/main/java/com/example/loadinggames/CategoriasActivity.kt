package com.example.loadinggames

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
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

    private fun iniciarComponentes() {
        btnMenu = findViewById(R.id.btnMenu)
        btnPesquisar = findViewById(R.id.btnPesquisar)
        btnCarrinho = findViewById(R.id.btnCarrinho)

        cardAcao = findViewById(R.id.cardAcao)
        cardAventura = findViewById(R.id.cardAventura)
        cardRpg = findViewById(R.id.cardRpg)
        cardEsportes = findViewById(R.id.cardEsportes)
        cardCorrida = findViewById(R.id.cardCorrida)
        cardEstrategia = findViewById(R.id.cardEstrategia)
    }

    private fun configurarCliques() {
        // Voltar para a MainActivity mostrando todos os jogos
        btnMenu.setOnClickListener {
            abrirCategoria("Todas as categorias")
        }

        btnPesquisar.setOnClickListener {
            abrirHomeComPesquisa()
        }

        btnCarrinho.setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        cardAcao.setOnClickListener { abrirCategoria("Ação") }
        cardAventura.setOnClickListener { abrirCategoria("Aventura") }
        cardRpg.setOnClickListener { abrirCategoria("RPG") }
        cardEsportes.setOnClickListener { abrirCategoria("Esportes") }
        cardCorrida.setOnClickListener { abrirCategoria("Corrida") }
        cardEstrategia.setOnClickListener { abrirCategoria("Estratégia") }
    }

    private fun abrirCategoria(categoria: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("categoriaSelecionada", categoria)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
        finish()
    }

    private fun abrirHomeComPesquisa() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("abrirPesquisa", true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
        finish()
    }
}