package com.example.cryptoapp.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoapp.data.storage.database.DbProvider
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.data.storage.CoinsStore
import com.example.cryptoapp.databinding.FragmentCoinListBinding
import com.example.cryptoapp.ui.adapter.CoinAdapter
import com.example.cryptoapp.ui.details.CoinDetailsActivity

//κοιταει ποια ειναι favorite τα φλτραει και τα δειχνει σε λιστα
class FavoritesFragment : Fragment() {

    //συνδεει με χμλ
    private var _binding: FragmentCoinListBinding? = null
    private val binding get() = _binding!!

    //adapter (ListAdapter) -> τον φτιαχνουμε 1 φορα και μετα αλλαζουμε τη λιστα με submitList
    private lateinit var adapter: CoinAdapter

    //για το φιλτερ (κραταει αυτα που δειχνει το favorites tab)
    private var allFavCoins: List<Coin> = emptyList()

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
    //στηνει τον adapter 1 φορα και καλει τη showFavorites() για να γεμίσει δεδομένα
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvCoins.layoutManager = LinearLayoutManager(requireContext())

        //φτιαχνουμε τον adapter 1 φορα (click: ανοίγει details)
        adapter = CoinAdapter { coin ->
            val intent = Intent(requireContext(), CoinDetailsActivity::class.java)
            intent.putExtra("name", coin.name)
            intent.putExtra("symbol", coin.symbol)
            intent.putExtra("price", coin.price)
            intent.putExtra("change", coin.change24h)
            startActivity(intent)
        }

        //βαζουμε τον adapter στο RecyclerView (1 φορα)
        binding.rvCoins.adapter = adapter

        showFavorites()
    }

    //αυτο καλειται κάθε φορά που επιστρέφεις στο tab Favorite γιατι
    //Μπορεί να έβαλες/έβγαλες favorites στα details
    //Θέλουμε η λίστα να ανανεώνεται
    override fun onResume() {
        super.onResume()
        showFavorites()
    }

    //fun που βρισκει favorite και τα δειχνει
    private fun showFavorites() {

        val allCoins = CoinsStore.getCoins()

        //φερνουμε τα favorites απο τη Room σε background thread
        Thread {
            val dao = DbProvider.getDb(requireContext()).favoriteDao()
            val favSymbols = dao.getAllSymbols().toSet()

            //τα φιλτραρει ολα και κραταει οσα εχουν symbol μέσα στα favorites
            val favCoins = allCoins.filter { favSymbols.contains(it.symbol) }

            requireActivity().runOnUiThread {
                // krataei kai gia to search
                allFavCoins = favCoins

                //ListAdapter: δεν ξαναφτιαχνουμε adapter, απλα δινουμε τη νεα λιστα
                adapter.submitList(favCoins)
            }
        }.start()
    }

    //λογικη για φιλτραρισμα (θα την καλει το MainActivity οταν γραφεις στο search)
    fun filterCoins(query: String) {
        if (!this::adapter.isInitialized) return

        val q = query.trim()
        if (q.isEmpty()) {
            adapter.submitList(allFavCoins)
            return
        }

        val filtered = allFavCoins.filter {
            it.name.contains(q, ignoreCase = true) ||
                    it.symbol.contains(q, ignoreCase = true)
        }

        //στελνουμε τη φιλτραρισμενη λιστα στον adapter
        adapter.submitList(filtered.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
