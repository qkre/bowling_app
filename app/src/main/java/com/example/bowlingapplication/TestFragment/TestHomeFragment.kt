package com.example.bowlingapplication.TestFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bowlingapplication.PlayerAdapter
import com.example.bowlingapplication.PlayerInfo
import com.example.bowlingapplication.R
import com.example.bowlingapplication.databinding.FragmentTestHomeBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.testRv
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val playerAdapter = PlayerAdapter()
        playerAdapter.setListener(object : PlayerAdapter.PlayerInfoListener {
            override fun onPlayerInfoChanged(playerInfo: PlayerInfo) {
                // 이 곳에서 변경된 플레이어 정보를 처리합니다.
            }
        })
        recyclerView.adapter = playerAdapter

        val myPlayer = arrayListOf<PlayerInfo>(
        )
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnAddPlayer.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.test_add_player, null)
            builder.setView(dialogView)

            builder.setPositiveButton("추가") { _, _ ->

                val playerName = dialogView.findViewById<EditText>(R.id.player_name).text.toString()
                myPlayer.add(PlayerInfo(playerName, 0, 0, 0))
                playerAdapter.setItems(myPlayer)
            }



            builder.setNegativeButton("취소", null).show()
        }

        binding.btnSave.setOnClickListener {
            val db = Firebase.firestore
            if (myPlayer.size >= 1) {
                for (player in myPlayer) {
                    val playerId = player.name
                    val playerWins = player.wins
                    val playerDraws = player.draws
                    val playerLosses = player.losses

                    val playerData = hashMapOf(
                        "wins" to playerWins,
                        "draws" to playerDraws,
                        "losses" to playerLosses
                    )

                    db.collection("players").document(playerId).set(playerData)
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                }
            }

        }

    }
}





