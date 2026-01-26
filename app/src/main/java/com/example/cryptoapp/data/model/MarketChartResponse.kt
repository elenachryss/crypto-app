package com.example.cryptoapp.data.model

import com.google.gson.annotations.SerializedName

data class MarketChartResponse(
    // Το CoinGecko ονομάζει τη λίστα "prices", άρα πρέπει να έχουμε το ίδιο όνομα ή SerializedName
    @SerializedName("prices")
    val prices: List<List<Double>>
)