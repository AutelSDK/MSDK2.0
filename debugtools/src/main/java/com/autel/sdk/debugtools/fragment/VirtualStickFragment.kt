package com.autel.sdk.debugtools.fragment


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.autel.drone.sdk.libbase.error.IAutelCode
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.pbprotocol.constants.FightParamKey
import com.autel.drone.sdk.vmodelx.manager.DeviceManager
import com.autel.drone.sdk.vmodelx.manager.NestModelManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.converter.AutelFloatConvert
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKeyInfo
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.KeyTools
import com.autel.drone.sdk.vmodelx.utils.ToastUtils
import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.databinding.FragmentVirtualStickBinding
import com.autel.sdk.debugtools.view.virtualstick.JoystickView
import com.autel.sdk.debugtools.view.virtualstick.OnJoystickListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * 虚拟摇杆示例
 */
class VirtualStickFragment : AutelFragment() {
    private val deviation: Double = 0.02
    private var currentLevel = 1
    private var binding: FragmentVirtualStickBinding? = null

    private val handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            1 -> {
                updateLocationInfo()
            }
        }
        return@Handler true
    }

    /**
     * 低速档：满杆量（水平速度）前3后3，左右3，（垂直速度）上升3，下降3. 偏航 90度
     * 舒适档：满杆量（水平速度）前后10，左右10，（垂直速度）上升5，下降4. 偏航 90度
     * 标准档：满杆量（水平速度）前后15，左右10，（垂直速度）上升6，下降6.  偏航 90度
     * 狂暴档：满杆量（水平速度）前23后18，左右20，（垂直速度）上升6，下降6. 偏航 120度
     */
    private val speedLevel = arrayOf(
        arrayOf(3, 3, 3, 3, 3, 3, 90),
        arrayOf(10, 10, 10, 10, 5, 4, 90),
        arrayOf(15, 15, 10, 10, 6, 6, 90),
        arrayOf(23, 18, 20, 20, 6, 6, 120)
    )

    private val stickValue = arrayOf(0f, 0f, 0f, 0f)

    private val joystickUpdate: MutableStateFlow<Long> = MutableStateFlow(0)

    private var fullLevelInfo: Array<String>? = null

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
        SDKLog.i(TAG, "initView...")
        setFloatValue(FightParamKey.SIM_LON, -122.3321f) //113.99777f
        setFloatValue(FightParamKey.SIM_LAT, 47.6062f) //22.595598f

        fullLevelInfo = resources.getStringArray(R.array.debug_stick_level_speed)
        binding?.spinner?.setSelection(currentLevel)
        binding?.levelFullInfo?.text = fullLevelInfo?.get(currentLevel) ?: ""
        binding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentLevel = position
                binding?.levelFullInfo?.text = fullLevelInfo?.get(currentLevel) ?: ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding?.leftStickView?.setJoystickListener(object : OnJoystickListener {
            override fun onTouch(joystick: JoystickView?, pX: Float, pY: Float) {
                stickValue[0] = 0F
                stickValue[1] = 0F
                if (abs(pX) >= deviation) {
                    stickValue[0] = pX
                }
                if (abs(pY) >= deviation) {
                    stickValue[1] = pY
                }
                joystickUpdate.tryEmit(System.currentTimeMillis())
                SDKLog.d(TAG, "leftStickView: ${stickValue[0]}, ${stickValue[1]}")
            }
        })
        binding?.rightStickView?.setJoystickListener(object : OnJoystickListener {
            override fun onTouch(joystick: JoystickView?, pX: Float, pY: Float) {
                stickValue[2] = 0F
                stickValue[3] = 0F
                if (abs(pX) >= deviation) {
                    stickValue[2] = pX
                }
                if (abs(pY) >= deviation) {
                    stickValue[3] = pY
                }
                joystickUpdate.tryEmit(System.currentTimeMillis())
                SDKLog.d(TAG, "rightStickView: ${stickValue[2]}, ${stickValue[3]}")
            }
        })

        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
        }) {
            joystickUpdate.collect {
                SDKLog.d(TAG, "collect: ${stickValue.joinToString(",")}")
                sendVirtualJoystickData(stickValue[0], stickValue[1], stickValue[2], stickValue[3])
            }
        }
        handler.sendEmptyMessageDelayed(1, 2000L)
    }

    @SuppressLint("DefaultLocale")
    private fun sendVirtualJoystickData(leftX: Float, leftY: Float, rightX: Float, rightY: Float) {
        val raiseOrDownValue =
            if (leftY > 0) leftY * speedLevel[currentLevel][4] else leftY * speedLevel[currentLevel][5]
        val turnYawValue = leftX * speedLevel[currentLevel][6]
        val forwardOrBackwardValue =
            if (rightY > 0) rightY * speedLevel[currentLevel][0] else rightY * speedLevel[currentLevel][1]
        val leftOrRightValue =
            if (rightX > 0) rightX * speedLevel[currentLevel][2] else rightX * speedLevel[currentLevel][3]

        handler.post {
            binding?.dataSend?.text = String.format("upOrDown: %.4f\nturnLeftOrRight: %.2f\nforwardOrBackward: %.4f\ngoLeftOrRight: %.4f",
                raiseOrDownValue, turnYawValue, forwardOrBackwardValue, leftOrRightValue)
        }

        NestModelManager.getInstance().updateVirtualJoystick(
            raiseOrDownValue.toInt(),
            turnYawValue.toInt(),
            forwardOrBackwardValue.toInt(),
            leftOrRightValue.toInt()
        )
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun updateLocationInfo() {
        val device = DeviceManager.getDeviceManager().getFirstDroneDevice()
        if (device == null) {
            handler.sendEmptyMessageDelayed(1, 5000L)
            return
        }

        val stateData = device.getStateMachine()
        val altitude = stateData.droneSystemStateHFNtfyBean?.altitude
        val distance = stateData.droneSystemStateHFNtfyBean?.distance
        val xSpeed = stateData.droneSystemStateHFNtfyBean?.velocityX ?: 0f
        val ySpeed = stateData.droneSystemStateHFNtfyBean?.velocityY ?: 0f
        val zSpeed = stateData.droneSystemStateHFNtfyBean?.velocityZ

        val speed = sqrt((xSpeed * xSpeed + ySpeed * ySpeed)) * 10 / 10f

        //val startAngle = roll + pitch
        //val endAngle = 180f + roll - pitch * 2
        val dronePitch = stateData.droneSystemStateHFNtfyBean?.droneAttitude?.getPitchDegree() ?: 0f //俯仰角
        val droneRoll = stateData.droneSystemStateHFNtfyBean?.droneAttitude?.getRollDegree() ?: 0f  //翻滚角/横滚角
        val droneYam = stateData.droneSystemStateHFNtfyBean?.droneAttitude?.getYawDegree() ?: 0f   //偏航角

        val latitude =  stateData.droneSystemStateHFNtfyBean?.droneLatitude
        val longitude =  stateData.droneSystemStateHFNtfyBean?.droneLongitude

        val info = String.format("latitude: %.08f, longitude: %.08f, altitude: %.02f\n" +
                "distance: %.02f, hSpeed: %.02f, vSpeed: %.02f\n" +
                "dronePitch: %.04f, droneRoll: %.04f, droneYam: %.04f",
            latitude, longitude, altitude, distance, speed, zSpeed, dronePitch, droneRoll, droneYam)
        binding?.locationInfo?.text = info
        handler.sendEmptyMessageDelayed(1, 1000)
    }
    private fun setFloatValue(keyName: String, value: Float) {
        val keyInfo: AutelKeyInfo<Float> = AutelKeyInfo(0, keyName, AutelFloatConvert()).canGet(true).canSet(true)
        val autelKey = KeyTools.createKey(keyInfo)
        DeviceManager.getDeviceManager().getFirstDroneDevice()?.getKeyManager()?.setValue(autelKey,
            value, object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                }
                override fun onFailure(code: IAutelCode, msg: String?) {
                    ToastUtils.showToast("${code.code}$msg")
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}