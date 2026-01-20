package com.example.cryptoapp.ui.overview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.data.model.MarketCoinDto
import com.example.cryptoapp.data.network.RetrofitClient
import com.example.cryptoapp.data.storage.CoinsStore
import com.example.cryptoapp.databinding.FragmentCoinListBinding
import com.example.cryptoapp.ui.adapter.CoinAdapter
import com.example.cryptoapp.ui.details.CoinDetailsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//ειναι Fragment (αρα θα μπει μεσα σε activity) γιατι ειναι κομματι οθονης. που δειχνει απλα τα coins
class OverviewFragment : Fragment() {

    //κραταμε ολα τα coins που ηρθαν απο το API για να μπορουμε να κανουμε φιλτραρισμα
    private var allCoins: List<Coin> = emptyList()

    //συνδεει το fragment με το XML μπορει να γινει null
    private var _binding: FragmentCoinListBinding? = null
    private val binding get() = _binding!!

    //adapter (ListAdapter) -> τον φτιαχνουμε 1 φορα και μετα αλλαζουμε τη λιστα με submitList
    private lateinit var adapter: CoinAdapter

    private fun formatPriceUsd(price: Double): String {
        return "$" + String.format("%,.2f", price)
    }

    private fun formatPercent(value: Double?): String {
        if (value == null) return "-"
        val sign = if (value >= 0) "+" else ""
        return sign + String.format("%.2f", value) + "%"
    }

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

    //εδω ξεκιναει το call
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
            fetchCoins(forceRefresh = true)
        }

        //πρωτη φορα που ανοιγει το fragment:
        // 1) αν εχουμε ηδη coins στο store (cache) -> τα δειχνουμε χωρις API
        // 2) αλλιως -> κανουμε API call 1 φορα
        val cached = CoinsStore.getCoins()
        if (cached.isNotEmpty()) {
            allCoins = cached
            adapter.submitList(cached)
        } else {
            fetchCoins(forceRefresh = false)
        }
    }

    //εδω ειναι το API call, για να το καλουμε και στην αρχη και στο swipe refresh
    private fun fetchCoins(forceRefresh: Boolean) {
        // αν ΔΕΝ ειναι swipe refresh και εχουμε ηδη δεδομενα, δεν ξανακαλουμε το API
        if (!forceRefresh && CoinsStore.getCoins().isNotEmpty()) return

        Log.d("API", "Starting API call...")

        //δείξε το spinner όταν κάνεις fetch
        binding.swipeRefresh.isRefreshing = true

        //Κάνει το API call (enqueue = το εκτελεί ασύγχρονα)
        RetrofitClient.api.getMarkets().enqueue(object : Callback<List<MarketCoinDto>> {

            override fun onResponse(
                call: Call<List<MarketCoinDto>>,
                response: Response<List<MarketCoinDto>>
            ) {
                //σταματάμε το spinner
                binding.swipeRefresh.isRefreshing = false

                Log.d("API", "Response code: ${response.code()}")

                if (!response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "API error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                //παιρνεις το body (dtoList) αν body είναι null, παίρνεις άδεια λίστα για να μη σκάσει
                val dtoList = response.body() ?: emptyList()

                //κανουμε map τα δεδομενα απο το body και τα παιρναμε μεσα στο coin που εχουμε φτιαξει
                val coins = dtoList.map { dto ->
                    Coin(
                        name = dto.name,
                        symbol = dto.symbol.uppercase(),
                        price = formatPriceUsd(dto.currentPrice),
                        change24h = formatPercent(dto.change24h),
                        change24hValue= dto.change24h,
                        image = dto.image ?: ""
                    )
                }

                //setaroume ta coins apo to response stin lista για να τα βλεπει και το FavoritesFragment
                CoinsStore.setCoins(coins)

                //κραταμε ολα τα coins για το search filter
                allCoins = coins

                //ListAdapter: δεν ξαναφτιαχνουμε adapter, απλα δινουμε τη νεα λιστα
                adapter.submitList(coins)
            }

            //όταν αποτύχει
            override fun onFailure(call: Call<List<MarketCoinDto>>, t: Throwable) {
                //σταματάμε το spinner
                binding.swipeRefresh.isRefreshing = false

                Log.e("API", "Network error", t)
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    //λογικη για φιλτραρισμα την λιστα με τα coins poy εμφανιζεται στην overview otan kanoume search
    fun filterCoins(query: String) {
        if (!this::adapter.isInitialized) return

        val q = query.trim()
        if (q.isEmpty()) {
            adapter.submitList(allCoins)
            return
        }

        val filtered = allCoins.filter {
            it.name.contains(q, ignoreCase = true) ||
                    it.symbol.contains(q, ignoreCase = true)
        }

        //στελνουμε τη φιλτραρισμενη λιστα στον adapter
        adapter.submitList(filtered.toList())
    }

    //παντα το γραφουμε σε fragments giati Καθαρίζει το binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
