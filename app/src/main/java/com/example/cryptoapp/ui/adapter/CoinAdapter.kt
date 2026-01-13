package com.example.cryptoapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.databinding.HolderCoinBinding

//Φτιάχνουμε έναν Adapter
class CoinAdapter(
//    //Του δίνουμε μια λίστα: List<Coin>
//    private val items: List<Coin>,

    //το unit ειναι void δεν περιμένουμε αποτέλεσμα πίσω
    private val onItemClick: (Coin) -> Unit
) : ListAdapter<Coin, CoinAdapter.CoinViewHolder>(DIFF) {

//    //Αυτό είναι το κουτάκι ενός item.
//    class CoinViewHolder(
//        val binding: HolderCoinBinding
//    ) : RecyclerView.ViewHolder(binding.root)


    //Φτιάξε ένα καινούργιο row όταν το RecyclerView το χρειαστεί
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        //“πάρε το holder_coin.xml”
        //“κάν’ το View”
        val binding = HolderCoinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        //“βάζ’ το μέσα σε ViewHolder”
        return CoinViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
//        //Για αυτό το row, βάλε τα σωστά δεδομένα
//        val item = items[position]

        val coin = getItem(position)
        holder.bind(coin)


//        //γεμισμα της λιστας
//        holder.binding.tvName.text = item.name
//        holder.binding.tvSymbol.text = item.symbol
//        holder.binding.tvPrice.text = item.price
//        holder.binding.tvChange.text = item.change24h
//
//        //kanei to klik
//        holder.binding.root.setOnClickListener {
//            onItemClick(item)
//        }
    }

    inner class CoinViewHolder(private val binding: HolderCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: Coin) {
            binding.tvName.text = coin.name
            binding.tvSymbol.text = coin.symbol
            binding.tvPrice.text = coin.price
            binding.tvChange.text = coin.change24h

            binding.root.setOnClickListener {
                onItemClick(coin)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Coin>() {
            override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
                // “ίδιο coin” → με βάση symbol (BTC, ETH)
                return oldItem.symbol == newItem.symbol
            }

            override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
                // όλα ίδια → δεν χρειάζεται update
                return oldItem == newItem
            }
        }
    }

//    override fun getItemCount(): Int {
//        return items.size
//    }
}