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

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.google.android.cameraview.demo.R;


public class PermissionDialog extends CameraDialog {
    @Override
    String getTitle() {
        return getResources().getString(R.string.permission_tip);
    }

    @Override
    String getMessage() {
        return getResources().getString(R.string.permission_msg);
    }

    @Override
    String getOKButtonMsg() {
        return getResources().getString(R.string.permission_btn_setting);
    }

    @Override
    String getNoButtonMsg() {
        return getResources().getString(R.string.permission_btn_quit);
    }

    @Override
    void onButtonClick(int which) {
        dismiss();
        if (which == DialogInterface.BUTTON_POSITIVE) {
            //setting detail intent
            final Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } else {
            getActivity().finish();
        }
    }
}
