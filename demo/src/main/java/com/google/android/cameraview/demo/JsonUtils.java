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

package com.google.android.cameraview.demo;


import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

import ikidou.reflect.TypeBuilder;

/**
 * @创建者 ly
 * @创建时间 2019/4/22
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class JsonUtils {

    public static <T> Response<List<T>> fromJsonArray(String json, Class<T> clazz) {
        Type type = TypeBuilder
                .newInstance(Response.class)
                .beginSubType(List.class)
                .addTypeParam(clazz)
                .endSubType()
                .build();
        return new Gson().fromJson(json, type);
    }

    public static <T> Response<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = TypeBuilder
                .newInstance(Response.class)
                .addTypeParam(clazz)
                .build();
        return new Gson().fromJson(json, type);
    }
}
