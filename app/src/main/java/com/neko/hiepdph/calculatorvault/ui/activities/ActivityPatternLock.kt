package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.biometric.BiometricConfig.Companion.biometricConfig
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.config.FingerPrintLockDisplay
import com.neko.hiepdph.calculatorvault.config.FingerPrintUnlock
import com.neko.hiepdph.calculatorvault.databinding.ActivityPatternLockBinding
import com.neko.hiepdph.calculatorvault.dialog.*
import com.takwolf.android.lock9.Lock9View
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityPatternLock : AppCompatActivity() {
    private lateinit var binding: ActivityPatternLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatternLockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("TAG", "onCreate: "+config.isShowLock)
        initView()
    }

    private fun initView() {
        when (config.fingerPrintLockDisplay) {
            FingerPrintLockDisplay.DISABLE -> binding.containerFingerPrint.hide()
            else -> binding.containerFingerPrint.show()
        }
        binding.tvStatus.apply {
            text = getString(R.string.draw_current_key)
            setTextColor(getColor(R.color.neutral_06))
        }
        binding.fingerPrint.clickWithDebounce {
            val biometric = biometricConfig {
                ownerFragmentActivity = this@ActivityPatternLock
                authenticateSuccess = {
                    config.isShowLock = true
                    Log.d("TAG", "authenticateSuccess: ")
                }
                authenticateFailed = {
                    Log.d("TAG", "authenticateFailed: ")
                }
            }
            biometric.showPrompt()
        }

        binding.tvPassword.clickWithDebounce {
            binding.containerFingerPrint.hide()
        }

        binding.tvForgotPassword.clickWithDebounce {
            val confirmDialog = DialogConfirm(onPositiveClicked= {
                    showDialogConfirmSecurityQuestion()
            }, DialogConfirmType.FORGOT_PASSWORD, null)
            confirmDialog.show(supportFragmentManager, confirmDialog.tag)
        }

        if (config.fingerPrintUnlock == FingerPrintUnlock.ENABLE) {
            val biometric = biometricConfig {
                ownerFragmentActivity = this@ActivityPatternLock
                authenticateSuccess = {
                    config.isShowLock = true
                    Log.d("TAG", "authenticateSuccess: ")
                }
                authenticateFailed = {
                    Log.d("TAG", "authenticateFailed: ")
                }
            }
            biometric.showPrompt()
        }

        binding.lock9View.apply {
            setEnableVibrate(config.tactileFeedback)
            setHighlighted(config.visiblePattern)
            setGestureCallback(object : Lock9View.GestureCallback {
                override fun onNodeConnected(numbers: IntArray) {
                    binding.tvStatus.apply {
                        text = getString(R.string.release_your_finger_when_done)
                        setTextColor(getColor(R.color.neutral_06))
                    }
                }

                override fun onGestureFinished(numbers: IntArray) {
                    checkPattern(numbers)
                }
            })
        }
    }

    private fun showDialogConfirmSecurityQuestion() {
        val dialogPassword = DialogPassword(callBack = object : SetupPassWordCallBack {
            override fun onPositiveClicked() {
                Log.d("TAG", "onPositiveClicked: " + config.patternLock)
                binding.lock9View.setDrawGivenPattern(config.patternLock)
            }

        })

        dialogPassword.show(supportFragmentManager, dialogPassword.tag)
    }

    private fun checkPattern(numbers: IntArray) {
        if (!(numbers contentEquals config.patternLock.toIntArray())) {
            val anim = TranslateAnimation(0f, -50f, 0f, 0f) // move view to left
            anim.duration = 100 // set animation duration to 500 milliseconds
            anim.repeatMode = Animation.REVERSE // repeat animation in reverse mode
            anim.repeatCount = 2 // repeat animation 2 times
            anim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    lifecycleScope.launch {
                        delay(400)
                        binding.tvStatus.apply {
                            text = getString(R.string.draw_current_key)
                            setTextColor(getColor(R.color.neutral_06))
                        }
                    }

                }

                override fun onAnimationRepeat(p0: Animation?) {
                }


            })
            binding.tvStatus.apply {
                text = getString(R.string.pattern_not_match)
                setTextColor(getColor(R.color.theme_12))
                startAnimation(anim)
            }
        } else {
            config.isShowLock = true
            startActivity(
                Intent(this@ActivityPatternLock, ActivityVault::class.java)
            )
            finish()
        }
    }

}