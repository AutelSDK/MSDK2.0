package com.autel.drone.demo

import android.app.Application
import com.autel.drone.sdk.vmodelx.SDKManager
import com.autel.drone.sdk.vmodelx.SDKLogManager

class DemoApplicationEx : Application() {

    override fun onCreate() {
        super.onCreate()
        SDKManager.get().init(applicationContext, true)
        SDKLogManager.get().setSDKLogPath(applicationContext)
        println("SDKManager V=${SDKManager.get().getSDKVersion()}")
    }
}