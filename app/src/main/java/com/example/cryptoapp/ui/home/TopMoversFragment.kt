package com.example.cryptoapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoapp.data.enum.TopMoversType
import com.example.cryptoapp.databinding.FragmentTopMoversBinding
import com.example.cryptoapp.navigation.CoinDetailsNavigator
import com.example.cryptoapp.ui.adapter.CoinAdapter
import com.example.cryptoapp.ui.dashboard.SearchViewModel
import com.example.cryptoapp.utils.getSortedTopMovers
import kotlin.getValue

class TopMoversFragment : Fragment() {

    //συνδεει το fragment με το XML μπορει να γινει null
    private var _binding: FragmentTopMoversBinding? = null
    private val binding get() = _binding!!

    //adapter (ListAdapter) -> τον φτιαχνουμε 1 φορα και μετα αλλαζουμε τη λιστα με submitList
    private lateinit var adapter: CoinAdapter

    //ViewModel = κρατάει state (coins/loading/error) για το Overview UI
    private val viewModel: OverviewViewModel by activityViewModels()

    //Shared ViewModel για να παιρνουμε το query του search απο το MainActivity
    private val searchViewModel: SearchViewModel by activityViewModels()

    //τι layout ua deixnei ayto to fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // το κλασικο που κανουμε για να φορτωσει το xml και να το δειξει
        _binding = FragmentTopMoversBinding.inflate(inflater, container, false)
        return binding.root
    }

    //ui set up
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //tα items να μπαίνουν κάθετα, σαν λίστα
        binding.rvTopMovers.layoutManager = LinearLayoutManager(requireContext())

        //φτιαχνουμε τον adapter 1 φορα
        adapter = CoinAdapter { coin ->
            CoinDetailsNavigator.open(this, coin)
        }

        //βαζουμε τον adapter στο RecyclerView (1 φορα)
        binding.rvTopMovers.adapter = adapter

        //swipe down -> refresh
        binding.swipeRefresh.setOnRefreshListener {
            // οταν κανει swipe down θελουμε ΠΑΝΤΑ να καλεσει API
            viewModel.fetchCoins(forceRefresh = true)
        }

        //ακουει το state απο το ViewModel και ενημερωνει UI
        viewModel.coins.observe(viewLifecycleOwner) { list ->
            //ListAdapter: δεν ξαναφτιαχνουμε adapter, απλα δινουμε τη νεα λιστα
            adapter.submitList(getSortedTopMovers(list, resolveTopMoverType(), null)) {
                // when clicking the other option of the toggle, it scrolls at the top smoothly
                binding.rvTopMovers.post { binding.rvTopMovers.smoothScrollToPosition(0) }
            }
        }

        // decide which sorting to apply based on the toggle option
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val list = viewModel.coins.value ?: return@addOnButtonCheckedListener
            adapter.submitList(getSortedTopMovers(list, resolveTopMoverType(), null)) {
                // when clicking the other option of the toggle, it scrolls at the top smoothly
                binding.rvTopMovers.post { binding.rvTopMovers.smoothScrollToPosition(0) }
            }
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

    private fun resolveTopMoverType(): TopMoversType {
        return when (binding.toggleGroup.checkedButtonId) {
            binding.btnGainers.id -> TopMoversType.GAINERS
            binding.btnLosers.id -> TopMoversType.LOSERS
            else -> TopMoversType.GAINERS
        }
    }

}