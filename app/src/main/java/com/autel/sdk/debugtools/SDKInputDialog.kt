package com.autel.sdk.debugtools

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.autel.drone.demo.databinding.SdkLayoutInputDialogBinding

/**
 * 通用输入框弹窗
 * Copyright: Autel Robotics
 * @author maowei on 2022/9/9.
 */
class SDKInputDialog(context: Context) : SDKBaseDialog(context) {

    private val binding = SdkLayoutInputDialogBinding.inflate(LayoutInflater.from(context))

    /** 是否自动关闭弹窗 */
    private var isAutoDismiss = true
    private var onBtnClick: ((String) -> Unit)? = null

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
            onBtnClick?.invoke(binding.etMsg.text.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setMessage(msg: String) {
        binding.etMsg.setText(msg)
    }

    fun setInputType(type: Int) {
        binding.etMsg.inputType = type
    }

    fun setButtonText(text: String) {
        binding.tvConfirm.text = text
    }

    fun setAutoDismiss(isAutoDismiss: Boolean) {
        this.isAutoDismiss = isAutoDismiss
    }

    fun setOnConfirmListener(listener: (String) -> Unit) {
        this.onBtnClick = listener
    }
}