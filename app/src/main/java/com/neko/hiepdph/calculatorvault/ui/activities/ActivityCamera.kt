package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ActivityCamera : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var listImagePrevious = mutableListOf<ListItem>()
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
                    val listItemDiff = mutableListOf<ListItem>()

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
                            item.path = config.picturePrivacyFolder.path + "/${item.mName}"
                        }
                        val listItemVault = config.listItemVault?.toMutableList() ?: mutableListOf()
                        listItemVault.addAll(newList)
                        config.listItemVault = listItemVault

                        CopyFiles.copy(
                            this@ActivityCamera,
                            listItemDiff.map { item -> File(item.path) }.toMutableList(),
                            config.picturePrivacyFolder,
                            0L,
                            progress = { state: Int, value: Float, currentFile: File? ->

                            },
                            false,
                            onError = {
                                finish()
                            },
                            onSuccess = {
                                finish()
                            }
                        )

                    } else {
                        finish()
                    }

                }


            }

        }


}