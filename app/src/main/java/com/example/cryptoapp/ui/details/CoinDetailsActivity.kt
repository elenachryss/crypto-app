package com.example.cryptoapp.ui.details

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptoapp.data.store.FavoritesStore
import com.example.cryptoapp.databinding.ActivityCoinDetailsBinding

class CoinDetailsActivity : AppCompatActivity() {

    //δημιουργουμε την κλαση απο το ΧΜΛ
    private lateinit var binding: ActivityCoinDetailsBinding

    //ti kaleitai otan anoigei h othoni
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Παντα αυτο το γραφουμε Πάρε το XML και φτιαξτο σε view
        binding = ActivityCoinDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Παίρνουμε τα δεδομένα από το Intent
        val name = intent.getStringExtra("name") ?: "-"
        val symbol = intent.getStringExtra("symbol") ?: "-"
        val price = intent.getStringExtra("price") ?: "-"
        val change = intent.getStringExtra("change") ?: "-"

        // Τα δείχνουμε στο UI γεμιζουμε τα textView
        binding.tvName.text = name
        binding.tvSymbol.text = symbol
        binding.tvPrice.text = price
        binding.tvChange.text = "$change (24h)"

        //για να φτιαξουμε το icon
        val isFav = FavoritesStore.isFavorite(symbol)
        binding.ivFavorite.setImageResource(
            if (isFav)
                R.drawable.btn_star_big_on
            else
                R.drawable.btn_star_big_off
        )

        // click στο icon
        binding.ivFavorite.setOnClickListener {
            val nowFav = FavoritesStore.toggle(symbol)

            binding.ivFavorite.setImageResource(
                if (nowFav)
                    R.drawable.btn_star_big_on
                else
                    R.drawable.btn_star_big_off
            )
        }
    }
}