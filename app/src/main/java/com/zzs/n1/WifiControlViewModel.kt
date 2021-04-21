package com.zzs.n1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class WifiControlViewModel:ViewModel() {

    val state = MutableLiveData<String>().apply {
        value = "未连接"
    }

    fun onMute(){

    }

    fun onScreenShot(){

    }

    fun onVDown(){

    }

    fun onVUp(){

    }

    fun onBack(){

    }
}