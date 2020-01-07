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

import java.util.ArrayList;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class PreferenceGroup {
    private ArrayList<CamListPreference> mList = new ArrayList<>();

    public void add(CamListPreference camListPreference) {
        mList.add(camListPreference);
    }

    public CamListPreference get(int index) {
        return mList.get(index);
    }

    public int size() {
        return mList.size();
    }

    public int find(String key) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public void remove(String key) {
        int index = find(key);
        if (index >= 0) {
            mList.remove(index);
        }
    }

    public void clear() {
        mList.clear();
    }
}
