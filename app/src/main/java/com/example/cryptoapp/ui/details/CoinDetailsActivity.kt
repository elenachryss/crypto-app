package com.example.cryptoapp.ui.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.bumptech.glide.Glide
import com.example.cryptoapp.R
import com.example.cryptoapp.data.storage.database.DbProvider
import com.example.cryptoapp.data.storage.database.FavoriteCoinEntity
import com.example.cryptoapp.databinding.ActivityCoinDetailsBinding

class CoinDetailsActivity : AppCompatActivity() {

    //δημιουργουμε την κλαση απο το ΧΜΛ
    private lateinit var binding: ActivityCoinDetailsBinding

    //κραταμε το symbol για να το χρησιμοποιουμε στο favorite
    private lateinit var symbol: String

    //κραταμε το state για να αλλαζουμε icon
    private var isFav: Boolean = false

    //κραταμε reference στο menu item για να αλλαζουμε χρωμα/εικονιδιο
    private var favMenuItem: MenuItem? = null

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


        // Τα δείχνουμε στο UI γεμιζουμε τα textView
        binding.tvName.text = name
        binding.tvSymbol.text = symbol
        binding.tvPrice.text = price
        binding.tvChange.text = "$change (24h)"
        binding.ivCoin.load(image)

        //φορτωνουμε αν το coin ειναι ηδη favorite
        loadFavoriteState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_coin_details, menu)

        //κραταμε reference στο menu item για να το ενημερωνουμε μετα
        favMenuItem = menu.findItem(R.id.action_favorite)

        //βαζουμε το σωστο icon/χρωμα με βαση το state
        updateFavIcon()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> { // back
                finish()
                true
            }

            R.id.action_favorite -> { // click
                toggleFavorite()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //ελεγχει αν το coin ειναι favorite και ενημερωνει το icon
    private fun loadFavoriteState() {
        Thread {
            val dao = DbProvider.getDb(this).favoriteDao()
            isFav = dao.exists(symbol) != null

            runOnUiThread {
                //αν το menu δεν εχει φτιαχτει ακομα, απλα κανουμε refresh οταν φτιαχτει
                invalidateOptionsMenu()
            }
        }.start()
    }

    //βαζει ή βγαζει το coin απο τα favorites
    private fun toggleFavorite() {
        Thread {
            val dao = DbProvider.getDb(this).favoriteDao()

            if (isFav) {
                //αν ειναι favorite, το σβηνουμε
                dao.deleteBySymbol(symbol)
                isFav = false
            } else {
                //αν δεν ειναι, το προσθετουμε
                dao.insert(FavoriteCoinEntity(symbol))
                isFav = true
            }

            runOnUiThread {
                //αλλαζουμε το icon αναλογα με το νεο state
                updateFavIcon()
            }
        }.start()
    }

    //ενημερωνει το icon + χρωμα
    private fun updateFavIcon() {
        val item = favMenuItem ?: return

        //βαζουμε το ιδιο vector (heart) και αλλαζουμε μονο tint
        item.setIcon(R.drawable.ic_favorite)

        val colorRes = if (isFav) R.color.purple_200 else android.R.color.white
        item.icon?.setTint(ContextCompat.getColor(this, colorRes))
    }
}
