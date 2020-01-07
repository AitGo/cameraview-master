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

package com.google.android.cameraview.demo.camera.callback;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CaptureRequest;

import androidx.annotation.Nullable;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class CameraUiEvent {
    public static final String ACTION_CLICK = "camera.action.click";
    public static final String ACTION_CHANGE_MODULE = "camera.action.change.module";
    public static final String ACTION_SWITCH_CAMERA = "camera.action.switch.camera";
    public static final String ACTION_PREVIEW_READY = "camera.action.preview.ready";

    public void onPreviewUiReady(SurfaceTexture mainSurface, @Nullable SurfaceTexture auxSurface) {
        // default empty implementation
    }

    public void onPreviewUiDestroy() {
        // default empty implementation
    }

    public void onTouchToFocus(float x, float y) {
        // default empty implementation
    }

    public void resetTouchToFocus() {
        // default empty implementation
    }

    public <T> void onSettingChange(CaptureRequest.Key<T> key, T value) {
        // default empty implementation
    }

    public <T> void onAction(String type, T value) {
        // default empty implementation
    }
}
