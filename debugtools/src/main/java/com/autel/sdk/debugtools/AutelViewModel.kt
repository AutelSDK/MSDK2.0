package com.autel.sdk.debugtools

import androidx.lifecycle.ViewModel
import com.autel.drone.sdk.vmodelx.interfaces.IKeyManager
import com.autel.drone.sdk.vmodelx.manager.DeviceManager

/**
 * Base view model class for Autel SDK
 *
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/10/29.
 */
open class AutelViewModel : ViewModel() {

    /**
     * return remote data for setting, obtaining, and controlling
     * */
    fun getRemoteKeyManager(): IKeyManager? {
        return DeviceManager.getDeviceManager().getFirstRemoteDevice()?.getKeyManager()
    }
    /**
     * return drone data for setting, obtaining, and controlling
     * */
    fun getKeyManager(): IKeyManager? {
        return DeviceManager.getDeviceManager().getFirstDroneDevice()?.getKeyManager()
    }

}