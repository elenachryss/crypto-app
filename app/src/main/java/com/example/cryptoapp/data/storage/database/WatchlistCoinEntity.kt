package com.example.cryptoapp.data.storage.database

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity = 1 row στον πινακα watchlist
//Κραταμε μονο το symbol (BTC, ETH κτλ)
@Entity(tableName = "watchlist")
data class WatchlistCoinEntity(
    @PrimaryKey val symbol: String
)
