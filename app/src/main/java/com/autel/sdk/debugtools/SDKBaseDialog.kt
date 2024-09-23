package com.autel.sdk.debugtools

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager

/**
 * base dialog behaviour setting class for sdk dialogs
 * Copyright: Autel Robotics
 * @author xulc on 2022/7/20
 */
open class SDKBaseDialog(context: Context) : Dialog(context) {

    //隐藏虚拟按键
    override fun show() {
        val win = window
        if (win != null) {
            win.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
            win.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            super.show()
            win.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            super.show()
        }
    }
}