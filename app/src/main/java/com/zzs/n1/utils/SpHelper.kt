package com.zzs.n1.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
@author  zzs
@Date 2021/4/21
@describe
 */
object SpHelper {

    private var sAppConfig: SharedPreferences? = null
    private const val SP_NAME = "Reading_pad_config"

    private lateinit var sApp: Application

    fun init(sApp: Application) {
        this.sApp = sApp
    }

    private fun getSp(): SharedPreferences? {
        if (sAppConfig == null && this::sApp.isInitialized) {
            sAppConfig = sApp.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            return sAppConfig
        }
        return sAppConfig
    }

    fun put(key: String, value: String) {
        val ed = getSp()?.edit()
        ed?.putString(key, value)
        ed?.commit()
    }

    fun put(key: String, value: Int) {
        val ed = getSp()?.edit()
        ed?.putInt(key, value)
        ed?.commit()
    }

    fun put(key: String, value: Boolean) {
        val ed = getSp()?.edit()
        ed?.putBoolean(key, value)
        ed?.commit()
    }

    fun put(key: String, value: Long) {
        val ed = getSp()?.edit()
        ed?.putLong(key, value)
        ed?.commit()
    }

    fun put(key: String, value: Float) {
        val ed = getSp()?.edit()
        ed?.putFloat(key, value)
        ed?.commit()
    }


    fun getInt(key: String): Int? {
        return getSp()?.getInt(key, 1)
    }

    fun getBoolean(key: String): Boolean? {
        return getSp()?.getBoolean(key, false)
    }
    fun getBoolean(key: String,defValue:Boolean): Boolean {
        return getSp()?.getBoolean(key, defValue)?:true
    }

    fun getLong(key: String): Long? {
        return getSp()?.getLong(key, -1L)
    }

    fun getFloat(key: String): Float? {
        return getSp()?.getFloat(key, -1f)
    }

    fun getString(key: String): String? {
        return getSp()?.getString(key, null)
    }

}