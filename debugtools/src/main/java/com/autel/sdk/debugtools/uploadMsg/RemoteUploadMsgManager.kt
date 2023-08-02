package com.autel.sdk.debugtools.uploadMsg

import android.content.Intent
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.SDKManager
import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.alink.enums.AirLinkMatchStatusEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.RCBandInfoTypeBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.RockerCalibrationStateNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.remotecontrol.bean.HardwareButtonInfoBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.remotecontrol.bean.RCHardwareStateNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.remotecontrol.bean.RCStateNtfyBean
import com.autel.sdk.debugtools.helper.RemoteUploadMsgData
import com.autel.sdk.debugtools.uploadMsg.inter.IRemoteUpMsgHandler

/**
 * remote upload message listening and canceling
 * Copyright: Autel Robotics
 * @author xulc on 2022/11/1
 */
object RemoteUploadMsgManager : IRemoteUpMsgHandler {

    /**
     * 对频进度上报事件
     */
    val airLinkMatchStatusData = RemoteUploadMsgData<AirLinkMatchStatusEnum>()

    /**
     * 遥控器定频上报
     */
    val rcHardwareReportData = RemoteUploadMsgData<RCHardwareStateNtfyBean>()

    /**
     * 遥控器按钮信息
     */
    val rcHardwareButtonInfoData = RemoteUploadMsgData<HardwareButtonInfoBean>()

    /**
     * 遥控器状态上报
     */
    val rcStateBeanData = RemoteUploadMsgData<RCStateNtfyBean>()

    /**
     * 摇杆校准状态上报
     */
    val rockerCalibrationData = RemoteUploadMsgData<RockerCalibrationStateNtfyBean>()
    var mRcType: Int = -1
    var lastLiveDeckTime: Long = 0

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
        keyManager?.listen(UploadMsgKeyTools.KeyRCHardwareStateReport,
            object : CommonCallbacks.KeyListener<RCHardwareStateNtfyBean> {

                override fun onValueChange(
                    oldValue: RCHardwareStateNtfyBean?, newValue: RCHardwareStateNtfyBean
                ) {
                    newValue?.let {
                        rcHardwareReportData.setValue(newValue)
                    }
                }
            })
        keyManager?.listen(UploadMsgKeyTools.KeyRCHardwareButtonInfo,
            object : CommonCallbacks.KeyListener<HardwareButtonInfoBean> {
                override fun onValueChange(
                    oldValue: HardwareButtonInfoBean?, newValue: HardwareButtonInfoBean
                ) {
                    newValue?.let {
                        rcHardwareButtonInfoData.setValue(newValue)
                    }
                }
            })
        keyManager?.listen(UploadMsgKeyTools.KeyRCHardwareButtonInfo,
            object : CommonCallbacks.KeyListener<HardwareButtonInfoBean> {

                override fun onValueChange(
                    oldValue: HardwareButtonInfoBean?, newValue: HardwareButtonInfoBean
                ) {
                    newValue?.let {
                        rcHardwareButtonInfoData.setValue(newValue)
                    }
                }
            })
        keyManager?.listen(
            UploadMsgKeyTools.KeyRCState,
            object : CommonCallbacks.KeyListener<RCStateNtfyBean> {
                override fun onValueChange(
                    oldValue: RCStateNtfyBean?, newValue: RCStateNtfyBean
                ) {
                    newValue?.let {
                        rcStateBeanData.setValue(newValue)
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

        keyManager?.listen(UploadMsgKeyTools.KeyRCBandInfoType,
            object : CommonCallbacks.KeyListener<RCBandInfoTypeBean> {
                override fun onValueChange(
                    oldValue: RCBandInfoTypeBean?, newValue: RCBandInfoTypeBean
                ) {
                    newValue?.let {
                        SDKLog.d("RCType", "rctype->" + newValue.RCType + " mRcType->$mRcType")
                        if (newValue.RCType != mRcType) {
                            mRcType = newValue.RCType
                            if (mRcType == 7) {
                                lastLiveDeckTime = System.currentTimeMillis()
                                val actionType = "com.autel.NEST_DEVICE_ATTACHED"
                                startLiveDeckPage(actionType)
                            } else {
                                if (System.currentTimeMillis() - lastLiveDeckTime > 1000) {
                                    val actionType = "com.autel.NEST_DEVICE_DETACHED"
                                    mRcType = -1
                                    startLiveDeckPage(actionType)
                                }
                            }
                        }
                    }
                }
            })
    }

    private fun startLiveDeckPage(actionType: String) {
        val intent = Intent() //靠intent进行数据传递
        intent.action = actionType
        intent.setPackage(SDKManager.get().sContext?.getPackageName())
        SDKManager.get().sContext?.sendBroadcast(intent)
    }


}