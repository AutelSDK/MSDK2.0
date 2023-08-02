package com.autel.sdk.debugtools.uploadMsg

import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.sdk.debugtools.uploadMsg.inter.IAircraftUpMsgHandler

/**
 * AI service detect with message manager by listen and cancel it
 * Copyright: Autel Robotics
 * @author xulc on 2022/10/11
 */
object AIServiceDetectTargetMsgManager : IAircraftUpMsgHandler {
    override fun listenMsg(aircraftDevice: IBaseDevice) {
        val keyManager = aircraftDevice.getKeyManager()
//        keyManager.listen()
    }

    override fun cancelListenMsg(aircraftDevice: IBaseDevice) {

    }
}