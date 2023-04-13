package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
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
        val listOfFolder = mutableListOf<File>()
        return try {
            val directory = File(dir)
            val files = directory.listFiles { file -> file.isDirectory }
            for (file in files) {
                listOfFolder.add(file)
            }
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
                if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PDF)) {
                    type = Constant.TYPE_PDF
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_CSV)) {
                    type = Constant.TYPE_CSV
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PPT)) {
                    type = Constant.TYPE_PPT
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PPT)) {
                    type = Constant.TYPE_PPTX
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_TEXT)) {
                    type = Constant.TYPE_TEXT
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_WORD)) {
                    type = Constant.TYPE_WORD
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_EXCEL)) {
                    type = Constant.TYPE_EXCEL
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_WORDX)) {
                    type = Constant.TYPE_WORD
                } else if (file.name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_ZIP)) {
                    type = Constant.TYPE_ZIP
                }
                listOfFolder.add(
                    ListItem(
                        0, file.path, file.name, false, 0, file.length(), file.lastModified(), type
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
}