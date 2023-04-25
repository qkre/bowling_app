package com.example.bowlingapplication.Fragment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bowlingapplication.PlayerInfo
import com.example.bowlingapplication.R
import com.example.bowlingapplication.ShowPlayerAdapter
import com.example.bowlingapplication.TestFiles.TestModFragment
import com.example.bowlingapplication.databinding.FragmentGraphBinding
import com.google.android.material.datepicker.DayViewDecorator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class GraphFragment : Fragment() {
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val ModFragment = ModFragment()
            val bundle = Bundle().apply { putString("dateKey", dateKey) }
            ModFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ModFragment)
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
                Log.w(ContentValues.TAG, "Error deleting documents", exception)
            }

// Delete the collection
            collectionRef.document().delete().addOnSuccessListener {
                Log.d(ContentValues.TAG, "Collection successfully deleted!")
            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error deleting collection", exception)
            }
            val myPlayer = arrayListOf<PlayerInfo>()
            showPlayerAdapter.setItems(myPlayer)

            val dateRef = db.collection("dateList")
            dateRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.id == dateKey) {
                        document.reference.delete()
                    }
                }
            }

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
                    Log.w(ContentValues.TAG, "Error deleting documents", exception)
                }

                // Delete the collection
                collectionRef.document().delete().addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Collection successfully deleted!")
                }.addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error deleting collection", exception)
                }
                val myPlayer = arrayListOf<PlayerInfo>()
                showPlayerAdapter.setItems(myPlayer)

            }

            binding.btnDayMod.setOnClickListener {
                val ModFragment = ModFragment()
                val bundle = Bundle().apply { putString("dateKey", dateKey) }
                ModFragment.arguments = bundle
                Log.d("dateKey :: ", dateKey)

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, ModFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }

}