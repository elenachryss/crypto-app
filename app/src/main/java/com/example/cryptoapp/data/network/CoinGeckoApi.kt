package com.example.cryptoapp.data.network

import com.example.cryptoapp.data.model.MarketCoinDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//2. είναι ένα Retrofit Service Interface:
//δηλώνεις endpoints σαν Kotlin functions και
// το Retrofit φτιάχνει από πίσω τον κώδικα που κάνει το HTTP request.
//εδω θα γραψω ολα τα get που θελουμε.
interface CoinGeckoApi {

    //πρωτα κανουμε HTTP GET Στο path που του δινουμε
    //θα παει να κανει το  get πανω στο base url που εχουμε γραψει στο client
    //εκει του εχουμε δωσει το https://api.coingecko.com/
    @GET("api/v3/coins/markets")

    //Αυτή η function περιγράφει:
    //Θα πάρεις πίσω μια λίστα από MarketCoinDto
    //Τυλιγμένη σε Call<> για να την εκτελέσεις async με callback.
    fun getMarkets(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): Call<List<MarketCoinDto>>
}