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

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.callback.CameraUiEvent;
import com.google.android.cameraview.demo.camera.callback.RequestCallback;
import com.google.android.cameraview.demo.camera.manager.CameraSession;
import com.google.android.cameraview.demo.camera.manager.CameraSettings;
import com.google.android.cameraview.demo.camera.manager.Controller;
import com.google.android.cameraview.demo.camera.manager.DeviceManager;
import com.google.android.cameraview.demo.camera.manager.DualDeviceManager;
import com.google.android.cameraview.demo.camera.manager.FocusOverlayManager;
import com.google.android.cameraview.demo.camera.manager.Session;
import com.google.android.cameraview.demo.camera.ui.DualCameraUI;
import com.google.android.cameraview.demo.camera.utils.FileSaver;
import com.google.android.cameraview.demo.camera.utils.MediaFunc;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class DualCameraModule extends CameraModule implements FileSaver.FileListener {

    private static final String TAG = Config.getTag(DualCameraModule.class);

    private SurfaceTexture mainSurfaceTexture;
    private SurfaceTexture auxSurfaceTexture;
    private DualCameraUI mUI;
    private CameraSession mSession;
    private CameraSession mAuxSession;
    private DualDeviceManager mDeviceMgr;
    private FocusOverlayManager mFocusManager;
    private int mPicCount = 0;

    @Override
    protected void init() {
        mUI = new DualCameraUI(appContext, mainHandler, mCameraUiEvent);
        mUI.setCoverView(getCoverView());
        mFocusManager = new FocusOverlayManager(getBaseUI().getFocusView(), mainHandler.getLooper());
        mFocusManager.setListener(mCameraUiEvent);
        mDeviceMgr = new DualDeviceManager(appContext, getExecutor(), mCameraEvent);
        mSession = new CameraSession(appContext, mainHandler, getSettings());
        mAuxSession = new CameraSession(appContext, mainHandler, getSettings());
    }

    @Override
    public void start() {
        String[] idList = mDeviceMgr.getCameraIdList();
        String mainId = getSettings().getGlobalPref(CameraSettings.KEY_MAIN_CAMERA_ID, idList[0]);
        String auxId = getSettings().getGlobalPref(
                CameraSettings.KEY_AUX_CAMERA_ID, idList[idList.length - 1]);
        mDeviceMgr.setCameraId(mainId, auxId);
        mDeviceMgr.openCamera(mainHandler);
        // when module changed , need update listener
        fileSaver.setFileListener(this);
        getBaseUI().setCameraUiEvent(mCameraUiEvent);
        addModuleView(mUI.getRootView());
        Log.d(TAG, "start module");
    }

    private DeviceManager.CameraEvent mCameraEvent = new DeviceManager.CameraEvent() {
        @Override
        public void onDeviceOpened(CameraDevice device) {
            super.onDeviceOpened(device);
            Log.d(TAG, "camera opened");
            mSession.applyRequest(Session.RQ_SET_DEVICE, device);
            enableState(Controller.CAMERA_STATE_OPENED);
            if (stateEnabled(Controller.CAMERA_STATE_UI_READY)) {
                mSession.applyRequest(Session.RQ_START_PREVIEW,mainSurfaceTexture, mRequestCallback);
                mAuxSession.applyRequest(Session.RQ_START_PREVIEW, auxSurfaceTexture, mAuxRequestCb);
            }
        }

        @Override
        public void onAuxDeviceOpened(CameraDevice device) {
            super.onAuxDeviceOpened(device);
            // method will be called before onDeviceOpened(CameraDevice device)
            mAuxSession.applyRequest(Session.RQ_SET_DEVICE, device, null);
        }

        @Override
        public void onDeviceClosed() {
            super.onDeviceClosed();
            disableState(Controller.CAMERA_STATE_OPENED);
            if (mUI != null) {
                mUI.resetFrameCount();
            }
            Log.d(TAG, "camera closed");
        }
    };

    private RequestCallback mRequestCallback = new RequestCallback() {
        @Override
        public void onDataBack(byte[] data, int width, int height) {
            super.onDataBack(data, width, height);
            saveFile(data, width, height, mDeviceMgr.getCameraId(true),
                    CameraSettings.KEY_PICTURE_FORMAT, "MAIN");
            enableUiAfterShot();
        }

        @Override
        public void onViewChange(int width, int height) {
            super.onViewChange(width, height);
            getBaseUI().updateUiSize(width, height);
            mUI.updateUISize(width, height);
            mFocusManager.onPreviewChanged(width, height, mDeviceMgr.getCharacteristics(true));
        }

        @Override
        public void onAFStateChanged(int state) {
            super.onAFStateChanged(state);
            updateAFState(state, mFocusManager);
        }
    };

    private RequestCallback mAuxRequestCb = new RequestCallback() {
        @Override
        public void onDataBack(byte[] data, int width, int height) {
            super.onDataBack(data, width, height);
            saveFile(data, width, height, mDeviceMgr.getCameraId(false),
                    CameraSettings.KEY_PICTURE_FORMAT, "AUX");
            enableUiAfterShot();
        }
    };

    private void enableUiAfterShot() {
        if (mPicCount == 2) {
            mUI.setUIClickable(true);
            getBaseUI().setUIClickable(true);
            mPicCount = 0;
            mSession.applyRequest(Session.RQ_RESTART_PREVIEW);
            mAuxSession.applyRequest(Session.RQ_RESTART_PREVIEW);
        }
    }

    @Override
    public void stop() {
        getBaseUI().setCameraUiEvent(null);
        getCoverView().showCover();
        mFocusManager.hideFocusUI();
        mFocusManager.removeDelayMessage();
        mSession.release();
        mAuxSession.release();
        mDeviceMgr.releaseCamera();
        Log.d(TAG, "stop module");
    }

    /**
     * FileSaver.FileListener
     * @param uri image file uri
     * @param path image file path
     * @param thumbnail image thumbnail
     */
    @Override
    public void onFileSaved(Uri uri, String path, Bitmap thumbnail) {
        MediaFunc.setCurrentUri(uri);
        mUI.setUIClickable(true);
        getBaseUI().setUIClickable(true);
        getBaseUI().setThumbnail(thumbnail);
    }

    /**
     * callback for file save error
     * @param msg error msg
     */
    @Override
    public void onFileSaveError(String msg) {
        Toast.makeText(appContext,msg, Toast.LENGTH_LONG).show();
        mUI.setUIClickable(true);
        getBaseUI().setUIClickable(true);
    }

    private void takePicture() {
        mUI.setUIClickable(false);
        getBaseUI().setUIClickable(false);
        mSession.applyRequest(Session.RQ_TAKE_PICTURE, getToolKit().getOrientation());
        mAuxSession.applyRequest(Session.RQ_TAKE_PICTURE, getToolKit().getOrientation());
    }

    private CameraUiEvent mCameraUiEvent = new CameraUiEvent() {

        @Override
        public void onPreviewUiReady(SurfaceTexture mainSurface, SurfaceTexture auxSurface) {
            Log.d(TAG, "onSurfaceTextureAvailable");
            mainSurfaceTexture = mainSurface;
            auxSurfaceTexture = auxSurface;
            enableState(Controller.CAMERA_STATE_UI_READY);
            if (stateEnabled(Controller.CAMERA_STATE_OPENED)) {
                mSession.applyRequest(Session.RQ_START_PREVIEW,mainSurfaceTexture, mRequestCallback);
                mAuxSession.applyRequest(Session.RQ_START_PREVIEW, auxSurfaceTexture, mAuxRequestCb);
            }
        }

        @Override
        public void onPreviewUiDestroy() {
            disableState(Controller.CAMERA_STATE_UI_READY);
            Log.d(TAG, "onSurfaceTextureDestroyed");
        }

        @Override
        public void onTouchToFocus(float x, float y) {
            mFocusManager.startFocus(x, y);
            MeteringRectangle focusRect = mFocusManager.getFocusArea(x, y, true);
            MeteringRectangle meterRect = mFocusManager.getFocusArea(x, y, false);
            mSession.applyRequest(Session.RQ_AF_AE_REGIONS, focusRect, meterRect);
            //mAuxSession.applyRequest(Session.RQ_AF_AE_REGIONS, focusRect, meterRect);
        }

        @Override
        public void resetTouchToFocus() {
            if (stateEnabled(Controller.CAMERA_MODULE_RUNNING)) {
                mSession.applyRequest(Session.RQ_FOCUS_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                mAuxSession.applyRequest(Session.RQ_FOCUS_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            }
        }

        @Override
        public <T> void onSettingChange(CaptureRequest.Key<T> key, T value) {

        }

        @Override
        public <T> void onAction(String type, T value) {
            switch (type) {
                case CameraUiEvent.ACTION_CLICK:
                    handleClick((View) value);
                    break;
                case CameraUiEvent.ACTION_CHANGE_MODULE:
                    setNewModule((Integer) value);
                    break;
                case CameraUiEvent.ACTION_SWITCH_CAMERA:
                    break;
                case CameraUiEvent.ACTION_PREVIEW_READY:
                    getCoverView().hideCoverWithAnimation();
                    break;
                default:
                    break;
            }
        }
    };

    private void handleClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shutter:
                takePicture();
                break;
            case R.id.btn_setting:
                showSetting();
                break;
            case R.id.thumbnail:
                MediaFunc.goToGallery(appContext);
                break;
        }
    }
}
