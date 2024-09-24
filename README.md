# Release Notes
## Version: V2.5.8.1

```js
//need the below dependencies
implementation 'com.squareup.okhttp3:logging-interceptor:4.10.0'
api 'com.squareup.retrofit2:retrofit:2.9.0'
api 'com.squareup.retrofit2:adapter-rxjava3:2.9.0'
api 'com.squareup.retrofit2:adapter-rxjava3:2.9.0'
api 'com.squareup.retrofit2:converter-moshi:2.9.0'
api 'me.jessyan:retrofit-url-manager:1.4.0'
implementation 'com.squareup.okhttp3:okhttp-dnsoverhttps:4.9.3'

//SDKConstants path changed to path com.autel.sdk
SDKConstants changes to path com.autel.drone.sdk.SDKConstants

```

## Version: V2.0.66

This release updates the firmware to the latest Autel Enterprise 1.7 version using MSDK, while maintaining compatibility with Autel Enterprise 1.6.

### Compilation Notes:
- Dependency version for Protobuf needs to be changed to `com.google.protobuf:protobuf-javalite:3.21.12`.

For detailed API usage instructions, please refer to [Autel Robotics Developer Documentation](https://developer.autelrobotics.com/version/v2).
