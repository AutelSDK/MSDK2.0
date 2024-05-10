package com.autel.sdk.debugtools;

import com.autel.drone.sdk.log.SDKLog;
import com.autel.drone.sdk.vmodelx.SDKManager;
import com.autel.drone.sdk.vmodelx.constants.SDKConstants;
import com.autel.drone.sdk.vmodelx.interfaces.IBaseDevice;
import com.autel.drone.sdk.vmodelx.interfaces.IKeyManager;
import com.autel.drone.sdk.vmodelx.manager.DeviceManager;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelActionKeyInfo;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKeyInfo;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.KeyTools;
import com.autel.drone.sdk.vmodelx.utils.ToastUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Key base structure provider for generated mesage
 * Copyright: Autel Robotics
 *
 * @author huangsihua on 2022/10/29.
 */
public class KeyBaseStructure<P, R> {

    private static final String TAG = KeyBaseStructure.class.getSimpleName();

    /**
     * 设置参数
     */
    protected P param;

    /**
     * 返回结果
     */
    protected R result;

    /**
     * 推送数据记录
     */
    protected String listenRecord = "";

    /**
     * 推送Listener宿主
     */
    protected Object listenHolder;
    protected int componetIndex = -1;
    protected int subComponetType = -1;
    protected int subComponetIndex = -1;
    /**
     * 枚举列表
     */
    protected Map<String, List<EnumItem>> subItemMap = new HashMap<>();

    public int getComponetIndex() {
        return componetIndex;
    }

    public void setComponetIndex(int componetIndex) {
        this.componetIndex = componetIndex;
    }

    public int getSubComponetType() {
        return subComponetType;
    }

    public void setSubComponetType(int subComponetType) {
        this.subComponetType = subComponetType;
    }

    public int getSubComponetIndex() {
        return subComponetIndex;
    }

    public void setSubComponetIndex(int subComponetIndex) {
        this.subComponetIndex = subComponetIndex;
    }

    //    /**
//     * 通过反射获取泛型类型数据并实例化
//     */
    protected void initGenericInstance() {
        try {
            KeyItemHelper.INSTANCE.initClassData(param);
            KeyItemHelper.INSTANCE.initClassData(result);
            initSubItemData();
        } catch (Exception e) {
            SDKLog.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * 如果为枚举或者枚举嵌套列表，则初始化该列表数据
     */
    protected void initSubItemData() {
        if (param == null) {
            return;
        }
        subItemMap.clear();
        subItemMap.putAll(KeyItemHelper.INSTANCE.initSubItemData(param));
    }


    /**
     * 获取需要设置的参数实例
     *
     * @return
     */
    public P getParam() {
        return param;
    }

    /**
     * 获取参数映射列表
     *
     * @return
     */
    public Map<String, List<EnumItem>> getSubItemMap() {
        return subItemMap;
    }

    /**
     * 获取推送数据记录
     *
     * @return
     */
    public String getListenRecord() {
        return listenRecord;
    }


    /**
     * 异步get
     *
     * @param keyInfo
     * @param getCallback
     */
    protected void get(AutelKeyInfo<R> keyInfo, CommonCallbacks.CompletionCallbackWithParam<R> getCallback) {
        AutelKey<R> key = createKey(keyInfo);
        if (getKeyManager(key) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(key).getValue(key, getCallback, 3);
        }
    }

    protected IKeyManager getKeyManager(AutelKey key) {
        if (SDKConstants.INSTANCE.isRemoteControlMode(key.mComponentIndex)) {
            if (DeviceManager.Companion.getDeviceManager().getFirstRemoteDevice() != null) {
                return DeviceManager.Companion.getDeviceManager().getFirstRemoteDevice().getKeyManager();
            } else {
                return null;
            }
        } else if (SDKConstants.INSTANCE.isRCHiddenMode(key.mComponentIndex)) {
            if (DeviceManager.Companion.getDeviceManager().getFirstRCHiddenDevice() != null) {
                return DeviceManager.Companion.getDeviceManager().getFirstRCHiddenDevice().getKeyManager();
            } else {
                return null;
            }
        } else {
            if (getDefaultDrone() != null) {
                return getDefaultDrone().getKeyManager();
            } else {
                return null;
            }
        }
    }

    private IBaseDevice getDefaultDrone() {
        return DeviceManager.Companion.getDeviceManager().getFirstDroneDevice();
    }

    protected void getList(List<AutelKeyInfo<?>> keyInfoList, CommonCallbacks.CompletionCallbackWithParam<List<?>> getCallback) {
        List<AutelKey<?>> keyList = new ArrayList<>();
        for (AutelKeyInfo keyInfo : keyInfoList) {
            AutelKey key = createKey(keyInfo);
            keyList.add(key);
        }
        if (getKeyManager(keyList.get(0)) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(keyList.get(0)).getValueList(keyList, getCallback, 3);
        }
    }

    /**
     * 设置属性
     *
     * @param keyInfo
     * @param setCallback
     */
    protected void set(AutelKeyInfo<P> keyInfo, P param, CommonCallbacks.CompletionCallback setCallback) {
        AutelKey<P> key = createKey(keyInfo);
        if (getKeyManager(key) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(key).setValue(key, param, setCallback, 3);
        }
    }


    /**
     * 设置属性
     *
     * @param keyInfoList
     * @param params
     * @param setCallback
     */
    protected void setList(List<AutelKeyInfo<?>> keyInfoList, List<P> params, CommonCallbacks.CompletionCallbackWithParam<List<?>> setCallback) {
        List<AutelKey<Object>> keyList = new ArrayList<>();
        for (AutelKeyInfo keyInfo : keyInfoList) {
            AutelKey key = createKey(keyInfo);
            keyList.add(key);
        }
        if (getKeyManager(keyList.get(0)) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(keyList.get(0)).setValueList(keyList, params, setCallback);
        }
    }

    /**
     * 设置Listener
     *
     * @param keyInfo
     * @param listenCallback
     */
    protected void listen(AutelKeyInfo<R> keyInfo, CommonCallbacks.KeyListener<R> listenCallback) {
        AutelKey<R> key = createKey(keyInfo);
        if (getKeyManager(key) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(key).listen(key, listenCallback);
        }
    }

    /**
     * 取消Listener
     *
     * @param keyInfo
     */
    protected void cancelListen(AutelKeyInfo<R> keyInfo, CommonCallbacks.KeyListener<R> listenCallback) {
        AutelKey<R> key = createKey(keyInfo);
        if (getKeyManager(key) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(key).cancelListen(key, listenCallback);
        }
    }

    public void cancelAllListen() {
        IBaseDevice device = getDefaultDrone();
        if (device == null || DeviceManager.Companion.getDeviceManager().getFirstRemoteDevice() == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_device_null));
        } else {
//            device.getKeyManager().removeAllListen();
//            DeviceManager.Companion.getDeviceManager().getFirstRemoteDevice().getKeyManager().removeAllListen();
        }
    }

    /**
     * 无参action
     *
     * @param keyInfo
     * @param actonCallback
     */
    protected void action(AutelActionKeyInfo<P, R> keyInfo, CommonCallbacks.CompletionCallbackWithParam<R> actonCallback) {
        AutelKey.ActionKey<P, R> key = createActionKey(keyInfo);
        if (getKeyManager(key) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(key).performAction(key, null, actonCallback, 3, "0");
        }
    }

    /**
     * 带参action
     *
     * @param keyInfo
     * @param param
     * @param actonCallback
     */
    protected void action(AutelActionKeyInfo<P, R> keyInfo, P param, CommonCallbacks.CompletionCallbackWithParam<R> actonCallback) {
        AutelKey.ActionKey<P, R> key = createActionKey(keyInfo);
        if (getKeyManager(key) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(key).performAction(key, param, actonCallback,3, "0");
        }
    }

    /**
     * 上报接口
     *
     * @param keyInfo
     * @param param
     */
    protected void report(AutelKeyInfo<P> keyInfo, P param) {
        AutelKey<P> key = createKey(keyInfo);
        if (getKeyManager(key) == null) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_keymanager_null));
        } else {
            getKeyManager(key).setFrequencyReport(key, param);
        }
    }


    protected AutelKey.ActionKey<P, R> createActionKey(AutelActionKeyInfo<P, R> keyInfo) {
        AutelKey.ActionKey<P, R> key = null;
        key = KeyTools.createKey(keyInfo);
        return key;
    }

    protected <Param> AutelKey<Param> createKey(AutelKeyInfo<Param> keyInfo) {
        AutelKey<Param> key;
        key = KeyTools.createKey(keyInfo);
        return key;
    }

}
