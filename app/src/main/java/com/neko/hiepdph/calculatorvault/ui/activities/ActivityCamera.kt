package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityCameraBinding
import com.neko.hiepdph.calculatorvault.encryption.CryptoCore
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@AndroidEntryPoint
class ActivityCamera : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var listImagePrevious = mutableListOf<FileVaultItem>()
    private val viewModel by viewModels<AppViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataImage()
        initView()
    }

    private fun getDataImage() {
        listImagePrevious = MediaStoreUtils.getAllImage(this@ActivityCamera).toMutableList()
    }

    private fun initView() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        cameraLauncher.launch(intent)
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_CANCELED) {
                lifecycleScope.launch {
                    val newImage = MediaStoreUtils.getAllImage(this@ActivityCamera)
                    val listItemDiff = mutableListOf<FileVaultItem>()

                    for (i in newImage.indices) {
                        if (listImagePrevious.isNotEmpty()) {
                            if (newImage[i] != listImagePrevious[0]) {
                                listItemDiff.add(newImage[i])
                            } else {
                                break
                            }
                        } else {
                            listItemDiff.add(newImage[i])
                        }
                    }

                    if (listItemDiff.isNotEmpty()) {
                        val newList = listItemDiff.toMutableList()
                        newList.forEach { item ->
                            item.apply {
                                timeLock = Calendar.getInstance().timeInMillis
                                encryptionType = 1
                                encryptedPath = "${config.picturePrivacyFolder.path}/${
                                    CryptoCore.getSingleInstance()
                                        .encryptString(Constant.SECRET_KEY, name)
                                }"
                            }
                            viewModel.insertVaultItem(item)
                        }
                        val listEncryptedString = mutableListOf<String>()
                        listEncryptedString.addAll(listItemDiff.map { item ->
                            CryptoCore.getSingleInstance(

                            ).encryptString(Constant.SECRET_KEY, item.name)
                        })
                        CopyFiles.encrypt(
                            this@ActivityCamera,
                            listItemDiff.map { item -> File(item.originalPath) },
                            listItemDiff.map { config.picturePrivacyFolder },
                            listEncryptedString,
                            0L,
                            progress = {  value: Float, currentFile: File? ->

                            },
                            onError = {
                                finish()
                            },
                            onSuccess = {
                                finish()
                            },
                            encryptionMode = 1
                        )

                    } else {
                        finish()
                    }

                }


            }

        }


}