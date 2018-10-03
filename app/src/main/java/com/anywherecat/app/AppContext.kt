package com.anywherecat.app

import android.app.Application
import com.baidu.mapapi.SDKInitializer
import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.Bugly

class AppContext: Application() {
    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        Bugly.init(applicationContext, "9b17beaa26", false)
        SDKInitializer.initialize(applicationContext)
    }
}