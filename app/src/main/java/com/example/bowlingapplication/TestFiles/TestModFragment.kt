package com.example.bowlingapplication.TestFiles

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bowlingapplication.AddPlayerAdapter
import com.example.bowlingapplication.PlayerInfo
import com.example.bowlingapplication.R
import com.example.bowlingapplication.databinding.FragmentTestModBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TestModFragment : Fragment() {
    private var _binding: FragmentTestModBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestModBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val addPlayerAdapter = AddPlayerAdapter()
        addPlayerAdapter.setListener(object : AddPlayerAdapter.PlayerInfoListener {
            override fun onPlayerInfoChanged(playerInfo: PlayerInfo) {
            }
        })

        recyclerView.adapter = addPlayerAdapter

        val myPlayer = arrayListOf<PlayerInfo>()

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

        binding.btnSavePlayer.setOnClickListener {

            val db = Firebase.firestore
            val dateKey = arguments?.getString("dateKey")

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

                    db.collection(dateKey!!).document(playerName).set(playerData)
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
            }
            db.collection("dateList").document(dateKey!!).set(hashMapOf("date" to dateKey))

            requireActivity().supportFragmentManager.popBackStack()

        }

    }
}