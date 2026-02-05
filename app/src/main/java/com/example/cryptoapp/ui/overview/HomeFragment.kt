package com.example.cryptoapp.ui.overview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoapp.R
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.databinding.FragmentHomeBinding
import com.example.cryptoapp.ui.adapter.CoinAdapter
import com.example.cryptoapp.ui.details.CoinDetailsActivity
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class HomeFragment : Fragment() {

    //συνδεει το fragment με το XML μπορει να γινει null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //adapter (ListAdapter) -> τον φτιαχνουμε 1 φορα και μετα αλλαζουμε τη λιστα με submitList
    private lateinit var adapter: CoinAdapter
    private lateinit var topMoversAdapter: CoinAdapter

    //ViewModel = κρατάει state (coins/loading/error) για το Overview UI
    private val viewModel: OverviewViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    //τι layout ua deixnei ayto to fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // το κλασικο που κανουμε για να φορτωσει το xml και να το δειξει
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    //ui set up
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cardAllCoinsBinding = binding.cardAllCoins
        val cardTopMoversBinding = binding.cardMovers

        cardTopMoversBinding.rvListTopMovers.isNestedScrollingEnabled = false

        //tα items να μπαίνουν κάθετα, σαν λίστα
        cardAllCoinsBinding.rvListAllCoins.layoutManager = LinearLayoutManager(requireContext())
        cardTopMoversBinding.rvListTopMovers.layoutManager = LinearLayoutManager(requireContext())

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

        topMoversAdapter = CoinAdapter { coin ->
            val intent = Intent(requireContext(), CoinDetailsActivity::class.java)
            intent.putExtra("name", coin.name)
            intent.putExtra("symbol", coin.symbol)
            intent.putExtra("price", coin.price)
            intent.putExtra("change", coin.change24h)
            intent.putExtra("image", coin.image)
            startActivity(intent)
        }

        //βαζουμε τον adapter στο RecyclerView (1 φορα)
        cardAllCoinsBinding.rvListAllCoins.adapter = adapter
        cardTopMoversBinding.rvListTopMovers.adapter = topMoversAdapter

        //ακουει το state απο το ViewModel και ενημερωνει UI
        viewModel.coins.observe(viewLifecycleOwner) { list ->
            //ListAdapter: δεν ξαναφτιαχνουμε adapter, απλα δινουμε τη νεα λιστα
            adapter.submitList(list.take(5))

            topMoversAdapter.submitList(getSortedTopMovers(list))
        }

        // decide which sorting to apply based on the toggle option
        cardTopMoversBinding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val list = viewModel.coins.value ?: return@addOnButtonCheckedListener
            topMoversAdapter.submitList(getSortedTopMovers(list))
        }

        //error toast/snackbar on the level of this view
        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Snackbar
                    .make(binding.root, msg, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.cardAllCoins.root.setOnClickListener {
            val overviewFragment = OverviewFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, overviewFragment)
                .addToBackStack("overview")
                .commit()
        }

        binding.swipeRefresh.setOnRefreshListener {
            // οταν κανει swipe down θελουμε ΠΑΝΤΑ(εκτος απο οταν τρεχει ηδη swipe) να καλεσει API
            if (viewModel.loading.value == true) return@setOnRefreshListener
            viewModel.fetchCoins(forceRefresh = true)
        }

        //spinner (swipe refresh)
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading == true
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_search)?.isVisible = false
    }

    private fun getSortedTopMovers(list: List<Coin>): List<Coin> {
        val cardTopMoversBinding = binding.cardMovers
        return when (cardTopMoversBinding.toggleGroup.checkedButtonId) {
            cardTopMoversBinding.btnGainers.id -> list.sortedByDescending { it.change24hValue }
                .take(5)

            cardTopMoversBinding.btnLosers.id -> list.sortedBy { it.change24hValue }.take(5)
            else -> emptyList()
        }
    }

}