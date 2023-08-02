//package com.autel.drone.sdk.expose.module.flight.data
//
//import junit.framework.Assert
//import junit.framework.TestCase
//
//class HeartBeatInfoTest : TestCase(){
//    private val heartBeatInfo = HeartBeatInfo()
//
//    fun testMask(){
//        Assert.assertTrue(heartBeatInfo.valid(2,0,
//            HeartBeatInfo.MASK_BATTERY))
//        Assert.assertTrue(heartBeatInfo.valid(1,2,
//            HeartBeatInfo.MASK_MAX_HEIGHT_REACH))
//        Assert.assertTrue(heartBeatInfo.valid(1,3,
//            HeartBeatInfo.MASK_MAX_RANGE_REACH))
//        Assert.assertTrue(heartBeatInfo.valid(1,4,
//            HeartBeatInfo.MASK_VALID_GPS))
//        Assert.assertTrue(heartBeatInfo.valid(1,5,
//            HeartBeatInfo.MASK_VALID_HOME_POINT))
//        Assert.assertTrue(heartBeatInfo.valid(1,6,
//            HeartBeatInfo.MASK_VALID_COMPASS))
//        Assert.assertTrue(heartBeatInfo.valid(1,7,
//            HeartBeatInfo.MASK_VALID_RC))
//        Assert.assertTrue(heartBeatInfo.valid(4,8,
//            HeartBeatInfo.MASK_WARN_AIRPORT))
//        Assert.assertTrue(heartBeatInfo.valid(4,12,
//            HeartBeatInfo.MASK_STATE_MAG))
//        Assert.assertTrue(heartBeatInfo.valid(1,16,
//            HeartBeatInfo.MASK_GOHOME_PENDING))
//        Assert.assertTrue(heartBeatInfo.valid(1,17,
//            HeartBeatInfo.MASK_STICK_LIMITED))
//        Assert.assertTrue(heartBeatInfo.valid(1,18,
//            HeartBeatInfo.MASK_VALID_TAKEOFF))
//        Assert.assertTrue(heartBeatInfo.valid(1,19,
//            HeartBeatInfo.MASK_ABLE_TAKEOFF))
//        Assert.assertTrue(heartBeatInfo.valid(2,20,
//            HeartBeatInfo.MASK_LAMP_STATUS))
//        Assert.assertTrue(heartBeatInfo.valid(1,22,
//            HeartBeatInfo.MASK_HOT_FC))
//        Assert.assertTrue(heartBeatInfo.valid(1,23,
//            HeartBeatInfo.MASK_HOT_BATTERY))
//        Assert.assertTrue(heartBeatInfo.valid(3,24,
//            HeartBeatInfo.MASK_STATUS_COMPASS))
//        Assert.assertTrue(heartBeatInfo.valid(1,27,
//            HeartBeatInfo.MASK_HOVER_ON))
//        Assert.assertTrue(heartBeatInfo.valid(1,28,
//            HeartBeatInfo.MASK_WARN_LARGE_WIND))
//        Assert.assertTrue(heartBeatInfo.valid(1,29,
//            HeartBeatInfo.MASK_NEAR_LIMIT))
//    }
//
//    fun testMask2(){
//        Assert.assertTrue(heartBeatInfo.valid(4,0,
//            HeartBeatInfo.MASK2_STATE_FLY))
//        Assert.assertTrue(heartBeatInfo.valid(4,4,
//            HeartBeatInfo.MASK2_MODE_JOYSTICK))
//        Assert.assertTrue(heartBeatInfo.valid(8,8,
//            HeartBeatInfo.MASK2_CODE_ERROR))
//        Assert.assertTrue(heartBeatInfo.valid(1,16,
//            HeartBeatInfo.MASK2_VISION_ORBIT))
//        Assert.assertTrue(heartBeatInfo.valid(1,17,
//            HeartBeatInfo.MASK2_GPS_LAND))
//        Assert.assertTrue(heartBeatInfo.valid(1,18,
//            HeartBeatInfo.MASK2_NECESSARY_CALIBRATION))
//        Assert.assertTrue(heartBeatInfo.valid(1,19,
//            HeartBeatInfo.MASK2_RTK_SUPPORT))
//        Assert.assertTrue(heartBeatInfo.valid(8,24,
//            HeartBeatInfo.MASK2_VERSION_ID))
//    }
//}