package com.autel.sdk.debugtools.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.autel.sdk.debugtools.R
import com.autel.sdk.debugtools.activity.FragmentPageInfoItem

/**
 * item list with title and description adapter
 * Copyright: Autel Robotics
 * @author huangsihua on 2022/12/17.
 */
class MainFragmentListAdapter(private val onClick: (FragmentPageInfoItem) -> Unit) :
    ListAdapter<FragmentPageInfoItem, MainFragmentListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.frag_main_item, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(itemView: View, val onClick: (FragmentPageInfoItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.item_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.item_description)
        private var currentPageInfoItem: FragmentPageInfoItem? = null

        init {
            itemView.setOnClickListener {
                currentPageInfoItem?.let {
                    onClick(it)
                }
            }
        }

        fun bind(pageInfo: FragmentPageInfoItem) {
            currentPageInfoItem = pageInfo
            titleTextView.text = itemView.context.resources.getString(pageInfo.title)
            descriptionTextView.text = itemView.context.resources.getString(pageInfo.description)


        }
    }

    object DiffCallback : DiffUtil.ItemCallback<FragmentPageInfoItem>() {
        override fun areItemsTheSame(
            oldItem: FragmentPageInfoItem,
            newItem: FragmentPageInfoItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FragmentPageInfoItem,
            newItem: FragmentPageInfoItem
        ): Boolean {
            return oldItem.title == newItem.title
        }
    }
}