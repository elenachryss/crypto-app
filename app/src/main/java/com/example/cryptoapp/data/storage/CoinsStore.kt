package com.example.cryptoapp.data.storage

import com.example.cryptoapp.data.model.Coin

// για να μπορουμε να φιλτραρουμε χωρις να φτιαξουμε ακομα databasε
//φτιαχνουμε μια λιστα απο coins poy περναμε τα coins poy mas erxontai
//kai filtraroume gia ta favourites edw
//Αυτό απλά θυμάται “τα τελευταία coins”
object CoinsStore {
    private var coins: List<Coin> = emptyList()

    fun setCoins(newCoins: List<Coin>) {
        coins = newCoins
    }

    fun getCoins(): List<Coin> = coins
}