package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import com.google.common.io.Files.getNameWithoutExtension
import com.neko.hiepdph.calculatorvault.common.Constant
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
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import java.io.File
import kotlin.math.floor
import kotlin.math.sqrt


object CopyFiles {
    fun encrypt(
        context: Context,
        sourceFileVault: List<FileVaultItem>,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileSuccess: MutableList<FileVaultItem>, listOfFileFailed: MutableList<FileVaultItem>) -> Unit,
        encryptionMode: Int = EncryptionMode.HIDDEN,
    ) {
        val listOfFileSuccess = mutableListOf<FileVaultItem>()
        val listOfFileFailed = mutableListOf<FileVaultItem>()
        if (sourceFileVault.isEmpty()) return
        val sourceFiles = sourceFileVault.map { File(it.originalPath) }
        var totalSize = 0L
        if (tSize == 0L) sourceFiles.forEach {
            totalSize += calFolderSize(it)
        }
        else totalSize = tSize
        var currentSize = 0f
        // move file

        sourceFiles.forEachIndexed { index, itemFile ->
            try {
                val item = sourceFileVault[index].copy()
                when (item.fileType) {
                    Constant.TYPE_PICTURE -> {
                        item.thumb = imagePathToBitmap(item.originalPath)?.let {
                            scaleBitmap(
                                it, 1024 * 1024
                            )
                        }
                    }

                    Constant.TYPE_VIDEOS -> {
                        item.thumb = MediaStoreUtils.getImageThumb(
                            item.mediaStoreId, context
                        )?.let { scaleBitmap(it, 1024 * 1024) }
                    }

                    Constant.TYPE_AUDIOS -> {
                        item.thumb = MediaStoreUtils.getThumbnail(item.originalPath)
                            ?.let { scaleBitmap(it, 1024 * 1024) }
                    }
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
                    { sourceFile, targetFiles, isSuccess ->
                        if (isSuccess) {
                            item.encryptedPath = targetFiles.path
                            listOfFileSuccess.add(item)
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
                            listOfFileFailed.add(sourceFileVault[index])
                        }

                    }, encryptionMode = encryptionMode
                )
            } catch (e: Exception) {
                listOfFileFailed.add(sourceFileVault[index])
            }
        }
        onResult(listOfFileSuccess, listOfFileFailed)
    }

    fun decrypt(
        context: Context,
        files: List<FileVaultItem>,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileVaultSuccess: MutableList<FileVaultItem>, listOfFileVaultFailed: MutableList<FileVaultItem>) -> Unit,
    ) {
        val listOfFileVaultSuccess = mutableListOf<FileVaultItem>()
        val listOfFileVaultFailed = mutableListOf<FileVaultItem>()
        if (files.isEmpty()) return
        val listSourceFiles =
            files.map { if (it.isDeleted) File(it.recyclerPath) else File(it.encryptedPath) }
        var totalSize = 0L
        if (tSize == 0L) listSourceFiles.forEach {
            totalSize += calFolderSize(it)
        }
        else totalSize = tSize
        var currentSize = 0f
        // move file
        listSourceFiles.forEachIndexed { index, itemFile ->
            try {
                if (files[index].decodePath != "" && File(files[index].decodePath).exists()) {
                    listOfFileVaultSuccess.add(files[index])
                } else {
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
                        { _, targetFiles, isSuccess ->
                            if (isSuccess) {
                                val newItem = files[index].copy()
                                newItem.decodePath = targetFiles.path
                                listOfFileVaultSuccess.add(newItem)
                                addMedia(context, targetFiles)
                                MediaScannerConnection.scanFile(
                                    context, arrayOf(targetFiles.path), null, null
                                )
                            } else {
                                listOfFileVaultFailed.add(files[index])
                            }

                        }, files[index].encryptionType
                    )
                }

            } catch (e: Exception) {
                listOfFileVaultFailed.add(files[index])
            }
        }
        onResult(listOfFileVaultSuccess, listOfFileVaultFailed)
    }

    fun unLock(
        context: Context,
        files: List<FileVaultItem>,
        targetFolders: List<File>,
        targetName: List<String>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<FileVaultItem>, listOfFileDeletedFailed: MutableList<FileVaultItem>) -> Unit,
    ) {
        val listOfFileSuccess = mutableListOf<FileVaultItem>()
        val listOfFileFailed = mutableListOf<FileVaultItem>()
        if (files.isEmpty()) return
        val listOfFile =
            files.map { if (it.isDeleted) File(it.recyclerPath) else File(it.encryptedPath) }
        var totalSize = 0L
        if (tSize == 0L) listOfFile.forEach {
            totalSize += calFolderSize(it)
        }
        else totalSize = tSize
        var currentSize = 0f
        // move file
        listOfFile.forEachIndexed { index, itemFile ->
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
                            listOfFileSuccess.add(files[index])
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
                            listOfFileFailed.add(files[index])

                        }

                    }, files[index].encryptionType
                )

            } catch (e: Exception) {
                listOfFileFailed.add(files[index])
            }
        }
        onResult(listOfFileSuccess, listOfFileFailed)
    }

    fun copy(
        context: Context,
        files: List<FileVaultItem>,
        targetFolders: List<File>,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<FileVaultItem>, listOfFileDeletedFailed: MutableList<FileVaultItem>) -> Unit,
        isMove: Boolean = false
    ) {
        val listOfFileSuccess = mutableListOf<FileVaultItem>()
        val listOfFileFailed = mutableListOf<FileVaultItem>()

        if (files.isEmpty()) return
        var totalSize = 0L
        val newListFile =
            files.map { if (it.isDeleted) File(it.recyclerPath) else File(it.encryptedPath) }
        newListFile.forEach {
            totalSize += calFolderSize(it)
        }
        var currentSize = 0f
        // move file
        newListFile.forEachIndexed { index, itemFile ->
            try {
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
                            listOfFileSuccess.add(files[index])
                            if (isMove) {
                                deleteFile(sourceFile, context)
                            }
                        } else {
                            listOfFileFailed.add(files[index])
                        }
                        addMedia(context, targetFile)
                        MediaScannerConnection.scanFile(
                            context, arrayOf(targetFile.path), null, null
                        )
                    })
            } catch (e: Exception) {
                listOfFileFailed.add(files[index])
            }
        }
        onResult(listOfFileSuccess, listOfFileFailed)

    }

    fun delete(
        context: Context,
        files: List<FileVaultItem>,
        targetFolders: List<File>,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<FileVaultItem>, listOfFileDeletedFailed: MutableList<FileVaultItem>) -> Unit,
    ) {
        val listOfFileSuccess = mutableListOf<FileVaultItem>()
        val listOfFileFailed = mutableListOf<FileVaultItem>()

        if (files.isEmpty()) return
        var totalSize = 0L
        val newListFile =
            files.map { if (it.isDeleted) File(it.recyclerPath) else File(it.encryptedPath) }
        newListFile.forEach {
            totalSize += calFolderSize(it)
        }
        var currentSize = 0f
        // move file
        newListFile.forEachIndexed { index, itemFile ->
            try {
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
                            val newItem = files[index].copy().apply {
                                recyclerPath = targetFile.path
                            }
                            listOfFileSuccess.add(newItem)
                            deleteFile(sourceFile, context)
                        } else {
                            listOfFileFailed.add(files[index])
                        }
                        addMedia(context, targetFile)
                        MediaScannerConnection.scanFile(
                            context, arrayOf(targetFile.path), null, null
                        )
                    })
            } catch (e: Exception) {
                listOfFileFailed.add(files[index])
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