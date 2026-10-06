package com.example.loadinggames.api

data class FinalizarCompraResponse(
    val mensagem: String,
    val idCompra: Int,
    val valorTotal: Double,
    val formaPagamento: String,
    val statusCompra: String
)