package com.autel.sdk.debugtools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *  key and it's value view model for data observer
 * Copyright: Autel Robotics
 * @author maowei on 2022/12/17.
 */

class KeyValueVM : ViewModel() {

    /**
     * language data changes boolean listener for data changes
     * */
    val languageLiveData = MutableLiveData<Boolean>()

    /**
     * display data changes boolean listener for data changes
     * */
    val displayLiveData = MutableLiveData<Boolean>()


}