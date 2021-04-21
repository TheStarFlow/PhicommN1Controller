package com.zzs.n1

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zzs.n1.bean.Action
import com.zzs.n1.bean.NetBean
import com.zzs.n1.ui.launchIO
import com.zzs.n1.utils.SpHelper
import com.zzs.n1.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


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

    var ip = ""

    val wifiIpEnable = MutableLiveData<Boolean>()

    fun onMute() {
        postKey(164)
    }

    fun onScreenShot() {
        showToast("暂未完成")
    }

    fun onVDown() {
        postKey(25)

    }

    fun onVUp() {
        postKey(24)

    }

    fun onBack() {
        postKey(4)

    }

    fun onLeft() {
        postKey(21)

    }

    fun onRight() {
        postKey(22)

    }

    fun onUp() {
        postKey(19)

    }

    fun onDown() {
        postKey(20)

    }

    fun onCenter() {
        postKey(23)

    }


    //  http://192.168.123.116:8080/v1/
    private fun getUrl(ip: String, action: String): String {
        return "http://$ip:8080/v1/$action"
    }

    fun log(msg: String) {
        Log.d(TAG, msg)
    }

    fun updateIp(ip: String? = SpHelper.getString(LAST_IP)) {
        wifiIpEnable.value = false
        this.ip = ip?:""
        SpHelper.put(LAST_IP,ip?:"")
        getSth("ping")

    }

    fun postKey(command: Int = -1, append: String = KEY_EVENT) {
       launchIO {
           val url = getUrl(ip, append)
           val json = if (append == KEY_EVENT) NetBean(command).toString() else Action().toString()
           log(url)
           log(json)
           val conn = URL(url).openConnection() as HttpURLConnection
           conn.doInput = true;
           conn.doOutput = true;
           conn.useCaches = false;
           conn.connectTimeout = 3000;//设置连接超时
           conn.readTimeout = 3000//设置读取超时
           conn.requestMethod = "POST";
           conn.setRequestProperty("Content-Type", "application/json")
           conn.connect()
           val out = OutputStreamWriter(conn.outputStream, "UTF-8") // utf-8编码
           out.append(json)
           out.flush()
           out.close()
           if (conn.responseCode == HttpURLConnection.HTTP_OK) {
               val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
               var line: String?
               var res = ""
               while (reader.readLine().also { line = it } != null) {
                   res += line
               }
               log(res)
               reader.close()
           } else {
               wifiIpEnable.postValue(false)
               withContext(Dispatchers.Main){
                   showToast("网络异常")
               }
           }
       }

    }

    fun getSth(event: String) {
        launchIO {

            val url = getUrl(ip, event)
            log(url)
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.doInput = true;
            conn.doOutput = true;
            conn.useCaches = false;
            conn.connectTimeout = 3000;//设置连接超时
            conn.readTimeout = 3000//设置读取超时
            conn.requestMethod = "GET";
            conn.connect()
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                var line: String?
                var res = ""
                while (reader.readLine().also { line = it } != null) {
                    res += line
                }
                log(res)
                reader.close()
                wifiIpEnable.postValue(true)
                ipName.postValue(ip)
            } else {
                wifiIpEnable.postValue(false)
                withContext(Dispatchers.Main){
                    showToast("网络异常")
                }
            }
        }
    }
}