package com.example.cryptoapp.ui.overview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoapp.databinding.FragmentCoinListBinding
import com.example.cryptoapp.ui.adapter.CoinAdapter
import com.example.cryptoapp.ui.dashboard.SearchViewModel
import com.example.cryptoapp.ui.details.CoinDetailsActivity

//ειναι Fragment (αρα θα μπει μεσα σε activity) γιατι ειναι κομματι οθονης. που δειχνει απλα τα coins
class OverviewFragment : Fragment() {

    //συνδεει το fragment με το XML μπορει να γινει null
    private var _binding: FragmentCoinListBinding? = null
    private val binding get() = _binding!!

    //adapter (ListAdapter) -> τον φτιαχνουμε 1 φορα και μετα αλλαζουμε τη λιστα με submitList
    private lateinit var adapter: CoinAdapter

    //ViewModel = κρατάει state (coins/loading/error) για το Overview UI
    private val viewModel: OverviewViewModel by viewModels()

    //Shared ViewModel για να παιρνουμε το query του search απο το MainActivity
    private val searchViewModel: SearchViewModel by activityViewModels()

    //τι layout ua deixnei ayto to fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // το κλασικο που κανουμε για να φορτωσει το xml και να το δειξει
        _binding = FragmentCoinListBinding.inflate(inflater, container, false)
        return binding.root
    }

    //ui set up
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //tα items να μπαίνουν κάθετα, σαν λίστα
        binding.rvCoins.layoutManager = LinearLayoutManager(requireContext())

        //φτιαχνουμε τον adapter 1 φορα
        adapter = CoinAdapter { coin ->
            val intent = Intent(requireContext(), CoinDetailsActivity::class.java)
            intent.putExtra("name", coin.name)
            intent.putExtra("symbol", coin.symbol)
            intent.putExtra("price", coin.price)
            intent.putExtra("change", coin.change24h)
            intent.putExtra("image", coin.image)
            startActivity(intent)
        }

        //βαζουμε τον adapter στο RecyclerView (1 φορα)
        binding.rvCoins.adapter = adapter

        //swipe down -> refresh
        binding.swipeRefresh.setOnRefreshListener {
            // οταν κανει swipe down θελουμε ΠΑΝΤΑ να καλεσει API
            viewModel.fetchCoins(forceRefresh = true)
        }

        //ακουει το state απο το ViewModel και ενημερωνει UI
        viewModel.coins.observe(viewLifecycleOwner) { list ->
            //ListAdapter: δεν ξαναφτιαχνουμε adapter, απλα δινουμε τη νεα λιστα
            adapter.submitList(list)
        }

        //spinner (swipe refresh)
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading == true
        }

        //error toast
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // ακουει το query απο το MainActivity και κανει filter (μεσω ViewModel)
        searchViewModel.query.observe(viewLifecycleOwner) { query ->
            viewModel.filterCoins(query.orEmpty())
        }

        //πρωτη φορα που ανοιγει το fragment:
        // 1) αν εχουμε ηδη coins στο store (cache) -> τα δειχνουμε χωρις API
        // 2) αλλιως -> κανουμε API call 1 φορα
        if (savedInstanceState == null) {
            viewModel.loadInitial()
        } else {
            //δεν θελουμε να κανουμε κατι
        }
    }

    //παντα το γραφουμε σε fragments giati Καθαρίζει το binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
