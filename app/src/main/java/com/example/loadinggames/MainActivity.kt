package com.example.loadinggames

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.JogoResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Normalizer
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // COMPONENTES DA HOME
    private lateinit var containerJogos: LinearLayout
    private lateinit var edtPesquisa: EditText
    private lateinit var txtTituloJogos: TextView

    // BOTÕES DO TOPO
    private lateinit var btnMenuTopo: ImageButton
    private lateinit var btnFavoritosTopo: ImageButton
    private lateinit var btnCarrinhoTopo: ImageButton
    private lateinit var btnPerfilTopo: ImageButton

    // MENU INFERIOR
    private lateinit var btnInicioMenu: LinearLayout
    private lateinit var btnBuscarMenu: LinearLayout
    private lateinit var btnCategoriasMenu: LinearLayout
    private lateinit var btnFavoritosMenu: LinearLayout
    private lateinit var btnCarrinhoMenu: LinearLayout

    // BOTÕES DA HOME
    private lateinit var btnVerTodos: TextView
    private lateinit var btnComprar: Button

    // MENU LATERAL
    private lateinit var menuLateral: LinearLayout
    private lateinit var fundoMenuLateral: View
    private lateinit var btnFecharMenu: TextView

    // DADOS DO USUÁRIO
    private lateinit var txtNomeUsuarioLateral: TextView
    private lateinit var txtEmailUsuarioLateral: TextView
    private lateinit var btnContaCabecalhoLateral: TextView

    private lateinit var menuUsuarioLogado: LinearLayout
    private lateinit var menuUsuarioVisitante: LinearLayout

    // OPÇÕES DO MENU LATERAL
    private lateinit var btnMinhaContaLateral: LinearLayout
    private lateinit var btnBibliotecaLateral: LinearLayout
    private lateinit var btnComprasLateral: LinearLayout
    private lateinit var btnConfiguracoesLateral: LinearLayout

    private lateinit var btnEntrarLateral: LinearLayout
    private lateinit var btnCriarContaLateral: LinearLayout
    private lateinit var btnAjudaLateral: LinearLayout
    private lateinit var btnSairLateral: LinearLayout

    // CONTROLE
    private var menuAberto = false
    private var jogosCarregados: List<JogoResponse> = emptyList()
    private var categoriaAtiva: String? = null

    // INICIAR A ACTIVITY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        iniciarComponentes()
        configurarCliques()
        configurarPesquisa()
        atualizarMenuLateral()
        verificarAcoesRecebidas(intent)
        buscarJogosDaApi()
    }

    override fun onResume() {
        super.onResume()

        if (::menuLateral.isInitialized) {
            atualizarMenuLateral()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        verificarAcoesRecebidas(intent)
        aplicarFiltros()
    }

    // INICIALIZAR COMPONENTES
    private fun iniciarComponentes() {

        containerJogos = findViewById(R.id.containerJogos)
        edtPesquisa = findViewById(R.id.edtPesquisa)
        txtTituloJogos = findViewById(R.id.txtTituloJogos)

        btnMenuTopo = findViewById(R.id.btnMenuTopo)
        btnFavoritosTopo = findViewById(R.id.btnFavoritosTopo)
        btnCarrinhoTopo = findViewById(R.id.btnCarrinhoTopo)
        btnPerfilTopo = findViewById(R.id.btnPerfilTopo)

        btnInicioMenu = findViewById(R.id.btnInicioMenu)
        btnBuscarMenu = findViewById(R.id.btnBuscarMenu)
        btnCategoriasMenu = findViewById(R.id.btnCategoriasMenu)
        btnFavoritosMenu = findViewById(R.id.btnFavoritosMenu)
        btnCarrinhoMenu = findViewById(R.id.btnCarrinhoMenu)

        btnVerTodos = findViewById(R.id.btnVerTodos)
        btnComprar = findViewById(R.id.btnComprar)

        menuLateral = findViewById(R.id.menuLateral)
        fundoMenuLateral = findViewById(R.id.fundoMenuLateral)
        btnFecharMenu = findViewById(R.id.btnFecharMenu)

        txtNomeUsuarioLateral =
            findViewById(R.id.txtNomeUsuarioLateral)

        txtEmailUsuarioLateral =
            findViewById(R.id.txtEmailUsuarioLateral)

        btnContaCabecalhoLateral =
            findViewById(R.id.btnContaCabecalhoLateral)

        menuUsuarioLogado =
            findViewById(R.id.menuUsuarioLogado)

        menuUsuarioVisitante =
            findViewById(R.id.menuUsuarioVisitante)

        btnMinhaContaLateral =
            findViewById(R.id.btnMinhaContaLateral)

        btnBibliotecaLateral =
            findViewById(R.id.btnBibliotecaLateral)

        btnComprasLateral =
            findViewById(R.id.btnComprasLateral)

        btnConfiguracoesLateral =
            findViewById(R.id.btnConfiguracoesLateral)

        btnEntrarLateral =
            findViewById(R.id.btnEntrarLateral)

        btnCriarContaLateral =
            findViewById(R.id.btnCriarContaLateral)

        btnAjudaLateral =
            findViewById(R.id.btnAjudaLateral)

        btnSairLateral =
            findViewById(R.id.btnSairLateral)
    }

    private fun configurarCliques() {

        btnMenuTopo.setOnClickListener {
            abrirMenuLateral()
        }

        btnFavoritosTopo.setOnClickListener {
            abrirFavoritos()
        }

        btnCarrinhoTopo.setOnClickListener {
            abrirCarrinho()
        }

        btnPerfilTopo.setOnClickListener {
            abrirPerfil()
        }

        btnInicioMenu.setOnClickListener {
            voltarParaInicio()
        }

        btnBuscarMenu.setOnClickListener {
            focarPesquisa()
        }

        btnCategoriasMenu.setOnClickListener {
            abrirCategorias()
        }

        btnFavoritosMenu.setOnClickListener {
            abrirFavoritos()
        }

        btnCarrinhoMenu.setOnClickListener {
            abrirCarrinho()
        }

        btnVerTodos.setOnClickListener {
            mostrarTodosOsJogos()
        }

        btnComprar.setOnClickListener {
            abrirResidentEvil()
        }

        btnFecharMenu.setOnClickListener {
            fecharMenuLateral()
        }

        fundoMenuLateral.setOnClickListener {
            fecharMenuLateral()
        }

        btnContaCabecalhoLateral.setOnClickListener {
            fecharMenuLateral()
            if (usuarioEstaLogado()) {
                abrirMinhaConta()
            } else {
                abrirLogin()
            }
        }

        btnMinhaContaLateral.setOnClickListener {
            fecharMenuLateral()
            abrirMinhaConta()
        }

        btnBibliotecaLateral.setOnClickListener {
            fecharMenuLateral()
            abrirBiblioteca()
        }

        btnComprasLateral.setOnClickListener {
            fecharMenuLateral()
            Toast.makeText(
                this,
                "Minhas compras será implementado na próxima etapa.",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnConfiguracoesLateral.setOnClickListener {
            fecharMenuLateral()

            if (usuarioEstaLogado()) {
                startActivity(
                    Intent(
                        this,
                        ConfiguracoesContaActivity::class.java
                    )
                )
            } else {
                abrirLogin()
            }
        }

        btnEntrarLateral.setOnClickListener {
            Toast.makeText(
                this,
                "Botão Entrar clicado!",
                Toast.LENGTH_SHORT
            ).show()

            abrirLogin()
        }

        btnCriarContaLateral.setOnClickListener {
            fecharMenuLateral()
            abrirCadastro()
        }

        btnAjudaLateral.setOnClickListener {
            fecharMenuLateral()
            Toast.makeText(
                this,
                "Ajuda e suporte será implementado na próxima etapa.",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnSairLateral.setOnClickListener {
            fecharMenuLateral()
            confirmarSaida()
        }
    }

    private fun abrirMenuLateral() {
        if (menuAberto) {
            fecharMenuLateral()
            return
        }

        atualizarMenuLateral()
        menuAberto = true

        fundoMenuLateral.alpha = 0f
        fundoMenuLateral.visibility = View.VISIBLE

        fundoMenuLateral.animate()
            .alpha(1f)
            .setDuration(220)
            .start()

        menuLateral.visibility = View.VISIBLE
        menuLateral.translationX = -dp(330).toFloat()

        menuLateral.animate()
            .translationX(0f)
            .setDuration(250)
            .start()
    }

    private fun fecharMenuLateral() {
        if (!menuAberto) return

        menuAberto = false

        fundoMenuLateral.animate()
            .alpha(0f)
            .setDuration(180)
            .withEndAction {
                fundoMenuLateral.visibility = View.GONE
            }
            .start()

        menuLateral.animate()
            .translationX(-dp(330).toFloat())
            .setDuration(220)
            .withEndAction {
                menuLateral.visibility = View.GONE
            }
            .start()
    }

    private fun atualizarMenuLateral() {
        val preferencias = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        val idUsuario = preferencias.getInt("idUsuario", -1)

        if (idUsuario != -1) {
            val nome = preferencias.getString(
                "nomeUsuario",
                null
            )?.trim().orEmpty()

            val email = preferencias.getString(
                "emailUsuario",
                null
            )?.trim().orEmpty()

            txtNomeUsuarioLateral.text =
                if (nome.isNotEmpty()) nome else "Jogador"

            txtEmailUsuarioLateral.text =
                if (email.isNotEmpty()) email
                else "Conta Loading Games"

            btnContaCabecalhoLateral.text = "MINHA CONTA  ›"

            menuUsuarioLogado.visibility = View.VISIBLE
            menuUsuarioVisitante.visibility = View.GONE
            btnSairLateral.visibility = View.VISIBLE
        } else {
            txtNomeUsuarioLateral.text = "Olá, jogador!"
            txtEmailUsuarioLateral.text =
                "Entre para acessar sua conta"

            btnContaCabecalhoLateral.text = "ENTRAR  ›"

            menuUsuarioLogado.visibility = View.GONE
            menuUsuarioVisitante.visibility = View.VISIBLE
            btnSairLateral.visibility = View.GONE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (menuAberto) {
            fecharMenuLateral()
        } else {
            super.onBackPressed()
        }
    }

    private fun voltarParaInicio() {
        categoriaAtiva = null

        if (edtPesquisa.text.isNotEmpty()) {
            edtPesquisa.setText("")
        } else {
            txtTituloJogos.text = "Destaques"
            aplicarFiltros()
        }
    }

    private fun mostrarTodosOsJogos() {
        edtPesquisa.clearFocus()
        categoriaAtiva = null
        txtTituloJogos.text = "Todos os jogos"
        aplicarFiltros()
    }

    private fun verificarAcoesRecebidas(
        intentRecebida: Intent
    ) {
        val abrirPesquisa = intentRecebida.getBooleanExtra(
            "abrirPesquisa",
            false
        )

        if (abrirPesquisa) {
            edtPesquisa.postDelayed(
                { focarPesquisa() },
                250
            )
        }

        val categoria = intentRecebida.getStringExtra(
            "categoriaSelecionada"
        )?.trim()

        if (!categoria.isNullOrBlank()) {
            if (
                categoria.equals("Todas as categorias", ignoreCase = true) ||
                categoria.equals("Todas", ignoreCase = true)
            ) {
                categoriaAtiva = null
                txtTituloJogos.text = "Todos os jogos"
            } else {
                categoriaAtiva = categoria
                txtTituloJogos.text = categoria
            }
        }

        intentRecebida.removeExtra("abrirPesquisa")
        intentRecebida.removeExtra("categoriaSelecionada")
    }

    private fun configurarPesquisa() {
        edtPesquisa.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val pesquisa = s?.toString()?.trim().orEmpty()

                    if (pesquisa.isEmpty()) {
                        txtTituloJogos.text = categoriaAtiva ?: "Destaques"
                    } else {
                        txtTituloJogos.text = if (categoriaAtiva != null) {
                            "$categoriaAtiva • Resultados"
                        } else {
                            "Resultados"
                        }
                    }

                    aplicarFiltros()
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )
    }

    private fun focarPesquisa() {
        edtPesquisa.requestFocus()
        edtPesquisa.setSelection(edtPesquisa.text.length)

        val teclado = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        teclado.showSoftInput(
            edtPesquisa,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    private fun abrirCategorias() {
        startActivity(
            Intent(this, CategoriasActivity::class.java)
        )
    }

    private fun usuarioEstaLogado(): Boolean {
        val preferencias = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        return preferencias.getInt("idUsuario", -1) != -1
    }

    private fun abrirLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
    }

    private fun abrirCadastro() {
        startActivity(
            Intent(this, CadastroActivity::class.java)
        )
    }

    private fun abrirCarrinho() {
        if (!usuarioEstaLogado()) {
            mostrarLoginNecessario(
                "Entre na sua conta para acessar o carrinho."
            )
            return
        }

        startActivity(
            Intent(this, CarrinhoActivity::class.java)
        )
    }

    private fun abrirBiblioteca() {
        if (!usuarioEstaLogado()) {
            mostrarLoginNecessario(
                "Entre na sua conta para acessar sua biblioteca."
            )
            return
        }

        startActivity(
            Intent(this, BibliotecaActivity::class.java)
        )
    }

    private fun abrirFavoritos() {
        if (!usuarioEstaLogado()) {
            mostrarLoginNecessario(
                "Entre na sua conta para acessar seus favoritos."
            )
            return
        }

        Toast.makeText(
            this,
            "Favoritos será implementado na próxima etapa.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun abrirPerfil() {
        if (usuarioEstaLogado()) {
            abrirMinhaConta()
        } else {
            abrirLogin()
        }
    }

    private fun abrirMinhaConta() {
        if (!usuarioEstaLogado()) {
            abrirLogin()
            return
        }

        startActivity(
            Intent(this, MinhaContaActivity::class.java)
        )
    }

    private fun mostrarLoginNecessario(mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle("Entre para continuar")
            .setMessage(mensagem)
            .setPositiveButton("ENTRAR") { _, _ ->
                abrirLogin()
            }
            .setNegativeButton("CRIAR CONTA") { _, _ ->
                abrirCadastro()
            }
            .setNeutralButton("AGORA NÃO", null)
            .show()
    }

    private fun confirmarSaida() {
        AlertDialog.Builder(this)
            .setTitle("Sair da conta")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("SAIR") { _, _ ->
                sairDaConta()
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun sairDaConta() {
        getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        ).edit()
            .remove("idUsuario")
            .remove("nomeUsuario")
            .remove("emailUsuario")
            .remove("loginUsuario")
            .remove("fotoPerfilUrl")
            .apply()

        atualizarMenuLateral()

        Toast.makeText(
            this,
            "Você saiu da sua conta.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun abrirResidentEvil() {
        val residentEvil = jogosCarregados.firstOrNull {
            it.nome.contains("Resident Evil", ignoreCase = true)
        }

        if (residentEvil == null) {
            edtPesquisa.setText("Resident Evil")
            focarPesquisa()
            return
        }

        abrirDetalhes(residentEvil)
    }

    private fun buscarJogosDaApi() {
        RetrofitClient.api
            .buscarJogos(null)
            .enqueue(object : Callback<List<JogoResponse>> {

                override fun onResponse(
                    call: Call<List<JogoResponse>>,
                    response: Response<List<JogoResponse>>
                ) {
                    if (response.isSuccessful) {
                        val jogos = response.body() ?: emptyList()
                        jogosCarregados = jogos

                        Log.d(
                            "JOGOS_API",
                            "Jogos recebidos: ${jogos.size}"
                        )

                        aplicarFiltros()
                    } else {
                        Log.e(
                            "JOGOS_API",
                            "Erro HTTP: ${response.code()}"
                        )

                        Toast.makeText(
                            this@MainActivity,
                            "Não foi possível carregar os jogos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<JogoResponse>>,
                    t: Throwable
                ) {
                    Log.e("JOGOS_API", "Erro de conexão", t)

                    Toast.makeText(
                        this@MainActivity,
                        "Não foi possível conectar ao servidor.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun aplicarFiltros() {
        val termoPesquisa = normalizarTexto(
            edtPesquisa.text?.toString()?.trim().orEmpty()
        )
        val categoriaAlvo = categoriaAtiva?.let { normalizarTexto(it) }

        val filtrados = jogosCarregados.filter { jogo ->
            val bateCategoria = if (categoriaAlvo.isNullOrBlank()) {
                true
            } else {
                extrairCategorias(jogo).any { cat ->
                    normalizarTexto(cat).contains(categoriaAlvo) ||
                            categoriaAlvo.contains(normalizarTexto(cat))
                }
            }

            val batePesquisa = if (termoPesquisa.isBlank()) {
                true
            } else {
                normalizarTexto(jogo.nome).contains(termoPesquisa)
            }

            bateCategoria && batePesquisa
        }

        exibirJogos(filtrados)
    }

    private fun extrairCategorias(jogo: JogoResponse): List<String> {
        val texto = "${jogo.nome}${jogo.descricao.orEmpty()}"
        val textoNormalizado = normalizarTexto(texto)
        val categoriasEncontradas = mutableListOf<String>()

        val regras = mapOf(
            "Ação" to listOf(
                "acao", "action", "resident evil", "gta", "grand theft auto",
                "god of war", "devil may cry", "sekiro", "cyberpunk", "monster hunter",
                "street fighter"
            ),
            "Aventura" to listOf(
                "aventura", "adventure", "red dead", "hogwarts", "minecraft",
                "god of war", "resident evil", "zelda", "uncharted"
            ),
            "RPG" to listOf(
                "rpg", "elden ring", "dark souls", "witcher", "dragon's dogma",
                "dragons dogma", "cyberpunk", "final fantasy", "persona"
            ),
            "Esportes" to listOf(
                "esporte", "esportes", "sport", "sports", "fc 24", "fifa",
                "nba", "efootball", "futebol"
            ),
            "Corrida" to listOf(
                "corrida", "racing", "forza", "gran turismo", "need for speed",
                "mario kart", "f1"
            ),
            "Estratégia" to listOf(
                "estrategia", "strategy", "civilization", "age of empires",
                "xcom", "tactics", "total war"
            )
        )

        regras.forEach { (categoria, palavrasChave) ->
            if (palavrasChave.any { palavra -> normalizarTexto(palavra) in textoNormalizado }) {
                categoriasEncontradas.add(categoria)
            }
        }

        return categoriasEncontradas
    }

    private fun normalizarTexto(texto: String): String {
        val semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        return semAcento.lowercase(Locale.ROOT).trim()
    }

    private fun exibirJogos(jogos: List<JogoResponse>) {
        containerJogos.removeAllViews()

        if (jogos.isEmpty()) {
            val mensagem = TextView(this).apply {
                text = if (categoriaAtiva != null) {
                    "Nenhum jogo encontrado na categoria \"$categoriaAtiva\"."
                } else {
                    "Nenhum jogo encontrado."
                }
                setTextColor(Color.parseColor("#8F93A2"))
                textSize = 14f
                setPadding(dp(4), dp(24), dp(4), dp(24))
            }

            containerJogos.addView(mensagem)
            return
        }

        // CADA LINHA RECEBE ATÉ DOIS JOGOS
        jogos.chunked(2).forEach { dupla ->

            val linha = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.TOP
            }

            val parametrosLinha = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }

            linha.layoutParams = parametrosLinha

            dupla.forEachIndexed { indice, jogo ->
                val card = criarCardJogo(jogo)

                card.layoutParams = LinearLayout.LayoutParams(
                    0,
                    dp(284),
                    1f
                ).apply {
                    if (indice == 0) {
                        rightMargin = dp(7)
                    } else {
                        leftMargin = dp(7)
                    }
                }

                linha.addView(card)
            }

            // SE A ÚLTIMA LINHA TIVER APENAS UM JOGO
            if (dupla.size == 1) {
                linha.addView(
                    View(this),
                    LinearLayout.LayoutParams(
                        0,
                        1,
                        1f
                    ).apply {
                        leftMargin = dp(7)
                    }
                )
            }

            containerJogos.addView(linha)
        }
    }

    private fun criarCardJogo(
        jogo: JogoResponse
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            isClickable = true
            isFocusable = true

            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(14).toFloat()
                setColor(Color.parseColor("#151A2B"))
                setStroke(
                    dp(1),
                    Color.parseColor("#292D43")
                )
            }

            clipToOutline = true
            elevation = dp(2).toFloat()
        }

        // CAPA DO JOGO
        val imagem = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(178)
            )

            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = jogo.nome

            setImageResource(
                descobrirImagem(
                    jogo.nome,
                    jogo.imagemPrincipalUrl
                )
            )
        }

        card.addView(imagem)

        // NOME
        val nome = TextView(this).apply {
            text = jogo.nome
            setTextColor(Color.WHITE)
            textSize = 13f
            setTypeface(typeface, Typeface.BOLD)
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END

            setPadding(
                dp(10),
                dp(9),
                dp(10),
                0
            )
        }

        card.addView(nome)

        // CLASSIFICAÇÃO
        val classificacao = TextView(this).apply {
            text = if (jogo.classificacaoIndicativa == 0) {
                "Livre"
            } else {
                "${jogo.classificacaoIndicativa} anos"
            }

            setTextColor(Color.parseColor("#9599AA"))
            textSize = 10f
            setPadding(dp(10), dp(4), dp(10), 0)
        }

        card.addView(classificacao)

        // ESPAÇO PARA MANTER O PREÇO EMBAIXO
        card.addView(
            View(this),
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // PREÇO
        val preco = TextView(this).apply {
            text = formatarPreco(jogo.preco)
            setTextColor(Color.parseColor("#C084FC"))
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)

            setPadding(
                dp(10),
                0,
                dp(10),
                dp(12)
            )
        }

        card.addView(preco)

        card.setOnClickListener {
            abrirDetalhes(jogo)
        }

        return card
    }

    private fun descobrirImagem(
        nomeJogo: String,
        caminhoImagem: String? = null
    ): Int {

        // PRIMEIRO PROCURA O ARQUIVO INFORMADO PELA API
        if (!caminhoImagem.isNullOrBlank()) {
            val nomeArquivo = caminhoImagem
                .substringBefore("?")
                .substringAfterLast("/")
                .substringBeforeLast(".")
                .lowercase(Locale.ROOT)

            val idImagem = resources.getIdentifier(
                nomeArquivo,
                "drawable",
                packageName
            )

            if (idImagem != 0) {
                return idImagem
            }
        }

        // DEPOIS PROCURA PELO NOME DO JOGO
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

        Log.w(
            "CAPA_JOGO",
            "Capa não encontrada: $nomeJogo"
        )

        return android.R.drawable.ic_menu_gallery
    }

    private fun formatarPreco(preco: Double): String {
        return NumberFormat
            .getCurrencyInstance(Locale("pt", "BR"))
            .format(preco)
    }

    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }

    private fun abrirDetalhes(jogo: JogoResponse) {

        val intent = Intent(
            this,
            DetalhesActivity::class.java
        )

        intent.putExtra("idJogo", jogo.id)
        intent.putExtra("nome", jogo.nome)

        intent.putExtra(
            "imagem",
            descobrirImagem(
                jogo.nome,
                jogo.imagemPrincipalUrl
            )
        )

        intent.putExtra(
            "preco",
            formatarPreco(jogo.preco)
        )

        intent.putExtra(
            "descricao",
            jogo.descricao ?: "Sem descrição disponível."
        )

        intent.putExtra(
            "classificacao",
            jogo.classificacaoIndicativa
        )

        intent.putExtra(
            "dataLancamento",
            jogo.dataLancamento
        )

        startActivity(intent)
    }
}