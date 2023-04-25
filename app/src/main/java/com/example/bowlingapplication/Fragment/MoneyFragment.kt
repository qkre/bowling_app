package com.example.bowlingapplication.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bowlingapplication.PlayerInfo
import com.example.bowlingapplication.ShowPlayerAdapter
import com.example.bowlingapplication.databinding.FragmentMoneyBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MoneyFragment : Fragment() {
    private var _binding: FragmentMoneyBinding? = null
    private val binding get() = _binding!!
    val startDate = Calendar.getInstance(Locale.KOREA).apply {
        set(Calendar.YEAR, 2023)
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val currentDate = System.currentTimeMillis()

    val db = Firebase.firestore
    val dateList = arrayListOf<String>()
    val showPlayerAdapter = ShowPlayerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = showPlayerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dateList = db.collection("dateList")

        dateList.get().addOnSuccessListener { documents ->
            val myPlayer = arrayListOf<PlayerInfo>()
            for (document in documents){
                val dateKey = document.id
                val data = db.collection(dateKey)
                data.get().addOnSuccessListener {documents ->
                    for(document in documents) {
                        val playerName = document.id
                        val playerWins = document.getLong("wins")?.toInt() ?: 0
                        val playerDraws = document.getLong("draws")?.toInt() ?: 0
                        val playerLosses = document.getLong("losses")?.toInt() ?: 0

                        val playerInfo = myPlayer.find { it.name == playerName }
                        if (playerInfo != null) {
                            playerInfo.wins += playerWins
                            playerInfo.losses += playerLosses
                            playerInfo.draws += playerDraws
                        } else {
                            myPlayer.add(PlayerInfo(playerName, playerWins, playerDraws, playerLosses))
                        }

                    }
                    showPlayerAdapter.setItems(myPlayer)
                    binding.kingHogu.text = whoTheHogu(myPlayer)
                }
            }
        }

        binding.btnClear.setOnClickListener {
            dateList.get().addOnSuccessListener {documents ->
                for (document in documents) {
                    val collectionRef = db.collection(document.id)
                    collectionRef.get().addOnSuccessListener {documents ->
                        for(document in documents){
                            document.reference.delete()
                        }
                    }
                    collectionRef.document().delete()
                }

                for(document in documents){
                    document.reference.delete()
                }
                dateList.document().delete()
            }
            showPlayerAdapter.setItems(arrayListOf())
        }
    }

    private fun whoTheHogu(playerList: List<PlayerInfo>):String{
        var maxCost = 0
        var hogu = ""
        for(player in playerList){
            val paidCost = player.draws*2500 + player.losses * 5000
            if(maxCost <= paidCost){
                maxCost = paidCost
                hogu = player.name
            }
        }

        return "🤴🏻현재 호구 : ${hogu}🤴🏻"
    }

}