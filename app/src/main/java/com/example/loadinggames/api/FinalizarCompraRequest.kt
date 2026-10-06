package com.example.loadinggames.api

data class FinalizarCompraRequest(
    val idUsuario: Int,
    val formaPagamento: String
)