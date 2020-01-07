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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.google.android.cameraview.demo.camera.Config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobExecutor {
    private static final String TAG = Config.getTag(JobExecutor.class);
    private ThreadPoolExecutor mExecutor;
    private Handler mHandler;

    public JobExecutor() {
        // init thread pool
        mExecutor = new ThreadPoolExecutor(1, 4, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(4),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        mHandler = new Handler(Looper.getMainLooper());
    }

    public <T> void execute(final Task<T> task) {
        if (mExecutor != null) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        T res = task.run();
                        postOnMainThread(task, res);
                        task.onJobThread(res);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "job execute error:" + e.getMessage());
                        task.onError(e.getMessage());
                    }
                }
            });
        }
    }

    public void destroy() {
        mExecutor.shutdown();
        mExecutor = null;
    }

    private <T> void postOnMainThread(final Task<T> task, final T res) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                task.onMainThread(res);
            }
        });
    }

    public static abstract class Task<T> {
        public T run() {
            return null;
        }
        public void onMainThread(T result) {
            // default no implementation
        }

        public void onJobThread(T result) {
            // default no implementation
        }

        public void onError(String msg) {
            // default no implementation
        }

    }
}
