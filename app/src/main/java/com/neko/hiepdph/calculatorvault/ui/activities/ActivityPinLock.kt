package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.biometric.BiometricConfig
import com.neko.hiepdph.calculatorvault.common.customview.PinFunction
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.databinding.ActivityPinLockBinding
import com.neko.hiepdph.calculatorvault.dialog.*

class ActivityPinLock : AppCompatActivity() {
    private lateinit var binding: ActivityPinLockBinding
    private var currentPassword = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinLockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun appendToPassword(key: String) {
        binding.tvStatus.text = String.EMPTY
        if (currentPassword.length < 4) {
            currentPassword += key
            checkView(currentPassword)
        }
    }

    private fun initView() {
        binding.pinView.setCalculatorMainCallback(object : PinFunction {
            override fun onPressButton0() {
                appendToPassword("0")
            }

            override fun onPressButton1() {
                appendToPassword("1")
            }

            override fun onPressButton2() {
                appendToPassword("2")
            }

            override fun onPressButton3() {
                appendToPassword("3")
            }

            override fun onPressButton4() {
                appendToPassword("4")
            }

            override fun onPressButton5() {
                appendToPassword("5")
            }

            override fun onPressButton6() {
                appendToPassword("6")
            }

            override fun onPressButton7() {
                appendToPassword("7")
            }

            override fun onPressButton8() {
                appendToPassword("8")
            }

            override fun onPressButton9() {
                appendToPassword("9")
            }

            override fun onPressButtonCheck() {
                checkPass()
            }

            override fun onPressButtonBackspace() {
                handlePressBackspace()
            }
        })

        binding.fingerPrint.clickWithDebounce {
            val biometric = BiometricConfig.biometricConfig {
                ownerFragmentActivity = this@ActivityPinLock
                authenticateSuccess = {
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
            val confirmDialog = DialogConfirm(callBack = object : ConfirmDialogCallBack {
                override fun onPositiveClicked() {
                    showDialogConfirmSecurityQuestion()
                }
            }, DialogConfirmType.FORGOT_PASSWORD, null)
            confirmDialog.show(supportFragmentManager, confirmDialog.tag)
        }
    }

    private fun showDialogConfirmSecurityQuestion() {
        val dialogPassword = DialogPassword(callBack = object : SetupPassWordCallBack {
            override fun onPositiveClicked() {
                showDialogPassword()
            }
        })
        dialogPassword.show(supportFragmentManager, dialogPassword.tag)
    }

    private fun showDialogPassword() {
        val dialogShowPassword = DialogShowPassword()
        dialogShowPassword.show(supportFragmentManager, dialogShowPassword.tag)
    }

    private fun checkPass() {
        if (currentPassword.length < 4) {
            binding.tvStatus.text = getString(R.string.password_must_be_4_characters)
            binding.tvStatus.setTextColor(getColor(R.color.theme_12))
            val anim = TranslateAnimation(0f, -100f, 0f, 0f) // move view to left
            anim.duration = 100 // set animation duration to 500 milliseconds
            anim.repeatMode = Animation.REVERSE // repeat animation in reverse mode
            anim.repeatCount = 2 // repeat animation 2 times
            binding.tvStatus.startAnimation(anim)
            return
        }
        if (currentPassword != config.secretPin) {
            binding.tvStatus.text = getString(R.string.current_password_is_not_correct)
            binding.tvStatus.setTextColor(getColor(R.color.theme_12))
            val anim = TranslateAnimation(0f, -100f, 0f, 0f) // move view to left
            anim.duration = 100 // set animation duration to 500 milliseconds
            anim.repeatMode = Animation.REVERSE // repeat animation in reverse mode
            anim.repeatCount = 2 // repeat animation 2 times
            binding.tvStatus.startAnimation(anim)
            return
        }
        if (config.secretPin == currentPassword) {
            config.isShowLock = true
            startActivity(
                Intent(this@ActivityPinLock, ActivityVault::class.java)
            )
            finish()
        }
    }

    private fun handlePressBackspace() {
        if (currentPassword.isNotEmpty()) {
            currentPassword = currentPassword.subSequence(0, currentPassword.length - 1).toString()
            checkView(currentPassword)
        }
    }

    private fun checkView(password: String) {
        val groupPin = listOf(binding.char1, binding.char2, binding.char3, binding.char4)
        groupPin.forEachIndexed { index, item ->
            if (index < password.length) {
                item.setBackgroundResource(R.drawable.bg_pin_active)
            } else {
                item.setBackgroundResource(R.drawable.bg_pin_inactive)
            }
        }
    }
}