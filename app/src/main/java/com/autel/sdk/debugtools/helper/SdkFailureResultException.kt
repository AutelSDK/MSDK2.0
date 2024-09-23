package com.autel.sdk.debugtools.helper

import com.autel.drone.sdk.libbase.error.IAutelCode

/**
 * sdk failure result code and message throw
 * Copyright: Autel Robotics
 *
 * @author xulc on 2022/10/12
 */
class SdkFailureResultException(val error: IAutelCode, val msg: String?) : Throwable()