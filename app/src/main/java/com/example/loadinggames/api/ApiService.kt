
package com.example.loadinggames.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // LOGIN
    @POST("api/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    // CADASTRO
    @POST("api/auth/cadastro")
    fun cadastrar(
        @Body request: CadastroRequest
    ): Call<CadastroResponse>

    // ATUALIZAR PERFIL
    @PUT("api/auth/perfil")
    fun atualizarPerfil(
        @Body request: AtualizarPerfilRequest
    ): Call<AtualizarPerfilResponse>

    // JOGOS
    @GET("api/jogos")
    fun buscarJogos(
        @Query("pesquisa") pesquisa: String? = null
    ): Call<List<JogoResponse>>

    // ADICIONAR AO CARRINHO
    @POST("api/carrinho/adicionar")
    fun adicionarAoCarrinho(
        @Body request: AdicionarCarrinhoRequest
    ): Call<AdicionarCarrinhoResponse>

    // BUSCAR CARRINHO
    @GET("api/carrinho/{idUsuario}")
    fun buscarCarrinho(
        @Path("idUsuario") idUsuario: Int
    ): Call<CarrinhoResponse>

    // REMOVER ITEM DO CARRINHO
    @DELETE("api/carrinho/item/{idItemCarrinho}")
    fun removerItemCarrinho(
        @Path("idItemCarrinho") idItemCarrinho: Int,
        @Query("idUsuario") idUsuario: Int
    ): Call<RemoverCarrinhoResponse>

    // FINALIZAR COMPRA
    @POST("api/compras/finalizar")
    fun finalizarCompra(
        @Body request: FinalizarCompraRequest
    ): Call<FinalizarCompraResponse>

    // ALTERAR SENHA
    @PUT("api/auth/alterar-senha")
    fun alterarSenha(
        @Body request: AlterarSenhaRequest
    ): Call<AlterarSenhaResponse>


    // MINHA BIBLIOTECA
    @GET("api/biblioteca/{idUsuario}")
    fun buscarBiblioteca(
        @Path("idUsuario") idUsuario: Int
    ): Call<BibliotecaResponse>


}
