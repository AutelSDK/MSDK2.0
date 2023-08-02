package com.autel.sdk.debugtools

/**
 * Creating toast result for success and failure
 *
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/10/29.
 *
 * @param isSuccess boolean value for success and failure
 * @param msg message in string type for toast
 */

class AutelToastResult(var isSuccess: Boolean, var msg: String? = null) {

    companion object {
        /**
         * string message and pass result of toast success
         * @param msg string message to show in toast
         * @return toast result class object with success with message
         * */
        fun success(msg: String? = null): AutelToastResult {
            return AutelToastResult(true, msg)
        }

        /**
         * string message and pass result of toast success
         * @param msg string message to show in toast
         * @return toast result class object with failure with message
         * */
        fun failed(msg: String): AutelToastResult {
            return AutelToastResult(false, msg)
        }
    }
}



