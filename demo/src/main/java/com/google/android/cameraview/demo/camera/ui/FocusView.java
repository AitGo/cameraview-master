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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.cameraview.demo.R;

/**
 * @创建者 ly
 * @创建时间 2019/12/30
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class FocusView extends View {

    private final String TAG = this.getClass().getSimpleName();
    private int radiusOuter, radiusInner, strokeWidth;
    private int colorSuccess, colorFailed, colorNormal, colorCurrent;
    private int previewWidth;
    private int previewHeight;
    private RectF outerRectF, innerRectF;
    private Paint paint;
    private ObjectAnimator animator;

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources resources = context.getResources();
        radiusOuter = resources.getDimensionPixelSize(R.dimen.radius_outer);
        radiusInner = resources.getDimensionPixelSize(R.dimen.radius_inner);
        strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_width);

        colorFailed = resources.getColor(R.color.focus_failed);
        colorSuccess = resources.getColor(R.color.focus_success);
        colorNormal = resources.getColor(R.color.focus_normal);
        colorCurrent = colorNormal;

        outerRectF = new RectF(strokeWidth, strokeWidth,
                radiusOuter * 2 - strokeWidth, radiusOuter * 2 - strokeWidth);
        innerRectF = new RectF(radiusOuter - radiusInner, radiusOuter - radiusInner,
                radiusOuter + radiusInner, radiusOuter + radiusInner);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);

        initAnimation();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(radiusOuter * 2, radiusOuter * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas, colorCurrent);
    }

    private void drawCircle(Canvas canvas, int color) {
        paint.setColor(color);
        //canvas.drawCircle(getWidth()/2,getHeight()/2,radiusOuter-strokeWidth,paint);
        //canvas.drawCircle(getWidth()/2,getHeight()/2,radiusInner,paint);
        for (int i = 0; i < 4; i++) {
            canvas.drawArc(outerRectF, 90 * i + 5, 80, false, paint);
            canvas.drawArc(innerRectF, 90 * i + 50, 80, false, paint);
        }

    }

    public void startFocus() {
        //this.setRotation(0);
        this.setVisibility(VISIBLE);
        colorCurrent = colorNormal;
        invalidate();
        setAnimator(0, 90, 500).start();
    }

    public void focusSuccess() {

        colorCurrent = colorSuccess;
        invalidate();
        setAnimator(90, 0, 200).start();
    }

    public void focusFailed() {
        colorCurrent = colorFailed;
        invalidate();
        setAnimator(180, 0, 200).start();
    }

    public void hideFocusView() {
        this.setVisibility(GONE);
    }

    public ObjectAnimator setAnimator(float from, float to, long duration) {
        //ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", from, to);
        animator.cancel();
        animator.setFloatValues(from, to);
        animator.setDuration(duration);
        return animator;
    }

    private void initAnimation() {
        animator = new ObjectAnimator();
        animator.setTarget(this);
        animator.setPropertyName("rotation");
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void moveToPosition(float x, float y) {
        x -= radiusOuter;
        y -= radiusOuter;
        this.setTranslationX(x);
        this.setTranslationY(y);
        this.setVisibility(VISIBLE);
        colorCurrent = colorNormal;
        invalidate();
    }

    public void resetToDefaultPosition() {
        int x = previewWidth / 2 - radiusOuter;
        int y = previewHeight / 2 - radiusOuter;
        this.setTranslationX(x);
        this.setTranslationY(y);
    }

    public void initFocusArea(int width, int height) {
        previewWidth = width;
        previewHeight = height;
        Log.d(TAG, "init focus view:" + previewWidth + "x" + previewHeight);
        resetToDefaultPosition();
    }

    public static RectF rectToRectF(Rect r) {
        return new RectF(r.left, r.top, r.right, r.bottom);
    }
}
