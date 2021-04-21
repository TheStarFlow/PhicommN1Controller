package com.yougu.library_network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
@author  zzs
@Date 2021/1/11
@describe
 */
class CommonResponseInterceptor :Interceptor {
    companion object{
        const val TAG = "CommonResponseInterceptor"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestTime = System.currentTimeMillis()
        val response = chain.proceed(chain.request())
        Log.d(TAG, "requestTime=" + (System.currentTimeMillis() - requestTime))
        return response
    }
}