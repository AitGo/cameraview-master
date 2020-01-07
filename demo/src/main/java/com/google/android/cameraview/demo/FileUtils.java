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

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @创建者 mayn
 * @创建时间 2019/3/15
 * @描述 ${复制，删除文件及文件夹utils}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class FileUtils {

    private static FileUtils instance;
    private static final int SUCCESS = 1;
    private static final int FAILED = 0;
    private Context context;
    private FileUtils.FileOperateCallback callback;
    private volatile boolean isSuccess;
    private String errorStr;

    public static FileUtils getInstance(Context context) {
        if (instance == null)
            instance = new FileUtils(context);
        return instance;
    }

    private FileUtils(Context context) {
        this.context = context;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callback != null) {
                if (msg.what == SUCCESS) {
                    callback.onSuccess();
                }
                if (msg.what == FAILED) {
                    callback.onFailed(msg.obj.toString());
                }
            }
        }
    };

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public FileUtils copyAssetsToSD(final String srcPath, final String sdPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssetsToDst(context, srcPath, sdPath);
                if (isSuccess)
                    handler.obtainMessage(SUCCESS).sendToTarget();
                else
                    handler.obtainMessage(FAILED, errorStr).sendToTarget();
            }
        }).start();
        return this;
    }

    public void setFileOperateCallback(FileUtils.FileOperateCallback callback) {
        this.callback = callback;
    }

    private void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(Environment.getExternalStorageDirectory(), dstPath);
                if (!file.exists()) file.mkdirs();
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(Environment.getExternalStorageDirectory(), dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            errorStr = e.getMessage();
            isSuccess = false;
        }
    }

    public interface FileOperateCallback {
        void onSuccess();

        void onFailed(String error);
    }

    public static void copyAssetsToDir(Context context,String assetsName, String strOutFileName)
            throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName + "/"
                + assetsName);
        myInput = context.getAssets().open(assetsName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    public static void copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        String name = root.getName();
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return;
        }

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if (root.isFile()) {
            CopySdcardFile(fromFile, toFile + "/" + name);
        } else {
            //如果存在则获取当前目录下的全部文件 填充数组
            currentFiles = root.listFiles();
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < currentFiles.length; i++) {
                if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
                {
                    copy(currentFiles[i].getPath() + "/", toFile + "/" + currentFiles[i].getName() + "/");
                } else//如果当前项为文件则进行文件拷贝
                {
                    CopySdcardFile(currentFiles[i].getPath(), toFile + "/" + currentFiles[i].getName());
                }
            }
        }
    }

    public static void copyRename(String fromFile, String toFile,String fileName) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        String name = fileName;
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return;
        }

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if (root.isFile()) {
            CopySdcardFile(fromFile, toFile + "/" + name);
        } else {
            //如果存在则获取当前目录下的全部文件 填充数组
            currentFiles = root.listFiles();
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < currentFiles.length; i++) {
                if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
                {
                    copy(currentFiles[i].getPath() + "/", toFile + "/" + currentFiles[i].getName() + "/");
                } else//如果当前项为文件则进行文件拷贝
                {
                    CopySdcardFile(currentFiles[i].getPath(), toFile + "/" + currentFiles[i].getName());
                }
            }
        }
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    private static void CopySdcardFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();

        } catch (Exception ex) {
            Log.e("FileUtils","复制单个文件操作出错");
            ex.printStackTrace();
        }
    }

    public static void CopySdcardFile(InputStream fosfrom, String toFile) {
        try {
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();

        } catch (Exception ex) {
            Log.e("FileUtils","复制单个文件操作出错");
            ex.printStackTrace();
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 根据指定路径生成文件夹
     * @param filePath
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }

    //读取本地JSON字符
    public static String ReadAssetsFile(Context context,String fileName) {
        InputStream is = null;
        String msg = null;
        try {
            is = context.getResources().getAssets().open(fileName);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            msg = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    //读取本地JSON字符
    public static String ReadSDCardFile(Context context,String filePath) {
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(filePath);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
            return null;
        }
        else {
            InputStream is = null;
            String msg = null;
            try {
                is = new FileInputStream(file);
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                msg = new String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return msg;
        }
    }

    /**
     * 检查目录下是否有子文件夹
     * @param photoPath
     * @return
     */
    public static boolean checkChildDir(String photoPath) {
        File file = new File(photoPath);
        File[] files = file.listFiles();
        for(File child : files) {
            if(child.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    public static String getLastPhoto(String photoPath) {
        List<File> compareFiles = new ArrayList<>();
        File file = new File(photoPath);
        File[] files = file.listFiles();
        for(File child : files) {
            if(child.isFile()) {
                compareFiles.add(child);
            }
        }
        if(compareFiles.size() > 0) {
            //排序
            File max = Collections.max(compareFiles);
            return max.getAbsolutePath();
        }else {
            return "";
        }
    }

    public static boolean checkFileExists(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean checkFileExists(File file) {
        return file.exists();
    }
}
