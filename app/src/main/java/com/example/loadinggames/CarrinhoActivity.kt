
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

        containerItensCarrinho =
            findViewById(R.id.containerItensCarrinho)

        txtQuantidadeItens =
            findViewById(R.id.txtQuantidadeItens)

        txtSubtotal = findViewById(R.id.txtSubtotal)
        txtDesconto = findViewById(R.id.txtDesconto)

        txtTotalCarrinho =
            findViewById(R.id.txtTotalCarrinho)

        btnContinuarComprando =
            findViewById(R.id.btnContinuarComprando)

        btnFinalizarCompra =
            findViewById(R.id.btnFinalizarCompra)

        btnVoltar = findViewById(R.id.btnVoltar)

        // VOLTAR
        btnVoltar.setOnClickListener {
            finish()
        }

        // CONTINUAR COMPRANDO
        btnContinuarComprando.setOnClickListener {
            finish()
        }

        // IR PARA O PAGAMENTO
        btnFinalizarCompra.setOnClickListener {
            startActivity(
                Intent(this, PagamentoActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        carregarCarrinho()
    }

    private fun carregarCarrinho() {

        val idUsuario = obterIdUsuario()

        btnFinalizarCompra.isEnabled = false

        if (idUsuario == -1) {
            mostrarMensagem(
                "Usuário não identificado. Faça login novamente."
            )
            return
        }

        RetrofitClient.api.buscarCarrinho(idUsuario)
            .enqueue(object : Callback<CarrinhoResponse> {

                override fun onResponse(
                    call: Call<CarrinhoResponse>,
                    response: Response<CarrinhoResponse>
                ) {
                    if (isFinishing || isDestroyed) return

                    if (response.isSuccessful) {
                        val carrinho = response.body()

                        if (carrinho != null) {
                            exibirCarrinho(carrinho)
                        } else {
                            mostrarMensagem(
                                "O servidor não retornou os dados do carrinho."
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
                    if (call.isCanceled || isFinishing || isDestroyed) {
                        return
                    }

                    mostrarMensagem(
                        "Erro ao conectar com o servidor: ${t.message}"
                    )
                }
            })
    }

    private fun exibirCarrinho(carrinho: CarrinhoResponse) {

        containerItensCarrinho.removeAllViews()

        val quantidade = carrinho.itens.size

        txtQuantidadeItens.text =
            if (quantidade == 1) {
                "1 item"
            } else {
                "$quantidade itens"
            }

        txtSubtotal.text = formatarPreco(carrinho.total)
        txtDesconto.text = formatarPreco(0.0)
        txtTotalCarrinho.text = formatarPreco(carrinho.total)

        if (carrinho.itens.isEmpty()) {

            val mensagem = TextView(this).apply {
                text = "Seu carrinho está vazio.\n\nExplore a loja e encontre sua próxima aventura!"
                setTextColor(Color.parseColor("#A7A9B5"))
                textSize = 15f
                gravity = Gravity.CENTER
                setPadding(
                    dp(20), dp(65), dp(20), dp(65)
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
                dp(12), dp(12), dp(10), dp(12)
            )

            background = GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                setColor(Color.parseColor("#151A2B"))
                setStroke(
                    dp(1),
                    Color.parseColor("#292E43")
                )
            }

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(14)
            }
        }

        // CAPA
        val imagem = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                dp(86),
                dp(112)
            )

            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = item.nome
            setImageResource(descobrirImagem(item.nome))

            clipToOutline = true

            background = GradientDrawable().apply {
                cornerRadius = dp(10).toFloat()
                setColor(Color.parseColor("#252A3B"))
            }
        }

        card.addView(imagem)

        // INFORMAÇÕES
        val informacoes = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL

            setPadding(dp(12), 0, dp(4), 0)

            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val nome = TextView(this).apply {
            text = item.nome
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            maxLines = 2
        }

        informacoes.addView(nome)

        val classificacao = TextView(this).apply {
            text = if (item.classificacaoIndicativa == 0) {
                "Classificação: Livre"
            } else {
                "Classificação: ${item.classificacaoIndicativa} anos"
            }

            setTextColor(Color.parseColor("#9CA3B8"))
            textSize = 11f

            setPadding(0, dp(7), 0, 0)
        }

        informacoes.addView(classificacao)

        val preco = TextView(this).apply {
            text = formatarPreco(item.preco)
            setTextColor(Color.parseColor("#C084FC"))
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)

            setPadding(0, dp(12), 0, 0)
        }

        informacoes.addView(preco)

        card.addView(informacoes)

        // BOTÃO REMOVER
        val remover = TextView(this).apply {
            text = "✕"
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#C6C8D4"))

            layoutParams = LinearLayout.LayoutParams(
                dp(40),
                dp(44)
            )

            contentDescription = "Remover ${item.nome}"
            isClickable = true
            isFocusable = true
        }

        remover.setOnClickListener {
            confirmarRemocao(item)
        }

        card.addView(remover)

        return card
    }

    // CONFIRMAR ANTES DE REMOVER
    private fun confirmarRemocao(item: ItemCarrinhoResponse) {

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(
                dp(24), dp(26), dp(24), dp(24)
            )
        }

        val titulo = TextView(this).apply {
            text = "Remover do carrinho?"
            textSize = 20f
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        container.addView(titulo)

        val mensagem = TextView(this).apply {
            text = "Deseja remover ${item.nome} do seu carrinho?"
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
                topMargin = dp(14)
                bottomMargin = dp(20)
            }
        )

        val btnRemover = Button(this).apply {
            text = "Remover jogo"
            isAllCaps = false
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#7C3AED")
            )
        }

        container.addView(
            btnRemover,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        val btnCancelar = Button(this).apply {
            text = "Cancelar"
            isAllCaps = false
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#292D43")
            )
        }

        container.addView(
            btnCancelar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            ).apply {
                topMargin = dp(10)
            }
        )

        val dialog = AlertDialog.Builder(this)
            .setView(container)
            .create()

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnRemover.setOnClickListener {
            dialog.dismiss()
            removerItemCarrinho(item)
        }

        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(
                GradientDrawable().apply {
                    cornerRadius = dp(18).toFloat()
                    setColor(Color.parseColor("#191C2D"))
                }
            )

            setLayout(
                (resources.displayMetrics.widthPixels * 0.88f).toInt(),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    // REMOVER ITEM PELA API
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
                if (isFinishing || isDestroyed) return

                if (response.isSuccessful) {
                    mostrarMensagem("Jogo removido do carrinho!")
                } else {
                    mostrarMensagem(
                        "Erro ao remover. Código: ${response.code()}"
                    )
                }

                carregarCarrinho()
            }

            override fun onFailure(
                call: Call<RemoverCarrinhoResponse>,
                t: Throwable
            ) {
                if (call.isCanceled || isFinishing || isDestroyed) {
                    return
                }

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

    // IDENTIFICAR CAPA DO JOGO

    private fun descobrirImagem(nomeJogo: String): Int {

        val nome = nomeJogo.lowercase(Locale.ROOT)

        val nomeDrawable = when {

            "resident evil revelations" in nome ->
                "caparesidentevilrevelations"

            "resident evil 5" in nome ->
                "caparesidentevil5"

            "resident evil 6" in nome ->
                "caparesidentevil6"

            "resident evil" in nome ->
                "resident_evil"

            "dark souls" in nome ->
                "capadarksouls"

            "sekiro" in nome ->
                "capasekiro"

            "devil may cry 4" in nome ->
                "capadevilmaycry4"

            "dragon's dogma" in nome ||
                    "dragons dogma" in nome ->
                "capadragonsdogma2"

            "monster hunter rise" in nome ->
                "capamonsterhunterrise"

            "monster hunter world" in nome ->
                "capamonsterhunterworld"

            "street fighter v" in nome ||
                    "street fighter 5" in nome ->
                "capastreetfighter5"

            "grand theft auto" in nome ||
                    "gta" in nome ->
                "capagtav"

            "cyberpunk" in nome ->
                "capacyberpunk"

            "red dead" in nome ->
                "capareddead"

            "witcher" in nome ->
                "capathewitcher"

            "minecraft" in nome ->
                "capaminecraft"

            "elden ring" in nome ->
                "capaeldenring"

            "god of war" in nome ->
                "god_of_war"

            "fc 24" in nome ->
                "fc24"

            "hogwarts" in nome ->
                "hogwarts"

            else -> ""
        }

        if (nomeDrawable.isNotEmpty()) {

            val idImagem = resources.getIdentifier(
                nomeDrawable,
                "drawable",
                packageName
            )

            if (idImagem != 0) {
                return idImagem
            }
        }

        return android.R.drawable.ic_menu_gallery
    }


    // FORMATAR PREÇO EM REAIS
    private fun formatarPreco(preco: Double): String {
        return NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        ).format(preco)
    }

    // MOSTRAR MENSAGEM
    private fun mostrarMensagem(mensagem: String) {
        Toast.makeText(
            this,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    // CONVERTER DP
    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }
}

