package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import org.opencv.core.Mat

class ScannerViewModel : ViewModel() {
    private val mFrame = MutableLiveData<Mat>()

    fun setFrame(frame: Mat) {
        mFrame.postValue(frame)
    }

    fun liveData(): LiveData<Mat> {
        return mFrame
    }
}