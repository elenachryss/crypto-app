package com.example.cryptoapp.ui.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.ItemWatchlistPickerBinding

//Adapter για το dialog που δείχνει όλα τα coins για προσθήκη στο Watchlist
//Δένει το item_watchlist_picker.xml
class WatchlistPickerAdapter(
    //callback: το καλούμε όταν ο χρήστης πατήσει το + για να προσθέσει coin
    private val onAddClick: (WatchlistPickerItem) -> Unit
) : ListAdapter<WatchlistPickerItem, WatchlistPickerAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemWatchlistPickerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemWatchlistPickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WatchlistPickerItem) {
            val coin = item.coin

            //γεμίζουμε texts + ιματζε
            binding.tvName.text = coin.name
            binding.tvSymbol.text = coin.symbol
            binding.ivCoin.load(coin.image)

            //αν το coin ειναι ηδη added:
            // - δειχνουμε check
            // - απενεργοποιουμε το κουμπι
            if (item.isAdded) {
                binding.btnAction.isEnabled = false
                binding.btnAction.alpha = 0.6f

                //βάζουμε check icon
                binding.btnAction.setIconResource(R.drawable.ic_check_small)
                //αν θες να ειναι διαφορετικό χρώμα:
                // binding.btnAction.iconTint = ColorStateList.valueOf(Color.GRAY)

            } else {
                binding.btnAction.isEnabled = true
                binding.btnAction.alpha = 1f

                //βάζουμε plus icon
                binding.btnAction.setIconResource(R.drawable.ic_add)
            }

            //click ΜΟΝΟ όταν δεν ειναι ηδη added
            binding.btnAction.setOnClickListener {
                if (!item.isAdded) {
                    onAddClick(item)
                }
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<WatchlistPickerItem>() {
            override fun areItemsTheSame(
                oldItem: WatchlistPickerItem,
                newItem: WatchlistPickerItem
            ): Boolean {
                //ίδιο item -> ίδιο coin symbol
                return oldItem.coin.symbol == newItem.coin.symbol
            }

            override fun areContentsTheSame(
                oldItem: WatchlistPickerItem,
                newItem: WatchlistPickerItem
            ): Boolean {
                //αν δεν άλλαξε τίποτα (ούτε isAdded)
                return oldItem == newItem
            }
        }
    }
}
