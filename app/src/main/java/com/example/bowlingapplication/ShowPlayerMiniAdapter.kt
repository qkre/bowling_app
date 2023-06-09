package com.example.bowlingapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowPlayerMiniAdapter : RecyclerView.Adapter<ShowPlayerMiniAdapter.ViewHolder>() {

    private var items: List<PlayerInfo> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val playerName: TextView = itemView.findViewById(R.id.player_name)
        val playerWins: TextView = itemView.findViewById(R.id.player_win)
        val playerDraws: TextView = itemView.findViewById(R.id.player_draw)
        val playerLosses: TextView = itemView.findViewById(R.id.player_loss)
        val playerCosts: TextView = itemView.findViewById(R.id.player_cost)

        fun bind(item: PlayerInfo){
            playerName.text = item.name
            playerWins.text = item.wins.toString()
            playerDraws.text = item.draws.toString()
            playerLosses.text = item.losses.toString()
            playerCosts.text = (item.draws * 2500 + item.losses * 5000).toString()
        }

    }
    fun setItems(items: List<PlayerInfo>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowPlayerMiniAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_playr_info_mini, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ShowPlayerMiniAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}