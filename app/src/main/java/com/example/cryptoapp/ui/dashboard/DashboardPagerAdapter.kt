package com.example.cryptoapp.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cryptoapp.ui.favorites.FavoritesFragment
import com.example.cryptoapp.ui.overview.OverviewFragment

class DashboardPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    //κραταμε τα fragments σαν μεταβλητες για να μπορει το MainActivity να τα βρει σιγουρα
    private val overviewFragment = OverviewFragment()
    private val favoritesFragment = FavoritesFragment()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) overviewFragment else favoritesFragment
    }

    //helper: δινουμε προσβαση στο OverviewFragment + favorite
    fun getOverviewFragment(): OverviewFragment = overviewFragment
    fun getFavoritesFragment(): FavoritesFragment = favoritesFragment
}
