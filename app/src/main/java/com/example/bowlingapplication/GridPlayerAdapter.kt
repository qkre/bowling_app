package com.example.bowlingapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GridPlayerAdapter : RecyclerView.Adapter<GridPlayerAdapter.ViewHolder>() {

    private var items: MutableList<PlayerInfo> = mutableListOf()
    private var checkedItems = arrayListOf<PlayerInfo>()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val playerName: TextView = itemView.findViewById(R.id.player_name)
        val addButton: CheckBox = itemView.findViewById(R.id.add_btn)

        init {
            addButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    checkedItems.add(PlayerInfo(playerName.text.toString(), 0, 0, 0))
                } else {
                    checkedItems.remove(PlayerInfo(playerName.text.toString(), 0, 0,0))
                }
            }
        }

        fun bind(item: PlayerInfo){
            playerName.text = item.name
        }
    }

    fun setItems(items: List<PlayerInfo>) {
        this.items = items as MutableList<PlayerInfo>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_add_player_data, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    fun getPlayerInfoList(): List<PlayerInfo> {
        Log.d("playerList :: ", checkedItems.toString())
        return checkedItems
    }


}