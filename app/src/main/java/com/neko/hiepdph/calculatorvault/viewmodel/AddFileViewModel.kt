package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.model.GroupItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFileViewModel @Inject constructor():ViewModel() {

    private val _listItemListGroupFile = MutableLiveData<MutableList<GroupItem>?>()
    val listItemListGroupFile: LiveData<MutableList<GroupItem>?> get() = _listItemListGroupFile
     fun setListItemPersistentData(list: MutableList<GroupItem>?) {
        _listItemListGroupFile.postValue(list)
    }


    fun getListGroupItem(context: Context,type:String){
        viewModelScope.launch(Dispatchers.IO) {
            setListItemPersistentData(MediaStoreUtils.getListGroupItem(context,type).toMutableList())
        }
    }
}