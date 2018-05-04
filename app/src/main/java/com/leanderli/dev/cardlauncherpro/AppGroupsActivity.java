package com.leanderli.dev.cardlauncherpro;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.adapter.AppGroupAdapter;
import com.leanderli.dev.cardlauncherpro.adapter.UnselectdAppAdapter;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.view.SmoothCheckBox;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 桌面设置-应用分组设置
 * @author leanderli
 * @description
 * @date 2018/3/23 00232224
 */

public class AppGroupsActivity extends AppCompatActivity {

    private RelativeLayout backLayout;

    private ListView groupListView;

    private SmoothCheckBox smoothCheckBox;

    private ArrayList<GroupInfo> groupInfos = new ArrayList<GroupInfo>();

    private AppGroupAdapter adapter;

    private Message message = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (AppEnum.MSG_GROUP_EXIST.getValue() == msg.what) {
                Toast.makeText(AppGroupsActivity.this, "该组已建立，可以前往组内编辑", Toast.LENGTH_SHORT).show();
            } else if (AppEnum.MSG_GROUP_NAME_IS_NULL.getValue() == msg.what) {
                Toast.makeText(AppGroupsActivity.this, "组名是必须的", Toast.LENGTH_SHORT).show();
            } else if (AppEnum.MSG_GROUP_SAVE.getValue() == msg.what) {
                Toast.makeText(AppGroupsActivity.this, "分组信息已保存", Toast.LENGTH_SHORT).show();
                GroupInfo groupInfo = new GroupInfo();
                groupInfos = groupInfo.list();
                adapter.updateData(groupInfos);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_groups);
        initView();
        initData();
    }

    private void initView() {
        backLayout = findViewById(R.id.rel_back);
        groupListView = findViewById(R.id.lv_groups);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        try {
            GroupInfo groupInfo = new GroupInfo();
            groupInfos = groupInfo.list();
            adapter = new AppGroupAdapter(AppGroupsActivity.this, groupInfos);
            groupListView.setAdapter(adapter);
            groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupInfo groupInfo = (GroupInfo) parent.getAdapter().getItem(position);
                    Intent intent = new Intent();
                    intent.setClass(AppGroupsActivity.this, NewGroupActivity.class);
                    String[] groupInfoArr = new String[2];
                    groupInfoArr[0] = groupInfo.getId();
                    groupInfoArr[1] = groupInfo.getGroupName();
                    intent.putExtra("groupInfoArr", groupInfoArr);
                    startActivity(intent);
                }
            });
            groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupInfo groupInfo = (GroupInfo) parent.getAdapter().getItem(position);
                    groupInfo.setChecked(!groupInfo.isChecked());
                    smoothCheckBox = (SmoothCheckBox) view.findViewById(R.id.ckb_);
                    smoothCheckBox.setChecked(groupInfo.isChecked(), true);
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "initData: 初始化分组信息异常:{}");
            e.printStackTrace();
        }
    }

    public void showSelectedItem(View view) {
        for (GroupInfo groupInfo : groupInfos) {
            if (groupInfo.isChecked()) {
                Toast.makeText(AppGroupsActivity.this, groupInfo.getId() + "," + groupInfo.getGroupName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 新建分组
     *
     * @param view
     */
    public void addGroup(View view) {
        alertUnselectedAppDialogAndSaveGroup();
    }

    @SuppressLint("RestrictedApi")
    private void alertUnselectedAppDialogAndSaveGroup() {
        try {
            // 查询所有未分组应用
            AppInfo appInfo = new AppInfo();
            final ArrayList<AppInfo> appInfos = appInfo.listByParam(null);
            final View view = View.inflate(AppGroupsActivity.this, R.layout.unselected_app_list, null);
            UnselectdAppAdapter unselectdAppAdapter = new UnselectdAppAdapter(AppGroupsActivity.this, appInfos);
            TextView noMuchApps = view.findViewById(R.id.tv_no_apps);
            GridView gridView = view.findViewById(R.id.gv_unselected_app);
            Button positiveButton = view.findViewById(R.id.btn_done);
            Button negativeButton = view.findViewById(R.id.btn_dismiss);
            final EditText groupNameEdit = view.findViewById(R.id.et_group_name);

            int status = noMuchApps.getVisibility();
            if (null != appInfos && appInfos.size() == 0) {
                if (View.GONE == status) {
                    noMuchApps.setVisibility(View.VISIBLE);
                }
            } else {
                if (View.VISIBLE == status) {
                    noMuchApps.setVisibility(View.GONE);
                }
            }

            gridView.setAdapter(unselectdAppAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AppInfo theAppInfo = (AppInfo) parent.getAdapter().getItem(position);
                    theAppInfo.setChecked(!theAppInfo.isChecked());
                    smoothCheckBox = (SmoothCheckBox) view.findViewById(R.id.ckb_);
                    smoothCheckBox.setChecked(theAppInfo.isChecked(), true);
                }
            });
            final AlertDialog.Builder builder = new AlertDialog.Builder(AppGroupsActivity.this);
            builder.setView(view, 0, 0, 0, 0);
            final AlertDialog alertDialog = builder.show();
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<AppInfo> selectedAppInfos = new ArrayList<AppInfo>();
                    for (AppInfo selectedApp : appInfos) {
                        if (selectedApp.isChecked()) {
                            selectedAppInfos.add(selectedApp);
                        }
                    }
                    if (null != selectedAppInfos && selectedAppInfos.size() > 0) {
                        try {
                            String groupName = groupNameEdit.getText().toString().trim();
                            if (StringUtils.isNoneBlank(groupName)) {
                                addSelectedAppToGroup(selectedAppInfos, groupName);
                            } else {
                                message = Message.obtain();
                                message.what = AppEnum.MSG_GROUP_NAME_IS_NULL.getValue();
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            Log.e(AppConstants.LOG_TAG, "onClick: 保存分组信息异常:{}");
                            e.printStackTrace();
                        }
                    }
                }
            });
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "alertUnselectedAppDialog: 弹出未分组应用选择界、保存分组信息异常:{}");
            e.printStackTrace();
        }
    }

    /**
     * 将已选择的应用添加到分组
     *
     * @param appInfos
     * @param groupName
     */
    private void addSelectedAppToGroup(ArrayList<AppInfo> appInfos, String groupName) throws Exception {
        if (isGroupExist(groupName)) {
            message = Message.obtain();
            message.what = AppEnum.MSG_GROUP_EXIST.getValue();
            handler.sendMessage(message);
        } else {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setId(UUID.randomUUID().toString());
            groupInfo.setGroupName(groupName);
            groupInfo.save();
            for (AppInfo appInfo : appInfos) {
                appInfo.setGroupId(groupInfo.getId());
                appInfo.setGroupName(groupName);
                appInfo.update();
            }
            message = Message.obtain();
            message.what = AppEnum.MSG_GROUP_SAVE.getValue();
            handler.sendMessage(message);
        }

    }

    /**
     * 分组是否存在
     *
     * @param groupName
     * @return
     */
    private Boolean isGroupExist(String groupName) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo = groupInfo.getByParam(groupName);
        if (null != groupInfo) {
            return true;
        }
        return false;
    }

//    @SuppressLint("RestrictedApi")
//    public void alertAddGroupDialog(View view) {
//        final EditText editText = new EditText(this);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(AppConstants.ADD_GROUP_INPUT_GROUP_NAME);
//        builder.setView(editText, 50, 50, 50, 0);
//        builder.setPositiveButton(AppConstants.BTN_DONE, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //按下确定键后的事件
//                Intent intent = new Intent();
//                intent.setClass(AppGroupsActivity.this, NewGroupActivity.class);
//                intent.putExtra("groupName", editText.getText().toString());
//                startActivityForResult(intent, AppEnum.MSG_PUT_EXTRA_ADD_GROUP.getValue());
//            }
//        });
//        builder.setNegativeButton(AppConstants.BTN_BACK, null).show();
//    }

    /**
     * 获取已选择的分组列表
     * @return
     */
    private ArrayList<GroupInfo> getSelectedGroup() {
        ArrayList<GroupInfo> selectedGroupInfos = new ArrayList<GroupInfo>();
        for (GroupInfo selectedGroup : groupInfos) {
            if (selectedGroup.isChecked()) {
                selectedGroupInfos.add(selectedGroup);
            }
        }
        return selectedGroupInfos;
    }

    public void removeGroup(View view) {
        final ArrayList<GroupInfo> selectedGroupInfos = getSelectedGroup();
        if (null != selectedGroupInfos && selectedGroupInfos.size() > 0) {
            final AppInfo appInfo = new AppInfo();
            AlertDialog.Builder builder = new AlertDialog.Builder(AppGroupsActivity.this);
            builder.setTitle("警告");
            builder.setMessage("分组内可能包含应用，继续移除分组将释放分组内应用！主页作为默认分组将得到保留");
            builder.setPositiveButton(AppConstants.BTN_DONE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //按下确定键后的事件
                    ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
                    for (GroupInfo groupInfo : selectedGroupInfos) {
                        if ("home".equals(groupInfo.getGroupName())) {
                            continue;
                        } else {
                            // 根据组id查询其下所有应用信息
                            appInfos = appInfo.listByParam(groupInfo.getId());
                            if (null != appInfos && appInfos.size() > 0) {
                                for (AppInfo theGroupApp : appInfos) {
                                    theGroupApp.setGroupId(null);
                                    theGroupApp.update();
                                }
                            }
                        }
                        groupInfo.remove();
                    }
                    message = Message.obtain();
                    message.what = AppEnum.MSG_GROUP_SAVE.getValue();
                    handler.sendMessage(message);
                }
            });
            builder.setNegativeButton(AppConstants.BTN_BACK, null);
            builder.show();
        }
    }
}
