package com.example.cryptoapp.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.data.repository.FavoritesRepository

//ViewModel = κραταει την λογικη + state (data) για το Favorites tab
class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    //κραταμε ΟΛΑ τα favorite coins (για να μπορουμε να κανουμε φιλτραρισμα)
    private var allFavCoins: List<Coin> = emptyList()

    //αυτη ειναι η λιστα που βλεπει το UI (μετα απο filter)
    private val _visibleFavCoins = MutableLiveData<List<Coin>>(emptyList())
    val visibleFavCoins: LiveData<List<Coin>> = _visibleFavCoins

    //φορτωνει τα favorites απο τη Room και τα φιλτραρει πανω στα coins που εχουμε (cached από CoinsStore)
    fun loadFavorites(allCoins: List<Coin>) {
        favoritesRepository.getAllSymbols { symbols ->
            val favSymbols = symbols.toSet()

            //τα φιλτραρει ολα και κραταει οσα εχουν symbol μέσα στα favorites
            val favCoins = allCoins.filter { favSymbols.contains(it.symbol) }

            allFavCoins = favCoins
            _visibleFavCoins.postValue(favCoins)

        }
    }

    //λογικη για φιλτραρισμα (θα την καλει το Fragment οταν γραφουμε στο search)
    fun filterCoins(query: String) {
        val q = query.trim()
        if (q.isEmpty()) {
            _visibleFavCoins.postValue(allFavCoins)
            return
        }

        val filtered = allFavCoins.filter {
            it.name.contains(q, ignoreCase = true) ||
                    it.symbol.contains(q, ignoreCase = true)
        }

        _visibleFavCoins.postValue(filtered)
    }
}
