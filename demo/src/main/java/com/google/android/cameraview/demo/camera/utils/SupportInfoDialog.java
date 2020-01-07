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

package com.google.android.cameraview.demo.camera.utils;


import com.google.android.cameraview.demo.R;

public class SupportInfoDialog extends CameraDialog {

    private String mMessage;

    public void setMessage(String msg) {
        mMessage = msg;
    }

    @Override
    String getTitle() {
        return getResources().getString(R.string.support_info_title);
    }

    @Override
    String getMessage() {
        return mMessage;
    }

    @Override
    String getOKButtonMsg() {
        return getResources().getString(R.string.support_info_done);
    }

    @Override
    String getNoButtonMsg() {
        return null;
    }

    @Override
    void onButtonClick(int which) {
        dismiss();
    }
}
