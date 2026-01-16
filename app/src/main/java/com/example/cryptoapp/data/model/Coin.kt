package com.example.cryptoapp.data.model

//Το Coin υπάρχει για να είναι το κοινό “νόμισμα” επικοινωνίας ανάμεσα σε API, UI, Adapter και Fragments.
//για να μην περναμε οαντου ολο το MarketCoinDto εχουμε αυτο γιατι εχει  fields που δεν χρειάζontai
// kai einai demeno kai me to api
data class Coin(
    val name: String,
    val symbol: String,
    val price: String,
    val change24h: String
)