package com.example.cryptoapp.data.store

//object = ena μοναδικο αντικείμενο
object FavoritesStore {

    //mutable γιατι τα στοιχεια αλλαζουν
    private val favoritesSymbols = mutableSetOf<String>()

    //κοιταει αν ενα coin ειναι favorite και επιστρεφει bool
    fun isFavorite(symbol: String): Boolean = favoritesSymbols.contains(symbol)


    //αλλαζει την κατασταση τον coin
    fun toggle(symbol: String): Boolean {
        return if (isFavorite(symbol)) {
            favoritesSymbols.remove(symbol)
            false
        } else {
            favoritesSymbols.add(symbol)
            true
        }
    }

    //αφαιρει κοιν απο αγαπημενα
    fun remove(symbol: String) {
        favoritesSymbols.remove(symbol)
    }

    //επιστρεφει ολα τα κοιν
    fun getAll(): Set<String> = favoritesSymbols
}