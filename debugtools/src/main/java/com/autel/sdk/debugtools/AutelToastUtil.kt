package com.autel.sdk.debugtools

import androidx.lifecycle.MutableLiveData

/**
 * Creating toast util for live data observe toast results add or update
 *
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/10/29.
 */
object AutelToastUtil {

    /**
     * Observe variable changes for these
     * */
    var autelToastLD: MutableLiveData<AutelToastResult>? = null
}