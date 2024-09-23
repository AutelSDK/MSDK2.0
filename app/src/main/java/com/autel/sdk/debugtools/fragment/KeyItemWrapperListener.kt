package com.autel.sdk.debugtools.fragment

/**
 * key and item value change listener or wrapper detection
 * Copyright: Autel Robotics
 * @author lizhiping on 2022/10/9.
 */
interface KeyItemWrapperListener<T> {
    fun actionChange(t: T?)
}