package com.autel.sdk.debugtools.uploadMsg.inter

import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice

/**
 * 遥控器上报信息
 * Copyright: Autel Robotics
 * @author xulc on 2022/11/5
 */
interface IRemoteUpMsgHandler {
    /**
     * 监听消息
     * @param remoterDevice 设备
     */
    fun listenMsg(remoterDevice: IBaseDevice)
}