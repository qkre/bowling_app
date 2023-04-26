package com.example.bowlingapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShowPlayerAdapter : RecyclerView.Adapter<ShowPlayerAdapter.ViewHolder>() {

    private var items: List<PlayerInfo> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val playerLayout: LinearLayout = itemView.findViewById(R.id.player_info)
        val playerName: TextView = itemView.findViewById(R.id.player_name)
        val playerWins: TextView = itemView.findViewById(R.id.player_win)
        val playerDraws: TextView = itemView.findViewById(R.id.player_draw)
        val playerLosses: TextView = itemView.findViewById(R.id.player_loss)
        val playerCosts: TextView = itemView.findViewById(R.id.player_cost)
        val playerRatings: TextView = itemView.findViewById(R.id.player_ratings)
        val playerRanking: TextView = itemView.findViewById(R.id.player_ranking)

        private val onLongClickListener = View.OnLongClickListener {
            // 길게 눌렀을 때의 처리 코드 작성
            Log.d("Long", "clicked")
            true // 이벤트 소비를 나타내는 true 반환
        }

        init {
            playerLayout.setOnLongClickListener(onLongClickListener)
        }

        fun bind(item: PlayerInfo){
            playerName.text = item.name
            playerWins.text = item.wins.toString()
            playerDraws.text = item.draws.toString()
            playerLosses.text = item.losses.toString()
            playerCosts.text = (item.draws * 2500 + item.losses * 5000).toString()
            playerRatings.text = String.format("%.2f", (item.wins.toFloat() / (item.wins + item.draws + item.losses) * 100))
            playerRanking.text = (adapterPosition+1).toString()
        }
    }

    fun setItems(items: List<PlayerInfo>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_player_info, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}