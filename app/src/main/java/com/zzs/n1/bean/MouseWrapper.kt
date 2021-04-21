package com.zzs.n1.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import com.zzs.n1.constant.HidConsts
import kotlin.experimental.and
import kotlin.experimental.or

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class MouseWrapper(mHidDevice: BluetoothHidDevice?, mPluginDevice: BluetoothDevice?):DataWrapper(
    mHidDevice,
    mPluginDevice
) {
    private val mBuffer = ByteArray(4)

    fun getReportId(): Byte {
        return if (mProtocol == BluetoothHidDevice.PROTOCOL_BOOT_MODE) {
            HidConsts.BOOT_MOUSE_REPORT_ID
        } else {
            HidConsts.MOUSE_REPORT_ID
        }
    }

    @Synchronized
    fun move(dx: Byte, dy: Byte) {
        // leave buttons state unchanged
        mBuffer[1] = dx
        mBuffer[2] = dy
        val id = getReportId()
        cacheReport(id.toInt(), mBuffer, true)
        sendReport(id.toInt(), mBuffer)
    }

    @Synchronized
    fun buttonDown(which: Int) {
        mBuffer[0] = mBuffer[0] or ((1 shl which).toByte())
        mBuffer[1] = 0
        mBuffer[2] = 0
        val id = getReportId()
        cacheReport(id.toInt(), mBuffer, true)
        sendReport(id.toInt(), mBuffer)
    }

    @Synchronized
    fun buttonUp(which: Int) {
        mBuffer[0] = mBuffer[0] and (1 shl which).inv().toByte()
        mBuffer[1] = 0
        mBuffer[2] = 0
        val id = getReportId()
        cacheReport(id.toInt(), mBuffer, true)
        sendReport(id.toInt(), mBuffer)
    }

    @Synchronized
    fun scroll(delta: Byte) {
        mBuffer[3] = delta
        val id = getReportId()
        cacheReport(id.toInt(), mBuffer, true)
        sendReport(id.toInt(), mBuffer)

        // reset to 0 after sending so it won't self-repeat in subsequent
        // reports
        mBuffer[3] = 0x00
    }
}