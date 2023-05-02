package com.example.bowlingapplication.Fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            for (document in documents) {
                val dateKey = document.id
                val data = db.collection(dateKey)
                data.get().addOnSuccessListener { documents ->
                    for (document in documents) {
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
                            myPlayer.add(
                                PlayerInfo(
                                    playerName,
                                    playerWins,
                                    playerDraws,
                                    playerLosses
                                )
                            )
                        }
                    }
                    val sortedPlayerList = sortByRaiting(myPlayer)
                    showPlayerAdapter.setItems(sortedPlayerList)
                    binding.kingHogu.text =
                        "\uD83E\uDD34\uD83C\uDFFB 호구왕 : ${sortedPlayerList[sortedPlayerList.size - 1].name} \uD83E\uDD34\uD83C\uDFFB"
                }

            }

        }

        binding.btnClear.setOnClickListener {
            Toast.makeText(requireContext(), "삭제하고 싶으시면 버튼을 길게 눌러주세요.", Toast.LENGTH_SHORT).show()
        }
        binding.btnClear.setOnLongClickListener{
            dateList.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val collectionRef = db.collection(document.id)
                    collectionRef.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                        }
                    }
                    collectionRef.document().delete()
                }

                for (document in documents) {
                    document.reference.delete()
                }
                dateList.document().delete()
            }
            showPlayerAdapter.setItems(arrayListOf())
            true
        }

    }

    private fun whoTheHogu(playerList: List<PlayerInfo>): String {
        var minRaiting = 100f
        var hogu = ""
        for (player in playerList) {
            val wins = player.wins
            val draws = player.draws
            val losses = player.losses
            val rating = (wins.toFloat()) / (wins + draws + losses) * 100
            Log.d("ratings :: ", "${player.name} : $rating")
            if (rating < minRaiting) {
                hogu = player.name
            }
        }

        return "🤴🏻현재 호구 : ${hogu}🤴🏻"
    }

    private fun sortByRaiting(playerList: List<PlayerInfo>): List<PlayerInfo> {
        // 각각의 PlayerInfo에 대한 승률을 계산하고 새로운 리스트에 추가합니다.
        val calculatedList = playerList.map {
            val totalGames = it.wins + it.draws + it.losses
            val winRate = if (totalGames == 0) {
                0f
            } else {
                it.wins.toFloat() / totalGames
            }
            Pair(it, winRate)
        }

        // 승률에 따라 내림차순으로 정렬한 새로운 리스트를 반환합니다.
        return calculatedList.sortedByDescending { it.second }.map { it.first }
    }

}