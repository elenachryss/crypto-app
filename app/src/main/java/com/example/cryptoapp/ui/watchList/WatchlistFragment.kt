package com.example.cryptoapp.ui.watchlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.data.repository.WatchlistRepository
import com.example.cryptoapp.databinding.DialogWatchlistPickerBinding
import com.example.cryptoapp.databinding.FragmentWatchlistBinding
import com.example.cryptoapp.ui.adapter.CoinAdapter
import com.example.cryptoapp.ui.details.CoinDetailsActivity


class WatchlistFragment : Fragment() {

    //συνδεει το fragment με το XML μπορει να γινει null
    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    //adapter για τη λιστα watchlist
    private lateinit var adapter: CoinAdapter

    //ViewModel
    private lateinit var viewModel: WatchlistViewModel

    //κραταμε reference στο dialog για να μπορουμε να το κλεισουμε
    private var pickerDialog: AlertDialog? = null

    //ti layout δειχνω
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    //εδω στηνεται το UI + observers
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //φτιαχνουμε ViewModel με repository
        val repo = WatchlistRepository(requireContext())
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return WatchlistViewModel(repo) as T
                }
            }
        )[WatchlistViewModel::class.java]

//        //insets για να μην ακουμπαει πανω στο status bar
//        ViewCompat.setOnApplyWindowInsetsListener(binding.watchlistRoot) { v, insets ->
//            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, v.paddingBottom)
//            insets
//        }

        //στηνουμε recycler
        binding.rvWatchlist.layoutManager = LinearLayoutManager(requireContext())

        //adapter 1 φορα (click -> ανοίγει details)
        adapter = CoinAdapter { coin ->
            val intent = Intent(requireContext(), CoinDetailsActivity::class.java)
            intent.putExtra("name", coin.name)
            intent.putExtra("symbol", coin.symbol)
            intent.putExtra("price", coin.price)
            intent.putExtra("change", coin.change24h)
            intent.putExtra("image", coin.image)
            startActivity(intent)
        }
        binding.rvWatchlist.adapter = adapter



        // Swipe left δείχνει το red remove background kai delete
        val swipeHelper = ItemTouchHelper(
            SwipeToRemoveCallback(requireContext()) { position ->
                val list = viewModel.watchlistCoins.value.orEmpty()
                if (position !in list.indices) return@SwipeToRemoveCallback

                val coin = list[position]
                viewModel.removeFromWatchlist(coin.symbol)
            }
        )

        swipeHelper.attachToRecyclerView(binding.rvWatchlist)

        //Add to watchlist -> ανοίγει picker
        binding.btnAdd.setOnClickListener {
            openAddDialog()
        }

        //observe list
        viewModel.watchlistCoins.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            //αν δεν εχει τιποτα δειχνουμε empty text
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        //observe message/errors
        viewModel.message.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        //φορτωνουμε watchlist
        viewModel.loadWatchlist()
    }


    //Custom dialog
    private fun openAddDialog() {
        // Φέρνουμε τα items (coin + isAdded)
        viewModel.loadPickerItems { items ->
            if (items.isEmpty()) return@loadPickerItems

            requireActivity().runOnUiThread {

                // inflate το dialog layout
                val dialogBinding = DialogWatchlistPickerBinding.inflate(layoutInflater)

                // φτιάχνουμε adapter
                lateinit var pickerAdapter: WatchlistPickerAdapter

                pickerAdapter = WatchlistPickerAdapter(
                    onAddClick = { item ->

                        // add και περιμένουμε να τελειώσει η Room
                        viewModel.addToWatchlist(item.coin.symbol) {

                            // ΜΟΝΟ ΜΕΤΑ το insert, ξαναφορτώνουμε items για να γινει check/disabled
                            viewModel.loadPickerItems { newItems ->
                                requireActivity().runOnUiThread {
                                    pickerAdapter.submitList(newItems)
                                }
                            }
                        }
                    }
                )


                //setup recycler
                dialogBinding.rvPicker.layoutManager = LinearLayoutManager(requireContext())
                dialogBinding.rvPicker.adapter = pickerAdapter

                //  initial list
                pickerAdapter.submitList(items)

                // build dialog
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogBinding.root)
                    .create()

                dialogBinding.btnDone.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        //αν αλλαξε κατι απο details, κανουμε refresh
        viewModel.loadWatchlist()
    }

    //παντα το γραφουμε σε fragments giati Καθαρίζει το binding
    override fun onDestroyView() {
        super.onDestroyView()
        pickerDialog = null
        _binding = null
    }
}
