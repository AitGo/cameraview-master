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

package com.google.android.cameraview.demo.camera;

import android.graphics.ImageFormat;
import android.util.Size;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class Config {
    // some default config, not actually
    public static final String MAIN_ID = "0";
    public static final String AUX_ID = "1";
    private static final String TAG_PREFIX = "mycamera/";
    public static final float AUX_PREVIEW_SCALE = 0.3F;
    public static final String IMAGE_FORMAT = String.valueOf(ImageFormat.JPEG);
    public static final String NULL_VALUE = "SharedPreference No Value";
    public static final int THUMB_SIZE = 128;

    public static String getTag(Class<?> cls) {
        return TAG_PREFIX + cls.getSimpleName();
    }

    public static boolean ratioMatched(Size size) {
        return size.getWidth() * 3 == size.getHeight() * 4;
    }

    public static boolean videoRatioMatched(Size size) {
        return size.getWidth() * 9 == size.getHeight() * 16;
    }
}
