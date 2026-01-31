package com.example.cryptoapp.ui.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import coil.load
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import com.example.cryptoapp.R
import com.example.cryptoapp.data.network.RetrofitClient
import com.example.cryptoapp.data.repository.FavoritesRepository
import com.example.cryptoapp.databinding.ActivityCoinDetailsBinding


class CoinDetailsActivity : AppCompatActivity() {

    //δημιουργουμε την κλαση απο το ΧΜΛ
    private lateinit var binding: ActivityCoinDetailsBinding

    //κραταμε το symbol για να το χρησιμοποιουμε στο favorite
    private lateinit var symbol: String

    //κραταμε reference στο menu item για να αλλαζουμε icon
    private var favMenuItem: MenuItem? = null

    //κραταμε το ΤΕΛΕΥΤΑΙΟ state για να μπορουμε να βαλουμε σωστο icon ακομα κι αν το menu δεν εχει φτιαχτει
    private var latestIsFav: Boolean = false

    //ViewModel για το details (κραταει state + μιλαει με repository)
    private lateinit var viewModel: CoinDetailsViewModel

    //ti kaleitai otan anoigei h othoni
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        //Παντα αυτο το γραφουμε Πάρε το XML και φτιαξτο σε view
        binding = ActivityCoinDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // gia na mhn akoybaei to layout xvris na valoume padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // toolbar -> app bar
        setSupportActionBar(binding.toolbar)

        // back βελάκι αριστερά
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Παίρνουμε τα δεδομένα από το Intent
        val name = intent.getStringExtra("name") ?: "-"
        symbol = intent.getStringExtra("symbol") ?: "-"
        val price = intent.getStringExtra("price") ?: "-"
        val change = intent.getStringExtra("change") ?: "-"
        val image = intent.getStringExtra("image") ?: ""

        println("im on  the $name")

        // Τα δείχνουμε στο UI γεμιζουμε τα textView
        binding.tvName.text = name
        binding.tvSymbol.text = symbol
        binding.tvPrice.text = price
        binding.tvChange.text = "$change (24h)"
        binding.ivCoin.load(image)

        binding.detailsBtnOneHour.setOnClickListener {
            println("1  ωρα details")
        }
        binding.detailsBtnFiveHours.setOnClickListener {
            println("5  ώρες  details")
        }
        binding.detailsBtnFiveHours.setOnClickListener {
            println("1  μέρα details")
        }

        // (προαιρετικο) αν εχεις ImageView για coin icon και εχεις βαλει Coil/Glide θα το γεμισεις εδω
        // binding.ivCoin.load(image)

        //φτιαχνουμε το ViewModel με Repository (χωρις να βαλουμε MVVM frameworks)
        val repo = FavoritesRepository(applicationContext)
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CoinDetailsViewModel(repo) as T
                }
            }
        )[CoinDetailsViewModel::class.java]

        //ακουει το favorite state και ενημερωνει το icon σωστα
        viewModel.isFavorite.observe(this) { isFav ->
            latestIsFav = isFav

            //αν εχει ηδη φτιαχτει το menu item, αλλαζουμε icon αμεσα
            if (favMenuItem != null) {
                updateFavIcon(isFav)
            } else {
                //αλλιως ζηταμε να ξαναφτιαχτει το menu για να παρει το σωστο icon
                invalidateOptionsMenu()
            }
        }

        //φορτωνουμε αρχικο state για να ξερουμε αν ειναι ηδη favorite
        viewModel.load(symbol)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_coin_details, menu)

        //κραταμε reference στο item
        favMenuItem = menu.findItem(R.id.action_favorite)

        //ΜΟΛΙΣ δημιουργηθει το menu, βαζουμε το σωστο icon με βαση το state που ηδη ξερουμε
        updateFavIcon(latestIsFav)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // back
                finish()
                true
            }

            R.id.action_favorite -> { // heart click
                viewModel.toggle(symbol)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //αλλαζει το icon αναλογα με το state
    private fun updateFavIcon(isFav: Boolean) {
        favMenuItem?.setIcon(
            if (isFav) R.drawable.ic_favorite_red else R.drawable.ic_favorite
        )
    }
}
