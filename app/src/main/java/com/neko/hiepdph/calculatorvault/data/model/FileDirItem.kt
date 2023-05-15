package com.neko.hiepdph.calculatorvault.data.model

import android.content.Context
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionN
import java.io.File

open class FileDirItem(
    var path: String,
    val originalPath: String,
    val name: String = "",
    var size: Long = 0L,
    var modified: Long = 0L,
    var mediaStoreId: Long = 0L
) {

    override fun toString() =
        "FileDirItem(path=$path,originalPath=$originalPath, name=$name,  size=$size, modified=$modified, mediaStoreId=$mediaStoreId)"

    fun getExtension() = path.substringAfterLast(".", "")

    fun getParentPath() = path.getParentPath()

    fun getDuration(context: Context) = context.getDuration(path)?.getFormattedDuration()

    fun getFileDurationSeconds(context: Context) = context.getDuration(path)

    fun getArtist(context: Context) = context.getArtist(path)

    fun getAlbum(context: Context) = context.getAlbum(path)

    fun getTitle(context: Context) = context.getTitle(path)

    fun getResolution(context: Context) = context.getResolution(path)

    fun getVideoResolution(context: Context) = context.getVideoResolution(path)

    fun getImageResolution(context: Context) = context.getImageResolution(path)

//    fun getThumb() = MediaStoreUtils.getThumbnail(path)


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