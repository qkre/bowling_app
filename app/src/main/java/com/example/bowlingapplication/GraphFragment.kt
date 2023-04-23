package com.example.bowlingapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.bowlingapplication.databinding.FragmentGraphBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


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

        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val playerList = mapOf(
            "juhyeon" to binding.juhyeonTvWDL,
            "jehyeon" to binding.jehyeonTvWDL,
            "seokyoung" to binding.seokyoungTvWDL
        )

        val costsList = mapOf(
            "juhyeon" to binding.juhyeonTvSum,
            "jehyeon" to binding.jehyeonTvSum,
            "seokyoung" to binding.seokyoungTvSum
        )

        val startDate = Calendar.getInstance(Locale.KOREA).apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 현재 날짜로 초기화
        val today = Calendar.getInstance()
        binding.today.text =
            SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(today.timeInMillis)

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val dateKey = dateFormat.format(today.timeInMillis)

        // 현재 날짜에 저장된 기록 가져와서 화면에 표시
        for ((player, textView) in playerList) {
            val savedWins = sharedPreferences.getInt("${dateKey}_${player}_wins", 0)
            val savedDraws = sharedPreferences.getInt("${dateKey}_${player}_draws", 0)
            val savedLoses = sharedPreferences.getInt("${dateKey}_${player}_losses", 0)

            textView.text = "${savedWins}승 ${savedDraws}무 ${savedLoses}패"
        }

        for ((player, textView) in costsList) {
            val savedCosts = sharedPreferences.getInt("${dateKey}_${player}_costs", 0)

            textView.text = "지출 : ${savedCosts}원"
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

            for ((player, textView) in playerList) {
                val savedWins = sharedPreferences.getInt("${dateKey}_${player}_wins", 0)
                val savedDraws = sharedPreferences.getInt("${dateKey}_${player}_draws", 0)
                val savedLoses = sharedPreferences.getInt("${dateKey}_${player}_losses", 0)

                textView.text = "${savedWins}승 ${savedDraws}무 ${savedLoses}패"
            }

            for ((player, textView) in costsList) {
                val savedCosts = sharedPreferences.getInt("${dateKey}_${player}_costs", 0)

                textView.text = "볼링에 쓴 금액 : ${savedCosts}원"
            }

            binding.btnDayClear.setOnClickListener {
                val editor = sharedPreferences.edit()
                for ((player, textView) in playerList) {
                    editor.putInt("${dateKey}_${player}_wins", 0)
                    editor.putInt("${dateKey}_${player}_draws", 0)
                    editor.putInt("${dateKey}_${player}_losses", 0)
                    editor.putInt("${dateKey}_${player}_costs", 0)
                }

                binding.juhyeonTvWDL.text = "0승 0무 0패"
                binding.juhyeonTvSum.text = "볼링에 쓴 금액 :0원"
                binding.jehyeonTvWDL.text = "0승 0무 0패"
                binding.jehyeonTvSum.text = "볼링에 쓴 금액 :0원"
                binding.seokyoungTvWDL.text = "0승 0무 0패"
                binding.seokyoungTvSum.text = "볼링에 쓴 금액 :0원"

                editor.apply()
                Toast.makeText(requireContext(), "초기화 되었습니다.", Toast.LENGTH_SHORT).show()

            }

            binding.juhyeonTvWDL.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                val view =
                    LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_data, null, false)

                builder.setPositiveButton("저장") { _, _ ->
                    val win =
                        if (view.findViewById<EditText>(R.id.win_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.win_editText
                        ).text.toString().toInt()
                    val draw =
                        if (view.findViewById<EditText>(R.id.draw_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.draw_editText
                        ).text.toString().toInt()
                    val loss =
                        if (view.findViewById<EditText>(R.id.loss_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.loss_editText
                        ).text.toString().toInt()

                    binding.juhyeonTvWDL.text = "${win}승 ${draw}무 ${loss}패"
                    binding.juhyeonTvSum.text = "지출 : ${draw * 2500 + loss * 5000}원"
                }

                builder.setView(view)
                builder.show()
            }

            binding.jehyeonTvWDL.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                val view =
                    LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_data, null, false)

                builder.setPositiveButton("저장") { _, _ ->
                    val win =
                        if (view.findViewById<EditText>(R.id.win_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.win_editText
                        ).text.toString().toInt()
                    val draw =
                        if (view.findViewById<EditText>(R.id.draw_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.draw_editText
                        ).text.toString().toInt()
                    val loss =
                        if (view.findViewById<EditText>(R.id.loss_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.loss_editText
                        ).text.toString().toInt()

                    binding.jehyeonTvWDL.text = "${win}승 ${draw}무 ${loss}패"
                    binding.jehyeonTvSum.text = "지출 : ${draw * 2500 + loss * 5000}원"
                }

                builder.setView(view)
                builder.show()
            }

            binding.seokyoungTvWDL.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                val view =
                    LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_data, null, false)

                builder.setPositiveButton("저장") { _, _ ->
                    val win =
                        if (view.findViewById<EditText>(R.id.win_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.win_editText
                        ).text.toString().toInt()
                    val draw =
                        if (view.findViewById<EditText>(R.id.draw_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.draw_editText
                        ).text.toString().toInt()
                    val loss =
                        if (view.findViewById<EditText>(R.id.loss_editText).text.toString() == "") 0 else view.findViewById<EditText>(
                            R.id.loss_editText
                        ).text.toString().toInt()

                    binding.seokyoungTvWDL.text = "${win}승 ${draw}무 ${loss}패"
                    binding.seokyoungTvSum.text = "지출 : ${draw * 2500 + loss * 5000}원"
                }

                builder.setView(view)
                builder.show()
            }

            binding.btnDayMod.setOnClickListener {
                val editor = sharedPreferences.edit()

                for ((player, textView) in playerList) {
                    // 저장할 값을 구함
                    val record = textView.text.toString()
                    val wins = record.substringBefore("승").toInt()
                    val draws = record.substringAfter("승 ").substringBefore("무").toInt()
                    val losses = record.substringAfter("무 ").substringBefore("패").toInt()

                    val existingWins = sharedPreferences.getInt("${dateKey}_${player}_wins", 0)
                    val existingDraws = sharedPreferences.getInt("${dateKey}_${player}_draws", 0)
                    val existingLosses = sharedPreferences.getInt("${dateKey}_${player}_losses", 0)
                    var totalWins = sharedPreferences.getInt("${player}_wins", 0)
                    var totalDraws = sharedPreferences.getInt("${player}_draws", 0)
                    var totalLosses = sharedPreferences.getInt("${player}_losses", 0)

                    editor.putInt("${dateKey}_${player}_wins", wins)
                    editor.putInt("${dateKey}_${player}_draws", draws)
                    editor.putInt("${dateKey}_${player}_losses", losses)
                    editor.putInt("${dateKey}_${player}_costs", draws * 2500 + losses * 5000)

                    editor.putInt("${player}_wins", totalWins - existingWins + wins)
                    editor.putInt("${player}_draws", totalDraws - existingDraws + draws)
                    editor.putInt("${player}_losses", totalLosses - existingLosses + losses)

                    editor.putInt("${player}_costs", (totalDraws - existingDraws + draws) * 2500 + (totalLosses - existingLosses + losses) * 5000)

                    editor.putBoolean("${dateKey}", true)

                    editor.apply()

                    totalWins = 0
                    totalDraws = 0
                    totalLosses = 0

                    for(date in startDate .. currentDate step TimeUnit.DAYS.toMillis(1)){
                        val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)

                        if(sharedPreferences.getBoolean("$formattedDate", false)){
                            totalWins += sharedPreferences.getInt("${formattedDate}_${player}_wins", 0)
                            totalDraws += sharedPreferences.getInt("${formattedDate}_${player}_draws", 0)
                            totalLosses += sharedPreferences.getInt("${formattedDate}_${player}_losses", 0)
                        }
                    }

                    val totalGame = totalWins + totalDraws + totalLosses
                    editor.putFloat("${dateKey}_${player}_ratings", totalWins.toFloat() / totalGame * 100)
                    editor.apply()
                }

                Toast.makeText(requireContext(), "수정 되었습니다.", Toast.LENGTH_SHORT).show()
            }

            binding.btnDayClear.setOnClickListener {
                val editor = sharedPreferences.edit()
                for ((player, textView) in playerList) {
                    editor.putInt("${dateKey}_${player}_wins", 0)
                    editor.putInt("${dateKey}_${player}_draws", 0)
                    editor.putInt("${dateKey}_${player}_losses", 0)
                    editor.putInt("${dateKey}_${player}_costs", 0)
                    editor.putBoolean("${dateKey}",false)
                }

                binding.juhyeonTvWDL.text = "0승 0무 0패"
                binding.juhyeonTvSum.text = "지출 :0원"
                binding.jehyeonTvWDL.text = "0승 0무 0패"
                binding.jehyeonTvSum.text = "지출 :0원"
                binding.seokyoungTvWDL.text = "0승 0무 0패"
                binding.seokyoungTvSum.text = "지출 :0원"

                editor.apply()
                Toast.makeText(requireContext(), "초기화 되었습니다.", Toast.LENGTH_SHORT).show()

            }

        }

    }
}