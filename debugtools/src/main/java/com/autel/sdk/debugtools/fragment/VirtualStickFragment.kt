package com.autel.sdk.debugtools.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.autel.drone.sdk.log.SDKLog.i
import com.autel.drone.sdk.vmodelx.manager.NestModelManager
import com.autel.drone.sdk.vmodelx.utils.ToastUtils
import com.autel.sdk.debugtools.databinding.FragmentVirtualStickBinding

/**
 * 虚拟摇杆示例
 */
class VirtualStickFragment : AutelFragment() {

    private var binding: FragmentVirtualStickBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVirtualStickBinding.inflate(inflater)
        initView()
        return binding?.root
    }

    companion object {
        private const val TAG = "VirtualStickFragment"
    }

    private fun initView() {
        i(TAG, "initView...")
        binding?.btnSend?.setOnClickListener {
            val etRaiseOrDownValue = binding?.etRise?.text.toString()
            val etTurnLeftRightValue = binding?.etTurnLeftRight?.text.toString()
            val etForwardOrBackwardValue = binding?.etForwardOrBackward?.text.toString()
            val etGoLeftOrRightValue = binding?.etGoLeftOrRight?.text.toString()
            if (TextUtils.isEmpty(etRaiseOrDownValue)
                || TextUtils.isEmpty(etTurnLeftRightValue)
                || TextUtils.isEmpty(etForwardOrBackwardValue)
                || TextUtils.isEmpty(etGoLeftOrRightValue)
            ) {
                ToastUtils.showToast("请输入杆量!")
                return@setOnClickListener
            }
            NestModelManager.getInstance()
                .updateVirtualJoystick(
                    etRaiseOrDownValue.toInt(),
                    etTurnLeftRightValue.toInt(),
                    etForwardOrBackwardValue.toInt(),
                    etGoLeftOrRightValue.toInt()
                )
        }

    }
}