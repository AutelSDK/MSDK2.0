package com.autel.sdk.debugtools.uploadMsg

import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.WarningAtom
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.DroneSystemStateHFNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.DroneSystemStateLFNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.DroneWarningStateNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.FlightControlStatusInfo
import com.autel.sdk.debugtools.helper.DroneUploadMsgData
import com.autel.sdk.debugtools.uploadMsg.inter.IAircraftUpMsgHandler

/**
 * 飞控上报消息处理类
 * Copyright: Autel Robotics
 * @author xulc on 2022/10/11
 *
 */
object FlightControlUploadMsgManager : IAircraftUpMsgHandler {
    //飞行控制高频上报参数
    val systemStateData = DroneUploadMsgData<DroneSystemStateHFNtfyBean>()

    //飞机通用参数上报
    val systemLFStateData = DroneUploadMsgData<DroneSystemStateLFNtfyBean>()

    //飞机工作状态信息上报
    val flightControlStatusInfoData = DroneUploadMsgData<FlightControlStatusInfo>()

    //告警上报参数
    val warningStateData = DroneUploadMsgData<DroneWarningStateNtfyBean>()

    //飞机告警通知
    val warningAtomListData = DroneUploadMsgData<List<WarningAtom>>()

    //飞机实时告警通知
    val warningAtomData = DroneUploadMsgData<WarningAtom>()

    private var curWarnTime = 0L//飞机告警信息，保证2s写一次
    override fun listenMsg(aircraftDevice: IBaseDevice) {
        val keyManager = aircraftDevice.getKeyManager()
        //飞控高频信息
        keyManager.listen(UploadMsgKeyTools.KeyFlightControlSystemHFState,
            object : CommonCallbacks.KeyListener<DroneSystemStateHFNtfyBean> {
                override fun onValueChange(
                    oldValue: DroneSystemStateHFNtfyBean?, newValue: DroneSystemStateHFNtfyBean
                ) {
                    newValue?.let {
                        systemStateData.putValue(aircraftDevice, newValue)
                    }
                }
            })
        //飞控低频信息
        keyManager.listen(UploadMsgKeyTools.KeyFlightControlSystemLFState,
            object : CommonCallbacks.KeyListener<DroneSystemStateLFNtfyBean> {
                override fun onValueChange(
                    oldValue: DroneSystemStateLFNtfyBean?, newValue: DroneSystemStateLFNtfyBean
                ) {
                    newValue?.let {
                        systemLFStateData.putValue(aircraftDevice, newValue)
                    }
                }
            })
        //飞机工作状态信息
        keyManager.listen(UploadMsgKeyTools.KeyDroneWorkStatusInfoReport,
            object : CommonCallbacks.KeyListener<FlightControlStatusInfo> {
                override fun onValueChange(
                    oldValue: FlightControlStatusInfo?, newValue: FlightControlStatusInfo
                ) {
                    newValue?.let {
                        flightControlStatusInfoData.putValue(aircraftDevice, newValue)
                    }
                }
            })
        //飞控告警信息
        keyManager.listen(UploadMsgKeyTools.KeyFlightStateWarning,
            object : CommonCallbacks.KeyListener<DroneWarningStateNtfyBean> {
                override fun onValueChange(
                    oldValue: DroneWarningStateNtfyBean?, newValue: DroneWarningStateNtfyBean
                ) {
                    newValue?.let {
                        warningStateData.putValue(aircraftDevice, newValue)
                    }
                }
            })
        //飞机告警通知
        keyManager.listen(
            UploadMsgKeyTools.KeyDroneWarning,
            object : CommonCallbacks.KeyListener<List<WarningAtom>> {
                override fun onValueChange(
                    oldValue: List<WarningAtom>?,
                    newValue: List<WarningAtom>
                ) {
                    newValue?.let {
                        //保证每2s存1次飞机最新状态
                        if (System.currentTimeMillis() - curWarnTime > 1900) {
                            curWarnTime = System.currentTimeMillis()
                        }
                        warningAtomListData.putValue(aircraftDevice, newValue)
                    }
                }
            })
        //飞机实时告警通知
        keyManager.listen(
            UploadMsgKeyTools.KeyDroneRuntimeWarning,
            object : CommonCallbacks.KeyListener<WarningAtom> {
                override fun onValueChange(
                    oldValue: WarningAtom?,
                    newValue: WarningAtom
                ) {
                    newValue?.let {
                        warningAtomData.putValue(aircraftDevice, newValue)
                    }
                }
            })
    }

    override fun cancelListenMsg(aircraftDevice: IBaseDevice) {
        systemStateData.remove(aircraftDevice)
        systemLFStateData.remove(aircraftDevice)
        flightControlStatusInfoData.remove(aircraftDevice)
        warningStateData.remove(aircraftDevice)
        warningAtomData.remove(aircraftDevice)
    }
}