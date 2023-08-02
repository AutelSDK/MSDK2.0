package com.autel.sdk.debugtools.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.autel.drone.sdk.libbase.error.IAutelCode
import com.autel.drone.sdk.store.SDKStorage
import com.autel.drone.sdk.vmodelx.SDKManager
import com.autel.drone.sdk.vmodelx.constants.MmkvConstants
import com.autel.drone.sdk.vmodelx.device.IAutelDroneListener
import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice
import com.autel.drone.sdk.vmodelx.interfaces.IKeyManager
import com.autel.drone.sdk.vmodelx.manager.DeviceManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.CommonKey
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.KeyTools
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.SystemInfoData
import com.autel.drone.sdk.vmodelx.utils.ToastUtils
import com.autel.sdk.debugtools.LanguageUtils
import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.SDKTwoButtonDialog
import com.autel.sdk.debugtools.activity.ExternalDebugToolsActivity
import com.autel.sdk.debugtools.databinding.FragMainTitleBinding
import com.autel.sdk.debugtools.view.spinnerview.DebugSpinnerView
import java.util.*

/**
 * SDK debug tools information showing fragment
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */
class ExternalMSDKInfoFragment : AutelFragment() {

    private lateinit var binging: FragMainTitleBinding

    private lateinit var csvLanguageSetting: DebugSpinnerView
    private var isRevertSelection = false
    private var languageIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binging = FragMainTitleBinding.inflate(inflater)
        return binging.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMSDKInfo()
        initListener()
        initLanguageSettings()
    }

    private fun initLanguageSettings() {
        csvLanguageSetting =
            binging.mainTitleLayout.findViewById(R.id.csv_language_setting)

         languageIndex = SDKStorage.getIntValue(MmkvConstants.IS_ENGLISH, 0)

        val languageList = listOf(
            getString(R.string.debug_text_english_tag),
            getString(R.string.debug_text_chines_tag),
//            getString(R.string.debug_text_Ukraine_tag)
        )

        csvLanguageSetting.dataList = languageList
        csvLanguageSetting.setDefaultText(languageIndex)
        csvLanguageSetting.setSpinnerViewListener {
            onOpenSelected(it)
        }
    }

    private fun onOpenSelected(index: Int) {
        SDKTwoButtonDialog(requireActivity()).apply {
            setMessage(getString(R.string.debug_text_confirm_language_change))
            setLeftBtnStr(getString(R.string.sdk_text_cancel))
            setRightBtnStr(getString(R.string.debug_common_text_confirm))
            setAutoDismiss(true)
            setOnDismissListener {
                isRevertSelection = false
                csvLanguageSetting.setDefaultText(languageIndex)
            }
            setRightBtnListener {
                languageIndex = index
                SDKStorage.setIntValue(MmkvConstants.IS_ENGLISH, languageIndex)
                isRevertSelection = true
                when (index) {
                    0 -> changeAppLanguage(LanguageUtils.defaultLanguage)
                    1 -> changeAppLanguage(LanguageUtils.chinesLanguage)
//                    2 -> changeAppLanguage(LanguageUtils.ukranianLanguage)
                }
            }
        }.show()
    }

    /**
     * Change language of app and redirects to Home view
     */
    private fun changeAppLanguage(locale: Locale) {
        csvLanguageSetting.setDefaultText(languageIndex)
        LanguageUtils.setLocale(requireActivity(), locale.language)
        isRevertSelection = false
        ToastUtils.showToast(
            requireContext(),
            getString(R.string.debug_language_change_successfully)
        )
        val refresh = Intent(requireActivity(), ExternalDebugToolsActivity::class.java)
        startActivity(refresh)
        requireActivity().finish()
    }

    private val iAutelDroneListener = object : IAutelDroneListener {
        override fun onDroneChangedListener(connected: Boolean, drone: IBaseDevice) {
            SDKStorage.setBooleanValue(MmkvConstants.IS_CONNECTED, connected)
            if (connected) {
                binging.mainTitleLayout.findViewById<TextView>(R.id.msdk_info_text_main).text =
                    String.format(
                        getString(R.string.debug_msdk_info_connected),
                        SDKManager.get().getSDKVersion()
                    )

                val key = KeyTools.createKey(CommonKey.keyGetSystemInitData)
                getKeyManager()?.performAction(
                    key,
                    null,
                    object : CommonCallbacks.CompletionCallbackWithParam<SystemInfoData> {
                        override fun onSuccess(t: SystemInfoData?) {
                            binging.mainTitleLayout.findViewById<TextView>(R.id.msdk_info_text_main).text =
                                String.format(
                                    getString(R.string.debug_msdk_info_connected),
                                    SDKManager.get().getSDKVersion()
                                )
                        }

                        override fun onFailure(error: IAutelCode, msg: String?) {

                        }
                    })

            } else {
                binging.mainTitleLayout.findViewById<TextView>(R.id.msdk_info_text_main).text =
                    String.format(
                        getString(R.string.debug_msdk_info_disconnected),
                        SDKManager.get().getSDKVersion()
                    )
            }
        }

        override fun onCameraAbilityFetchListener(fetched: Boolean) {
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMSDKInfo() {
        SDKManager.get().getDeviceManager().addDroneListener(iAutelDroneListener)

        if (SDKStorage.getStringValue(MmkvConstants.DRONE_VERSION)?.isNotEmpty() == true) {
            binging.mainTitleLayout.findViewById<TextView>(R.id.msdk_info_text_second).text =
                String.format(
                    getString(R.string.debug_drone_version),
                    SDKStorage.getStringValue(MmkvConstants.DRONE_VERSION)
                )
        }

        val connectedStatus: String =
            if (DeviceManager.Companion.getDeviceManager().getFirstDroneDevice()
                    ?.isConnected() == true
            ) {
                getString(R.string.debug_connected)
            } else {
                getString(R.string.debug_disconnected)
            }

        binging.mainTitleLayout.findViewById<TextView>(R.id.msdk_info_text_main).text =
            String.format(
                getString(R.string.debug_msdk_info_with_status),
                SDKManager.get().getSDKVersion()
            ) + connectedStatus

        msdkInfoVm.msdkInfo.observe(viewLifecycleOwner) {
            it?.let {
                binging.mainTitleLayout.findViewById<TextView>(R.id.msdk_info_text_main).text =
                    String.format(getString(R.string.debug_msdk_info), it.SDKVersion)
            }
        }
        msdkInfoVm.refreshMSDKInfo()

        msdkInfoVm.mainTitle.observe(viewLifecycleOwner) {
            it?.let {
                binging.mainTitleLayout.findViewById<TextView>(R.id.title_text_view)?.text = it
            }
        }
    }

    private fun initListener() {
        binging.mainTitleLayout.findViewById<ImageButton>(R.id.return_btn)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getKeyManager(): IKeyManager? {
        return DeviceManager.Companion.getDeviceManager().getFirstDroneDevice()?.getKeyManager()
    }

    override fun onDestroy() {
        super.onDestroy()
        SDKManager.get().getDeviceManager().removeDroneListener(iAutelDroneListener)
    }
}