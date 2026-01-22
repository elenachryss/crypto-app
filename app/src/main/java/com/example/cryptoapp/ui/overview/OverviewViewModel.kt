package com.example.cryptoapp.ui.overview

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.data.repository.CoinsRepository
import com.example.cryptoapp.data.storage.CoinsStore

//ViewModel = κρατάει state (coins/loading/error) για το Overview UI
class OverviewViewModel : ViewModel() {

    private val repo = CoinsRepository()

    //κραταμε ολα τα coins που ηρθαν απο το API για να μπορουμε να κανουμε φιλτραρισμα
    private var allCoins: List<Coin> = emptyList()

    //λίστα που βλέπει το UI
    private val _coins = MutableLiveData<List<Coin>>()
    val coins: LiveData<List<Coin>> = _coins

    //για το swipe refresh spinner
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    //error με Toast απο το fragment
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    //πρωτη φορα που ανοιγει:
    // 1) αν εχουμε ηδη coins στο store (cache) -> τα δινουμε χωρις API
    // 2) αλλιως -> κανουμε API call 1 φορα
    fun loadInitial() {
        val cached = CoinsStore.getCoins()
        if (cached.isNotEmpty()) {
            allCoins = cached
            _coins.value = cached
        } else {
            fetchCoins(forceRefresh = false)
        }
    }

    fun testFunction(){
        println("test")
    }

    //εδω ειναι το API call, για να το καλουμε και στην αρχη και στο swipe refresh
    fun fetchCoins(forceRefresh: Boolean) {
        // αν ΔΕΝ ειναι swipe refresh και εχουμε ηδη δεδομενα, δεν ξανακαλουμε το API
        if (!forceRefresh && CoinsStore.getCoins().isNotEmpty()) return

        _loading.value = true
        _error.value = null

        repo.fetchCoins(
            onSuccess = { list ->
                _loading.postValue(false)

                //setaroume ta coins apo to response stin lista για να τα βλεπει και το FavoritesFragment
                CoinsStore.setCoins(list)

                //κραταμε ολα τα coins για το search filter
                allCoins = list

                //δινουμε τη λιστα στο UI
                _coins.postValue(list)
            },
            onError = { msg ->
                _loading.postValue(false)
                _error.postValue(msg)
            }
        )
    }

    //λογικη για φιλτραρισμα την λιστα με τα coins poy εμφανιζεται στην overview otan kanoume search
    fun filterCoins(query: String) {
        val q = query.trim()
        if (q.isEmpty()) {
            _coins.value = allCoins
            return
        }

        val filtered = allCoins.filter {
            it.name.contains(q, ignoreCase = true) ||
                    it.symbol.contains(q, ignoreCase = true)
        }

        //στελνουμε τη φιλτραρισμενη λιστα στον adapter
        _coins.value = filtered.toList()
    }
}
