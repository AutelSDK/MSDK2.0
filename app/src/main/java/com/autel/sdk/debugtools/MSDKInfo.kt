package com.autel.sdk.debugtools

/**
 * data class for SDK info
 * Copyright: Autel Robotics
 * @author maowei on 2022/12/17.
 * @param SDKVersion sdk version name as string
 */
data class MSDKInfo(val SDKVersion: String = DEFAULT_STR) {
    var buildVer: String = DEFAULT_STR
}