package com.neko.hiepdph.calculatorvault.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.databinding.ActivityCaculatorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityCalculator : AppCompatActivity() {
    private lateinit var binding: ActivityCaculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setThemeMode()
    }


    private fun setThemeMode() {
        if (config.darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}