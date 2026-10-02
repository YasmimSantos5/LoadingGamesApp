package com.example.loadinggames.api

data class JogoResponse(
    val id: Int,
    val nome: String,
    val descricao: String?,
    val preco: Double,
    val imagemPrincipalUrl: String?,
    val dataLancamento: String?,
    val classificacaoIndicativa: Int
)