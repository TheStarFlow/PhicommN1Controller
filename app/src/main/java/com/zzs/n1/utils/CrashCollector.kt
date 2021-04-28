package com.zzs.n1.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess

/**
@author  zzs
@Date 2021/4/25
@describe
 */
object CrashCollector : Thread.UncaughtExceptionHandler, Runnable {

    private const val PACKAGE_INFO = "packageInfo"
    private const val SYSTEM_VERSION = "systemVersion"
    private const val EXCEPTION_INFO = "exceptionInfo"
    private const val tag = "ZZSCrashLog"
    private lateinit var sAppContext: Application
    private val exceptionMap = HashMap<String, String>()


    fun init(app: Application) {
        sAppContext = app
        Thread.setDefaultUncaughtExceptionHandler(this)
        Handler(Looper.getMainLooper()).post(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        collectInfo(e)
        saveCrashInfo(t)
    }

    private fun collectInfo(e: Throwable) {
        exceptionMap[SYSTEM_VERSION] = collectSysVersion()
        exceptionMap[EXCEPTION_INFO] = collectExceptionInfo(e = e)
        exceptionMap[PACKAGE_INFO] = collectPackageInfo()
    }

    private fun handlerMainThreadException(e: Throwable) {

    }

    private fun handlerSubThreadException(e: Throwable) {

    }

    private fun collectExceptionInfo(e: Throwable): String {
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        e.printStackTrace(printWriter)
        e.printStackTrace()
        var throwable = e.cause
        while (throwable != null) {
            throwable.printStackTrace(printWriter)
            printWriter.append("\r\n")
            throwable = throwable.cause
        }
        printWriter.close()
        return writer.toString()
    }

    private fun collectPackageInfo(): String {
        val packageManager: PackageManager = sAppContext.packageManager
        val contentMap = HashMap<String, String>()
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                    sAppContext.packageName,
                    PackageManager.GET_ACTIVITIES
            )
            val versionName =
                    if (packageInfo.versionName == null) "null" else packageInfo.versionName
            val versionCode: String = packageInfo.longVersionCode.toString() + ""
            contentMap["VERSION_NAME"] = versionName
            contentMap["VERSION_CODE"] = versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return contentMap.toString()
    }

    private fun collectSysVersion(): String {
        val versionInfo = StringBuffer()
        versionInfo.append("\n\n")
        versionInfo.append(
                """
            手机型号：${Build.MODEL}
            """.trimIndent()
        )
        versionInfo.append(
                """
            SDK版本：${Build.VERSION.SDK_INT}
            """.trimIndent()
        )
        versionInfo.append(
                """
            系统版本${Build.VERSION.RELEASE}
            """.trimIndent()
        )
        versionInfo.append(
                """
            手机制造商：${Build.MANUFACTURER}
            """.trimIndent()
        )
        return versionInfo.toString()
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveCrashInfo(t: Thread) {
        val stringBuffer = StringBuffer()
        stringBuffer.append(exceptionMap[PACKAGE_INFO]).append(System.lineSeparator())
        stringBuffer.append(exceptionMap[SYSTEM_VERSION]).append(System.lineSeparator())
        stringBuffer.append(exceptionMap[EXCEPTION_INFO]).append(System.lineSeparator())
        val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val time: String = simpleDateFormat.format(Date())
        val fileName = "CrashLog$time.txt"
        val fileDir: String = sAppContext.externalCacheDir?.absolutePath
                .toString() + "/${tag}" + "/Log/"
        stringBuffer.append("===Thread Name===>").append(t.name)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val file = File(fileDir)
            if (!file.exists()) {
                val a: Boolean = file.mkdirs()
                if (!a) {
                    return
                }
            }
            try {
                val fileOutputStream = FileOutputStream(fileDir + fileName)
                fileOutputStream.write(stringBuffer.toString().toByteArray())
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun run() {
        while (true) {
            try {
                Looper.loop()
            } catch (e: Exception) {
                uncaughtException(Looper.getMainLooper().thread, e)
            }
        }
    }
}