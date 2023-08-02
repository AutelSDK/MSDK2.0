package com.autel.sdk.debugtools;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.autel.drone.sdk.libbase.error.AutelStatusCode;
import com.autel.drone.sdk.libbase.error.IAutelCode;
import com.autel.drone.sdk.log.SDKLog;
import com.autel.drone.sdk.vmodelx.SDKManager;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.CameraKey;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelActionKeyInfo;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKeyInfo;
import com.autel.drone.sdk.vmodelx.utils.ToastUtils;
import com.autel.sdk.debugtools.fragment.KeyItemActionListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * KeyItem作为key能力和动作的载体来进行封装
 * Copyright: Autel Robotics
 *
 * @author huangsihua on 2022/10/29.
 */

public class KeyItem<P, R> extends KeyBaseStructure implements Comparable<KeyItem<?, ?>> {

    public static final String TAG = KeyItem.class.getSimpleName();
    public static final int GET = 0;
    public static final int SET = 1;
    public static final int ACTION = 2;
    public static final int LISTEN = 3;
    public static final int UN_LISTEN = 4;
    public static final int REPORT = 5;
    /**
     * 是否在监听中
     */
    public boolean isListening = false;
    /**
     * 属性展示名
     */
    public String name;
    public boolean isSingleAutelValue;
    /**
     * 参数key的能力携带实体
     */
    public AutelKeyInfo<?> keyInfo;
    /**
     * 需要调用者注入的回调接口，用于结果通知
     */
    protected KeyItemActionListener<Object> keyOperateCallBack;
    private final CommonCallbacks.CompletionCallbackWithParam<R> actionCallback = new CommonCallbacks.CompletionCallbackWithParam<R>() {
        @Override
        public void onSuccess(Object data) {
            if (data != null) {
                endSuccess(ACTION, data.toString());
            } else {
                endSuccess(ACTION, null);
            }
        }

        @Override
        public void onFailure(@NonNull IAutelCode error, @Nullable String errorMsg) {
            endFail(ACTION, error, errorMsg);
        }
    };
    /**
     * 推送数据回调（需要调用者注入）
     */
    protected KeyItemActionListener<String> pushCallBack;
    /**
     * 使用次数，用户排序
     */
    private long count;
    private boolean isItemSelected;
    /**
     * 推送回调
     */
    private CommonCallbacks.KeyListener<R> listenSDKCallback = new CommonCallbacks.KeyListener<R>() {
        @Override
        public void onValueRealChange(R value) {
        }

        @Override
        public void onValueChange(R oldValue, R newValue) {
            StringBuilder sb = new StringBuilder("【LISTEN】");
            sb.append(getName());
            sb.append(" result:");
            // sb.append("oldValue:").append(oldValue);
            sb.append(" newValue:").append(newValue);

            if (pushCallBack != null) {
                pushCallBack.actionChange(sb.toString());
            }
        }
    };


    public KeyItem(AutelKeyInfo<?> keyInfo) {
        super();
        this.keyInfo = keyInfo;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean isItemSelected() {
        return isItemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        isItemSelected = itemSelected;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? keyInfo.getIdentifier() : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AutelKeyInfo getKeyInfo() {
        return keyInfo;
    }

    /**
     * 获取listen宿主
     *
     * @return
     */
    public Object getListenHolder() {
        return listenHolder;
    }

    /**
     * 是否可以Get
     *
     * @return
     */
    public boolean canGet() {
        return keyInfo.isCanGet();
    }

    /**
     * 是否可以Set
     *
     * @return
     */
    public boolean canSet() {
        return keyInfo.isCanSet();
    }

    /**
     * 是否可以Listen
     *
     * @return
     */
    public boolean canListen() {
        return keyInfo.isCanListen();
    }

    /**
     * 是否为action
     *
     * @return
     */
    public boolean canAction() {
        return keyInfo.isCanPerformAction();
    }

    /**
     * 是否为定频上报
     *
     * @return
     */
    public boolean canReport() {
        return keyInfo.isCanReport();
    }

    /**
     * 需要调用者注入的回调接口，用于结果通知
     *
     * @param keyOperateCallBack
     */
    public void setKeyOperateCallBack(KeyItemActionListener<Object> keyOperateCallBack) {
        this.keyOperateCallBack = keyOperateCallBack;
    }

    /**
     * 推送回调
     *
     * @param pushCallBack
     */
    public void setPushCallBack(KeyItemActionListener<String> pushCallBack) {
        this.pushCallBack = pushCallBack;
    }

    /**
     * Get请求
     */
    public void doGet() {
        try {
            start(GET, null);
            get(keyInfo, new CommonCallbacks.CompletionCallbackWithParam<R>() {
                @Override
                public void onFailure(@NonNull IAutelCode code, @Nullable String errorMsg) {
                    endFail(GET, code, errorMsg);
                }

                @Override
                public void onSuccess(R data) {
                    if (data != null) {
                        endSuccess(GET, data.toString());
                    } else {
                        endSuccess(GET, null);
                    }
                }
            });
        } catch (Exception e) {
            SDKLog.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    private void start(int type, String param) {
        if (keyOperateCallBack != null) {
            if (param == null) {
                param = "";
            }
            switch (type) {
                case GET:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_send)
                            + "：【GET】 【" + keyInfo.keyName + "】 "
                            + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter)
                            + param);
                    break;
                case SET:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_send)
                            + "：【SET】 【" + keyInfo.keyName + "】 "
                            + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter)
                            + param);
                    break;
                case ACTION:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_send)
                            + "：【ACTION】 【" + keyInfo.keyName + "】 "
                            + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter)
                            + param);
                    break;
                case LISTEN:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_send)
                            + "：【LISTEN】 【" + keyInfo.keyName + "】 "
                            + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter)
                            + param);
                    break;
                case UN_LISTEN:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_send)
                            + "：【UN_LISTEN】 【" + keyInfo.keyName + "】 "
                            + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter)
                            + param);
                    break;
                case REPORT:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_send)
                            + "：【REPORT】 【" + keyInfo.keyName + "】 "
                            + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter)
                            + param);
                    break;
            }
        }
    }

    private void endSuccess(int type, String result) {
        if (keyOperateCallBack != null) {
            if (result == null) {
                result = "";
            }
            switch (type) {
                case GET:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【GET】 【" + keyInfo.keyName + "】 " +
                            SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_success)
                            + "：" + result);
                    break;
                case SET:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【SET】 【" + keyInfo.keyName + "】 " +
                            SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_success)
                            + "：" + result);
                    break;
                case ACTION:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【ACTION】 【" + keyInfo.keyName + "】 " +
                            SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_success)
                            + "：" + result);
                    break;
                case LISTEN:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【LISTEN】 【" + keyInfo.keyName + "】 " +
                            SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_success)
                            + "：" + result);
                    break;
            }
        }
    }

    private void endFail(int type, @NonNull IAutelCode error, @Nullable String errorMsg) {
        if (keyOperateCallBack != null) {
            String errString = SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_failed)
                    + "：" + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_error_code_is)
                    + "：" + error + ","
                    + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_error_message)
                    + "：" + errorMsg;

            switch (type) {
                case GET:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【GET】 【" + keyInfo.keyName + "】" + errString);
                    break;
                case SET:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【SET】 【" + keyInfo.keyName + "】 " + errString);
                    break;
                case ACTION:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【ACTION】 【" + keyInfo.keyName + "】 " + errString);
                    break;
                case LISTEN:
                    keyOperateCallBack.actionChange(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_take_over)
                            + "：【LISTEN】 【" + keyInfo.keyName + "】 " + errString);
                    break;
            }
        }
    }

    public void doGetList() {
        List<AutelKeyInfo<?>> keyList = new ArrayList<>();
        keyList.add(CameraKey.INSTANCE.getKeyAELock());
        keyList.add(CameraKey.INSTANCE.getKeyApertureSize());
        keyList.add(CameraKey.INSTANCE.getKeyZoomFactor());
        keyList.add(CameraKey.INSTANCE.getKeyVideoEncoderConfig());
        start(GET, null);
        try {
            getList(keyList, new CommonCallbacks.CompletionCallbackWithParam<List<?>>() {
                @Override
                public void onFailure(@NonNull IAutelCode code, @Nullable String errorMsg) {
                    if (code instanceof AutelStatusCode) {
                        endFail(SET, code, ((AutelStatusCode) code).getMsg());
                    } else {
                        endFail(GET, code, errorMsg);
                    }
                }

                @Override
                public void onSuccess(List<?> objects) {
                    if (objects != null) {
                        endSuccess(GET, objects.toString());
                    } else {
                        endSuccess(GET, null);
                    }
                }
            });
        } catch (Exception e) {
            SDKLog.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Set请求
     */
    public void doSet(String jsonStr) {
        try {
            final P p = validPrams(jsonStr);
            if (p == null) {
                return;
            }
            start(SET, jsonStr);
            set(keyInfo, p, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onFailure(@NonNull IAutelCode code, @Nullable String msg) {
                    endFail(SET, code, msg);
                }

                @Override
                public void onSuccess() {
                    endSuccess(SET, null);
                }
            });
        } catch (Exception e) {
            ToastUtils.INSTANCE.showToast(SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter_input_error));
        }
    }

    /**
     * action请求
     */
    public void doAction(String jsonStr) {
        AutelActionKeyInfo<?, ?> actionKeyInfo = (AutelActionKeyInfo<?, ?>) keyInfo;
        P p = null;
        if (jsonStr != null && !jsonStr.isEmpty()) {
            p = validPrams(jsonStr);
        }
        start(ACTION, jsonStr);
        if (p == null) {
            action(actionKeyInfo, actionCallback);
        } else {
            action(actionKeyInfo, p, actionCallback);
        }
    }

    /**
     * action请求
     */
    public void doReport(String jsonStr) {
        AutelKeyInfo<?> actionKeyInfo = (AutelKeyInfo<?>) keyInfo;
        P p = null;
        if (jsonStr != null && !jsonStr.isEmpty()) {
            p = validPrams(jsonStr);
        }
        start(REPORT, jsonStr);
        if (p == null) {
            report(actionKeyInfo, null);
        } else {
            report(actionKeyInfo, p);
        }
    }

    /**
     * 注册Listen（新接口）
     *
     * @param listenHolder
     */
    public void listen(Object listenHolder) {
        start(LISTEN, null);
        this.listenHolder = listenHolder;
        isListening = true;
        listen(keyInfo, listenSDKCallback);
    }

    /**
     * 取消Listen（新接口）
     *
     * @param listenHolder
     */
    public void cancelListen(Object listenHolder) {
        start(UN_LISTEN, null);
        isListening = false;
        if (this.listenHolder == listenHolder) {
            this.listenHolder = null;
            cancelListen(keyInfo, listenSDKCallback);
            pushCallBack = null;
            listenRecord = "";
        }
    }

    /**
     * 验证参数
     *
     * @param jsonStr
     * @return
     */
    private P validPrams(String jsonStr) {
        if (Util.isBlank(jsonStr)) {
            String msg = "【SET】【" + getName() + "】【" + keyInfo.keyName + "】 "
                    + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_please_set_parameters_first);
            if (keyOperateCallBack != null) {
                keyOperateCallBack.actionChange(msg);
            }
            ToastUtils.INSTANCE.showToast(msg);
            return null;
        }
        final P p = buildParamFromJsonStr(jsonStr);
        if (p == null) {
            String msg = "【SET】【" + getName() + "】【" + keyInfo.keyName + "】 "
                    + SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_please_set_parameters_first)
                    + jsonStr +
                    SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_parameter);
            if (keyOperateCallBack != null) {
                keyOperateCallBack.actionChange(msg);
            }
            ToastUtils.INSTANCE.showToast(msg);
            return null;
        }
        return p;
    }

    /**
     * 反序列化：根据JSON串获取对象
     *
     * @param jsonStr
     * @return
     */
    public P buildParamFromJsonStr(String jsonStr) {
        if (jsonStr == null) {
            return null;
        }
        P p = null;
        Object o = keyInfo.getTypeConverter().fromJsonStr(jsonStr);
        if (o != null) {
            p = (P) o;
        }
        return p;
    }

    /**
     * 获取SingleValue 中原始类型包装类的value值
     *
     * @param jsonStr
     * @return
     */
    private String getSingleJsonValue(String jsonStr) {
        String value = "";
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            value = jsonObj.getString("value");
        } catch (Exception e) {
            SDKLog.e(TAG, e.getMessage());
        }
        return value;
    }

    /**
     * 序列化：获取默认JSON串
     *
     * @return
     */
    public String getParamJsonStr() {
        return keyInfo.getTypeConverter().getJsonStr();
    }

    /**
     * 移除回调
     */
    public void removeCallBack() {
        cancelListen(listenHolder);
        keyOperateCallBack = null;
    }

    @Override
    public int compareTo(KeyItem keyItem) {
        if (keyItem.count - this.count > 0) {
            return 1;
        } else if (keyItem.count - this.count < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
