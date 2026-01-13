package com.example.cryptoapp.data.model

import com.google.gson.annotations.SerializedName

//1. φτιαχνω το dto
//@SerializedName Επειδή στο JSON τα keys είναι π.χ. "current_price" αλλά εσύ θες Kotlin property currentPrice
data class MarketCoinDto(
    @SerializedName("name") val name: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("price_change_percentage_24h") val change24h: Double?,
    @SerializedName("id") val id: String,
    @SerializedName("image") val image: String?
)