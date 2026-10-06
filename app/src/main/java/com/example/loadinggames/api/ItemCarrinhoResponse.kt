package com.example.loadinggames.api

data class ItemCarrinhoResponse(
    val idItemCarrinho: Int,
    val idJogo: Int,
    val nome: String,
    val preco: Double,
    val imagemPrincipalUrl: String?,
    val classificacaoIndicativa: Int,
    val dataAdicao: String?
)