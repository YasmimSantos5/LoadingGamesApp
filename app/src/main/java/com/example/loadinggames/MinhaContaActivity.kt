
package com.example.loadinggames

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MinhaContaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minha_conta)

        val preferencias = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        if (preferencias.getInt("idUsuario", -1) == -1) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
            return
        }

        // ==========================================
        // BOTÃO VOLTAR
        // ==========================================

        findViewById<View>(R.id.btnVoltarConta)
            .setOnClickListener {
                finish()
            }

        // ==========================================
        // OPÇÕES DA CONTA
        // ==========================================

        findViewById<View>(R.id.btnMeusDados)
            .setOnClickListener {
                startActivity(
                    Intent(this, MeusDadosActivity::class.java)
                )
            }

        findViewById<View>(R.id.btnAlterarSenha)
            .setOnClickListener {
                startActivity(
                    Intent(this, AlterarSenhaActivity::class.java)
                )
            }

        findViewById<View>(R.id.btnConfiguracoesConta)
            .setOnClickListener {
                startActivity(
                    Intent(this, ConfiguracoesContaActivity::class.java)
                )
            }

        // ==========================================
        // MENU INFERIOR - PADRÃO DA MAIN
        // ==========================================

        // INÍCIO
        findViewById<View>(R.id.btnInicioConta)
            .setOnClickListener {
                abrirMain()
            }

        // BUSCAR
        findViewById<View>(R.id.btnBuscarConta)
            .setOnClickListener {
                abrirMain("buscar")
            }

        // CATEGORIAS
        findViewById<View>(R.id.btnCategoriasConta)
            .setOnClickListener {
                startActivity(
                    Intent(this, CategoriasActivity::class.java)
                )
            }

        // FAVORITOS
        findViewById<View>(R.id.btnFavoritosConta)
            .setOnClickListener {
                abrirMain("favoritos")
            }

        // CARRINHO
        findViewById<View>(R.id.btnCarrinhoConta)
            .setOnClickListener {
                startActivity(
                    Intent(this, CarrinhoActivity::class.java)
                )
            }
    }

    // ==========================================
    // ABRIR MAINACTIVITY
    // ==========================================

    private fun abrirMain(acao: String? = null) {

        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            if (acao != null) {
                putExtra("acao", acao)
            }
        }

        startActivity(intent)
        finish()
    }

    // ==========================================
    // ATUALIZAR DADOS DO USUÁRIO
    // ==========================================

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        if (prefs.getInt("idUsuario", -1) == -1) {
            finish()
            return
        }

        findViewById<TextView>(R.id.txtNomeConta).text =
            prefs.getString("nomeUsuario", "Jogador")

        findViewById<TextView>(R.id.txtEmailConta).text =
            prefs.getString("emailUsuario", "")

        val usuario = prefs.getString(
            "loginUsuario",
            ""
        ).orEmpty()

        findViewById<TextView>(
            R.id.txtNomeUsuarioConta
        ).text =
            if (usuario.isNotBlank()) {
                "@$usuario"
            } else {
                "@jogador"
            }
    }
}
