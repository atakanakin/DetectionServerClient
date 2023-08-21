package com.atakan.mainclient.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class ServiceViewModel @Inject constructor(count: Int) : ViewModel() {

    private val _isServiceConnected = MutableLiveData<Int>(count)

    val isServiceConnected: LiveData<Int> = _isServiceConnected

    fun updateCount() {
        _isServiceConnected.postValue(_isServiceConnected.value!!.plus(1))
    }
}


