package com.example.cryptoapp.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.ActivityDashboardBinding
import com.example.cryptoapp.ui.favorites.FavoritesFragment
import com.example.cryptoapp.ui.overview.OverviewFragment
import com.example.cryptoapp.ui.watchlist.WatchlistFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    // κρατάμε reference για να το κάνουμε reset όταν αλλάζουμε tab
    private var searchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null

    // Shared ViewModel για το search (το βλεπουν και τα fragments)
    private val searchViewModel: SearchViewModel by viewModels()

    // κραταμε 1 instance απο καθε fragment (να μην ξαναφτιαχνεται καθε φορα)
    private val overviewFragment = OverviewFragment()
    private val favoritesFragment = FavoritesFragment()
    private val watchlistFragment = WatchlistFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge
        // βαζουμε μονο TOP inset στο toolbar για να μην μπαινει κατω απο το status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, view.paddingBottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        // Βάζουμε default fragment (Overview) την πρωτη φορα
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, overviewFragment)
            }
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> overviewFragment
                R.id.nav_favorites -> favoritesFragment
                R.id.nav_watchlist -> watchlistFragment
                else -> overviewFragment
            }

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, fragment)
            }

            // οταν αλλαζουμε tab θελουμε reset το search
            resetSearch()

            true
        }

        // αν πατησει ξανα το ιδιο tab (optional), κανε reset search / scroll top κτλ
        binding.bottomNav.setOnItemReselectedListener {
            resetSearch()
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

        // αν υπαρχει ηδη query στο Shared ViewModel, το δειχνουμε στο SearchView
        val existingQuery = searchViewModel.query.value.orEmpty()
        if (existingQuery.isNotEmpty()) {
            item.expandActionView()
            sv.setQuery(existingQuery, false)
            sv.clearFocus()
        }

        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //στελνουμε το query στο Shared ViewModel (και τα fragments θα κανουν observe)
                searchViewModel.setQuery(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //στελνουμε το query στο Shared ViewModel (και τα fragments θα κανουν observe)
                searchViewModel.setQuery(newText.orEmpty())
                return true
            }
        })

        // αν πατησει Χ στο search, καθαριζουμε και επαναφερουμε τη λιστα
        sv.setOnCloseListener {
            //καθαριζουμε το query στο Shared ViewModel
            searchViewModel.clear()
            false
        }

        return true
    }

    // reset search οταν αλλαζουμε (Overview/Favorites/Watchlist)
    private fun resetSearch() {
        // κλεινουμε το UI του search και καθαριζουμε το query (αν εχει ηδη δημιουργηθει)
        searchView?.setQuery("", false)
        searchView?.clearFocus()
        searchMenuItem?.collapseActionView()

        // καθαριζουμε το query στο Shared ViewModel (θα κανει reset τα lists)
        searchViewModel.clear()
    }
}
