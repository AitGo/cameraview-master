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
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.view.OrientationEventListener;

import com.google.android.cameraview.demo.camera.utils.FileSaver;
import com.google.android.cameraview.demo.camera.utils.JobExecutor;


/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class CameraToolKit {

    private Context mContext;
    private Handler mMainHandler;
    private MyOrientationListener mOrientationListener;
    private FileSaver mFileSaver;
    private int mRotation = 0;
    private JobExecutor mJobExecutor;

    public CameraToolKit(Context context) {
        mContext = context;
        mMainHandler = new Handler(Looper.getMainLooper());
        mFileSaver = new FileSaver(mContext, mMainHandler);
        setOrientationListener();
        mJobExecutor = new JobExecutor();
    }

    public void destroy() {
        if (mFileSaver != null) {
            mFileSaver.release();
        }
        mOrientationListener.disable();
        mJobExecutor.destroy();
    }

    public FileSaver getFileSaver() {
        return mFileSaver;
    }

    public int getOrientation() {
        return mRotation;
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }

    public JobExecutor getExecutor() {
        return mJobExecutor;
    }

    private class MyOrientationListener extends OrientationEventListener {

        MyOrientationListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            mRotation = (orientation + 45) / 90 * 90;
        }
    }

    private void setOrientationListener() {
        mOrientationListener = new MyOrientationListener(mContext, SensorManager.SENSOR_DELAY_UI);
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }
}
