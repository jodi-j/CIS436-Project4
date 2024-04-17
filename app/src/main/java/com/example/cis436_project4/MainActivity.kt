package com.example.cis436_project4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cis436_project4.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, SwipingFragment())
                        .commit()
                    true
                }
                R.id.item_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ProductsFragment())
                        .commit()
                    true
                }
                R.id.item_3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

}