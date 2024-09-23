package com.autel.sdk.debugtools


/**
 * network detection util singleton class
 * Copyright: Autel Robotics
 * @author lizhiping on 2022/10/12.
 */
class NetworkDetectUtil {

    companion object {
        fun getInstance() = InstanceHelper.sSingle
    }

    object InstanceHelper {
        val sSingle = NetworkDetectUtil()
    }

    /**
     * Ping命令格式为：ping -c count -w timeOut ip
     * 其中参数 ：
     * -c：是指ping的次数
     * -w：是指执行的最后期限，单位为m
     *
     * @param ip    需要ping的IP
     */
    public fun ping(ip: String): Boolean {
        return ping(ip, 1, 5)
    }

    /**
     * Ping命令格式为：ping -c count -w timeOut ip
     * 其中参数 ：
     * -c：是指ping的次数
     * -w：是指执行的最后期限，单位为m
     *
     * @param ip        需要ping的IP
     * @param count     ping的次数
     * @param timeOut   超时时间
     */
    public fun ping(ip: String, count: Int, timeOut: Int): Boolean {
        val cmd = "ping -c $count -w $timeOut $ip"
        val process = Runtime.getRuntime().exec(cmd)
        val status = process.waitFor()
        return status == 0
    }

}