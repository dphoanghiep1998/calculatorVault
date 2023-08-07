package com.neko.hiepdph.calculatorvault.common.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Log
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.Constant.DECRYPT_FOLDER_NAME
import com.neko.hiepdph.calculatorvault.common.Constant.PRIVACY_FOLDER_NAME
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_AUDIOS
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_CSV
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_EXCEL
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_FILE
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_OTHER
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_PDF
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_PICTURE
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_PPT
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_RAR
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_TEXT
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_VIDEOS
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_WORD
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_ZIP
import com.neko.hiepdph.calculatorvault.common.Constant.archiveMimeTypes
import com.neko.hiepdph.calculatorvault.common.Constant.extraAudioMimeTypes
import com.neko.hiepdph.calculatorvault.common.Constant.extraDocumentMimeTypes
import com.neko.hiepdph.calculatorvault.common.extensions.getArtist
import com.neko.hiepdph.calculatorvault.common.extensions.getDuration
import com.neko.hiepdph.calculatorvault.common.extensions.getLongValue
import com.neko.hiepdph.calculatorvault.common.extensions.getStringValue
import com.neko.hiepdph.calculatorvault.common.extensions.queryCursor
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.model.GroupItem
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale

object MediaStoreUtils {
    fun getListGroupItem(context: Context, type: String): List<GroupItem> {
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns._ID,
        )
        val folders = mutableListOf<Pair<String, GroupItem>>()

        try {
            context.queryCursor(uri, projection) { cursor ->
                try {
                    val name = cursor.getStringValue(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    if (name.startsWith(".")) {
                        return@queryCursor
                    }
                    val fullMimetype = cursor.getStringValue(MediaStore.Files.FileColumns.MIME_TYPE)
                        ?.lowercase(Locale.getDefault()) ?: return@queryCursor
                    val size = cursor.getLongValue(MediaStore.Files.FileColumns.SIZE)
                    if (size == 0L) {
                        return@queryCursor
                    }

                    val path = cursor.getStringValue(MediaStore.Files.FileColumns.DATA)

                    val mimetype = fullMimetype.substringBefore("/")

                    when (type) {
                        TYPE_PICTURE -> {
                            if (mimetype == "image") {
                                val parentFolderPath = File(path).parentFile?.path
                                val parentFolder = File(path).parentFile?.name ?: "No_name"
                                if (parentFolder.startsWith(".") || parentFolderPath?.contains(
                                        PRIVACY_FOLDER_NAME
                                    ) == true
                                ) {
                                    return@queryCursor
                                }
                                if (!folders.any { it.first == parentFolder }) {
                                    folders.add(
                                        Pair(
                                            parentFolder, GroupItem(
                                                parentFolder,
                                                TYPE_PICTURE,
                                                mutableListOf(),
                                                parentFolderPath.toString()
                                            )
                                        )
                                    )
                                } else {
                                    if (!folders.any { it.second.folderPath == parentFolderPath }) {
                                        folders.add(
                                            Pair(
                                                parentFolder, GroupItem(
                                                    parentFolder,
                                                    TYPE_PICTURE,
                                                    mutableListOf(),
                                                    parentFolderPath.toString()
                                                )
                                            )
                                        )
                                    }
                                }

                                if (path.isNotEmpty()) {

                                    folders.find { it.second.folderPath == parentFolderPath }?.second?.dataList?.add(
                                        path
                                    )
                                }
                            }
                        }

                        TYPE_VIDEOS -> {
                            if (mimetype == "video") {

                                val parentFolderPath = File(path).parentFile?.path
                                val parentFolder = File(path).parentFile?.name ?: "No_name"
                                if (parentFolder.startsWith(".") || parentFolderPath?.contains(
                                        PRIVACY_FOLDER_NAME
                                    ) == true || parentFolderPath?.contains(
                                        DECRYPT_FOLDER_NAME
                                    ) == true
                                ) {
                                    return@queryCursor
                                }
                                if (!folders.any { it.first == parentFolder }) {
                                    folders.add(
                                        Pair(
                                            parentFolder, GroupItem(
                                                parentFolder,
                                                TYPE_VIDEOS,
                                                mutableListOf(),
                                                File(path).parentFile?.path ?: ""
                                            )
                                        )
                                    )
                                } else if (!folders.any { it.second.folderPath == parentFolderPath }) {
                                    folders.add(
                                        Pair(
                                            parentFolder, GroupItem(
                                                parentFolder,
                                                TYPE_VIDEOS,
                                                mutableListOf(),
                                                File(path).parentFile?.path ?: ""
                                            )
                                        )
                                    )
                                }

                                if (path.isNotEmpty()) {
                                    folders.find { it.second.folderPath == parentFolderPath }?.second?.dataList?.add(
                                        path
                                    )
                                }
                            }
                        }

                        TYPE_AUDIOS -> {
                            if (mimetype == "audio" || extraAudioMimeTypes.contains(fullMimetype)) {

                                val parentFolderPath = File(path).parentFile?.path
                                val parentFolder = File(path).parentFile?.name ?: "No_name"
                                if (parentFolder.startsWith(".") || parentFolderPath?.contains(
                                        PRIVACY_FOLDER_NAME
                                    ) == true
                                ) {
                                    return@queryCursor
                                }
                                if (!folders.any { it.first == parentFolder }) {
                                    folders.add(
                                        Pair(
                                            parentFolder, GroupItem(
                                                parentFolder,
                                                TYPE_AUDIOS,
                                                mutableListOf(),
                                                File(path).parentFile?.path ?: ""
                                            )
                                        )
                                    )
                                } else {
                                    if (!folders.any { it.second.folderPath == parentFolderPath }) {
                                        folders.add(
                                            Pair(
                                                parentFolder, GroupItem(
                                                    parentFolder,
                                                    TYPE_AUDIOS,
                                                    mutableListOf(),
                                                    File(path).parentFile?.path ?: ""
                                                )
                                            )
                                        )
                                    }
                                }

                                if (path.isNotEmpty()) {
                                    folders.find { it.second.folderPath == parentFolderPath }?.second?.dataList?.add(
                                        path
                                    )
                                }
                            }
                        }

                        TYPE_FILE -> {
                            if (mimetype == "text" || extraDocumentMimeTypes.contains(fullMimetype) || archiveMimeTypes.contains(
                                    fullMimetype
                                )
                            ) {
                                val parentFolder = Constant.FILES_FOLDER_NAME
                                if (!folders.any { it.first == parentFolder }) {
                                    folders.add(
                                        Pair(
                                            parentFolder, GroupItem(
                                                parentFolder,
                                                TYPE_FILE,
                                                mutableListOf(),
                                                File(path).parentFile?.path ?: "",
                                                mutableSetOf()
                                            )
                                        )
                                    )
                                }
                                if (path.isNotEmpty() && !path.contains(PRIVACY_FOLDER_NAME)) {
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_PDF)) {
                                        folders[0].second.dataTypeList?.add(TYPE_PDF)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_CSV)) {
                                        folders[0].second.dataTypeList?.add(TYPE_CSV)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_PPT)) {
                                        folders[0].second.dataTypeList?.add(TYPE_PPT)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_PPT)) {
                                        folders[0].second.dataTypeList?.add(Constant.TYPE_PPTX)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_TEXT)) {
                                        folders[0].second.dataTypeList?.add(TYPE_TEXT)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_WORD)) {
                                        folders[0].second.dataTypeList?.add(TYPE_WORD)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_EXCEL)) {
                                        folders[0].second.dataTypeList?.add(TYPE_EXCEL)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_WORDX)) {
                                        folders[0].second.dataTypeList?.add(TYPE_WORD)
                                        return@queryCursor
                                    }
                                    if (name.lowercase(Locale.ROOT).endsWith(TYPE_ZIP)) {
                                        folders[0].second.dataTypeList?.add(TYPE_ZIP)
                                        return@queryCursor
                                    }
                                    folders[0].second.dataTypeList?.add(TYPE_OTHER)

                                }


                            }
                        }

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return folders.map { it.second }
    }

    fun getThumbnail(path: String): Bitmap? {
        return try {
            val mr = MediaMetadataRetriever()
            mr.setDataSource(path)
            val byte1 = mr.embeddedPicture
            mr.release()
            if (byte1 != null) BitmapFactory.decodeByteArray(byte1, 0, byte1.size)
            else null
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun getAllImage(context: Context): List<FileVaultItem> {
        try {
            val listImageChild = mutableListOf<FileVaultItem>()
            val uri = MediaStore.Images.Media.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media._ID
            )
            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"


            context.queryCursor(uri, projection, sortOrder = sortOrder) { cursor ->

                val id = cursor.getLongValue(MediaStore.Images.Media._ID)
                val childPath = cursor.getStringValue(MediaStore.Images.Media.DATA)
                val size = cursor.getLongValue(MediaStore.Images.Media.SIZE)
                val modified = cursor.getLongValue(MediaStore.Images.Media.SIZE) * 1000
                Log.d("TAG", "getAllImage: " + modified)
                val name = cursor.getStringValue(MediaStore.Images.Media.DISPLAY_NAME)

                if (childPath.isNotBlank()) {
                    listImageChild.add(
                        FileVaultItem(
                            0,
                            childPath,
                            "",
                            "",
                            "",
                            name,
                            size,
                            modified,
                            0L,
                            id,
                            getImageResolution(childPath),
                            "",
                            0,
                            1,
                            TYPE_PICTURE,
                        )
                    )
                }
            }
            return listImageChild
        } catch (e: Exception) {
            e.printStackTrace()
            return mutableListOf()
        }
    }

    fun getChildImageFromPath(context: Context, path: String): MutableList<FileVaultItem> {
        try {
            val listImageChild = mutableListOf<FileVaultItem>()
            val uri = MediaStore.Images.Media.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media._ID
            )
            val selection =
                MediaStore.Images.Media.DATA + " LIKE ? AND " + MediaStore.Images.Media.DATA + " NOT LIKE ?"
            val selectionArgs = arrayOf("$path/%", "$path/%/%")

            context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->

                val id = cursor.getLongValue(MediaStore.Images.Media._ID)
                val childPath = cursor.getStringValue(MediaStore.Images.Media.DATA)
                val size = cursor.getLongValue(MediaStore.Images.Media.SIZE)
                val modified = cursor.getLongValue(MediaStore.Images.Media.DATE_MODIFIED) * 1000
                Log.d("TAG", "getAllImage: " + modified)

                val name = cursor.getStringValue(MediaStore.Images.Media.DISPLAY_NAME)
                val file = File(childPath)
                if (!file.exists() || file.isHidden || !file.canRead()) return@queryCursor

                if (childPath.isNotBlank() && childPath != path) {
                    Log.d("TAG", "getChildImageFromPath: " + childPath)
                    listImageChild.add(
                        FileVaultItem(
                            0,
                            childPath,
                            "",
                            "",
                            "",
                            name,
                            size,
                            modified,
                            0L,
                            id,
                            getImageResolution(childPath),
                            "",
                            0,
                            1,
                            TYPE_PICTURE,
                        )
                    )
                }
            }
            return listImageChild
        } catch (e: Exception) {
            e.printStackTrace()
            return mutableListOf()
        }

    }

    fun getChildVideoFromPath(context: Context, path: String): MutableList<FileVaultItem> {

        val listVideoChild = mutableListOf<FileVaultItem>()
        try {
            val uri = MediaStore.Video.Media.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media._ID,
            )
            val selection = MediaStore.Video.Media.DATA + " LIKE ?"
            val selectionArgs = arrayOf("$path/%")
            context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->
                val id = cursor.getLongValue(MediaStore.Video.Media._ID)
                val childPath = cursor.getStringValue(MediaStore.Video.Media.DATA)
                val size = cursor.getLongValue(MediaStore.Video.Media.SIZE)
                val modified = cursor.getLongValue(MediaStore.Video.Media.DATE_MODIFIED) * 1000
                val name = cursor.getStringValue(MediaStore.Video.Media.DISPLAY_NAME)
                val file = File(childPath)
                if (!file.exists() || file.isHidden || !file.canRead()) return@queryCursor
                if (childPath.isNotBlank()) {
                    listVideoChild.add(
                        FileVaultItem(
                            0,
                            childPath,
                            "",
                            "",
                            "",
                            name,
                            size,
                            modified,
                            0L,
                            id,
                            null,
                            context.getArtist(childPath).toString(),
                            context.getDuration(childPath) ?: 0,
                            1,
                            TYPE_VIDEOS,
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listVideoChild

    }

    fun getChildAudioFromPath(
        context: Context, path: String
    ): MutableList<FileVaultItem> {
        val listAudioChild = mutableListOf<FileVaultItem>()

        try {

            val uri = MediaStore.Audio.Media.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media._ID,
            )
            val selection = MediaStore.Audio.Media.DATA + " LIKE ?"

            val selectionArgs = arrayOf("$path/%")
            context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->
                val id = cursor.getLongValue(MediaStore.Audio.Media._ID)
                val childPath = cursor.getStringValue(MediaStore.Audio.Media.DATA)
                val size = cursor.getLongValue(MediaStore.Audio.Media.SIZE)
                val modified = cursor.getLongValue(MediaStore.Audio.Media.DATE_MODIFIED) * 1000
                val name = cursor.getStringValue(MediaStore.Audio.Media.DISPLAY_NAME)
                val file = File(childPath)
                if (!file.exists() || file.isHidden || !file.canRead()) return@queryCursor
                if (childPath.isNotBlank()) {
                    listAudioChild.add(
                        FileVaultItem(
                            0,
                            childPath,
                            "",
                            "",
                            "",
                            name,
                            size,
                            modified,
                            0L,
                            id,
                            null,
                            context.getArtist(childPath).toString(),
                            context.getDuration(childPath) ?: 0,
                            1,
                            TYPE_AUDIOS,
                            null,
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listAudioChild
    }

    fun getChildFileFromPath(
        context: Context, path: String, type: String
    ): MutableList<FileVaultItem> {
        val listFileChild = mutableListOf<FileVaultItem>()
        try {
            val uri = MediaStore.Files.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MIME_TYPE
            )
            context.queryCursor(uri, projection) { cursor ->
                val fullMimetype = cursor.getStringValue(MediaStore.Files.FileColumns.MIME_TYPE)
                    ?.lowercase(Locale.getDefault()) ?: return@queryCursor
                val id = cursor.getLongValue(MediaStore.Files.FileColumns._ID)
                val childPath = cursor.getStringValue(MediaStore.Files.FileColumns.DATA)
                val size = cursor.getLongValue(MediaStore.Files.FileColumns.SIZE)
                val modified =
                    cursor.getLongValue(MediaStore.Files.FileColumns.DATE_MODIFIED) * 1000
                Log.d("TAG", "getChildFileFromPath: " + modified)
                val name = cursor.getStringValue(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val file = File(childPath)
                if (!file.exists() || file.isHidden || !file.canRead()) return@queryCursor
                val mimetype = fullMimetype.substringBefore("/")

                if (mimetype == "text" || extraDocumentMimeTypes.contains(fullMimetype) || archiveMimeTypes.contains(
                        fullMimetype
                    )
                ) {
                    if (path.isNotEmpty() && !childPath.contains(PRIVACY_FOLDER_NAME)) {
                        when (type) {
                            TYPE_PDF -> {
                                if (name.lowercase(Locale.ROOT).endsWith(TYPE_PDF)) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            null,
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_PDF,
                                        )
                                    )

                                }
                            }

                            TYPE_PPT, Constant.TYPE_PPTX -> {
                                if (name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_PPTX) || (name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_PPT))
                                ) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            null,
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_PPT,
                                        )
                                    )
                                }
                            }

                            Constant.TYPE_WORDX, TYPE_WORD -> {
                                if (name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_WORD) || name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_WORDX)
                                ) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            getImageResolution(childPath),
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_WORD,
                                        )
                                    )
                                }
                            }

                            TYPE_EXCEL -> {
                                if (name.lowercase(Locale.ROOT).endsWith(TYPE_EXCEL)) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            getImageResolution(childPath),
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_EXCEL,
                                        )
                                    )
                                }
                            }

                            TYPE_CSV -> {
                                if (name.lowercase(Locale.ROOT).endsWith(TYPE_CSV)) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            null,
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_CSV,
                                        )
                                    )
                                }
                            }

                            TYPE_TEXT -> {
                                if (name.lowercase(Locale.ROOT).endsWith(TYPE_TEXT)) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            null,
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_TEXT,
                                        )
                                    )
                                }
                            }

                            TYPE_ZIP -> {
                                if (name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_ZIP) || name.lowercase(Locale.ROOT).endsWith(
                                        TYPE_RAR
                                    )
                                ) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            null,
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_ZIP,
                                        )
                                    )
                                }
                            }

                            else -> {
                                if (!name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_PDF) && !name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_PPTX) && !name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_PPT) && !name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_ZIP) && !name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_TEXT) && !name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_CSV) && !name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_EXCEL) && !name.lowercase(Locale.ROOT)
                                        .endsWith(TYPE_WORD) && !name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_WORDX)
                                ) {
                                    listFileChild.add(
                                        FileVaultItem(
                                            0,
                                            childPath,
                                            "",
                                            "",
                                            "",
                                            name,
                                            size,
                                            modified,
                                            0L,
                                            id,
                                            null,
                                            null,
                                            null,
                                            1,
                                            TYPE_FILE,
                                            TYPE_OTHER,
                                        )
                                    )
                                }
                            }
                        }

                    }
                }
            }
        } catch (e: Exception) {
            return listFileChild
        }
        return listFileChild

    }

    private fun getImageResolution(imagePath: String): String {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(imagePath, options)
        return "${options.outWidth}x${options.outHeight}"
    }

    fun getImageThumb(id: Long, context: Context): ByteArray? {
        val crThumb: ContentResolver = context.contentResolver
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 1
        val curThumb = MediaStore.Video.Thumbnails.getThumbnail(
            crThumb,
            id,
            MediaStore.Video.Thumbnails.MICRO_KIND,
            options
        )
        if (curThumb != null) {
            val stream = ByteArrayOutputStream()
            curThumb.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            curThumb.recycle()
            return byteArray
        }
        return null
    }


}
