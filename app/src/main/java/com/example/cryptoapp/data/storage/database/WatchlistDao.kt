package com.example.cryptoapp.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//DAO = οι εντολες (queries) που θα κανουμε στη βαση για watchlist
@Dao
interface WatchlistDao {

    @Query("SELECT symbol FROM watchlist")
    fun getAllSymbols(): List<String>

    @Query("SELECT symbol FROM watchlist WHERE symbol = :symbol LIMIT 1")
    fun exists(symbol: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: WatchlistCoinEntity)

    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
    fun deleteBySymbol(symbol: String)
}
