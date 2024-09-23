package com.autel.sdk.debugtools

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.autel.drone.demo.databinding.SdkLayoutSingleButtonDialogBinding

/**
 * 单按钮操作操作窗口
 * Copyright: Autel Robotics
 * @author maowei on 2022/9/9
 */
class SDKSingleButtonDialog(context: Context) : SDKBaseDialog(context) {

    private val binding = SdkLayoutSingleButtonDialogBinding.inflate(LayoutInflater.from(context))

    /** 是否自动关闭弹窗 */
    private var isAutoDismiss = true
    private var onBtnClick: (() -> Unit)? = null

    init {
        binding.tvConfirm.setOnClickListener {
            if (isAutoDismiss) {
                dismiss()
            }
            onBtnClick?.invoke()
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
        binding.tvMsg.text = msg
    }

    fun setButtonText(text: String) {
        binding.tvConfirm.text = text
    }

    fun setMessageGravity(gravity: Int) {
        binding.tvMsg.gravity = gravity
    }

    fun setAutoDismiss(isAutoDismiss: Boolean) {
        this.isAutoDismiss = isAutoDismiss
    }

    fun setOnConfirmListener(listener: () -> Unit) {
        this.onBtnClick = listener
    }
}