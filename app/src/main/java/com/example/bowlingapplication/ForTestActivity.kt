package com.example.bowlingapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bowlingapplication.Fragment.GraphFragment
import com.example.bowlingapplication.Fragment.HomeFragment
import com.example.bowlingapplication.Fragment.MoneyFragment
import com.example.bowlingapplication.TestFragment.TestHomeFragment
import com.example.bowlingapplication.databinding.ActivityForTestBinding

class ForTestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, TestHomeFragment())
            .commit()

        binding.bottomNav.setOnItemSelectedListener {

                item ->
            when (item.itemId) {

                R.id.tab_main -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, TestHomeFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.tab_graph -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, GraphFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.tab_money -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, MoneyFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                else -> {
                    false
                }

            }

        }
    }
}