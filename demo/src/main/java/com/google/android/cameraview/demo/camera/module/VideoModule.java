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
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.callback.CameraUiEvent;
import com.google.android.cameraview.demo.camera.callback.MenuInfo;
import com.google.android.cameraview.demo.camera.callback.RequestCallback;
import com.google.android.cameraview.demo.camera.manager.CameraSettings;
import com.google.android.cameraview.demo.camera.manager.Controller;
import com.google.android.cameraview.demo.camera.manager.DeviceManager;
import com.google.android.cameraview.demo.camera.manager.FocusOverlayManager;
import com.google.android.cameraview.demo.camera.manager.Session;
import com.google.android.cameraview.demo.camera.manager.SingleDeviceManager;
import com.google.android.cameraview.demo.camera.manager.VideoSession;
import com.google.android.cameraview.demo.camera.ui.CameraBaseMenu;
import com.google.android.cameraview.demo.camera.ui.CameraMenu;
import com.google.android.cameraview.demo.camera.ui.ShutterButton;
import com.google.android.cameraview.demo.camera.ui.VideoUI;
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
public class VideoModule  extends CameraModule implements FileSaver.FileListener,
        CameraBaseMenu.OnMenuClickListener {

    private SurfaceTexture mSurfaceTexture;
    private VideoUI mUI;
    private VideoSession mSession;
    private SingleDeviceManager mDeviceMgr;
    private FocusOverlayManager mFocusManager;
    private CameraMenu mCameraMenu;

    private static final String TAG = Config.getTag(VideoModule.class);

    @Override
    protected void init() {
        mUI = new VideoUI(appContext, mainHandler, mCameraUiEvent);
        mUI.setCoverView(getCoverView());
        mDeviceMgr = new SingleDeviceManager(appContext, getExecutor(), mCameraEvent);
        mFocusManager = new FocusOverlayManager(getBaseUI().getFocusView(), mainHandler.getLooper());
        mFocusManager.setListener(mCameraUiEvent);
        mCameraMenu = new CameraMenu(appContext, R.xml.menu_preference, mMenuInfo);
        mCameraMenu.setOnMenuClickListener(this);
        mSession = new VideoSession(appContext, mainHandler, getSettings());
    }

    @Override
    public void start() {
        String cameraId = getSettings().getGlobalPref(
                CameraSettings.KEY_VIDEO_ID, mDeviceMgr.getCameraIdList()[0]);
        mDeviceMgr.setCameraId(cameraId);
        mDeviceMgr.openCamera(mainHandler);
        // when module changed , need update listener
        fileSaver.setFileListener(this);
        getBaseUI().setCameraUiEvent(mCameraUiEvent);
        getBaseUI().setMenuView(mCameraMenu.getView());
        getBaseUI().setShutterMode(ShutterButton.VIDEO_MODE);
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
                mSession.applyRequest(Session.RQ_START_PREVIEW, mSurfaceTexture, mRequestCallback);
            }
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
        public void onViewChange(final int width, final int height) {
            super.onViewChange(width, height);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getBaseUI().updateUiSize(width, height);
                    mFocusManager.onPreviewChanged(width, height, mDeviceMgr.getCharacteristics());
                }
            });
        }

        @Override
        public void onAFStateChanged(final int state) {
            super.onAFStateChanged(state);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateAFState(state, mFocusManager);
                }
            });
        }

        @Override
        public void onRecordStarted(final boolean success) {
            super.onRecordStarted(success);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleRecordStarted(success);
                }
            });
        }

        @Override
        public void onRecordStopped(final String filePath, final int width, final int height) {
            getExecutor().execute(new JobExecutor.Task<Bitmap>() {
                @Override
                public Bitmap run() {
                    return getVideoThumbnail(filePath);
                }

                @Override
                public void onJobThread(Bitmap result) {
                    fileSaver.saveVideoFile(width, height, getToolKit().getOrientation(),
                            filePath, MediaFunc.MEDIA_TYPE_VIDEO);
                }

                @Override
                public void onMainThread(Bitmap result) {
                    getBaseUI().setThumbnail(result);
                }
            });
        }
    };

    @Override
    public void stop() {
        mCameraMenu.close();
        getBaseUI().setCameraUiEvent(null);
        getBaseUI().setShutterMode(ShutterButton.PHOTO_MODE);
        getCoverView().showCover();
        getBaseUI().removeMenuView();
        mFocusManager.removeDelayMessage();
        mFocusManager.hideFocusUI();
        if (stateEnabled(Controller.CAMERA_STATE_START_RECORD)) {
            stopVideoRecording();
        }
        mSession.release();
        mDeviceMgr.releaseCamera();
        Log.d(TAG, "stop module");
    }

    private Bitmap getVideoThumbnail(String path) {
        return ThumbnailUtils.createVideoThumbnail(
                path, MediaStore.Video.Thumbnails.MICRO_KIND);
    }

    private void handleRecordStarted(boolean success) {
        getBaseUI().setUIClickable(true);
        if (success) {
            getBaseUI().setShutterMode(ShutterButton.VIDEO_RECORDING_MODE);
            mUI.startVideoTimer();
        } else {
            disableState(Controller.CAMERA_STATE_START_RECORD);
            getBaseUI().setShutterMode(ShutterButton.VIDEO_MODE);
        }
    }

    private void startVideoRecording() {
        enableState(Controller.CAMERA_STATE_START_RECORD);
        getBaseUI().setUIClickable(false);
        if (stateEnabled(Controller.CAMERA_STATE_UI_READY)) {
            getExecutor().execute(new JobExecutor.Task<Void>() {
                @Override
                public Void run() {
                    mSession.applyRequest(Session.RQ_START_RECORD,
                            getToolKit().getOrientation());
                    return super.run();
                }
            });
        }
    }

    private void stopVideoRecording() {
        disableState(Controller.CAMERA_STATE_START_RECORD);
        getBaseUI().setShutterMode(ShutterButton.VIDEO_MODE);
        getBaseUI().setUIClickable(false);
        getExecutor().execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                mSession.applyRequest(Session.RQ_STOP_RECORD);
                mSession.applyRequest(Session.RQ_START_PREVIEW,
                        mSurfaceTexture, mRequestCallback);
                return super.run();
            }

            @Override
            public void onMainThread(Void result) {
                super.onMainThread(result);
                mUI.stopVideoTimer();
                getBaseUI().setUIClickable(true);
            }
        });
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
        Log.d(TAG, "uri:" + uri.toString());
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

    private CameraUiEvent mCameraUiEvent = new CameraUiEvent() {

        @Override
        public void onPreviewUiReady(SurfaceTexture mainSurface, SurfaceTexture auxSurface) {
            Log.d(TAG, "onSurfaceTextureAvailable");
            mSurfaceTexture = mainSurface;
            enableState(Controller.CAMERA_STATE_UI_READY);
            if (stateEnabled(Controller.CAMERA_STATE_OPENED)) {
                mSession.applyRequest(Session.RQ_START_PREVIEW, mSurfaceTexture, mRequestCallback);
            }
        }

        @Override
        public void onPreviewUiDestroy() {
            disableState(Controller.CAMERA_STATE_UI_READY);
            Log.d(TAG, "onSurfaceTextureDestroyed");
        }

        @Override
        public void onTouchToFocus(float x, float y) {
            // close all menu when touch to focus
            mCameraMenu.close();
            mFocusManager.startFocus(x, y);
            CameraCharacteristics c = mDeviceMgr.getCharacteristics();
            MeteringRectangle focusRect = mFocusManager.getFocusArea(x, y, true);
            MeteringRectangle meterRect = mFocusManager.getFocusArea(x, y, false);
            mSession.applyRequest(Session.RQ_AF_AE_REGIONS, focusRect, meterRect);
        }

        @Override
        public void resetTouchToFocus() {
            if (stateEnabled(Controller.CAMERA_MODULE_RUNNING)) {
                mSession.applyRequest(Session.RQ_FOCUS_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            }
        }

        @Override
        public <T> void onSettingChange(CaptureRequest.Key<T> key, T value) {
            if (key == CaptureRequest.LENS_FOCUS_DISTANCE) {
                mSession.applyRequest(Session.RQ_FOCUS_DISTANCE, value);
            }
        }

        @Override
        public <T> void onAction(String type, T value) {
            // close all menu when ui click
            mCameraMenu.close();
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

    private MenuInfo mMenuInfo = new MenuInfo() {
        @Override
        public String[] getCameraIdList() {
            return mDeviceMgr.getCameraIdList();
        }

        @Override
        public String getCurrentCameraId() {
            return getSettings().getGlobalPref(CameraSettings.KEY_CAMERA_ID);
        }

        @Override
        public String getCurrentValue(String key) {
            return getSettings().getGlobalPref(key);
        }
    };

    private void handleClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shutter:
                handleShutterClick();
                break;
            case R.id.btn_setting:
                showSetting();
                break;
            case R.id.thumbnail:
                MediaFunc.goToGallery(appContext);
                break;
            case R.id.ll_record_timer:
                // TODO for pause/resume video recording
                //handleTimerViewClick();
                break;
            default:
                break;
        }
    }

    private void handleShutterClick() {
        if (stateEnabled(Controller.CAMERA_STATE_START_RECORD)) {
            stopVideoRecording();
        } else {
            startVideoRecording();
        }
    }

    private void handleTimerViewClick() {
        if (stateEnabled(Controller.CAMERA_STATE_PAUSE_RECORD)) {
            disableState(Controller.CAMERA_STATE_PAUSE_RECORD);
            mUI.refreshPauseButton(true);
        } else {
            enableState(Controller.CAMERA_STATE_PAUSE_RECORD);
            mUI.refreshPauseButton(false);
        }
    }

    /**
     * CameraBaseMenu.OnMenuClickListener
     * @param key clicked menu key
     * @param value clicked menu value
     */
    @Override
    public void onMenuClick(String key, String value) {
        switch (key) {
            case CameraSettings.KEY_SWITCH_CAMERA:
                switchCamera();
                break;
            case CameraSettings.KEY_FLASH_MODE:
                getSettings().setPrefValueById(mDeviceMgr.getCameraId(), key, value);
                mSession.applyRequest(Session.RQ_FLASH_MODE, value);
                break;
            default:
                break;
        }
    }

    private void switchCamera() {
        int currentId = Integer.parseInt(mDeviceMgr.getCameraId());
        int cameraCount = mDeviceMgr.getCameraIdList().length;
        currentId++;
        if (cameraCount < 2) {
            // only one camera, just return
            return;
        } else if (currentId >= cameraCount) {
            currentId = 0;
        }
        String switchId = String.valueOf(currentId);
        mDeviceMgr.setCameraId(switchId);
        boolean ret = getSettings().setGlobalPref(CameraSettings.KEY_VIDEO_ID, switchId);
        if (ret) {
            stopModule();
            startModule();
        } else {
            Log.e(TAG, "set camera id pref fail");
        }
    }
}
