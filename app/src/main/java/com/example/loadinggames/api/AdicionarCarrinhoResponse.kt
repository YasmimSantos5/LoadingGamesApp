package com.example.loadinggames.api

data class AdicionarCarrinhoResponse(
    val mensagem: String,
    val idCarrinho: Int?,
    val idJogo: Int?,
    val nomeJogo: String?
)