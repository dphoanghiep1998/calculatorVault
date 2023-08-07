package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.biometric.BiometricConfig.Companion.biometricConfig
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.config.FingerPrintLockDisplay
import com.neko.hiepdph.calculatorvault.config.FingerPrintUnlock
import com.neko.hiepdph.calculatorvault.databinding.ActivityPatternLockBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.dialog.DialogPassword
import com.neko.hiepdph.calculatorvault.dialog.SetupPassWordCallBack
import com.neko.hiepdph.calculatorvault.viewmodel.PinLockViewModel
import com.takwolf.android.lock9.Lock9View
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ActivityPatternLock : AppCompatActivity() {
    private lateinit var binding: ActivityPatternLockBinding
    private var byteArray: ByteArray? = null
    private var takePhotoIntruder = false
    private var camera: Camera? = null
    private val viewModel by viewModels<PinLockViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatternLockBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                    (application as CustomApplication).authority = true
                    (application as CustomApplication).isLockShowed = true
                    finish()
                }
                authenticateFailed = {
                    (application as CustomApplication).authority = false
                    (application as CustomApplication).isLockShowed = true
                    if (config.photoIntruder && !takePhotoIntruder) {
                        takePicture(action = {
                            finish()
                        })

                    } else if (config.fingerprintFailure) {
                        finishAffinity()
                    }
                }
            }
            biometric.showPrompt()
        }

        binding.tvPassword.clickWithDebounce {
            binding.containerFingerPrint.hide()
        }

        binding.tvForgotPassword.clickWithDebounce {
            val confirmDialog = DialogConfirm(onPositiveClicked = {
                showDialogConfirmSecurityQuestion()
            }, DialogConfirmType.FORGOT_PASSWORD, null)
            confirmDialog.show(supportFragmentManager, confirmDialog.tag)
        }

        if (config.fingerPrintUnlock == FingerPrintUnlock.ENABLE) {
            showBiometric()
        }

        if (config.fingerPrintLockDisplay) {
            binding.containerFingerPrint.show()
        } else {
            binding.containerFingerPrint.hide()
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

    private fun showBiometric() {
        val biometric = biometricConfig {
            ownerFragmentActivity = this@ActivityPatternLock
            authenticateSuccess = {
                (application as CustomApplication).authority = true
                startActivity(
                    Intent(this@ActivityPatternLock, ActivityVault::class.java)
                )
                finish()
            }
            authenticateFailed = {
                (application as CustomApplication).authority = false

                if (config.photoIntruder && !takePhotoIntruder) {
                    takePicture()
                }
                if (config.fakePassword) {
                    startActivity(
                        Intent(this@ActivityPatternLock, ActivityVault::class.java)
                    )
                    finish()
                }
            }
        }
        biometric.showPrompt()
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
            (application as CustomApplication).authority = false
            (application as CustomApplication).isLockShowed = true
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

            if (config.fakePassword) {
                if (config.photoIntruder && !takePhotoIntruder) {
                    takePicture(action = {
                        finish()
                    })
                } else {
                    if (config.photoIntruder && !takePhotoIntruder) {
                        takePicture(action = {})
                    }
                }
            }
        } else {

            finish()
        }
    }

    private fun takePicture(action: (() -> Unit)? = null) {
        startCamera()
        takePhotoIntruder(action)
        takePhotoIntruder = true
    }


    private fun takePhotoIntruder(action: (() -> Unit)? = null) {
        try {
            camera?.takePicture(
                null, null
            ) { data, camera ->
                Log.d("TAG", "takePhotoIntruder: " + data)
                byteArray = data
                action?.invoke()

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCamera() {
        val dummy = SurfaceTexture(0)

        try {
            val cameraId = getFrontCameraId()
            if (cameraId == -1) {
                return
            }
            camera = Camera.open(cameraId).also {
                it.setPreviewTexture(dummy)
                it.startPreview()
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    private fun stopCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    private fun getFrontCameraId(): Int {
        var camId = -1
        val numberOfCameras = Camera.getNumberOfCameras()
        val ci = Camera.CameraInfo()

        for (i in 0 until numberOfCameras) {
            Camera.getCameraInfo(i, ci)
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camId = i
            }
        }

        return camId
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCamera()
        if (!(application as CustomApplication).authority) {
            config.caughtIntruder = true
            if (!config.intruderFolder.exists()) {
                config.intruderFolder.mkdirs()
            }
            val file = File(
                config.intruderFolder,
                "lmao_intruder_${config.intruderFolder.listFiles().size}.jpeg"
            )
            val fos = FileOutputStream(file)
            fos.write(byteArray)
            fos.close()
        }
    }

    override fun onBackPressed() {

    }

}