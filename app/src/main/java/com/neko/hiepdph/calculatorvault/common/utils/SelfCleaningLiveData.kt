package com.neko.hiepdph.calculatorvault.common.utils

import androidx.lifecycle.MutableLiveData

class SelfCleaningLiveData<T> : MutableLiveData<T>() {
    override fun onInactive() {
        super.onInactive()
        value = null
    }
}