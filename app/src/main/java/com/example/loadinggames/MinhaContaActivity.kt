
package com.example.loadinggames

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class MinhaContaActivity : AppCompatActivity() {

    private lateinit var imgFotoPerfil: ImageView
    private lateinit var txtAvatarPadrao: TextView

    private val selecionarFoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            salvarFotoPerfil(uri)
        }
    }

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

        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        txtAvatarPadrao = findViewById(R.id.txtAvatarPadrao)

        // ALTERAR FOTO
        findViewById<View>(R.id.btnAlterarFotoConta)
            .setOnClickListener {
                selecionarFoto.launch("image/*")
            }

        findViewById<View>(R.id.txtAlterarFotoConta)
            .setOnClickListener {
                selecionarFoto.launch("image/*")
            }

        // VOLTAR
        findViewById<View>(R.id.btnVoltarConta)
            .setOnClickListener {
                finish()
            }

        // MEUS DADOS
        findViewById<View>(R.id.btnMeusDados)
            .setOnClickListener {
                startActivity(
                    Intent(this, MeusDadosActivity::class.java)
                )
            }

        // ALTERAR SENHA
        findViewById<View>(R.id.btnAlterarSenha)
            .setOnClickListener {
                startActivity(
                    Intent(this, AlterarSenhaActivity::class.java)
                )
            }

        // CONFIGURAÇÕES
        findViewById<View>(R.id.btnConfiguracoesConta)
            .setOnClickListener {
                startActivity(
                    Intent(
                        this,
                        ConfiguracoesContaActivity::class.java
                    )
                )
            }

        // MENU INFERIOR
        findViewById<View>(R.id.btnInicioConta)
            .setOnClickListener {
                abrirMain()
            }

        findViewById<View>(R.id.btnBuscarConta)
            .setOnClickListener {
                abrirMain("buscar")
            }

        findViewById<View>(R.id.btnCategoriasConta)
            .setOnClickListener {
                startActivity(
                    Intent(this, CategoriasActivity::class.java)
                )
            }

        findViewById<View>(R.id.btnFavoritosConta)
            .setOnClickListener {
                abrirMain("favoritos")
            }

        findViewById<View>(R.id.btnCarrinhoConta)
            .setOnClickListener {
                startActivity(
                    Intent(this, CarrinhoActivity::class.java)
                )
            }
    }

    // SALVAR FOTO NO ARMAZENAMENTO DO APP
    private fun salvarFotoPerfil(uri: android.net.Uri) {

        val idUsuario = obterIdUsuario()

        if (idUsuario == -1) return

        try {
            val arquivo = File(
                filesDir,
                "foto_perfil_$idUsuario.jpg"
            )

            val bitmap = contentResolver
                .openInputStream(uri)
                ?.use { entrada ->
                    BitmapFactory.decodeStream(entrada)
                }

            if (bitmap == null) {
                Toast.makeText(
                    this,
                    "Não foi possível abrir a imagem.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            FileOutputStream(arquivo).use { saida ->
                bitmap.compress(
                    android.graphics.Bitmap.CompressFormat.JPEG,
                    85,
                    saida
                )
            }

            bitmap.recycle()

            carregarFotoPerfil()

            Toast.makeText(
                this,
                "Foto de perfil atualizada!",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Erro ao salvar a foto: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // CARREGAR FOTO DO USUÁRIO LOGADO
    private fun carregarFotoPerfil() {

        val idUsuario = obterIdUsuario()

        if (idUsuario == -1) return

        val arquivo = File(
            filesDir,
            "foto_perfil_$idUsuario.jpg"
        )

        if (arquivo.exists()) {

            val bitmap = BitmapFactory.decodeFile(
                arquivo.absolutePath
            )

            if (bitmap != null) {
                imgFotoPerfil.setImageBitmap(bitmap)
                imgFotoPerfil.visibility = View.VISIBLE
                txtAvatarPadrao.visibility = View.GONE
                return
            }
        }

        // MOSTRAR AVATAR PADRÃO
        imgFotoPerfil.setImageDrawable(null)
        imgFotoPerfil.visibility = View.GONE
        txtAvatarPadrao.visibility = View.VISIBLE
    }

    private fun obterIdUsuario(): Int {
        return getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        ).getInt("idUsuario", -1)
    }

    // ABRIR MAINACTIVITY
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

    // ATUALIZAR PERFIL
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

        carregarFotoPerfil()
    }
}
