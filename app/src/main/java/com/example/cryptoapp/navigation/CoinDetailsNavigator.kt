package com.example.cryptoapp.navigation

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.ui.details.CoinDetailsActivity

object CoinDetailsNavigator {

    fun open(fragment: Fragment, coin: Coin) {
        val intent = Intent(fragment.requireContext(), CoinDetailsActivity::class.java).apply {
            putExtra("name", coin.name)
            putExtra("symbol", coin.symbol)
            putExtra("price", coin.price)
            putExtra("change", coin.change24h)
            putExtra("image", coin.image)
        }
        fragment.startActivity(intent)
    }

    fun open(activity: Activity, coin: Coin) {
        val intent = Intent(activity, CoinDetailsActivity::class.java).apply {
            putExtra("name", coin.name)
            putExtra("symbol", coin.symbol)
            putExtra("price", coin.price)
            putExtra("change", coin.change24h)
            putExtra("image", coin.image)
        }
        activity.startActivity(intent)
    }
}
