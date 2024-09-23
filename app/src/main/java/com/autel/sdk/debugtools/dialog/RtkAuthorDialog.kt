package com.autel.sdk.debugtools.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.autel.drone.demo.databinding.SdkRtkAuthorLayoutDialogBinding
import com.autel.drone.sdk.store.SDKStorage
import com.autel.sdk.debugtools.beans.RTKConstans
import com.autel.sdk.debugtools.SDKBaseDialog
import com.autel.sdk.debugtools.fragment.*

/**
 * com.autel.sdk.debugtools.dialog
 *
 * Copyright: Autel Robotics
 *
 * @author R22711 on 2023/5/18.
 */
class RtkAuthorDialog(context: Context) : SDKBaseDialog(context) {

    private val binding = SdkRtkAuthorLayoutDialogBinding.inflate(LayoutInflater.from(context))

    /** 是否自动关闭弹窗 */
    private var isAutoDismiss = true
    private var onBtnClick: ((String, Int, String, String, String) -> Unit)? = null

    init {
        binding.tvCancel.setOnClickListener {
            if (isAutoDismiss) {
                dismiss()
            }
        }

        binding.tvConfirm.setOnClickListener {
            if (isAutoDismiss) {
                dismiss()
            }
            if (!binding.etHost.text.toString().isNullOrEmpty() && !binding.etPost.text.toString().isNullOrEmpty() && !binding.etName.toString()
                    .isNullOrEmpty() && !binding.etPsw.toString().isNullOrEmpty()
            ) {
                onBtnClick?.invoke(
                    binding.etHost.text.toString(),
                    binding.etPost.text.toString().toInt(),
                    binding.etName.text.toString(),
                    binding.etPsw.text.toString(),
                    binding.etMount.text.toString()
                )
            }
        }
        RTKConstans.NTRIP_RTK_HOST = SDKStorage.getStringValue(SP_NTRIP_RTK_HOST, RTKConstans.NTRIP_RTK_HOST).toString()
        RTKConstans.NTRIP_RTK_PORT = SDKStorage.getIntValue(SP_NTRIP_RTK_PORT, RTKConstans.NTRIP_RTK_PORT)
        RTKConstans.NTRIP_RTK_ACCOUNT = SDKStorage.getStringValue(SP_NTRIP_RTK_ACCOUNT, RTKConstans.NTRIP_RTK_ACCOUNT).toString()
        RTKConstans.NTRIP_RTK_PWD = SDKStorage.getStringValue(SP_NTRIP_RTK_PWD, RTKConstans.NTRIP_RTK_PWD).toString()
        RTKConstans.NTRIP_RTK_MOUNT_POINT = SDKStorage.getStringValue(SP_NTRIP_RTK_MOUNT_POINT, RTKConstans.NTRIP_RTK_MOUNT_POINT).toString()
        binding.etHost.setText(RTKConstans.NTRIP_RTK_HOST)
        binding.etPost.setText(RTKConstans.NTRIP_RTK_PORT.toString())
        binding.etName.setText(RTKConstans.NTRIP_RTK_ACCOUNT)
        binding.etPsw.setText(RTKConstans.NTRIP_RTK_PWD)
        binding.etMount.setText(RTKConstans.NTRIP_RTK_MOUNT_POINT)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setAutoDismiss(isAutoDismiss: Boolean) {
        this.isAutoDismiss = isAutoDismiss
    }

    fun setOnConfirmListener(listener: (String, Int, String, String, String) -> Unit) {
        this.onBtnClick = listener
    }

}