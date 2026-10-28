
package com.example.loadinggames

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loadinggames.api.CarrinhoResponse
import com.example.loadinggames.api.FinalizarCompraRequest
import com.example.loadinggames.api.FinalizarCompraResponse
import com.example.loadinggames.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class PagamentoActivity : AppCompatActivity() {

    private lateinit var txtItens: TextView
    private lateinit var txtTotal: TextView
    private lateinit var btnPagar: Button
    private lateinit var grupoPagamento: RadioGroup

    private var quantidadeItens = 0
    private var totalCompra = 0.0
    private var carrinhoCarregado = false

    private val idUsuario: Int
        get() = getSharedPreferences(
            "LoadingGamesPrefs",
            MODE_PRIVATE
        ).getInt("idUsuario", -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento)

        txtItens = findViewById(R.id.txtItensPagamento)
        txtTotal = findViewById(R.id.txtTotalPagamento)
        btnPagar = findViewById(R.id.btnPagar)
        grupoPagamento = findViewById(R.id.grupoPagamento)

        findViewById<TextView>(
            R.id.btnVoltarPagamento
        ).setOnClickListener {
            finish()
        }

        grupoPagamento.setOnCheckedChangeListener { _, _ ->
            atualizarBotao()
        }

        btnPagar.isEnabled = false

        btnPagar.setOnClickListener {
            finalizarPagamento()
        }

        carregarResumo()
    }

    private fun formaSelecionada(): String {
        return when (grupoPagamento.checkedRadioButtonId) {
            R.id.radioCartao -> "Cartão de crédito"
            R.id.radioBoleto -> "Boleto bancário"
            else -> "Pix"
        }
    }

    private fun atualizarBotao() {
        btnPagar.text = when (grupoPagamento.checkedRadioButtonId) {
            R.id.radioCartao -> "Pagar com Cartão"
            R.id.radioBoleto -> "Pagar com Boleto"
            else -> "Pagar com Pix"
        }
    }

    private fun carregarResumo() {
        if (idUsuario == -1) {
            Toast.makeText(
                this,
                "Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        btnPagar.isEnabled = false
        carrinhoCarregado = false

        RetrofitClient.api.buscarCarrinho(idUsuario)
            .enqueue(object : Callback<CarrinhoResponse> {

                override fun onResponse(
                    call: Call<CarrinhoResponse>,
                    response: Response<CarrinhoResponse>
                ) {
                    val carrinho = response.body()

                    if (!response.isSuccessful || carrinho == null) {
                        mostrarErro("Não foi possível carregar o carrinho.")
                        return
                    }

                    quantidadeItens = carrinho.itens.size
                    totalCompra = carrinho.total
                    carrinhoCarregado = true

                    txtItens.text = if (quantidadeItens == 1) {
                        "1 item"
                    } else {
                        "$quantidadeItens itens"
                    }

                    txtTotal.text = formatarPreco(totalCompra)
                    btnPagar.isEnabled = quantidadeItens > 0
                }

                override fun onFailure(
                    call: Call<CarrinhoResponse>,
                    t: Throwable
                ) {
                    mostrarErro("Erro de conexão: ${t.message}")
                }
            })
    }

    private fun finalizarPagamento() {
        if (!carrinhoCarregado || quantidadeItens == 0) {
            mostrarErro("Seu carrinho está vazio.")
            return
        }

        val forma = formaSelecionada()
        btnPagar.isEnabled = false
        btnPagar.text = "Processando..."

        val request = FinalizarCompraRequest(
            idUsuario = idUsuario,
            formaPagamento = forma
        )

        RetrofitClient.api.finalizarCompra(request)
            .enqueue(object : Callback<FinalizarCompraResponse> {

                override fun onResponse(
                    call: Call<FinalizarCompraResponse>,
                    response: Response<FinalizarCompraResponse>
                ) {
                    val compra = response.body()

                    if (response.isSuccessful && compra != null) {
                        val intent = Intent(
                            this@PagamentoActivity,
                            SucessoCompraActivity::class.java
                        )

                        intent.putExtra("idCompra", compra.idCompra.toString())
                        intent.putExtra("quantidadeItens", quantidadeItens)
                        intent.putExtra("totalCompra", totalCompra)
                        intent.putExtra("formaPagamento", forma)

                        startActivity(intent)
                        finish()
                    } else {
                        btnPagar.isEnabled = true
                        atualizarBotao()
                        mostrarErro(
                            "Não foi possível finalizar. Código: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<FinalizarCompraResponse>,
                    t: Throwable
                ) {
                    btnPagar.isEnabled = true
                    atualizarBotao()

                    mostrarErro(
                        "Não foi possível confirmar a compra. " +
                                "Verifique a biblioteca antes de tentar novamente."
                    )
                }
            })
    }

    private fun mostrarErro(mensagem: String) {
        Toast.makeText(
            this,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun formatarPreco(valor: Double): String {
        return NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        ).format(valor)
    }
}
