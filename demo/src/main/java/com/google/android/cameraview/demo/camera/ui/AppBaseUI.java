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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.cameraview.demo.R;
import com.google.android.cameraview.demo.camera.callback.CameraUiEvent;
import com.google.android.cameraview.demo.camera.utils.CameraUtil;
import com.google.android.cameraview.demo.camera.utils.MediaFunc;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class AppBaseUI implements View.OnClickListener {
    private CoverView mCoverView;
    private RelativeLayout mPreviewRootView;
    private ShutterButton mShutter;
    private ImageButton mSetting;
    private CircleImageView mThumbnail;
    private LinearLayout mBottomContainer;
    private FocusView mFocusView;
    private LinearLayout mMenuContainer;
    private CameraUiEvent mEvent;
    private IndicatorView mIndicatorView;

    private Point mDisplaySize;
    private int mVirtualKeyHeight;
    private int mTopBarHeight;

    public AppBaseUI(Context context, View rootView) {
        mCoverView = rootView.findViewById(R.id.cover_view);

        mPreviewRootView = rootView.findViewById(R.id.preview_root_view);
        mShutter = rootView.findViewById(R.id.btn_shutter);
        mShutter.setOnClickListener(this);
        mSetting = rootView.findViewById(R.id.btn_setting);
        mSetting.setOnClickListener(this);
        mBottomContainer = rootView.findViewById(R.id.bottom_container);
        mThumbnail = rootView.findViewById(R.id.thumbnail);
        mThumbnail.setOnClickListener(this);
        mMenuContainer = rootView.findViewById(R.id.menu_container);
        mIndicatorView = rootView.findViewById(R.id.indicator_view);

        mDisplaySize = CameraUtil.getDisplaySize(context);
        mVirtualKeyHeight = CameraUtil.getVirtualKeyHeight(context);
        mTopBarHeight = context.getResources()
                .getDimensionPixelSize(R.dimen.menu_item_height);
        mFocusView = new FocusView(context);
        mFocusView.setVisibility(View.GONE);
        mPreviewRootView.addView(mFocusView);
    }

    public void setCameraUiEvent(CameraUiEvent event) {
        mEvent = event;
    }

    public RelativeLayout getRootView() {
        return mPreviewRootView;
    }

    public CoverView getCoverView() {
        return mCoverView;
    }

    public FocusView getFocusView() {
        return mFocusView;
    }

    public View getBottomView() {
        return mBottomContainer;
    }

    public IndicatorView getIndicatorView() {
        return mIndicatorView;
    }

    public void setMenuView(View view) {
        mMenuContainer.removeAllViews();
        mMenuContainer.addView(view);
    }

    public void setShutterMode(String mode) {
        mShutter.setMode(mode);
    }

    public void removeMenuView() {
        mMenuContainer.removeAllViews();
    }

    /**
     * Adjust layout when based on preview width
     * @param width preview screen width
     * @param height preview screen height
     */
    public void updateUiSize(int width, int height) {
        mFocusView.initFocusArea(width, height);
        int realHeight = mDisplaySize.y + mVirtualKeyHeight;
        int bottomHeight = CameraUtil.getBottomBarHeight(mDisplaySize.x);
        RelativeLayout.LayoutParams previewParams = new RelativeLayout.LayoutParams(width, height);
        RelativeLayout.LayoutParams bottomBarParams =
                (RelativeLayout.LayoutParams) mBottomContainer.getLayoutParams();
        int topMargin = 0;
        boolean needTopMargin = (height + 2 * mTopBarHeight) < realHeight;
        boolean needAlignCenter = width == height;
        if (needAlignCenter)  {
            topMargin = (realHeight - mTopBarHeight - mVirtualKeyHeight - height) / 2;
        } else if (needTopMargin) {
            topMargin = mTopBarHeight;
        }
        int reservedHeight = realHeight - topMargin - height;
        boolean needAdjustBottomBar = reservedHeight > bottomHeight;
        if (needAdjustBottomBar) {
            bottomHeight = reservedHeight;
        }
        // preview
        previewParams.setMargins(0, topMargin, 0, 0);
        mPreviewRootView.setLayoutParams(previewParams);
        // bottom bar
        bottomBarParams.height = bottomHeight;
        mBottomContainer.setPadding(0, 0, 0, mVirtualKeyHeight);
        mBottomContainer.setLayoutParams(bottomBarParams);
    }

    /* should not call in main thread */
    public void updateThumbnail(Context context, Handler handler) {
        final Bitmap bitmap = MediaFunc.getThumb(context);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) {
                    mThumbnail.setClickable(false);
                    return;
                }
                mThumbnail.setImageBitmap(bitmap);
                mThumbnail.setClickable(true);
            }
        });
    }

    public void setThumbnail(Bitmap bitmap) {
        if (mThumbnail != null && bitmap != null) {
            mThumbnail.setImageBitmap(bitmap);
        }
    }

    public void setUIClickable(boolean clickable) {
        mShutter.setClickable(clickable);
        mThumbnail.setClickable(clickable);
        mSetting.setClickable(clickable);
        if (mMenuContainer.getChildCount() > 0) {
            mMenuContainer.getChildAt(0).setClickable(clickable);
        }
        mIndicatorView.setClickable(clickable);
    }

    @Override
    public void onClick(View v) {
        if (mEvent != null) {
            mEvent.onAction(CameraUiEvent.ACTION_CLICK, v);
        }
    }
}
