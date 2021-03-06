/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.cameraview.demo.camera.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.callback.CameraUiEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class ProfessionalUI extends CameraBaseUI implements TextureView.SurfaceTextureListener{

    private final String TAG = this.getClass().getSimpleName();

    private RelativeLayout mRootView;

    private GestureTextureView mPreviewTexture;
    private AppCompatSeekBar mFocusLensBar;

    public ProfessionalUI(Context context, Handler handler, CameraUiEvent event) {
        super(event);
        mRootView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.module_professional_layout, null);
        mPreviewTexture = mRootView.findViewById(R.id.texture_preview);
        mPreviewTexture.setSurfaceTextureListener(this);
        mPreviewTexture.setGestureListener(this);

        mFocusLensBar = mRootView.findViewById(R.id.sb_focus_length);
        mFocusLensBar.setOnSeekBarChangeListener(mFocusLensChangerListener);
    }

    @Override
    public void setUIClickable(boolean clickable) {
        super.setUIClickable(clickable);
        mPreviewTexture.setClickable(clickable);
    }

    @Override
    public RelativeLayout getRootView() {
        return mRootView;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        uiEvent.onPreviewUiReady(surface, null);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        uiEvent.onPreviewUiDestroy();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // preview frame is ready when receive second frame
        if (frameCount == 2) {return;}
        frameCount++;
        if (frameCount == 2) {
            uiEvent.onAction(CameraUiEvent.ACTION_PREVIEW_READY, null);
        }
    }

    private SeekBar.OnSeekBarChangeListener mFocusLensChangerListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float value = progress / (float) seekBar.getMax();
                    uiEvent.onSettingChange(CaptureRequest.LENS_FOCUS_DISTANCE, value);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

}
