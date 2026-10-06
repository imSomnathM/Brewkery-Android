package com.example.brewkery.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface BrewkeryApi {
    @GET("data.json")
    suspend fun getMenu(): MenuResponse
}

object ApiClient {
    val api: BrewkeryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/VivekShah138/Brewkery/main/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrewkeryApi::class.java)
    }
}
