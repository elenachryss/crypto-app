package com.example.cryptoapp.data.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

//DAO = οι εντολες (queries) που θα κανουμε στη βαση
@Dao
interface FavoriteDao {

    @Query("SELECT symbol FROM favorites")
    fun getAllSymbols(): List<String>

    //επιστρεφει 1 αν υπαρχει το symbol, αλλιως null
    @Query("SELECT symbol FROM favorites WHERE symbol = :symbol LIMIT 1")
    fun exists(symbol: String): String?

    //βαζει favorite
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: FavoriteCoinEntity)

    //σβηνει favorite
    @Delete
    fun delete(item: FavoriteCoinEntity)

    //σβηνει με query
    @Query("DELETE FROM favorites WHERE symbol = :symbol")
    fun deleteBySymbol(symbol: String)

}