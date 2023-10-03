package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.autel.drone.sdk.vmodelx.SDKManager
import com.autel.drone.sdk.vmodelx.interfaces.IOTAFirmwareUpgradeManager
import com.autel.drone.sdk.vmodelx.interfaces.OTAFirmwareUpgradeProcessStateListener
import com.autel.drone.sdk.vmodelx.manager.OTAFirmwareUpgradeManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.upgrade.enums.UpgradeClientTypeEnum
import com.autel.drone.sdk.vmodelx.module.droneLog.bean.DeviceLogPackResponseBean
import com.autel.drone.sdk.vmodelx.module.droneLog.bean.DeviceLogQueryResponseBen
import com.autel.drone.sdk.vmodelx.module.droneLog.data.DeviceSysLog
import com.autel.drone.sdk.vmodelx.module.droneLog.db.DeviceLogDatabaseHelper
import com.autel.drone.sdk.vmodelx.module.droneLog.enums.DeviceLogEnum
import com.autel.drone.sdk.vmodelx.module.upgrade.enums.OTAFirmwareUpgradeProcessState
import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.databinding.FragmentDroneLogBinding
import com.autel.sdk.debugtools.databinding.FragmentOtaFirmwareUpgradeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import android.Manifest
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Environment
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.module.fileservice.FileTransmitListener
import com.autel.drone.sdk.vmodelx.module.fileservice.FileUtils
import com.autel.drone.sdk.vmodelx.module.upgrade.bean.OTAUpgradeModel
import com.autel.drone.sdk.vmodelx.module.upgrade.bean.ota.CheckResponseBean
import com.autel.drone.sdk.vmodelx.module.upgrade.bean.ota.ObservableMap
import com.autel.drone.sdk.vmodelx.utils.S3DownloadInterceptor
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * A simple [Fragment] subclass.
 * Use the [OTAFirmwareUpgradeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OTAFirmwareUpgradeFragment : AutelFragment(), OTAFirmwareUpgradeProcessStateListener {

    private val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val requestCode = 1

    private lateinit var binding: FragmentOtaFirmwareUpgradeBinding

    private var map = ObservableMap<String, CheckResponseBean.Data>()

    private var s3DownloadInterceptor: S3DownloadInterceptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentOtaFirmwareUpgradeBinding.inflate(layoutInflater)
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtaFirmwareUpgradeBinding.inflate(inflater)
        initView()
        return binding.root
    }

    companion object {
        private const val TAG = "OTA Test"
    }

    private fun initView() {
        OTAFirmwareUpgradeManager.getInstance().addListener(this)
        binding.check.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main.immediate) {
                try {
                    map =  OTAFirmwareUpgradeManager.getInstance().checkProduct()
                    SDKLog.i("OTAFirmwareUpgradeFragment","checkProduct: $map")
                } catch (e: Exception) {
                    SDKLog.i("OTAFirmwareUpgradeFragment","checkProduct fair: ${e.message}")
                }

            }
        }

        binding.downLoad.setOnClickListener {

            val modex = map.get("ModelX")
            modex?.let {
                if (it.isNeed_upgrade) {
                    s3DownloadInterceptor = OTAFirmwareUpgradeManager.getInstance().downLoadProductFile(it, object :
                        FileTransmitListener<File>{
                        override fun onSuccess(result: File?) {
                            SDKLog.d("OTAFirmwareUpgradeFragment","downLoad success: ")
                        }

                        override fun onProgress(sendLength: Long, totalLength: Long, speed: Long?) {

                            SDKLog.d("OTAFirmwareUpgradeFragment","downLoad size: $sendLength")
                        }

                        override fun onFailed(message: String?) {
                            SDKLog.d("OTAFirmwareUpgradeFragment","downLoad failed: $message")
                        }
                    })
                }
            }

        }

        binding.cancel.setOnClickListener {
            s3DownloadInterceptor?.cancel()
        }

        binding.startUpgrade.setOnClickListener {
            var filePath = SDKManager.get().sContext?.getExternalFilesDir(null)?.absolutePath
            val gndFilePath = filePath + "/RC79.Autel-V1.5.0.9-20230708165411.uav"
            val skyFilePath = filePath + "/ModelX-V1.5.0.75-20230706142034.autel"
            var gndFile =  File(gndFilePath)
            var skyFile =  File(skyFilePath)
            lifecycleScope.launch(Dispatchers.Main.immediate) {
                try {
                    val gndMode = OTAUpgradeModel(UpgradeClientTypeEnum.CLIENT_TYPE_GND,gndFile)
                    val skyMode = OTAUpgradeModel(UpgradeClientTypeEnum.CLIENT_TYPE_SKY,skyFile)
                    val list = arrayOf(skyMode, gndMode);
                    OTAFirmwareUpgradeManager.getInstance().startFirmwareUpgrade(list)
                }catch (e: Exception) {
                    SDKLog.i("OTAFirmwareUpgradeFragment","upgrade fair: ${e.message}")
                }
            }


        }
    }

    override fun onOTAFirmwareUpgradeProcessState(
        model: OTAUpgradeModel,
        status: OTAFirmwareUpgradeProcessState
    ) {

        lifecycleScope.launch(Dispatchers.Main) {
            val typeEnum = model.clientType
            if (status == OTAFirmwareUpgradeProcessState.UPGRADING || status == OTAFirmwareUpgradeProcessState.UPLOADING) {
                binding.upgradeState.text = "upgrade state: " + (if(typeEnum == UpgradeClientTypeEnum.CLIENT_TYPE_GND) "gnd  " else "sky ") + "state: ${status.name}" + " progress: ${status.progress}"
            } else if (status ==  OTAFirmwareUpgradeProcessState.OPERATION_FAILED) {
                binding.upgradeState.text = "upgrade state: " + (if(typeEnum == UpgradeClientTypeEnum.CLIENT_TYPE_GND) "gnd  " else "sky  ") + "upgrade fair" + " error: ${status.errorMessage?.message}"
            }else if (status ==  OTAFirmwareUpgradeProcessState.UPGRADE_COMPLETED) {
                binding.upgradeState.text = "upgrade state: success"
            }else {
                binding.upgradeState.text = "upgrade state: " + (if(typeEnum == UpgradeClientTypeEnum.CLIENT_TYPE_GND) "gnd  " else "sky  ") + "state: ${status.name}"
            }
        }
    }

    fun calculateMD5(file: File): String {
        val md5Digest = MessageDigest.getInstance("MD5")
        val inputStream = FileInputStream(file)
        val buffer = ByteArray(8192)
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            md5Digest.update(buffer, 0, bytesRead)
        }

        inputStream.close()

        val md5Bytes = md5Digest.digest()
        val md5String = StringBuilder()

        for (byte in md5Bytes) {
            md5String.append(String.format("%02x", byte))
        }

        return md5String.toString()
    }

}