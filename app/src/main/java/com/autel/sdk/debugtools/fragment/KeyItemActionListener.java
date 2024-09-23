package com.autel.sdk.debugtools.fragment;


import org.jetbrains.annotations.Nullable;

/**
 * key and item action change listener
 * Copyright: Autel Robotics
 *
 * @author huangsihua on 2022/10/9.
 */
public interface KeyItemActionListener<T> {

    void actionChange(@Nullable T t);
}

