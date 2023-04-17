package com.neko.hiepdph.calculatorvault.sharedata

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.model.ListItem

class ShareData() {
    companion object {
        private var shareDataInstance: ShareData? = null
        fun getInstance():ShareData {
            if(shareDataInstance == null){
                shareDataInstance = ShareData()
            }
            return shareDataInstance!!
        }
    }

    private val _listItemImage = MutableLiveData<MutableList<ListItem>>()
    val listItemImage: LiveData<MutableList<ListItem>> get() = _listItemImage
    fun setListItemImage(list: MutableList<ListItem>) {
        _listItemImage.postValue(list)
    }


}