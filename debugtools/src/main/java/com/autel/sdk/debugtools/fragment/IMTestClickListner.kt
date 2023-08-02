package com.autel.sdk.debugtools.fragment

/**
 * item click listener for clicked on details item
 *
 * Copyright: Autel Robotics
 * @author  on 2022/12/17.
 */

interface IMTestClickListener {
    /**
     * test click listener to get details where override this method
     * @param testName name of details
     * @param position position of item clicked
     */
    fun testDetails(testName: String, position: Int)
}