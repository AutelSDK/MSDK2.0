package com.autel.sdk.debugtools.fragment

import android.media.MediaCodec
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.constants.SDKConstants
import com.autel.module_player.codec.OnRenderFrameInfoListener
import com.autel.module_player.player.AutelPlayerManager
import com.autel.module_player.player.autelplayer.AutelPlayer
import com.autel.module_player.player.autelplayer.AutelPlayerView
import com.autel.drone.sdk.vmodelx.manager.RtmpServiceManager
import com.autel.rtmp.publisher.IPublishListener
import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.databinding.FragmentLivestreamBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

/**
 * live streaming of video on autel media player(custom exoplayer)
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */

class LiveStreamFragment : AutelFragment() , OnRenderFrameInfoListener {
    var TAG = "LiveStreamFragment"
    var contentView: View? = null
    lateinit var edit_url: EditText
    lateinit var btn_start: Button
    lateinit var btn_switch:Button
    lateinit var btn_refersh:Button

    var right_view: LinearLayout? = null
    private var mAutelPlayer: AutelPlayer? = null
    var codecView: AutelPlayerView? = null

    var left_view: LinearLayout? = null
    private var mAutelPlayer2: AutelPlayer? = null
    var codecView2: AutelPlayerView? = null

    var rtmpPort:Int = SDKConstants.STREAM_CHANNEL_16110;

    private lateinit var uiBinding: FragmentLivestreamBinding

    private var mPublishFlag: Boolean = false

    private var iCurrentPort:Int = SDKConstants.STREAM_CHANNEL_16110 //可见光
    //"rtmp://183.6.112.146:17072/live/YD202220530_flight"; // 南网外网环境
    // "rtmp://183.6.112.146:1935/live/NEST202203038_flight_zoom" // 南网内网推流地址
    //"rtmp://116.205.231.28/live/livestream/zoom77" // 公司内网推流地址
    private var rtmpUrl:String  = "rtmp://183.6.112.146:1935/live/NEST202203038_flight_zoom_test" //"rtmp://a.rtmp.youtube.com/live2/5hh6-xas1-btk7-2cmc-2rz9"

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var connectStatus = -1;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uiBinding = FragmentLivestreamBinding.inflate(inflater, container, false)

        AutelPlayerManager.getInstance().init(activity, false)
        AutelPlayerManager.getInstance().registStremDataListener()

        AutelPlayerManager.getInstance().startStreamChannel(SDKConstants.STREAM_CHANNEL_16110)
        AutelPlayerManager.getInstance().startStreamChannel(SDKConstants.STREAM_CHANNEL_16115);
        return uiBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edit_url = uiBinding.root.findViewById(R.id.edit_url)
        btn_start = uiBinding.root.findViewById(R.id.btn_start)
        btn_switch= uiBinding.root.findViewById(R.id.btn_switch)
        btn_refersh = uiBinding.root.findViewById(R.id.btn_refersh)

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

        AutelPlayerManager.getInstance().addCodecListeners(TAG, SDKConstants.STREAM_CHANNEL_16110, this)

        initView();

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


    private fun createAutelCodecView(): AutelPlayerView? {
        val codecView = AutelPlayerView(activity)
        val params = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        codecView.layoutParams = params
        return codecView
    }

    private fun initView() {

        edit_url.setText(rtmpUrl)

        btn_start.setOnClickListener {

                if (mPublishFlag) {
                    mPublishFlag = false;
                    stopPublish()
                } else {
                    mPublishFlag = true
                    startPublish()
                }

        }

        btn_switch.setOnClickListener{

            coroutineScope.launch {
                if(iCurrentPort == SDKConstants.STREAM_CHANNEL_16110){
                    rtmpUrl = "rtmp://116.205.231.28/live/livestream/ir77"
                    iCurrentPort = SDKConstants.STREAM_CHANNEL_16115
                    RtmpServiceManager.getInstance().switchStream(SDKConstants.STREAM_CHANNEL_16115,rtmpUrl);
                } else {
                    rtmpUrl = "rtmp://116.205.231.28/live/livestream/zoom77"
                    iCurrentPort = SDKConstants.STREAM_CHANNEL_16110
                    RtmpServiceManager.getInstance().switchStream(SDKConstants.STREAM_CHANNEL_16110,rtmpUrl);
                }
            }
            edit_url.setText(rtmpUrl)
        }

        btn_refersh.setOnClickListener{
            if(!mPublishFlag)
                return@setOnClickListener
            coroutineScope.launch {
                RtmpServiceManager.getInstance().refershStream();
            }
        }

        coroutineScope.launch {
            RtmpServiceManager.getInstance().initRtmpConfig(edit_url.text.toString(), rtmpPort, false)
            RtmpServiceManager.getInstance().setRtmpPublishListener(object : IPublishListener {
                override fun onConnecting() {
                    SDKLog.d(TAG, "Rtmp Status : connecting")
                    connectStatus = 0;
                }

                override fun onConnected() {
                    SDKLog.d(TAG, "Rtmp Status : connected.")
                    connectStatus = 1;
                }

                override fun onConnectedFailed(code: Int) {
                    SDKLog.d(TAG, "Rtmp Status : connected failed code=" + code)
                    connectStatus = 2;
                }

                override fun onStartPublish() {
                    SDKLog.d(TAG, "Rtmp Status : start publish stream")
                }

                override fun onStopPublish() {
                    SDKLog.d(TAG, "Rtmp Status : stoppublish now")
                }

                override fun onFpsStatistic(fps: Int) {
                    SDKLog.d(TAG, "Rtmp upload fps : " + fps)
                }

                override fun onRtmpDisconnect() {
                    SDKLog.d(TAG, "Rtmp Status : disconnect..")
                }

                override fun onVideoBriate(value: Int) {
                    SDKLog.d(TAG, "Rtmp onVideoBriate : " + value + " KBPS")
                }

                override fun onAudioBriate(value: Int) {
                    SDKLog.d(TAG, "Rtmp onAudioBriate : " + value + " KBPS")
                }

                override fun onPublishSuccess() {
                    SDKLog.d(TAG, "Rtmp onPublishSuccess")
                }

                override fun onPublishFailed(errorCode: Int) {
                    SDKLog.d(TAG, "Rtmp onPublishFailed errcode" + errorCode)
                }
            })
        }
    }

    private fun stopPublish() {
        coroutineScope.launch {
            RtmpServiceManager.getInstance().stopPublishStream(null)
        }

        btn_start.text = getString(R.string.debug_start)
    }

    private fun startPublish() {
        coroutineScope.launch {
            RtmpServiceManager.getInstance().startPublishStream()
        }
        btn_start.text = getString(R.string.debug_stop)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.launch {
            RtmpServiceManager.getInstance().stopPublishStream(null)
            RtmpServiceManager.getInstance().releasePublishStream()
        }


        AutelPlayerManager.getInstance().endStreamChannel(SDKConstants.STREAM_CHANNEL_16110)
        if (mAutelPlayer != null) {
            mAutelPlayer?.removeVideoView()
            mAutelPlayer?.releasePlayer()
        }

        AutelPlayerManager.getInstance().endStreamChannel(SDKConstants.STREAM_CHANNEL_16115)
        if (mAutelPlayer2 != null) {
            mAutelPlayer2?.removeVideoView()
            mAutelPlayer2?.releasePlayer()
        }
    }

    override fun onRenderFrameTimestamp(pts: Long) {

    }

    override fun onRenderFrameSizeChanged(width: Int, height: Int) {

    }

    override fun onFrameStream(videoBuffer: ByteArray?, isIFrame: Boolean, size: Int, pts: Long, videoType: Int) {
       //todo:264码流回调到此处
    }

    override fun onFrameStream(videoBuffer: ByteBuffer?, mInfo: MediaCodec.BufferInfo?, isIFrame: Boolean, width: Int, height: Int, formatType: Int) {
    }

}