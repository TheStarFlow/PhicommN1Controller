package com.yougu.library_network.base

import com.yougu.library_network.environment.IEnvironment
import com.yougu.library_network.interceptor.CommonInterceptor
import com.yougu.library_network.interceptor.CommonResponseInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
@author  zzs
@Date 2021/1/11
@describe
 */
abstract class NetWorkApi : IEnvironment {

    private val retrofitHashMap = HashMap<String, Retrofit>()
    private var mOkHttpClient: OkHttpClient? = null

    init {
        mBaseUrl = if (!mIsFormal) {
            getTest()
        } else {
            getFormal()
        }
    }

    protected open fun getRetrofit(service: Class<*>): Retrofit {
        if (retrofitHashMap[mBaseUrl + service.name] != null) {
            return retrofitHashMap[mBaseUrl + service.name]!!
        }
        val retrofitBuilder = Retrofit.Builder()
        retrofitBuilder.baseUrl(mBaseUrl)
        retrofitBuilder.client(getOkHttpClient())
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = retrofitBuilder.build()
        retrofitHashMap[mBaseUrl + service.name] = retrofit
        return retrofit
    }

    protected open fun getOkHttpClient(): OkHttpClient {
        if (mOkHttpClient == null) {
            val okHttpClientBuilder = OkHttpClient.Builder()
            if (getInterceptor() != null) {
                okHttpClientBuilder.addInterceptor(getInterceptor())
            }
            val cacheSize = 100 * 1024 * 1024 // 10MB
            okHttpClientBuilder.cache(Cache(iNetworkRequiredInfo?.getApplicationContext()?.cacheDir, cacheSize.toLong()))
            okHttpClientBuilder.addInterceptor(CommonInterceptor(iNetworkRequiredInfo))
            okHttpClientBuilder.addInterceptor(CommonResponseInterceptor())
            if (iNetworkRequiredInfo != null && iNetworkRequiredInfo!!.isDebug()) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
            }
            mOkHttpClient = okHttpClientBuilder.build()
        }
        return mOkHttpClient!!
    }

    protected abstract fun getInterceptor(): Interceptor?

    companion object {
        var mBaseUrl: String = ""


        @JvmStatic
         var mIsFormal = true

        @JvmStatic
        var iNetworkRequiredInfo: INetworkRequiredInfo? = null

        @JvmStatic
        fun init(networkRequiredInfo: INetworkRequiredInfo, isFormal: Boolean = false) {
            iNetworkRequiredInfo = networkRequiredInfo
            mIsFormal = isFormal
        }
    }


}