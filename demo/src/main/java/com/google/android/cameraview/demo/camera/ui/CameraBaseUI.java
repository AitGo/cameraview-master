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

import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.cameraview.demo.camera.callback.CameraUiEvent;
import com.google.android.cameraview.demo.camera.manager.ModuleManager;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public abstract class CameraBaseUI implements GestureTextureView.GestureListener, View
        .OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    CameraUiEvent uiEvent;
    int frameCount = 0;
    private CoverView mCoverView;

    CameraBaseUI(CameraUiEvent event) {
        uiEvent = event;
    }

    public abstract RelativeLayout getRootView();

    public void setUIClickable(boolean clickable) {
    }

    public void resetFrameCount() {
        frameCount = 0;
    }

    public void setCoverView(CoverView coverView) {
        mCoverView = coverView;
    }

    /* View.OnClickListener*/
    @Override
    public void onClick(View v) {
        uiEvent.onAction(CameraUiEvent.ACTION_CLICK, v);
    }

    /* GestureTextureView.GestureListener */
    @Override
    public void onClick(float x, float y) {
        uiEvent.onTouchToFocus(x, y);
    }

    @Override
    public void onSwipeLeft() {
        int newIndex = ModuleManager.getCurrentIndex() + 1;
        if (ModuleManager.isValidIndex(newIndex)) {
            mCoverView.setAlpha(1.0f);
            uiEvent.onAction(CameraUiEvent.ACTION_CHANGE_MODULE, newIndex);
        }
    }

    @Override
    public void onSwipeRight() {
        int newIndex = ModuleManager.getCurrentIndex() - 1;
        if (ModuleManager.isValidIndex(newIndex)) {
            mCoverView.setAlpha(1.0f);
            uiEvent.onAction(CameraUiEvent.ACTION_CHANGE_MODULE, newIndex);
        }
    }

    @Override
    public void onSwipe(float percent) {
        int newIndex;
        if (percent < 0) {
            newIndex = ModuleManager.getCurrentIndex() + 1;
        } else {
            newIndex = ModuleManager.getCurrentIndex() - 1;
        }
        if (ModuleManager.isValidIndex(newIndex)) {
            mCoverView.setMode(newIndex);
            mCoverView.setAlpha(Math.abs(percent));
            mCoverView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCancel() {
        mCoverView.setVisibility(View.GONE);
        mCoverView.setAlpha(1.0f);
    }
}
