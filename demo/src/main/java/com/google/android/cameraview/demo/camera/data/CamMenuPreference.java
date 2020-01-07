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

package com.google.android.cameraview.demo.camera.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.Config;


/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class CamMenuPreference extends CamListPreference{
    private static final String TAG = Config.getTag(CamMenuPreference.class);
    private int mIcon;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private int[] mEntryIcons;
    private String mDefaultValue;

    public CamMenuPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CamListPreference);
        mIcon = a.getResourceId(R.styleable.CamListPreference_icon, 0);
        mEntries = a.getTextArray(R.styleable.CamListPreference_entries);
        mEntryValues = a.getTextArray(R.styleable.CamListPreference_entryValues);
        mEntryIcons = getIds(context.getResources(),
                a.getResourceId(R.styleable.CamListPreference_entryIcons, 0));
        mDefaultValue = a.getString(R.styleable.CamListPreference_defaultValue);
        a.recycle();
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        mEntries = entries;
    }

    @Override
    public void setEntryValues(CharSequence[] entryValues) {
        mEntryValues = entryValues;
    }

    @Override
    public void setIcon(int icon) {
        mIcon = icon;
    }

    @Override
    public int getIcon() {
        return mIcon;
    }

    @Override
    public CharSequence[] getEntries() {
        return mEntries;
    }

    @Override
    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }

    @Override
    public int[] getEntryIcons() {
        return mEntryIcons;
    }

    private void dump() {
        Log.d(TAG, "key:" + getKey());
        Log.d(TAG, "title:" + getTitle());
        Log.d(TAG, "default value:" + mDefaultValue);
        Log.d(TAG, "icon:" + mIcon);
        if (mEntries != null) {
            for (CharSequence str : mEntries) {
                Log.d(TAG, "entries:" + str);
            }
        }
        if (mEntryValues != null) {
            for (CharSequence str : mEntryValues) {
                Log.d(TAG, "entries value:" + str);
            }
        }
        if (mEntryIcons != null) {
            for (int id : mEntryIcons) {
                Log.d(TAG, "entries icon:" + id);
            }
        }

    }
}
