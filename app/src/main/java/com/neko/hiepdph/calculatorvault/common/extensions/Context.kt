package com.neko.hiepdph.calculatorvault.common.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Point
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.BaseColumns
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionN
import com.neko.hiepdph.calculatorvault.config.MainConfig
import java.io.File


val Context.config: MainConfig get() = MainConfig.newInstance(applicationContext)

fun Context.shareFile(listPath: List<String>) {
    try {
        val uris = ArrayList<Uri>()
        listPath.forEach {
            uris.add(
                FileProvider.getUriForFile(this, "$packageName.provider", File(it))
            )
        }


        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        shareIntent.type = "*/*"
        shareIntent.putParcelableArrayListExtra(
            Intent.EXTRA_STREAM, uris
        )
        startActivity(Intent.createChooser(shareIntent, "Share"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.getDataColumn(
    uri: Uri, selection: String? = null, selectionArgs: Array<String>? = null
): String? {
    try {
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                val data = cursor.getStringValue(MediaStore.Files.FileColumns.DATA)
                if (data != "null") {
                    return data
                }
            }
        }
    } catch (e: Exception) {
    }
    return null
}

fun Context.queryCursor(
    uri: Uri,
    projection: Array<String>,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    showErrors: Boolean = false,
    callback: (cursor: Cursor) -> Unit
) {
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        }
    } catch (e: Exception) {
        if (showErrors) {
            showErrorToast(e)
        }
    }
}

fun Context.toast(id: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), length)
}

val isOnMainThread = Looper.myLooper() == Looper.getMainLooper()

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        if (isOnMainThread) {
            doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
    }
}


private fun doToast(context: Context, message: String, length: Int) {
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            Toast.makeText(context, message, length).show()
        }
    } else {
        Toast.makeText(context, message, length).show()
    }
}

fun Context.showErrorToast(msg: String, length: Int = Toast.LENGTH_LONG) {
    toast(String.format(getString(R.string.app_name), msg), length)
}

fun Context.showErrorToast(exception: Exception, length: Int = Toast.LENGTH_LONG) {
    showErrorToast(exception.toString(), length)
}

fun Context.getFileUri(path: String) = when {
    path.isImageSlow() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    path.isVideoSlow() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    path.isAudioSlow() -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    else -> MediaStore.Files.getContentUri("external")
}

fun Context.getDuration(path: String): Int? {
    val projection = arrayOf(
        MediaStore.MediaColumns.DURATION
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(path)

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return Math.round(cursor.getIntValue(MediaStore.MediaColumns.DURATION) / 1000.toDouble())
                    .toInt()
            }
        }
    } catch (ignored: Exception) {
    }

    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        Math.round(
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                .toInt() / 1000f
        )
    } catch (ignored: Exception) {
        null
    }
}

fun Context.getArtist(path: String): String? {
    val projection = arrayOf(
        MediaStore.Audio.Media.ARTIST
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(path)

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(MediaStore.Audio.Media.ARTIST)
            }
        }
    } catch (ignored: Exception) {
    }

    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    } catch (ignored: Exception) {
        null
    }
}

fun Context.getAlbum(path: String): String? {
    val projection = arrayOf(
        MediaStore.Audio.Media.ALBUM
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(path)

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(MediaStore.Audio.Media.ALBUM)
            }
        }
    } catch (ignored: Exception) {
    }

    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    } catch (ignored: Exception) {
        null
    }
}

fun Context.getTitle(path: String): String? {
    val projection = arrayOf(
        MediaStore.MediaColumns.TITLE
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(path)

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getStringValue(MediaStore.MediaColumns.TITLE)
            }
        }
    } catch (ignored: Exception) {
    }

    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
    } catch (ignored: Exception) {
        null
    }
}

fun Context.getImageResolution(path: String): Point? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val width = options.outWidth
    val height = options.outHeight
    return if (width > 0 && height > 0) {
        Point(options.outWidth, options.outHeight)
    } else {
        null
    }
}

fun Context.getLastModified(path:String): Long {
    return when {

        buildMinVersionN() && path.startsWith("content://") -> this.getMediaStoreLastModified(
            path
        )
        else -> File(path).lastModified()
    }
}

fun Context.getVideoResolution(path: String): Point? {
    var point = try {
        val retriever = MediaMetadataRetriever()
//        if (isRestrictedSAFOnlyRoot(path)) {
//            retriever.setDataSource(this, getAndroidSAFUri(path))
//        } else {
//            retriever.setDataSource(path)
//        }

        retriever.setDataSource(path)

        val width =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
        val height =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
        Point(width, height)
    } catch (ignored: Exception) {
        null
    }

    if (point == null && path.startsWith("content://", true)) {
        try {
            val fd = contentResolver.openFileDescriptor(Uri.parse(path), "r")?.fileDescriptor
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(fd)
            val width =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
            val height =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                    .toInt()
            point = Point(width, height)
        } catch (ignored: Exception) {
        }
    }

    return point
}


fun Context.getResolution(path: String): Point? {
    return if (path.isImageFast() || path.isImageSlow()) {
        getImageResolution(path)
    } else if (path.isVideoFast() || path.isVideoSlow()) {
        getVideoResolution(path)
    } else {
        null
    }
}

fun Context.getMediaStoreLastModified(path: String): Long {
    val projection = arrayOf(
        MediaStore.MediaColumns.DATE_MODIFIED
    )

    val uri = getFileUri(path)
    val selection = "${BaseColumns._ID} = ?"
    val selectionArgs = arrayOf(path.substringAfterLast("/"))

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getLongValue(MediaStore.MediaColumns.DATE_MODIFIED) * 1000
            }
        }
    } catch (ignored: Exception) {
    }
    return 0
}



