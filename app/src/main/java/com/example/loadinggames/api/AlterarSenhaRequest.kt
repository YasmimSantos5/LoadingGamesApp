
package com.example.loadinggames.api

data class AlterarSenhaRequest(
    val idUsuario: Int,
    val senhaAtual: String,
    val novaSenha: String
)
