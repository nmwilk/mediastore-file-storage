package com.nmwilkinson.mediastorefiles

import android.app.Application
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
    }
}