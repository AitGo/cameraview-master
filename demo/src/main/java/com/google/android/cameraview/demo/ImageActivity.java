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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * @创建者 ly
 * @创建时间 2019/12/17
 * @描述
 * @更新者 $
 * @更新时间 $
 * @更新描述
 */
public class ImageActivity extends Activity {

    private ImageView imageViwe;
    private ImageView imageViwe2;
    private TextView tv;
    private Button save;

    public static final String cameraPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cameraview" + File.separator + "photo/";
    public static final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cameraview";
    private String mobileTypeFileName = "mobileTypeSetting.json";
    private String path = "";
//    private String path = "/sdcard/newcsi/photo/evidence_20191209091018.jpg";
//    private String path = "/sdcard/newcsi/photo/evidence_20191101103447.jpg";

    private float rea_width = (float) 6.6;
    private float rea_height = (float) 9.3;

    private Bitmap bitmap,newBitmap;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mContext = this;
        initMobileTypeSetting(Build.MANUFACTURER,Build.MODEL);
        imageViwe = findViewById(R.id.iv);
        imageViwe2 = findViewById(R.id.iv2);
        tv= findViewById(R.id.tv);
        tv.setText("品牌:" + Build.MANUFACTURER + "   型号:" + Build.MODEL);
        save = findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存处理过的截图图片，删除之前拍照的大图，处理为8bit灰度图
                BitmapUtils.saveBitmapAsPng(newBitmap,new File(cameraPath + "456.jpg"));
                Bitmap bitmap = BitmapUtils.imageScale(newBitmap,512,512);
                bitmap = BitmapUtils.save640Bitmap(bitmap);
//                bitmap = BitmapUtils.convertGray(bitmap);
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
                BitmapUtils.save8BitBmp(bitmap, cameraPath + "Finger_" + timeStamp + ".bmp");
                BitmapUtils.saveBitmapAsPng(bitmap,new File(cameraPath + "Finger_" + timeStamp + ".jpg"));
                Toast.makeText(mContext,"保存图片成功！",Toast.LENGTH_LONG);
            }
        });

        path = getIntent().getStringExtra("path");

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            BitmapUtils.saveBitmapAsPng(bitmap,new File(cameraPath + "123.jpg"));
            newBitmap = BitmapUtils.get26Bitmap(bitmap, rea_width, rea_height);
            imageViwe.setImageBitmap(newBitmap);
            imageViwe2.setImageBitmap(bitmap);
        }
    }

    public void initMobileTypeSetting(String manufacturer, String model) {
        //检查文件sd卡下是否存在
        if(FileUtils.checkFileExists(filePath + File.separator + mobileTypeFileName)) {
            //判断code，确定是否需要复制
            String assetsFile = FileUtils.ReadAssetsFile(this, mobileTypeFileName);
            Response<List<MobileType>> assetsTypes = JsonUtils.fromJsonArray(assetsFile,MobileType.class);
            String sdCardFile = FileUtils.ReadSDCardFile(this, filePath + File.separator + mobileTypeFileName);
            if(sdCardFile != null) {
                Response<List<MobileType>> sdCardTypes = JsonUtils.fromJsonArray(sdCardFile,MobileType.class);
                if(sdCardTypes.getCode() >= assetsTypes.getCode()) {
                    //读取配置到SP
                    for(MobileType type : assetsTypes.getData()) {
                        if(type.getManufacturer().equals(manufacturer) && type.getModel().equals(model)) {
                            rea_width = type.getWidth();
                            rea_height = type.getHeight();
                        }
                    }
                    return;
                }
            }
        }
        //复制assets文件下文件
        try {
            FileUtils.copyAssetsToDir(this,mobileTypeFileName,filePath);
            String assetsFile = FileUtils.ReadAssetsFile(this, mobileTypeFileName);
            Response<List<MobileType>> assetsTypes = JsonUtils.fromJsonArray(assetsFile,MobileType.class);
            for(MobileType type : assetsTypes.getData()) {
                if(type.getManufacturer().equals(manufacturer) && type.getModel().equals(model)) {
                    rea_width = type.getWidth();
                    rea_height = type.getHeight();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
