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

package com.google.android.cameraview.demo.camera.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.callback.MenuInfo;
import com.google.android.cameraview.demo.camera.data.CamListPreference;
import com.google.android.cameraview.demo.camera.data.PrefListAdapter;
import com.google.android.cameraview.demo.camera.data.PreferenceGroup;
import com.google.android.cameraview.demo.camera.data.SubPrefListAdapter;
import com.google.android.cameraview.demo.camera.manager.CameraSettings;
import com.google.android.cameraview.demo.camera.utils.XmlInflater;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class CameraMenu extends CameraBaseMenu {

    public static final String TAG = Config.getTag(CameraMenu.class);
    private CameraSubMenu mSubMenu;
    private Context mContext;
    private OnMenuClickListener mMenuClickListener;
    private MenuInfo mMenuInfo;
    private PrefListAdapter mAdapter;

    public CameraMenu(Context context, int resId, MenuInfo info) {
        super(context);
        mContext = context;
        mMenuInfo = info;
        XmlInflater xmlInflater = new XmlInflater(context);
        mAdapter = new PrefListAdapter(context, xmlInflater.inflate(resId));
        updateAllMenuIcon();
        mAdapter.setClickListener(mMenuListener);
        recycleView.setAdapter(mAdapter);
    }

    private void updateAllMenuIcon() {
        PreferenceGroup group = mAdapter.getPrefGroup();
        for (int i = 0; i < group.size(); i++) {
            updateMenuIcon(i);
        }
    }

    /**
     * Find icon preference and notify update item
     * @param position used for get CamListPreference
     */
    private void updateMenuIcon(int position) {
        if (position < 0) { return; }
        CamListPreference preference = mAdapter.getPrefGroup().get(position);
        switch (preference.getKey()) {
            case CameraSettings.KEY_SWITCH_CAMERA:
                updateIcon(preference, mMenuInfo.getCurrentCameraId());
                break;
            case CameraSettings.KEY_FLASH_MODE:
                updateIcon(preference, mMenuInfo.getCurrentValue(preference.getKey()));
                break;
            default:
                break;
        }
        mAdapter.notifyItemChanged(position);
    }

    /**
     * Find correct icon in icon list by currentValue
     * @param preference which icon need update
     * @param currentValue current value of this preference stored in shared pref
     */
    private void updateIcon(CamListPreference preference, String currentValue) {
        int index = getIndex(preference.getEntryValues(), currentValue);
        if (index < preference.getEntryIcons().length && index >= 0) {
            preference.setIcon(preference.getEntryIcons()[index]);
        }
    }


    public View getView() {
        return recycleView;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    /**
     * Camera menu click listener
     */
    private PrefListAdapter.PrefClickListener mMenuListener =
            new PrefListAdapter.PrefClickListener() {
                @Override
                public void onClick(View view, int position, CamListPreference preference) {
                    // if is switch menu click, no need show sub menu
                    if (preference.getKey().equals(CameraSettings.KEY_SWITCH_CAMERA)) {
                        if (mMenuClickListener != null) {
                            mMenuClickListener.onMenuClick(preference.getKey(), null);
                            updateMenuIcon(position);
                        }
                        return;
                    }
                    if (mSubMenu == null) {
                        mSubMenu = new CameraSubMenu(mContext, preference);
                        mSubMenu.setItemClickListener(mItemClickListener);
                    }
                    mSubMenu.notifyDataSetChange(preference, mMenuInfo);
                    mSubMenu.show(view, 0, view.getHeight());
                }
            };

    /**
     * Camera sub menu click listener
     */
    private SubPrefListAdapter.PrefItemClickListener mItemClickListener =
            new SubPrefListAdapter.PrefItemClickListener() {
                @Override
                public void onItemClick(String key, String value) {
                    Log.d(TAG, "sub menu click key:" + key + " value:" + value);
                    if (mMenuClickListener != null) {
                        mMenuClickListener.onMenuClick(key, value);
                    }
                    // after menu value change, update icon
                    if (key.equals(CameraSettings.KEY_FLASH_MODE)) {
                        mSubMenu.close();
                        int position = mAdapter.getPrefGroup().find(key);
                        updateMenuIcon(position);
                    }
                }
            };

    public void close() {
        if (mSubMenu != null) {
            mSubMenu.close();
        }
    }
}
