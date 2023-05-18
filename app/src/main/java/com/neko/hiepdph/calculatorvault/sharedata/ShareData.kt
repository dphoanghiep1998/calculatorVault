package com.neko.hiepdph.calculatorvault.sharedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem

class ShareData() {
    companion object {
        private var shareDataInstance: ShareData? = null
        fun getInstance(): ShareData {
            if (shareDataInstance == null) {
                shareDataInstance = ShareData()
            }
            return shareDataInstance!!
        }
    }

    private val _listItemImage = MutableLiveData<MutableList<FileVaultItem>>()
    val listItemImage: LiveData<MutableList<FileVaultItem>> get() = _listItemImage
    fun setListItemImage(list: MutableList<FileVaultItem>) {
        _listItemImage.postValue(list)
    }

    private val _listItemVideo = MutableLiveData<MutableList<FileVaultItem>>()
    val listItemVideo: LiveData<MutableList<FileVaultItem>> get() = _listItemVideo
    fun setListItemVideo(list: MutableList<FileVaultItem>) {
        _listItemVideo.postValue(list)
    }

    private val _listItemAudio = MutableLiveData<MutableList<FileVaultItem>>()
    val listItemAudio: LiveData<MutableList<FileVaultItem>> get() = _listItemAudio
    fun setListItemAudio(list: MutableList<FileVaultItem>) {
        _listItemAudio.postValue(list)
    }

    private val _clearBrowser = MutableLiveData(false)
    val clearBrowser: LiveData<Boolean> get() = _clearBrowser
    fun setBrowserClear(status: Boolean) {
        _clearBrowser.postValue(status)
    }


}