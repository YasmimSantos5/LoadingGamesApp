
package com.example.loadinggames.api

data class AtualizarPerfilResponse(
    val mensagem: String,
    val usuario: UsuarioPerfilResponse
)

data class UsuarioPerfilResponse(
    val id: Int,
    val nome: String,
    val nomeUsuario: String,
    val email: String,
    val fotoPerfilUrl: String?,
    val tipoUsuario: String
)
