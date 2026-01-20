package com.example.cryptoapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cryptoapp.data.model.Coin
import com.example.cryptoapp.databinding.HolderCoinBinding

//Φτιάχνουμε έναν Adapter που
//παίρνει Coin αντικείμενα
//τα μετατρέπει σε γραμμές λίστας
//και λέει στο RecyclerView πώς να τα δείξει και πώς να αντιδρά στο click
class CoinAdapter(
//    //Του δίνουμε μια λίστα: List<Coin>
//    private val items: List<Coin>,

    //το unit ειναι void δεν περιμένουμε αποτέλεσμα πίσω
    private val onItemClick: (Coin) -> Unit
) : ListAdapter<Coin, CoinAdapter.CoinViewHolder>(DIFF) {

    //Φτιάξε ένα καινούργιο row όταν το RecyclerView το χρειαστεί
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        //πάρε το holder_coin.xml
        //κάν’ το View
        val binding = HolderCoinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        //μέσα σε ViewHolder
        return CoinViewHolder(binding)
    }


    //γεμισμα δεδομενων σε κάθε γραμμή του RecyclerView
    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {

        val coin = getItem(position)
        holder.bind(coin)
    }

    //Η γραμμή της λίστας
    inner class CoinViewHolder(private val binding: HolderCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: Coin) {
            binding.tvName.text = coin.name
            binding.tvSymbol.text = coin.symbol
            binding.tvPrice.text = coin.price
            binding.tvChange.text = coin.change24h
            binding.ivIcon.load(coin.image)

            binding.root.setOnClickListener {
                onItemClick(coin)
            }
        }
    }

    //συγκριση dyo coins αφου βαλαμε γιατι βαλαμε το ListAdapter
    //Το DiffUtil είναι υποχρεωτικό όταν βαζουμε ListAdapter.
    //Είναι ο κανόνας σύγκρισης παλιάς και νέας λίστας
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

}