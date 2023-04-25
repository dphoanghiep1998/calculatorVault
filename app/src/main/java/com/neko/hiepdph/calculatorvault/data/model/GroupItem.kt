package com.neko.hiepdph.calculatorvault.data.model


import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupItem(
    var name: String,
    var type: String,
    var dataList: MutableList<String>,
    var folderPath: String,
    var dataTypeList: MutableSet<String>? = null //document only
) : Parcelable {
    val itemCount: Int
        get() = dataList.size
}
