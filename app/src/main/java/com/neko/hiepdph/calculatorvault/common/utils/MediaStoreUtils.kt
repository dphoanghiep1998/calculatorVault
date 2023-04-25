package com.neko.hiepdph.calculatorvault.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.Constant.PRIVACY_FOLDER_NAME
import com.neko.hiepdph.calculatorvault.common.Constant.TYPE_AUDIOS
import com.neko.hiepdph.calculatorvault.common.Constant.archiveMimeTypes
import com.neko.hiepdph.calculatorvault.common.Constant.extraAudioMimeTypes
import com.neko.hiepdph.calculatorvault.common.Constant.extraDocumentMimeTypes
import com.neko.hiepdph.calculatorvault.common.extensions.getLongValue
import com.neko.hiepdph.calculatorvault.common.extensions.getStringValue
import com.neko.hiepdph.calculatorvault.common.extensions.queryCursor
import com.neko.hiepdph.calculatorvault.data.model.GroupItem
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import java.io.File
import java.util.*

object MediaStoreUtils {
    fun getListGroupItem(context: Context, type: String): List<GroupItem> {
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns._ID
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
                    val id = cursor.getLongValue(MediaStore.Files.FileColumns._ID)
                    val size = cursor.getLongValue(MediaStore.Files.FileColumns.SIZE)
                    if (size == 0L) {
                        return@queryCursor
                    }

                    val path = cursor.getStringValue(MediaStore.Files.FileColumns.DATA)
                    val lastModified =
                        cursor.getLongValue(MediaStore.Files.FileColumns.DATE_MODIFIED) * 1000

                    val mimetype = fullMimetype.substringBefore("/")

                    when (type) {
                        Constant.TYPE_PICTURE -> {
                            if (mimetype == "image") {
                                val parentFolderPath = File(path).parentFile?.path
                                val parentFolder = File(path).parentFile?.name ?: "No_name"
                                if (parentFolder.startsWith(".") || parentFolderPath?.contains(Constant.PRIVACY_FOLDER_NAME) == true) {
                                    return@queryCursor
                                }
                                if (!folders.any { it.first == parentFolder }) {
                                    folders.add(
                                        Pair(
                                            parentFolder, GroupItem(
                                                parentFolder,
                                                Constant.TYPE_PICTURE,
                                                mutableListOf(),
                                                parentFolderPath.toString()
                                            )
                                        )
                                    )
                                } else {
                                    if (!folders.any { it.second?.folderPath == parentFolderPath }) {
                                        folders.add(
                                            Pair(
                                                parentFolder, GroupItem(
                                                    parentFolder,
                                                    Constant.TYPE_PICTURE,
                                                    mutableListOf(),
                                                    parentFolderPath.toString()
                                                )
                                            )
                                        )
                                    }
                                }

                                if (path.isNotEmpty()) {

                                    folders.find { it.second?.folderPath == parentFolderPath }?.second?.dataList?.add(
                                        path
                                    )
                                }
                            }
                        }
                        Constant.TYPE_VIDEOS -> {
                            if (mimetype == "video") {

                                val parentFolderPath = File(path).parentFile?.path
                                val parentFolder = File(path).parentFile?.name ?: "No_name"
                                if (parentFolder.startsWith(".") || parentFolderPath?.contains(Constant.PRIVACY_FOLDER_NAME) == true) {
                                    return@queryCursor
                                }
                                if (!folders.any { it.first == parentFolder }) {
                                    folders.add(
                                        Pair(
                                            parentFolder, GroupItem(
                                                parentFolder,
                                                Constant.TYPE_VIDEOS,
                                                mutableListOf(),
                                                File(path).parentFile?.path ?: ""
                                            )
                                        )
                                    )
                                } else
                                    if (!folders.any { it.second?.folderPath == parentFolderPath }) {
                                        folders.add(
                                            Pair(
                                                parentFolder, GroupItem(
                                                    parentFolder,
                                                    Constant.TYPE_VIDEOS,
                                                    mutableListOf(),
                                                    File(path).parentFile?.path ?: ""
                                                )
                                            )
                                        )
                                    }

                                if (path.isNotEmpty()) {
                                    folders.find { it.second?.folderPath == parentFolderPath }?.second?.dataList?.add(
                                        path
                                    )
                                }
                            }
                        }
                        TYPE_AUDIOS -> {
                            if (mimetype == "audio" || extraAudioMimeTypes.contains(fullMimetype)) {

                                val parentFolderPath = File(path).parentFile?.path
                                val parentFolder = File(path).parentFile?.name ?: "No_name"
                                if (parentFolder.startsWith(".")|| parentFolderPath?.contains(Constant.PRIVACY_FOLDER_NAME) == true) {
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
                                    if (!folders.any { it.second?.folderPath == parentFolderPath }) {
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
                                    folders.find { it.second?.folderPath == parentFolderPath }?.second?.dataList?.add(
                                        path
                                    )
                                }
                            }
                        }
                        Constant.TYPE_FILE -> {
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
                                                Constant.TYPE_FILE,
                                                mutableListOf(),
                                                File(path).parentFile?.path ?: "",
                                                mutableSetOf()
                                            )
                                        )
                                    )
                                }
                                if (path.isNotEmpty() && !path.contains(PRIVACY_FOLDER_NAME)) {
                                    if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PDF)) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_PDF)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_CSV)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_CSV)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_PPT)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_PPT)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_PPT)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_PPTX)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_TEXT)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_TEXT)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_WORD)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_WORD)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_EXCEL)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_EXCEL)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_WORDX)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_WORD)
                                    } else if (name.lowercase(Locale.ROOT)
                                            .endsWith(Constant.TYPE_ZIP)
                                    ) {
                                        folders[0].second?.dataTypeList?.add(Constant.TYPE_ZIP)
                                    }
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

    fun getAllImage(context: Context): List<ListItem> {
        try {
            val listImageChild = mutableListOf<ListItem>()
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
                val modified = cursor.getLongValue(MediaStore.Images.Media.SIZE)
                val name = cursor.getStringValue(MediaStore.Images.Media.DISPLAY_NAME)

                if (childPath.isNotBlank()) {
                    listImageChild.add(
                        ListItem(
                            id,
                            childPath,
                            File(childPath).parentFile?.path.toString(),
                            name,
                            false,
                            0,
                            size,
                            modified,
                            Constant.TYPE_PICTURE
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

    fun getChildImageFromPath(context: Context, path: String): List<ListItem> {
        try {
            val listImageChild = mutableListOf<ListItem>()
            val uri = MediaStore.Images.Media.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media._ID
            )
            Log.d("TAG", "getChildImageFromPath: " + path)
            val selection =
                MediaStore.Images.Media.DATA + " LIKE ? AND " + MediaStore.Images.Media.DATA + " NOT LIKE ?"
            val selectionArgs = arrayOf("$path/%", "$path/%/%")

            context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->

                val id = cursor.getLongValue(MediaStore.Images.Media._ID)
                val childPath = cursor.getStringValue(MediaStore.Images.Media.DATA)
                val size = cursor.getLongValue(MediaStore.Images.Media.SIZE)
                val modified = cursor.getLongValue(MediaStore.Images.Media.SIZE)
                val name = cursor.getStringValue(MediaStore.Images.Media.DISPLAY_NAME)

                if (childPath.isNotBlank() && childPath != path) {
                    listImageChild.add(
                        ListItem(
                            id,
                            childPath,
                            path,
                            name,
                            false,
                            0,
                            size,
                            modified,
                            Constant.TYPE_PICTURE
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

    fun getChildVideoFromPath(context: Context, path: String): List<ListItem> {

        val listVideoChild = mutableListOf<ListItem>()
        try {
            val uri = MediaStore.Video.Media.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media._ID
            )
            val selection = MediaStore.Video.Media.DATA + " LIKE ?"
            val selectionArgs = arrayOf("$path/%")
            context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->
                val id = cursor.getLongValue(MediaStore.Video.Media._ID)
                val childPath = cursor.getStringValue(MediaStore.Video.Media.DATA)
                val size = cursor.getLongValue(MediaStore.Video.Media.SIZE)
                val modified = cursor.getLongValue(MediaStore.Video.Media.DATE_MODIFIED)
                val name = cursor.getStringValue(MediaStore.Video.Media.DISPLAY_NAME)

                if (childPath.isNotBlank()) {
                    val duration = getDuration(context, childPath)
                    listVideoChild.add(
                        ListItem(
                            id,
                            childPath,
                            path,
                            name,
                            false,
                            0,
                            size,
                            modified,
                            Constant.TYPE_VIDEOS
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
    ): List<ListItem> {
        val listAudioChild = mutableListOf<ListItem>()

        try {

            val uri = MediaStore.Audio.Media.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media._ID
            )
            val selection = MediaStore.Audio.Media.DATA + " LIKE ?"

            val selectionArgs = arrayOf("$path/%")
            context.queryCursor(uri, projection, selection, selectionArgs) { cursor ->
                val id = cursor.getLongValue(MediaStore.Audio.Media._ID)
                val childPath = cursor.getStringValue(MediaStore.Audio.Media.DATA)
                val size = cursor.getLongValue(MediaStore.Audio.Media.SIZE)
                val modified = cursor.getLongValue(MediaStore.Audio.Media.DATE_MODIFIED)
                val name = cursor.getStringValue(MediaStore.Audio.Media.DISPLAY_NAME)

                if (childPath.isNotBlank()) {
                    val thumb = getThumbnail(childPath)
                    listAudioChild.add(
                        ListItem(
                            id, childPath, childPath, name, false, 0, size, modified, TYPE_AUDIOS
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listAudioChild
    }

    fun getChildFileFromPath(context: Context, path: String, type: String): List<ListItem> {
        val listFileChild = mutableListOf<ListItem>()
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
                val modified = cursor.getLongValue(MediaStore.Files.FileColumns.DATE_MODIFIED)
                val name = cursor.getStringValue(MediaStore.Files.FileColumns.DISPLAY_NAME)

                val mimetype = fullMimetype.substringBefore("/")

                if (mimetype == "text" || extraDocumentMimeTypes.contains(fullMimetype) || archiveMimeTypes.contains(
                        fullMimetype
                    )
                ) {
                    if (path.isNotEmpty() && !childPath.contains(PRIVACY_FOLDER_NAME)) {
                        Log.d("TAG", "getChildFileFromPath: " + childPath)
                        when (type) {
                            Constant.TYPE_PDF -> {
                                if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PDF)) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",
                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_PDF
                                        )
                                    )

                                }
                            }
                            Constant.TYPE_PPT, Constant.TYPE_PPTX -> {
                                if (name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_PPTX) || (name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_PPT))
                                ) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",
                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_PPT
                                        )
                                    )
                                }
                            }
                            Constant.TYPE_WORDX, Constant.TYPE_WORD -> {
                                if (name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_WORD) || name.lowercase(Locale.ROOT)
                                        .endsWith(Constant.TYPE_WORDX)
                                ) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",

                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_WORD
                                        )
                                    )
                                }
                            }
                            Constant.TYPE_EXCEL -> {
                                if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_EXCEL)) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",

                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_EXCEL
                                        )
                                    )
                                }
                            }
                            Constant.TYPE_CSV -> {
                                if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_CSV)) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",

                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_CSV
                                        )
                                    )
                                }
                            }
                            Constant.TYPE_TEXT -> {
                                if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_TEXT)) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",

                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_TEXT
                                        )
                                    )
                                }
                            }
                            Constant.TYPE_ZIP -> {
                                if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_ZIP)) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",

                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_ZIP
                                        )
                                    )
                                }
                            }
                            else -> {
                                if (name.lowercase(Locale.ROOT).endsWith(Constant.TYPE_PDF)) {
                                    listFileChild.add(
                                        ListItem(
                                            id,
                                            childPath,
                                            File(childPath).parentFile?.path ?: "",

                                            name,
                                            false,
                                            0,
                                            size,
                                            modified,
                                            Constant.TYPE_FILE,
                                            Constant.TYPE_OTHER
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


    private fun getDuration(context: Context, path: String): Int {
        val mp = MediaPlayer.create(context, Uri.parse(path))
        return mp.duration
    }
}