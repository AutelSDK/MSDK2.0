package com.autel.sdk.debugtools

/**
 * defines channel type for every communication with drone and remote pad
 *
 * Copyright: Autel Robotics
 * @author maowei on 2022/10/29.
 */
enum class ChannelType( name: String) {
//    /**
//     * 电池
//     */
//    CHANNEL_TYPE_BATTERY("BATTERY"),

    /**
     * 云台
     */
    CHANNEL_TYPE_GIMBAL("GIMBAL"),

    /**
     * 相机
     */
    CHANNEL_TYPE_CAMERA("CAMERA"),


    /**
     * 飞行任务
     */
    CHANNEL_TYPE_FLIGHT_MISSION("FLIGHT_MISSION"),

    /**
     * Airlink
     */
    CHANNEL_TYPE_AIRLINK("AIRLINK"),
    /**
     * AIService
     */
    CHANNEL_TYPE_AISERVICE("AI_SERVICE"),

    /**
     * Flight Params
     */
    CHANNEL_TYPE_FLIGHT_PARAMS("FLIGHT PARAMS"),

    /**
     * Flight Control
     */
    CHANNEL_TYPE_FLIGHT_CONTROL("FLIGHT CONTROL"),

    /**
     * Remote Controller
     */
    CHANNEL_TYPE_REMOTE_CONTROLLER("REMOTE CONTROLLER"),

    /**
     * BLE
     */
    CHANNEL_TYPE_BLE("BLE"),

    /**
     * RTK
     */
    CHANNEL_TYPE_RTK_BASE_STATION("RTK BASE STATION"),

    /**
     * RTK
     */
    CHANNEL_TYPE_RTK_MOBILE_STATION("RTK MOBILE STATION"),

    /**
     * Product
     */
    CHANNEL_TYPE_PRODUCT("PRODUCT"),

    /**
     * OcuSync
     */
    CHANNEL_TYPE_OCU_SYNC("OCU SYNC"),

    /**
     * Vision
     */
    CHANNEL_TYPE_VISION("VISION"),

    /**
     * Radar
     */
    CHANNEL_TYPE_RADAR("RADAR"),
    /**
     * RTK
     */
    CHANNEL_TYPE_RTK("RTK"),

    /**
     * NTRIP
     */
    CHANNEL_TYPE_NTRIP("NTRIP"),

    /**
     * Radar
     */
    CHANNEL_TYPE_MQTT("MQTT"),
    /**
     * Radar
     */
    CHANNEL_TYPE_LTEMODULE("LTE MODULE"),


    /**
     * Mobile Network
     */
    CHANNEL_TYPE_MOBILE_NETWORK("MOBILE NETWORK"),


    /**
     * on board
     */
    CHANNEL_TYPE_ON_BOARD("BOARD"),

    /**
     * Payload
     */
    CHANNEL_TYPE_ON_PAYLOAD("PAYLOAD"),

    /**
     * lidar
     */
    CHANNEL_TYPE_LIDAR("LIDAR"),

    CHANNEL_TYPE_AI_TRACK("AI_TRACK"),

    CHANNEL_TYPE_REMOTE_ID("REMOTE_ID"),

    CHANNEL_TYPE_NEST("NEST"),

    SYSTEM_MANAGER("SYSTEM_MANAGER"),

    MISSION_MANAGER("MISSION_MANAGER"),
    ACCESSORIES_PROXY("ACCESSORIES_PROXY"),
    CLOUD_API("CLOUD_API"),

    /**
     * common upload
     */
    COMMON_UPLOAD("COMMON UPLOAD"),

    /**
     * Autonomy
     */
    CHANNEL_TYPE_AUTONOMY("AUTONOMY"),
    /**
     * Accurate Retake
     */
    CHANNEL_TYPE_ACCURATE_RETAKE("ACCURATE_RETAKE"),
    /**
     * RTMP
     */
    CHANNEL_TYPE_RTMP("RTMP"),
    /**
     * Command Center
     */
    CHANNEL_TYPE_COMMAND_CENTER("COMMAND_CENTER"),

    /**
     * RTC
     */
    CHANNEL_TYPE_RTC("RTC"),

    /**
     * WIFI
     */
    CHANNEL_TYPE_WIFI("WIFI"),

    /**
     * Hardware data security
     */
    HARDWARE_DATA_SECURITY("HARDWARE_DATA_SECURITY"),

    /**
     * Payload_Key
     */
    PAYLOAD_KEY("PAYLOAD_KEY")

    ;

    private val value: String
    override fun toString(): String {
        return value
    }

    init {
        value = name
    }
}