package com.autel.sdk.debugtools.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.autel.drone.demo.R
import com.autel.drone.demo.databinding.TestcaseResultItemBinding
import com.autel.sdk.debugtools.ScenarioTestResultDataModel
import com.autel.sdk.debugtools.ScenarioTestResultStatusEnum

/**
 * test case result adapter
 * Copyright: Autel Robotics
 * @author  on 2022/12/17.
 */

class TestCaseResultAdapter(val activity: Activity) :
    RecyclerView.Adapter<TestCaseResultAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: TestcaseResultItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var resultList = ArrayList<ScenarioTestResultDataModel>()

    fun setData(result: ScenarioTestResultDataModel) {
        resultList.add(result)
        notifyDataSetChanged()
    }

    fun clearData(result: ScenarioTestResultDataModel) {
        resultList.clear()
        resultList.add(result)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TestcaseResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(resultList[position]) {
                binding.tvResult.text = this.message
                when (this.status) {
                    ScenarioTestResultStatusEnum.SUCCESS -> {
                        binding.tvResultBg.setBackgroundColor(activity.resources.getColor(R.color.debug_color_4CAF50))
                    }
                    ScenarioTestResultStatusEnum.FAILED -> {
                        binding.tvResultBg.setBackgroundColor(activity.resources.getColor(R.color.debug_color_FF0000))
                    }
                    ScenarioTestResultStatusEnum.PROCESSING -> {
                        binding.tvResultBg.setBackgroundColor(activity.resources.getColor(R.color.debug_black))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }
}
