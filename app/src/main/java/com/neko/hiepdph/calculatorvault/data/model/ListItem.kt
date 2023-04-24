package com.neko.hiepdph.calculatorvault.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class ListItem(
    val id: Long,
    var mPath: String,
    val mOriginalPath: String,
    val mName: String = "",
    var mIsDirectory: Boolean = false,
    var mChildren: Int = 0,
    var mSize: Long = 0L,
    var mModified: Long = 0L,
    var type: String? = "",
    var realType: String? = "",
) : FileDirItem(mPath, mOriginalPath, mName, mIsDirectory, mChildren, mSize, mModified),
    Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other is ListItem) {
            this.mPath == other.mPath
        } else {
            false
        }
    }
}