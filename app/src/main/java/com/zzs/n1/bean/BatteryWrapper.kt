package com.zzs.n1.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.util.Log
import android.widget.TableRow
import com.zzs.n1.constant.HidConsts

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class BatteryWrapper(mHidDevice: BluetoothHidDevice?, mPluginDevice: BluetoothDevice?) : DataWrapper(
    mHidDevice,
    mPluginDevice
) {
    private val mBuffer = ByteArray(1)

    @Synchronized
    fun update(level: Float) {
        Log.v(TAG, "BatteryWrapper: update: level = $level")
        if (mConnectState!=BluetoothHidDevice.STATE_CONNECTED)
            return
        if (mProtocol!= BluetoothHidDevice.PROTOCOL_REPORT_MODE){
            return
        }
        val value =(level*255).toInt()
        Log.v(TAG, "BatteryWrapper: update: val = $value")
        mBuffer[0] = (value and 0xff) as Byte
        Log.v(TAG, "BatteryWrapper: update: mBuffer[0] = " + mBuffer[0])
        cacheReport(HidConsts.BATTERY_REPORT_ID.toInt(),mBuffer,true)
        sendReport(HidConsts.BATTERY_REPORT_ID.toInt(), mBuffer)
    }

}