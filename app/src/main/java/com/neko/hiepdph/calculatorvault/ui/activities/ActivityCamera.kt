package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityCameraBinding
import com.neko.hiepdph.calculatorvault.encryption.CryptoCore
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.Calendar

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
        lifecycleScope.launch(Dispatchers.IO){
            listImagePrevious = MediaStoreUtils.getAllImage(this@ActivityCamera).toMutableList()
        }
    }

    private fun initView() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        cameraLauncher.launch(intent)
    }


    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_CANCELED) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val newImage = MediaStoreUtils.getAllImage(this@ActivityCamera)
                    Log.d("TAG", "TAGGGGG" + newImage.size)
                    val listItemDiff = mutableListOf<FileVaultItem>()

                    for (i in newImage.indices) {
                        if (listImagePrevious.isNotEmpty()) {
                            if (newImage[i] != listImagePrevious[0]) {
                                Log.d("TAG", "difff" + newImage[i].originalPath)

                                val newItem = newImage[i].copy()
                                if (imageToByteArray(newImage[i].originalPath) != null) {
                                    newItem.thumb = Base64.encodeToString(
                                        imageToByteArray(newItem.originalPath), Base64.DEFAULT
                                    )
                                }
                                listItemDiff.add(newItem)
                            } else {
                                break
                            }
                        } else {
                            val newItem = newImage[i].copy()
                            if (imageToByteArray(newImage[i].originalPath) != null) {
                                newItem.thumb = Base64.encodeToString(
                                    imageToByteArray(newItem.originalPath), Base64.DEFAULT
                                )
                            }
                            listItemDiff.add(newItem)
                        }
                    }
                    Log.d("TAG", "adasdasd " + listItemDiff.size)
                    val listNameEncrypt = listItemDiff.map { nitem ->
                        CryptoCore.getSingleInstance()
                            .encryptString(Constant.SECRET_KEY, nitem.name)
                    }.toMutableList()
                    if (listItemDiff.isNotEmpty()) {
                        viewModel.encrypt(
                            this@ActivityCamera,
                            listItemDiff.map { mItem -> File(mItem.originalPath) },
                            listItemDiff.map { config.picturePrivacyFolder },
                            listNameEncrypt,
                            progress = { _: File? ->

                            },
                            onSuccess = {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    listItemDiff.forEachIndexed { index, item ->
                                        item.apply {
                                            recyclerPath =
                                                "${config.recyclerBinFolder.path}/${listNameEncrypt[index]}"
                                            timeLock = Calendar.getInstance().timeInMillis
                                            encryptionType = EncryptionMode.HIDDEN
                                            encryptedPath = "${config.picturePrivacyFolder}/${
                                                listNameEncrypt[index]
                                            }"
                                        }

                                        viewModel.insertVaultItem(item)
                                    }
                                }

                            },
                            onError = {

                            },
                            EncryptionMode.HIDDEN
                        )
                    } else {
                        finish()
                    }

                }


            }

        }

    private fun imageToByteArray(filePath: String): ByteArray {
        val file = File(filePath)
        val byteStream = FileInputStream(file)
        val byteBuffer = ByteArray(file.length().toInt())

        byteStream.read(byteBuffer)
        byteStream.close()

        return byteBuffer
    }


}