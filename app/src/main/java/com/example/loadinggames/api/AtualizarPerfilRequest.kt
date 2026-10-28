
package com.example.loadinggames.api

data class AtualizarPerfilRequest(
    val idUsuario: Int,
    val nome: String,
    val nomeUsuario: String,
    val email: String,
    val senhaAtual: String
)
