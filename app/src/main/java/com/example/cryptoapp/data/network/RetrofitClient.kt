package com.example.cryptoapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//το γραφουμε μια φορα αυτο object = singleton Δηλαδή υπάρχει 1 μόνο instance σε όλο το app
//λεμε ποιο api θα χτυπησουμε
object RetrofitClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/")
        //όταν έρθει JSON από το API, χρησιμοποίησε Gson για να το μετατρέψεις σε Kotlin objects
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Δημιουργία του API interface
    val api: CoinGeckoApi = retrofit.create(CoinGeckoApi::class.java)
}