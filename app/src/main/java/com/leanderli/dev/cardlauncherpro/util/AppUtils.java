package com.leanderli.dev.cardlauncherpro.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;

import com.leanderli.dev.cardlauncherpro.HomeActivity;
import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leanderli
 * @description
 * @date 2018/2/11 00111846
 */

public class AppUtils {

    /**
     * 页面跳转
     *
     * @param source
     * @param target
     */
    public static void showIntent(Context source, Class<?> target) throws Exception {
        try {
            Intent intent = new Intent();
            intent.setClass(source, target);
            source.startActivity(intent);
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "showIntent: 页面跳转异常:{}");
            e.printStackTrace();
        }
    }

    /**
     * 页面跳转
     *
     * @param context
     * @param intent
     */
    public static void showIntent(Context context, Intent intent) throws Exception {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "showIntent: 页面跳转异常:{}");
            e.printStackTrace();
        }
    }

    /**
     * 打开应用，addFlags 作为新的任务打开
     *
     * @param position
     */
    public static void openApp(ArrayList<AppInfo> appInfos, Integer position, Context context) throws Exception {
        try {
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(appInfos.get(position).getPackageName(),
                    appInfos.get(position).getActivityName()));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "openApp: 启动应用异常:{}" + appInfos.get(position).toString());
            e.printStackTrace();
        }
    }

    /**
     * 打开应用
     *
     * @param context
     * @param componentName
     * @throws Exception
     */
    public static void openApp(Context context, ComponentName componentName) throws Exception {
        try {
            Intent launchIntent = new Intent();
            launchIntent.setComponent(componentName);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 加载动画
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(context,
                    R.anim.modal_in, R.anim.modal_out);
            ActivityCompat.startActivity(context, launchIntent, activityOptionsCompat.toBundle());
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "openApp: 启动应用异常:{}" + componentName.getPackageName() + ","
                    + componentName.getClassName());
            e.printStackTrace();
        }
    }

    public static void uninstallApp(Context context, String pkgName) {
        Uri uri = Uri.fromParts("package", pkgName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /**
     * 获取应用列表
     *
     * @param context
     * @return
     */
    public static ArrayList<AppInfo> getAllApps(Context context) throws Exception {
        try {
            PackageManager packageManager = context.getPackageManager();

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();

            int appMatch = appMatch = packageManager.MATCH_ALL;
            List<ResolveInfo> resolveInfos = packageManager
                    .queryIntentActivities(mainIntent, appMatch);
            Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(packageManager));
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name;
                String pkgName = reInfo.activityInfo.packageName;
                String appName = (String) reInfo.loadLabel(packageManager);
                Drawable icon = reInfo.loadIcon(packageManager);
                Bitmap appIcon = PictureUtils.drawableToBitmap(icon);
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName(appName);
                appInfo.setPackageName(pkgName);
                appInfo.setActivityName(activityName);
                appInfo.setAppIcon(appIcon);
                appInfos.add(appInfo);
            }
            Log.i(AppConstants.LOG_TAG, "应用数量：{}" + appInfos.size());
            return appInfos;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "getAllApps: 获取应用列表异常");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据应用包名获取应用信息
     *
     * @param context
     * @param appPkgName
     * @return
     */
    public static AppInfo getAppInfo(Context context, String appPkgName) {
        try {
            AppInfo appInfo = null;
            PackageManager packageManager = context.getPackageManager();

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            int appMatch = appMatch = packageManager.MATCH_ALL;
            List<ResolveInfo> resolveInfos = packageManager
                    .queryIntentActivities(mainIntent, appMatch);
            Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(packageManager));
            for (ResolveInfo reInfo : resolveInfos) {
                String pkgName = reInfo.activityInfo.packageName;
                if (appPkgName.equals(pkgName)) {
                    String activityName = reInfo.activityInfo.name;
                    String appName = (String) reInfo.loadLabel(packageManager);
                    Drawable icon = reInfo.loadIcon(packageManager);
                    Bitmap appIcon = PictureUtils.drawableToBitmap(icon);
                    appInfo = new AppInfo();
                    appInfo.setAppName(appName);
                    appInfo.setPackageName(pkgName);
                    appInfo.setActivityName(activityName);
                    appInfo.setAppIcon(appIcon);
                    break;
                }
            }
            return appInfo;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "getAppInfo: 根据包名获取应用信息异常:{}" + appPkgName);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 初始化所有应用数据到数据库
     */
    public static void initAppInfo(Context context) throws Exception {
        ArrayList<AppInfo> appInfos = AppUtils.getAllApps(context);
        if (null != appInfos && appInfos.size() != 0) {
            SQLiteOpenHelper sqLiteOpenHelper = new MyDBHelper(context, AppConstants.APP_DATABASE_NAME,
                    null, AppConstants.APP_DATABASE_VERSION);
            SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            FileOutputStream outputStream = null;
            try {
                for (AppInfo appInfo : appInfos) {
                    String appName = appInfo.getAppName();
                    String activityName = appInfo.getActivityName();
                    String packageName = appInfo.getPackageName();
                    Bitmap appIcon = appInfo.getAppIcon();
                    String activityNameFormat = activityName.toLowerCase().replace(".", "_");
                    String iconName = "." + activityNameFormat + "_.png";
                    String iconPath = FileUtils.getSDPath() + File.separator +
                            AppConstants.APP_FOLDER_NAME + File.separator + AppConstants.ICON_FOLDER +
                            File.separator + iconName;
                    outputStream = new FileOutputStream(iconPath);
                    appIcon.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    sqLiteDatabase.execSQL("insert into tb_app_info values('" + packageName + "', " +
                            "'" + activityName + "', '" + appName + "', '" + iconPath + "');");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != sqLiteDatabase) {
                    sqLiteDatabase.close();
                }
            }
        }
    }

    /**
     * 重启应用
     */
    public static void restartApplication(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 从数据库查询所有应用信息
     *
     * @param context
     * @return
     */
    public static ArrayList<AppInfo> listAllAppInfo(Context context) {
        SQLiteOpenHelper sqLiteOpenHelper = null;
        SQLiteDatabase sqLiteDatabase = null;
        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
        try {
            sqLiteOpenHelper = new MyDBHelper(context, AppConstants.APP_DATABASE_NAME, null, AppConstants.APP_DATABASE_VERSION);
            sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from tb_app_info order by app_name asc", null);
            AppInfo appInfo = null;
            while (cursor.moveToNext()) {
                appInfo = new AppInfo();
                String appName = cursor.getString(cursor.getColumnIndex("app_name"));
                String activityName = cursor.getString(cursor.getColumnIndex("activity_name"));
                String packageName = cursor.getString(cursor.getColumnIndex("package_name"));
                String iconPath = cursor.getString(cursor.getColumnIndex("icon_path"));
                appInfo.setAppName(appName);
                appInfo.setActivityName(activityName);
                appInfo.setPackageName(packageName);
                appInfo.setIconPath(iconPath);
                appInfos.add(appInfo);
            }
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, e.getMessage());
        } finally {
            if (null != sqLiteDatabase) {
                sqLiteDatabase.close();
            }
        }
        return appInfos;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        } else {
            statusBarHeight = 0;
        }
        return statusBarHeight;
    }

    /**
     * 判断字符串是否已英文字母开头
     *
     * @param string
     * @return
     */
    public static boolean isStartWithWord(String string) {
        String str = String.valueOf(string.charAt(0));
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher m = pattern.matcher(str);
        return m.matches();
    }

    /**
     * 是否存在NavigationBar
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        boolean hasNavigationBar = checkDeviceHasNavigationBar(context);
        int navigationBarHeight = 0;
        if (hasNavigationBar) {
            //获取status_bar_height资源的ID
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return navigationBarHeight;
    }
}
