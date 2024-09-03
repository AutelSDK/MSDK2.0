package com.autel.sdk.debugtools.fragment;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.autel.module_player.player.autelplayer.AutelPlayerSurfaceView;

public class TestSurface extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder.Callback mCallback;
    public TestSurface(Context context, SurfaceHolder.Callback cb) {

        super(context);
        mCallback = cb;

        this.getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (mCallback != null){
            mCallback.surfaceCreated(holder);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (mCallback != null){
            mCallback.surfaceChanged(holder, format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (mCallback != null){
            mCallback.surfaceDestroyed(holder);
        }
    }
}
