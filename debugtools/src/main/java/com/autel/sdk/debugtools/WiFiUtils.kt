package com.autel.sdk.debugtools

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.wifi.*
import android.text.TextUtils
import android.util.Log
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*


/**
 * WiFi及以太网 相关操作工具类
 * @author fyh
 * @Date 2023-03-15
 */
object WiFiUtils {

    private const val TAG: String = "WiFiUtils"

    /**
     * 判断有线网络是否连接 根据是否有获取到IP地址来判断
     */
    fun isEthernetConnected(): Boolean {
        var ip = getEth0IP()
//        AutelLog.d("NetWork", "isEthernetConnected ip $ip")
        return !TextUtils.isEmpty(ip)
    }

    /**
     * 获取WiFi热点的状态
     */
    fun getWiFiHotSpotState(applicationContext: Context): Boolean {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val methods = wifiManager.javaClass.declaredMethods
        for (method in methods) {
            if (method.name == "isWifiApEnabled") {
                try {
                    return method.invoke(wifiManager) as Boolean
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                break
            }
        }
        return false

    }



    /**
     * 修改WiFi 热点名称及密码
     */
    fun changeWiFiHotSpot(context: Context, ssid: String, password: String) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiConfiguration = WifiConfiguration().apply {
            SSID = ssid // 设置热点名称
            preSharedKey = password // 设置热点密码
            allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN) // 设置认证算法
            allowedProtocols.set(WifiConfiguration.Protocol.RSN) // 设置安全协议
            allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK) // 设置密钥管理类型
        }
        wifiManager.isWifiEnabled = true
        val method = wifiManager.javaClass.getMethod(
            "setWifiApEnabled",
            WifiConfiguration::class.java,
            Boolean::class.java
        )
        method.invoke(wifiManager, wifiConfiguration, true) // 打开热点

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentWifiConfiguration(wifiManager: WifiManager): WifiConfiguration? {
        val configuredNetworks = wifiManager.configuredNetworks
        if (configuredNetworks != null) {
            for (config in configuredNetworks) {
                if (config.status == WifiConfiguration.Status.CURRENT) {
                    return config
                }
            }
        }
        return null
    }

    /**
     * 获取设备的ETH0 或者WiFi 的IP地址
     */
    fun getIPAddress(): String? {
        var ip = getEth0IP()
        return if (!TextUtils.isEmpty(ip)) {
            ip
        } else {
            getWiFiWlan0IPAddress()
        }
    }

    /**
     * 获取有线网络IP地址
     */
    private fun getEth0IP(): String {
        val interfaceName = "eth0"
        try {
            val enNetworkInterface = NetworkInterface.getNetworkInterfaces() //获取本机所有的网络接口
            while (enNetworkInterface.hasMoreElements()) {  //判断 Enumeration 对象中是否还有数据
                val networkInterface = enNetworkInterface.nextElement() //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp) { // 判断网口是否在使用
                    continue
                }
                if (interfaceName != networkInterface.displayName) { // 网口名称是否和需要的相同
                    continue
                }
                val enInetAddress =
                    networkInterface.inetAddresses //getInetAddresses 方法返回绑定到该网卡的所有的 IP 地址。
                while (enInetAddress.hasMoreElements()) {
                    val inetAddress = enInetAddress.nextElement()
                    if (inetAddress is Inet4Address) {  //判断是否为ipv4
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取WiFi 的IP地址
     */
    fun getWiFiIP(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiManager!!.connectionInfo
        val ipAddress = wifiInfo.ipAddress

        return (ipAddress and 0xFF).toString() + "." + (ipAddress shr 8 and 0xFF) + "." + (ipAddress shr 16 and 0xFF) + "." + (ipAddress shr 24 and 0xFF)
    }

    /**
     * 获取WiFi热点IP地址
     */
    fun getWiFiWlan0IPAddress(): String {
        try {
            val ifaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (iface in ifaces) {
                if (iface.name.equals("wlan0", ignoreCase = true)) {
                    val addrs = Collections.list(iface.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLinkLocalAddress && !addr.isLoopbackAddress) {
                            return addr.hostAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("getWifiIpAddress", e.toString())
        }
        return "0.0.0.0"
    }

    /**
     * 获取WiFi热点IP地址,此接口反馈0.0.0.0
     */
    fun getWiFiHotSpotIP(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcpInfo = wifiManager.dhcpInfo
        val ipAddress = dhcpInfo.ipAddress
        return "${(ipAddress and 0xFF)}.${(ipAddress shr 8 and 0xFF)}.${(ipAddress shr 16 and 0xFF)}.${(ipAddress shr 24 and 0xFF)}"
    }

    /**
     * 获取当前扫描的WiFi列表信息
     */
    fun getWifiList(context: Context): List<ScanResult>? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.startScan()
        return wifiManager.scanResults
    }

    /**
     * 获取当前连接的WiFi信息，如果没有连接WiFi 返回null
     */
    fun getCurrentWifiInfo(context: Context): WifiInfo? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo
    }

    /**
     * 判断WiFi是否打开
     */
    fun isWiFiState(applicationContext: Context): Boolean {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }

    /**
     * 判断WiFi是否连接
     */
    fun isWiFiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return (networkInfo != null
                && networkInfo.isConnected
                && networkInfo.type == ConnectivityManager.TYPE_WIFI)
    }

    fun connectToWiFi(context: Context, ssid: String, password: String) {
        val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        Log.d("WifiUtils",  "connectWiFi->$ssid $password")
        // 检查 WiFi 是否已打开
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        // 构建 WiFi 配置
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = String.format("\"%s\"", ssid)
        wifiConfig.preSharedKey = String.format("\"%s\"", password)

        // 添加 WiFi 配置
        val networkId = wifiManager.addNetwork(wifiConfig)

        // 检查密码是否正确
        val isPasswordCorrect = wifiManager.enableNetwork(networkId, false)
        Log.d("WifiUtils",  "connectWiFi 密码是否正确->$isPasswordCorrect")
        if (!isPasswordCorrect) {
            // 删除已添加的网络
            wifiManager.removeNetwork(networkId)
            return
        }

        // 连接到 WiFi
        wifiManager.disconnect()
        wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()
    }


    /**
     * 将信号强度值转换成5格的信号强度值
     */
    fun convertWifiSignalStrength(signalStrength: Int): Int {
        return when {
            signalStrength >= -50 -> 5
            signalStrength >= -60 -> 4
            signalStrength >= -70 -> 3
            signalStrength >= -80 -> 2
            else -> 1
        }
    }

    /**
     * 端开当前WiFi连接
     */
    fun disconnectWiFi(context: Context) {
        val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled) {
            wifiManager.disconnect()
        }
    }

}

