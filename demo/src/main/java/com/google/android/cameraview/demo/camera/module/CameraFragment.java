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

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.cameraview.demo.ImageActivity;
import com.google.android.cameraview.demo.MainActivity;
import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.CameraActivity;
import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.manager.CameraSettings;
import com.google.android.cameraview.demo.camera.manager.CameraToolKit;
import com.google.android.cameraview.demo.camera.manager.Controller;
import com.google.android.cameraview.demo.camera.manager.ModuleManager;
import com.google.android.cameraview.demo.camera.ui.AppBaseUI;
import com.google.android.cameraview.demo.camera.utils.JobExecutor;

import androidx.annotation.Nullable;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class CameraFragment extends Fragment implements CameraModule.TakenPhotoListener {

    private static final String TAG = Config.getTag(CameraFragment.class);
    private CameraToolKit mToolKit;
    private ModuleManager mModuleManager;
    private AppBaseUI mBaseUI;
    private CameraSettings mSettings;
    private Context mAppContext;
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppContext = getActivity().getApplicationContext();
        mToolKit = new CameraToolKit(mAppContext);
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.camera_fragment_layout, null);
        mBaseUI = new AppBaseUI(mAppContext, mRootView);
        mModuleManager = new ModuleManager(mAppContext, mController);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mModuleManager.getCurrentModule() == null) {
            Log.d(TAG, "init module");
            updateThumbnail(mAppContext);
            CameraModule cameraModule = mModuleManager.getNewModule();
            cameraModule.init(mAppContext, mController);
        }
        mModuleManager.getCurrentModule().startModule();
        mModuleManager.getCurrentModule().setTakenPhoto(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mModuleManager.getCurrentModule() != null) {
            mModuleManager.getCurrentModule().stopModule();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mToolKit.destroy();
    }

    public Controller getController() {
        return mController;
    }


    private Controller mController = new Controller() {
        @Override
        public void changeModule(int index) {
            if (mModuleManager.needChangeModule(index)) {
                mModuleManager.getCurrentModule().stopModule();
                CameraModule module = mModuleManager.getNewModule();
                module.init(mAppContext, this);
                module.startModule();
            }
        }

        @Override
        public CameraToolKit getToolKit() {
            return mToolKit;
        }

        @Override
        public void showSetting() {
            getCameraActivity().addSettingFragment();
        }


        @Override
        public CameraSettings getCameraSettings(Context context) {
            if (mSettings == null) {
                mSettings = new CameraSettings(context);
            }
            return mSettings;
        }

        @Override
        public AppBaseUI getBaseUI() {
            return mBaseUI;
        }
    };

    private CameraActivity getCameraActivity() {
        if (getActivity() instanceof CameraActivity) {
            return (CameraActivity) getActivity();
        } else {
            throw new RuntimeException("CameraFragment must add to CameraActivity");
        }
    }

    private void updateThumbnail(final Context context) {
        mToolKit.getExecutor().execute(new JobExecutor.Task<Void>() {
            @Override
            public Void run() {
                mBaseUI.updateThumbnail(context, mToolKit.getMainHandler());
                return super.run();
            }
        });
    }

    @Override
    public void taken(Uri uri, String path, Bitmap thumbnail) {
        Intent intent = new Intent(getActivity(), ImageActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
    }
}
