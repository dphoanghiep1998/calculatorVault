package com.neko.hiepdph.calculatorvault.ui.activities

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.biometric.BiometricConfig
import com.neko.hiepdph.calculatorvault.common.customview.PinFunction
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityPinLockBinding
import com.neko.hiepdph.calculatorvault.dialog.*
import com.neko.hiepdph.calculatorvault.viewmodel.ListItemViewModel
import com.neko.hiepdph.calculatorvault.viewmodel.PinLockViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ActivityPinLock : AppCompatActivity() {
    private lateinit var binding: ActivityPinLockBinding
    private var currentPassword = ""
    private var byteArray: ByteArray? = null
    private var takePhotoIntruder = false
    private var camera: Camera? = null
    private val viewModel by viewModels<PinLockViewModel>()


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
                    (application as CustomApplication).authority = true
                    (application as CustomApplication).isLockShowed = true
                    finish()
                }
                authenticateFailed = {
                    if (config.fakePassword) {
                        (application as CustomApplication).authority = false
                        (application as CustomApplication).isLockShowed = true
                        if (config.photoIntruder && !takePhotoIntruder) {
                            takePicture(action = {
                                finish()
                            })
                        }else {
                            takePicture(action = {
                            })
                        }
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
            if (config.photoIntruder && !takePhotoIntruder) {
                takePicture()
            }
            return
        }

        if (config.secretPin != currentPassword && config.fakePassword) {
            (application as CustomApplication).authority = false
            (application as CustomApplication).isLockShowed = true
            if (config.photoIntruder && !takePhotoIntruder) {
                takePicture(action = {
                    finish()
                })
            }
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
            if (config.photoIntruder && !takePhotoIntruder) {
                takePicture()
            }
            return
        }

        if (config.secretPin == currentPassword) {
            (application as CustomApplication).authority = true
            (application as CustomApplication).isLockShowed = true
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

    private fun takePicture(action: (() -> Unit)? = null) {
        startCamera()
        takePhotoIntruder(action)
        takePhotoIntruder = true
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
        if (!(application as CustomApplication).authority) {
            config.caughtIntruder = true
            CoroutineScope(Dispatchers.IO).launch {
                if (!config.intruderFolder.exists()) {
                    config.intruderFolder.mkdirs()
                }
                val name = "lmao_intruder_${config.intruderFolder.listFiles().size}.jpeg"
                val file = File(
                    config.intruderFolder, name
                )
                val fos = FileOutputStream(file)
                fos.write(byteArray)
                fos.close()
                viewModel.insertFileToRoom(
                    FileVaultItem(
                        0, "${config.intruderFolder.path}/" + name,"${config.intruderFolder.path}/" + name, name = name
                    )
                )
            }

        }
        stopCamera()

    }
}