package com.autel.sdk.debugtools.uploadMsg


import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.alink.enums.AirLinkMatchStatusEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.RockerCalibrationStateNtfyBean
import com.autel.sdk.debugtools.helper.RemoteUploadMsgData
import com.autel.sdk.debugtools.uploadMsg.inter.IRemoteUpMsgHandler


object DebugToolUploadMsgManager : IRemoteUpMsgHandler {

    /**
     * 对频进度上报事件
     */
    val airLinkMatchStatusData = RemoteUploadMsgData<AirLinkMatchStatusEnum>()

    /**
     * 摇杆校准状态上报
     */
    val rockerCalibrationData = RemoteUploadMsgData<RockerCalibrationStateNtfyBean>()

    override fun listenMsg(remoterDevice: IBaseDevice) {
        val keyManager = remoterDevice.getKeyManager()
        keyManager?.listen(UploadMsgKeyTools.keyLinkMatch,
            object : CommonCallbacks.KeyListener<AirLinkMatchStatusEnum> {
                override fun onValueChange(
                    oldValue: AirLinkMatchStatusEnum?, newValue: AirLinkMatchStatusEnum
                ) {
                    newValue?.let {
                        airLinkMatchStatusData.setValue(newValue)
                    }
                }
            })

        keyManager?.listen(UploadMsgKeyTools.KeyRCRockerCalibrationState,
            object : CommonCallbacks.KeyListener<RockerCalibrationStateNtfyBean> {
                override fun onValueChange(
                    oldValue: RockerCalibrationStateNtfyBean?,
                    newValue: RockerCalibrationStateNtfyBean
                ) {
                    newValue?.let {
                        rockerCalibrationData.setValue(newValue)
                    }
                }
            })
    }



}