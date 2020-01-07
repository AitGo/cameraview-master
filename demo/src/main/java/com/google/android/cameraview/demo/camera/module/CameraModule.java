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

package com.google.android.cameraview.demo.camera.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.CaptureResult;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.manager.CameraSettings;
import com.google.android.cameraview.demo.camera.manager.CameraToolKit;
import com.google.android.cameraview.demo.camera.manager.Controller;
import com.google.android.cameraview.demo.camera.manager.FocusOverlayManager;
import com.google.android.cameraview.demo.camera.ui.AppBaseUI;
import com.google.android.cameraview.demo.camera.ui.CoverView;
import com.google.android.cameraview.demo.camera.utils.FileSaver;
import com.google.android.cameraview.demo.camera.utils.JobExecutor;
import com.google.android.cameraview.demo.camera.utils.MediaFunc;


/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public abstract class CameraModule {

    private static final String TAG = Config.getTag(CameraModule.class);

    Handler mainHandler;
    FileSaver fileSaver;
    private int mCameraState = Controller.CAMERA_MODULE_STOP;

    RelativeLayout rootView;

    private Controller mController;
    Context appContext;

    public TakenPhotoListener mTakenPhotoListener;
    public interface TakenPhotoListener{
        void taken(Uri uri, String path, Bitmap thumbnail);
    }
    public void setTakenPhoto(TakenPhotoListener listener) {
        this.mTakenPhotoListener = listener;
    }

    public void init(Context context, Controller controller) {
        // just need init once
        if (mController != null) { return; }
        appContext = context;
        mController = controller;
        mainHandler = getToolKit().getMainHandler();
        fileSaver = getToolKit().getFileSaver();
        rootView = controller.getBaseUI().getRootView();
        // call subclass init()
        init();
    }

    boolean isAndTrue(int param1, int param2) {
        return (param1 & param2) != 0;
    }

    void enableState(int state) {
        mCameraState = mCameraState | state;
    }

    void disableState(int state) {
        mCameraState = mCameraState & (~state);
    }

    boolean stateEnabled(int state) {
        return isAndTrue(mCameraState, state);
    }

    public void startModule() {
        if (isAndTrue(mCameraState, Controller.CAMERA_MODULE_STOP)) {
            disableState(Controller.CAMERA_MODULE_STOP);
            enableState(Controller.CAMERA_MODULE_RUNNING);
            start();
        }
    }

    public void stopModule() {
        if (isAndTrue(mCameraState, Controller.CAMERA_MODULE_RUNNING)) {
            disableState(Controller.CAMERA_MODULE_RUNNING);
            enableState(Controller.CAMERA_MODULE_STOP);
            stop();
        }
    }

    protected abstract void init();

    protected abstract void start();

    protected abstract void stop();

    void addModuleView(View view) {
        if (rootView.getChildAt(0) != view) {
            if (rootView.getChildCount() > 1) {
                rootView.removeViewAt(0);
            }
            rootView.addView(view, 0);
        }
    }

    void saveFile(final byte[] data, final int width, final int height, final String cameraId,
                  final String formatKey, final String tag) {
        getExecutor().execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                int format = getSettings().getPicFormat(cameraId, formatKey);
                int saveType = MediaFunc.MEDIA_TYPE_IMAGE;
                if (format != ImageFormat.JPEG) {
                    saveType = MediaFunc.MEDIA_TYPE_YUV;
                }
                fileSaver.saveFile(width, height, getToolKit().getOrientation(), data, tag, saveType);
                return super.run();
            }
        });
    }

    void setNewModule(int index) {
        mController.changeModule(index);
        getBaseUI().getIndicatorView().select(index);
    }

    CameraToolKit getToolKit() {
        return mController.getToolKit();
    }

    CoverView getCoverView() {
        return mController.getBaseUI().getCoverView();
    }


    protected CameraSettings getSettings() {
        return mController.getCameraSettings(appContext);
    }

    JobExecutor getExecutor() {
        return getToolKit().getExecutor();
    }

    AppBaseUI getBaseUI() {
        return mController.getBaseUI();
    }

    protected void runOnUiThread(Runnable runnable) {
        getToolKit().getMainHandler().post(runnable);
    }

    protected void runOnUiThreadDelay(Runnable runnable, long delay) {
        getToolKit().getMainHandler().postDelayed(runnable, delay);
    }

    void showSetting() {
        mController.showSetting();
    }

    void updateAFState(int state, FocusOverlayManager overlayManager) {
        switch (state) {
            case CaptureResult.CONTROL_AF_STATE_ACTIVE_SCAN:
                overlayManager.startFocus();
                break;
            case CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED:
                overlayManager.focusSuccess();
                break;
            case CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED:
                overlayManager.focusFailed();
                break;
            case CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED:
                overlayManager.focusSuccess();
                break;
            case CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN:
                overlayManager.autoFocus();
                break;
            case CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED:
                overlayManager.focusFailed();
                break;
            case CaptureResult.CONTROL_AF_STATE_INACTIVE:
                overlayManager.hideFocusUI();
                break;
        }
    }

}
