package com.leanderli.dev.cardlauncherpro.util;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author leanderli
 * @description
 * @date 2018/2/25 00252015
 */

public class FileUtils {

    private final int _STARTTOCREATE = 1005 ;

    private Handler mHandler = null;

    public FileUtils() {

    }

    public FileUtils(Handler mHnadler) {
        this.mHandler = mHnadler;
    }

    /**
     * 获取内存卡路径
     * @return
     */
    public static String getSDPath() throws Exception {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 获取跟目录
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

    /**
     * 根据上下文我以及路径创建文件夹
     * @param path
     * @param context
     * @return
     */
    public static String mkdirs(String path, Context context) throws Exception {
        String sdcard = getSDPath();
        if (path.indexOf(getSDPath()) == -1) {
            path = sdcard + (path.indexOf("/") == 0 ? "" : "/") + path;
        }
        File destDir = new File(path);
        if (!destDir.exists()) {
            path = makeDir(path);
            if (path == null) {
                return null;
            }
        }
        return path;
    }

    /**
     * 创建文件夹
     * @param path
     * @return
     */
    private static String makeDir(String path) throws Exception {
        String sdPath = getSDPath();
        String[] dirs = path.replace(sdPath, "").split("/");
        StringBuffer filePath = new StringBuffer(sdPath);
        for (String dir : dirs) {
            if (!"".equals(dir) && !dir.equals(sdPath)) {
                filePath.append("/").append(dir);
                File destDir = new File(filePath.toString());
                if (!destDir.exists()) {
                    boolean b = destDir.mkdirs();
                    if (!b) {
                        return null;
                    }
                }
            }
        }
        return filePath.toString();
    }

    public static void openFileOutput(Context context, String fileName, byte[] bytes) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(bytes, 0 , bytes.length);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fileOutputStream) {
                fileOutputStream.close();
            }
        }
    }

    public static byte[] openFileInput(Context context, String fileName) throws IOException {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = context.openFileInput(fileName);
            byteArrayOutputStream = new ByteArrayOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0 , bytes.length);
            }
            byteArrayOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fileInputStream) {
                fileInputStream.close();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
}
