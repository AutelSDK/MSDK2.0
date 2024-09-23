package com.autel.sdk.debugtools

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.autel.drone.demo.databinding.SdkLayoutTwoButtonDialogBinding

/**
 * 双按钮操作操作窗口
 * Copyright: Autel Robotics
 * @author maowei on 2022/9/9
 */
class SDKTwoButtonDialog(context: Context) : SDKBaseDialog(context) {

    private val binding = SdkLayoutTwoButtonDialogBinding.inflate(LayoutInflater.from(context))

    /**
     * 是否自动关闭弹窗
     */
    private var isAutoDismiss = true
    private var onLeftBtnClick: (() -> Unit)? = null
    private var onRightBtnClick: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        binding.tvCancel.setOnClickListener {
            if (isAutoDismiss) {
                dismiss()
            }
            onLeftBtnClick?.invoke()
        }
        binding.tvConfirm.setOnClickListener {
            if (isAutoDismiss) {
                dismiss()
            }
            onRightBtnClick?.invoke()
        }
    }

    fun setMessage(msg: String) {
        binding.tvMsg.text = msg
    }

    fun setRightBtnStr(text: String) {
        binding.tvConfirm.text = text
    }

    fun setLeftBtnStr(text: String) {
        binding.tvCancel.text = text
    }

    fun setAutoDismiss(isAutoDismiss: Boolean) {
        this.isAutoDismiss = isAutoDismiss
    }

    fun setRightBtnListener(listener: () -> Unit) {
        onRightBtnClick = listener
    }

    fun setLeftBtnListener(listener: () -> Unit) {
        onLeftBtnClick = listener
    }
}