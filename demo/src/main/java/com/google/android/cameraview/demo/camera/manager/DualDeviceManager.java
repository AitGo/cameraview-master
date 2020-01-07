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
public class DualDeviceManager extends  DeviceManager{
    private final String TAG = Config.getTag(DualDeviceManager.class);

    private CameraDevice mDevice;
    private CameraDevice mAuxDevice;
    private JobExecutor mExecutor;
    private String mCameraId = Config.MAIN_ID;
    private String mAuxCameraId = Config.AUX_ID;
    private DeviceManager.CameraEvent mCameraEvent;

    public DualDeviceManager(Context context, JobExecutor executor, DeviceManager.CameraEvent event) {
        super(context);
        mExecutor = executor;
        mCameraEvent = event;
    }

    public void setCameraId(@NonNull String id, @NonNull String auxId) {
        mCameraId = id;
        mAuxCameraId = auxId;
    }

    public String getCameraId(boolean isMain) {
        if (isMain) {
            return mCameraId;
        } else {
            return mAuxCameraId;
        }
    }

    public CameraDevice getCameraDevice(boolean isMain) {
        if (isMain) {
            return mDevice;
        } else {
            return mAuxDevice;
        }
    }

    public CameraCharacteristics getCharacteristics(boolean isMain) {
        String cameraId = isMain ? mCameraId : mAuxCameraId;
        try {
            return cameraManager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StreamConfigurationMap getConfigMap(boolean isMain) {
        String cameraId = isMain ? mCameraId : mAuxCameraId;
        try {
            CameraCharacteristics c = cameraManager.getCameraCharacteristics(cameraId);
            return c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void openCamera(final Handler mainHandler) {
        mExecutor.execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                openDevice(mCameraId, mainHandler);
                openDevice(mAuxCameraId, mainHandler);
                return super.run();
            }
        });
    }

    public void releaseCamera() {
        mExecutor.execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                closeDevice();
                return super.run();
            }
        });

    }

    @SuppressLint("MissingPermission")
    private synchronized void openDevice(String cameraId, Handler handler) {
        // no need to check permission, because we check permission in onStart() every time
        try {
            cameraManager.openCamera(cameraId, stateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private synchronized void closeDevice() {
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }
        if (mAuxDevice != null) {
            mAuxDevice.close();
            mAuxDevice = null;
        }
        mCameraEvent.onDeviceClosed();
    }

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "device opened :" + camera.getId());
            if (camera.getId().equals(mCameraId)) {
                mDevice = camera;
            } else if (camera.getId().equals(mAuxCameraId)) {
                mAuxDevice = camera;
            } else {
                Log.e(TAG, "internal error, camera id not match any requested camera id");
            }
            if (mDevice != null && mAuxDevice != null) {
                mCameraEvent.onAuxDeviceOpened(mAuxDevice);
                mCameraEvent.onDeviceOpened(mDevice);
            }
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
