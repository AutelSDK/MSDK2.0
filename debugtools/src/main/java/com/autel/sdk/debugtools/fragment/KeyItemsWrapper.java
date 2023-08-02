package com.autel.sdk.debugtools.fragment;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.autel.drone.sdk.libbase.error.AutelStatusCode;
import com.autel.drone.sdk.libbase.error.IAutelCode;
import com.autel.drone.sdk.vmodelx.SDKManager;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks;
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKeyInfo;
import com.autel.drone.sdk.vmodelx.utils.ToastUtils;
import com.autel.sdk.debugtools.KeyBaseStructure;
import com.autel.sdk.debugtools.KeyItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * key item structure wrapper class
 * Copyright: Autel Robotics
 *
 * @author lizhiping on 2022/10/9.
 */
public class KeyItemsWrapper<P, R> extends KeyBaseStructure {

    public final static String GET_ALL = "GET ALL";
    public final static String SET_ALL = "SET ALL";
    public final static String ACTION_ALL = "ACTION ALL";
    public final static String LISTEN_ALL = "LISTEN ALL";

    private final static String STATUS_BEGIN = "BEGIN";
    private final static String STATUS_SUCCESS = "SUCCESS";
    private final static String STATUS_FAILED = "FAILED";

    public List<KeyItem<P, R>> keyItemList;
    public KeyItemWrapperListener keyItemWrapperListener;

    public void getList(List<KeyItem> keyItemList) {
        List<AutelKeyInfo<?>> keyInfoList = new ArrayList<>();
        for (KeyItem keyItem : keyItemList) {
            keyInfoList.add(keyItem.keyInfo);
        }
        printAndToast(true, keyItemWrapperListener, false,
                SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_get_all),
                SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_begin), Collections.singletonList("begin ..."));
        printAndToast(true, keyItemWrapperListener, false,
                SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_get_all), SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_begin), getKeyItemNameList(keyItemList));

        getList(keyInfoList, new CommonCallbacks.CompletionCallbackWithParam<List>() {

            @Override
            public void onFailure(@NonNull IAutelCode code, @Nullable String errorMsg) {
                String msg;
                if (code instanceof AutelStatusCode) {
                    msg = code + ((AutelStatusCode) code).getMsg();
                } else {
                    msg = code.toString();
                }
                printAndToast(true, keyItemWrapperListener, true,
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_get_all),
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_failed), Collections.singletonList(msg));
            }

            @Override
            public void onSuccess(@Nullable List data) {
                printAndToast(true, keyItemWrapperListener, false,
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_get_all),
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_success), data);
            }
        });
    }

    /**
     * Set请求
     */
    public void setList(List<KeyItem> keyItemList) {

        List<AutelKeyInfo<?>> keyInfoList = new ArrayList<>();
        List<P> paramList = new ArrayList<>();
        for (KeyItem keyItem : keyItemList) {
            P param = (P) keyItem.buildParamFromJsonStr(keyItem.getParamJsonStr());
            if (param == null) {
                continue;
            }
            keyInfoList.add(keyItem.keyInfo);
            paramList.add(param);
        }

        printAndToast(true, keyItemWrapperListener, false,
                SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_set_all), SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_begin), Collections.singletonList("begin ..."));
        printAndToast(true, keyItemWrapperListener, false,
                SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_set_all), SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_begin), getKeyItemNameList(keyItemList));

        setList(keyInfoList, paramList, new CommonCallbacks.CompletionCallbackWithParam<List<?>>() {
            @Override
            public void onFailure(@NonNull IAutelCode code, @Nullable String errorMsg) {
                String msg;
                if (code instanceof AutelStatusCode) {
                    msg = code + ((AutelStatusCode) code).getMsg();
                } else {
                    msg = code.toString();
                }
                printAndToast(true, keyItemWrapperListener, true,
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_set_all),
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_failed), Collections.singletonList(msg));
            }

            @Override
            public void onSuccess(@Nullable List<?> data) {
                printAndToast(true, keyItemWrapperListener, true,
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_set_all),
                        SDKManager.get().getSContext().getString(com.autel.sdk.debugtools.R.string.debug_success), data);
            }
        });
    }

    private List<String> getKeyItemNameList(List<KeyItem> keyItemList) {
        List<String> msgList = new ArrayList<>();
        for (int i = 0; i < keyItemList.size(); i++) {
            String msg = "【" + getKeyItemName(keyItemList.get(i)) + "】";
            msgList.add(msg);
        }
        return msgList;
    }

    private String getKeyItemName(KeyItem<P, R> keyItem) {
        if (TextUtils.isEmpty(keyItem.name)) {
            return keyItem.keyInfo.getIdentifier();
        } else {
            return keyItem.name;
        }
    }

    /**
     * print会打印多条，toast会合并打印，最多只会打印1条
     */
    private void printAndToast(boolean needPrint, KeyItemWrapperListener<String> printListener,
                               boolean needToast,
                               String tag, String status, List msgList) {
        if (needPrint && printListener != null && msgList != null) {
            for (Object msg : msgList) {
                printListener.actionChange(String.format("【%s】【%s】%s", tag, status, msg));
            }
        }

        if (needToast && msgList != null) {
            StringBuilder toastStr = new StringBuilder();
            for (Object msg : msgList) {
                toastStr.append(msg);
            }
            ToastUtils.INSTANCE.showToast(String.format("【%s】【%s】%s", tag, status, toastStr));
        }
    }

}
