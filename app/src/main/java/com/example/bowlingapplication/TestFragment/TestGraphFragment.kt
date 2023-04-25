package com.example.bowlingapplication.TestFragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bowlingapplication.PlayerInfo
import com.example.bowlingapplication.R
import com.example.bowlingapplication.ShowPlayerAdapter
import com.example.bowlingapplication.databinding.FragmentTestGraphBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class TestGraphFragment : Fragment() {
    private var _binding: FragmentTestGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore

        val today = Calendar.getInstance()
        binding.today.text =
            SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(today.timeInMillis)
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val dateKey = dateFormat.format(today.timeInMillis)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val showPlayerAdapter = ShowPlayerAdapter()


        recyclerView.adapter = showPlayerAdapter

        val dateDocument = db.collection(dateKey)
        dateDocument.get().addOnSuccessListener { documents ->
            val myPlayer = arrayListOf<PlayerInfo>()
            for (document in documents) {
                val playerName = document.id
                val playerWins = document.getLong("wins")?.toInt() ?: 0
                val playerDraws = document.getLong("draws")?.toInt() ?: 0
                val playerLosses = document.getLong("losses")?.toInt() ?: 0

                myPlayer.add(PlayerInfo(playerName, playerWins, playerDraws, playerLosses))
            }
            showPlayerAdapter.setItems(myPlayer)
        }.addOnFailureListener { _ ->
            val myPlayer = arrayListOf<PlayerInfo>(PlayerInfo("이날은 데이터가 없네요.", 0, 0, 0))
            showPlayerAdapter.setItems(myPlayer)
        }

        binding.btnDayMod.setOnClickListener {
            val testModFragment = TestModFragment()
            val bundle = Bundle().apply { putString("dateKey", dateKey) }
            testModFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, testModFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnDayClear.setOnClickListener {
            val collectionRef = db.collection(dateKey)

            // Delete all documents in the collection
            collectionRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error deleting documents", exception)
            }

// Delete the collection
            collectionRef.document().delete().addOnSuccessListener {
                Log.d(TAG, "Collection successfully deleted!")
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error deleting collection", exception)
            }
            val myPlayer = arrayListOf<PlayerInfo>()
            showPlayerAdapter.setItems(myPlayer)

        }

        binding.caledarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 날짜가 변경될 때마다 텍스트뷰에 업데이트
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            binding.today.text =
                SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(calendar.timeInMillis)
            val currentDate = calendar.timeInMillis
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val dateKey = dateFormat.format(currentDate)
            val dateDocument = db.collection(dateKey)

            dateDocument.get().addOnSuccessListener { documents ->
                val myPlayer = arrayListOf<PlayerInfo>()
                for (document in documents) {
                    val playerName = document.id
                    val playerWins = document.getLong("wins")?.toInt() ?: 0
                    val playerDraws = document.getLong("draws")?.toInt() ?: 0
                    val playerLosses = document.getLong("losses")?.toInt() ?: 0

                    myPlayer.add(PlayerInfo(playerName, playerWins, playerDraws, playerLosses))
                }
                showPlayerAdapter.setItems(myPlayer)
            }.addOnFailureListener { _ ->
                val myPlayer = arrayListOf(PlayerInfo("이날은 데이터가 없네요.", 0, 0, 0))
                showPlayerAdapter.setItems(myPlayer)
            }

            binding.btnDayClear.setOnClickListener {
                val collectionRef = db.collection(dateKey)

                // Delete all documents in the collection
                collectionRef.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error deleting documents", exception)
                }

                // Delete the collection
                collectionRef.document().delete().addOnSuccessListener {
                    Log.d(TAG, "Collection successfully deleted!")
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error deleting collection", exception)
                }
                val myPlayer = arrayListOf<PlayerInfo>()
                showPlayerAdapter.setItems(myPlayer)

            }

            binding.btnDayMod.setOnClickListener {
                val testModFragment = TestModFragment()
                val bundle = Bundle().apply { putString("dateKey", dateKey) }
                testModFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, testModFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }
}