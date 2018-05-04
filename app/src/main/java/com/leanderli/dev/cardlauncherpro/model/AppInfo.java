package com.leanderli.dev.cardlauncherpro.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/2/21 00211741
 */
@Table(database = AppDataBase.class)
public class AppInfo extends BaseModel {

    @PrimaryKey
    public String id;
    /**
     * 应用程序标签
     */
    @Column
    public String appName;
    /**
     * 应用名称中文拼音
     */
    @Column
    public String pinyin;
    /**
     * 应用程序图像
     */
    public Bitmap appIcon;
    /**
     * 应用程序活动名
     */
    @Column
    public String activityName;
    /**
     * 应用程序所对应的包名
     */
    @Column
    public String packageName;
    /**
     * 应用图标存储路径
     */
    @Column
    public String iconPath;
    /**
     * 分组 id
     */
    @Column
    public String groupId;
    /**
     * 分组 name
     */
    @Column
    public String groupName;

    @ColumnIgnore
    private boolean isChecked;

    private ModelAdapter<AppInfo> modelAdapter = null;

    public AppInfo() {
    }

    /**
     * 保存
     *
     * @return
     */
    public boolean save() {
        try {
            modelAdapter = FlowManager.getModelAdapter(AppInfo.class);
            modelAdapter.insert(AppInfo.this);
            return true;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "应用信息保存异常:{}" + AppInfo.this.toString());
            e.printStackTrace();
        }
        return false;
    }

    public boolean update() {
        try {
            modelAdapter = FlowManager.getModelAdapter(AppInfo.class);
            modelAdapter.update(AppInfo.this);
            return true;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "update: 更新应用信息异常:{}" + AppInfo.this.toString());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除
     *
     * @return
     */
    public boolean remove() {
        boolean result = false;
        try {
            modelAdapter = FlowManager.getModelAdapter(AppInfo.class);
            modelAdapter.delete(AppInfo.this);
            result = true;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "删除应用信息异常:{}");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询列表
     *
     * @return
     */
    public ArrayList<AppInfo> list() {
        try {
            ArrayList<AppInfo> appInfos = (ArrayList<AppInfo>) SQLite.select().from(AppInfo.class)
                    .orderBy(AppInfo_Table.pinyin.asc())
                    .queryList();
            return appInfos;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "查询所有应用信息异常:{}");
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<AppInfo> listByParam(String groupId) {
        try {
            ArrayList<AppInfo> appInfos = null;
            if (StringUtils.isBlank(groupId)) {
                appInfos = (ArrayList<AppInfo>) SQLite.select().from(AppInfo.class)
                        .where(AppInfo_Table.groupId.isNull())
                        .orderBy(AppInfo_Table.pinyin.asc())
                        .queryList();
            } else if (null != groupId) {
                appInfos = (ArrayList<AppInfo>) SQLite.select().from(AppInfo.class)
                        .where(AppInfo_Table.groupId.eq(groupId))
                        .orderBy(AppInfo_Table.pinyin.asc())
                        .queryList();
            }
            return appInfos;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "listByParam: 根据groupId查询应用列表异常:{}" + groupId);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据条件查询
     *
     * @return
     */
    public boolean getByParam() {
        boolean result = false;
        try {
            AppInfo appInfo = SQLite.select().from(AppInfo.class)
                    .where(AppInfo_Table.packageName.eq(this.packageName)).querySingle();
            if (null != appInfo) {
                this.setAppName(appInfo.getAppName());
                this.setPinyin(appInfo.getPinyin());
                this.setPackageName(appInfo.getPackageName());
                this.setActivityName(appInfo.getActivityName());
                this.setId(appInfo.getId());
                this.setIconPath(appInfo.getIconPath());
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "id='" + id + '\'' +
                ", appName='" + appName + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", appIcon=" + appIcon +
                ", activityName='" + activityName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", iconPath='" + iconPath + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
