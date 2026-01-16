package com.example.cryptoapp.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptoapp.R
import com.example.cryptoapp.data.storage.database.DbProvider
import com.example.cryptoapp.data.storage.database.FavoriteCoinEntity
import com.example.cryptoapp.databinding.ActivityCoinDetailsBinding

class CoinDetailsActivity : AppCompatActivity() {

    //δημιουργουμε την κλαση απο το ΧΜΛ
    private lateinit var binding: ActivityCoinDetailsBinding

    //κραταμε το symbol για να το χρησιμοποιουμε στο favorite
    private lateinit var symbol: String

    //ti kaleitai otan anoigei h othoni
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Παντα αυτο το γραφουμε Πάρε το XML και φτιαξτο σε view
        binding = ActivityCoinDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Παίρνουμε τα δεδομένα από το Intent
        val name = intent.getStringExtra("name") ?: "-"
        symbol = intent.getStringExtra("symbol") ?: "-"
        val price = intent.getStringExtra("price") ?: "-"
        val change = intent.getStringExtra("change") ?: "-"

        // Τα δείχνουμε στο UI γεμιζουμε τα textView
        binding.tvName.text = name
        binding.tvSymbol.text = symbol
        binding.tvPrice.text = price
        binding.tvChange.text = "$change (24h)"

        //φορτωνουμε αν το coin ειναι ηδη favorite για να φτιαξουμε το icon
        loadFavoriteState()

        // click στο icon (βαζει ή βγαζει favorite)
        binding.ivFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    //ελεγχει αν το coin ειναι favorite και ενημερωνει το icon
    private fun loadFavoriteState() {
        Thread {
            val dao = DbProvider.getDb(this).favoriteDao()
            val isFav = dao.exists(symbol) != null

            runOnUiThread {
                binding.ivFavorite.setImageResource(
                    if (isFav)
                        android.R.drawable.btn_star_big_on
                    else
                        android.R.drawable.btn_star_big_off
                )
            }
        }.start()
    }

    //βαζει ή βγαζει το coin απο τα favorites
    private fun toggleFavorite() {
        Thread {
            val dao = DbProvider.getDb(this).favoriteDao()
            val isFav = dao.exists(symbol) != null

            if (isFav) {
                //αν ειναι favorite, το σβηνουμε
                dao.deleteBySymbol(symbol)
            } else {
                //αν δεν ειναι, το προσθετουμε
                dao.insert(FavoriteCoinEntity(symbol))
            }

            runOnUiThread {
                //αλλαζουμε το icon αναλογα με το νεο state
                binding.ivFavorite.setImageResource(
                    if (!isFav)
                        android.R.drawable.btn_star_big_on
                    else
                        android.R.drawable.btn_star_big_off
                )
            }
        }.start()
    }
}
