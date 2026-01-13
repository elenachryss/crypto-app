package com.example.cryptoapp.ui.dashboard

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.ActivityDashboardBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var pagerAdapter: DashboardPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pagerAdapter = DashboardPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter


        setSupportActionBar(binding.toolbar)

        val titles = listOf("Overview", "Favorites")

        // tabs με τα fragments
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    //λογικη για φιλτερινκγ
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search coins..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                sendSearchQuery(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                android.util.Log.d("SEARCH", "typing: $newText")
                sendSearchQuery(newText.orEmpty())
                return true
            }
        })

        searchView.setOnCloseListener {
            sendSearchQuery("")
            false
        }

        return true
    }

    //filtrarei kai ta dyo
    private fun sendSearchQuery(query: String) {
        pagerAdapter.getOverviewFragment().filterCoins(query)
        pagerAdapter.getFavoritesFragment().filterCoins(query)
    }


    //φιλτραρει πoιο απο τα δυο βλεπει
//    private fun sendSearchQuery(query: String) {
//        if (binding.viewPager.currentItem == 0) {
//            pagerAdapter.getOverviewFragment().filterCoins(query)
//        } else {
//            pagerAdapter.getFavoritesFragment().filterCoins(query)
//        }
//    }


}
