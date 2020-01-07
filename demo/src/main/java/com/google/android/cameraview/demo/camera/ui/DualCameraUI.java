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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.callback.CameraUiEvent;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class DualCameraUI extends CameraBaseUI implements GestureTextureView.GestureListener  {

    private final String TAG = this.getClass().getSimpleName();

    private RelativeLayout mRootView;
    private GestureTextureView mMainPreviewTexture;
    private TextureView mAuxPreviewTexture;

    private int mCountFlag = 0;


    public DualCameraUI(Context context, Handler mainHandler, CameraUiEvent event) {
        super(event);
        mRootView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout
                .module_dual_camera_layout, null);
        mMainPreviewTexture = mRootView.findViewById(R.id.main_texture);
        mMainPreviewTexture.setSurfaceTextureListener(mMainListener);
        mMainPreviewTexture.setGestureListener(this);
        mAuxPreviewTexture = mRootView.findViewById(R.id.aux_texture);
        mAuxPreviewTexture.setSurfaceTextureListener(mAuxListener);
    }

    public void updateUISize(int width, int height) {
        int auxW = (int) (width * Config.AUX_PREVIEW_SCALE);
        int auxH = (int) (height * Config.AUX_PREVIEW_SCALE);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(auxW, auxH);
        mAuxPreviewTexture.setLayoutParams(params1);
    }

    @Override
    public void setUIClickable(boolean clickable) {
        super.setUIClickable(clickable);
        mMainPreviewTexture.setClickable(clickable);
        mAuxPreviewTexture.setClickable(clickable);
    }

    @Override
    public RelativeLayout getRootView() {
        return mRootView;
    }

    private TextureView.SurfaceTextureListener mMainListener = new TextureView
            .SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mCountFlag++;
            setSurfaceCallback();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mCountFlag--;
            setSurfaceCallback();
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
    };
    private TextureView.SurfaceTextureListener mAuxListener = new TextureView
            .SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mCountFlag++;
            setSurfaceCallback();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mCountFlag--;
            setSurfaceCallback();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void setSurfaceCallback() {
        if (mCountFlag == 2) {
            uiEvent.onPreviewUiReady(mMainPreviewTexture.getSurfaceTexture(),
                    mAuxPreviewTexture.getSurfaceTexture());
        } else if (mCountFlag == 0) {
            uiEvent.onPreviewUiDestroy();
        } else {
            Log.d(TAG, "main or aux surface not available");
        }
    }

}
