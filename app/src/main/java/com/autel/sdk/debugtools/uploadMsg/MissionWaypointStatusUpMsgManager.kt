package com.autel.sdk.debugtools.uploadMsg

import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.mission.bean.MissionWaypointStatusReportNtfyBean
import com.autel.sdk.debugtools.helper.DroneUploadMsgData
import com.autel.sdk.debugtools.uploadMsg.inter.IAircraftUpMsgHandler

/**
 * mission way point status message listening and cancelling
 * Copyright: Autel Robotics
 * @author xulc on 2022/10/11
 */
object MissionWaypointStatusUpMsgManager : IAircraftUpMsgHandler {
    //航点状态信息上报通知
    val waypointStatusData = DroneUploadMsgData<MissionWaypointStatusReportNtfyBean>()

    override fun listenMsg(aircraftDevice: IBaseDevice) {
        val keyManager = aircraftDevice.getKeyManager()
        keyManager.listen(
            UploadMsgKeyTools.KeyStatusReportNtfy,
            object : CommonCallbacks.KeyListener<MissionWaypointStatusReportNtfyBean> {

                override fun onValueChange(
                    oldValue: MissionWaypointStatusReportNtfyBean?,
                    newValue: MissionWaypointStatusReportNtfyBean
                ) {
                    newValue?.let {
                        waypointStatusData.putValue(aircraftDevice, newValue)
                    }
                }
            })
    }

    override fun cancelListenMsg(aircraftDevice: IBaseDevice) {
        waypointStatusData.remove(aircraftDevice)
    }


}