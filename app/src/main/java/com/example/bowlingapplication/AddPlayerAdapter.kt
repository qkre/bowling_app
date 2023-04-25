package com.example.bowlingapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddPlayerAdapter : RecyclerView.Adapter<AddPlayerAdapter.ViewHolder>() {

    private var items: List<PlayerInfo> = emptyList()

    fun setItems(items: List<PlayerInfo>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_add_player_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface PlayerInfoListener {
        fun onPlayerInfoChanged(playerInfo: PlayerInfo)
    }

    private lateinit var listener: PlayerInfoListener

    fun setListener(listener: PlayerInfoListener){
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.text_name)
        val winsTextView: TextView = itemView.findViewById(R.id.text_wins)
        val drawsTextView: TextView = itemView.findViewById(R.id.text_draws)
        val lossesTextView: TextView = itemView.findViewById(R.id.text_losses)
        val btnWin: Button = itemView.findViewById(R.id.btn_win)
        val btnDraw: Button = itemView.findViewById(R.id.btn_draw)
        val btnLose: Button = itemView.findViewById(R.id.btn_lose)
        val btnClear: Button = itemView.findViewById(R.id.btn_clear)

        init {
            btnWin.setOnClickListener {

                val wins = winsTextView.text.toString().toInt()
                winsTextView.text = (wins + 1).toString()
                val playerInfo = items[adapterPosition]
                playerInfo.wins = wins + 1
                listener.onPlayerInfoChanged(playerInfo)
            }
            btnDraw.setOnClickListener {
                val draw = drawsTextView.text.toString().toInt()
                drawsTextView.text = (draw + 1).toString()
                val playerInfo = items[adapterPosition]
                playerInfo.draws = draw + 1
                listener.onPlayerInfoChanged(playerInfo)
            }
            btnLose.setOnClickListener {
                val lose = lossesTextView.text.toString().toInt()
                lossesTextView.text = (lose + 1).toString()
                val playerInfo = items[adapterPosition]
                playerInfo.losses = lose + 1
                listener.onPlayerInfoChanged(playerInfo)
            }
            btnClear.setOnClickListener {
                winsTextView.text = "0"
                drawsTextView.text = "0"
                lossesTextView.text = "0"
                val playerInfo = items[adapterPosition]
                playerInfo.wins = 0
                playerInfo.draws = 0
                playerInfo.losses = 0
                listener.onPlayerInfoChanged(playerInfo)
            }
        }

        fun bind(item: PlayerInfo) {
            nameTextView.text = item.name
            winsTextView.text = item.wins.toString()
            drawsTextView.text = item.draws.toString()
            lossesTextView.text = item.losses.toString()
        }
    }
}