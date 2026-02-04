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
import com.example.cryptoapp.R
import com.example.cryptoapp.data.repository.FavoritesRepository
import com.example.cryptoapp.databinding.ActivityCoinDetailsBinding
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.cryptoapp.data.network.RetrofitClient
import com.example.cryptoapp.data.repository.CoinsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // Τα δείχνουμε στο UI γεμιζουμε τα textView
        binding.tvName.text = name
        binding.tvSymbol.text = symbol
        binding.tvPrice.text = price
        binding.tvChange.text = "$change (24h)"
        binding.ivCoin.load(image)
        val coinId = name.lowercase()

        if (name != null) {
            fetchCoinDetails(coinId)
        } else {
            Toast.makeText(this, "Error: No Coin ID found", Toast.LENGTH_SHORT).show()
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

    private fun fetchCoinDetails(id: String) {
        binding.progressBarDetails.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Replace 'RetrofitInstance.api' with your actual Singleton accessor
                println("This is the id in fetchCoinDetails: $id")
                val response = RetrofitClient.api.getCoinDetails(id)

                println("This is the response in fetchCoinDetails: $response")
                withContext(Dispatchers.Main) {
                    binding.progressBarDetails.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!

                        // 2. Populate Description (Parsing HTML)
                        // CoinGecko descriptions often contain HTML tags
                        binding.tvDescription.text = Html.fromHtml(
                            data.description.en,
                            Html.FROM_HTML_MODE_COMPACT
                        )

                        // 3. Handle Website Link
                        val homepageUrl = data.links.homepage?.firstOrNull { it.isNotEmpty() }
                        if (!homepageUrl.isNullOrEmpty()) {
                            binding.btnWebsite.visibility = View.VISIBLE
                            binding.btnWebsite.setOnClickListener {
                                openBrowser(homepageUrl)
                            }
                        }

                        // 4. Handle Whitepaper Link
                        val whitepaperUrl = data.links.whitepaper
                        if (!whitepaperUrl.isNullOrEmpty()) {
                            binding.btnWhitepaper.visibility = View.VISIBLE
                            binding.btnWhitepaper.setOnClickListener {
                                openBrowser(whitepaperUrl)
                            }
                        }

                    } else {
                        Toast.makeText(this@CoinDetailsActivity, "Failed to load details", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBarDetails.visibility = View.GONE
                    Toast.makeText(this@CoinDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show()
        }
    }
}
