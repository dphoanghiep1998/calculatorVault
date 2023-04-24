package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.google.common.io.Files.getNameWithoutExtension
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.copyDirectoryOneLocationToAnotherLocation
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.createNewFile
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.deleteFile
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.getExtension
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.isWritableNormalOrSaf
import com.neko.hiepdph.calculatorvault.common.utils.FileNameUtils.mkdir
import java.io.File

private const val STATE_PREPARE = 1
const val STATE_PROCESSING = 2
private const val STATE_DONE = 3

object CopyFiles {
    fun copy(
        context: Context,
        files: MutableList<File>?,
        targetFolder: File,
        tSize: Long,
        progress: (state: Int, value: Float, currentFile: File?) -> Unit,
        isMove: Boolean = false,
        onSuccess: () -> Unit = {},
        onError: (t: Throwable) -> Unit = {},
    ) {
        try {
            if (files?.isEmpty() == true) return
            var totalSize = 0L
            if (tSize == 0L) files?.forEach {
                totalSize += calFolderSize(it)
            }
            else totalSize = tSize
            var currentSize = 0f
            // move file
            files?.forEach { itemFile ->
                val targetFile = if (itemFile.isDirectory) {
                    File(createNewFolder(context, targetFolder, itemFile.name))
                } else {
                    File(createNewFile(context, targetFolder, itemFile.name))
                }

                copyDirectoryOneLocationToAnotherLocation(context, itemFile, targetFile,
                    // on progress
                    { len, file ->
                        run {
                            currentSize += len
                            progress(
                                STATE_PROCESSING, (currentSize * 100 / tSize).toFloat(), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFile ->
                        if (isMove) {
                            deleteFile(sourceFile, context)
                        }
                        addMedia(context, targetFile)
                        onSuccess()
                    })
            }

        } catch (e: Exception) {
            onError(e)
        }
    }

    fun copy(
        context: Context,
        files: MutableList<File>?,
        targetFolder: MutableList<File>,
        tSize: Long,
        progress: (state: Int, value: Float, currentFile: File?) -> Unit,
        isMove: Boolean = false,
        onSuccess: () -> Unit = {},
        onError: (t: Throwable) -> Unit = {},
    ) {
        try {
            if (files?.isEmpty() == true) return
            var totalSize = 0L
            if (tSize == 0L) files?.forEach {
                totalSize += calFolderSize(it)
            }
            else totalSize = tSize
            var currentSize = 0f
            // move file
            files?.forEachIndexed { index, itemFile ->
                val targetFile = if (itemFile.isDirectory) {
                    File(createNewFolder(context, targetFolder[index], itemFile.name))

                } else {
                    File(createNewFile(context, targetFolder[index], itemFile.name).toString())

                }
                copyDirectoryOneLocationToAnotherLocation(context, itemFile, targetFile,
                    // on progress
                    { len, file ->
                        run {
                            currentSize += len
                            progress(
                                STATE_PROCESSING, (currentSize * 100 / tSize).toFloat(), file
                            )
                        }
                    },
                    // on finish
                    { sourceFile, targetFile ->
                        if (isMove) {
                            deleteFile(sourceFile, context)
                        }
                        addMedia(context, targetFile)
                        onSuccess()
                    })
            }

        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun calFolderSize(it: File): Long {
        return it.length()
    }

    fun copy(
        context: Context,
        file: File,
        targetFolder: File,
        tSize: Long,
        progress: (state: Int, value: Float, currentFile: File?) -> Unit,
        isMove: Boolean = false,
        onSuccess: () -> Unit = {},
        onError: (t: Throwable) -> Unit = {},
    ) {
        try {

//            if (!isWritableNormalOrSaf(file, context)) {
//                throw Exception(file.parent)
//            }

            var currentSize = 0f
            // move file
//                 = if (file.isDirectory) {
//                    File(createNewFolder(context, targetFolder, file.name))
//                } else {
            var targetFile = File(createNewFile(context, targetFolder, file.name))
//                }
            copyDirectoryOneLocationToAnotherLocation(context, file, targetFile,
                // on progress
                { len, file ->
                    run {
                        currentSize += len
                        progress(
                            STATE_PROCESSING, (currentSize * 100 / tSize).toFloat(), file
                        )
                    }
                },
                // on finish
                { sourceFile, targetFile ->
                    if (isMove) {
                        deleteFile(sourceFile, context)
                    }
                    addMedia(context, targetFile)
                    onSuccess()
                })


        } catch (e: Exception) {
            onError(e)
        }
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