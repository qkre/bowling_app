package com.example.bowlingapplication.Fragment

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
import com.example.bowlingapplication.databinding.FragmentModBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ModFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ModFragment : Fragment() {
    private var _binding: FragmentModBinding? = null
    private val binding get() = _binding!!
    val db = Firebase.firestore
    var myPlayer = arrayListOf<PlayerInfo>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val addPlayerAdapter = AddPlayerAdapter()
        recyclerView.adapter = addPlayerAdapter

        addPlayerAdapter.setListener(object : AddPlayerAdapter.PlayerInfoListener {
            override fun onPlayerInfoChanged(playerInfo: PlayerInfo) {
                // 이 곳에서 변경된 플레이어 정보를 처리합니다.
            }
        })

        val dateKey = arguments?.getString("dateKey")
        if (dateKey != null) {
            val existData = db.collection(dateKey!!)
            existData.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val playerName = document.id
                    val playerWins = document.getLong("wins")?.toInt() ?: 0
                    val playerDraws = document.getLong("draws")?.toInt() ?: 0
                    val playerLosses = document.getLong("losses")?.toInt() ?: 0
                    val playerInfo = PlayerInfo(playerName, playerWins, playerDraws, playerLosses)

                    myPlayer.add(playerInfo)
                }
                addPlayerAdapter.setItems(myPlayer)
                Log.d("Player :: ", myPlayer.toString())
            }


            binding.btnAddPlayer.setOnClickListener {
                myPlayer = addPlayerAdapter.getItem()
                val builder = AlertDialog.Builder(requireContext())
                val dialogView =
                    LayoutInflater.from(requireContext())
                        .inflate(R.layout.add_new_player, null)
                builder.setView(dialogView)

                builder.setPositiveButton("추가") { _, _ ->
                    val playerName =
                        dialogView.findViewById<EditText>(R.id.new_player_name).text.toString()
                    myPlayer.add(PlayerInfo(playerName, 0, 0, 0))
                    addPlayerAdapter.setItems(myPlayer)
                }

                builder.setNegativeButton("취소", null).show()
            }

            binding.btnSavePlayer.setOnClickListener {

                val db = Firebase.firestore
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
                        val existPlayer = db.collection(dateKey)
                        existPlayer.get().addOnSuccessListener { players ->
                            for (player in players) {
                                if (!(player.id in myPlayer.map { it.name })) {
                                    existPlayer.document(player.id).delete()
                                    Log.d("name :: ", myPlayer.toString())
                                }
                            }
                        }
                    }
                }

                db.collection("dateList").document(dateKey!!).set(hashMapOf("date" to dateKey))

                val fragment = GraphFragment()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()

            }

        }
        else{
            Log.d("DateKey :: ", "null")
        }
    }
}