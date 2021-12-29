package ru.aasmc.taskie.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import ru.aasmc.taskie.R
import ru.aasmc.taskie.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val pagerAdapter by lazy {
        MainPagerAdapter(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        binding.fragmentPager.adapter = pagerAdapter
        val tabLayout = binding.tabs
        TabLayoutMediator(tabLayout, binding.fragmentPager) { tab, position ->
            tab.text = if (position == 0) "Notes" else "Profile"
        }.attach()
    }

    override fun onBackPressed() {
        if (binding.fragmentPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.fragmentPager.currentItem = binding.fragmentPager.currentItem - 1
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }
    }
}