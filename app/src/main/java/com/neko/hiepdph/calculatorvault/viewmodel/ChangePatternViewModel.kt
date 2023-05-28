package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePatternViewModel @Inject constructor() : ViewModel() {
    private var _currentState = MutableLiveData<Int?>()
    val currentState: LiveData<Int?> get() = _currentState
    fun setState(state: Int?) {
        _currentState.postValue(state)
    }

    private var _state1Pattern = MutableLiveData<MutableList<Int>?>()
    val state1Pattern: LiveData<MutableList<Int>?> get() = _state1Pattern
    fun setState1Pattern(pw: MutableList<Int>?) {
        _state1Pattern.postValue(pw)
    }

    private var _state2Pattern = MutableLiveData<MutableList<Int>?>()
    val state2Pattern: LiveData<MutableList<Int>?> get() = _state2Pattern
    fun setState2Pattern(pw: MutableList<Int>?) {
        _state2Pattern.postValue(pw)
    }

    private var _state3Pattern = MutableLiveData<MutableList<Int>?>()
    val state3Pattern: LiveData<MutableList<Int>?> get() = _state3Pattern
    fun setState3Pattern(pw: MutableList<Int>?) {
        _state3Pattern.postValue(pw)
    }

    init {
        setState(0)
        setState1Pattern(arrayListOf())
        setState2Pattern(arrayListOf())
        setState3Pattern(arrayListOf())
    }
}