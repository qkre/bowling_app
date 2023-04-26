package com.example.bowlingapplication.Fragment

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bowlingapplication.AddPlayerAdapter
import com.example.bowlingapplication.PlayerInfo
import com.example.bowlingapplication.R
import com.example.bowlingapplication.databinding.FragmentHomeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val db = Firebase.firestore
    val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    var myPlayer = arrayListOf<PlayerInfo>(
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.testRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val addPlayerAdapter = AddPlayerAdapter()
        addPlayerAdapter.setListener(object : AddPlayerAdapter.PlayerInfoListener {
            override fun onPlayerInfoChanged(playerInfo: PlayerInfo) {
                // 이 곳에서 변경된 플레이어 정보를 처리합니다.
            }
        })
        recyclerView.adapter = addPlayerAdapter



        val existData = db.collection(date)
        existData.get().addOnSuccessListener { documents ->
            for (document in documents){
                val playerName = document.id
                val playerWins = document.getLong("wins")?.toInt() ?: 0
                val playerDraws = document.getLong("draws")?.toInt() ?: 0
                val playerLosses = document.getLong("losses")?.toInt() ?: 0
                val playerInfo = PlayerInfo(playerName, playerWins, playerDraws, playerLosses)

                myPlayer.add(playerInfo)
            }
            addPlayerAdapter.setItems(myPlayer)
        }

        binding.btnAddPlayer.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.add_player_data, null)
            builder.setView(dialogView)

            builder.setPositiveButton("추가") { _, _ ->

                val playerName = dialogView.findViewById<EditText>(R.id.player_name).text.toString()
                myPlayer.add(PlayerInfo(playerName, 0, 0, 0))
                addPlayerAdapter.setItems(myPlayer)
            }
            builder.setNegativeButton("취소", null).show()
        }

        binding.btnSave.setOnClickListener {
            myPlayer = addPlayerAdapter.getItem()
            if (myPlayer.size >= 1) {
                for (player in myPlayer) {
                    val playerName = player.name
                    val playerWins = player.wins
                    val playerDraws = player.draws
                    val playerLosses = player.losses

                    val playerData = hashMapOf(
                        "wins" to playerWins,
                        "draws" to playerDraws,
                        "losses" to playerLosses
                    )

                    db.collection(date).document(playerName).set(playerData)
                        .addOnSuccessListener {
                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                ContentValues.TAG,
                                "Error writing document",
                                e
                            )
                        }
                }
                val existPlayer = db.collection(date)
                existPlayer.get().addOnSuccessListener {players ->
                    for(player in players){
                        if(!(player.id in myPlayer.map{it.name})){
                            existPlayer.document(player.id).delete()
                        }
                    }
                }
            }

            db.collection("dateList").document(date).set(hashMapOf("date" to date))
            Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

    }
}