package com.autel.sdk.debugtools.helper

import com.autel.drone.sdk.libbase.error.IAutelCode
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.interfaces.IKeyManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.AutelKey
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 协程包装类
 * Copyright: Autel Robotics
 *
 * @author xulc on 2022/10/12
 */
object KeyManagerCoroutineWrapper {
    private const val TAG = "KeyManagerCoroutine"

    private fun getKeyNameListStr(keyList: List<AutelKey<*>>): String {
        val keyNameList = StringBuilder()
        keyList.forEach {
            keyNameList.append(it.keyInfo.keyName + " ")
        }
        return keyNameList.toString()
    }

    private fun getResultListStr(resultList: List<*>?): String {
        val resultBuilder = StringBuilder()
        resultList?.forEach {
            resultBuilder.append("[" + it.toString() + "]" + " ")
        }
        return resultBuilder.toString()
    }

    suspend fun <Result> getValue(
        keyManager: IKeyManager,
        key: AutelKey<Result>
    ): Result {
        return suspendCancellableCoroutine {
            SDKLog.i(TAG, "getValue ${key.keyInfo.keyName} -> start")
            keyManager.getValue(key, object : CommonCallbacks.CompletionCallbackWithParam<Result> {

                override fun onSuccess(t: Result?) {
                    SDKLog.i(TAG, "getValue ${key.keyInfo.keyName} ->  onSuccess, result = " + t)
                    it.resume(t!!)
                }

                override fun onFailure(code: IAutelCode, msg: String?) {
                    SDKLog.e(
                        TAG,
                        "getValue ${key.keyInfo.keyName} -> onFailure,code = $code, msg =  ${msg.orEmpty()}"
                    )
                    it.resumeWithException(SdkFailureResultException(code, msg))
                }
            })
        }
    }

    suspend fun getValueList(
        keyManager: IKeyManager,
        keyList: List<AutelKey<*>>,
    ): List<*> {
        val keyNameList = getKeyNameListStr(keyList)
        return suspendCancellableCoroutine {
            SDKLog.i(TAG, "getValueList $keyNameList -> start")
            keyManager.getValueList(
                keyList,
                object : CommonCallbacks.CompletionCallbackWithParam<List<*>> {
                    override fun onSuccess(t: List<*>?) {
                        SDKLog.i(
                            TAG,
                            "getValueList $keyNameList ->  onSuccess, result = " + KeyManagerCoroutineWrapper.getResultListStr(
                                t
                            )
                        )
                        it.resume(t!!)
                    }

                    override fun onFailure(error: IAutelCode, msg: String?) {
                        SDKLog.e(
                            TAG,
                            "getValueList $keyNameList -> onFailure,code = $error, msg =  ${msg.orEmpty()}"
                        )
                        it.resumeWithException(SdkFailureResultException(error, msg))
                    }
                })
        }
    }

    suspend fun <Param> setValue(
        keyManager: IKeyManager,
        key: AutelKey<Param>,
        param: Param
    ) {
        return suspendCancellableCoroutine {
            SDKLog.i(TAG, "setValue ${key.keyInfo.keyName} -> start, param + $param")
            keyManager.setValue(key, param, object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    SDKLog.i(TAG, "setValue ${key.keyInfo.keyName} ->  onSuccess")
                    it.resume(Unit)
                }

                override fun onFailure(code: IAutelCode, msg: String?) {
                    SDKLog.e(
                        TAG,
                        "setValue ${key.keyInfo.keyName} -> onFailure,code = $code, msg =  ${msg.orEmpty()}"
                    )
                    it.resumeWithException(SdkFailureResultException(code, msg))
                }
            })
        }
    }

    suspend fun setValueList(
        keyManager: IKeyManager,
        keyList: List<AutelKey<Any>>,
        paramList: List<Any>
    ): List<*>? {
        return suspendCancellableCoroutine {
            val keyNameList = getKeyNameListStr(keyList)
            SDKLog.i(TAG, "setValueList $keyNameList -> start, paramList + $paramList")
            keyManager.setValueList(
                keyList,
                paramList,
                object : CommonCallbacks.CompletionCallbackWithParam<List<*>> {
                    override fun onSuccess(t: List<*>?) {
                        SDKLog.i(
                            TAG,
                            "setValueList $keyNameList ->  onSuccess,result = " + getResultListStr(t)
                        )
                        it.resume(t)
                    }

                    override fun onFailure(error: IAutelCode, msg: String?) {
                        SDKLog.e(
                            TAG,
                            "setValueList $keyNameList -> onFailure,code = $error, msg =  ${msg.orEmpty()}"
                        )
                        it.resumeWithException(SdkFailureResultException(error, msg))
                    }

                })
        }
    }

    suspend fun <Result> performAction(
        keyManager: IKeyManager,
        key: AutelKey.ActionKey<*, Result>
    ): Result? {
        return suspendCancellableCoroutine {
            SDKLog.i(TAG, "performAction ${key.keyInfo.keyName} -> start")
            keyManager.performAction(
                key,
                callback = object : CommonCallbacks.CompletionCallbackWithParam<Result> {

                    override fun onSuccess(t: Result?) {
                        SDKLog.i(
                            TAG,
                            "performAction ${key.keyInfo.keyName} ->  onSuccess, result = " + t
                        )
                        it.resume(t)
                    }

                    override fun onFailure(error: IAutelCode, msg: String?) {
                        SDKLog.e(
                            TAG,
                            "performAction ${key.keyInfo.keyName} -> onFailure,code = $error, msg =  ${msg.orEmpty()}"
                        )
                        it.resumeWithException(
                            SdkFailureResultException(
                                error,
                                "\"performAction ${key.keyInfo.keyName}-> onFailure,code = $error\""
                            )
                        )
                    }
                })
        }
    }

    suspend fun <Param, Result> performAction(
        keyManager: IKeyManager,
        key: AutelKey.ActionKey<Param, Result>,
        param: Param
    ): Result? {
        return suspendCancellableCoroutine {
            SDKLog.i(TAG, "performAction ${key.keyInfo.keyName} -> start, param = " + param)
            keyManager.performAction(
                key,
                param,
                object : CommonCallbacks.CompletionCallbackWithParam<Result> {
                    override fun onSuccess(t: Result?) {
                        SDKLog.i(
                            TAG,
                            "performAction ${key.keyInfo.keyName} ->  onSuccess, result = " + t
                        )
                        it.resume(t)
                    }

                    override fun onFailure(error: IAutelCode, msg: String?) {
                        SDKLog.e(
                            TAG,
                            "performAction ${key.keyInfo.keyName} -> onFailure,code = $error, msg =  ${msg.orEmpty()}"
                        )
                        it.resumeWithException(SdkFailureResultException(error, msg))
                    }
                })
        }
    }
}