package com.yougu.library_network.base

import android.app.Application

/**
@author  zzs
@Date 2021/1/11
@describe
 */
interface INetworkRequiredInfo {
    fun getAppVersionName():String
    fun getAppVersion():String
    fun isDebug():Boolean
    fun getApplicationContext():Application
}