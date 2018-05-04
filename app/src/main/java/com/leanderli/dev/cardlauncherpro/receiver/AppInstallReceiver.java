package com.leanderli.dev.cardlauncherpro.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;
import com.leanderli.dev.cardlauncherpro.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


/**
 * 应用安装卸载广播接收者
 * @author leanderli
 * @description
 * @date 2018/3/3 00032101
 */

public class AppInstallReceiver extends BroadcastReceiver {

    private Handler handler;

    public AppInstallReceiver() {}

    public AppInstallReceiver(Handler handler) {
        this.handler = handler;
    }

    private final String LOG_TAG = AppInstallReceiver.class.getName();
    private UpdateAppInfoTak updateAppInfoTak;
    private PackageManager packageManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String cacheStr = intent.getDataString();
        String appPkgName = cacheStr.substring(cacheStr.indexOf(":") + 1, cacheStr.length());
        packageManager = context.getPackageManager();
        Log.i(LOG_TAG, "onReceive: " + appPkgName);
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            updateAppInfoTak = new UpdateAppInfoTak(context, appPkgName, 0);
            updateAppInfoTak.execute();
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            updateAppInfoTak = new UpdateAppInfoTak(context, appPkgName, 1);
            updateAppInfoTak.execute();
        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {

        }
    }

    /**
     * 更新应用信息异步任务
     */
    class UpdateAppInfoTak extends AsyncTask<String, String, Boolean> {

        private Context context;
        private String packageName;
        private int type;

        public UpdateAppInfoTak(Context context, String packageName, int type) {
            this.context = context;
            this.packageName = packageName;
            this.type = type;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean result = false;
            if (0 == type) {
                if (save()) {
                    Message message = Message.obtain();
                    message.what = AppEnum.ADD_APPS.getValue();
                    handler.sendMessage(message);
                    result = true;
                }
            }
            if (1 == type) {
                if (remove()) {
                    Message message = Message.obtain();
                    message.what = AppEnum.REMOVE_APPS.getValue();
                    handler.sendMessage(message);
                    result = true;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        private boolean save() {
            boolean result = false;
            FileOutputStream outputStream = null;
            try {
                AppInfo appInfo = AppUtils.getAppInfo(context, packageName);
                if (null != appInfo) {
                    String id = UUID.randomUUID().toString();
                    String activityName = appInfo.getActivityName();
                    String packageName = appInfo.getPackageName();
                    String appId = packageName.replace(".", "_")
                            + "@" + activityName.replace(".", "_")
                            + "@";
                    appInfo.setId(id);
                    String iconName = appId + ".png";
                    String iconPath = FileUtils.getSDPath() + File.separator +
                            AppConstants.APP_FOLDER_NAME + File.separator + AppConstants.ICON_FOLDER +
                            File.separator + iconName;

                    File localIcon = new File(iconPath);
                    if (!localIcon.exists() && !localIcon.isFile()) {
                        outputStream = new FileOutputStream(iconPath);
                        appInfo.getAppIcon().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.flush();
                    }

                    appInfo.setIconPath(iconPath);

                    String pinyin = "";
                    String appName = appInfo.getAppName();
                    if (!AppUtils.isStartWithWord(appName)) {
                        pinyin = PinyinHelper.convertToPinyinString(appName,"", PinyinFormat.WITHOUT_TONE);
                    } else {
                        pinyin = appName;
                    }
                    appInfo.setPinyin(pinyin.toLowerCase());

                    if (appInfo.getByParam()) {
                        appInfo.remove();
                    }
                    appInfo.save();
                    result = true;
                } else {
                    Log.e(LOG_TAG, "doInBackground: " + "没有获取到应用信息:{}" + appInfo.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != outputStream) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        private boolean remove() {
            boolean result = false;
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            if (appInfo.getByParam()) {
                appInfo.remove();
                result = true;
            }
            return result;
        }
    }
}
