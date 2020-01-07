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

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.data.PreferenceGroup;
import com.google.android.cameraview.demo.camera.module.CameraModule;
import com.google.android.cameraview.demo.camera.module.DualCameraModule;
import com.google.android.cameraview.demo.camera.ui.IndicatorView;
import com.google.android.cameraview.demo.camera.utils.XmlInflater;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class ModuleManager implements IndicatorView.IndicatorListener{
    private static int sModuleNum = 1;
    private static int mCurrentIndex = 1;
    private CameraModule mCurrentModule;
    private IndicatorView mIndicatorView;
    private Class<?>[] mModulesClass;
    private Controller mController;

    /**
     * Manage all module, use reflection to create module instance
     * @param context used to init ModuleIndicator
     * @param controller interface for change module
     */
    public ModuleManager(Context context, Controller controller) {
        mController = controller;
        mIndicatorView = mController.getBaseUI().getIndicatorView();
        XmlInflater inflater = new XmlInflater(context);
        PreferenceGroup group = inflater.inflate(R.xml.module_preference);
        mIndicatorView.setIndicatorListener(this);
        boolean loadDualCamera = mController.getCameraSettings(context).isDualCameraEnable();
        mModulesClass = getModuleClass(group, loadDualCamera);
        sModuleNum = mModulesClass.length;
        // init default position
        mIndicatorView.select(mCurrentIndex);
    }

    private Class<?>[] getModuleClass(PreferenceGroup group, boolean loadDualCamera) {
        if (!loadDualCamera) {
            group.remove(DualCameraModule.class.getName());
        }
        Class<?>[] moduleCls = new Class[group.size()];
        for (int i = 0; i < group.size(); i++) {
            // use reflection to get module class
            try {
                moduleCls[i] = Class.forName(group.get(i).getKey());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // add indicator item
            mIndicatorView.addItem(group.get(i).getTitle());
        }
        group.clear();
        return moduleCls;
    }

    public boolean needChangeModule(int index) {
        if (index < 0 || index >= sModuleNum || mCurrentIndex == index) {
            return false;
        } else {
            mCurrentIndex = index;
            return true;
        }
    }

    public CameraModule getNewModule() {
        try {
            mCurrentModule = (CameraModule) mModulesClass[mCurrentIndex].newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return mCurrentModule;
    }

    public CameraModule getCurrentModule() {
        return mCurrentModule;
    }

    public static int getCurrentIndex() {
        return mCurrentIndex;
    }

    public static boolean isValidIndex(int index) {
        return (index >= 0 && index < sModuleNum);
    }

    public static int getModuleCount() {
        return sModuleNum;
    }

    @Override
    public void onPositionChanged(int index) {
        mController.changeModule(index);
    }
}
