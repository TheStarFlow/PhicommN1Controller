package com.zzs.n1.bean

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.util.Log
import com.zzs.n1.constant.HidConsts
import kotlin.experimental.and
import kotlin.experimental.or

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class KeyBoardWrapper(mHidDevice: BluetoothHidDevice?, mPluginDevice: BluetoothDevice?):DataWrapper(
    mHidDevice,
    mPluginDevice
) {
    companion object{
        private const val MODIFIER_BASE = 0xe0.toByte()
        private const val MODIFIER_COUNT: Byte = 8
    }

    private val mBuffer = ByteArray(8)

    fun getReportId(type: Int): Byte {
        return if (mProtocol == BluetoothHidDevice.PROTOCOL_BOOT_MODE) {
            HidConsts.BOOT_KEYBOARD_REPORT_ID
        } else {
            if (type == BluetoothHidDevice.REPORT_TYPE_INPUT.toInt()) {
                HidConsts.KEYBOARD_INPUT_REPORT_ID
            } else {
                HidConsts.KEYBOARD_OUTPUT_REPORT_ID
            }
        }
    }

    @Synchronized
    fun keyDown(key: Byte) {
        if (key >= MODIFIER_BASE && key <= MODIFIER_BASE + MODIFIER_COUNT) {
            mBuffer[0] =
                mBuffer[0] or ((1 shl key - MODIFIER_BASE).toByte())
        } else if (key and 0x80.toByte() != 0.toByte()) {
            mBuffer[1] = mBuffer[1] or ((1 shl ((key and 0x07).toInt())).toByte())
        } else {
            for (i in 2..7) {
                if (mBuffer[i] == 0x00.toByte()) {
                    mBuffer[i] = key
                    break
                }
            }
        }
        val id = getReportId(BluetoothHidDevice.REPORT_TYPE_INPUT.toInt())
        cacheReport(id.toInt(), mBuffer, true)
        sendReport(id.toInt(), mBuffer)
    }

    @Synchronized
    fun keyUp(key: Byte) {
        if (key >= MODIFIER_BASE && key <= MODIFIER_BASE + MODIFIER_COUNT) {
            mBuffer[0] =
                mBuffer[0] and (1 shl key - MODIFIER_BASE).inv()
                    .toByte()
        } else if (key and 0x80.toByte() != 0.toByte()) {
            mBuffer[1] = mBuffer[1] and (1 shl (key and 0x07).toInt()).inv().toByte()
        } else {
            for (i in 2..7) {
                if (mBuffer[i] == key) {
                    mBuffer[i] = 0x00
                    break
                }
            }
        }
        val id = getReportId(BluetoothHidDevice.REPORT_TYPE_INPUT.toInt())
        cacheReport(id.toInt(), mBuffer, true)
        sendReport(id.toInt(), mBuffer)
    }

    fun parseIncomingReport(
        reportId: Byte,
        data: ByteArray
    ): Boolean {
        if (reportId != getReportId(BluetoothHidDevice.REPORT_TYPE_OUTPUT.toInt())) return false
        Log.v(TAG, "parseIncomingReport(): data.length = " + data.size + " mProtocol = " + mProtocol)
        /* Output report can only be of 1 byte */if (data.size != 1) return false
        val leds = data[0]
        cacheReport(reportId.toInt(), data, false)
        return true
    }
}