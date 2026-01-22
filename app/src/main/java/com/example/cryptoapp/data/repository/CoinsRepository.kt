package com.example.cryptoapp.data.repository

import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.data.model.MarketCoinDto
import com.example.cryptoapp.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Repository = το μέρος που μιλάει με το api
//είναι mono data logic
class CoinsRepository {

    //κανουν φορματ τους αριθμους
    private fun formatPriceUsd(price: Double): String {
        return "$" + String.format("%,.2f", price)
    }

    private fun formatPercent(value: Double?): String {
        if (value == null) return "-"
        val sign = if (value >= 0) "+" else ""
        return sign + String.format("%.2f", value) + "%"
    }

    //κανει fetch τα coins απο το API
    //onSuccess επιστρεφει List
    //onError επιστρεφει λαθος
    fun fetchCoins(
        onSuccess: (List<Coin>) -> Unit,
        onError: (String) -> Unit
    ) {
        RetrofitClient.api.getMarkets().enqueue(object : Callback<List<MarketCoinDto>> {

            override fun onResponse(
                call: Call<List<MarketCoinDto>>,
                response: Response<List<MarketCoinDto>>
            ) {
                if (!response.isSuccessful) {
                    onError("API error: ${response.code()}")
                    return
                }

                val dtoList = response.body() ?: emptyList()

                //map dto -> Coin (αυτο ειναι το model”
                // που χρησιμοποιει το UI)
                val coins = dtoList.map { dto ->
                    Coin(
                        name = dto.name,
                        symbol = dto.symbol.uppercase(),
                        price = formatPriceUsd(dto.currentPrice),
                        change24h = formatPercent(dto.change24h),
                        change24hValue= dto.change24h,
                        image = dto.image.orEmpty()
                    )
                }

                onSuccess(coins)
            }

            override fun onFailure(call: Call<List<MarketCoinDto>>, t: Throwable) {
                onError("Network error: ${t.message ?: "unknown"}")
            }
        })
    }
}
