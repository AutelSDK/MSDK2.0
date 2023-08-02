package com.autel.sdk.debugtools.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.autel.drone.sdk.store.SDKStorage
import com.autel.drone.sdk.vmodelx.constants.MmkvConstants
import com.autel.drone.sdk.vmodelx.constants.SDKConstants
import com.autel.drone.sdk.vmodelx.utils.ToastUtils
import com.autel.sdk.debugtools.*
import com.autel.sdk.debugtools.activity.FragmentPageInfo
import com.autel.sdk.debugtools.activity.FragmentPageInfoItem
import com.autel.sdk.debugtools.databinding.FragMainPageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main fragment for debugtools sub module
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */
class MainFragment : AutelFragment() {
    private lateinit var binding: FragMainPageBinding
    private val msdkCommonOperateVm: MSDKCommonOperateVm by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragMainPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMainList()
        initBtnClickListener()
    }

    override fun updateTitle() {
        msdkInfoVm.mainTitle.value = getString(R.string.debug_testing_tools)
    }

    private fun initMainList() {
        val adapter = MainFragmentListAdapter { item -> adapterOnItemClick(item) }
        binding.viewList.adapter = adapter
        binding.viewList.layoutManager = LinearLayoutManager(context)
        msdkCommonOperateVm.mainPageInfoList.observe(viewLifecycleOwner) {
            it?.let {
                val itemList = LinkedHashSet<FragmentPageInfoItem>()
                for (info: FragmentPageInfo in it) {
                    itemList.addAll(info.items)
                    addDestination(info.vavGraphId)
                }
                adapter.submitList(itemList.toList())
            }
        }
    }

    private fun addDestination(id: Int) {
        view?.let {
            val v = Navigation.findNavController(it).navInflater.inflate(id)
            Navigation.findNavController(it).graph.addAll(v)
        }
    }

    private fun adapterOnItemClick(item: FragmentPageInfoItem) {
        view?.let {
            Navigation.findNavController(it)
                .navigate(item.id, bundleOf(MAIN_FRAGMENT_PAGE_TITLE to item.title))
        }
    }

    private fun initBtnClickListener() {
        var ip = SDKStorage.getStringValue(MmkvConstants.IP)
        if (TextUtils.isEmpty(ip)) {
            ip = SDKConstants.IP
        }
        binding.tvCurrentIp.text = getString(R.string.debug_current_ip) + "$ip"
        binding.updateIp.setOnClickListener {
            val inputIp = binding.ipEt.text.toString()
            if (TextUtils.isEmpty(inputIp)) {
                ToastUtils.showToast(getString(R.string.debug_please_enter_ip))
            } else if (inputIp == SDKConstants.IP) {
                SDKSingleButtonDialog(requireContext()).apply {
                    setMessage(getString(R.string.debug_connect_real_machine_ip_click_button))
                    show()
                }
            } else {
                SDKTwoButtonDialog(requireContext()).apply {
                    setMessage(getString(R.string.debug_sure_restart_app))
                    setRightBtnListener {
                        SDKStorage.setStringValue(MmkvConstants.IP, inputIp)
                        SDKStorage.setBooleanValue(MmkvConstants.IS_MOCK_DEVICE, true)
                        restartApplication()
                    }
                    show()
                }
            }
        }
        binding.tvConnectRemote.setOnClickListener {
            if (ip == SDKConstants.IP) {
                ToastUtils.showToast(getString(R.string.debug_current_connection_real_machine))
            } else {
                SDKTwoButtonDialog(requireContext()).apply {
                    setMessage(getString(R.string.debug_sure_restart_app))
                    setRightBtnListener {
                        SDKStorage.setStringValue(MmkvConstants.IP, SDKConstants.IP)
                        SDKStorage.setBooleanValue(MmkvConstants.IS_MOCK_DEVICE, false)
                        restartApplication()
                    }
                    show()
                }
            }
        }

        binding.netWorkCheckBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val inputIp = binding.ipEt.text.toString()//"10.250.12.155"
                val isConnect = NetworkDetectUtil.getInstance().ping(inputIp, 3, 5)
                withContext(Dispatchers.Main) {
                    // ToastUtils.showToast("is ping $inputIp " + if (isConnect) "success" else "failed")
                    val strMessage =
                        getString(R.string.debug_is_pin) + " " + inputIp + " " + if (isConnect) getString(
                            R.string.debug_success
                        ) else getString(R.string.debug_failed)
                    ToastUtils.showToast(strMessage)
                }
            }
        }

        binding.useTestDataRb.isChecked = isUserDebugDataForApp()
        binding.useRealDataRb.isChecked = !isUserDebugDataForApp()
        binding.useDebugForAppRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.use_real_data_rb -> isUserDebugDataForApp(false)
                R.id.use_test_data_rb -> isUserDebugDataForApp(true)
                else -> isUserDebugDataForApp(false)
            }
        }
    }

    private fun restartApplication() {
        //杀掉以前进程
        android.os.Process.killProcess(android.os.Process.myPid())
        requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
            ?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(it)
            }
    }

    private fun isUserDebugDataForApp(useDebugData: Boolean) {
        SDKStorage.setBooleanValue(MmkvConstants.IS_USE_DEBUG_DATA_FOR_APP, useDebugData)
    }

    private fun isUserDebugDataForApp(): Boolean {
        return SDKStorage.getBooleanValue(MmkvConstants.IS_USE_DEBUG_DATA_FOR_APP, false);
    }

}