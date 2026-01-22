package com.example.cryptoapp.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptoapp.data.repository.FavoritesRepository

//ViewModel για το CoinDetails:
//- κραταει το state του favorite (isFavorite)
//- μιλαει με το FavoritesRepository (Room)
class CoinDetailsViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    //φορτωνει αρχικο state (αν υπαρχει στη Room)
    fun load(symbol: String) {
        favoritesRepository.isFavorite(symbol) { fav ->
            _isFavorite.postValue(fav)
        }
    }

    //toggle -> βαζει/βγαζει και ενημερωνει το UI state
    fun toggle(symbol: String) {
        favoritesRepository.toggle(symbol) { newState ->
            _isFavorite.postValue(newState)
        }
    }
}
