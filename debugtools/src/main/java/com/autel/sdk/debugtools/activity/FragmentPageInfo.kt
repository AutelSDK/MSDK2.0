package com.autel.sdk.debugtools.activity

import com.autel.sdk.debugtools.DEFAULT_RES_ID

/**
 * page info with temp lists
 * Copyright: Autel Robotics
 * @author  on 2022/12/17.
 */

data class FragmentPageInfo(
    val vavGraphId: Int = DEFAULT_RES_ID,
    val items: LinkedHashSet<FragmentPageInfoItem> = LinkedHashSet<FragmentPageInfoItem>()
)

data class FragmentPageInfoItem(
    val id: Int = DEFAULT_RES_ID,
    val title: Int = DEFAULT_RES_ID,
    val description: Int = DEFAULT_RES_ID
)