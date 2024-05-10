package com.autel.sdk.debugtools.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.autel.drone.sdk.pbprotocol.constants.MessageTypeConstant
import com.autel.drone.sdk.vmodelx.extensions.test
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.CameraKey
import com.autel.drone.sdk.vmodelx.manager.keyvalue.key.base.KeyTools
import com.autel.drone.sdk.vmodelx.utils.ToastUtils.showToast
import com.autel.sdk.debugtools.*
import com.autel.sdk.debugtools.KeyItemHelper.processSubListLogic
import com.autel.sdk.debugtools.databinding.FragmentKeyListBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * key and value of key item fragment
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */

class KeyValueFragment : AutelFragment(), View.OnClickListener {

    companion object {
        private const val TAG = "KeyValueFragment"
        private const val LISTEN_RECORD_MAX_LENGTH = 6000
        private const val HIGH_FREQUENCY_KEY_SP_NAME = "highfrequencykey"
    }

    private val keyValueVM: KeyValueVM by activityViewModels()
    private var currentChannelType: ChannelType? = ChannelType.CHANNEL_TYPE_CAMERA

    private val logMessage = StringBuilder()
    private var currentKeyItem: KeyItem<*, *>? = null
    private val currentKeyTypeList: MutableList<KeyItem<*, *>> = ArrayList()
    private val currentKeyItemList: MutableList<KeyItem<*, *>> = ArrayList()
    private val keyItemsWrapper: KeyItemsWrapper<*, *> = KeyItemsWrapper<Any, Any>()
    private val data: MutableList<KeyItem<*, *>> = ArrayList()
    private var cameraParamsAdapter: KeyItemAdapter? = null
    private val batteryKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val wifiKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val bleList: List<KeyItem<*, *>> = ArrayList()
    private val gimbalKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val cameraKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val flightAssistantKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val flightParamsKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val flightControlKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val commonUploadList: MutableList<KeyItem<*, *>> = ArrayList()
    private val airlinkKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val aiServiceKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val trackKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val remoteIdKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val systemManagerKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val remoteControllerKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val productKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val ocuSyncKeyList: List<KeyItem<*, *>> = ArrayList()
    private val visionKeyList: List<KeyItem<*, *>> = ArrayList()
    private val rtkKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val radarKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val ntripKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val ltemoduleKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val mqttKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val appKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val mobileNetworkKeyList: List<KeyItem<*, *>> = ArrayList()
    private val mobileNetworkLinkRCKeyList: List<KeyItem<*, *>> = ArrayList()
    private val batteryBoxKeyList: List<KeyItem<*, *>> = ArrayList()
    private val onBoardKeyList: List<KeyItem<*, *>> = ArrayList()
    private val payloadKeyList: List<KeyItem<*, *>> = ArrayList()
    private val lidarKeyList: List<KeyItem<*, *>> = ArrayList()
    private val waypointKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val nestKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val AccessoriesKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val cloudAPiKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val missionManagerKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val autonomyKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val accurateRetakeKeyList : MutableList<KeyItem<*, *>> = ArrayList()
    private val rtmpKeyList : MutableList<KeyItem<*, *>> = ArrayList()
    private val rtcKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val commandCenterKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val hardwareDataSecurityKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val payLoadKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val dragonFishFCKeyList: MutableList<KeyItem<*, *>> = ArrayList()
    private val dragonFishCmdList: MutableList<KeyItem<*, *>> = ArrayList()


    private var keyValueSharedPreferences: SharedPreferences? = null
    private val selectMode = false
    private var isBatchTestSwitchOn = false
    private var isNowListenAll = false

    private lateinit var binding: FragmentKeyListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentKeyListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLocalData()
        initView()
        initRemoteData()
        keyValueVM.languageLiveData.observe(viewLifecycleOwner) {
            cameraParamsAdapter?.notifyDataSetChanged()
//            updateOperateText()
        }
        keyValueVM.displayLiveData.postValue(true)
    }

    private fun initLocalData() {
        data.clear()
        KeyItemDataUtil.initCameraKeyList(cameraKeyList)
        currentKeyItemList.addAll(cameraKeyList)
        data.addAll(currentKeyItemList)
        keyItemsWrapper.keyItemList = currentKeyItemList
        keyItemsWrapper.keyItemWrapperListener = keyItemWrapperListener
        cameraParamsAdapter = KeyItemAdapter(activity, data, itemClickCallback)
        keyValueSharedPreferences =
            activity?.getSharedPreferences(HIGH_FREQUENCY_KEY_SP_NAME, Context.MODE_PRIVATE)
    }


    private fun initView() {
        initViewAndListener()
//        binding.layoutKey.tvResult.setOnLongClickListener {
//            val cmb = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            cmb.setPrimaryClip(ClipData.newPlainText("label", binding.layoutKey.tvResult.text.toString()))
//            true
//        }
        binding.layoutKey.apply {
            btGet.setOnClickListener(this@KeyValueFragment)
            btSet.setOnClickListener(this@KeyValueFragment)
            btListen.setOnClickListener(this@KeyValueFragment)
            btAction.setOnClickListener(this@KeyValueFragment)
            btFrequencyReport.setOnClickListener(this@KeyValueFragment)
            btnClearlog.setOnClickListener(this@KeyValueFragment)
            btSetAll.setOnClickListener(this@KeyValueFragment)
            btGetAll.setOnClickListener(this@KeyValueFragment)
            btSetGetAll.setOnClickListener(this@KeyValueFragment)
            btListenAll.setOnClickListener(this@KeyValueFragment)
            paramTest.setOnClickListener(this@KeyValueFragment)
        }
        binding.swBatchTest.isChecked = isBatchTestSwitchOn
        binding.swBatchTest.setOnCheckedChangeListener { _, enable -> setBatchTestSwitch(enable) }
        setKeyCount(currentKeyItemList.size)
    }

    private fun initViewAndListener() {
        binding.tvOperateTitle.setOnClickListener {
            channelTypeFilterOperate()
        }
//        binding.ivActionArrow1.setOnClickListener {
//            channelTypeFilterOperate()
//        }
        binding.llFilterContainer.setOnClickListener {
            keyFilterOperate()
        }

        binding.etFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //Do Something
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //Do Something
            }

            override fun afterTextChanged(s: Editable) {
                cameraParamsAdapter?.filter?.filter(s.toString())
            }
        })
    }

    private fun initRemoteData() {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = cameraParamsAdapter
    }

    /**
     * key列表点击回调
     */
    private val itemClickCallback: KeyItemActionListener<KeyItem<*, *>?> = object :
        KeyItemActionListener<KeyItem<*, *>?> {

        override fun actionChange(keyItem: KeyItem<*, *>?) {
            if (keyItem == null) {
                return
            }
            initKeyInfo(keyItem)
            cameraParamsAdapter?.notifyDataSetChanged()
        }
    }

    private val keyItemWrapperListener: KeyItemWrapperListener<Any> =
        object : KeyItemWrapperListener<Any> {
            override fun actionChange(t: Any?) {
                binding.layoutKey.tvResult.text = appendLogMessageRecord(t.toString())
                scrollToBottom()
            }
        }

    /**
     * key操作结果回调
     */
    private val keyItemOperateCallBack: KeyItemActionListener<Any> =
        KeyItemActionListener<Any> { t -> //  processListenLogic();
            t?.let {
                binding.layoutKey.tvResult.text = appendLogMessageRecord(t.toString())
                scrollToBottom()
            }
        }


    private fun scrollToBottom() {
        with(binding.layoutKey.tvResult) {
            if (layout != null) {
                val scrollOffset = (this.layout.getLineTop(this.lineCount)
                        - this.height)
                if (scrollOffset > 0) {
                    this.scrollTo(0, scrollOffset)
                } else {
                    this.scrollTo(0, 0)
                }
            }
        }
    }

    private fun appendLogMessageRecord(appendStr: String?): String {
        val curTime = SimpleDateFormat("HH:mm:ss").format(Date())
        logMessage.append(curTime)
            .append(":")
            .append(appendStr)
            .append("\n")

        //长度限制
        var result = logMessage.toString()
        if (result.length > LISTEN_RECORD_MAX_LENGTH) {
            result = result.substring(result.length - LISTEN_RECORD_MAX_LENGTH)
        }
        return result
    }
//
    /**
     * 推送结果回调
     */
    private val pushCallback: KeyItemActionListener<String> =
        KeyItemActionListener<String> { t -> //  processListenLogic();
            Log.i("tv_result", " pushCallback")
            binding.layoutKey.tvResult.text = appendLogMessageRecord(t)
            scrollToBottom()
        }

    /**
     * 初始化Key的信息
     *
     * @param keyItem
     */
    private fun initKeyInfo(keyItem: KeyItem<*, *>) {
        currentKeyItem = keyItem
        currentKeyItem!!.setKeyOperateCallBack(keyItemOperateCallBack)
        binding.layoutKey.apply {
            tvName.text = keyItem.name
            btAddCommand
            btAddCommand.visibility = if (selectMode) View.VISIBLE else View.GONE
            btGpscoord.visibility = View.GONE

            tvVideoRecordTime.visibility = View.GONE
            tvName.text = keyItem.name
            btAddCommand.visibility = if (selectMode) View.VISIBLE else View.GONE
            btSet.isEnabled = currentKeyItem!!.canSet()
            btGet.isEnabled = currentKeyItem!!.canGet()
            btListen.isEnabled = currentKeyItem!!.canListen()
            btAction.isEnabled = currentKeyItem!!.canAction()
            btFrequencyReport.isEnabled = currentKeyItem!!.canReport()
        }
        keyItem.count = System.currentTimeMillis()
        resetSelected()
        keyValueSharedPreferences?.edit()?.putLong(keyItem.toString(), keyItem.count)?.apply()
        keyItem.isItemSelected = true

        if (currentKeyItem!!.canListen() && currentKeyItem!!.isListening) {
            binding.layoutKey.btListen.text = getString(R.string.debug_un_listen)
        } else {
            binding.layoutKey.btListen.text = getString(R.string.debug_listen)
        }
    }

    private fun resetSelected() {
        for (item in data) {
            if (item.isItemSelected) {
                item.isItemSelected = false
            }
        }
    }

    /**
     * 处理Listen显示控件
     */
    private fun processListenLogic() {
        if (currentKeyItem == null) {

            binding.layoutKey.btListen.text = getString(R.string.debug_listen)
            binding.layoutKey.tvName.text = ""
            return
        }

        if (currentKeyItem!!.listenHolder == null) {
            binding.layoutKey.btListen.text = getString(R.string.debug_listen)
        } else {
            binding.layoutKey.btListen.text = getString(R.string.debug_un_listen)
        }
    }

    /**
     * 根据不同类型入口，初始化不同数据
     */
    private fun processChannelInfo() {
        currentKeyTypeList.clear()
        currentKeyItemList.clear()
        var tips: String? = ""
        when (currentChannelType) {
//            ChannelType.CHANNEL_TYPE_BATTERY -> {
//                KeyItemDataUtil.initBatteryKeyList(batteryKeyList)
//                tips = getString(R.string.debug_battery)
//                currentKeyItemList.addAll(batteryKeyList)
//            }
            ChannelType.CHANNEL_TYPE_GIMBAL -> {
                KeyItemDataUtil.initGimbalKeyList(gimbalKeyList)
                tips = getString(R.string.debug_gimbal)
                currentKeyItemList.addAll(gimbalKeyList)
            }
            ChannelType.CHANNEL_TYPE_CAMERA -> {
                KeyItemDataUtil.initCameraKeyList(cameraKeyList)
                tips = getString(R.string.debug_camera)
                currentKeyItemList.addAll(cameraKeyList)
            }
            ChannelType.CHANNEL_TYPE_FLIGHT_PARAMS -> {
                tips = getString(R.string.debug_flight_params)
                KeyItemDataUtil.initFlightParamsKeyList(flightParamsKeyList)
                currentKeyItemList.addAll(flightParamsKeyList)
            }
            ChannelType.CHANNEL_TYPE_FLIGHT_CONTROL -> {
                tips = getString(R.string.debug_flight_control)
                KeyItemDataUtil.initFlightControllerKeyList(flightControlKeyList)
                currentKeyItemList.addAll(flightControlKeyList)
            }
            ChannelType.CHANNEL_TYPE_AIRLINK -> {
                tips = getString(R.string.debug_airlink)
                KeyItemDataUtil.initAirlinkKeyList(airlinkKeyList)
                currentKeyItemList.addAll(airlinkKeyList)
            }
            ChannelType.CHANNEL_TYPE_AISERVICE -> {
                tips = getString(R.string.debug_ai_service)
                KeyItemDataUtil.initAIServiceKeyList(aiServiceKeyList)
                currentKeyItemList.addAll(aiServiceKeyList)
            }
            ChannelType.CHANNEL_TYPE_REMOTE_CONTROLLER -> {
                tips = getString(R.string.debug_remote_controller)
                KeyItemDataUtil.initRemoteControllerKeyList(remoteControllerKeyList)
                currentKeyItemList.addAll(remoteControllerKeyList)
            }
            ChannelType.CHANNEL_TYPE_BLE -> {
                tips = getString(R.string.debug_ble)
                KeyItemDataUtil.initBleKeyList(bleList)
                currentKeyItemList.addAll(bleList)
            }
            ChannelType.CHANNEL_TYPE_PRODUCT -> {
                tips = getString(R.string.debug_product)
                KeyItemDataUtil.initProductKeyList(productKeyList)
                currentKeyItemList.addAll(productKeyList)
            }

            ChannelType.CHANNEL_TYPE_OCU_SYNC -> {
                tips = getString(R.string.debug_ocusync)
                KeyItemDataUtil.initOcuSyncKeyList(ocuSyncKeyList)
                currentKeyItemList.addAll(ocuSyncKeyList)
            }
            ChannelType.CHANNEL_TYPE_VISION -> {
                tips = getString(R.string.debug_vision)
                KeyItemDataUtil.initVisionKeyList(visionKeyList)
                currentKeyItemList.addAll(visionKeyList)
            }
            ChannelType.CHANNEL_TYPE_RTK -> {
                tips = getString(R.string.debug_rtk)
                KeyItemDataUtil.initRtkKeyList(rtkKeyList)
                currentKeyItemList.addAll(rtkKeyList)
            }
            ChannelType.CHANNEL_TYPE_RADAR -> {
                tips = getString(R.string.debug_radar)
                KeyItemDataUtil.initRadarKeyList(radarKeyList)
                currentKeyItemList.addAll(radarKeyList)
            }

            ChannelType.CHANNEL_TYPE_NTRIP -> {
                tips = getString(R.string.debug_ntrip)
                KeyItemDataUtil.initNtripKeyList(ntripKeyList)
                currentKeyItemList.addAll(ntripKeyList)
            }
            ChannelType.CHANNEL_TYPE_MQTT -> {
                tips = getString(R.string.debug_mqtt)
                KeyItemDataUtil.initMqttKeyList(mqttKeyList)
                currentKeyItemList.addAll(mqttKeyList)
            }

            ChannelType.CHANNEL_TYPE_LTEMODULE -> {
                tips = getString(R.string.debug_lte_module)
                KeyItemDataUtil.initLteModuleList(ltemoduleKeyList)
                currentKeyItemList.addAll(ltemoduleKeyList)
            }
            ChannelType.CHANNEL_TYPE_MOBILE_NETWORK -> {
                tips = getString(R.string.debug_mobile_network)
                KeyItemDataUtil.initMobileNetworkKeyList(mobileNetworkKeyList)
                KeyItemDataUtil.initMobileNetworkLinkRCKeyList(mobileNetworkLinkRCKeyList)
                currentKeyItemList.addAll(mobileNetworkKeyList)
                currentKeyItemList.addAll(mobileNetworkLinkRCKeyList)
            }
            ChannelType.CHANNEL_TYPE_ON_BOARD -> {
                tips = getString(R.string.debug_on_board)
                KeyItemDataUtil.initOnboardKeyList(onBoardKeyList)
                currentKeyItemList.addAll(onBoardKeyList)
            }
            ChannelType.CHANNEL_TYPE_ON_PAYLOAD -> {
                tips = getString(R.string.debug_payload)
                KeyItemDataUtil.initPayloadKeyList(payloadKeyList)
                currentKeyItemList.addAll(payloadKeyList)
            }
            ChannelType.CHANNEL_TYPE_LIDAR -> {
                tips = getString(R.string.debug_lidar)
                KeyItemDataUtil.initLidarKeyList(lidarKeyList)
                currentKeyItemList.addAll(lidarKeyList)
            }
            ChannelType.CHANNEL_TYPE_FLIGHT_MISSION -> {
                tips = getString(R.string.debug_channel_type_flightmission)
                KeyItemDataUtil.initMissionKeyList(waypointKeyList)
                currentKeyItemList.addAll(waypointKeyList)
            }
            ChannelType.COMMON_UPLOAD -> {
                tips = getString(R.string.debug_common)
                KeyItemDataUtil.initCommonUploadKeyList(commonUploadList)
                currentKeyItemList.addAll(commonUploadList)
            }
            ChannelType.CHANNEL_TYPE_AI_TRACK -> {
                tips = getString(R.string.debug_ai_track)
                KeyItemDataUtil.initTrackingKeyList(trackKeyList)
                currentKeyItemList.addAll(trackKeyList)
            }
            ChannelType.CHANNEL_TYPE_REMOTE_ID -> {
                tips = getString(R.string.debug_remote_id)
                KeyItemDataUtil.initRemoteIdKeyList(remoteIdKeyList)
                currentKeyItemList.addAll(remoteIdKeyList)
            }
            ChannelType.SYSTEM_MANAGER -> {
                tips = getString(R.string.debug_system_manager)
                KeyItemDataUtil.initSystemManagerKeyList(systemManagerKeyList)
                currentKeyItemList.addAll(systemManagerKeyList)
            }
            ChannelType.CHANNEL_TYPE_NEST -> {
                tips = getString(R.string.debug_nest)
                KeyItemDataUtil.initNestKeyList(nestKeyList)
                currentKeyItemList.addAll(nestKeyList)
            }
            ChannelType.MISSION_MANAGER -> {
                tips = getString(R.string.debug_mission_manager)
                KeyItemDataUtil.initMissionManagerKeyList(missionManagerKeyList)
                currentKeyItemList.addAll(missionManagerKeyList)
            }
            ChannelType.ACCESSORIES_PROXY -> {
                tips = getString(R.string.debug_accessories_proxy)
                KeyItemDataUtil.initAccessoriesKeyListList(AccessoriesKeyList)
                currentKeyItemList.addAll(AccessoriesKeyList)
            }
            ChannelType.CLOUD_API -> {
                tips = getString(R.string.debug_cloud_api)
                KeyItemDataUtil.initCloudAPiKeyListList(cloudAPiKeyList)
                currentKeyItemList.addAll(cloudAPiKeyList)
            }
            ChannelType.CHANNEL_TYPE_AUTONOMY -> {
                tips = getString(R.string.debug_autonomy)
                KeyItemDataUtil.initAutonomyKeyList(autonomyKeyList)
                currentKeyItemList.addAll(autonomyKeyList)
            }
            ChannelType.CHANNEL_TYPE_ACCURATE_RETAKE -> {
                tips = getString(R.string.debug_accurate_retake)
                KeyItemDataUtil.initAccurateRetakeKeyList(accurateRetakeKeyList)
                currentKeyItemList.addAll(accurateRetakeKeyList)
            }
            ChannelType.CHANNEL_TYPE_RTMP -> {
                tips = getString(R.string.debug_rtmp)
                KeyItemDataUtil.initRtmpKeyList(rtmpKeyList)
                currentKeyItemList.addAll(rtmpKeyList)
            }
            ChannelType.CHANNEL_TYPE_COMMAND_CENTER -> {
                tips = getString(R.string.debug_command_center)
                KeyItemDataUtil.initCommandCenterKeyList(commandCenterKeyList)
                currentKeyItemList.addAll(commandCenterKeyList)
            }
            ChannelType.CHANNEL_TYPE_WIFI -> {
                tips = getString(R.string.debug_wifi)
                KeyItemDataUtil.initWifiKeyList(wifiKeyList)
                currentKeyItemList.addAll(wifiKeyList)
            }
            ChannelType.CHANNEL_TYPE_RTC -> {
                tips = getString(R.string.debug_rtc)
                KeyItemDataUtil.initRtcKeyList(rtcKeyList)
                currentKeyItemList.addAll(rtcKeyList)
            }
            ChannelType.HARDWARE_DATA_SECURITY -> {
                tips = getString(R.string.debug_data_security)
                KeyItemDataUtil.initHardwareDataSecurityListList(hardwareDataSecurityKeyList)
                currentKeyItemList.addAll(hardwareDataSecurityKeyList)
            }
            ChannelType.PAYLOAD_KEY -> {
                tips = getString(R.string.debug_payload)
                KeyItemDataUtil.initPayloadKeyKeyList(payLoadKeyList)
                currentKeyItemList.addAll(payLoadKeyList)
            }
            else -> {}
        }
        for (item in currentKeyItemList) {
            item.isItemSelected = false
            val count = keyValueSharedPreferences?.getLong(item.toString(), 0L)
            if (count != null && count != 0L) {
                item.count = count
            }
        }
        binding.tvOperateTitle.text = tips
        setDataWithCapability(false)
    }


    private fun setDataWithCapability(enable: Boolean) {
        val showList: MutableList<KeyItem<*, *>> = ArrayList(currentKeyItemList)
        data.clear()
        data.addAll(showList)
        resetSearchFilter()
        setKeyCount(showList.size)
        resetSelected()
        cameraParamsAdapter?.notifyDataSetChanged()
    }

    private fun setBatchTestSwitch(enable: Boolean) {
        with(binding.layoutKey) {
            if (enable) {
                msgTestContainer.visibility = View.GONE
                msgBatchTestContainer.visibility = View.VISIBLE
                binding.tvBatchTest.text = getString(R.string.debug_batch_test)
                isBatchTestSwitchOn = true
            } else {
                msgTestContainer.visibility = View.VISIBLE
                msgBatchTestContainer.visibility = View.GONE
                binding.tvBatchTest.text = getString(R.string.debug_single_test)
                isBatchTestSwitchOn = false
            }
        }
    }


    /**
     * 清空search框
     */
    private fun resetSearchFilter() {
        binding.etFilter.setText("")
        cameraParamsAdapter?.filter?.filter("")
    }

    private fun setKeyCount(count: Int) {
        binding.tvCount.text = "(${count})"
    }

    override fun onClick(view: View) {
        if ((Util.isBlank(binding.layoutKey.tvName.text.toString()) || currentKeyItem == null)
            && view.id != R.id.bt_set_all
            && view.id != R.id.bt_get_all
            && view.id != R.id.bt_set_get_all
            && view.id != R.id.btn_clearlog
            && view.id != R.id.bt_listen_all
            && view.id != R.id.param_test
        ) {
            showToast(getString(R.string.debug_please_select_key_first))
            return
        }
        setKeyInfo()

        when (view.id) {
            R.id.bt_get -> {
                get()
            }
            R.id.bt_unlistenall -> {
                unListenAll()
            }
            R.id.bt_set -> {
                set()
            }
            R.id.bt_listen -> {
                listen()
            }
            R.id.bt_action -> {
                action()
            }
            R.id.bt_frequency_report -> {
                report()
            }
            R.id.btn_clearlog -> {
                binding.layoutKey.tvResult.text = ""
                logMessage.delete(0, logMessage.length)
            }
            R.id.bt_set_all -> {
                setAll()
            }
            R.id.bt_get_all -> {
                getAll()
            }
            R.id.bt_set_get_all -> {
                setGetAll()
            }
            R.id.bt_listen_all -> {
                listenAll()
            }
            R.id.param_test -> {
                paramTest()
            }
        }

    }

    private fun setAll() {
        currentKeyItemList.filter {
            it.canSet()
        }.run {
            setList(currentKeyItemList)
            binding.layoutKey.tvName.text = getString(R.string.debug_set_all)
        }
    }

    private fun getAll() {
        currentKeyItemList.filter {
            it.canGet()
        }.run {
            getList(currentKeyItemList)
            binding.layoutKey.tvName.text = getString(R.string.debug_get_all)
        }
    }

    private fun setGetAll() {
        // TODO::
        currentKeyItemList.filter {
            it.canSet() && it.canGet()
        }.forEach {
            val createKey = KeyTools.createKey(it.keyInfo)
            val param =
                it.keyInfo.typeConverter.fromJsonStr(it.keyInfo.typeConverter.getJsonStr() ?: "")

            val createKey1 = KeyTools.createKey(CameraKey.KeyCameraDeviceInfo)
            val param1 = CameraKey.KeyCameraDeviceInfo.typeConverter.fromJsonStr(
                CameraKey.KeyCameraDeviceInfo.typeConverter.getJsonStr() ?: ""
            )

//            instance.setValue(createKey, param, object : CommonCallbacks.CompletionCallback {
//                override fun onSuccess() {
//                    instance.getValue(createKey, object : CommonCallbacks.CompletionCallbackWithParam<*>() {
//
//                    })
//                }
//                override fun onFailure(code: IAutelCode, msg: String?) {
//                    TODO("Not yet implemented")
//                }
//            })
        }
    }

    private fun listenAll() {
        currentKeyItemList.filter {
            it.canListen()
        }.forEach {
            it.setKeyOperateCallBack(keyItemOperateCallBack)
            if (isNowListenAll) {
                it.cancelListen(it.keyInfo)
            } else {
                it.listen(it.keyInfo)
            }
        }

        binding.layoutKey.tvName.text = getString(R.string.debug_listen_all)
        if (isNowListenAll) {
            binding.layoutKey.btListenAll.text = getString(R.string.debug_un_listen_all)
        } else {
            binding.layoutKey.btListenAll.text = getString(R.string.debug_listen_all)
        }

        isNowListenAll = !isNowListenAll
    }

    private fun paramTest() {
        val falseList = mutableListOf<String>()
        val trueList = mutableListOf<String>()
        currentKeyItemList.forEach() {
            val result = it.keyInfo.typeConverter.test()
            if (result) {
                trueList.add(it.keyInfo.keyName + getString(R.string.debug_analysis_and_antianalysis_normal))
            } else {
                val name = it.keyInfo.keyName + getString(R.string.debug_parse_or_antiparse_exception)
                falseList.add(name)
            }
        }
        falseList.forEach {
            keyItemOperateCallBack.actionChange(it)
        }
        trueList.forEach {
            keyItemOperateCallBack.actionChange(it)
        }
    }

    /**
     * key列表条件过滤
     */
    private fun keyFilterOperate() {
    }


    private fun channelTypeFilterOperate() {
        val showChannelList: MutableList<ChannelType>
        val capabilityChannelList = arrayOf(
            ChannelType.CHANNEL_TYPE_CAMERA,
            ChannelType.CHANNEL_TYPE_FLIGHT_MISSION,
            ChannelType.CHANNEL_TYPE_FLIGHT_CONTROL,
            ChannelType.CHANNEL_TYPE_GIMBAL,
            ChannelType.CHANNEL_TYPE_AIRLINK,
            ChannelType.CHANNEL_TYPE_AISERVICE,
            ChannelType.CHANNEL_TYPE_FLIGHT_PARAMS,
            ChannelType.CHANNEL_TYPE_VISION,
            ChannelType.CHANNEL_TYPE_REMOTE_CONTROLLER,
            ChannelType.COMMON_UPLOAD,
            ChannelType.CHANNEL_TYPE_AI_TRACK,
            ChannelType.CHANNEL_TYPE_REMOTE_ID,
            ChannelType.CHANNEL_TYPE_NEST,
            ChannelType.SYSTEM_MANAGER,
            ChannelType.MISSION_MANAGER,
            ChannelType.ACCESSORIES_PROXY,
            ChannelType.CLOUD_API,
            ChannelType.CHANNEL_TYPE_AUTONOMY,
            ChannelType.CHANNEL_TYPE_RADAR,
            ChannelType.CHANNEL_TYPE_RTK,
            ChannelType.CHANNEL_TYPE_NTRIP,
            ChannelType.CHANNEL_TYPE_LTEMODULE,
            ChannelType.CHANNEL_TYPE_ACCURATE_RETAKE,
            ChannelType.CHANNEL_TYPE_RTMP,
            ChannelType.CHANNEL_TYPE_COMMAND_CENTER,
            ChannelType.CHANNEL_TYPE_WIFI,
            ChannelType.HARDWARE_DATA_SECURITY,
            ChannelType.PAYLOAD_KEY,
            ChannelType.CHANNEL_TYPE_RTC,
        )
        showChannelList = capabilityChannelList.toMutableList()
        KeyValueDialogUtil.showChannelFilterListWindow(
            binding.tvOperateTitle,
            showChannelList
        ) { channelType ->
            currentChannelType = channelType
            currentKeyItem = null
            processChannelInfo()
            processListenLogic()
        }
    }

    private fun setKeyInfo() {}

    /**
     * 获取操作
     */
    private fun get() {
        get(currentKeyItem)
    }

    /**
     * 获取操作
     */
    private fun get(keyItem: KeyItem<*, *>?) {
        if (!keyItem?.canGet()!!) {
            showToast(getString(R.string.debug_not_support_get))
            return
        }
        keyItem.setKeyOperateCallBack(keyItemOperateCallBack)
        keyItem.doGet()
    }

    /**
     * 批量获取操作
     */
    private fun getList(keyItems: List<KeyItem<*, *>?>) {
        keyItemsWrapper.getList(keyItems)
        keyItemsWrapper.keyItemWrapperListener = keyItemWrapperListener
    }

    /**
     * 批量设置和获取操作
     */
    private fun setAndGetList(keyItems: List<KeyItem<*, *>?>) {
        keyItemsWrapper.getList(keyItems)
        keyItemsWrapper.keyItemWrapperListener = keyItemWrapperListener
    }

    /**
     * 批量获取操作
     */
    private fun setList(keyItems: List<KeyItem<*, *>?>) {
        keyItemsWrapper.setList(keyItems)
        keyItemsWrapper.keyItemWrapperListener = keyItemWrapperListener
    }

    private fun unListenAll() {
        release()
        processListenLogic()
    }

    /**
     * Listen操作
     */
    private fun listen() {
        if (!currentKeyItem?.canListen()!!) {
            showToast(getString(R.string.debug_not_support_listen))
            return
        }
        currentKeyItem!!.setPushCallBack(pushCallback)
        currentKeyItem!!.setKeyOperateCallBack(keyItemOperateCallBack)
        val listenHolder = currentKeyItem!!.listenHolder

        if (listenHolder == null) {
            currentKeyItem!!.listen(this)
            binding.layoutKey.btListen.text=  getString(R.string.debug_un_listen)

        } else if (listenHolder is KeyValueFragment) {
            currentKeyItem!!.cancelListen(this)
            binding.layoutKey.btListen.text=  getString(R.string.debug_listen)
        }
        processListenLogic()
    }

    /**
     * 设置操作
     */
    private fun set() {
        set(currentKeyItem)
    }

    /**
     * 设置操作
     */
    private fun set(keyItem: KeyItem<*, *>?, useDefaultData: Boolean = false) {
        if (!keyItem?.canSet()!!) {
            showToast(getString(R.string.debug_not_support_set))
            return
        }
        keyItem.setKeyOperateCallBack(keyItemOperateCallBack)
        if (useDefaultData) {
            keyItem.doSet(keyItem.paramJsonStr)
        } else if (keyItem.subItemMap.isNotEmpty()) {
            processSubListLogic(
                binding.layoutKey.btSet,
                keyItem.param,
                keyItem.subItemMap as Map<String?, List<EnumItem>>,
                object :
                    KeyItemActionListener<String?> {
                    override fun actionChange(paramJsonStr: String?) {
                        if (Util.isBlank(paramJsonStr)) {
                            return
                        }
                        keyItem.doSet(paramJsonStr)
                    }
                })
        } else {
            KeyValueDialogUtil.showInputDialog(
                activity,
                keyItem,
                object :
                    KeyItemActionListener<String?> {
                    override fun actionChange(s: String?) {
                        if (Util.isBlank(s)) {
                            return
                        }
                        keyItem.doSet(s)
                    }
                })
        }
    }

    /**
     * 动作操作
     */
    private fun action() {
        if (!currentKeyItem?.canAction()!!) {
            showToast(getString(R.string.debug_not_support_action))
            return
        }
        currentKeyItem!!.setKeyOperateCallBack(keyItemOperateCallBack)
        if (currentKeyItem?.subItemMap!!.isNotEmpty()) {
            processSubListLogic(
                binding.layoutKey.btSet,
                currentKeyItem?.param,
                currentKeyItem?.subItemMap as Map<String?, List<EnumItem>>,
                object :
                    KeyItemActionListener<String?> {
                    override fun actionChange(paramJsonStr: String?) {
                        if (Util.isBlank(paramJsonStr)) {
                            return
                        }
                        currentKeyItem!!.doAction(paramJsonStr)
                    }
                })
        } else if (currentKeyItem!!.paramJsonStr == null) {
            currentKeyItem!!.doAction(null)
        } else {
            KeyValueDialogUtil.showInputDialog(
                activity,
                currentKeyItem
            ) { s -> currentKeyItem!!.doAction(s) }
        }
    }

    private fun String.toIntOrUseDefault(defaultValue: Int): Int {
        return try {
            toInt()
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }


    /**
     * 上报操作
     */
    private fun report() {
        if (!currentKeyItem?.canReport()!!) {
            showToast(getString(R.string.debug_not_support_report))
            return
        }
        currentKeyItem!!.setKeyOperateCallBack(keyItemOperateCallBack)

        val spSubtypeValue = binding.layoutKey.spSubtype.text.toString()
        val srcNodeId = spSubtypeValue.toIntOrUseDefault(1)

        val spSubIndexValue = binding.layoutKey.spSubindex.text.toString()
        val dstNodeId = spSubIndexValue.toIntOrUseDefault(0)

        currentKeyItem?.keyInfo?.setSrcNodeId(srcNodeId)
        currentKeyItem?.keyInfo?.setDestNodeId(dstNodeId)


        if(currentKeyItem?.keyInfo?.keyName == MessageTypeConstant.PAYLOAD_LIGHT_SPEAKER_RUNTIME_MESSAGE){
            showToast("开始执行喊话")

            var index = 0
            while(currentKeyItem?.isStop == false){
                if (!TextUtils.isEmpty(currentKeyItem?.paramJsonStr)) {
                    currentKeyItem!!.doReport(currentKeyItem?.paramJsonStr)
                    Thread.sleep(100L)
                    index++
                }
            }

            currentKeyItem?.isStop = false

            showToast("执行喊话结束  $index 次")

        } else{
            if (currentKeyItem?.subItemMap!!.isNotEmpty()) {
                processSubListLogic(
                    binding.layoutKey.btSet,
                    currentKeyItem?.param,
                    currentKeyItem?.subItemMap as Map<String?, List<EnumItem>>,
                    object :
                        KeyItemActionListener<String?> {
                        override fun actionChange(paramJsonStr: String?) {
                            if (Util.isBlank(paramJsonStr)) {
                                return
                            }
                            currentKeyItem!!.doReport(paramJsonStr)
                        }
                    })
            } else if (currentKeyItem!!.paramJsonStr == null) {
                currentKeyItem!!.doReport(null)
            } else {
                KeyValueDialogUtil.showInputDialog(
                    activity,
                    currentKeyItem
                ) { s -> currentKeyItem!!.doReport(s) }
            }
        }
    }

    /**
     * 注销Listen，移除业务回调
     *
     * @param list
     */
    private fun releaseKeyInfo(list: MutableList<KeyItem<*, *>>?) {
        if (list == null) {
            return
        }
        for (item in list) {
            item.removeCallBack()
            item.cancelListen(this)
        }
    }

    private fun release() {
        if (currentKeyItem != null) {
            currentKeyItem!!.cancelListen(this)
        }
        releaseKeyInfo(batteryKeyList)
        releaseKeyInfo(gimbalKeyList)
        releaseKeyInfo(cameraKeyList)
        releaseKeyInfo(wifiKeyList)
        releaseKeyInfo(flightAssistantKeyList)
        releaseKeyInfo(flightControlKeyList)
        releaseKeyInfo(airlinkKeyList)
        releaseKeyInfo(productKeyList)
        releaseKeyInfo(rtkKeyList)
        releaseKeyInfo(remoteControllerKeyList)
        releaseKeyInfo(radarKeyList)
        releaseKeyInfo(appKeyList)
        releaseKeyInfo(accurateRetakeKeyList)
        releaseKeyInfo(payLoadKeyList)
        releaseKeyInfo(cloudAPiKeyList)
        releaseKeyInfo(rtcKeyList)
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
        // 清除该页面的注册的监听，不能直接清除全部监听，那样app的监听也没了
        isNowListenAll = true
        listenAll()
//        currentKeyItem?.cancelAllListen()
        keyValueVM.displayLiveData.postValue(false)
    }
}