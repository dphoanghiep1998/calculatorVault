package com.neko.hiepdph.calculatorvault.data.model

class VaultFileDirItem(
    val mPath: String,
    val mName: String = "",
    val type: String = "",
    var mIsDirectory: Boolean = true,
    var mChildren: Int = 0,
    var mSize: Long = 0L,
    var mModified: Long = 0L,

    ) : FileDirItem(mPath,mPath, mName, mIsDirectory, mChildren, mSize, mModified) {}