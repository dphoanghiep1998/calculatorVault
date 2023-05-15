package com.neko.hiepdph.calculatorvault.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class ListItem(
    val id: Long,
    var mPath: String,
    val mOriginalPath: String,
    val mName: String = "",
    var mSize: Long = 0L,
    var mModified: Long = 0L,
    var mTimeLock: Long = 0L,
    var mImageSize: String = "",
    var mediaDuration: Int = 0,
    var type: String? = "",
    var realType: String? = "",
    var thumb: Bitmap? = null
) : FileDirItem(mPath, mOriginalPath, mName, mSize, mModified),
    Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other is ListItem) {
            this.mPath == other.mPath
        } else {
            false
        }
    }
}