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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.google.android.cameraview.demo.R;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class CamListPreference {
    private String mKey;
    private String mTitle;

    public static final int RES_NULL = 0;

    public CamListPreference(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CamListPreference);
        mKey = a.getString(R.styleable.CamListPreference_key);
        mTitle = a.getString(R.styleable.CamListPreference_title);
        a.recycle();
    }

    public String getKey() {
        return mKey;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIcon() {
        return RES_NULL;
    }

    public CharSequence[] getEntries() {
        return null;
    }

    public CharSequence[] getEntryValues() {
        return null;
    }

    public void setEntries(CharSequence[] entries) {
    }

    public void setEntryValues(CharSequence[] entryValues) {
    }

    public void setIcon(int icon) {
    }

    public int[] getEntryIcons() {
        return null;
    }

    int[] getIds(Resources res, int iconsRes) {
        if (iconsRes == 0) return null;
        TypedArray array = res.obtainTypedArray(iconsRes);
        int n = array.length();
        int ids[] = new int[n];
        for (int i = 0; i < n; ++i) {
            ids[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return ids;
    }
}
