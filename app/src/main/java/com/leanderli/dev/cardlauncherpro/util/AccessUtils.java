package com.leanderli.dev.cardlauncherpro.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * @author leanderli
 * @description
 * @date 2018/3/25 00252054
 */

public class AccessUtils {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    /**
     * 存储卡
     * @param activity
     */
    public static int verifyStoragePermissions(Activity activity) {
        int permissionGrantedStatus = -1;
        try {
            //检测是否有写的权限
            permissionGrantedStatus = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionGrantedStatus != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissionGrantedStatus;
    }
}
