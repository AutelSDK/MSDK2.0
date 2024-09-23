package com.autel.sdk.debugtools.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.autel.drone.demo.databinding.ItemTextviewScenarioTestcasesBinding
import com.autel.sdk.debugtools.adapter.ScenarioAdapter.ScenarioViewHolder
import com.autel.sdk.debugtools.fragment.IMTestClickListener


/**
 * scenario testing adapter item
 * Copyright: Autel Robotics
 * @author rahul on 2022/12/17.
 */
class ScenarioAdapter(
    private val dataList: ArrayList<String>,
    private val imTestClickListener: IMTestClickListener
) : RecyclerView.Adapter<ScenarioViewHolder>() {
    private var selected_position: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        return ScenarioViewHolder(
            ItemTextviewScenarioTestcasesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        holder.binding.tvItem.setBackgroundColor(Color.TRANSPARENT)

        if (selected_position == position) {
            holder.binding.tvItem.setBackgroundColor(Color.GRAY)
        }

        holder.bindData(dataList.get(position))
        holder.binding.tvItem.setText(dataList.get(position))

        holder.binding.tvItem.setOnClickListener {
            val position: Int = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                selected_position = position
                imTestClickListener.testDetails(dataList.get(position), position)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size ?: 0
    }

    inner class ScenarioViewHolder(val binding: ItemTextviewScenarioTestcasesBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bindData(data: String) {

        }
    }


}