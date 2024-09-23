package com.autel.sdk.debugtools.fragment

import com.autel.sdk.debugtools.activity.FragmentPageInfo

/**
 * fragment info create listener return fragment object
 * Copyright: Autel Robotics
 * @author  on 2022/12/17.
 */
interface IFragmentPageInfoFactory {
    /**
     * creating page info
     * @return return page info object
     */
    fun createPageInfo(): FragmentPageInfo
}