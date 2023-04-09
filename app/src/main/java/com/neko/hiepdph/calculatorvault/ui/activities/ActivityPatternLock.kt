package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.databinding.ActivityPatternLockBinding
import com.takwolf.android.lock9.Lock9View

class ActivityPatternLock : AppCompatActivity() {
    private lateinit var binding: ActivityPatternLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatternLockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.lock9View.setGestureCallback(object : Lock9View.GestureCallback {
            override fun onNodeConnected(numbers: IntArray) {
                binding.tvStatus.text = String.EMPTY
            }

            override fun onGestureFinished(numbers: IntArray) {
                checkPattern(numbers)
            }
        })
    }

    private fun checkPattern(numbers: IntArray) {
        if(!(numbers contentEquals config.patternLock.toIntArray())){
            binding.tvStatus.text = getString(R.string.pattern_not_match)
            binding.tvStatus.setTextColor(getColor(R.color.theme_12))
            val anim = TranslateAnimation(0f, -100f, 0f, 0f) // move view to left
            anim.duration = 100 // set animation duration to 500 milliseconds
            anim.repeatMode = Animation.REVERSE // repeat animation in reverse mode
            anim.repeatCount = 2 // repeat animation 2 times
            binding.tvStatus.startAnimation(anim)
        }else{
            val intent = Intent(this@ActivityPatternLock,ActivityVault::class.java)
            startActivity(intent)
            finish()
        }
    }
}