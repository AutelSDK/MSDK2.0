package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.constants.SDKConstants
import com.autel.drone.sdk.vmodelx.module.camera.bean.LensTypeEnum
import com.autel.module_player.player.AutelPlayerManager
import com.autel.module_player.player.IVideoStreamListener
import com.autel.module_player.player.autelplayer.AutelPlayer
import com.autel.module_player.player.autelplayer.AutelPlayerView
import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.databinding.FragMuiltStreamBinding
import java.nio.ByteBuffer

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

        AutelPlayerManager.getInstance().init(activity, true)
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
        mAutelPlayer?.setVideoInfoListener( object : IVideoStreamListener {
            override fun onVideoInfoCallback(playerId: Int, x: Int, y: Int, w: Int, h: Int) {
                SDKLog.e("mAutelPlayer", "playerId $playerId is x $x y $y w $w h  $h")
            }

            override fun onFrameYuv(yuv: ByteBuffer?, width: Int, height: Int, stride: Int) {
                SDKLog.e("mAutelPlayer", "yuv is $yuv")
            }
        })

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