package com.example.loadinggames.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
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

    @GET("api/jogos")
    fun buscarJogos(
        @Query("pesquisa") pesquisa: String? = null
    ): Call<List<JogoResponse>>

}
