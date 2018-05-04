package com.leanderli.dev.cardlauncherpro.model;

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

import java.util.ArrayList;

/**
 * 应用分组信息对象
 * @author leanderli
 * @description
 * @date 2018/3/23 00232227
 */
@Table(database = AppDataBase.class)
public class GroupInfo extends BaseModel {

    /**
     * 分组 id
     */
    @PrimaryKey
    private String id;
    /**
     * 组名
     */
    @Column
    private String groupName;

    @ColumnIgnore
    private boolean isChecked;

    private ModelAdapter<GroupInfo> modelAdapter = null;

    public GroupInfo() {
    }

    public GroupInfo(String id, String groupName, boolean isChecked) {
        this.id = id;
        this.groupName = groupName;
        this.isChecked = isChecked;
    }

    public boolean save() {
        try {
            modelAdapter = FlowManager.getModelAdapter(GroupInfo.class);
            modelAdapter.insert(GroupInfo.this);
            return true;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "save: 分组信息保存异常:{}" + GroupInfo.this.toString());
            e.printStackTrace();
        }
        return false;
    }

    public boolean remove() {
        try {
            modelAdapter = FlowManager.getModelAdapter(GroupInfo.class);
            modelAdapter.delete(GroupInfo.this);
            return true;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "remove: 分组信息删除异常:{}" + GroupInfo.this.toString());
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<GroupInfo> list() {
        try {
            ArrayList<GroupInfo> groupInfos = (ArrayList<GroupInfo>) SQLite.select().from(GroupInfo.class).queryList();
            return groupInfos;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "list: 分组信息查询列表异常:{}" + GroupInfo.this.toString());
            e.printStackTrace();
        }
        return null;
    }

    public GroupInfo getByParam(String groupName) {
        try {
            GroupInfo groupInfo = SQLite.select().from(GroupInfo.class).where(GroupInfo_Table.groupName.eq(groupName)).querySingle();
            return groupInfo;
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "get: 查询单个分组信息异常:{}");
            e.printStackTrace();
        }
        return null;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "id='" + id + '\'' +
                ", groupName='" + groupName + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
