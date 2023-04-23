package com.neko.hiepdph.calculatorvault.common.utils

import java.io.File
const val FILE_EXISTED_ERROR = "FILE_EXISTED_ERROR"

object CreateFile {
    fun createFileDirectory(
        parentDir: File,
        name: String,
        onSuccess: () -> Unit,
        onError: (message:String) -> Unit
    ) {
        try {
            if (!parentDir.exists()) {
                parentDir.mkdir()
            }
            val folder = File(parentDir, name)
            if (!folder.exists()) {
                folder.mkdir()
                onSuccess()
            } else {
                onError(FILE_EXISTED_ERROR)
            }
        } catch (e: Exception) {
            onError(e.message.toString())
        }

    }

    fun createFileDirectory(
        parentDir: File,
        name: List<String>,
        onSuccess: () -> Unit,
        onError: (message:String) -> Unit
    ) {
        try {
            var count = 0
            if (!parentDir.exists()) {
                parentDir.mkdir()
            }
            name.forEach {
                val folder = File(parentDir, it)
                if (!folder.exists()) {
                    folder.mkdir()
                    count++
                } else {
                    onError(FILE_EXISTED_ERROR)
                }
                if(count == name.size-1){
                    onSuccess()
                }
            }

        } catch (e: Exception) {
            onError(e.message.toString())
        }

    }
}