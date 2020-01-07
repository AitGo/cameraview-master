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
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

import com.google.android.cameraview.demo.camera.utils.CameraUtil;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class GestureTextureView extends TextureView {
    private  float mClickDistance;
    private  float mFlingDistance;
    private  float mMaxDistance;
    private  final long DELAY_TIME = 200;

    private GestureListener mListener;
    private float mDownX;
    private float mDownY;
    private long mTouchTime;
    private long mDownTime;

    public interface GestureListener {
        void onClick(float x, float y);

        void onSwipeLeft();

        void onSwipeRight();

        void onSwipe(float percent);

        void onCancel();
    }


    public GestureTextureView(Context context) {
        this(context, null);
    }

    public GestureTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Point point = CameraUtil.getDisplaySize(context);
        mClickDistance = point.x / 20;
        mFlingDistance = point.x / 10;
        mMaxDistance = point.x / 5;
    }

    public void setGestureListener(GestureListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
//                detectSwipe(mDownX, event.getX());
                break;
            case MotionEvent.ACTION_UP:
                mTouchTime = System.currentTimeMillis() - mDownTime;
                detectGesture(mDownX, event.getX(), mDownY, event.getY());
                break;
        }
        return true;
    }

    private void detectGesture(float downX, float upX, float downY, float upY) {
        float distanceX = upX - downX;
        float distanceY = upY - downY;
        if (Math.abs(distanceX) < mClickDistance
                && Math.abs(distanceY) < mClickDistance
                && mTouchTime < DELAY_TIME) {
            mListener.onClick(upX, upY);
        }
//        if (Math.abs(distanceX) > mMaxDistance) {
//            if (distanceX > 0) {
//                mListener.onSwipeRight();
//            } else {
//                mListener.onSwipeLeft();
//            }
//        } else if (Math.abs(distanceX) > mClickDistance && mTouchTime < DELAY_TIME) {
//            if (distanceX > 0) {
//                mListener.onSwipeRight();
//            } else {
//                mListener.onSwipeLeft();
//            }
//        }
//        if (Math.abs(distanceX) < mMaxDistance && mTouchTime > DELAY_TIME) {
//            mListener.onCancel();
//        }
    }

    private void detectSwipe(float downX, float moveX) {
        float alpha;
        if (Math.abs(moveX - downX) > mClickDistance) {
            alpha = ((int) (moveX - downX)) / mMaxDistance;
            if (alpha > 1f) {
                alpha = 1f;
            }
            if (alpha < -1f) {
                alpha = -1f;
            }
            mListener.onSwipe(alpha);
        }
    }
}
