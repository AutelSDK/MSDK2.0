package com.autel.sdk.debugtools;


import com.autel.drone.sdk.log.SDKLog;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.AIServiceKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.AITrackingKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.AccessoriesProxyKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.AccurateRetakeKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.AirLinkKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.AutonomyKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.CameraKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.CloudApiKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.CommandCenterKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.CommonKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.FlightControlKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.FlightMissionKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.FlightPropertyKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.GimbalKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.HardwareDataSecurityKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.LteModuleKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.MissionManagerKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.MqttPropertyKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.NestKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.NtripAccountKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.PayloadKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.RadarPropertyKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.RemoteControllerKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.RemoteIDKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.RtcKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.RtkPropertKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.RtmpKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.SystemManagerKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.VisionKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.WifiKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelActionKeyInfo;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKeyInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Key items generator data utils class
 * Copyright: Autel Robotics
 *
 * @author maowei on 2022/12/17.
 */

public class KeyItemDataUtil {
    private static final String TAG = KeyItemDataUtil.class.getSimpleName();
    private static final String KEY_RREFIX = "Key";

    private KeyItemDataUtil() {
        //dosomething
    }



    public static void initAirlinkKeyList(List<KeyItem<?, ?>> keylist) {

        initList(keylist, AirLinkKey.class);
    }

    public static void initAIServiceKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, AIServiceKey.class);
    }


    public static void initAccurateRetakeKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, AccurateRetakeKey.class);
    }


    public static void initGimbalKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, GimbalKey.class);
    }

    public static void initCameraKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, CameraKey.class);
    }

    public static void initSystemManagerKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, SystemManagerKey.class);
    }

    public static void initFlightParamsKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, FlightPropertyKey.class);
    }

    public static void initFlightControllerKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, FlightControlKey.class);
    }

    public static void initRemoteControllerKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, RemoteControllerKey.class);
    }

    public static void initBleKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initProductKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initRtkBaseStationKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initRtkMobileStationKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initOcuSyncKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initVisionKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, VisionKey.class);
    }

    public static void initRtkKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, RtkPropertKey.class);
    }


    public static void initWifiKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, WifiKey.class);
    }
    public static void initRadarKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, RadarPropertyKey.class);
    }
    public static void initNtripKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, NtripAccountKey.class);
    }

    public static void initLteModuleList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, LteModuleKey.class);
    }
    public static void initMqttKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, MqttPropertyKey.class);
    }

    public static void initAppKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initMobileNetworkKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initMobileNetworkLinkRCKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initOnboardKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initPayloadKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initLidarKeyList(List<KeyItem<?, ?>> keyList) {
    }

    public static void initMissionKeyList(List<KeyItem<?, ?>> keyList) {

        initList(keyList, FlightMissionKey.class);
    }

    public static void initRemoteIdKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, RemoteIDKey.class);
    }

    public static void initTrackingKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, AITrackingKey.class);
    }

    public static void initCommonUploadKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, CommonKey.class);
    }

    public static void initNestKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, NestKey.class);
    }

    public static void initMissionManagerKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, MissionManagerKey.class);
    }
    public static void initAccessoriesKeyListList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, AccessoriesProxyKey.class);
    }

    public static void initCloudAPiKeyListList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, CloudApiKey.class);
    }

    public static void initHardwareDataSecurityListList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, HardwareDataSecurityKey.class);
    }

    public static void initRtmpKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, RtmpKey.class);
    }
    public static void initCommandCenterKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, CommandCenterKey.class);
    }

    public static void initRtcKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, RtcKey.class);
    }

    public static void initPayloadKeyKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, PayloadKey.class);
    }

    /**
     * 获取本类及其父类的属性的方法
     *
     * @param clazz 当前类对象
     * @return 字段数组
     */
    private static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }


    public static boolean checkList(List<KeyItem<?, ?>> keyList) {
        return keyList != null && !keyList.isEmpty();
    }

    /**
     * create key list
     *
     * @param keylist pass list for add more items
     * @param clazz   for botaining this class and methods
     */

    private static void initList(List<KeyItem<?, ?>> keylist, Class<?> clazz) {
        if (checkList(keylist)) {
            return;
        }
        Field[] fields = getAllFields(clazz);
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(null);
                Class<?> fieldClass = field.getType();
                String keyName = field.getName();

                if (fieldClass.equals(AutelKeyInfo.class) || fieldClass.equals(AutelActionKeyInfo.class)) {
                    AutelKeyInfo<?> keyInfo = (AutelKeyInfo<?>) fieldValue;
                    KeyItem<?, ?> item = new KeyItem(keyInfo);
                    genericItem(item, keyInfo, keyName);

                    if (keylist != null) {
                        keylist.add(item);
                    }
                }
            } catch (Exception e) {
                SDKLog.e(TAG, Objects.requireNonNull(e.getMessage()));
            }

        }
    }

    /**
     * generating item key for with prefix or item it self
     *
     * @param item    for item object
     * @param keyInfo key info for get identifier
     * @param keyName name of key
     */

    public static void genericItem(KeyItem<?, ?> item, AutelKeyInfo<?> keyInfo, String keyName) {

        try {
            if (keyInfo.getIdentifier().isEmpty()) {
                item.setName(keyName.contains(KEY_RREFIX) ? keyName.substring(KEY_RREFIX.length()) : keyName);
            }
        } catch (Exception e) {
            SDKLog.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

    }

    public static void initAutonomyKeyList(List<KeyItem<?, ?>> keyList) {
        initList(keyList, AutonomyKey.class);
    }
}
