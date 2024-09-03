# Release Notes

## Version: V2.0.68.2

### Update Details

**AutelPlayer Class**:

- A new `public synchronized void startPlayer(Surface var1)` method has been added to the `AutelPlayer` class. This method allows users to customize the video playback `Surface`.
- The `startPlayer(Surface var1)` method should now be used instead of the `startPlayer()` method to support custom video playback surfaces.

### Code Example

Refer to the related code in `MuiltCodecFragment` for usage:

```kotlin
private fun createTestView(): TestSurface? {
    val callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            // Actions when the Surface is created
            SDKLog.i("Noiled", "surfaceCreated")
            mAutelPlayer?.startPlayer(holder.surface)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            // Actions when the Surface is changed
            SDKLog.i("Noiled", "surfaceChanged $width x $height")
            mAutelPlayer?.setSurfaceChange(width, height)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            SDKLog.i("Noiled", "surfaceDestroyed")
            // Actions when the Surface is destroyed
            mAutelPlayer?.setSurfaceDestoryed()
        }
    }
    val v = TestSurface(activity, callback)
    val params = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    v.layoutParams = params
    return v
}
```

## Version: V2.0.66

This release updates the firmware to the latest Autel Enterprise 1.7 version using MSDK, while maintaining compatibility with Autel Enterprise 1.6.

### Compilation Notes:
- Dependency version for Protobuf needs to be changed to `com.google.protobuf:protobuf-javalite:3.21.12`.

For detailed API usage instructions, please refer to [Autel Robotics Developer Documentation](https://developer.autelrobotics.com/version/v2).
