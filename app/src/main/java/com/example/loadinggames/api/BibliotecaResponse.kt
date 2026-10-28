
package com.example.loadinggames.api

data class BibliotecaResponse(
    val mensagem: String,
    val totalJogos: Int,
    val jogos: List<JogoBibliotecaResponse>
)

data class JogoBibliotecaResponse(
    val idJogo: Int,
    val nome: String,
    val descricao: String?,
    val imagemPrincipalUrl: String?,
    val dataAquisicao: String,
    val idCompra: Int
)
