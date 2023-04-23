package com.example.bowlingapplication

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bowlingapplication.databinding.FragmentMoneyBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MoneyFragment : Fragment() {
    private var _binding: FragmentMoneyBinding? = null
    private val binding get() = _binding!!
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        lineChart = binding.lineChart

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

        val juhyeon_profit = getPlayerData(binding, "juhyeon", startDate, currentDate)
        val jehyeon_profit = getPlayerData(binding, "jehyeon", startDate, currentDate)
        val seokyoung_profit = getPlayerData(binding, "seokyoung", startDate, currentDate)

        var minProfit = 0
        var minPlayer = ""

        if (juhyeon_profit > minProfit) {
            minProfit = juhyeon_profit
            minPlayer = "박 주 현"
        }

        if (jehyeon_profit > minProfit) {
            minProfit = jehyeon_profit
            minPlayer = "박 제 현"
        }

        if (seokyoung_profit > minProfit) {
            minProfit = seokyoung_profit
            minPlayer = "정 석 영"
        }

        binding.titleHogu.text =
            "\uD83E\uDD34\uD83C\uDFFB현재 호구 : ${minPlayer}\uD83E\uDD34\uD83C\uDFFB"

        binding.btnClear.setOnClickListener {
            clearData(binding, startDate, currentDate)
        }

        var juhyeon_entries = arrayListOf<Entry>()
        var jehyeon_entries = arrayListOf<Entry>()
        var seokyoung_entries = arrayListOf<Entry>()
        var dateList = arrayListOf<String>()
        var index = 0f

        for (date in startDate..(currentDate + TimeUnit.DAYS.toMillis(1)) step TimeUnit.DAYS.toMillis(
            1
        )) {
            val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)

            if (sharedPreferences.getBoolean("${formattedDate}", false)) {

                val ju_data = getRatings("juhyeon", startDate, date)
                val je_data = getRatings("jehyeon", startDate, date)
                val se_data = getRatings("seokyoung", startDate, date)

                juhyeon_entries.add(Entry(index, ju_data))
                jehyeon_entries.add(Entry(index, je_data))
                seokyoung_entries.add(Entry(index, se_data))

                dateList.add(formattedDate)
                index += 1
            }
        }

        val top5_jehyeon_entries = arrayListOf<Entry>()
        val top5_juhyeon_entries = arrayListOf<Entry>()
        val top5_seokyoung_entries = arrayListOf<Entry>()
        val top5_dateList = arrayListOf<String>()

        if(juhyeon_entries.size >= 5) {
            index = 0f
            for (i in jehyeon_entries.size - 5..jehyeon_entries.size - 1) {
                top5_jehyeon_entries.add(Entry(index, jehyeon_entries[i].y))
                top5_juhyeon_entries.add(Entry(index, juhyeon_entries[i].y))
                top5_seokyoung_entries.add(Entry(index, seokyoung_entries[i].y))
                Log.d("date : ", dateList[i])
                top5_dateList.add(dateList[i])
                index += 1
            }

            juhyeon_entries = top5_juhyeon_entries
            jehyeon_entries = top5_jehyeon_entries
            seokyoung_entries = top5_seokyoung_entries

            Log.d("juhyeon_entries :: ", juhyeon_entries.toString() )
            dateList = top5_dateList
        }

        // 데이터셋 초기화 및 설정
        val juhyeon_dataSet = LineDataSet(juhyeon_entries, "주현")
        juhyeon_dataSet.color = Color.RED
        juhyeon_dataSet.lineWidth = 4f
        juhyeon_dataSet.valueTextSize = 16f
        juhyeon_dataSet.valueFormatter = object : ValueFormatter() {
            private val format = DecimalFormat("#.##")
            override fun getFormattedValue(value: Float): String {
                return "${format.format(value)}%"
            }
        }
        val jehyeon_dataSet = LineDataSet(jehyeon_entries, "제현")
        jehyeon_dataSet.color = Color.BLUE
        jehyeon_dataSet.lineWidth = 4f
        jehyeon_dataSet.valueTextSize = 16f
        jehyeon_dataSet.valueFormatter = object : ValueFormatter() {
            private val format = DecimalFormat("#.##")
            override fun getFormattedValue(value: Float): String {
                return "${format.format(value)}%"
            }
        }
        val seokyoung_dataSet = LineDataSet(seokyoung_entries, "석영")
        seokyoung_dataSet.color = Color.GREEN
        seokyoung_dataSet.lineWidth = 4f
        seokyoung_dataSet.valueTextSize = 16f
        seokyoung_dataSet.valueFormatter = object : ValueFormatter() {
            private val format = DecimalFormat("#.##")
            override fun getFormattedValue(value: Float): String {
                return "${format.format(value)}%"
            }
        }

        // 라인 데이터 초기화 및 설정
        val lineData = LineData(juhyeon_dataSet, jehyeon_dataSet, seokyoung_dataSet)

        lineChart.data = lineData


        // 그래프 설정
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisLeft.axisMaximum = 110f
        lineChart.legend.isEnabled = true
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.granularity = 1f
        lineChart.setPinchZoom(true)
        lineChart.invalidate()
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                Log.d("value ::", value.toString())
                return if (value < 0f || value >= dateList.size || value % 1f != 0f) "" else dateList[value.toInt()]
            }
        }
        val padding = 30f
        lineChart.setExtraOffsets(padding, padding, padding, padding)

    }


    private fun clearData(binding: FragmentMoneyBinding, startDate: Long, currentDate: Long) {

        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        AlertDialog.Builder(requireContext()).setTitle("초기화")
            .setMessage("정말 초기화 하시겠습니까? 다시는 복구 할 수 없습니다.").setPositiveButton("초기화") { _, _ ->
                val editor = sharedPreferences.edit()
                editor.clear()

                binding.juhyeonTvWDL.text = "0승 0무 0패"
                binding.juhyeonTvSum.text = "총액 :0원"
                binding.jehyeonTvWDL.text = "0승 0무 0패"
                binding.jehyeonTvSum.text = "총액 :0원"
                binding.seokyoungTvWDL.text = "0승 0무 0패"
                binding.seokyoungTvSum.text = "총액 :0원"
                binding.seokyoungPercentage.text = "현재 승률 : 0%"
                binding.juhyeonPercentage.text = "현재 승률 : 0%"
                binding.jehyeonPercentage.text = "현재 승률 : 0%"
                binding.titleHogu.text =
                    "\uD83E\uDD34\uD83C\uDFFB현재 호구 : 아무개\uD83E\uDD34\uD83C\uDFFB"
                editor.apply()
                Toast.makeText(requireContext(), "초기화 되었습니다.", Toast.LENGTH_SHORT).show()

            }.setNegativeButton("취소", null).show()

    }

    private fun getPlayerData(
        binding: FragmentMoneyBinding,
        name: String,
        startDate: Long,
        currentDate: Long
    ): Int {

        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)




        when (name) {
            "juhyeon" -> {

                val totalWins = sharedPreferences.getInt("${name}_wins", 0)
                val totalDraws = sharedPreferences.getInt("${name}_draws", 0)
                val totalLosses = sharedPreferences.getInt("${name}_losses", 0)
                val totalRatings =
                    (totalWins).toFloat() / (totalWins + totalDraws + totalLosses) * 100
                val totalCosts = sharedPreferences.getInt("${name}_costs", 0)
                val formattedRatings = String.format("%.2f", totalRatings)

                binding.juhyeonTvWDL.text = "${totalWins}승 ${totalDraws}무 ${totalLosses}패"
                binding.juhyeonPercentage.text = "현재 승률 : ${formattedRatings}%"
                binding.juhyeonTvSum.text = "총액 : ${totalCosts}원"

                return totalCosts
            }
            "jehyeon" -> {
                val totalWins = sharedPreferences.getInt("${name}_wins", 0)
                val totalDraws = sharedPreferences.getInt("${name}_draws", 0)
                val totalLosses = sharedPreferences.getInt("${name}_losses", 0)
                val totalRatings =
                    (totalWins).toFloat() / (totalWins + totalDraws + totalLosses) * 100
                val totalCosts = sharedPreferences.getInt("${name}_costs", 0)
                val formattedRatings = String.format("%.2f", totalRatings)

                binding.jehyeonTvWDL.text = "${totalWins}승 ${totalDraws}무 ${totalLosses}패"
                binding.jehyeonPercentage.text = "현재 승률 : ${formattedRatings}%"
                binding.jehyeonTvSum.text = "총액 : ${totalCosts}원"

                return totalCosts
            }
            "seokyoung" -> {
                val totalWins = sharedPreferences.getInt("${name}_wins", 0)
                val totalDraws = sharedPreferences.getInt("${name}_draws", 0)
                val totalLosses = sharedPreferences.getInt("${name}_losses", 0)
                val totalRatings =
                    (totalWins).toFloat() / (totalWins + totalDraws + totalLosses) * 100
                val totalCosts = sharedPreferences.getInt("${name}_costs", 0)
                val formattedRatings = String.format("%.2f", totalRatings)

                binding.seokyoungTvWDL.text = "${totalWins}승 ${totalDraws}무 ${totalLosses}패"
                binding.seokyoungPercentage.text = "현재 승률 : ${formattedRatings}%"
                binding.seokyoungTvSum.text = "총액 : ${totalCosts}원"

                return totalCosts
            }
            else -> return 0
        }

    }

    private fun getRatings (name: String, startDate : Long, currentDate : Long) : Float{
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        when(name){
            "juhyeon" -> {
                var totalWins = 0
                var totalDraws = 0
                var totalLosses = 0
                for(date in startDate .. currentDate step TimeUnit.DAYS.toMillis(1)){
                    val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
                    totalWins += sharedPreferences.getInt("${formattedDate}_${name}_wins", 0)
                    totalDraws += sharedPreferences.getInt("${formattedDate}_${name}_draws", 0)
                    totalLosses += sharedPreferences.getInt("${formattedDate}_${name}_losses", 0)
                }
                val totalGame = totalWins + totalDraws + totalLosses

                return totalWins.toFloat() / totalGame * 100
            }
            "jehyeon" -> {
                var totalWins = 0
                var totalDraws = 0
                var totalLosses = 0
                for(date in startDate .. currentDate step TimeUnit.DAYS.toMillis(1)){
                    val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
                    totalWins += sharedPreferences.getInt("${formattedDate}_${name}_wins", 0)
                    totalDraws += sharedPreferences.getInt("${formattedDate}_${name}_draws", 0)
                    totalLosses += sharedPreferences.getInt("${formattedDate}_${name}_losses", 0)
                }
                val totalGame = totalWins + totalDraws + totalLosses

                return totalWins.toFloat() / totalGame * 100
            }
            "seokyoung" -> {
                var totalWins = 0
                var totalDraws = 0
                var totalLosses = 0
                for(date in startDate .. currentDate step TimeUnit.DAYS.toMillis(1)){
                    val formattedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
                    totalWins += sharedPreferences.getInt("${formattedDate}_${name}_wins", 0)
                    totalDraws += sharedPreferences.getInt("${formattedDate}_${name}_draws", 0)
                    totalLosses += sharedPreferences.getInt("${formattedDate}_${name}_losses", 0)
                }
                val totalGame = totalWins + totalDraws + totalLosses

                return totalWins.toFloat() / totalGame * 100
            }
        }

        return 0f
    }

}