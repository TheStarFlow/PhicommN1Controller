package com.yougu.library_network.interceptor

import com.yougu.library_network.base.INetworkRequiredInfo
import okhttp3.Interceptor
import okhttp3.Response

/**
@author  zzs
@Date 2021/1/11
@describe
 */
class CommonInterceptor(private val requiredInfo: INetworkRequiredInfo?) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("os", "android")
        builder.addHeader("appVersion", requiredInfo?.getAppVersion()?:"error version")
        return chain.proceed(builder.build())
    }
}