package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.os.Environment
import android.util.Log

object StorageHelper {
     fun checkDevice(context:Context){
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            val externalDirs =context.getExternalFilesDirs(null)
            for (file in externalDirs) {
                if (file != null) {
                    val totalSize = file.totalSpace
                    val availableSize = file.usableSpace
                    val path = file.absolutePath
                    Log.d("Storage", "Path: $path")
                    Log.d("Storage", "Total size: $totalSize")
                    Log.d("Storage", "Available size: $availableSize")
                }
            }
        }
    }

}