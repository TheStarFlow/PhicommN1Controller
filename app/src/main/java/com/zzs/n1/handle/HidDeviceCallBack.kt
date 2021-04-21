package com.zzs.n1.handle

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.util.Log
import com.zzs.n1.bean.*
import com.zzs.n1.bean.DataWrapper.Companion.mBatteryLevel
import com.zzs.n1.bean.DataWrapper.Companion.mCurrentBatteryLevel

/**
@author  zzs
@Date 2021/4/20
@describe  运行在线程里面
 */
class HidDeviceCallBack(val mHidDevice: BluetoothHidDevice, val mState: StateReporter) : BluetoothHidDevice.Callback() {

    companion object {
        const val TAG = "HidDeviceCallBack"
    }


    var mKeyBoardWrapper: KeyBoardWrapper? = null
    var mMouseWrapper: MouseWrapper? = null
    var mBatteryWrapper: BatteryWrapper? = null
    var mPluginDevice: BluetoothDevice? = null

    override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
        mPluginDevice = pluggedDevice
        if (registered){
            mPluginDevice?.run {
                Log.d(TAG, "插入设备注册")
                mState.onReportState(mPluginDevice?.name ?: "匿名设备")
                initWrapper()
            }
        }
        mState.reportRegister(registered)
    }

    override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
        DataWrapper.mConnectState = state
        if (state == BluetoothHidDevice.STATE_CONNECTED) {
            DataWrapper.mInputReportCache.clear()
            DataWrapper.mOutputReportCache.clear()
            mBatteryWrapper?.update(mBatteryLevel)
            mCurrentBatteryLevel = mBatteryLevel
            if (device != mPluginDevice) {
                mPluginDevice = device
                mMouseWrapper?.mPluginDevice = mPluginDevice
                mKeyBoardWrapper?.mPluginDevice = mPluginDevice
                mMouseWrapper?.mPluginDevice = mPluginDevice
            }
            mState.onReportState(mPluginDevice?.name ?: "匿名设备")
        } else {
            DataWrapper.mConnectState = BluetoothHidDevice.PROTOCOL_REPORT_MODE.toInt()
            mState.onReportState("连接失败~")
        }
    }

    override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
        when (type) {
            BluetoothHidDevice.REPORT_TYPE_INPUT -> {
                var rd = DataWrapper.mInputReportCache.get(id.toInt())
                if (rd != null) {
                    mHidDevice.replyReport(mPluginDevice, BluetoothHidDevice.REPORT_TYPE_INPUT, id, rd.data)
                } else {
                    rd = ReportData()
                    when (id) {
                        mKeyBoardWrapper?.getReportId(BluetoothHidDevice.REPORT_TYPE_INPUT.toInt()) -> {
                            val mBuffer = ByteArray(8)
                            Log.v(TAG, "get_report id for keyboard")
                            for (i in 0..7) {
                                mBuffer[i] = 0x00
                            }
                            DataWrapper.cacheReport(id.toInt(), mBuffer, true)
                            mHidDevice.replyReport(mPluginDevice, BluetoothHidDevice.REPORT_TYPE_INPUT, id, mBuffer)
                        }
                        mMouseWrapper?.getReportId() -> {
                            val mBuffer = ByteArray(4)
                            Log.v(TAG, "get_report id for mouse")
                            for (i in 0..3) {
                                mBuffer[i] = 0x00
                            }
                            DataWrapper.cacheReport(id.toInt(), mBuffer, true)
                            mHidDevice.replyReport(mPluginDevice, BluetoothHidDevice.REPORT_TYPE_INPUT, id, mBuffer)
                        }
                        else -> {

                            /* Invalid Report Id */
                            Log.v(TAG, "Get Report for Invalid report id = $id")
                            mHidDevice.reportError(mPluginDevice, BluetoothHidDevice.ERROR_RSP_INVALID_RPT_ID)
                        }
                    }
                }
            }
            BluetoothHidDevice.REPORT_TYPE_OUTPUT -> {
                if (id != mKeyBoardWrapper?.getReportId(BluetoothHidDevice.REPORT_TYPE_OUTPUT.toInt())) {
                    Log.v(TAG, "onGetReport(), output report for invalid id = $id")
                    mHidDevice.reportError(device, BluetoothHidDevice.ERROR_RSP_INVALID_RPT_ID)
                } else {
                    val rd: ReportData = DataWrapper.mOutputReportCache.get(id.toInt())
                    if (rd == null) {
                        /*
                          * No output report received with particular report id,
                          * report default values
                          */
                        val mBuffer = ByteArray(8)
                        Log.v(TAG, "get_report id for keyboard")
                        for (i in 0..7) {
                            mBuffer[i] = 0x00
                        }
                        mHidDevice.replyReport(device, BluetoothHidDevice.REPORT_TYPE_OUTPUT, id, mBuffer)
                    } else {
                        mHidDevice.replyReport(device, BluetoothHidDevice.REPORT_TYPE_OUTPUT, id, rd.data)
                    }
                }
            }
            else -> {
                Log.v(TAG, "onGetReport(), unsupported report type = $type")
                /* Unsupported report type */
                mHidDevice.reportError(device, BluetoothHidDevice.ERROR_RSP_UNSUPPORTED_REQ)
            }

        }
    }

    override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
        if (type != BluetoothHidDevice.REPORT_TYPE_OUTPUT) {
            /* Unsupported report type */
            Log.v(TAG, "onSetReport(), unsupported report type = $type")
            mHidDevice.reportError(device, BluetoothHidDevice.ERROR_RSP_UNSUPPORTED_REQ)
        } else {
            if (id != mKeyBoardWrapper?.getReportId(BluetoothHidDevice.REPORT_TYPE_OUTPUT.toInt())) {
                Log.v(TAG, "onSetReport(), output report for invalid id = $id")
                mHidDevice.reportError(device, BluetoothHidDevice.ERROR_RSP_INVALID_RPT_ID)
            } else {
                Log.v(TAG, "MESSAGE_SET_REPORT_RECEIVED for id = $id")
                if (id != mKeyBoardWrapper?.getReportId(BluetoothHidDevice.REPORT_TYPE_OUTPUT.toInt())) {
                    Log.v(TAG, "onSetReport(), set report for invalid id = $id")
                    mHidDevice.reportError(mPluginDevice, BluetoothHidDevice.ERROR_RSP_INVALID_RPT_ID)
                    return
                }

                if (mKeyBoardWrapper?.parseIncomingReport(id, data!!) == false) {
                    Log.v(TAG, "onSetReport(), parameters invalid")
                    mHidDevice.reportError(mPluginDevice, BluetoothHidDevice.ERROR_RSP_INVALID_PARAM)
                    return
                }

                Log.v(TAG, "onSetReport(), sending successful handshake for set report")
                mHidDevice.reportError(mPluginDevice, BluetoothHidDevice.ERROR_RSP_SUCCESS)
            }
        }
    }

    override fun onSetProtocol(device: BluetoothDevice?, protocol: Byte) {
        DataWrapper.mProtocol = protocol
    }

    override fun onInterruptData(device: BluetoothDevice?, reportId: Byte, data: ByteArray?) {
        data?.let { mKeyBoardWrapper?.parseIncomingReport(reportId, it) }
    }

    override fun onVirtualCableUnplug(device: BluetoothDevice?) {
        mPluginDevice = null
        reset()
    }


    private fun initWrapper() {
        mKeyBoardWrapper = KeyBoardWrapper(mHidDevice, mPluginDevice)
        mMouseWrapper = MouseWrapper(mHidDevice, mPluginDevice)
        mBatteryWrapper = BatteryWrapper(mHidDevice, mPluginDevice)
    }

    private fun reset() {
        mBatteryWrapper?.reSet()
        mMouseWrapper?.reSet()
        mKeyBoardWrapper?.reSet()
    }

    interface StateReporter {

        fun onReportState(msg: String)

        fun reportRegister(boolean: Boolean)
    }
}