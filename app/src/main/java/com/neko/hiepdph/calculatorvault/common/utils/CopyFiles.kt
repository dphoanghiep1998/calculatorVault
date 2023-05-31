package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import java.io.File

private const val STATE_PREPARE = 1
const val STATE_PROCESSING = 2
private const val STATE_DONE = 3

object CopyFiles {
    fun encrypt(
        context: Context,
        files: List<File>?,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onSuccess: (MutableList<String>) -> Unit = {},
        onError: (t: Throwable) -> Unit = {},
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        try {
            val listOfFile = mutableListOf<String>()
            if (files?.isEmpty() == true) return
            var totalSize = 0L
            if (tSize == 0L) files?.forEach {
                totalSize += calFolderSize(it)
            }
            else totalSize = tSize
            var currentSize = 0f
            // move file
            files?.forEachIndexed { index, itemFile ->
                if (targetFolders[index].exists()) {
                    listOfFile.add(targetFolders[index].name)
                    return@forEachIndexed
                }
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
                    { sourceFile, targetFile ->
                        listOfFile.add(targetFile.name)
                        deleteFile(sourceFile, context)
                        addMedia(context, targetFile)
                    }, encryptionMode
                )

            }
            onSuccess(listOfFile)


        } catch (e: Exception) {
            onError(e)
        }
    }

    fun decrypt(
        context: Context,
        files: List<File>?,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: ( value: Float, currentFile: File?) -> Unit,
        onSuccess: (MutableList<String>) -> Unit = {},
        onError: (t: Throwable) -> Unit = {},
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        try {
            val listOfFile = mutableListOf<String>()

            if (files?.isEmpty() == true) return
            var totalSize = 0L
            if (tSize == 0L) files?.forEach {
                totalSize += calFolderSize(it)
            }
            else totalSize = tSize
            var currentSize = 0f
            // move file
            files?.forEachIndexed { index, itemFile ->
                if (targetFolders[index].exists()) {
                    listOfFile.add(targetFolders[index].name)
                    return@forEachIndexed
                }
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
                            progress((currentSize * 100 / totalSize), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFile ->
                        listOfFile.add(targetFolders[index].name)
                        deleteFile(sourceFile, context)
                        addMedia(context, targetFile)
                    }, encryptionMode
                )

            }
            onSuccess(listOfFile)


        } catch (e: Exception) {
            onError(e)
        }
    }

    fun copy(
        context: Context,
        files: List<File>?,
        targetFolders: List<File>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onSuccess: (MutableList<String>) -> Unit = {},
        onError: (t: Throwable) -> Unit = {},
    ) {
        try {
            val listOfFile = mutableListOf<String>()
            if (files?.isEmpty() == true) return
            var totalSize = 0L
            if (tSize == 0L) files?.forEach {
                totalSize += calFolderSize(it)
            }
            else totalSize = tSize
            var currentSize = 0f
            // move file
            files?.forEachIndexed { index, itemFile ->
                if (targetFolders[index].exists()) {
                    listOfFile.add(targetFolders[index].name)
                    return@forEachIndexed
                }
                val targetFile = if (itemFile.isDirectory) {
                    File(createNewFolder(context, targetFolders[index], itemFile.name))
                } else {
                    File(createNewFile(context, targetFolders[index], itemFile.name).toString())
                }
                copyFileToAnotherLocation(context, itemFile, targetFile,
                    // on progress
                    { len, file ->
                        run {
                            currentSize += len
                            progress(
                                (currentSize * 100 / tSize), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFile ->
                        listOfFile.add(targetFile.name)
                        deleteFile(sourceFile, context)
                        addMedia(context, targetFile)
                    })
            }
            onSuccess(listOfFile)


        } catch (e: Exception) {
            onError(e)
        }
    }


    private fun calFolderSize(it: File): Long {
        return it.length()
    }


    fun addMedia(c: Context, f: File?) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.setData(Uri.fromFile(f))
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