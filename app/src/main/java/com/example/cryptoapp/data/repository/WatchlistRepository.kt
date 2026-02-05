package com.example.cryptoapp.data.repository

import android.content.Context
import com.example.cryptoapp.data.storage.database.DbProvider
import com.example.cryptoapp.data.storage.database.WatchlistCoinEntity

//Repository = το μέρος που μιλάει με τη βαση (Room) για watchlist
//Ολα τα Thread μενουν εδω, για να μη γεμιζουν τα Activities/Fragments
class WatchlistRepository(private val context: Context) {

    //φερνει ολα τα watchlist symbols
    fun getAllSymbols(onResult: (List<String>) -> Unit) {
        Thread {
            val dao = DbProvider.getDb(context).watchlistDao()
            onResult(dao.getAllSymbols())
        }.start()
    }


    //ελεγχει αν ενα symbol ειναι watchlist
    fun isInWatchlist(symbol: String, onResult: (Boolean) -> Unit) {
        Thread {
            val dao = DbProvider.getDb(context).watchlistDao()
            onResult(dao.exists(symbol) != null)
        }.start()
    }

    //προσθετει στο watchlist
    fun add(symbol: String, onResult: () -> Unit) {
        Thread {
            val dao = DbProvider.getDb(context).watchlistDao()
            dao.insert(WatchlistCoinEntity(symbol))
            onResult()
        }.start()
    }

    //σβηνει απο watchlist
    fun remove(symbol: String, onResult: () -> Unit) {
        Thread {
            val dao = DbProvider.getDb(context).watchlistDao()
            dao.deleteBySymbol(symbol)
            onResult()
        }.start()
    }
}


