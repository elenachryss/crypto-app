package com.example.cryptoapp.ui.watchlist

import com.example.cryptoapp.data.model.Coin

//WatchlistPickerItem = το "item" που θα δειχνουμε στο dialog picker
//Σκοπος του:
//1) Να κουβαλαει τα στοιχεια του Coin (name, symbol, imageUrl κτλ)
//2) Να ξερει αν ειναι ηδη στη watchlist (για να δειχνουμε check + disabled στο κουμπι)
data class WatchlistPickerItem(
    val coin: Coin,          //το κανονικο Coin που εχεις ηδη στο app
    val isAdded: Boolean     //true = υπαρχει ηδη στη watchlist, αρα δειχνουμε check + δεν επιτρεπουμε add
)


//Στο ViewModel θα κάνουμε κάτι τέτοιο:
//
//παίρνουμε όλα τα coins (από CoinsStore ή API)
//
//παίρνουμε όλα τα symbols της watchlist (από Room)
//
//φτιάχνουμε List<WatchlistPickerItem>:
//
//isAdded = true αν το symbol υπάρχει ήδη στη watchlist
//
//Και ο adapter θα λέει:
//
//αν isAdded == true → icon ic_check, isEnabled=false
//
//αλλιώς → icon ic_add, isEnabled=true
