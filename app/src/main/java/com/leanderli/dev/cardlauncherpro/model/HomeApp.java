package com.leanderli.dev.cardlauncherpro.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.ArrayList;

/**
 * 主屏应用对象
 * @author leanderli
 * @description
 * @date 2018/3/15 00152220
 */
@Table(database = AppDataBase.class)
public class HomeApp {

    private static final String LOG_TAG = AppInfo.class.getName();

    @PrimaryKey
    public String appId;
    /**
     * 应用程序标签
     */
    @Column
    public String appName;
    /**
     * 应用程序图像
     */
    public Bitmap appIcon ;
    /**
     * 应用程序活动名
     */
    @Column
    public String activityName;
    /**
     * 应用程序所对应的包名
     */
    @Column
    public String packageName ;
    /**
     * 应用图标存储路径
     */
    @Column
    public String iconPath;

    private ModelAdapter<HomeApp> modelAdapter = null;

    public HomeApp() {}

    public boolean save() {
        try {
            modelAdapter = FlowManager.getModelAdapter(HomeApp.class);
            modelAdapter.insert(HomeApp.this);
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "save: " + e.toString());
        }
        return false;
    }

    public ArrayList<HomeApp> list() {
        try {
            ArrayList<HomeApp> apps = (ArrayList<HomeApp>) SQLite.select().from(HomeApp.class)
                    .queryList();
            return apps;
        } catch (Exception e) {
            Log.e(LOG_TAG, "list: " + e.toString());
        }
        return null;
    }



    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Bitmap getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Bitmap appIcon) {
        this.appIcon = appIcon;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
