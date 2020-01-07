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
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.callback.MenuInfo;
import com.google.android.cameraview.demo.camera.data.CamListPreference;
import com.google.android.cameraview.demo.camera.data.SubPrefListAdapter;
import com.google.android.cameraview.demo.camera.manager.CameraSettings;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class CameraSubMenu extends CameraBaseMenu {

    private SubPrefListAdapter mAdapter;
    private PopupWindow mPopWindow;

    public CameraSubMenu(Context context, CamListPreference preference) {
        super(context);
        mAdapter = new SubPrefListAdapter(context, preference);
        recycleView.setAdapter(mAdapter);
        initPopWindow(context);
    }

    private void initPopWindow(Context context) {
        mPopWindow = new PopupWindow(context);
        mPopWindow.setContentView(recycleView);
        int color = context.getResources().getColor(R.color.pop_window_bg);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(color));
        mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopWindow.setAnimationStyle(-1);
        mPopWindow.setOutsideTouchable(false);
    }

    public void setItemClickListener(SubPrefListAdapter.PrefItemClickListener listener) {
        mAdapter.setClickListener(listener);
    }

    public void notifyDataSetChange(CamListPreference preference, MenuInfo info) {
        updatePrefValueByMenuInfo(preference, info);
        mAdapter.updateDataSet(preference);
    }

    private void updatePrefValueByMenuInfo(CamListPreference preference, MenuInfo info) {
        if (info == null) { return; }
        switch (preference.getKey()) {
            case CameraSettings.KEY_SWITCH_CAMERA:
                preference.setEntries(info.getCameraIdList());
                preference.setEntryValues(info.getCameraIdList());
                mAdapter.updateHighlightIndex(getIndex(info.getCameraIdList(),
                        info.getCurrentCameraId()), false);
                break;
            default:
                mAdapter.updateHighlightIndex(-1, false);
                break;
        }
    }

    public void show(View view, int xOffset, int yOffset) {
        if (!mPopWindow.isShowing()) {
            mPopWindow.showAtLocation(view, Gravity.TOP | Gravity.CENTER, xOffset, yOffset);
        } else  {
            mPopWindow.dismiss();
        }
    }

    public void close() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
        }
    }
}
