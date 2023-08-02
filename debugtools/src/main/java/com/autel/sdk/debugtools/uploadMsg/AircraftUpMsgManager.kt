package com.autel.sdk.debugtools.uploadMsg

import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.sdk.debugtools.uploadMsg.inter.IAircraftUpMsgHandler

/**
 * 上报消息处理类
 * Copyright: Autel Robotics
 * @author xulc on 2022/10/09.
 */
object AircraftUpMsgManager : IAircraftUpMsgHandler {

    override fun listenMsg(aircraftDevice: IBaseDevice) {
        FlightControlUploadMsgManager.listenMsg(aircraftDevice)
        MissionWaypointStatusUpMsgManager.listenMsg(aircraftDevice)
        AIServiceDetectTargetMsgManager.listenMsg(aircraftDevice)
        CameraStatusUpMsgManager.listenMsg(aircraftDevice)
        GimbalUploadMsgManager.listenMsg(aircraftDevice)
        VisionUploadMsgManager.listenMsg(aircraftDevice)
    }

    override fun cancelListenMsg(aircraftDevice: IBaseDevice) {
        //移除mock数据对应的监听
        aircraftDevice.getKeyManager().removeAllListen()
        FlightControlUploadMsgManager.cancelListenMsg(aircraftDevice)
        MissionWaypointStatusUpMsgManager.cancelListenMsg(aircraftDevice)
        AIServiceDetectTargetMsgManager.cancelListenMsg(aircraftDevice)
        CameraStatusUpMsgManager.cancelListenMsg(aircraftDevice)
        GimbalUploadMsgManager.cancelListenMsg(aircraftDevice)
        VisionUploadMsgManager.cancelListenMsg(aircraftDevice)
    }

}

