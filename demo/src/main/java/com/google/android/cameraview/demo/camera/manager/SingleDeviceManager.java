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

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.util.Log;


import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.utils.JobExecutor;

import androidx.annotation.NonNull;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class SingleDeviceManager extends DeviceManager {
    private final String TAG = Config.getTag(SingleDeviceManager.class);

    private CameraDevice mDevice;
    private JobExecutor mJobExecutor;
    private String mCameraId = Config.MAIN_ID;
    private CameraEvent mCameraEvent;

    public SingleDeviceManager(Context context, JobExecutor executor, CameraEvent event) {
        super(context);
        mJobExecutor = executor;
        mCameraEvent = event;
    }

    public void setCameraId(@NonNull String id) {
        mCameraId = id;
    }

    public String getCameraId() {
        return mCameraId;
    }

    public CameraDevice getCameraDevice() {
        return mDevice;
    }

    public CameraCharacteristics getCharacteristics() {
        try {
            return cameraManager.getCameraCharacteristics(mCameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StreamConfigurationMap getConfigMap() {
        try {
            CameraCharacteristics c = cameraManager.getCameraCharacteristics(mCameraId);
            return c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void openCamera(final Handler mainHandler) {
        mJobExecutor.execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                openDevice(mainHandler);
                return super.run();
            }
        });
    }

    public void releaseCamera() {
        mJobExecutor.execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                closeDevice();
                return super.run();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private synchronized void openDevice(Handler handler) {
        // no need to check permission, because we check permission in onStart() every time
        try {
            cameraManager.openCamera(mCameraId, stateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private synchronized void closeDevice() {
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }
        mCameraEvent.onDeviceClosed();
    }

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "device opened :" + camera.getId());
            mDevice = camera;
            mCameraEvent.onDeviceOpened(camera);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.w(TAG, "onDisconnected");
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "error occur when open camera :" + camera.getId() + " error code:" + error);
            camera.close();
        }
    };
}
