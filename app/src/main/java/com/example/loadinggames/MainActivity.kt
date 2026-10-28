
package com.example.loadinggames

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var containerJogos: LinearLayout
    private lateinit var edtPesquisa: EditText
    private lateinit var txtTituloJogos: TextView

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

    private var menuAberto = false

    private var jogosCarregados: List<JogoResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iniciarComponentes()
        configurarCliques()
        configurarPesquisa()

        atualizarMenuLateral()
        buscarJogosDaApi()
        verificarAcoesRecebidas(intent)
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
    }

    // ==============================================
    // INICIALIZAR COMPONENTES
    // ==============================================

    private fun iniciarComponentes() {

        containerJogos = findViewById(R.id.containerJogos)
        edtPesquisa = findViewById(R.id.edtPesquisa)
        txtTituloJogos = findViewById(R.id.txtTituloJogos)

        btnMenuTopo = findViewById(R.id.btnMenuTopo)
        btnFavoritosTopo = findViewById(R.id.btnFavoritosTopo)
        btnCarrinhoTopo = findViewById(R.id.btnCarrinhoTopo)
        btnPerfilTopo = findViewById(R.id.btnPerfilTopo)

        // MENU INFERIOR
        btnInicioMenu = findViewById(R.id.btnInicioMenu)
        btnBuscarMenu = findViewById(R.id.btnBuscarMenu)
        btnCategoriasMenu = findViewById(R.id.btnCategoriasMenu)
        btnFavoritosMenu = findViewById(R.id.btnFavoritosMenu)
        btnCarrinhoMenu = findViewById(R.id.btnCarrinhoMenu)

        btnVerTodos = findViewById(R.id.btnVerTodos)
        btnComprar = findViewById(R.id.btnComprar)

        // MENU LATERAL
        menuLateral = findViewById(R.id.menuLateral)
        fundoMenuLateral = findViewById(R.id.fundoMenuLateral)
        btnFecharMenu = findViewById(R.id.btnFecharMenu)

        // DADOS DO USUÁRIO
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

        // OPÇÕES DO MENU LATERAL
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

    // ==============================================
    // CLIQUES DOS BOTÕES
    // ==============================================

    private fun configurarCliques() {

        // BOTÕES DO TOPO

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

        // ==========================================
        // MENU INFERIOR
        // ==========================================

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

        // ==========================================
        // BOTÕES DA HOME
        // ==========================================

        btnVerTodos.setOnClickListener {
            mostrarTodosOsJogos()
        }

        btnComprar.setOnClickListener {
            abrirResidentEvil()
        }

        // ==========================================
        // ABRIR E FECHAR MENU LATERAL
        // ==========================================

        btnFecharMenu.setOnClickListener {
            fecharMenuLateral()
        }

        fundoMenuLateral.setOnClickListener {
            fecharMenuLateral()
        }

        // CABEÇALHO DO MENU LATERAL

        btnContaCabecalhoLateral.setOnClickListener {
            fecharMenuLateral()

            if (usuarioEstaLogado()) {
                abrirMinhaConta()
            } else {
                abrirLogin()
            }
        }

        // ==========================================
        // MENU LATERAL - USUÁRIO LOGADO
        // ==========================================

        // MINHA CONTA
        btnMinhaContaLateral.setOnClickListener {
            fecharMenuLateral()
            abrirMinhaConta()
        }

        // MINHA BIBLIOTECA
        btnBibliotecaLateral.setOnClickListener {
            fecharMenuLateral()
            abrirBiblioteca()
        }

        // MINHAS COMPRAS
        btnComprasLateral.setOnClickListener {
            fecharMenuLateral()

            Toast.makeText(
                this,
                "Minhas compras será implementado na próxima etapa.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // CONFIGURAÇÕES
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

        // ==========================================
        // MENU LATERAL - VISITANTE
        // ==========================================

        btnEntrarLateral.setOnClickListener {
            fecharMenuLateral()
            abrirLogin()
        }

        btnCriarContaLateral.setOnClickListener {
            fecharMenuLateral()
            abrirCadastro()
        }

        // ==========================================
        // AJUDA E SUPORTE
        // ==========================================

        btnAjudaLateral.setOnClickListener {
            fecharMenuLateral()

            Toast.makeText(
                this,
                "Ajuda e suporte será implementado na próxima etapa.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // ==========================================
        // SAIR DA CONTA
        // ==========================================

        btnSairLateral.setOnClickListener {
            fecharMenuLateral()
            confirmarSaida()
        }
    }

    // ==============================================
    // ABRIR MINHA BIBLIOTECA
    // ==============================================

    private fun abrirBiblioteca() {

        if (!usuarioEstaLogado()) {
            mostrarLoginNecessario(
                "Entre na sua conta para acessar sua biblioteca."
            )
            return
        }

        startActivity(
            Intent(
                this,
                BibliotecaActivity::class.java
            )
        )
    }

    // ==============================================
    // MENU LATERAL
    // ==============================================

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

        if (!menuAberto) {
            return
        }

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

    // ==============================================
    // ATUALIZAR DADOS DO MENU LATERAL
    // ==============================================

    private fun atualizarMenuLateral() {

        val preferencias = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        val idUsuario = preferencias.getInt(
            "idUsuario",
            -1
        )

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
                if (nome.isNotEmpty()) {
                    nome
                } else {
                    "Jogador"
                }

            txtEmailUsuarioLateral.text =
                if (email.isNotEmpty()) {
                    email
                } else {
                    "Conta Loading Games"
                }

            btnContaCabecalhoLateral.text =
                "MINHA CONTA  ›"

            menuUsuarioLogado.visibility =
                View.VISIBLE

            menuUsuarioVisitante.visibility =
                View.GONE

            btnSairLateral.visibility =
                View.VISIBLE

        } else {

            txtNomeUsuarioLateral.text =
                "Olá, jogador!"

            txtEmailUsuarioLateral.text =
                "Entre para acessar sua conta"

            btnContaCabecalhoLateral.text =
                "ENTRAR  ›"

            menuUsuarioLogado.visibility =
                View.GONE

            menuUsuarioVisitante.visibility =
                View.VISIBLE

            btnSairLateral.visibility =
                View.GONE
        }
    }

    // ==============================================
    // BOTÃO VOLTAR
    // ==============================================

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (menuAberto) {
            fecharMenuLateral()
        } else {
            super.onBackPressed()
        }
    }

    // ==============================================
    // HOME
    // ==============================================

    private fun voltarParaInicio() {

        if (edtPesquisa.text.isNotEmpty()) {

            edtPesquisa.setText("")

        } else {

            txtTituloJogos.text = "Destaques"

            buscarJogosDaApi()
        }
    }

    private fun mostrarTodosOsJogos() {

        edtPesquisa.clearFocus()

        txtTituloJogos.text = "Todos os jogos"

        buscarJogosDaApi()
    }

    // ==============================================
    // AÇÕES RECEBIDAS DE OUTRAS TELAS
    // ==============================================

    private fun verificarAcoesRecebidas(
        intentRecebida: Intent
    ) {

        val abrirPesquisa =
            intentRecebida.getBooleanExtra(
                "abrirPesquisa",
                false
            )

        if (abrirPesquisa) {

            edtPesquisa.postDelayed(
                {
                    focarPesquisa()
                },
                250
            )
        }

        val categoria =
            intentRecebida.getStringExtra(
                "categoriaSelecionada"
            )

        if (!categoria.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Categoria selecionada: $categoria",
                Toast.LENGTH_SHORT
            ).show()
        }

        intentRecebida.removeExtra(
            "abrirPesquisa"
        )

        intentRecebida.removeExtra(
            "categoriaSelecionada"
        )
    }

    // ==============================================
    // PESQUISA
    // ==============================================

    private fun configurarPesquisa() {

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
                        s?.toString()?.trim().orEmpty()

                    if (pesquisa.isEmpty()) {

                        txtTituloJogos.text = "Destaques"

                        buscarJogosDaApi()

                    } else {

                        txtTituloJogos.text = "Resultados"

                        buscarJogosDaApi(pesquisa)
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun focarPesquisa() {

        edtPesquisa.requestFocus()

        edtPesquisa.setSelection(
            edtPesquisa.text.length
        )

        val teclado = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        teclado.showSoftInput(
            edtPesquisa,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    // ==============================================
    // CATEGORIAS
    // ==============================================

    private fun abrirCategorias() {

        startActivity(
            Intent(
                this,
                CategoriasActivity::class.java
            )
        )
    }

    // ==============================================
    // VERIFICAR LOGIN
    // ==============================================

    private fun usuarioEstaLogado(): Boolean {

        val preferencias = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )

        return preferencias.getInt(
            "idUsuario",
            -1
        ) != -1
    }

    // ==============================================
    // LOGIN
    // ==============================================

    private fun abrirLogin() {

        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )
    }

    // ==============================================
    // CADASTRO
    // ==============================================

    private fun abrirCadastro() {

        startActivity(
            Intent(
                this,
                CadastroActivity::class.java
            )
        )
    }

    // ==============================================
    // CARRINHO
    // ==============================================

    private fun abrirCarrinho() {

        if (!usuarioEstaLogado()) {

            mostrarLoginNecessario(
                "Entre na sua conta para acessar o carrinho."
            )

            return
        }

        startActivity(
            Intent(
                this,
                CarrinhoActivity::class.java
            )
        )
    }

    // ==============================================
    // FAVORITOS
    // ==============================================

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

    // ==============================================
    // PERFIL
    // ==============================================

    private fun abrirPerfil() {

        if (usuarioEstaLogado()) {

            abrirMinhaConta()

        } else {

            abrirLogin()
        }
    }

    // ==============================================
    // MINHA CONTA
    // ==============================================

    private fun abrirMinhaConta() {

        if (!usuarioEstaLogado()) {
            abrirLogin()
            return
        }

        val intent = Intent(
            this,
            MinhaContaActivity::class.java
        )

        startActivity(intent)
    }

    // ==============================================
    // AVISO DE LOGIN NECESSÁRIO
    // ==============================================

    private fun mostrarLoginNecessario(
        mensagem: String
    ) {

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

    // ==============================================
    // CONFIRMAR SAÍDA DA CONTA
    // ==============================================

    private fun confirmarSaida() {

        AlertDialog.Builder(this)
            .setTitle("Sair da conta")
            .setMessage(
                "Deseja realmente sair da sua conta?"
            )
            .setPositiveButton("SAIR") { _, _ ->

                sairDaConta()
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    // ==============================================
    // SAIR DA CONTA
    // ==============================================

    private fun sairDaConta() {

        getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        )
            .edit()
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

    // ==============================================
    // BANNER RESIDENT EVIL
    // ==============================================

    private fun abrirResidentEvil() {

        val residentEvil =
            jogosCarregados.firstOrNull { jogo ->

                jogo.nome.contains(
                    "Resident Evil",
                    ignoreCase = true
                )
            }

        if (residentEvil == null) {

            edtPesquisa.setText(
                "Resident Evil"
            )

            focarPesquisa()

            return
        }

        abrirDetalhes(
            idJogo = residentEvil.id,
            nome = residentEvil.nome,
            imagem = descobrirImagem(
                residentEvil.nome
            ),
            preco = formatarPreco(
                residentEvil.preco
            ),
            descricao =
                residentEvil.descricao
                    ?: "Sem descrição disponível.",
            classificacao =
                residentEvil.classificacaoIndicativa,
            dataLancamento =
                residentEvil.dataLancamento
        )
    }

    // ==============================================
    // BUSCAR JOGOS DA API
    // ==============================================

    private fun buscarJogosDaApi(
        pesquisa: String? = null
    ) {

        RetrofitClient.api
            .buscarJogos(pesquisa)
            .enqueue(

                object :
                    Callback<List<JogoResponse>> {

                    override fun onResponse(
                        call: Call<List<JogoResponse>>,
                        response: Response<List<JogoResponse>>
                    ) {

                        if (response.isSuccessful) {

                            val jogos =
                                response.body()
                                    ?: emptyList()

                            if (
                                pesquisa.isNullOrBlank()
                            ) {

                                jogosCarregados =
                                    jogos
                            }

                            Log.d(
                                "JOGOS_API",
                                "Jogos recebidos: ${jogos.size}"
                            )

                            exibirJogos(
                                jogos
                            )

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

                        Log.e(
                            "JOGOS_API",
                            "Erro de conexão",
                            t
                        )

                        Toast.makeText(
                            this@MainActivity,
                            "Não foi possível conectar ao servidor.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    // ==============================================
    // EXIBIR JOGOS NA TELA INICIAL
    // ==============================================

    private fun exibirJogos(
        jogos: List<JogoResponse>
    ) {

        containerJogos.removeAllViews()

        if (jogos.isEmpty()) {

            val mensagem =
                TextView(this)

            mensagem.text =
                "Nenhum jogo encontrado."

            mensagem.setTextColor(
                Color.parseColor(
                    "#8F93A2"
                )
            )

            mensagem.textSize =
                14f

            mensagem.setPadding(
                dp(4),
                dp(24),
                dp(30),
                dp(24)
            )

            containerJogos.addView(
                mensagem
            )

            return
        }

        jogos.forEach { jogo ->

            containerJogos.addView(
                criarCardJogo(jogo)
            )
        }
    }

    // ==============================================
    // CRIAR CARD DE CADA JOGO
    // ==============================================

    private fun criarCardJogo(
        jogo: JogoResponse
    ): LinearLayout {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setBackgroundColor(
            Color.parseColor(
                "#111421"
            )
        )

        card.isClickable = true
        card.isFocusable = true

        val parametrosCard =
            LinearLayout.LayoutParams(
                dp(165),
                dp(230)
            )

        parametrosCard.setMargins(
            0,
            0,
            dp(12),
            0
        )

        card.layoutParams =
            parametrosCard

        // CAPA DO JOGO

        val imagem =
            ImageView(this)

        imagem.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(125)
            )

        imagem.scaleType =
            ImageView.ScaleType.CENTER_CROP

        imagem.contentDescription =
            jogo.nome

        imagem.setImageResource(
            descobrirImagem(
                jogo.nome
            )
        )

        card.addView(
            imagem
        )

        // NOME DO JOGO

        val nome =
            TextView(this)

        nome.text =
            jogo.nome

        nome.setTextColor(
            Color.WHITE
        )

        nome.textSize = 14f

        nome.setTypeface(
            nome.typeface,
            Typeface.BOLD
        )

        nome.maxLines = 2

        nome.setPadding(
            dp(10),
            dp(9),
            dp(10),
            0
        )

        card.addView(
            nome
        )

        // CLASSIFICAÇÃO INDICATIVA

        val classificacao =
            TextView(this)

        classificacao.text =
            if (
                jogo.classificacaoIndicativa == 0
            ) {
                "Livre"
            } else {
                "${jogo.classificacaoIndicativa} anos"
            }

        classificacao.setTextColor(
            Color.parseColor(
                "#8F93A2"
            )
        )

        classificacao.textSize = 10f

        classificacao.setPadding(
            dp(10),
            dp(4),
            dp(10),
            0
        )

        card.addView(
            classificacao
        )

        // PREÇO DO JOGO

        val preco =
            TextView(this)

        preco.text =
            formatarPreco(
                jogo.preco
            )

        preco.setTextColor(
            Color.parseColor(
                "#C084FC"
            )
        )

        preco.textSize = 15f

        preco.setTypeface(
            preco.typeface,
            Typeface.BOLD
        )

        preco.setPadding(
            dp(10),
            dp(7),
            dp(10),
            0
        )

        card.addView(
            preco
        )

        // ABRIR DETALHES AO TOCAR NO CARD

        card.setOnClickListener {

            abrirDetalhes(
                idJogo = jogo.id,
                nome = jogo.nome,
                imagem = descobrirImagem(
                    jogo.nome
                ),
                preco = formatarPreco(
                    jogo.preco
                ),
                descricao =
                    jogo.descricao
                        ?: "Sem descrição disponível.",
                classificacao =
                    jogo.classificacaoIndicativa,
                dataLancamento =
                    jogo.dataLancamento
            )
        }

        return card
    }

    // ==============================================
    // IMAGENS DOS JOGOS
    // ==============================================

    private fun descobrirImagem(
        nomeJogo: String
    ): Int {

        return when {

            nomeJogo.contains(
                "Grand Theft Auto",
                ignoreCase = true
            ) ->
                R.drawable.capagtav

            nomeJogo.contains(
                "GTA",
                ignoreCase = true
            ) ->
                R.drawable.capagtav

            nomeJogo.contains(
                "Cyberpunk",
                ignoreCase = true
            ) ->
                R.drawable.capacyberpunk

            nomeJogo.contains(
                "Red Dead",
                ignoreCase = true
            ) ->
                R.drawable.capareddead

            nomeJogo.contains(
                "Witcher",
                ignoreCase = true
            ) ->
                R.drawable.capathewitcher

            nomeJogo.contains(
                "Minecraft",
                ignoreCase = true
            ) ->
                R.drawable.capaminecraft

            nomeJogo.contains(
                "Elden Ring",
                ignoreCase = true
            ) ->
                R.drawable.capaeldenring

            nomeJogo.contains(
                "God of War",
                ignoreCase = true
            ) ->
                R.drawable.god_of_war

            nomeJogo.contains(
                "FC 24",
                ignoreCase = true
            ) ->
                R.drawable.fc24

            nomeJogo.contains(
                "Hogwarts",
                ignoreCase = true
            ) ->
                R.drawable.hogwarts

            nomeJogo.contains(
                "Resident Evil",
                ignoreCase = true
            ) ->
                R.drawable.resident_evil

            else ->
                android.R.drawable.ic_menu_gallery
        }
    }

    // ==============================================
    // FORMATAR PREÇO EM REAIS
    // ==============================================

    private fun formatarPreco(
        preco: Double
    ): String {

        return NumberFormat
            .getCurrencyInstance(
                Locale(
                    "pt",
                    "BR"
                )
            )
            .format(
                preco
            )
    }

    // ==============================================
    // CONVERTER DP PARA PIXELS
    // ==============================================

    private fun dp(
        valor: Int
    ): Int {

        return (
                valor *
                        resources.displayMetrics.density
                ).toInt()
    }

    // ==============================================
    // ABRIR DETALHES DO JOGO
    // ==============================================

    private fun abrirDetalhes(
        idJogo: Int,
        nome: String,
        imagem: Int,
        preco: String,
        descricao: String,
        classificacao: Int,
        dataLancamento: String?
    ) {

        val intent = Intent(
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

        startActivity(intent)
    }
}
