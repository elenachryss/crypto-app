package com.example.cryptoapp.ui.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.data.repository.CoinsRepository
import com.example.cryptoapp.data.repository.WatchlistRepository
import com.example.cryptoapp.data.storage.CoinsStore

//εδώ κρατάμε symbols στη DB και εμφανίζουμε πλήρη Coin φιλτραρισμένα πάνω στα cached coins.
//ViewModel = κραταει state + logic για το Watchlist tab
class WatchlistViewModel(
    private val watchlistRepo: WatchlistRepository
) : ViewModel() {

    private val coinsRepo = CoinsRepository()

    //κραταμε ΟΛΑ τα coins (cache) για να διαλεγει ο χρηστης τι θα προσθεσει
    private var allCoins: List<Coin> = emptyList()

    //η λιστα που βλεπει το UI (coins που ειναι στο watchlist)
    private val _watchlistCoins = MutableLiveData<List<Coin>>(emptyList())
    val watchlistCoins: LiveData<List<Coin>> = _watchlistCoins

    //για loading (πχ οταν χρειαστει να ξανακανει fetch coins)
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    //αν θες να δειχνεις μηνυμα/Toast απο Fragment
    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    //φορτωνει τη watchlist λιστα (με βάση τα coins που εχουμε στο store)
    fun loadWatchlist() {
        //παιρνουμε τα coins απο cache (ή θα ειναι empty αν δεν εχει γινει fetch ποτε)
        val cached = CoinsStore.getCoins()
        allCoins = cached

        //φερνουμε τα symbols απο τη Room και φιλτραρουμε πάνω στο cache
        watchlistRepo.getAllSymbols { symbols ->
            val set = symbols.toSet()
            val list = allCoins.filter { set.contains(it.symbol) }
            _watchlistCoins.postValue(list)
        }
    }

    //ετοιμαζει τη λιστα με coins για το "Add to Watchlist"
    //αν δεν υπαρχουν coins cached -> κανει fetch και γεμιζει CoinsStore
    fun loadCoinsForPicker(onReady: (List<Coin>) -> Unit) {
        val cached = CoinsStore.getCoins()
        if (cached.isNotEmpty()) {
            allCoins = cached
            onReady(cached)
            return
        }

        _loading.postValue(true)
        _message.postValue(null)

        coinsRepo.fetchCoins(
            onSuccess = { list ->
                _loading.postValue(false)

                //γεμιζουμε το cache για ολο το app
                CoinsStore.setCoins(list)
                allCoins = list

                onReady(list)
            },
            onError = { msg ->
                _loading.postValue(false)
                _message.postValue(msg)
                onReady(emptyList())
            }
        )
    }

    //Φτιαχνει items για το dialog:
    //Coin + boolean (αν ειναι ηδη added στη watchlist -> θα δειξεις check/disabled)
    fun loadPickerItems(onResult: (List<WatchlistPickerItem>) -> Unit) {
        //1) σιγουρευομαστε οτι εχουμε coins (απο cache ή απο API)
        loadCoinsForPicker { coins ->
            if (coins.isEmpty()) {
                onResult(emptyList())
                return@loadCoinsForPicker
            }

            //2) παιρνουμε τα watchlist symbols απο τη Room
            watchlistRepo.getAllSymbols { symbols ->
                val set = symbols.toSet()

                //3) φτιαχνουμε items: για καθε coin -> isAdded = υπαρχει στη watchlist?
                val items = coins.map { c ->
                    WatchlistPickerItem(
                        coin = c,
                        isAdded = set.contains(c.symbol)
                    )
                }

                onResult(items)
            }
        }
    }

    //προσθετει coin στο watchlist και οταν τελειωσει, κανει refresh + callback
    fun addToWatchlist(symbol: String, onDone: (() -> Unit)? = null) {
        watchlistRepo.add(symbol) {
            loadWatchlist()      // refresh για το watchlist tab
            onDone?.invoke()     // ενημερωσε το dialog ΜΟΝΟ αφου γραφτηκε στη DB
        }
    }


    //αφαιρει coin απο watchlist και κανει refresh τη λιστα
    fun removeFromWatchlist(symbol: String) {
        watchlistRepo.remove(symbol) {
            //refresh watchlist list
            loadWatchlist()
        }
    }
}
