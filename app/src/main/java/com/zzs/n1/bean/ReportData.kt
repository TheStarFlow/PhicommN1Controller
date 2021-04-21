package com.zzs.n1.bean

/**
@author  zzs
@Date 2021/4/20
@describe
 */
class ReportData {
    var data: ByteArray?=null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReportData

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }
}