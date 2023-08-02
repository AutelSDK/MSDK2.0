package com.autel.sdk.debugtools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 * test tools view model for toast result changed or not
 * Copyright: Autel Robotics
 * @author maowei on 2022/10/29
 */

class TestToolsVM : ViewModel() {
    /**
     * toast result update observer supporter object of live data
     */
    val autelToastResult = MutableLiveData<AutelToastResult>()

}