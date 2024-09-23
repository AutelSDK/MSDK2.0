package com.autel.sdk.debugtools.uploadMsg.inter

import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice

/**
 * 飞行器上报信息接口
 * Copyright: Autel Robotics
 * @author xulc on 2022/10/11
 *
 */
interface IAircraftUpMsgHandler {
    /**
     * 监听消息
     * @param aircraftDevice 飞机设备
     */
    fun listenMsg(aircraftDevice: IBaseDevice)

    /**
     * 取消监听消息
     * @param aircraftDevice 飞机设备
     */
    fun cancelListenMsg(aircraftDevice: IBaseDevice)
}