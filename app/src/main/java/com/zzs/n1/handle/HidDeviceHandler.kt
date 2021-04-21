package com.zzs.n1.handle

import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.zzs.n1.BluetoothControlViewModel
import com.zzs.n1.constant.HidConsts
import java.util.concurrent.Executors

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class HidDeviceHandler(
    private val mHidDevice: BluetoothHidDevice,
    val mState: MutableLiveData<String>,
    val reportRegister: MutableLiveData<Boolean>
) : HidDeviceCallBack.StateReporter {
    companion object {
        const val TAG = "HidDeviceHandler"
    }

    private val callBack = HidDeviceCallBack(mHidDevice, this)

    fun handle() {
        register(mHidDevice)
    }

    override fun onReportState(msg: String) {
        mState.postValue(msg)
    }

    override fun reportRegister(boolean: Boolean) {
        reportRegister.postValue(boolean)
    }

    fun keyUp(code: Byte) {
        callBack.mKeyBoardWrapper?.keyUp(code)
    }

    fun keyDown(code: Byte) {
        callBack.mKeyBoardWrapper?.keyDown(code)

    }

    fun register(mHidDevice: BluetoothHidDevice = this.mHidDevice) {
        val sdp = BluetoothHidDeviceAppSdpSettings(
            HidConsts.NAME,
            HidConsts.DESCRIPTION, HidConsts.PROVIDER, BluetoothHidDevice.SUBCLASS1_COMBO,
            HidConsts.DESCRIPTOR
        )

        val inQos = BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED, 200, 2, 200,
            10000 /* 10 ms */, 10000 /* 10 ms */
        )

        val outQos = BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_GUARANTEED, 900, 9, 900,
            10000 /* 10 ms */, 10000 /* 10 ms */
        )

        val result =
            mHidDevice.registerApp(sdp, inQos, outQos, Executors.newCachedThreadPool(), callBack)
        Log.v(BluetoothControlViewModel.TAG, "registerApp()=$result")

    }


}