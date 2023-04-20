package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import java.io.File
import java.util.*

interface IDeleteFile {
    fun onSuccess()
    fun onFailed()

}

interface IRenameFile {
    fun onSuccess()
    fun onFailed()

}

interface ICreateFile {
    fun onSuccess()
    fun onFailed()
}

interface IMoveFile {
    fun onSuccess()
    fun onFailed()

    fun onDoneWithWarning()
}

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

    fun createSecretFile(parentDir: File, name: String, callback: ICreateFile) {
        try {
            if(!parentDir.exists()){
                parentDir.mkdir()
            }
            val folder = File(parentDir, name)
            if (!folder.exists()) {
                folder.mkdir()
                callback.onSuccess()
            } else {
                callback.onFailed()
            }
        } catch (e: Exception) {
            callback.onFailed()
            e.printStackTrace()
        }

    }

    fun getFoldersInDirectory(dir: String): List<File> {
        Log.d("TAG", "getFoldersInDirectory: "+dir)

        val listOfFolder = mutableListOf<File>()
        return try {
            val directory = File(dir)

            val files = directory.listFiles { file -> file.isDirectory }
            files?.let {
                for (file in files) {
                    listOfFolder.add(file)
                }
            }
            Log.d("TAG", "getFoldersInDirectory: "+listOfFolder.size)
            listOfFolder
        } catch (e: Exception) {
            e.printStackTrace()
            listOfFolder
        }

    }

    fun getFileInDirectory(dir: String): List<ListItem> {
        val listOfFolder = mutableListOf<ListItem>()
        return try {
            val directory = File(dir)
            val files = directory.listFiles()
            for (file in files) {
                var type = ""
                var realType: String? = null
                when {
                    getMimeType(file.path).contains("image") -> type = Constant.TYPE_PICTURE
                    getMimeType(file.path).contains("video") -> type = Constant.TYPE_VIDEOS
                    getMimeType(file.path).contains("audio") || Constant.extraAudioMimeTypes.contains(
                        getMimeType(file.path)
                    ) -> type = Constant.TYPE_AUDIOS
                    else -> {
                        type = Constant.TYPE_FILE

                        if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PDF)) {
                            realType = Constant.TYPE_PDF
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_CSV)) {
                            realType = Constant.TYPE_CSV
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PPT)) {
                            realType = Constant.TYPE_PPT
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PPT)) {
                            realType = Constant.TYPE_PPTX
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_TEXT)) {
                            realType = Constant.TYPE_TEXT
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_WORD)) {
                            realType = Constant.TYPE_WORD
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_EXCEL)) {
                            realType = Constant.TYPE_EXCEL
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_WORDX)) {
                            realType = Constant.TYPE_WORD
                        } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_ZIP)) {
                            realType = Constant.TYPE_ZIP
                        }
                    }
                }

                listOfFolder.add(
                    ListItem(
                        0,
                        file.path,
                        file.path,
                        file.name,
                        false,
                        0,
                        file.length(),
                        file.lastModified(),
                        type,
                        realType
                    )
                )
            }
            listOfFolder
        } catch (e: Exception) {
            e.printStackTrace()
            listOfFolder
        }

    }

    fun deleteFolderInDirectory(pathFolder: String, callback: IDeleteFile) {
        try {
            val folder = File(pathFolder)
            val delete = folder.deleteRecursively()
            if (delete) {
                callback.onSuccess()
            } else {
                callback.onFailed()
            }
        } catch (e: Exception) {
            callback.onFailed()
            e.printStackTrace()
        }
    }

    fun copyMoveTo(
        filePath: List<String>, destinationPath: String, isCopy: Boolean, callback: IMoveFile
    ) {
        try {


            val directory = File(destinationPath)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            filePath.forEach {
                val sourceFile = File(it)
                var newName = sourceFile.name
                var counter = 1

                while (File(directory, newName).exists()) {
                    newName = "${sourceFile.nameWithoutExtension}($counter).${sourceFile.extension}"
                    counter++
                }
                val destinationFile = File(directory, newName)
                sourceFile.copyTo(destinationFile)
            }

            if (!isCopy) {
                filePath.forEach {
                    val sourceFile = File(it)
                    sourceFile.delete()
                }
            }
            callback.onSuccess()

        } catch (e: Exception) {
            callback.onFailed()
            e.printStackTrace()
        }
    }

    private fun getMimeType(url: String): String {
        var type = ""
        var extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).toString()
        }
        return type
    }

    fun renameFile(oldFile: File, name: String, callback: IRenameFile) {
        try {
            val newFile = File(oldFile.parentFile?.path + "/$name")
            if (oldFile.isDirectory) {
                org.apache.commons.io.FileUtils.moveDirectory(oldFile, newFile)
            } else {
                org.apache.commons.io.FileUtils.moveFile(oldFile, newFile)
            }
            callback.onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onFailed()
        }
    }



}