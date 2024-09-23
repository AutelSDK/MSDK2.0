package com.autel.sdk.debugtools.helper

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * 遥控器上报消息
 * 消息只分发一次，不会重复分发，且在后台也能分发消息，会根据LifecycleOwner的生命周期自动移除
 * Copyright: Autel Robotics
 *
 * @author xulc on 2022/11/25
 */
class RemoteUploadMsgData<T : Any> {

    private var value: T? = null

    private val mObservers: LinkedHashMap<Observer<T>, ObserverWrapper> = LinkedHashMap()


    /**
     * 观察默认飞机的数据
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        val wrapper = LifecycleBoundObserver(owner, observer)
        //putIfAbsent 方法是在 API 级别 24 中添加的，低于 API 级别 24 的运行时没有该方法。
        val existing =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mObservers.putIfAbsent(observer, wrapper)
            } else {
                var isExist = mObservers[observer]
                if (isExist == null) {
                    isExist = mObservers.put(observer, wrapper)
                }
                isExist
            }
        if (existing is LifecycleBoundObserver) {
            require(existing.isAttachedTo(owner)) {
                ("Cannot add the same observer"
                        + " with different lifecycles")
            }
        }
        if (existing != null) {
            return
        }
        owner.lifecycle.addObserver(wrapper)
    }

    /**
     * 无生命周期的观察数据
     */
    fun observeForever(observer: Observer<T>) {
        val wrapper = AlwaysActiveObserver(observer)
        val existing =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mObservers.putIfAbsent(observer, wrapper)
            } else {
                var isExist = mObservers[observer]
                if (isExist == null) {
                    isExist = mObservers.put(observer, wrapper)
                }
                isExist
            }
        require((existing is AlwaysActiveObserver)) {
            ("Cannot add the same observer"
                    + " with different lifecycles")
        }
    }

    /**
     * 上报数据
     */
    fun setValue(value: T) {
        this.value = value
        for (ob in mObservers.values) {
            ob.observer.onChanged(value)
        }
    }

    /**
     * 获取数据
     */
    fun getValue(): T? {
        return value
    }

    /**
     * 移除观察者
     */
    fun removeObserver(observer: Observer<T>) {
        val removed = mObservers.remove(observer) ?: return
        removed.detachObserver()
    }

    open inner class ObserverWrapper(val observer: Observer<T>) {
        open fun isAttachedTo(owner: LifecycleOwner): Boolean {
            return false
        }

        open fun detachObserver() {

        }
    }

    inner class LifecycleBoundObserver(private val mOwner: LifecycleOwner, observer: Observer<T>) :
        ObserverWrapper(observer), LifecycleEventObserver {

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            val currentState = mOwner.lifecycle.currentState
            if (currentState == Lifecycle.State.DESTROYED) {
                removeObserver(observer)
                return
            }
        }

        override fun isAttachedTo(owner: LifecycleOwner): Boolean {
            return mOwner === owner
        }

        override fun detachObserver() {
            mOwner.lifecycle.removeObserver(this)
        }
    }

    inner class AlwaysActiveObserver internal constructor(observer: Observer<T>) :
        ObserverWrapper(observer) {

    }
}