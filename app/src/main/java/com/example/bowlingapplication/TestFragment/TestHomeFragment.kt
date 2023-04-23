package com.example.bowlingapplication.TestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bowlingapplication.R
import com.example.bowlingapplication.databinding.FragmentTestHomeBinding

class TestHomeFragment : Fragment() {
    private var _binding: FragmentTestHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.testRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val playerAdapter = PlayerAdapter()
        val myPlayer = arrayListOf<PlayerAdapter.PlayerInfo>(
            PlayerAdapter.PlayerInfo("박제현", 1, 1, 1)
        )
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnAddPlayer.setOnClickListener {
            myPlayer.add(PlayerAdapter.PlayerInfo("", 0, 0, 0))
            recyclerView.adapter = playerAdapter
            playerAdapter.setItems(myPlayer)
        }

        myPlayer[0].name


    }
}

class PlayerAdapter : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    private var items: List<PlayerInfo> = emptyList()

    fun setItems(items: List<PlayerInfo>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_player_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.text_name)
        private val winsTextView: TextView = itemView.findViewById(R.id.text_wins)
        private val drawsTextView: TextView = itemView.findViewById(R.id.text_draws)
        private val lossesTextView: TextView = itemView.findViewById(R.id.text_losses)

        fun bind(item: PlayerInfo) {
            nameTextView.text = item.name
            winsTextView.text = item.wins.toString()
            drawsTextView.text = item.draws.toString()
            lossesTextView.text = item.losses.toString()
        }
    }

    data class PlayerInfo(val name: String, val wins: Int, val draws: Int, val losses: Int)
}


