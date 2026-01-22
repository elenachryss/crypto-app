package com.example.cryptoapp.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//Shared ViewModel = κραταει το query του search για ολο το dashboard
class SearchViewModel : ViewModel() {

    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    //το καλει το MainActivity οταν γραφεις στο search
    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    //το καλει το MainActivity οταν κανουμε reset/close
    fun clear() {
        _query.value = ""
    }
}
