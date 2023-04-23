package com.example.bowlingapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bowlingapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment())
            .commit()

        binding.bottomNav.setOnItemSelectedListener {

                item ->
            when (item.itemId) {

                R.id.tab_main -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, HomeFragment()).commit()
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