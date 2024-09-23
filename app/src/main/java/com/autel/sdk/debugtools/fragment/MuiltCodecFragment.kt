package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.autel.drone.demo.R
import com.autel.drone.demo.databinding.FragMuiltStreamBinding
import com.autel.drone.sdk.vmodelx.constants.SDKConstants
import com.autel.module_player.player.AutelPlayerManager
import com.autel.module_player.player.autelplayer.AutelPlayer
import com.autel.module_player.player.autelplayer.AutelPlayerView

/**
 * multiple type of codec for video streaming
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */
class MuiltCodecFragment : AutelFragment() {

    var right_view: LinearLayout? = null
    private var mAutelPlayer: AutelPlayer? = null
    var codecView: AutelPlayerView? = null

    var left_view: LinearLayout? = null
    private var mAutelPlayer2: AutelPlayer? = null
    var codecView2: AutelPlayerView? = null

    private lateinit var uiBinding: FragMuiltStreamBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        uiBinding = FragMuiltStreamBinding.inflate(inflater, container, false)

        AutelPlayerManager.getInstance().init(activity, false);
        AutelPlayerManager.getInstance().registStremDataListener();

        AutelPlayerManager.getInstance().startStreamChannel(SDKConstants.STREAM_CHANNEL_16110);
        AutelPlayerManager.getInstance().startStreamChannel(SDKConstants.STREAM_CHANNEL_16115);

        return uiBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        left_view = uiBinding.root.findViewById(R.id.layout_left_view)
        codecView = createAutelCodecView()
        with(left_view) { this?.addView(codecView) }

        mAutelPlayer = AutelPlayer(SDKConstants.STREAM_CHANNEL_16110)
        mAutelPlayer!!.addVideoView(codecView)

        AutelPlayerManager.getInstance().addAutelPlayer(mAutelPlayer);


        right_view = uiBinding.root.findViewById(R.id.layout_right_view)
        codecView2 = createAutelCodecView2()
        with(right_view) { this?.addView(codecView2) }


        mAutelPlayer2 = AutelPlayer(SDKConstants.STREAM_CHANNEL_16115)
        mAutelPlayer2!!.addVideoView(codecView2)

        AutelPlayerManager.getInstance().addAutelPlayer(mAutelPlayer2);

        mAutelPlayer!!.startPlayer()
        mAutelPlayer2!!.startPlayer()
    }

    /**
     * create code view for autel media player 1
     */
    private fun createAutelCodecView(): AutelPlayerView? {
        val codecView = AutelPlayerView(activity)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        codecView.layoutParams = params
        return codecView
    }

    /**
     * create code view for autel media player 2
     */
    private fun createAutelCodecView2(): AutelPlayerView? {
        val codecView = AutelPlayerView(activity)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        codecView.layoutParams = params
        return codecView
    }

    override fun onDestroy() {
        super.onDestroy()

        AutelPlayerManager.getInstance().endStreamChannel(SDKConstants.STREAM_CHANNEL_16110);

        AutelPlayerManager.getInstance().endStreamChannel(SDKConstants.STREAM_CHANNEL_16115);

        if (mAutelPlayer != null) {
            mAutelPlayer!!.removeVideoView()
            mAutelPlayer!!.releasePlayer()
        }

        if (mAutelPlayer2 != null) {
            mAutelPlayer2!!.removeVideoView()
            mAutelPlayer2!!.releasePlayer()
        }
	  
	   //for new home

        //AutelPlayerManager.getInstance().unregistStreamDataListener();
        //AutelPlayerManager.getInstance().release();
    }
}