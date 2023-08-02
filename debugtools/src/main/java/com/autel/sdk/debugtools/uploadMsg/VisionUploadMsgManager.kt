package com.autel.sdk.debugtools.uploadMsg

import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.vision.bean.VisionRadarInfoBean
import com.autel.sdk.debugtools.helper.DroneUploadMsgData
import com.autel.sdk.debugtools.uploadMsg.inter.IAircraftUpMsgHandler

/**
 * vision uploading message listening and canceling
 * Copyright: Autel Robotics
 * @author gaojie on 2022/10/12
 */
object VisionUploadMsgManager : IAircraftUpMsgHandler {

    /**
     * 视觉告警
     */
    val visionWarningData = DroneUploadMsgData<List<VisionRadarInfoBean>>()

    override fun listenMsg(aircraftDevice: IBaseDevice) {
        val keyManager = aircraftDevice.getKeyManager()
        keyManager.listen(UploadMsgKeyTools.KeyWarning,
            object : CommonCallbacks.KeyListener<List<VisionRadarInfoBean>> {
                override fun onValueChange(
                    oldValue: List<VisionRadarInfoBean>?, newValue: List<VisionRadarInfoBean>
                ) {
                    newValue?.let {
                        visionWarningData.putValue(aircraftDevice, newValue)
                    }
                }
            })
    }

    override fun cancelListenMsg(aircraftDevice: IBaseDevice) {
        visionWarningData.remove(aircraftDevice)
    }

}