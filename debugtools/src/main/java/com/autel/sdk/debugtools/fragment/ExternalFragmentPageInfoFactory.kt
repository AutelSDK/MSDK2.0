package com.autel.sdk.debugtools.fragment

import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.activity.FragmentPageInfo
import com.autel.sdk.debugtools.activity.FragmentPageInfoItem

/**
 * page info creating with over ride method and items list
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */
class ExternalFragmentPageInfoFactory : IFragmentPageInfoFactory {

    override fun createPageInfo(): FragmentPageInfo {
        return FragmentPageInfo(R.navigation.nav_external).apply {
            items.add(
                FragmentPageInfoItem(
                    R.id.key_value_page,
                    R.string.debug_item_key_value_title,
                    R.string.debug_item_key_value_description
                )
            )
            items.add(
                FragmentPageInfoItem(
                    R.id.media_page,
                    R.string.debug_item_media_file_title,
                    R.string.debug_item_media_file_description
                )
            )
            items.add(
                FragmentPageInfoItem(
                    R.id.multi_video_decoding_page,
                    R.string.debug_item_multi_video_decoding_title,
                    R.string.debug_item_multi_video_decoding_description
                )
            )
            items.add(
                FragmentPageInfoItem(
                    R.id.livestream_page,
                    R.string.debug_item_livestreaming_title,
                    R.string.debug_item_livestreaming_title_description
                )
            )


            items.add(
                FragmentPageInfoItem(
                    R.id.scenerio_testing_page,
                    R.string.debug_scenario_testing_title,
                    R.string.debug_scenario_testing_description
                )
            )

            items.add(
                FragmentPageInfoItem(
                    R.id.rtk_page,
                    R.string.debug_rtk_title,
                    R.string.debug_rtk_title_description
                )
            )


            items.add(
                FragmentPageInfoItem(
                    R.id.device_log,
                    R.string.debug_device_log,
                    R.string.debug_device_log_description
                )
            )

        }
    }
}