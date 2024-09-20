# Release Notes

## Version: V2.0.67.2
New Features:
Virtual Stick Demo: Added a new virtual stick demo, allowing users to adjust speed levels and view real-time data. This demo showcases remote drone control capabilities.
WebRTC Integration: Integrated the WebRTC SDK to support potential future enhancements, paving the way for advanced communication and remote operation features.
YUV callback: Implemented a YUV callback function to stream video data from the drone.

retrieve YUV data from a video stream, using the MultiCodecFragment as a reference:

AutelPlayerManager.getInstance().init(activity, true)
AutelPlayerManager.getInstance().registStremDataListener();

AutelPlayerManager.getInstance().startStreamChannel(SDKConstants.STREAM_CHANNEL_16110);
mAutelPlayer = AutelPlayer(SDKConstants.STREAM_CHANNEL_16110)

mAutelPlayer?.setVideoInfoListener( object : IVideoStreamListener {
    override fun onVideoInfoCallback(playerId: Int, x: Int, y: Int, w: Int, h: Int) {
        SDKLog.e("mAutelPlayer", "playerId $playerId is x $x y $y w $w h  $h")
    }

    override fun onFrameYuv(yuv: ByteBuffer?, width: Int, height: Int, stride: Int) {
        SDKLog.e("mAutelPlayer", "yuv is $yuv")
    }
})

AutelPlayerManager.getInstance().addAutelPlayer(mAutelPlayer);

mAutelPlayer!!.startPlayer()


Improvements:
UI Updates: Added necessary resources and layout files to support the new virtual stick demo.
Fragment Modifications: Updated existing fragments to accommodate and enhance the new functionalities.

## Version: V2.0.66

This release updates the firmware to the latest Autel Enterprise 1.7 version using MSDK, while maintaining compatibility with Autel Enterprise 1.6.

### Compilation Notes:
- Dependency version for Protobuf needs to be changed to `com.google.protobuf:protobuf-javalite:3.21.12`.

For detailed API usage instructions, please refer to [Autel Robotics Developer Documentation](https://developer.autelrobotics.com/version/v2).
