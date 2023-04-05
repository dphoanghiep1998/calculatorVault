package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.IMoveFile
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListItemViewModel @Inject constructor() :ViewModel() {

    private val _listItemList = SelfCleaningLiveData<MutableList<ListItem>>()
    val listItemList: LiveData<MutableList<ListItem>> get() = _listItemList
    fun setListItemData(list: List<ListItem>) {
        _listItemList.postValue(list.toMutableList())
    }

    fun getItemListFromFolder(context: Context,folderPath:String,type:String,fileType:String?=null){
        viewModelScope.launch(Dispatchers.IO) {
            when(type){
                Constant.TYPE_PICTURE -> {
                    setListItemData(MediaStoreUtils.getChildImageFromPath(context,folderPath))
                }
                Constant.TYPE_AUDIOS -> {
                    setListItemData(MediaStoreUtils.getChildAudioFromPath(context,folderPath))
                }
                Constant.TYPE_VIDEOS -> {
                    setListItemData(MediaStoreUtils.getChildVideoFromPath(context,folderPath))
                }
                Constant.TYPE_FILE -> {
                    setListItemData(MediaStoreUtils.getChildFileFromPath(context,folderPath,fileType!!))
                }
            }

        }
    }

    fun copyMoveFile(listPath:List<String>,destinationPath:String,callback:IMoveFile){
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.copyMoveTo(listPath,destinationPath,false, callback)
        }
    }
}