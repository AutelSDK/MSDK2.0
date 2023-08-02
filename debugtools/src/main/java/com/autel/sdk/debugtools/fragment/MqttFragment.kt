package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.autel.drone.sdk.libbase.error.IAutelCode
import com.autel.drone.sdk.store.SDKStorage
import com.autel.drone.sdk.vmodelx.manager.DeviceManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.utils.ToastUtils
import com.autel.sdk.debugtools.beans.*
import com.autel.sdk.debugtools.beans.MQTTConstans.SP_MQTT_ACCOUNT
import com.autel.sdk.debugtools.beans.MQTTConstans.SP_MQTT_HOST
import com.autel.sdk.debugtools.beans.MQTTConstans.SP_MQTT_PWD
import com.autel.sdk.debugtools.databinding.FragmentMqttLoginBinding
import kotlinx.coroutines.launch

/**
 * com.autel.sdk.debugtools.fragment
 *
 * Copyright: Autel Robotics
 *
 * @author R22711 on 2023/6/3.
 */
class MqttFragment: AutelFragment() {
    private lateinit var binding: FragmentMqttLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMqttLoginBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        MQTTConstans.MQTT_HOST = SDKStorage.getStringValue(SP_MQTT_HOST, MQTTConstans.MQTT_HOST).toString()
        MQTTConstans.MQTT_ACCOUNT =  SDKStorage.getStringValue(SP_MQTT_ACCOUNT,
            MQTTConstans.MQTT_ACCOUNT).toString()
        MQTTConstans.MQTT_PWD = SDKStorage.getStringValue(SP_MQTT_PWD, MQTTConstans.MQTT_PWD).toString()
    }

    private fun initView() {
        binding.etHost.setText(MQTTConstans.MQTT_HOST)
        binding.etName.setText(MQTTConstans.MQTT_ACCOUNT)
        binding.etPsw.setText(MQTTConstans.MQTT_PWD)

        binding.tvConfirm.setOnClickListener {
            lifecycleScope.launch {
                DeviceManager.getFirstDroneDevice()?.getRtkManager()?.loginMqtt(
                    binding.etHost.text.toString(),
                    binding.etName.text.toString(),
                    binding.etPsw.text.toString(),
                    object : CommonCallbacks.CompletionCallback {
                        override fun onSuccess() {
                            ToastUtils.showToast("登录成功")
                        }

                        override fun onFailure(error: IAutelCode, msg: String?) {
                            ToastUtils.showToast("登录失败,$msg")
                        }
                    })
            }

        }
    }


}