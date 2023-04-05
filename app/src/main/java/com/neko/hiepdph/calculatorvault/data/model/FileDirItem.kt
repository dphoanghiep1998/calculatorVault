package com.neko.hiepdph.calculatorvault.data.model

import android.content.Context
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionN
import java.io.File

open class FileDirItem(
    val path: String,
    val name: String = "",
    var isDirectory: Boolean = false,
    var children: Int = 0,
    var size: Long = 0L,
    var modified: Long = 0L,
    var mediaStoreId: Long = 0L
) {

    override fun toString() =
        "FileDirItem(path=$path, name=$name, isDirectory=$isDirectory, children=$children, size=$size, modified=$modified, mediaStoreId=$mediaStoreId)"

    fun getExtension() = if (isDirectory) name else path.substringAfterLast(".", "")

    fun getParentPath() = path.getParentPath()

    fun getDuration(context: Context) = context.getDuration(path)?.getFormattedDuration()

    fun getFileDurationSeconds(context: Context) = context.getDuration(path)

    fun getArtist(context: Context) = context.getArtist(path)

    fun getAlbum(context: Context) = context.getAlbum(path)

    fun getTitle(context: Context) = context.getTitle(path)

    fun getResolution(context: Context) = context.getResolution(path)

    fun getVideoResolution(context: Context) = context.getVideoResolution(path)

    fun getImageResolution(context: Context) = context.getImageResolution(path)

    fun getThumb() = MediaStoreUtils.getThumbnail(path)


    fun getLastModified(context: Context): Long {
        return when {
//            context.isRestrictedSAFOnlyRoot(path) -> context.getAndroidSAFLastModified(path)
//            context.isPathOnOTG(path) -> context.getFastDocumentFile(path)?.lastModified() ?: 0L
            buildMinVersionN() && path.startsWith("content://") -> context.getMediaStoreLastModified(
                path
            )
            else -> File(path).lastModified()
        }
    }


}