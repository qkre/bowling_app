package com.example.bowlingapplication.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bowlingapplication.PlayerInfo
import com.example.bowlingapplication.R
import com.example.bowlingapplication.ShowPlayerMiniAdapter
import com.example.bowlingapplication.databinding.FragmentGraphBinding
import com.google.android.material.datepicker.DayViewDecorator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class GraphFragment : Fragment() {
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!
    val dateList = arrayListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        val showPlayerMiniAdapter = ShowPlayerMiniAdapter()

        val calendarView = binding.calendarView
        calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
        calendarView.setSelectedDate(CalendarDay.today())

        recyclerView.adapter = showPlayerMiniAdapter

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
            showPlayerMiniAdapter.setItems(myPlayer)
        }.addOnFailureListener { _ ->
            val myPlayer = arrayListOf<PlayerInfo>(PlayerInfo("이날은 데이터가 없네요.", 0, 0, 0))
            showPlayerMiniAdapter.setItems(myPlayer)
        }

        binding.btnDayMod.setOnClickListener {
            val ModFragment = ModFragment()
            val bundle = Bundle().apply { putString("dateKey", dateKey) }
            ModFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ModFragment).addToBackStack(null).commit()
        }

        binding.btnDayClear.setOnClickListener {
            Toast.makeText(requireContext(), "삭제하고 싶으시면 버튼을 길게 눌러주세요.", Toast.LENGTH_SHORT).show()
        }

        binding.btnDayClear.setOnLongClickListener {
            val dateRef = db.collection("dateList")
            dateRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.id == dateKey) {
                        document.reference.delete()
                        dateList.remove(dateKey)
                        Log.d("dateList :: ", dateList.toString())
                    }
                }
                calendarView.removeDecorators()
                calendarView.addDecorator(CustomDayViewDecorator(dateList))

                val collectionRef = db.collection(dateKey)
                collectionRef.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                    // Delete the collection after all documents have been deleted
                    collectionRef.document().delete().addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Collection successfully deleted!")
                    }.addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error deleting collection", exception)
                    }
                    val myPlayer = arrayListOf<PlayerInfo>()
                    showPlayerMiniAdapter.setItems(myPlayer)
                }.addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error deleting documents", exception)
                }
            }


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
            showPlayerMiniAdapter.setItems(myPlayer)

            Toast.makeText(requireContext(), "데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show()

            true
        }






        db.collection("dateList").get().addOnSuccessListener { documents ->
            for (document in documents) {
                dateList.add(document.id)
            }
            calendarView.addDecorator(CustomDayViewDecorator(dateList))

        }
        calendarView.setOnDateChangedListener { _, date, _ ->
            val year = date.year
            val month = if (date.month < 10) {
                "0${date.month}"
            } else {
                "${date.month}"
            }
            val day = if (date.day < 10) {
                "0${date.day}"
            } else {
                "${date.day}"
            }
            val dateKey = "$year$month$day"
            Log.d("DateKey :: ", dateKey)

            binding.today.text = "${year}년 ${month}월 ${day}일"

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
                showPlayerMiniAdapter.setItems(myPlayer)
            }.addOnFailureListener { _ ->
                val myPlayer = arrayListOf(PlayerInfo("이날은 데이터가 없네요.", 0, 0, 0))
                showPlayerMiniAdapter.setItems(myPlayer)
            }

            binding.btnDayClear.setOnClickListener {
                Toast.makeText(requireContext(), "삭제하고 싶으시면 버튼을 길게 눌러주세요.", Toast.LENGTH_SHORT)
                    .show()
            }

            binding.btnDayClear.setOnLongClickListener {
                val dateRef = db.collection("dateList")
                dateRef.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id == dateKey) {
                            document.reference.delete()
                            dateList.remove(dateKey)
                            Log.d("dateList :: ", dateList.toString())
                        }
                    }
                    calendarView.removeDecorators()
                    calendarView.addDecorator(CustomDayViewDecorator(dateList))

                    val collectionRef = db.collection(dateKey)
                    collectionRef.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                        }
                        // Delete the collection after all documents have been deleted
                        collectionRef.document().delete().addOnSuccessListener {
                            Log.d(ContentValues.TAG, "Collection successfully deleted!")
                        }.addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error deleting collection", exception)
                        }
                        val myPlayer = arrayListOf<PlayerInfo>()
                        showPlayerMiniAdapter.setItems(myPlayer)
                    }.addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error deleting documents", exception)
                    }
                }


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
                showPlayerMiniAdapter.setItems(myPlayer)

                true
            }

            binding.btnDayMod.setOnClickListener {
                val ModFragment = ModFragment()
                val bundle = Bundle().apply { putString("dateKey", dateKey) }
                ModFragment.arguments = bundle
                Log.d("dateKey :: ", dateKey)

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, ModFragment).addToBackStack(null).commit()
            }


        }

    }

    @SuppressLint("ParcelCreator")
    class TodayDecorator : DayViewDecorator(),
        com.prolificinteractive.materialcalendarview.DayViewDecorator {
        private var date = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(StyleSpan(Typeface.BOLD))
            view?.addSpan(RelativeSizeSpan(1.4f))
            view?.addSpan(ForegroundColorSpan(Color.parseColor("#1D872A")))
        }

        override fun describeContents(): Int {
            TODO("Not yet implemented")
        }

        override fun writeToParcel(p0: Parcel, p1: Int) {
            TODO("Not yet implemented")
        }
    }

    @SuppressLint("ParcelCreator")
    class CustomDayViewDecorator(private val dateList: ArrayList<String>) : DayViewDecorator(),
        com.prolificinteractive.materialcalendarview.DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            for (dateString in dateList) {
                val date = dateFormat.parse(dateString)
                val calendar = Calendar.getInstance().apply {
                    time = date
                }
                val year = calendar.get(Calendar.YEAR)
                val month =
                    calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH의 범위는 0부터 11이므로 1을 더해줍니다.
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val calendarDay = CalendarDay.from(year, month, dayOfMonth)
                if (day?.equals(calendarDay) == true) {
                    return true
                }
            }
            return false
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(StyleSpan(Typeface.BOLD))
            view?.addSpan(RelativeSizeSpan(1.6f))
            view?.addSpan(ForegroundColorSpan(Color.parseColor("#FF0000")))
        }

        override fun describeContents(): Int {
            TODO("Not yet implemented")
        }

        override fun writeToParcel(p0: Parcel, p1: Int) {
            TODO("Not yet implemented")
        }
    }
}

