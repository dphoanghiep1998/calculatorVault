package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.databinding.ActivityCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ActivityCamera : AppCompatActivity() {
    private lateinit var binding:ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        cameraLauncher.launch(intent)
        lifecycleScope.launch(Dispatchers.IO) {
            val a = FileUtils.getFileInDirectory(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path.toString())
            Log.d("TAG", "a" + a.size)
        }
    }



    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        lifecycleScope.launch(Dispatchers.IO) {
            val a = FileUtils.getFileInDirectory(getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path.toString())
            Log.d("TAG", "a" + a.size)
        }
    }

}