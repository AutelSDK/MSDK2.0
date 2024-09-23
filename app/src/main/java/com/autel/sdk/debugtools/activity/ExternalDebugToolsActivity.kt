package com.autel.sdk.debugtools.activity


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.autel.drone.sdk.store.SDKStorage
import com.autel.drone.sdk.vmodelx.constants.MmkvConstants
import com.autel.sdk.debugtools.LanguageUtils
import com.autel.sdk.debugtools.fragment.ExternalFragmentPageInfoFactory
import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * testing tools activity class for debug tools
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/23
 */

class ExternalDebugToolsActivity : DebugToolsActivity() {

    companion object {
        const val TAG = "ExternalDebugToolsActivity"
    }


    val array = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )


    override fun loadPages() {
        msdkCommonOperateVm.apply {
            val itemList = LinkedHashSet<FragmentPageInfo>().also {
                it.add(ExternalFragmentPageInfoFactory().createPageInfo())
            }
            loaderItem(itemList)
        }
    }

    override fun onCameraAbilityFetchListener(fetched: Boolean) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (SDKStorage.getIntValue(MmkvConstants.IS_ENGLISH, 0)) {
            0 -> LanguageUtils.setLocale(this, LanguageUtils.defaultLanguage.language)
            1 -> LanguageUtils.setLocale(this, LanguageUtils.chinesLanguage.language)
            //2 -> LanguageUtils.setLocale(this, LanguageUtils.ukranianLanguage.language)
            else -> LanguageUtils.setLocale(this, LanguageUtils.defaultLanguage.language)
        }
        requestPermission()
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //申请权限
            ActivityCompat.requestPermissions(
                this,
                array, 1
            )
        }

    }
}