package com.autel.sdk.debugtools.uploadMsg


import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.*
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKey
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.KeyTools
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.alink.enums.AirLinkMatchStatusEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.bean.*
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.AFStateEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.DisplayModeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.ExposureEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.RCBandInfoTypeBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.RockerCalibrationStateNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.bean.WarningAtom
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.DroneSystemStateHFNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.DroneSystemStateLFNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.DroneWarningStateNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.flight.bean.FlightControlStatusInfo
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.gimbal.bean.DroneGimbalStateBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.mission.bean.MissionWaypointStatusReportNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.remotecontrol.bean.HardwareButtonInfoBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.remotecontrol.bean.RCHardwareStateNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.remotecontrol.bean.RCStateNtfyBean
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.vision.bean.VisionRadarInfoBean

/**
 * uploading messages passing key identifiers
 * Copyright: Autel Robotics
 * @author xulc on 2022/10/11
 */
object UploadMsgKeyTools {
    /**---------------------------飞控--------------------------------*/
    /**
     * 飞控高频消息
     */
    val KeyFlightControlSystemHFState: AutelKey<DroneSystemStateHFNtfyBean> = KeyTools.createKey(
        CommonKey.KeyDroneSystemStatusHFNtfy
    )

    /**
     * 飞控低频消息
     */
    val KeyFlightControlSystemLFState: AutelKey<DroneSystemStateLFNtfyBean> = KeyTools.createKey(
        CommonKey.KeyDroneSystemStatusLFNtfy
    )

    /**
     * 飞机工作状态信息
     */
    val KeyDroneWorkStatusInfoReport: AutelKey<FlightControlStatusInfo> = KeyTools.createKey(
        CommonKey.KeyDroneWorkStatusInfoReport
    )

    /**
     * 飞控告警消息
     */
    val KeyFlightStateWarning: AutelKey<DroneWarningStateNtfyBean> = KeyTools.createKey(
        CommonKey.KeyDroneWarningMFNtfy
    )

    //飞机告警通知
    val KeyDroneWarning: AutelKey<List<WarningAtom>> = KeyTools.createKey(
        CommonKey.KeyDroneWarning
    )

    //飞机实时告警通知
    val KeyDroneRuntimeWarning: AutelKey<WarningAtom> = KeyTools.createKey(
        CommonKey.KeyDroneRuntimeWarning
    )

    /**---------------------------任务--------------------------------*/
    /**
     * 任务执行信息
     */
    val KeyStatusReportNtfy: AutelKey<MissionWaypointStatusReportNtfyBean> = KeyTools.createKey(
        FlightMissionKey.KeyStatusReportNtfy
    )


    /**---------------------------相机--------------------------------*/
    /**
     * 相机消息上报
     */
    val KeyCameraStatus: AutelKey<CameraStatusBean> = KeyTools.createKey(CameraKey.KeyCameraStatus)

    /**
     * 拍照文件信息上报
     */
    val KeyPhotoFileInfo: AutelKey<PhotoFileInfoBean> =
        KeyTools.createKey(CameraKey.KeyPhotoFileInfo)

    /**
     * 拍照状态上报
     */
    val KeyTakePhotoStatus: AutelKey<TakePhotoStatusBean> =
        KeyTools.createKey(CameraKey.KeyTakePhotoStatus)

    /**
     * 录像状态上报
     */
    val KeyRecordStatus: AutelKey<RecordStatusBean> = KeyTools.createKey(CameraKey.KeyRecordStatus)

    /**
     * 录像文件信息上报
     */
    val KeyRecordFileInfo: AutelKey<RecordFileInfoBean> =
        KeyTools.createKey(CameraKey.KeyRecordFileInfo)

    /**
     * SD卡状态上报
     */
    val KeySDCardStatus: AutelKey<CardStatusBean> = KeyTools.createKey(CameraKey.KeySDCardStatus)

    /**
     * MMC状态上报
     */
    val KeyMMCStatusInfo: AutelKey<CardStatusBean> = KeyTools.createKey(CameraKey.KeyMMCStatusInfo)

    /**
     * 定时拍倒计时上报
     */
    val KeyIntervalShotStatus: AutelKey<Int> = KeyTools.createKey(CameraKey.KeyIntervalShotStatus)

    /**
     * 全景状态上报
     */
    val KeyPanoramaStatus: AutelKey<PanoramaStatusBean> =
        KeyTools.createKey(CameraKey.KeyPanoramaStatus)

    /**
     * 延时摄影状态上报
     */
    val KeyDelayShotStatus: AutelKey<DelayShotStatusBean> =
        KeyTools.createKey(CameraKey.KeyDelayShotStatus)

    /**
     * 重置相机状态上报
     */
    val KeyResetCameraState: AutelKey<Boolean> = KeyTools.createKey(CameraKey.KeyResetCameraState)

    /**
     * AE/AF状态改变
     */
    val KeyAeAfStatusChange: AutelKey<CameraAFAEStatusBean> =
        KeyTools.createKey(CameraKey.KeyAeAfStatusChange)

    /**
     * 温度报警事件
     */
    val KeyTempAlarm: AutelKey<TempAlarmBean> = KeyTools.createKey(CameraKey.KeyTempAlarm)

    /**
     * 移动延时摄影状态
     */
    val KeyMotionDelayShotStatus: AutelKey<MotionDelayShotBean> =
        KeyTools.createKey(CameraKey.KeyMotionDelayShotStatus)

    /**
     * 相机曝光状态
     */
    val KeyPhotoExposure: AutelKey<ExposureEnum> = KeyTools.createKey(CameraKey.KeyPhotoExposure)

    /**
     * 任务录制航点信息上报
     */
    val KeyMissionRecordWaypoint: AutelKey<MissionRecordWaypointBean> =
        KeyTools.createKey(CameraKey.KeyMissionRecordWaypoint)

    /**
     * 相机对焦状态
     */
    val KeyAFState: AutelKey<AFStateEnum> = KeyTools.createKey(CameraKey.KeyAFState)

    /**
     * 相机模式切换上报
     */
    val KeyCameraModeSwitch: AutelKey<CameraModeSwitchBean> =
        KeyTools.createKey(CameraKey.KeyCameraModeSwitch)

    /**
     * 显示模式切换上报
     */
    val KeyDisplayModeSwitch: AutelKey<DisplayModeEnum> =
        KeyTools.createKey(CameraKey.KeyDisplayModeSwitch)

    /**
     * 红外相机温度信息上报
     */
    val KeyInfraredCameraTempInfo: AutelKey<InfraredCameraTempInfoBean> = KeyTools.createKey(
        CameraKey.KeyInfraredCameraTempInfo
    )

    /**
     * 专业参数信息上报
     */
    val KeyProfessionalParamInfo: AutelKey<ProfessionalParamInfoBean> =
        KeyTools.createKey(CameraKey.KeyProfessionalParamInfo)

    /**
     * 存储状态信息上报
     */
    val KeyStorageStatusInfo: AutelKey<StorageStatusInfoBean> =
        KeyTools.createKey(CameraKey.KeyStorageStatusInfo)

    /**
     * 点测光上报
     */
    val KeyLocationMeterInfo: AutelKey<MeteringPointBean> =
        KeyTools.createKey(CameraKey.KeyLocationMeterInfo)

    /**
     * 白平衡参数上报
     */
    val KeyWhiteBalanceNtfy: AutelKey<WhiteBalanceBean> =
        KeyTools.createKey(CameraKey.KeyWhiteBalanceNtfy)

    /**
     * AELock信息上报
     */
    val KeyAeLockNtfy: AutelKey<Boolean> = KeyTools.createKey(CameraKey.KeyAeLockNtfy)

    /**
     * HDR信息上报
     */
    val KeyHdrNtfy: AutelKey<Boolean> = KeyTools.createKey(CameraKey.KeyHdrNtfy)

    /**
     * ROI配置上报
     */
    val KeyRoiNtfy: AutelKey<ROIBean> = KeyTools.createKey(CameraKey.KeyRoiNtfy)

    /**
     * 相机拍照分辨率信息上报
     */
    val KeyPhotoResolutionNtfy: AutelKey<PhotoResolutionEnum> =
        KeyTools.createKey(CameraKey.KeyPhotoResolutionNtfy)

    /**
     * 相机录像分辨率信息上报
     */
    val KeyVideoResolutionNtfy: AutelKey<VideoResolutionBean> =
        KeyTools.createKey(CameraKey.KeyVideoResolutionNtfy)

    /**---------------------------云台--------------------------------*/

    /**
     * 云台上报参数
     */
    val KeyGimbalHeatBeat: AutelKey<DroneGimbalStateBean> =
        KeyTools.createKey(GimbalKey.KeyHeatBeat)

    /**---------------------------视觉--------------------------------*/

    /**
     * 视觉告警
     */
    val KeyWarning: AutelKey<List<VisionRadarInfoBean>> =
        KeyTools.createKey(VisionKey.KeyReportEmergency)

    /**---------------------------遥控器--------------------------------*/

    /**
     * 遥控器定频上报
     */
    val KeyRCHardwareStateReport: AutelKey<RCHardwareStateNtfyBean> =
        KeyTools.createKey(RemoteControllerKey.KeyRCHardwareState)

    /**
     * 遥控器自定义按钮上报参数
     */
    val KeyRCHardwareButtonInfo: AutelKey<HardwareButtonInfoBean> =
        KeyTools.createKey(RemoteControllerKey.KeyRCHardwareInfo)

    /**
     * 遥控器状态上报参数
     */
    val KeyRCState: AutelKey<RCStateNtfyBean> = KeyTools.createKey(RemoteControllerKey.KeyRCState)

    /**
     * 遥控器校准状态上报
     */
    val KeyRCRockerCalibrationState: AutelKey<RockerCalibrationStateNtfyBean> =
        KeyTools.createKey(RemoteControllerKey.KeyRCRockerCalibrationState)

    /**
     * 遥控器对频
     */
    val keyLinkMatch: AutelKey<AirLinkMatchStatusEnum> =
        KeyTools.createKey(AirLinkKey.KeyALinkMatchingStatus)

    /**
     *  遥控器带宽、类型上报
     */
    val KeyRCBandInfoType: AutelKey<RCBandInfoTypeBean> =
        KeyTools.createKey(RemoteControllerKey.KeyRCBandInfoTypeNtfy)
}