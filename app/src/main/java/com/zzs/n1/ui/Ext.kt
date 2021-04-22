package com.zzs.n1.ui

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zzs.n1.WifiControlViewModel
import com.zzs.n1.bean.Action
import com.zzs.n1.bean.NetBean
import com.zzs.n1.utils.showToast
import com.zzs.n1.widget.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
@author  zzs
@Date 2021/4/21
@describe
 */

private var dialog: LoadingDialog? = null


fun Fragment.showLoading(msg: String = "正在配对...") {
    if (dialog == null) {
        dialog = LoadingDialog(requireContext())
    }
    if (dialog?.isShowing == true) {
        dialog?.setMsg(msg)
    } else {
        dialog?.setMsg(msg)
        dialog?.show()
    }
}

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Activity.showToast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}


fun Fragment.hideLoading() {
    dialog?.dismiss()
}

fun ViewModel.launchIO(bl: suspend () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        kotlin.runCatching {

            bl.invoke()
        }.onFailure {
            withContext(Dispatchers.Main) {
                showToast("网络异常 ${it.message}")
            }
        }

    }
}

fun ViewModel.httpPost(url: String, json: String, onSucceed: (String) -> Unit) {
    launchIO {
        log(url)
        log(json)
        val conn = initConnection(url, "POST")
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
            withContext(Dispatchers.Main) {
                onSucceed.invoke(res)
            }
        } else {
            withContext(Dispatchers.Main) {
                showToast("网络异常")
            }
        }
        conn.disconnect()
    }
}


fun ViewModel.httpGet(url: String, onSucceed: (String) -> Unit) {
    log(url)
    launchIO {
        val conn = initConnection(url, "GET")
        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            var line: String?
            var res = ""
            while (reader.readLine().also { line = it } != null) {
                res += line
            }
            log(res)
            reader.close()
            withContext(Dispatchers.Main) {
                onSucceed.invoke(res)
            }
        } else {
            withContext(Dispatchers.Main) {
                showToast("网络异常")
            }
        }
        conn.disconnect()
    }
}


private fun initConnection(url: String, method: String): HttpURLConnection {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.doInput = true;
    conn.doOutput = true;
    conn.useCaches = false;
    conn.connectTimeout = 3000;//设置连接超时
    conn.readTimeout = 3000//设置读取超时
    conn.requestMethod = method
    conn.setRequestProperty("Content-Type", "application/json")
    conn.connect()
    return conn
}

fun getUrl(ip: String, action: String): String {
    return "http://$ip:8080/v1/$action"
}

fun log(msg: String) {
    Log.d(WifiControlViewModel.TAG, msg)
}