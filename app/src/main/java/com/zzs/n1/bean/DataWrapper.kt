package com.zzs.n1.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.util.SparseArray

/**
@author  zzs
@Date 2021/4/20
@describe
 */
abstract class DataWrapper(
    private var mHidDevice: BluetoothHidDevice?,
    var mPluginDevice: BluetoothDevice?
) {
    protected val TAG = "DataWrapper"

    companion object {
        val mInputReportCache = SparseArray<ReportData>()

        val mOutputReportCache = SparseArray<ReportData>()

        var mConnectState = BluetoothHidDevice.STATE_DISCONNECTED

        var mProtocol: Byte = 0

        var mCurrentBatteryLevel = 0f

        var mBatteryLevel = 0f

        fun cacheReport(rpId:Int,data: ByteArray,isInput:Boolean){
            var rd:ReportData?
            rd = if (isInput){
                mInputReportCache[rpId]
            }else{
                mOutputReportCache[rpId]
            }
            if (rd==null){
                rd = ReportData()
                if (isInput){
                    mInputReportCache.put(rpId,rd)
                }else{
                    mOutputReportCache.put(rpId,rd)
                }
            }
            rd.data = data.clone()
        }
    }

    protected fun sendReport(id: Int, data: ByteArray) {
        mHidDevice?.sendReport(mPluginDevice, id, data)
    }

    fun reSet() {
        mHidDevice = null
        mPluginDevice = null
    }
}