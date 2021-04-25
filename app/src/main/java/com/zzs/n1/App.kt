package com.zzs.n1

import android.app.Application
import com.zzs.n1.utils.CrashCollector
import com.zzs.n1.utils.SpHelper

/**
@author  zzs
@Date 2021/4/21
@describe
 */
class App:Application() {

    companion object{
        lateinit var application: Application
    }
    override fun onCreate() {
        super.onCreate()
        application = this
        SpHelper.init(this)
        CrashCollector.init(this)
    }
}