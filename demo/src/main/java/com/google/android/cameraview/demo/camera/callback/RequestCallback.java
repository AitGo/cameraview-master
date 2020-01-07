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

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class RequestCallback {
    public void onDataBack(byte[] data, int width, int height) {
        // default empty implementation
    }

    public void onRequestComplete() {
        // default empty implementation
    }

    public void onViewChange(int width, int height) {
        // default empty implementation
    }

    public void onAFStateChanged(int state) {
        // default empty implementation
    }

    public void onRecordStarted(boolean success) {
        // default empty implementation
    }

    public void onRecordStopped(String filePath, int width, int height) {
        // default empty implementation
    }
}
