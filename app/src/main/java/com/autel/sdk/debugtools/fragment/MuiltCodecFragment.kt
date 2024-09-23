package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.autel.drone.demo.R
import com.autel.drone.demo.databinding.FragMuiltStreamBinding
import com.autel.drone.sdk.SDKConstants
import com.autel.module_player.MediaInfo
import com.autel.module_player.player.AutelPlayerManager
import com.autel.module_player.player.IVideoStreamListener
import com.autel.module_player.player.autelplayer.AutelPlayer
import com.autel.module_player.player.autelplayer.AutelPlayerView
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

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
    private var isFrameSaved = false

    private lateinit var uiBinding: FragMuiltStreamBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        uiBinding = FragMuiltStreamBinding.inflate(inflater, container, false)


        return uiBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        left_view = uiBinding.root.findViewById(R.id.layout_left_view)
//        codecView = createAutelCodecView()
//        with(left_view) { this?.addView(codecView) }

        mAutelPlayer = AutelPlayer(SDKConstants.STREAM_CHANNEL_16110)
        mAutelPlayer?.setVideoInfoListener(object : IVideoStreamListener {
            override fun onVideoSizeChanged(playerId: Int, width: Int, height: Int) {

            }

            /**
             * video info call back with some info about video playing
             *
             * @param playerId id number for streaming
             * @param x    x co-ordinates for video
             * @param y    y co-ordinates for video
             * @param w    width of video
             * @param h    height of video
             */
            override fun onVideoInfoCallback(playerId: Int, x: Int, y: Int, w: Int, h: Int) {

            }

            override fun onFrameYuv(yuv: ByteBuffer?, mediaInfo: MediaInfo?) {
                Log.i("MuiltCodecFragment", " yuv ${yuv?.capacity()} mediaInfo ${mediaInfo.toString()}")
                if (!isFrameSaved && yuv != null && mediaInfo != null){
                    isFrameSaved = true;
                    if (mediaInfo.pixelFormat == MediaInfo.PixelFormat.PIX_FMT_NV12){
                        saveYuvToFile(yuv, mediaInfo.width, mediaInfo.height, mediaInfo.stride, mediaInfo.sliceHeight)
                    }

                }
            }

            override fun onVideoErrorCallback(playerId: Int, type: Int, errorContent: String?) {

            }

        })
//        mAutelPlayer!!.addVideoView(codecView)

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

    private fun saveYuvToFile(buffer: ByteBuffer, width: Int, height: Int, stride: Int, sliceHeight: Int) {

        val fileName = "frame_${System.currentTimeMillis()}.yuv"
        val file = File(requireContext().filesDir, fileName)

        try {
            FileOutputStream(file).use { fos ->
                var b = removeNV12Padding(buffer, width, height, stride, sliceHeight)
                val data = ByteArray(b.remaining())
                b.get(data)
                fos.write(data)
            }
            println("File saved at: ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeNV12Padding(originalBuffer: ByteBuffer, width: Int, height: Int, stride: Int, sliceHeight:Int): ByteBuffer {
        val ySize = width * height
        val uvSize = ySize / 2
        val totalSize = ySize + uvSize

        val resultBuffer = ByteBuffer.allocateDirect(totalSize)
        resultBuffer.order(ByteOrder.nativeOrder())

        // Copy Y plane without padding
        for (row in 0 until height) {
            val srcOffset = row * stride
            val dstOffset = row * width
            originalBuffer.position(srcOffset)
            val temp = ByteArray(width)
            originalBuffer.get(temp)
            resultBuffer.position(dstOffset)
            resultBuffer.put(temp)
        }

        // Copy UV plane without padding
        val uvHeight = height / 2
        for (row in 0 until uvHeight) {
            val srcOffset = stride*sliceHeight + row * stride
            val dstOffset = ySize + row * width
            originalBuffer.position(srcOffset)
            val temp = ByteArray(width)
            originalBuffer.get(temp)
            resultBuffer.position(dstOffset)
            resultBuffer.put(temp)
        }

        resultBuffer.rewind()
        return resultBuffer
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