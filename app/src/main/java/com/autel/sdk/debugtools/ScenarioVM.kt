package com.autel.sdk.debugtools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.manager.DeviceManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.*
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.KeyTools
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.bean.RecordParametersBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.bean.TakePhotoParametersBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.CalibrationCommandBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.CalibrationEventBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.CalibrationScheduleBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CalibrationEventEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CalibrationTypeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CmdEnum
import com.autel.sdk.debugtools.helper.KeyManagerCoroutineWrapper
import com.autel.sdk.debugtools.helper.SdkFailureResultException
import com.autel.sdk.debugtools.uploadMsg.SdkDeviceNonExistException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * scenario testing video model to load data in background
 * Copyright: Autel Robotics
 * @author maowei on 2022/12/17.
 */
class ScenarioVM : AutelViewModel() {
    companion object {
        const val TAG = "ScenarioVM"
    }

    private var currentCalType: CalibrationTypeEnum = CalibrationTypeEnum.UNKNOWN

    /**
     * Start paring scenario
     */
    suspend fun startMatching(onSuccess: () -> Unit) {
        return suspendCancellableCoroutine {
            val keyManager = getRemoteKeyManager()
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                if (throwable is SdkFailureResultException) it.resumeWithException(throwable)
            }) {
                val key = KeyTools.createKey(AirLinkKey.KeyALinkStartMatching)
                keyManager?.let {KeyManagerCoroutineWrapper.performAction(it, key) }
                onSuccess.invoke()
            }
        }
    }

    /**
     * Exit the paring scenario
     */
    fun endMatching(){
//        val keyManager = getRemoteKeyManager()
//        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
//        }) {
//            val key = KeyTools.createKey(AirLinkKey.KeyALinkStartMatching)
//            keyManager?.let { KeyManagerCoroutineWrapper.performAction(it, key) }
//        }
    }


    /**
     * Start RC calibration scenario
     */
    suspend fun enterControllerCalibration(onSuccess: () -> Unit) {
        return suspendCancellableCoroutine {
            val keyManager = DeviceManager.getDeviceManager().getFirstRemoteDevice()?.getKeyManager()
            viewModelScope.launch(CoroutineExceptionHandler { _, throwbale ->
                if(throwbale is SdkFailureResultException)
                    it.resumeWithException(throwbale)
            }){
                val key = KeyTools.createKey(RemoteControllerKey.KeyRCEnterCalibration)
                keyManager?.let { it -> KeyManagerCoroutineWrapper.performAction(it, key) }
                onSuccess.invoke()
            }
        }
    }

    /**
     * Exit RC calibration scenario
     */
     fun exitControllerCalibration(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            onError.invoke(throwable)
        }) {
            val key = KeyTools.createKey(RemoteControllerKey.KeyRCExitCalibration)
            getRemoteKeyManager()?.let { KeyManagerCoroutineWrapper.performAction(it, key) }
            onSuccess.invoke()
        }
    }


    /**
     *Start Photo test scenario
     */
    fun startTakingPhoto( onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val keyManager = DeviceManager.getDeviceManager().getFirstDroneDevice()?.getKeyManager()
        if (keyManager == null) {
            onError.invoke(SdkDeviceNonExistException())
        } else {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                onError.invoke(throwable)
            }) {
                val key = KeyTools.createKey(CameraKey.KeyStartTakePhoto)
                KeyManagerCoroutineWrapper.performAction(keyManager, key, null)
                onSuccess.invoke()
            }
        }
    }

    /**
     * After photo is taken the data is stored in takePhotoParametersBean and then this bean is observed
     * for data.
     */
    val takePhotoParametersBean = MutableLiveData<TakePhotoParametersBean>()

    /**
     * To set the Photo Scenario data in takePhotoParametersBean
     */
    fun getTakePhotoParameters(onError: (Throwable) -> Unit) {
        val keyManager = DeviceManager.getDeviceManager().getFirstDroneDevice()?.getKeyManager()
        if (keyManager == null) {
            onError.invoke(SdkDeviceNonExistException())
        } else {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                onError.invoke(throwable)
            }) {
                val key = KeyTools.createKey(CameraKey.KeyTakePhotoParameters)
                takePhotoParametersBean.value =
                    KeyManagerCoroutineWrapper.getValue(keyManager, key)
            }
        }
    }

    /**
     * Video test scenario
     * Starts the recording and then puts value in a RecordParametersBean
     */
    fun startRecording(onSuccess: () -> Unit, onError: (Throwable) -> Unit){
        val keyManager = DeviceManager.getDeviceManager().getFirstDroneDevice()?.getKeyManager()
        if (keyManager == null) {
            onError.invoke(SdkDeviceNonExistException())
        } else {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                onError.invoke(throwable)
            }) {
                val key = KeyTools.createKey(CameraKey.KeyStartRecord)
                KeyManagerCoroutineWrapper.performAction(keyManager, key, null)
                onSuccess.invoke()
            }
        }
    }

    val _recordParametersBean = MutableLiveData<RecordParametersBean>()
    fun getRecordParameters(onError: (Throwable) -> Unit) {
        val keyManager = DeviceManager.getDeviceManager().getFirstDroneDevice()?.getKeyManager()
        if (keyManager == null) {
            onError.invoke(SdkDeviceNonExistException())
        } else {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                onError.invoke(throwable)
            }) {
                val key = KeyTools.createKey(CameraKey.KeyRecordParameters)
                _recordParametersBean.value =
                    KeyManagerCoroutineWrapper.getValue(keyManager, key)
            }
        }
    }

    suspend fun exitRecord(onSuccess: () -> Unit) {
        return suspendCancellableCoroutine {
            val keyManager = DeviceManager.getDeviceManager().getFirstDroneDevice()?.getKeyManager()
            viewModelScope.launch(CoroutineExceptionHandler { _, throwbale ->
                if (throwbale is SdkFailureResultException)
                    it.resumeWithException(throwbale)
            }) {
                val key = KeyTools.createKey(CameraKey.KeyStopRecord)
                keyManager?.let { it -> KeyManagerCoroutineWrapper.performAction(it, key) }
                onSuccess.invoke()
            }
        }
    }

    /**
     * start for IMU Calibration, Gimbal Calibration, Compass Calibration
     * @param calType - tells the type of calibration to be performed
     * @param onSuccess - callback for success
     * @param onError - callback for failure
     * @see com.autel.setting.business.SettingCalibrationVM.startCalibration
     */
    fun startCalibration(calType: CalibrationTypeEnum, onSuccess: (Boolean) -> Unit, onError: (Throwable) -> Unit) {
        currentCalType = calType
        listenCalibrationStatus()
        listenCalibrationStep()
        val keyManager = getKeyManager()
        if (keyManager == null) {
            onError.invoke(SdkDeviceNonExistException())
        } else {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                onError.invoke(throwable)
            }) {
                val key = KeyTools.createKey(CommonKey.KeyDroneCalibrationCommand)
                KeyManagerCoroutineWrapper.performAction(
                    getKeyManager()!!,
                    key,
                    CalibrationCommandBean(calType, CmdEnum.START)
                )
                onSuccess.invoke(true)
            }
        }
    }

    /**
     * listening to calibration status
     */
    private fun listenCalibrationStatus() {
        val key = KeyTools.createKey(CommonKey.KeyDroneCalibrationEventNtfy)
        getKeyManager()?.listen(key, listenCalibrationStatus)
    }

    /**
     * Listen for calibration steps
     */
    private fun listenCalibrationStep() {
        val key = KeyTools.createKey(CommonKey.KeyDroneCalibrationScheduleNtfy)
        getKeyManager()?.listen(key, listenCalibrationStep)
    }

    /**
     * Calibration status
     */
    private val _calibrationStatus = MutableLiveData<CalibrationEventEnum>()
    val calibrationStatus: MutableLiveData<CalibrationEventEnum> = _calibrationStatus

    /**
     * Calibrate the loading monitor
     */
    private val listenCalibrationStatus = object : CommonCallbacks.KeyListener<CalibrationEventBean> {
        override fun onValueChange(oldValue: CalibrationEventBean?, newValue: CalibrationEventBean) {
            SDKLog.d(TAG, "status ===========>>$newValue")
            if (newValue.calibrationType == currentCalType) {
                _calibrationStatus.value = newValue.calibrationEvent
            }
        }
    }

    /**
     * Calibration steps
     */
    private val _calibrationStep = MutableLiveData<CalibrationScheduleBean>()
    val calibrationStep: MutableLiveData<CalibrationScheduleBean> = _calibrationStep


    /**
     * Calibration steps listen
     */
    private val listenCalibrationStep = object : CommonCallbacks.KeyListener<CalibrationScheduleBean> {
        override fun onValueChange(oldValue: CalibrationScheduleBean?, newValue: CalibrationScheduleBean) {
            SDKLog.d(TAG, "step ========>$newValue")
            if (newValue.calibrationType == currentCalType) {
                _calibrationStep.value =
                    CalibrationScheduleBean(newValue.imcStep, newValue.compassStep, newValue.calibrationPercent)
            }
        }
    }

}