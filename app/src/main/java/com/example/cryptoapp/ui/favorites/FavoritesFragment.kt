package com.example.cryptoapp.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoapp.data.repository.FavoritesRepository
import com.example.cryptoapp.data.storage.CoinsStore
import com.example.cryptoapp.databinding.FragmentCoinListBinding
import com.example.cryptoapp.navigation.CoinDetailsNavigator
import com.example.cryptoapp.ui.adapter.CoinAdapter
import com.example.cryptoapp.ui.dashboard.SearchViewModel
import com.example.cryptoapp.ui.details.CoinDetailsActivity

//κοιταει ποια ειναι favorite τα φλτραει και τα δειχνει σε λιστα
class FavoritesFragment : Fragment() {

    //συνδεει με χμλ
    private var _binding: FragmentCoinListBinding? = null
    private val binding get() = _binding!!

    //adapter (ListAdapter) -> τον φτιαχνουμε 1 φορα και μετα αλλαζουμε τη λιστα με submitList
    private lateinit var adapter: CoinAdapter

    // Shared ViewModel για να παιρνουμε το query του search απο το MainActivity
    private val searchViewModel: SearchViewModel by activityViewModels()

    // FavoritesViewModel (κραταει state + λογικη favorites)
    private val viewModel: FavoritesViewModel by lazy {
        val repo = FavoritesRepository(requireContext().applicationContext)

        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return FavoritesViewModel(repo) as T
                }
            }
        )[FavoritesViewModel::class.java]
    }

    //ti layout δειχνω
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinListBinding.inflate(inflater, container, false)
        return binding.root
    }

    //λέει στο RecyclerView να γίνει κάθετη λίστα
    //στηνει τον adapter 1 φορα και συνδεεται με το ViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvCoins.layoutManager = LinearLayoutManager(requireContext())

        // δεν θελουμε swipe down refresh στα favorites
        binding.swipeRefresh.isEnabled = false
        binding.swipeRefresh.isRefreshing = false

        //φτιαχνουμε τον adapter 1 φορα (click: ανοίγει details)
        adapter = CoinAdapter { coin ->
            CoinDetailsNavigator.open(this, coin)
        }

        //βαζουμε τον adapter στο RecyclerView (1 φορα)
        binding.rvCoins.adapter = adapter

        // 1) Παρακολουθουμε τη λιστα που δινει το ViewModel (μετα απο filter)
        viewModel.visibleFavCoins.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // 2) ακουει το query απο το MainActivity και κανει filter mesa στο ViewModel
        searchViewModel.query.observe(viewLifecycleOwner) { query ->
            viewModel.filterCoins(query)
        }

        // 3) φορτωνουμε favorites (Room) και τα φιλτραρουμε πανω στο cached coins list
        viewModel.loadFavorites(CoinsStore.getCoins())
    }

    //αυτο καλειται κάθε φορά που επιστρέφεις στο tab Favorite γιατι
    //Μπορεί να βγαλεις/βαλεις favorites στα details
    //Θέλουμε η λίστα να ανανεώνεται
    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites(CoinsStore.getCoins())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
