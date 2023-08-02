package com.autel.sdk.debugtools

import java.io.Serializable

/**
 * SDK corresponding enumeration
 *
 * Copyright: Autel Robotics
 * @author maowei on 2022/12/17.
 */

class EnumItem : Serializable {
    /**
     * 需要显示的条目描述
     */
    private var description: String? = null

    /**
     * SDK对应的枚举
     */
    private var name: String? = null

    /**
     * 是否选中
     */
    private var selected = false
    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun isSelected(): Boolean {
        return selected
    }

    fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    companion object {
        private const val serialVersionUID = 876323262645176354L
    }
}