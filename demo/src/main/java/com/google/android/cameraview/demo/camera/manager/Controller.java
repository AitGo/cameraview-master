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

package com.google.android.cameraview.demo.camera.manager;

import android.content.Context;

import com.google.android.cameraview.demo.camera.ui.AppBaseUI;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public interface Controller {
    int CAMERA_MODULE_STOP = 1;
    int CAMERA_MODULE_RUNNING = 1 << 1;
    int CAMERA_STATE_OPENED = 1 << 2;
    int CAMERA_STATE_UI_READY = 1 << 3;
    int CAMERA_STATE_START_RECORD = 1 << 4;
    int CAMERA_STATE_PAUSE_RECORD = 1 << 5;


    void changeModule(int module);

    CameraToolKit getToolKit();

    void showSetting();

    CameraSettings getCameraSettings(Context context);

    AppBaseUI getBaseUI();
}
