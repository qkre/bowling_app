package com.example.bowlingapplication.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bowlingapplication.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        var juhyeon_currentWins = binding.juhyeonTvWDL.text.toString().substringBefore("승").toInt()
        var juhyeon_currentDraws = binding.juhyeonTvWDL.text.toString().substringAfter("승 ").substringBefore("무").toInt()
        var juhyeon_currentLoses = binding.juhyeonTvWDL.text.toString().substringAfter("무 ").substringBefore("패").toInt()
        binding.juhyeonBtnWin.setOnClickListener {
            juhyeon_currentWins++
            binding.juhyeonTvWDL.text = "${juhyeon_currentWins}승 ${juhyeon_currentDraws}무 ${juhyeon_currentLoses}패"
        }
        binding.juhyeonBtnDraw.setOnClickListener {
            juhyeon_currentDraws++
            binding.juhyeonTvWDL.text = "${juhyeon_currentWins}승 ${juhyeon_currentDraws}무 ${juhyeon_currentLoses}패"
        }
        binding.juhyeonBtnLose.setOnClickListener {
            juhyeon_currentLoses++
            binding.juhyeonTvWDL.text = "${juhyeon_currentWins}승 ${juhyeon_currentDraws}무 ${juhyeon_currentLoses}패"
        }
        binding.juhyeonBtnClear.setOnClickListener {
            juhyeon_currentDraws = 0
            juhyeon_currentLoses = 0
            juhyeon_currentWins = 0
            binding.juhyeonTvWDL.text = "0승 0무 0패"
        }

        var jehyeon_currentWins = binding.jehyeonTvWDL.text.toString().substringBefore("승").toInt()
        var jehyeon_currentDraws = binding.jehyeonTvWDL.text.toString().substringAfter("승 ").substringBefore("무").toInt()
        var jehyeon_currentLoses = binding.jehyeonTvWDL.text.toString().substringAfter("무 ").substringBefore("패").toInt()
        binding.jehyeonBtnWin.setOnClickListener {
            jehyeon_currentWins++
            binding.jehyeonTvWDL.text = "${jehyeon_currentWins}승 ${jehyeon_currentDraws}무 ${jehyeon_currentLoses}패"
        }
        binding.jehyeonBtnDraw.setOnClickListener {
            jehyeon_currentDraws++
            binding.jehyeonTvWDL.text = "${jehyeon_currentWins}승 ${jehyeon_currentDraws}무 ${jehyeon_currentLoses}패"
        }
        binding.jehyeonBtnLose.setOnClickListener {
            jehyeon_currentLoses++
            binding.jehyeonTvWDL.text = "${jehyeon_currentWins}승 ${jehyeon_currentDraws}무 ${jehyeon_currentLoses}패"
        }
        binding.jehyeonBtnClear.setOnClickListener {
            jehyeon_currentDraws = 0
            jehyeon_currentLoses = 0
            jehyeon_currentWins = 0
            binding.jehyeonTvWDL.text = "0승 0무 0패"
        }

        var seokyoung_currentWins = binding.seokyoungTvWDL.text.toString().substringBefore("승").toInt()
        var seokyoung_currentDraws = binding.seokyoungTvWDL.text.toString().substringAfter("승 ").substringBefore("무").toInt()
        var seokyoung_currentLoses = binding.seokyoungTvWDL.text.toString().substringAfter("무 ").substringBefore("패").toInt()
        binding.seokyoungBtnWin.setOnClickListener {
            seokyoung_currentWins++
            binding.seokyoungTvWDL.text = "${seokyoung_currentWins}승 ${seokyoung_currentDraws}무 ${seokyoung_currentLoses}패"
        }
        binding.seokyoungBtnDraw.setOnClickListener {
            seokyoung_currentDraws++
            binding.seokyoungTvWDL.text = "${seokyoung_currentWins}승 ${seokyoung_currentDraws}무 ${seokyoung_currentLoses}패"
        }
        binding.seokyoungBtnLose.setOnClickListener {
            seokyoung_currentLoses++
            binding.seokyoungTvWDL.text = "${seokyoung_currentWins}승 ${seokyoung_currentDraws}무 ${seokyoung_currentLoses}패"
        }
        binding.seokyoungBtnClear.setOnClickListener {
            seokyoung_currentDraws = 0
            seokyoung_currentLoses = 0
            seokyoung_currentWins = 0
            binding.seokyoungTvWDL.text = "0승 0무 0패"
        }



        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val playerTextViewMap = mapOf(
            "juhyeon" to binding.juhyeonTvWDL,
            "jehyeon" to binding.jehyeonTvWDL,
            "seokyoung" to binding.seokyoungTvWDL
        )

        binding.btnSave.setOnClickListener {
            val editor = sharedPreferences.edit()

            val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            for ((player, textView) in playerTextViewMap) {
                // 저장할 값을 구함
                val record = textView.text.toString()
                val wins = record.substringBefore("승").toInt()
                val draws = record.substringAfter("승 ").substringBefore("무").toInt()
                val losses = record.substringAfter("무 ").substringBefore("패").toInt()

                // 오늘 기존에 저장되어있는 값을 불러옴
                var todayWins = sharedPreferences.getInt("${date}_${player}_wins", 0)
                var todayDraws = sharedPreferences.getInt("${date}_${player}_draws", 0)
                var todayLosses = sharedPreferences.getInt("${date}_${player}_losses", 0)

                // 전체 기록 값을 불러옴
                var totalWins = sharedPreferences.getInt("${player}_wins", 0)
                var totalDraws = sharedPreferences.getInt("${player}_draws", 0)
                var totalLosses = sharedPreferences.getInt("${player}_losses", 0)

                editor.putInt("${date}_${player}_wins", wins + todayWins)
                editor.putInt("${date}_${player}_draws", draws + todayDraws)
                editor.putInt("${date}_${player}_losses", losses + todayLosses)
                editor.putInt("${date}_${player}_costs", (draws + todayDraws) * 2500 + (losses + todayLosses) * 5000)

                editor.putInt("${player}_wins", totalWins + wins)
                editor.putInt("${player}_draws", totalDraws + draws)
                editor.putInt("${player}_losses", totalLosses + losses)
                editor.apply()

                totalWins = sharedPreferences.getInt("${player}_wins", 0)
                totalDraws = sharedPreferences.getInt("${player}_draws", 0)
                totalLosses = sharedPreferences.getInt("${player}_losses", 0)

                editor.putInt("${player}_costs", totalDraws * 2500 + totalLosses * 5000)

                val totalGames = (totalWins + totalDraws + totalLosses).toFloat()

                editor.putFloat("${date}_${player}_ratings", totalWins.toFloat() / totalGames * 100)

                editor.putBoolean("${date}", true)

            }
            editor.apply()

            Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()

            juhyeon_currentDraws = 0
            juhyeon_currentLoses = 0
            juhyeon_currentWins = 0
            binding.juhyeonTvWDL.text = "0승 0무 0패"
            jehyeon_currentDraws = 0
            jehyeon_currentLoses = 0
            jehyeon_currentWins = 0
            binding.jehyeonTvWDL.text = "0승 0무 0패"
            seokyoung_currentDraws = 0
            seokyoung_currentLoses = 0
            seokyoung_currentWins = 0
            binding.seokyoungTvWDL.text = "0승 0무 0패"
        }

    }
}