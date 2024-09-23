package com.autel.sdk.debugtools.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.autel.drone.demo.databinding.FragmentDroneLogBinding
import com.autel.drone.sdk.libbase.error.IAutelCode
import com.autel.drone.sdk.vmodelx.manager.DeviceLogManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.callback.CommonCallbacks
import com.autel.drone.sdk.log.SDKLog.i
import com.autel.drone.sdk.vmodelx.SDKManager
import com.autel.drone.sdk.vmodelx.module.droneLog.bean.*
import com.autel.drone.sdk.vmodelx.module.droneLog.data.*
import com.autel.drone.sdk.vmodelx.module.droneLog.db.DeviceLogDatabaseHelper
import com.autel.drone.sdk.vmodelx.module.droneLog.enums.DeviceLogEnum
import com.autel.drone.sdk.vmodelx.module.droneLog.enums.DeviceLogTypeEnum
import java.io.File
import java.text.SimpleDateFormat


class DroneLogFragment : AutelFragment()  {

    private lateinit var binding: FragmentDroneLogBinding
    private var queryResponseBen: DeviceLogQueryResponseBen? = null
    private var packResponseBean: DeviceLogPackResponseBean? = null
    private lateinit var helper: DeviceLogDatabaseHelper

    private var droneLog: DeviceSysLog? = null

    private var taskNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDroneLogBinding.inflate(layoutInflater)
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDroneLogBinding.inflate(inflater)
        initView()
        helper = DeviceLogDatabaseHelper(SDKManager.get().sContext!!)
        var drone = helper.getDeviceLog("12345678901")
        var lsit = helper.getPowerLog("12345678901", 395)
        var module = helper.getModuleLog("12345678901", 395,DeviceLogEnum.RCTRANSMISSION)

        var moduleLsit = helper.getAllModuleLogs("12345678901")
        return binding.root
    }

    companion object {
        private const val TAG = "DroneLog Test"
    }

    private fun initView() {
        var filePath = SDKManager.get().sContext?.getExternalFilesDir(null)?.absolutePath
        filePath += "/deviceLogTest"
        if (filePath != null) {
            DeviceLogManager.getInstance().configLogLocalPath(filePath)
        }
        binding.queryLogList.setOnClickListener {
            DeviceLogManager.getInstance().queryLogInfoList("12345678901", DeviceLogTypeEnum.DRONE, object : CommonCallbacks.CompletionCallbackWithParam<DeviceSysLog?> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSuccess(t: DeviceSysLog?) {

                    t?.let {
                        val dataDeviceLog = DeviceLog(it.type, it.sn, emptyList())
                        val powerLogs = mutableListOf<DeviceOncePowerLog>()
                        for (sysPower in it.powerLogList) {
                            val moduleLogs = mutableListOf<DeviceLogModule>()
                            val powerLog = DeviceOncePowerLog(sysPower.type,sysPower.sn,sysPower.index,sysPower.time, emptyList())
                            for (sysmodule in sysPower.moduleList) {
                                val  moduleLog = DeviceLogModule(sysmodule.mode, sysmodule.sn, sysmodule.index, sysmodule.type, sysmodule.time, sysmodule.size)
                                helper.updateModuleLog(moduleLog)
                                moduleLogs.add(moduleLog)
                            }
                            powerLog.moduleList = moduleLogs
                            powerLogs.add(powerLog)

                        }
                        dataDeviceLog.powerLogList = powerLogs
                    }
                    droneLog = t


                    i(TAG, "queryLogInfoList success")
                    var power1 = t!!.powerLogList.first()
                    val timestamp = power1.time
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val formattedString = sdf.format(timestamp)
                    i(TAG, "formattedString ${formattedString}")

                }

                override fun onFailure(error: IAutelCode, msg: String?) {
                    i(TAG, "queryLogInfo fair  error" + error.toString())
                }

            })
        }
        binding.logDownLoad.setOnClickListener {

            droneLog?.let {

                var power1 = it.powerLogList.last()
                DeviceLogManager.getInstance().downloadModuleLog(arrayListOf(power1.moduleList.first(),it.powerLogList.first().moduleList.first()),
                    object  : CommonCallbacks.DownLoadCallbackCompletionWithProgressAndFile<Double, DeviceSysLogModule> {
                        override fun onProgressUpdate(progress: Double, speed: Double) {

                            i(TAG, "onProgressUpdate $progress  $speed")
                        }
                        override fun onProgressFileUpdate(file: File?, mode: DeviceSysLogModule?) {
                            i(TAG, " logDownLoad onSuccess  ${file?.name}" )
                        }

                        override fun onSuccess() {
                            i(TAG, " logDownLoad onSuccess ")
                        }

                        override fun onFailure(error: IAutelCode) {
                            i(TAG, " logDownLoad onFailure ")
                        }

                    })
            }
        }

        binding.cancel.setOnClickListener {

            DeviceLogManager.getInstance().canceldownload()
        }


        binding.logDownLoad2.setOnClickListener {

            droneLog?.let {

                var power0 = it.powerLogList[0]
                var power1 = it.powerLogList[1]
                var power2 = it.powerLogList[2]
                var power3 = it.powerLogList[3]
                DeviceLogManager.getInstance().downloadPowerLog(
                    arrayListOf(power0,power1,power2,power3),
                    object  : CommonCallbacks.DownLoadCallbackCompletionWithProgressAndFile<Double, DeviceSysLogModule> {
                        override fun onProgressUpdate(progress: Double, speed: Double) {

                            i(TAG, "onProgressUpdate $progress  $speed")
                        }
                        override fun onProgressFileUpdate(file: File?, mode: DeviceSysLogModule?) {
                            i(TAG, " logDownLoad onSuccess  ${file?.name}" )
                        }

                        override fun onSuccess() {
                            i(TAG, " logDownLoad onSuccess ")
                        }

                        override fun onFailure(error: IAutelCode) {
                            i(TAG, " logDownLoad onFailure ")
                        }

                    })
            }
        }

    }
}