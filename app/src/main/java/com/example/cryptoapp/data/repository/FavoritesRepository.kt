package com.example.cryptoapp.data.repository

import android.content.Context
import com.example.cryptoapp.data.storage.database.DbProvider
import com.example.cryptoapp.data.storage.database.FavoriteCoinEntity

//Repository = το μέρος που μιλάει με τη βαση (Room)
//threads edw?
class FavoritesRepository(private val context: Context) {

    //φερνει ολα τα favorite symbols
    fun getAllSymbols(onResult: (List<String>) -> Unit) {
        Thread {
            val dao = DbProvider.getDb(context).favoriteDao()
            val result = dao.getAllSymbols()
            onResult(result)
        }.start()
    }

    //ελεγχει αν ενα symbol ειναι favorite
    fun isFavorite(symbol: String, onResult: (Boolean) -> Unit) {
        Thread {
            val dao = DbProvider.getDb(context).favoriteDao()
            val exists = dao.exists(symbol) != null
            onResult(exists)
        }.start()
    }

    //κανει toggle favorite (αν ειναι -> remove, αν δεν ειναι -> add)
    //επιστρεφει το νεο state (true = favorite)
    fun toggle(symbol: String, onResult: (Boolean) -> Unit) {
        Thread {
            val dao = DbProvider.getDb(context).favoriteDao()
            val isFav = dao.exists(symbol) != null

            if (isFav) {
                dao.deleteBySymbol(symbol)
                onResult(false)
            } else {
                dao.insert(FavoriteCoinEntity(symbol))
                onResult(true)
            }
        }.start()
    }
}
