package com.atakan.detectionclient.presentation.view_model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor() : ViewModel() {

    private val _imageLive = MutableLiveData<Bitmap>(null)
    val imageLive: LiveData<Bitmap> = _imageLive

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    var action: String = ""

    fun refreshData(resource: Bitmap){
        _imageLive.value = resource
        _isEmpty.value = true
        action = "face"
    }

    fun clean(){
        _imageLive.value = null
        _isEmpty.value = false
        action = ""
    }
}