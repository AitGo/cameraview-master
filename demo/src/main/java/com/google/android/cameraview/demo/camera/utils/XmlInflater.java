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

package com.google.android.cameraview.demo.camera.utils;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.google.android.cameraview.demo.camera.Config;
import com.google.android.cameraview.demo.camera.data.CamListPreference;
import com.google.android.cameraview.demo.camera.data.PreferenceGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class XmlInflater {
    private static final String TAG = Config.getTag(XmlInflater.class);
    private static final Class<?>[] CTOR_SIGNATURE =
            new Class[] {Context.class, AttributeSet.class};
    private static final ArrayMap<String, Constructor<?>> sConstructorMap = new ArrayMap<>();
    private Context mContext;

    public XmlInflater(Context context) {
        mContext = context;
    }

    private CamListPreference getInstance(String tagName, Object[] args) {
        Constructor<?> constructor = sConstructorMap.get(tagName);
        if (constructor == null) {
            try {
                Class<?> clazz = mContext.getClassLoader().loadClass(tagName);
                constructor = clazz.getConstructor(CTOR_SIGNATURE);
                sConstructorMap.put(tagName, constructor);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        try {
            assert constructor != null;
            return (CamListPreference) constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "init preference error");
        return null;
    }

    public PreferenceGroup inflate(int resId) {
        XmlPullParser parser = mContext.getResources().getXml(resId);
        AttributeSet attrs = Xml.asAttributeSet(parser);
        Object[] args = new Object[]{mContext, attrs};
        PreferenceGroup preferenceGroup = new PreferenceGroup();
        try {
            for (int type = parser.next();
                 type != XmlPullParser.END_DOCUMENT; type = parser.next()) {
                if (type != XmlPullParser.START_TAG) continue;
                int depth = parser.getDepth();
                if (depth > 1) {
                    CamListPreference pref = getInstance(parser.getName(), args);
                    preferenceGroup.add(pref);
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return preferenceGroup;
    }
}
