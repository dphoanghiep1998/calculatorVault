package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.neko.hiepdph.calculatorvault.common.Constant
import java.io.File


object FileUtils {
    fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.filesDir, fileName)
        csvFile.createNewFile()
        return if (csvFile.exists()) {
            csvFile
        } else {
            null
        }
    }


    fun shareFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val mimType = context.contentResolver.getType(contentUri)
        intent.setDataAndType(contentUri, mimType)
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return intent
    }


    fun getFoldersInDirectory(dir: String): List<File> {
        val listOfFolder = mutableListOf<File>()
        return try {
            val directory = File(dir)

            val files = directory.listFiles { file -> file.isDirectory }
            files?.let {
                for (file in files) {
                    listOfFolder.add(file)
                }
            }
            listOfFolder
        } catch (e: Exception) {
            e.printStackTrace()
            listOfFolder
        }

    }


    private fun checkImageExtension(path: String): Boolean {
        Constant.photoExtensions.forEach {
            if (path.endsWith(it)) {
                return true
            }
        }
        return false
    }

    fun deleteFolderInDirectory(
        pathFolder: String, onSuccess: () -> Unit, onError: (message: String) -> Unit
    ) {
        try {
            val folder = File(pathFolder)
            val delete = folder.deleteRecursively()
            if (delete) {
                onSuccess()
            } else {
                onError("Can not delete file/folder")
            }
        } catch (e: Exception) {
            onError(e.message.toString())

        }
    }

    fun deleteAllChildInDirectory(
        path: String,
        onResult: (listOfFilePathDeletedSuccess: MutableList<String>, listOfFilePathDeletedFailed: MutableList<String>) -> Unit,
        onProgress: (value: Float) -> Unit
    ) {
        val listOfFilePathDeletedSuccess = mutableListOf<String>()
        val listOfFilePathDeletedFailed = mutableListOf<String>()
        try {
            val folder = File(path)
            val totalSize = folder.listFiles()?.size ?: 0
            var count = 0

            folder.listFiles()?.let {
                for (file in it) {
                    val delete = file.deleteRecursively()
                    Log.d("TAG", "deleteAllChildInDirectory: "+delete)
                    if (delete) {
                        listOfFilePathDeletedSuccess.add(file.path)
                    } else {
                        listOfFilePathDeletedFailed.add(file.path)
                    }
                    count++
                    if (totalSize != 0) {
                        onProgress(count / totalSize.toFloat())
                    }
                }
            }
            onResult(listOfFilePathDeletedSuccess, listOfFilePathDeletedFailed)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(listOfFilePathDeletedSuccess, listOfFilePathDeletedFailed)
        }
    }

    fun deleteMultipleFolderInDirectory(
        pathFolder: List<String>,
        onResult: (listOfFilePathDeletedSuccess: MutableList<String>, listOfFilePathDeletedFailed: MutableList<String>) -> Unit,
        onProgress: (value: Float) -> Unit,
    ) {
        val listOfFilePathDeletedSuccess = mutableListOf<String>()
        val listOfFilePathDeletedFailed = mutableListOf<String>()
        try {
            var count = 0
            val sizeFolder = pathFolder.size
            pathFolder.forEach {
                count++
                val folder = File(it)
                val delete = folder.deleteRecursively()
                if (delete) {
                    listOfFilePathDeletedSuccess.add(it)
                    onProgress((count / sizeFolder * 100).toFloat())
                } else {
                    listOfFilePathDeletedFailed.add(it)
                }
            }
            onResult(listOfFilePathDeletedSuccess, listOfFilePathDeletedFailed)
        } catch (e: Exception) {
            onResult(listOfFilePathDeletedSuccess, listOfFilePathDeletedFailed)
        }
    }

    private fun getMimeType(url: String): String {
        var type = ""
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
        }
        return type
    }

    fun renameFile(
        oldFile: File, name: String, onSuccess: () -> Unit, onError: (message: String) -> Unit
    ) {
        try {
            val newFile = File(oldFile.parentFile?.path + "/$name")
            if (oldFile.isDirectory) {
                org.apache.commons.io.FileUtils.moveDirectory(oldFile, newFile)
            } else {
                org.apache.commons.io.FileUtils.moveFile(oldFile, newFile)
            }
            onSuccess()
        } catch (e: Exception) {
            onError(e.message.toString())
        }
    }


}