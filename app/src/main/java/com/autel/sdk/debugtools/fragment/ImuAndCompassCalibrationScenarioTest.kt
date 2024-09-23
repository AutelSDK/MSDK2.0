package com.autel.sdk.debugtools.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.autel.drone.demo.R
import com.autel.drone.demo.databinding.FragmentImuCalibrationScenarioTestBinding
import com.autel.drone.sdk.vmodelx.manager.DeviceManager
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CalibrationEventEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CalibrationTypeEnum
import com.autel.drone.sdk.vmodelx.manager.keyvalue.value.common.enums.CompassCalibrationStepEnum
import com.autel.sdk.debugtools.ScenarioVM
import com.autel.sdk.debugtools.uploadMsg.FlightControlUploadMsgManager

/**
 * - IMU and compass calibration scenario test
 * @see com.autel.setting.view.SettingIMUCalibrationActivity
 * @see com.autel.setting.view.SettingCompassCalibrationActivity
 * Copyright: Autel Robotics
 * @author viaks sharma on 2022/12/30.
 */
class ImuAndCompassCalibrationScenarioTest : Fragment() {

    lateinit var binding: FragmentImuCalibrationScenarioTestBinding
    private lateinit var preformAction: PreformAction
    private var currentStep = 0
    private var isInCal: Boolean = false
    val scenarioVM: ScenarioVM by viewModels()
    private var type: CalibrationTypeEnum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // helps identify the type of calibration to be preformed
            type = it.getSerializable("type") as CalibrationTypeEnum
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImuCalibrationScenarioTestBinding.inflate(layoutInflater)
        if (type == CalibrationTypeEnum.COMPASS)
            binding.startImuCalibration.text = getString(R.string.debug_start_compass_calibration)
        else
            binding.startImuCalibration.text = getString(R.string.debug_start_imu_calibration)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startImuCalibration.setOnClickListener {
            if (type == CalibrationTypeEnum.COMPASS)
                startCompassCal()
            else
                startImuCal()
        }
        initObservers()
    }

    /**
     * Observing calibration
     */
    private fun initObservers() {
        scenarioVM.calibrationStatus.observe(viewLifecycleOwner) {
            when (it) {
                CalibrationEventEnum.START -> {
                    if (type == CalibrationTypeEnum.COMPASS)
                        calInStep(CompassCalibrationStepEnum.STEP1)
                    else
                        imuNextStep(1, 0)
                }
                CalibrationEventEnum.SUCCESS -> {
                    allStateSucceeded()
                }
                CalibrationEventEnum.SAVE_DATA_FAILED -> {
                    calibrationFailed()
                }
                CalibrationEventEnum.INVALID_LEANS -> {}
                CalibrationEventEnum.INVALID_SHAKE -> {}
                CalibrationEventEnum.INVALID_MOTOR_WORKING -> {}
                CalibrationEventEnum.TIMEOUT -> {
                    calibrationFailed()
                }
                CalibrationEventEnum.NO_GPS -> {
                    calibrationFailed()
                    preformAction.showToast(getString(R.string.debug_text_no_gps))
                }
                CalibrationEventEnum.WRONG_DIRECTION -> {}
                else -> {

                }
            }
        }
        scenarioVM.calibrationStep.observe(viewLifecycleOwner) {
            if (it.calibrationType == CalibrationTypeEnum.IMU) {
                imuNextStep(it.imcStep.value, 0)
            }
            if (it.calibrationType == CalibrationTypeEnum.COMPASS) {
                calInStep(it.compassStep)
            }
        }

    }

    /**
     * Changing UI based on the compass calibration step
     * @param step
     */
    private fun calInStep(step: CompassCalibrationStepEnum) {
        when (step) {
            CompassCalibrationStepEnum.STEP1, CompassCalibrationStepEnum.ROTATE1 -> {
                setUpInitialUi()
                binding.calibrateInstruction.text =
                    (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_one))
                binding.calibratePositionText.text =
                    getString(R.string.debug_text_step_first_content)
                binding.calibratePositionImage.setImageResource(R.drawable.debug_campass_cal_step_1)

            }
            CompassCalibrationStepEnum.STEP2, CompassCalibrationStepEnum.ROTATE2 -> {
                setUpInitialUi()
                binding.calibrateInstruction.text =
                    (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_two))
                binding.calibratePositionText.text =
                    getString(R.string.debug_text_step_seconde_content)
                binding.calibratePositionImage.setImageResource(R.drawable.debug_campass_cal_step_2)

            }
            CompassCalibrationStepEnum.STEP3, CompassCalibrationStepEnum.ROTATE3 -> {
                setUpInitialUi()
                binding.calibrateInstruction.text =
                    (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_three))
                binding.calibratePositionText.text =
                    getString(R.string.debug_text_step_third_content)
                binding.calibratePositionImage.setImageResource(R.drawable.debug_campass_cal_step_3)
            }
            else -> {}
        }
    }


    /**
     * setting ui for showing the instruction image to user
     */
    private fun setUpInitialUi() {
        binding.calibrateStateLayout.visibility = View.VISIBLE
    }

    /**
     * initiate Imu calibration
     */
    private fun startImuCal() {
        binding.calibrateInstruction.background =
            ResourcesCompat.getDrawable(resources, R.color.debug_light_gray2, null)
        binding.calibrateInstruction.text = getString(R.string.debug_test_initial_message)
        binding.calibrateStateLayout.visibility = View.GONE
        preformAction.resetStep()
        isInCal = true
        currentStep = 0
        if (DeviceManager.getDeviceManager().isConnected()) {
            scenarioVM.startCalibration(CalibrationTypeEnum.IMU, {
                if (!it) {
                    calibrationFailed()
                }
            }) {
                calibrationFailed()
            }
        } else {
            binding.calibrateInstruction.text = getString(R.string.debug_drone_not_connected)
            binding.startImuCalibration.visibility = View.VISIBLE
        }
    }

    /**
     * initiate Compass calibration
     */
    private fun startCompassCal() {
        binding.calibrateInstruction.background =
            ResourcesCompat.getDrawable(resources, R.color.debug_light_gray2, null)
        binding.calibrateInstruction.text = getString(R.string.debug_test_initial_message)
        binding.calibrateStateLayout.visibility = View.GONE
        preformAction.resetStep()
        if (DeviceManager.getDeviceManager().isConnected()) {
            val isGpsValid =
                FlightControlUploadMsgManager.warningStateData.getValue()?.isGPSValid ?: false
            if (!isGpsValid) {
                preformAction.showToast(getString(R.string.debug_text_step_no_gps))
                return
            }
            scenarioVM.startCalibration(CalibrationTypeEnum.COMPASS, {
                if (!it) {
                    calibrationFailed()
                }
            }) {
                calibrationFailed()
            }
        } else {
            binding.calibrateInstruction.text = getString(R.string.debug_drone_not_connected)
            binding.startImuCalibration.visibility = View.VISIBLE
        }
    }

    /**
     * @param step Current calibration steps (1-6 represent: horizontal up, horizontal down, right side up, left side up, vertical up, vertical down)
     * @param status Current step execution status: 1 - start calibration; 2- Calibrating; 3 - the plane shakes; 4- Wrong orientation; 5- Calibration successful; 6 - Calibration failed
     */
    private fun imuNextStep(step: Int, status: Int) {
        if (step == 8) {// Calibration failed
            calibrationFailed()
            return
        }
        if (step == 7) {
            allStateSucceeded()
            return
        }
        if (preformAction.enableGimbal()) {
            if (step != currentStep/* && step == gimbalStep*/) {
                currentStep = step
                imuStepDone(step)
            } else {
                if (step == 7) {
                    allStateSucceeded()
                    return
                }
            }
        } else {
            if (step != currentStep) {
                currentStep = step
                imuStepDone(step)
            } else {
                if (step == 7) {
                    allStateSucceeded()
                    return
                }
            }
        }
    }

    /**
     * when the calibration fails
     */
    private fun calibrationFailed() {
        binding.startImuCalibration.visibility = View.VISIBLE
        isInCal = false
        preformAction.showToast(getString(R.string.debug_common_text_calibration_faild))
        binding.calibrateInstruction.text = getString(R.string.debug_common_text_calibration_faild)
        binding.calibrateStateLayout.visibility = View.GONE
        binding.calibrateInstruction.background =
            ResourcesCompat.getDrawable(resources, R.color.debug_color_red, null)
    }

    /**
     * when the calibration succeeds
     */
    private fun allStateSucceeded() {
        binding.startImuCalibration.visibility = View.VISIBLE
        isInCal = false
        if (type == CalibrationTypeEnum.COMPASS) {
            preformAction.showToast(getString(R.string.debug_compass_calibration_success))
            binding.calibrateInstruction.text =
                getString(R.string.debug_compass_calibration_success)
        } else {
            preformAction.showToast(getString(R.string.debug_imu_calibration_successful))
            binding.calibrateInstruction.text = getString(R.string.debug_imu_calibration_successful)
        }
        binding.calibrateStateLayout.visibility = View.GONE
        binding.calibrateInstruction.background =
            ResourcesCompat.getDrawable(resources, R.color.debug_color_4CAF50, null)
    }

    /**
     * Changing the ui based on the step of IMU calibration
     * @param step
     */
    private fun imuStepDone(step: Int) {
        if (step == 1 || step == 2 || step == 3 || step == 4 || step == 5 || step == 6)
            setUpInitialUi()
        else
            binding.calibrateStateLayout.visibility = View.GONE
        if (step == 1) {
            binding.calibrateInstruction.text =
                (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_one))
            binding.calibratePositionText.text = getString(R.string.debug_text_step_one_tips)
            binding.calibratePositionImage.setImageResource(R.drawable.debug_setting_icon_imu_1)
        }
        if (step == 2) {
            binding.calibrateInstruction.text =
                (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_two))
            binding.calibratePositionText.text = getString(R.string.debug_text_step_two_tips)
            binding.calibratePositionImage.setImageResource(R.drawable.debug_setting_icon_imu_2)
        }
        if (step == 3) {
            binding.calibrateInstruction.text =
                (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_three))
            binding.calibratePositionText.text = getString(R.string.debug_text_step_three_tips)
            binding.calibratePositionImage.setImageResource(R.drawable.debug_setting_icon_imu_3)
        }
        if (step == 4) {
            binding.calibrateInstruction.text =
                (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_four))
            binding.calibratePositionText.text = getString(R.string.debug_text_step_four_tips)
            binding.calibratePositionImage.setImageResource(R.drawable.debug_setting_icon_imu_4)
        }
        if (step == 5) {
            binding.calibrateInstruction.text =
                (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_five))
            binding.calibratePositionText.text = getString(R.string.debug_text_step_five_tips)
            binding.calibratePositionImage.setImageResource(R.drawable.debug_setting_icon_imu_5)
        }
        if (step == 6) {
            binding.calibrateInstruction.text =
                (getString(R.string.debug_follow_instruction_below) + "\n\n" + getString(R.string.debug_text_step_six))
            binding.calibratePositionText.text = getString(R.string.debug_text_step_six_tips)
            binding.calibratePositionImage.setImageResource(R.drawable.debug_setting_icon_imu_6)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment BlankDummyFragment.
         */
        @JvmStatic
        fun newInstance(type: CalibrationTypeEnum, preformAction: PreformAction) =
            ImuAndCompassCalibrationScenarioTest().apply {
                arguments = Bundle().apply {
                    putSerializable("type", type)
                }
                this.preformAction = preformAction
            }
    }

    interface PreformAction {
        fun resetStep()
        fun enableGimbal(): Boolean
        fun showToast(msg: String)
    }
}