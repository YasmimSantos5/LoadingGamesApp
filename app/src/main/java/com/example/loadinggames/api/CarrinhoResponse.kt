package com.example.loadinggames.api

data class CarrinhoResponse(
    val idCarrinho: Int,
    val itens: List<ItemCarrinhoResponse>,
    val total: Double
)