package com.autel.sdk.debugtools.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.autel.drone.sdk.log.SDKLog
import com.autel.drone.sdk.vmodelx.manager.DeviceManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.alink.enums.AirLinkMatchStatusEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.CameraWorkModeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.RecordStatusEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.TakePhotoModeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.camera.enums.TakePhotoStatusEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CalibrationEventEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CalibrationTypeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.GimbalCalState
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.RcDirectionEnum
import com.autel.sdk.debugtools.*
import com.autel.sdk.debugtools.adapter.ScenarioAdapter
import com.autel.sdk.debugtools.adapter.TestCaseResultAdapter
import com.autel.sdk.debugtools.databinding.FragmentScenerioTestsBinding
import com.autel.sdk.debugtools.helper.SdkFailureResultException
import com.autel.sdk.debugtools.uploadMsg.CameraStatusUpMsgManager
import com.autel.sdk.debugtools.uploadMsg.DebugToolUploadMsgManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * scenario testing fragment for drone and rcPad
 * Copyright: Autel Robotics
 * @author asif on 2022/12/17.
 */


open class ScenerioTestFragment : AutelFragment(), IMTestClickListener,
    ImuAndCompassCalibrationScenarioTest.PreformAction {
    companion object {
        const val TAG = "ScenerioTestFragment"
    }

    private lateinit var binding: FragmentScenerioTestsBinding
    private var dataList = ArrayList<String>()
    val scenarioVM: ScenarioVM by viewModels()
    var imSelectedTask = ""
    var imSelectedPosition = 0
    private var countDownTimer: CountDownTimer? = null
    private lateinit var resultAdapter: TestCaseResultAdapter
    private var isTimerRunning = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentScenerioTestsBinding.inflate(layoutInflater, container, false)
        setDataItems()
        setAdapter()
        listenData()
        handleListeners()
        // Setting up the result
        resetStep()
        return binding.root
    }

    private fun setAdapter() {
        val adapter = ScenarioAdapter(dataList, this)

        resultAdapter = TestCaseResultAdapter(requireActivity())
        binding.resultRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.resultRecyclerView.adapter = resultAdapter


        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun handleListeners() {
        binding.btStart.setOnClickListener {
            onTestSelectedForStart()
        }
        binding.btExit.setOnClickListener {
            onTestSelectedForExit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("LongLogTag")
    private fun listenData() {

        // Listening to paring status...
        DebugToolUploadMsgManager.airLinkMatchStatusData.observe(this, Observer { matchStatusEnum ->
            countDownTimer?.let { timer ->
                timer.cancel()
                isTimerRunning = false
            }
            when (matchStatusEnum) {

                AirLinkMatchStatusEnum.STATUS_FINISH -> {
                    SDKLog.i(TAG, AirLinkMatchStatusEnum.STATUS_FINISH.name)
                }

                AirLinkMatchStatusEnum.STATUS_UNKNOWN -> {
                    SDKLog.d(
                        "Remote control paring",
                        getString(R.string.debug_pairing_status_unknown)
                    )
                }
                AirLinkMatchStatusEnum.STATUS_PAIRING -> {
                    addToLogView(getString(R.string.debug_pairing))
                }
                AirLinkMatchStatusEnum.STATUS_SUC -> {
                    addToLogView(
                        getString(R.string.debug_pairing_success),
                        ScenarioTestResultStatusEnum.SUCCESS
                    )
                }
                AirLinkMatchStatusEnum.STATUS_FAILED -> {
                    addToLogView(
                        getString(R.string.debug_pairing_failed),
                        ScenarioTestResultStatusEnum.FAILED
                    )
                }
                else -> {

                }
            }
        })

        // Listening to remote control joysticks/rockers calibration...
        DebugToolUploadMsgManager.rockerCalibrationData.observe(this, Observer {
            countDownTimer?.let { timer ->
                timer.cancel()
                isTimerRunning = false
            }
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_top),
                it.getLeftStickState(RcDirectionEnum.TOP)
            )
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_left_top),
                it.getLeftStickState(RcDirectionEnum.LEFT_TOP)
            )
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_left),
                it.getLeftStickState(RcDirectionEnum.LEFT)
            )
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_left_bottom),
                it.getLeftStickState(RcDirectionEnum.LEFT_BOTTOM)
            )
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_bottom),
                it.getLeftStickState(RcDirectionEnum.BOTTOM)
            )
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_right_bottom),
                it.getLeftStickState(RcDirectionEnum.RIGHT_BOTTOM)
            )
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_right),
                it.getLeftStickState(RcDirectionEnum.RIGHT)
            )
            matchCalibration(
                getString(R.string.debug_left_stick),
                getString(R.string.debug_right_top),
                it.getLeftStickState(RcDirectionEnum.RIGHT_TOP)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_top),
                it.getRightStickState(RcDirectionEnum.TOP)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_left_top),
                it.getRightStickState(RcDirectionEnum.LEFT_TOP)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_left),
                it.getRightStickState(RcDirectionEnum.LEFT)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_left_bottom),
                it.getRightStickState(RcDirectionEnum.LEFT_BOTTOM)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_bottom),
                it.getRightStickState(RcDirectionEnum.BOTTOM)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_right_bottom),
                it.getRightStickState(RcDirectionEnum.RIGHT_BOTTOM)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_right),
                it.getRightStickState(RcDirectionEnum.RIGHT)
            )
            matchCalibration(
                getString(R.string.debug_right_stick),
                getString(R.string.debug_right_top),
                it.getRightStickState(RcDirectionEnum.RIGHT_TOP)
            )
            matchCalibration(
                getString(R.string.debug_left_thumbwheel),
                getString(R.string.debug_roll_left),
                it.getLeftThumbWheelState(RcDirectionEnum.ROLL_LEFT)
            )
            matchCalibration(
                getString(R.string.debug_left_thumbwheel),
                getString(R.string.debug_roll_right),
                it.getLeftThumbWheelState(RcDirectionEnum.ROLL_RIGHT)
            )
            matchCalibration(
                getString(R.string.debug_right_thumbwheel),
                getString(R.string.debug_roll_left),
                it.getRightThumbWheelState(RcDirectionEnum.ROLL_LEFT)
            )
            matchCalibration(
                getString(R.string.debug_right_thumbwheel),
                getString(R.string.debug_roll_right),
                it.getRightThumbWheelState(RcDirectionEnum.ROLL_RIGHT)
            )
        })

        //Listening to take photo status data...
        CameraStatusUpMsgManager.takePhotoStatusData.observe(this) {
            countDownTimer?.let { timer ->
                timer.cancel()
                isTimerRunning = false
            }
            var currentMode: TakePhotoModeEnum = it.currentMode
            var state: TakePhotoStatusEnum = it.state
            addToLogView(getString(R.string.debug_text_current_state) + state.name)
            when (state.value) {
                TakePhotoStatusEnum.START.value -> {

                }
                TakePhotoStatusEnum.END.value -> {
                    scenarioVM.getTakePhotoParameters { }
                }
                TakePhotoStatusEnum.UNKNOWN.value -> {

                }
            }
        }

        //Listening and reading photo data...
        scenarioVM.takePhotoParametersBean.observe(viewLifecycleOwner) {
            addToLogView(
                getString(R.string.debug_text_photo_type) + it.picType,
                ScenarioTestResultStatusEnum.SUCCESS
            )
        }

        //Listening to take video recording status data...
        CameraStatusUpMsgManager.recordStatusData.observe(this) {
            countDownTimer?.let { timer ->
                timer.cancel()
                isTimerRunning = false
            }
            var currentMode: CameraWorkModeEnum = it.currentMode
            var state: RecordStatusEnum = it.state
            addToLogView(getString(R.string.debug_text_current_state) + state.name)
            when (state.value) {
                RecordStatusEnum.START.value -> {
                    binding.btExit.isEnabled = true
                }
                RecordStatusEnum.RECORDING.value -> {
                    binding.btExit.isEnabled = true
                }
                RecordStatusEnum.END.value -> {
                    binding.btExit.isEnabled = false
                    scenarioVM.getRecordParameters { }
                }
                RecordStatusEnum.UNKNOWN.value -> {
                    binding.btExit.isEnabled = true
                }
            }
        }

        //Listening and reading Video data...
        scenarioVM._recordParametersBean.observe(viewLifecycleOwner) {
            addToLogView(
                getString(R.string.debug_text_video_format) + it.fileFormat,
                ScenarioTestResultStatusEnum.SUCCESS
            )
        }

        // Listening to Gimbal calibration status and steps
        scenarioVM.calibrationStatus.observe(viewLifecycleOwner) {
            if (it != CalibrationEventEnum.UNKNOWN) {
                when (it) {
                    CalibrationEventEnum.START -> {
                        addToLogView(getString(R.string.debug_gimbal_calibration_started))
                    }
                    CalibrationEventEnum.SUCCESS -> {
                        addToLogView(
                            getString(R.string.debug_gimbal_calibration_success),
                            ScenarioTestResultStatusEnum.SUCCESS
                        )
                    }
                    else -> {
                        addToLogView(
                            getString(R.string.debug_gimbal_calibration_failed),
                            ScenarioTestResultStatusEnum.FAILED
                        )
                    }
                }
            }
        }
        scenarioVM.calibrationStep.observe(viewLifecycleOwner) {
            if (it.calibrationType == CalibrationTypeEnum.GIMBAL_ANGLE) {
                when (it.calibrationPercent) {
                    100 -> {
                        addToLogView(
                            getString(R.string.debug_gimbal_calibration_success),
                            ScenarioTestResultStatusEnum.SUCCESS
                        )
                    }
                    else -> {
                        addToLogView(getString(R.string.debug_gimbal_calibration_progress) + " " + it.calibrationPercent)
                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun matchCalibration(
        rocker: String,
        direction: String,
        gimbalCalState: GimbalCalState
    ) {
        synchronized(this) {
            when (gimbalCalState) {
                GimbalCalState.NORMAL -> { //calibrated
                    addToLogView(
                        rocker + " --> " + direction + " : " + getString(R.string.debug_rockernormal),
                        ScenarioTestResultStatusEnum.SUCCESS
                    )
                }
                GimbalCalState.INVALID -> {  //The direction fluctuates, but the valid position is not reached
                    addToLogView(
                        rocker + " --> " + direction + " : " + getString(R.string.debug_rockerunvalid),
                        ScenarioTestResultStatusEnum.FAILED
                    )
                }
                GimbalCalState.UNKNOWN -> { //unknown calibration status
                }
                else -> {
                }
            }
        }


    }

    private fun addToLogView(
        message: String,
        status: ScenarioTestResultStatusEnum = ScenarioTestResultStatusEnum.PROCESSING
    ) {
        if (resultAdapter.itemCount > 200) {
            resultAdapter.clearData(ScenarioTestResultDataModel(getString(R.string.debug_logging_threshold_reached_clear)))
        }
        val curTime = SimpleDateFormat("HH:mm:ss").format(Date())
        binding.resultRecyclerView.scrollToPosition(resultAdapter.itemCount)
        resultAdapter.setData(ScenarioTestResultDataModel("$curTime : $message", status))
    }

    override fun testDetails(testName: String, position: Int) {
        imSelectedTask = testName
        imSelectedPosition = position
        initializeViewForCase()
    }

    /*
    * add test case name here
    * for adding into test list index
    */
    private fun setDataItems() {
        dataList.add(getString(R.string.debug_remote_controller_pairing))
        dataList.add(getString(R.string.debug_remote_controller_calibration))
        dataList.add(getString(R.string.debug_imu_calibration))
        dataList.add(getString(R.string.debug_compass_calibration))
        dataList.add(getString(R.string.debug_photograph))
        dataList.add(getString(R.string.debug_gimbal_calibration))
        dataList.add(getString(R.string.debug_videography))
    }

    /*
    * Change the text of buttons and title of window
    * for respective test case
    * */
    private fun initializeViewForCase() {
        resetStep()
        // Disabling button till the cases are not available
        showButtonLayout()
        binding.btExit.isEnabled = true
        binding.btExit.visibility = View.VISIBLE
        when (imSelectedTask) {
            getString(R.string.debug_remote_controller_pairing) -> {
                binding.tvOperateTitle.text = getString(R.string.debug_remote_controller_pairing)
                binding.btStart.text = getString(R.string.debug_text_start_paring)
                binding.btExit.text = getString(R.string.debug_text_end_paring)
                binding.resultRecyclerView.visibility = View.VISIBLE
                binding.msgTestContainer.visibility = View.VISIBLE
                binding.fragmentContainerView.visibility = View.GONE
                binding.btExit.isEnabled = false
            }
            getString(R.string.debug_remote_controller_calibration) -> {
                binding.tvOperateTitle.text =
                    getString(R.string.debug_remote_controller_calibration)
                binding.btStart.text = getString(R.string.debug_start_calibration)
                binding.btExit.text = getString(R.string.debug_exit_calibration)
                binding.resultRecyclerView.visibility = View.VISIBLE
                binding.msgTestContainer.visibility = View.VISIBLE
                binding.fragmentContainerView.visibility = View.GONE
            }
            getString(R.string.debug_imu_calibration) -> {
                // Disabling button till the cases are not available
                binding.btExit.isEnabled = false
                binding.resultRecyclerView.visibility = View.GONE
                binding.msgTestContainer.visibility = View.GONE
                binding.fragmentContainerView.visibility = View.VISIBLE
                parentFragmentManager.commit {
                    replace(
                        binding.fragmentContainerView.id,
                        ImuAndCompassCalibrationScenarioTest.newInstance(
                            CalibrationTypeEnum.IMU,
                            this@ScenerioTestFragment
                        )
                    )
                    addToBackStack(null)
                }
            }
            getString(R.string.debug_compass_calibration) -> {
                // Disabling button till the cases are not available
                binding.btExit.isEnabled = false
                binding.resultRecyclerView.visibility = View.GONE
                binding.msgTestContainer.visibility = View.GONE
                binding.fragmentContainerView.visibility = View.VISIBLE
                parentFragmentManager.commit {
                    replace(
                        binding.fragmentContainerView.id,
                        ImuAndCompassCalibrationScenarioTest.newInstance(
                            CalibrationTypeEnum.COMPASS,
                            this@ScenerioTestFragment
                        )
                    )
                    addToBackStack(null)
                }
            }
            getString(R.string.debug_photograph) -> {
                // Photo graph config
                binding.tvOperateTitle.text = getString(R.string.debug_photograph)
                binding.btStart.text = getString(R.string.debug_btn_take_picture)
                binding.btExit.visibility = View.GONE
                binding.resultRecyclerView.visibility = View.VISIBLE
                binding.msgTestContainer.visibility = View.VISIBLE
                binding.fragmentContainerView.visibility = View.GONE
            }
            getString(R.string.debug_gimbal_calibration) -> {
                // Compass calibration config
                binding.tvOperateTitle.text = getString(R.string.debug_gimbal_calibration)
                binding.btStart.text = getString(R.string.debug_start_calibration)
                binding.btExit.visibility = View.GONE
                binding.resultRecyclerView.visibility = View.VISIBLE
                binding.msgTestContainer.visibility = View.VISIBLE
                binding.fragmentContainerView.visibility = View.GONE
            }
            getString(R.string.debug_videography) -> {
                // Video graph config
                binding.tvOperateTitle.text = getString(R.string.debug_videography)
                binding.btStart.text = getString(R.string.debug_btn_record_video)
                binding.btExit.text = getString(R.string.debug_text_end_record)
                // Disabling button till the cases are not available
                binding.btExit.isEnabled = false
                binding.resultRecyclerView.visibility = View.VISIBLE
                binding.msgTestContainer.visibility = View.VISIBLE
                binding.fragmentContainerView.visibility = View.GONE
            }
        }
    }

    /*
    * Perform test case according to the selected test case
    * */
    private fun onTestSelectedForStart() {
        resetStep()
        when (imSelectedTask) {
            //Remote control paring scenario
            getString(R.string.debug_remote_controller_pairing) -> {
                lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
                    if (throwable is SdkFailureResultException) addToLogView(
                        throwable.msg ?: getString(
                            R.string.debug_error
                        )
                    )
                }) {
                    scenarioVM.startMatching {
                        scenarioTestStartBuffer(getString(R.string.debug_remote_controller_pairing))
                    }
                }
            }

            //Joysticks/rockers celebration scenario
            getString(R.string.debug_remote_controller_calibration) -> {
                lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
                    if (throwable is SdkFailureResultException) addToLogView(
                        throwable.msg ?: getString(
                            R.string.debug_error
                        )
                    )
                }) {
                    scenarioVM.enterControllerCalibration {
                        scenarioTestStartBuffer(getString(R.string.debug_remote_controller_calibration))
                    }
                }
            }

            //Photograph scenario
            getString(R.string.debug_photograph) -> {
                if (DeviceManager.getDeviceManager().isConnected()) {
                    scenarioVM.startTakingPhoto({
                        scenarioTestStartBuffer(getString(R.string.debug_photograph))
                    }, { e ->
                        addToLogView(getString(R.string.debug_start_photo_fail) + e)
                        showToast(getString(R.string.debug_start_photo_fail) + e)
                    })
                } else {
                    addToLogView(getString(R.string.debug_drone_not_connected))
                }
            }

            //Automatic gimbal calibration scenario
            getString(R.string.debug_gimbal_calibration) -> {
                if (DeviceManager.getDeviceManager().isConnected()) {
                    scenarioVM.startCalibration(CalibrationTypeEnum.GIMBAL_ANGLE, {
                        if (!it) {
                            addToLogView(
                                getString(R.string.debug_gimbal_calibration_failed),
                                ScenarioTestResultStatusEnum.FAILED
                            )
                        }
                    }) {
                        addToLogView(
                            getString(R.string.debug_gimbal_calibration_failed) + " :${it.message}",
                            ScenarioTestResultStatusEnum.FAILED
                        )
                        showToast(getString(R.string.debug_gimbal_calibration_failed) + " :${it.message}")
                    }
                }
            }

            //Videography scenario
            getString(R.string.debug_videography) -> {
                if (DeviceManager.getDeviceManager().isConnected()) {
                    scenarioVM.startRecording({
                        scenarioTestStartBuffer(getString(R.string.debug_videography))
                    }, { e ->
                        addToLogView(getString(R.string.debug_start_video_fail) + e)
                        showToast(getString(R.string.debug_start_video_fail) + e)
                    })
                } else {
                    addToLogView(getString(R.string.debug_drone_not_connected))
                }
            }

            //IMU calibration scenario
            getString(R.string.debug_imu_calibration) -> {
            }

            //Compass calibration scenario
            getString(R.string.debug_compass_calibration) -> {
            }
        }
    }

    /**
     * wait for 3 seconds for scenario to start and if no response is observing,
     * fail (reason:Problem with the master control) the scenario.
     */
    private fun scenarioTestStartBuffer(scenarioName: String) {
        addToLogView(scenarioName+getString(R.string.debug_scenario_initiated))
        var counter = 0
        countDownTimer?.let { timer ->
            if (isTimerRunning) {
                timer.cancel()
                isTimerRunning = false
            }
        }
        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(p0: Long) {
                isTimerRunning = true
                addToLogView(counter++.toString())
            }

            override fun onFinish() {
                isTimerRunning = false
                when (scenarioName) {
                    getString(R.string.debug_remote_controller_pairing) -> {
                        addToLogView(
                            getString(R.string.debug_text_paring_failed),
                            ScenarioTestResultStatusEnum.FAILED
                        )
                        scenarioVM.endMatching()
                    }
                    getString(R.string.debug_remote_controller_calibration) -> {
                        addToLogView(
                            getString(R.string.debug_text_calibration_failed),
                            ScenarioTestResultStatusEnum.FAILED
                        )
                        scenarioVM.exitControllerCalibration({
                            addToLogView(getString(R.string.debug_text_joystick_calibration_failed))
                        }, {
                            addToLogView("${getString(R.string.debug_text_joystick_calibration_failed)} :${it.message}")
                        })
                    }
                    getString(R.string.debug_photograph) -> {
                        addToLogView(
                            getString(R.string.debug_text_photograph_failed),
                            ScenarioTestResultStatusEnum.FAILED
                        )
                    }
                    getString(R.string.debug_videography) -> {
                        addToLogView(
                            getString(R.string.debug_text_video_failed),
                            ScenarioTestResultStatusEnum.FAILED
                        )
                    }

                }
            }

        }
    }

    private fun onTestSelectedForExit() {
        when (imSelectedTask) {
            getString(R.string.debug_exit_pairing) -> {
                lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
                    if (throwable is SdkFailureResultException) addToLogView(
                        throwable.msg ?: getString(
                            R.string.debug_error
                        )
                    )
                }) {
                    scenarioVM.endMatching()
                }
            }
            getString(R.string.debug_remote_controller_calibration) -> {
                lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
                    if (throwable is SdkFailureResultException) addToLogView(
                        throwable.msg ?: getString(
                            R.string.debug_error
                        )
                    )
                }) {
                    scenarioVM.exitControllerCalibration({}, {})
                }
            }
            getString(R.string.debug_videography) -> {
                lifecycleScope.launch(CoroutineExceptionHandler { _, throwable ->
                    if (throwable is SdkFailureResultException) addToLogView(
                        throwable.msg ?: getString(
                            R.string.debug_error
                        )
                    )
                }) {
                    scenarioVM.exitRecord {
                        binding.btExit.isEnabled = false
                    }
                }
            }
        }
    }

    override fun resetStep() {
        resultAdapter.clearData(ScenarioTestResultDataModel(getString(R.string.debug_result_will_be_displayed_here)))
    }

    override fun showToast(msgRes: String) {
        try {
            Toast.makeText(requireContext(), msgRes, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    /**
     * 是否有云台 如果没有安装云台，则不需要校准
     */
    override fun enableGimbal(): Boolean {
        return true
    }

    private fun showButtonLayout() {
        binding.msgTestContainer.visibility = View.VISIBLE
    }
}



