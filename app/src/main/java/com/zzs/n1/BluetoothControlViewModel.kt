package com.zzs.n1

import android.bluetooth.*
import android.content.Context
import android.os.Parcel
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.zzs.n1.WifiControlViewModel.Companion.LAST_IP
import com.zzs.n1.bean.NetBean
import com.zzs.n1.handle.HidDeviceHandler
import com.zzs.n1.ui.*
import com.zzs.n1.utils.SpHelper
import com.zzs.n1.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class BluetoothControlViewModel : ViewModel(), BluetoothProfile.ServiceListener, LifecycleObserver {

    companion object {
        const val TAG = "BluetoothControlViewModel"
        const val LAST_CONTROL = "LAST_CONTROL_DEVICE"

    }

    private val blAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mAppIsRegister = false
    private var mHidDevice: BluetoothHidDevice? = null
    val mState = MutableLiveData<String>().apply {
        value = "未连接"
    }
    val hidEnable = MutableLiveData<Boolean>().apply {
        value = false
    }
    val reportRegister = MutableLiveData<Boolean>()

    // 0 正在连接 1 连接完成 2 连接失败
    val mConnectState = MutableLiveData<Int>()

    var mConnectDevice: BluetoothDevice? = null

    private var mHidHandler: HidDeviceHandler? = null

    val device = MutableLiveData<BluetoothDevice>()

    val vibrator:Vibrator = App.application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


    fun power(){
        //关机
        if (vibrator.hasVibrator()){
            vibrator.vibrate(VibrationEffect.createOneShot(50,255))
        }
        val ip = SpHelper.getString(LAST_IP)
        if (!TextUtils.isEmpty(ip)){
            val url = getUrl(ip!!, WifiControlViewModel.KEY_EVENT)
            val json = NetBean(26).toString()
            httpPost(url,json){ log(it)}
        }

        //开机
        val lastDeviceAddress = SpHelper.getString(LAST_CONTROL)
        if (TextUtils.isEmpty(lastDeviceAddress))return
        val bl = blAdapter.getRemoteDevice(lastDeviceAddress)
        val gatt = bl?.connectGatt(App.application,false,object : BluetoothGattCallback() {})
        gatt?.close()
    }

    fun onClick(code:Int){
        if (vibrator.hasVibrator()){
            vibrator.vibrate(VibrationEffect.createOneShot(50,255))
        }
        keyAction(code.toByte())
    }

    private fun keyAction(code: Byte) {
        viewModelScope.launch {
            mHidHandler?.keyDown(code)
            delay(5)
            mHidHandler?.keyUp(code)
        }
    }

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
        Log.d(TAG, "连接设备回调 $profile  ${proxy.toString()}")
        if (profile != BluetoothProfile.HID_DEVICE) return
        proxy ?: return
        hidEnable.postValue(true)
        mHidDevice = proxy as BluetoothHidDevice
        mHidHandler = HidDeviceHandler(mHidDevice!!, mState,reportRegister)
        mHidHandler?.handle()
    }

    override fun onServiceDisconnected(profile: Int) {
        if (profile != BluetoothProfile.HID_DEVICE) return
        mHidDevice = null
    }

    fun register(context: Context) {
        if (mAppIsRegister) return
        val isResult = blAdapter.getProfileProxy(
            context.applicationContext,
            this,
            BluetoothHidDevice.HID_DEVICE
        )
        if (!isResult) {
            showToast("获取配置代理失败")
        } else {
            mAppIsRegister = true
        }
    }


    override fun onCleared() {
        super.onCleared()
        mHidDevice?.unregisterApp()
        blAdapter.closeProfileProxy(BluetoothProfile.HID_DEVICE, mHidDevice)
    }


    fun connectBl(device: BluetoothDevice) {
        SpHelper.put(LAST_CONTROL,device.address)
        mConnectDevice = device
        mConnectState.value = 0
        blAdapter.cancelDiscovery()
        launchIO {
            mHidDevice?.connect(device)
            if (hidEnable.value==false){
                mConnectState.postValue(2)
            }
        }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    fun register() {
        mHidDevice?.run {
            mHidHandler?.register(this)
            mConnectDevice?.run {
                mHidDevice!!.connect(this)
            }
        }
    }

    fun tryConnectDfBl() {
        val lastAddr = SpHelper.getString(LAST_CONTROL)
        if (!TextUtils.isEmpty(lastAddr)){
            connectBl(blAdapter.getRemoteDevice(lastAddr))
        }
    }
}