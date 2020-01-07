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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public abstract class CameraDialog extends DialogFragment implements DialogInterface.OnClickListener {
    abstract String getTitle();

    abstract String getMessage();

    abstract String getOKButtonMsg();

    abstract String getNoButtonMsg();

    abstract void onButtonClick(int which);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getTitle());
        builder.setMessage(getMessage());
        builder.setCancelable(false);
        if (getOKButtonMsg() != null) {
            builder.setPositiveButton(getOKButtonMsg(), this);
        }
        if (getNoButtonMsg() != null) {
            builder.setNegativeButton(getNoButtonMsg(), this);
        }
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        onButtonClick(which);
    }
}
