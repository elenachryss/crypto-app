package com.example.cryptoapp.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.ActivityDashboardBinding
import com.example.cryptoapp.ui.favorites.FavoritesFragment
import com.example.cryptoapp.ui.overview.OverviewFragment
import com.example.cryptoapp.ui.watchlist.WatchlistFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    //κρατάμε τι έχει γράψει στο search
    private var currentQuery: String = ""

    // κρατάμε reference για να το κάνουμε reset όταν αλλάζουμε tab
    private var searchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null

    // κραταμε 1 instance απο καθε fragment (να μην ξαναφτιαχνεται καθε φορα)
    private val overviewFragment = OverviewFragment()
    private val favoritesFragment = FavoritesFragment()
    private val watchlistFragment = WatchlistFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // gia na mhn akoybaei to layout xvris na valoume padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        // Βάζουμε default fragment (Overview) ΜΟΝΟ την πρωτη φορα
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, overviewFragment)
                .commit()
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> overviewFragment
                R.id.nav_favorites -> favoritesFragment
                R.id.nav_watchlist -> watchlistFragment
                else -> overviewFragment
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()

            // οταν αλλαζουμε tab θελουμε reset το search
            resetSearch()

            true
        }
    }

    //λογικη για search
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)

        val item = menu.findItem(R.id.action_search)
        val sv = item.actionView as SearchView

        // κραταμε references για reset
        searchMenuItem = item
        searchView = sv

        sv.queryHint = "Search"

        // ΔΕΝ κρυβουμε το search απο πουθενα
        item.isVisible = true

        //όταν ξαναγίνει inflate το menu, γεμίζουμε το SearchView με το currentQuery
        if (currentQuery.isNotEmpty()) {
            item.expandActionView()
            sv.setQuery(currentQuery, false)
            sv.clearFocus()
        }

        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                val q = query.orEmpty()
                currentQuery = q
                sendSearchToCurrentFragment(q)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText.orEmpty()
                currentQuery = q
                sendSearchToCurrentFragment(q)
                return true
            }
        })

        // αν πατησει Χ στο search, καθαριζουμε και επαναφερουμε τη λιστα
        sv.setOnCloseListener {
            currentQuery = ""
            sendSearchToCurrentFragment("")
            false
        }

        return true
    }

    // στελνουμε filter στο fragment που βλεπει ο χρηστης τωρα (Overview ή Favorites)
    // (στο watchlist απλα δεν κανει τιποτα προς το παρον)
    private fun sendSearchToCurrentFragment(query: String) {
        val current = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (current is OverviewFragment) {
            current.filterCoins(query)
            return
        }

        if (current is FavoritesFragment) {
            current.filterCoins(query)
            return
        }

        // αν ειναι Watchlist (ή κατι αλλο), δεν κανουμε κατι ακομα
    }

    // reset search οταν αλλαζουμε (Overview/Favorites/Watchlist)
    private fun resetSearch() {
        // καθαριζουμε το text που εχει γραψει
        currentQuery = ""

        // κλεινουμε το UI του search και καθαριζουμε το query (αν εχει ηδη δημιουργηθει)
        searchView?.setQuery("", false)
        searchView?.clearFocus()
        searchMenuItem?.collapseActionView()

        // επαναφερουμε τη λιστα στο fragment που φαινεται (Overview ή Favorites)
        sendSearchToCurrentFragment("")
    }
}
