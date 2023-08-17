package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.show
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
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.system.exitProcess

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
        lifecycleScope.launch(Dispatchers.IO) {
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
                binding.containerLoad.show()
                lifecycleScope.launch(Dispatchers.IO) {
                    val newImage = MediaStoreUtils.getAllImage(this@ActivityCamera)
                    Log.d("TAG", "TAGGGGG" + newImage.size)
                    val listItemDiff = mutableListOf<FileVaultItem>()

                    for (i in newImage.indices) {
                        if (listImagePrevious.isNotEmpty()) {
                            if (newImage[i] != listImagePrevious[0]) {
                                val newItem = newImage[i].copy()
                                newItem.thumb =
                                    imagePathToBitmap(newImage[i].originalPath)?.let { it1 ->
                                        scaleBitmap(
                                            it1, 512 * 512
                                        )
                                    }
                                listItemDiff.add(newItem)
                            } else {
                                break
                            }
                        } else {
                            val newItem = newImage[i].copy()
                            newItem.thumb =
                                imagePathToBitmap(newImage[i].originalPath)?.let { it1 ->
                                    scaleBitmap(
                                        it1, 512 * 512
                                    )
                                }
                            listItemDiff.add(newItem)
                        }
                    }
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
                                            decodePath = "${config.decryptFolder}/${
                                                item.name
                                            }"
                                        }

                                        viewModel.insertVaultItem(item, action = {
                                            lifecycleScope.launch(Dispatchers.Main) {
                                                finishAffinity()
                                                exitProcess(-1)
                                            }
                                        })


                                    }
                                }

                            },
                            onError = {
                                finishAffinity()
                                exitProcess(-1)
                            },
                            EncryptionMode.HIDDEN
                        )
                    } else {
                        finish()
                    }

                }


            }

        }

    private fun imagePathToBitmap(filePath: String): Bitmap? {
        val image = File(filePath)
        val bmOptions = BitmapFactory.Options()
        return try {
            BitmapFactory.decodeFile(image.path, bmOptions)
        } catch (e: Exception) {
            null
        }
    }

    private fun scaleBitmap(
        input: Bitmap, maxBytes: Long
    ): Bitmap? {
        val currentWidth = input.width
        val currentHeight = input.height
        val currentPixels = currentWidth * currentHeight
        // Get the amount of max pixels:
        // 1 pixel = 4 bytes (R, G, B, A)
        val maxPixels = maxBytes / 4 // Floored
        if (currentPixels <= maxPixels) {
            // Already correct size:
            return input
        }
        // Scaling factor when maintaining aspect ratio is the square root since x and y have a relation:
        val scaleFactor = sqrt(maxPixels / currentPixels.toDouble())
        val newWidthPx = floor(currentWidth * scaleFactor).toInt()
        val newHeightPx = floor(currentHeight * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(input, newWidthPx, newHeightPx, true)
    }


}