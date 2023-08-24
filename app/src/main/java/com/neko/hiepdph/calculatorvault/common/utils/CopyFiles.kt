package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import com.google.common.io.Files.getNameWithoutExtension
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.copyFileToAnotherLocation
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.createNewFile
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.decryptFileToAnotherLocation
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.deleteFile
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.encryptFileToAnotherLocation
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.getExtension
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.isWritableNormalOrSaf
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.mkdir
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.unLockFile
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import java.io.File


object CopyFiles {
    fun encrypt(
        context: Context,
        files: List<File>?,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileSuccess: MutableList<String>, listOfFileFailed: MutableList<String>) -> Unit,
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        val listOfFileSuccess = mutableListOf<String>()
        val listOfFileFailed = mutableListOf<String>()
        if (files?.isEmpty() == true) return
        var totalSize = 0L
        if (tSize == 0L) files?.forEach {
            totalSize += calFolderSize(it)
        }
        else totalSize = tSize
        var currentSize = 0f
        // move file

        files?.forEachIndexed { index, itemFile ->
            try {
                val targetFile = File(
                    createNewFile(
                        context,
                        targetFolders[index],
                        targetName[index],
                    ).toString()
                )
                encryptFileToAnotherLocation(
                    context, itemFile, targetFile,
                    // on progress
                    { len, file ->
                        run {
                            currentSize += len
                            progress(
                                (currentSize * 100 / totalSize), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFiles, isSuccess ->
                        if (isSuccess) {
                            listOfFileSuccess.add(sourceFile.path)
                            deleteFile(sourceFile, context)
                            addMedia(context, targetFiles)
                            context.contentResolver.delete(
                                MediaStore.Files.getContentUri("external"),
                                MediaStore.MediaColumns.DATA + "=?",
                                arrayOf(sourceFile.path)
                            )
                            MediaScannerConnection.scanFile(
                                context, arrayOf(sourceFile.path), null, null
                            )
                        } else {
                            listOfFileFailed.add(sourceFile.path)
                        }

                    }, encryptionMode = encryptionMode
                )
            } catch (e: Exception) {
                listOfFileFailed.add(itemFile.path)
            }
        }
        onResult(listOfFileSuccess, listOfFileFailed)
    }

    fun decrypt(
        context: Context,
        files: List<File>?,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDecryptSuccess: MutableList<String>, listOfTargetFile: MutableList<String>, listOfFileDecryptFailed: MutableList<String>) -> Unit,
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        val listOfFileSuccess = mutableListOf<String>()
        val listOfFileTargetSuccess = mutableListOf<String>()
        val listOfFileFailed = mutableListOf<String>()
        var tempIndex = 0
        if (files?.isEmpty() == true) return
        var totalSize = 0L
        if (tSize == 0L) files?.forEach {
            totalSize += calFolderSize(it)
        }
        else totalSize = tSize
        var currentSize = 0f
        // move file
        files?.forEachIndexed { index, itemFile ->
            try {

                tempIndex = index
                val targetFile = File(
                    createNewFile(
                        context,
                        targetFolders[index],
                        targetName[index],
                    ).toString()
                )
                decryptFileToAnotherLocation(
                    context, itemFile, targetFile,
                    // on progress
                    { len, file ->
                        run {
                            currentSize += len
                            progress(
                                (currentSize * 100 / totalSize), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFiles, isSuccess ->
                        if (isSuccess) {
                            listOfFileSuccess.add(sourceFile.path)
                            listOfFileTargetSuccess.add(targetFiles.path)
                            addMedia(context, targetFiles)
                            MediaScannerConnection.scanFile(
                                context, arrayOf(targetFiles.path), null, null
                            )
                        } else {
                            listOfFileFailed.add(sourceFile.path)
                        }

                    }, encryptionMode
                )
            } catch (e: Exception) {
                listOfFileFailed.add(itemFile.path)
            }
        }
        onResult(listOfFileSuccess, listOfFileTargetSuccess, listOfFileFailed)
    }

    fun unLock(
        context: Context,
        files: List<File>?,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit,
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        val listOfFileSuccess = mutableListOf<String>()
        val listOfFileFailed = mutableListOf<String>()
        if (files?.isEmpty() == true) return
        var totalSize = 0L
        if (tSize == 0L) files?.forEach {
            totalSize += calFolderSize(it)
        }
        else totalSize = tSize
        var currentSize = 0f
        // move file
        files?.forEachIndexed { index, itemFile ->
            try {
                val targetFile = File(
                    createNewFile(
                        context,
                        targetFolders[index],
                        targetName[index],
                    ).toString()
                )

                unLockFile(
                    context, itemFile, targetFile,
                    // on progress
                    { len, file ->
                        run {
                            currentSize += len
                            progress(
                                (currentSize * 100 / totalSize), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFile, isSuccess ->
                        if (isSuccess) {
                            listOfFileSuccess.add(sourceFile.path)
                            deleteFile(sourceFile, context)
                            addMedia(context, targetFile)
                            context.contentResolver.delete(
                                MediaStore.Files.getContentUri("external"),
                                MediaStore.MediaColumns.DATA + "=?",
                                arrayOf(sourceFile.path)
                            )
                            MediaScannerConnection.scanFile(
                                context, arrayOf(sourceFile.path), null, null
                            )
                        } else {
                            listOfFileFailed.add(sourceFile.path)

                        }

                    }, encryptionMode
                )

            } catch (e: Exception) {
                listOfFileFailed.add(itemFile.path)
            }
        }
        onResult(listOfFileSuccess, listOfFileFailed)
    }

    fun copy(
        context: Context,
        files: List<File>?,
        targetFolders: List<File>,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit,
        isMove: Boolean = false
    ) {
        val listOfFileSuccess = mutableListOf<String>()
        val listOfFileFailed = mutableListOf<String>()

        if (files?.isEmpty() == true) return
        var totalSize = 0L
        files?.forEach {
            totalSize += calFolderSize(it)
        }
        var currentSize = 0f
        // move file
        files?.forEachIndexed { index, itemFile ->
            try {
                if (File(targetFolders[index], files[index].name).exists()) {
                    listOfFileSuccess.add(targetFolders[index].path + "/${files[index].name}")
                    return@forEachIndexed
                }
                val targetFile =
                    File(createNewFile(context, targetFolders[index], itemFile.name).toString())
                copyFileToAnotherLocation(context, itemFile, targetFile,
                    // on progress
                    { len, file ->
                        run {
                            currentSize += len
                            progress(
                                (currentSize * 100 / totalSize), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFile, isSuccess ->
                        if (isSuccess) {
                            listOfFileSuccess.add(targetFile.path)
                            if (isMove) {
                                deleteFile(sourceFile, context)
                            }
                        } else {
                            listOfFileFailed.add(targetFile.path)
                        }
                        addMedia(context, targetFile)
                        MediaScannerConnection.scanFile(
                            context, arrayOf(targetFile.path), null, null
                        )
                    })
            } catch (e: Exception) {
                listOfFileFailed.add(itemFile.path)
            }
        }
        onResult(listOfFileSuccess, listOfFileFailed)

    }


    private fun calFolderSize(it: File): Long {
        return it.length()
    }


    private fun addMedia(c: Context, f: File?) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(f)
        c.sendBroadcast(intent)
    }

    fun createNewFolder(
        context: Context, currentFolder: File, name: String, index: Int = 0
    ): String {
        val newFile = File(currentFolder, createNewName(currentFolder, name))

        if (!isWritableNormalOrSaf(currentFolder, context)) {
            throw Exception(currentFolder.absolutePath)
        }

        mkdir(context, newFile)
        return newFile.absolutePath
    }

    private fun createNewName(currentFolder: File, name: String, index: Int = 0): String {
        return if (currentFolder.listFiles()?.any { it.name == name } == true) {

            val newFile = File(currentFolder, name)
            val extension = getExtension(newFile)

            var newIndex = 1
            var newName =
                getNameWithoutExtension(newFile.absolutePath) + "($newIndex)" + if (TextUtils.isEmpty(
                        extension
                    )
                ) "" else ".$extension"

            val patttern = java.util.regex.Pattern.compile(".*\\([0-9]*\\)\\..*")

            if (patttern.matcher(name).find()) {
                val lastIndexClose = name.lastIndexOf(")")
                val lastIndexOpen = name.lastIndexOf("(")
                if (lastIndexOpen in 1 until lastIndexClose) {
                    val currentIndex =
                        name.substring(lastIndexOpen + 1, lastIndexClose).toIntOrNull() ?: 0

                    if (currentIndex == 0) {
                        newName =
                            getNameWithoutExtension(newFile.absolutePath) + "($newIndex)" + if (TextUtils.isEmpty(
                                    extension
                                )
                            ) "" else ".$extension"
                    } else {
                        newIndex = currentIndex + 1
                        newName = name.substring(
                            0, lastIndexOpen
                        ) + "($newIndex)" + if (TextUtils.isEmpty(extension)) "" else ".$extension"
                    }
                }
            }

            createNewName(currentFolder, newName, newIndex)
        } else {
            name
        }
    }
}