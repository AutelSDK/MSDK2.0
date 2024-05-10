package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.constants.SDKConstants
import com.autel.drone.sdk.vmodelx.manager.GB28181ServiceManager
import com.autel.gb28181.IGB28181PublishListener
import com.autel.module_player.player.AutelPlayerManager
import com.autel.module_player.player.autelplayer.AutelPlayer
import com.autel.module_player.player.autelplayer.AutelPlayerView
import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.WiFiUtils
import com.autel.sdk.debugtools.databinding.FragmentGb28181PublisherBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GB28181Fragment : AutelFragment()  {
    var TAG = "GB28181Fragment"
    var contentView: View? = null
    lateinit var edit_url: TextView
    lateinit var btn_start: Button

    var right_view: LinearLayout? = null
    private var mAutelPlayer: AutelPlayer? = null
    var codecView: AutelPlayerView? = null

    var left_view: LinearLayout? = null
    private var mAutelPlayer2: AutelPlayer? = null
    var codecView2: AutelPlayerView? = null

    var rtmpPort:Int = SDKConstants.STREAM_CHANNEL_16110;

    private lateinit var uiBinding: FragmentGb28181PublisherBinding

    private var mPublishFlag: Boolean = false

    private var iCurrentPort:Int = SDKConstants.STREAM_CHANNEL_16110 //可见光

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uiBinding = FragmentGb28181PublisherBinding.inflate(inflater, container, false)

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

        btn_start.isEnabled = true

        btn_start.setOnClickListener {

            if (mPublishFlag) {
                mPublishFlag = false;
                stopPublish()
            } else {
                mPublishFlag = true
                startPublish()
            }
        }

//        GB28181Params.setSIPServerIPAddress("121.36.245.130");//SIP服务器地址
//        GB28181Params.setRemoteSIPServerPort(5060);//SIP服务器端口
//        GB28181Params.setLocalSIPIPAddress("10.10.49.221");//本机地址
//        GB28181Params.setRemoteSIPServerID("34020000002000000001"); // SIP服务器ID
//        GB28181Params.setRemoteSIPServerSerial("3402000000");// SIP服务器域
//        GB28181Params.setLocalSIPPort(7060);//本机端口
//        GB28181Params.setUserName("admin");//SIP用户名
//        GB28181Params.setPassword("12345678");//密码
//        GB28181Params.setLocalSIPDeviceId("34020000001110000006");//本机SIP设备ID
//        GB28181Params.setLocalSIPMediaId("34020000001310000001");//本机SIP媒体ID

        var strIP = WiFiUtils.getIPAddress()
        if(!strIP.equals("0.0.0.0")) {
//            GB28181ServiceManager.getInstance().initGB28181Params(iCurrentPort,
//                "10.250.13.67",
//                5060,
//                "34020000002000000001",
//                "340200000",
//                strIP,
//                7060,
//                "34020000001110000006",
//                "12345678",
//                "34020000001110000006",
//                "34020000001310000001"
//            );
//                        GB28181ServiceManager.getInstance().initGB28181Params(iCurrentPort,
//                "47.111.155.82",
//                32003,
//                "44010200492000000001",
//                "4401020049",
//                strIP,
//                7060,
//                "admin",
//                "admin123",
//                "34020000001311109900",
//                "34020000001311109900"
//            );

            GB28181ServiceManager.getInstance().initGB28181Params(iCurrentPort,
                "10.250.13.233",
                15080,
                "34020000002000000001",
                "340200000",
                strIP,
                7060,
                "34020000001320000003",
                "12345678",
                "34020000001320000003",
                "34020000001320000003"
            );
        }

        GB28181ServiceManager.getInstance().setGB28181PublishListener(object:IGB28181PublishListener{
            override fun onGB28181StartRegist() {
                SDKLog.i("GB28181Fragment","onGB28181StartRegist")
            }

            override fun onGB28181StartUnRegist() {
                SDKLog.i("GB28181Fragment","onGB28181StartUnRegist")
            }

            override fun onGB28181RegistSucc() {
                SDKLog.i("GB28181Fragment","onGB28181RegistSucc")
            }

            override fun onGB28181RegistFailed() {
                SDKLog.i("GB28181Fragment","onGB28181RegistFailed")
            }

            override fun onGB28181UnRegistSucc() {
                SDKLog.i("GB28181Fragment","onGB28181UnRegistSucc")
            }

            override fun onGB28181UnRegistFalied() {
                SDKLog.i("GB28181Fragment","onGB28181UnRegistFalied")
            }

            override fun onGB28181StartAuth() {
                SDKLog.i("GB28181Fragment","onGB28181StartAuth")
            }

            override fun onGB28181AuthSucc() {
                SDKLog.i("GB28181Fragment","onGB28181AuthSucc")
            }

            override fun onGB28181AuthFalied() {
                SDKLog.i("GB28181Fragment","onGB28181AuthFalied")
            }

            override fun onGB28181CatalogInfo() {
                SDKLog.i("GB28181Fragment","onGB28181CatalogInfo")
            }

            override fun onGB28181DeviceInfo() {
                SDKLog.i("GB28181Fragment","onGB28181DeviceInfo")
            }

            override fun onGB28181DeviceStatusInfo() {
                SDKLog.i("GB28181Fragment","onGB28181DeviceStatusInfo")
            }

            override fun onGB28181DeviceControlInfo() {
                SDKLog.i("GB28181Fragment","onGB28181DeviceControlInfo")
            }

            override fun onGB28181MediaInviteInfo() {
                SDKLog.i("GB28181Fragment","onGB28181MediaInviteInfo")
            }

            override fun onGB28181MediaAckInfo() {
                SDKLog.i("GB28181Fragment","onGB28181MediaAckInfo")
            }

            override fun onGB28181MediaByeInfo() {
                SDKLog.i("GB28181Fragment","onGB28181MediaByeInfo")
            }


            override fun onGB28181KeepAlive() {
                SDKLog.i("GB28181Fragment","onGB28181KeepAlive")
            }

            override fun onGB28181MetaData() {
                SDKLog.i("GB28181Fragment","onGB28181MetaData")
            }

            override fun onGB28181VideoFps(fps: Float) {
                SDKLog.i("GB28181Fragment","onGB28181VideoFps")
            }

            override fun onGB28181VideoRate(videoRate: Float) {
                SDKLog.i("GB28181Fragment","onGB28181VideoRate")
            }
        })
    }

    private fun stopPublish() {
        coroutineScope.launch {
            GB28181ServiceManager.getInstance().stopPublishStream()
        }

        btn_start.text = getString(R.string.debug_start)
    }

    private fun startPublish() {
        coroutineScope.launch {
            GB28181ServiceManager.getInstance().startPublishStream()
        }
        btn_start.text = getString(R.string.debug_stop)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.launch {
            GB28181ServiceManager.getInstance().releasePublishStream()
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
}