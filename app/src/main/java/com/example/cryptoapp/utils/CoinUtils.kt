package com.example.cryptoapp.utils

import com.example.cryptoapp.data.enum.TopMoversType
import com.example.cryptoapp.data.model.Coin

fun getSortedTopMovers(
    list: List<Coin>,
    type: TopMoversType,
    limit: Int? = null
): List<Coin> {
    val sorted = when (type) {
        TopMoversType.GAINERS -> list.sortedByDescending { it.change24hValue }
        TopMoversType.LOSERS -> list.sortedBy { it.change24hValue }
    }
    return if (limit != null) sorted.take(limit) else sorted
}
