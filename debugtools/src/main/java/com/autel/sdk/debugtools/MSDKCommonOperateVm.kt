package com.autel.sdk.debugtools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.autel.sdk.debugtools.activity.FragmentPageInfo

/**
 * MSDK operate view model class
 * Copyright: Autel Robotics
 * @author maowei on 2022/12/17.
 */
class MSDKCommonOperateVm : ViewModel() {

    /**
     * observer for data changes in pager info of fragment
     * */
    val mainPageInfoList = MutableLiveData<LinkedHashSet<FragmentPageInfo>>()

    /**
     * item loader class page info
     * @param itemList data list for fragment info
     * */
    fun loaderItem(itemList: LinkedHashSet<FragmentPageInfo>) {
        if (mainPageInfoList.value == null) {
            mainPageInfoList.value = LinkedHashSet<FragmentPageInfo>()
        }
        mainPageInfoList.value?.addAll(itemList)
        mainPageInfoList.postValue(mainPageInfoList.value)
    }


}