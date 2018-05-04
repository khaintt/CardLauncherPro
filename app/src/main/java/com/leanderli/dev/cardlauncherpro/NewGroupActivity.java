package com.leanderli.dev.cardlauncherpro;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.adapter.UnselectdAppAdapter;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.view.SmoothCheckBox;

import java.util.ArrayList;

/**
 * 桌面设置-单个分组详细
 * @author leanderli
 * @description
 * @date 2018/3/25 00250123
 */

public class NewGroupActivity extends AppCompatActivity {

    /**
     * 上层视图传来的分组Id
     */
    private String parentGroupId;
    /**
     * 上层视图传来的分组名
     */
    private String parentGroupName;

    private RelativeLayout backLayout;

    private TextView groupName;

    private GridView groupApps;

    private SmoothCheckBox smoothCheckBox;

    private UnselectdAppAdapter unselectdAppAdapter;

    private ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();

    private Message message = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (AppEnum.MSG_GROUP_SAVE.getValue() == msg.what) {
                Toast.makeText(NewGroupActivity.this, "分组信息已保存", Toast.LENGTH_SHORT).show();
                AppInfo appInfo = new AppInfo();
                appInfos = appInfo.listByParam(parentGroupId);
                unselectdAppAdapter.updateData(appInfos);
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_info);
        getExtra();
        initView();
        initData();
    }

    private void getExtra() {
        Intent intent = getIntent();
        String[] groupInfoArr = intent.getStringArrayExtra("groupInfoArr");
        parentGroupId = groupInfoArr[0];
        parentGroupName = groupInfoArr[1];
    }

    private void initView() {
        backLayout = findViewById(R.id.rel_back);
        groupName = findViewById(R.id.tv_new_group_name);
        groupApps = findViewById(R.id.gv_apps);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        groupName.setText(parentGroupName);
    }

    private void initData() {
        try {
            AppInfo appInfo = new AppInfo();
            appInfos = appInfo.listByParam(parentGroupId);
            unselectdAppAdapter = new UnselectdAppAdapter(NewGroupActivity.this, appInfos);
            groupApps.setAdapter(unselectdAppAdapter);
            groupApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AppInfo selectedApp = (AppInfo) parent.getAdapter().getItem(position);
                    selectedApp.setChecked(!selectedApp.isChecked());
                    smoothCheckBox = (SmoothCheckBox) view.findViewById(R.id.ckb_);
                    smoothCheckBox.setChecked(selectedApp.isChecked(), true);
                }
            });

        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "initData: 初始化分组内应用信息异常:{}");
            e.printStackTrace();
        }
    }


    public void moveApp(View view) {
        Toast.makeText(NewGroupActivity.this, "请耐心等待下一个版本升级。+1s", Toast.LENGTH_SHORT).show();
    }

    public void addApp(View view) {
        alertUnselectedAppDialogAndSaveGroup();
    }

    /**
     * 从分组中移除应用
     * @param view
     */
    public void removeApp(View view) {
        ArrayList<AppInfo> selectedAppInfos = getSelectedApp();
        if (null != selectedAppInfos && selectedAppInfos.size() > 0) {
            for (AppInfo appInfo : selectedAppInfos) {
                appInfo.setGroupId(null);
                appInfo.update();
            }
            message = Message.obtain();
            message.what = AppEnum.MSG_GROUP_SAVE.getValue();
            handler.sendMessage(message);
        }
    }

    /**
     * 获取已选择的App
     *
     * @return
     */
    private ArrayList<AppInfo> getSelectedApp() {
        ArrayList<AppInfo> selectedApps = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfos) {
            if (appInfo.isChecked()) {
                selectedApps.add(appInfo);
            }
        }
        return selectedApps;
    }

    /**
     * 弹出待分组应用对话框
     */
    @SuppressLint("RestrictedApi")
    private void alertUnselectedAppDialogAndSaveGroup() {
        try {
            // 查询所有未分组应用
            AppInfo appInfo = new AppInfo();
            final ArrayList<AppInfo> appInfos = appInfo.listByParam(null);
            final View view = View.inflate(NewGroupActivity.this, R.layout.unselected_app_list, null);
            UnselectdAppAdapter unselectdAppAdapter = new UnselectdAppAdapter(NewGroupActivity.this, appInfos);
            TextView noMuchApps = view.findViewById(R.id.tv_no_apps);
            GridView gridView = view.findViewById(R.id.gv_unselected_app);
            Button positiveButton = view.findViewById(R.id.btn_done);
            Button negativeButton = view.findViewById(R.id.btn_dismiss);
            final EditText groupNameEdit = view.findViewById(R.id.et_group_name);
            // 将分组名绑定到输入框，并禁用输入框
            groupNameEdit.setText(parentGroupName);
            groupNameEdit.setFocusable(false);

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
            final AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupActivity.this);
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
                            addSelectedAppToGroup(selectedAppInfos);
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
     */
    private void addSelectedAppToGroup(ArrayList<AppInfo> appInfos) throws Exception {
        for (AppInfo appInfo : appInfos) {
            appInfo.setGroupId(parentGroupId);
            appInfo.setGroupName(parentGroupName);
            appInfo.update();
        }
        message = Message.obtain();
        message.what = AppEnum.MSG_GROUP_SAVE.getValue();
        handler.sendMessage(message);
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
}
