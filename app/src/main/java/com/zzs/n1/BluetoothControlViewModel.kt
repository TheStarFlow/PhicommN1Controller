package com.zzs.n1

import android.bluetooth.*
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
import com.zzs.n1.handle.HidDeviceHandler
import com.zzs.n1.ui.BluetoothFragment
import com.zzs.n1.ui.launchIO
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

    fun onMute() {
        val code = 0x84.toByte()
        keyAction(code)
    }

    fun onScreenShot() {
        val code = 0x46.toByte()
        keyAction(code)
    }


    fun onVDown() {
        val code = 0x86.toByte()
        keyAction(code)
    }

    fun onVUp() {
        val code = 0x85.toByte()
        keyAction(code)
    }

    fun onBack() {
        val code: Byte = 0x29
        keyAction(code)
    }

    fun onLeft() {
        val code: Byte = 0x50
        keyAction(code)
    }

    fun onRight() {
        val code: Byte = 0x4F
        keyAction(code)
    }

    fun onUp() {
        val code: Byte = 0x52
        keyAction(code)
    }

    fun onDown() {
        val code: Byte = 0x51
        keyAction(code)
    }

    fun onCenter() {
        val code: Byte = 0x58
        keyAction(code)
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