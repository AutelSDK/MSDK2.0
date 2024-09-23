package com.autel.sdk.debugtools;

import android.content.res.Resources;

/**
 * dp px sp 转换工具类
 * Copyright: Autel Robotics
 *
 * @author peiguo.chen on 2021/8/14
 */
public class DisplayUtil {


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
