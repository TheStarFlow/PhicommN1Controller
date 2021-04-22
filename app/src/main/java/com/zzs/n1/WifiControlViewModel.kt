package com.zzs.n1

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zzs.n1.bean.Action
import com.zzs.n1.bean.NetBean
import com.zzs.n1.ui.getUrl
import com.zzs.n1.ui.httpGet
import com.zzs.n1.ui.log
import com.zzs.n1.ui.httpPost
import com.zzs.n1.utils.SpHelper
import com.zzs.n1.utils.showToast


/**
@author  zzs
@Date 2021/4/20
@describe
 */
class WifiControlViewModel : ViewModel() {

    val ipName = MutableLiveData<String>().apply {
        value = "未连接"
    }

    companion object {
        const val TAG = "WifiControlViewModel"
        const val KEY_EVENT = "keyevent"
        const val LAST_IP = "LAST_IP"
    }

    val vibrator: Vibrator = App.application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    var ip = ""

    val wifiIpEnable = MutableLiveData<Boolean>()


    fun onClick(code:Int){
        if (vibrator.hasVibrator()){
            vibrator.vibrate(VibrationEffect.createOneShot(50,255))
        }
        postKey(code)
    }

    fun onScreenShot() {
        showToast("暂未完成")
    }
    //  http://192.168.123.116:8080/v1/

    fun updateIp(ip: String? = SpHelper.getString(LAST_IP)) {
        wifiIpEnable.value = false
        this.ip = ip ?: ""
        getData("ping") {
            wifiIpEnable.postValue(true)
            ipName.postValue(ip)
            SpHelper.put(LAST_IP, ip ?: "")
        }

    }

    private fun postKey(
        command: Int = -1,
        append: String = KEY_EVENT,
        onSucceed: (String) -> Unit = {}
    ) {
        if (TextUtils.isEmpty(ip)) return
        val url = getUrl(ip, append)
        val json = if (append == KEY_EVENT) NetBean(command).toString() else Action().toString()
        httpPost(url, json, onSucceed)
    }

    private fun getData(event: String, onSucceed: (String) -> Unit) {
        if (TextUtils.isEmpty(ip)) return
        val url = getUrl(ip, event)
        httpGet(url, onSucceed)
    }
}