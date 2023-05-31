package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePinViewModel @Inject constructor() : ViewModel() {
    private var _currentState = MutableLiveData<Int?>()
    val currentState: LiveData<Int?> get() = _currentState
    fun setState(state: Int?) {
        _currentState.value = state
    }

    private var _state1Password = MutableLiveData<String?>()
    val state1Password: LiveData<String?> get() = _state1Password
    fun setState1Password(pw: String?) {
        _state1Password.value = pw
    }

    private var _state2Password = MutableLiveData<String?>()
    val state2Password: LiveData<String?> get() = _state2Password
    fun setState2Password(pw: String?) {
        _state2Password.value = pw

    }

    private var _state3Password = MutableLiveData<String?>()
    val state3Password: LiveData<String?> get() = _state3Password
    fun setState3Password(pw: String?) {
        _state3Password.value = pw
    }

    init {
        setState(0)
        setState1Password("")
        setState2Password("")
        setState3Password("")
    }
}