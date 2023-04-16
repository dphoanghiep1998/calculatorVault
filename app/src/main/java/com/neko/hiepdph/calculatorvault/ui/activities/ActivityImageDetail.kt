package com.neko.hiepdph.calculatorvault.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.databinding.ActivityImageDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityImageDetail : AppCompatActivity() {
    private lateinit var binding :ActivityImageDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initView(){
        initButton()
    }

    private fun initButton() {
        binding.tvUnlock.clickWithDebounce {

        }

        binding.tvDelete.clickWithDebounce {

        }

        binding.tvShare.clickWithDebounce {

        }

        binding.tvSlideshow.clickWithDebounce {

        }
    }
}