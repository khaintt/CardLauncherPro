package com.leanderli.dev.cardlauncherpro;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leanderli.dev.cardlauncherpro.adapter.HomeGroupInfoAdapter;
import com.leanderli.dev.cardlauncherpro.adapter.SectionedSpanSizeLookup;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.model.HomeGroupInfo;
import com.leanderli.dev.cardlauncherpro.receiver.AppInstallReceiver;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;
import com.leanderli.dev.cardlauncherpro.util.DensityUtils;
import com.leanderli.dev.cardlauncherpro.view.AppSettingsPopupMenu;

import java.util.ArrayList;

/**
 * 桌面-分组列表
 * @author leanderli
 * @description
 * @date 2018/4/5 00051338
 */

public class HomeGroupsActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private RecyclerView mainView;
    private HomeGroupInfoAdapter homeGroupInfoAdapter;

    private TextView mainTitle;
    private RelativeLayout sectionMainTopLayout;
    private RelativeLayout sectionListLayout;

    private AppInstallReceiver appInstallReceiver;

    private Message message = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppInfo appInfo = new AppInfo();
            if (msg.what == AppEnum.ADD_APPS.getValue()) {
                init();
            } else if (msg.what == AppEnum.REMOVE_APPS.getValue()) {
                init();
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_section_main);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, MODE_PRIVATE);

        appInstallReceiver = new AppInstallReceiver(handler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        this.registerReceiver(appInstallReceiver, intentFilter);

        init();
    }

    private void init() {
        sectionMainTopLayout = findViewById(R.id.rel_section_main_top);
        mainTitle = findViewById(R.id.tv_all_groups_title);
        mainView = findViewById(R.id.recyclerView);
        sectionListLayout = findViewById(R.id.rel_sectim_main_list);

        int statusBarHeight = AppUtils.getStatusBarHeight(HomeGroupsActivity.this);
        RelativeLayout.LayoutParams sectionMainTopLayoutParams = new RelativeLayout.LayoutParams(sectionMainTopLayout.getLayoutParams());
        sectionMainTopLayoutParams.setMargins(0, statusBarHeight + DensityUtils.dip2px(HomeGroupsActivity.this, 20), 0, 0);
        sectionMainTopLayout.setLayoutParams(sectionMainTopLayoutParams);

        mainTitle.setTextColor(preferences.getInt(AppConstants.HOME_TEXT_COLOR, Color.GRAY));

        mainTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSettingsPopupMenu appSettingsPopupMenu = new AppSettingsPopupMenu(HomeGroupsActivity.this);
                appSettingsPopupMenu.showPopupWindow(v);
            }
        });

        int navigationBarHeight = AppUtils.getNavigationBarHeight(HomeGroupsActivity.this);
        RelativeLayout.LayoutParams sectionListLayoutParams = new RelativeLayout.LayoutParams(sectionListLayout.getLayoutParams());
        sectionListLayoutParams.setMargins(0, DensityUtils.dip2px(HomeGroupsActivity.this, 10), 0, navigationBarHeight);
        sectionListLayoutParams.addRule(RelativeLayout.BELOW, R.id.rel_section_main_top);
        sectionListLayout.setLayoutParams(sectionListLayoutParams);

        homeGroupInfoAdapter = new HomeGroupInfoAdapter(HomeGroupsActivity.this);
        GridLayoutManager layoutManager = new GridLayoutManager(HomeGroupsActivity.this, 4);
        layoutManager.setSpanSizeLookup(new SectionedSpanSizeLookup(homeGroupInfoAdapter, layoutManager));
        mainView.setLayoutManager(layoutManager);
        mainView.setAdapter(homeGroupInfoAdapter);

        HomeGroupInfo homeGroupInfo = getHomeGroupInfos();
        homeGroupInfoAdapter.setData(homeGroupInfo.singleGroupInfos);

    }

    private HomeGroupInfo getHomeGroupInfos() {
        HomeGroupInfo homeGroupInfo = new HomeGroupInfo();
        HomeGroupInfo.SingleGroupInfo singleGroupInfo = null;
        ArrayList<HomeGroupInfo.SingleGroupInfo> singleGroupInfos = new ArrayList<HomeGroupInfo.SingleGroupInfo>();
        GroupInfo groupInfo = new GroupInfo();
        AppInfo appInfo = new AppInfo();
        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
        ArrayList<GroupInfo> groupInfos = groupInfo.list();
        String groupName = "";
        if (null != groupInfos && groupInfos.size() > 0) {
            for (GroupInfo theGroupInfo : groupInfos) {
                groupName = theGroupInfo.getGroupName();
                // 分组页不显示 home 分组信息
                if ("home".equals(groupName)) {
                    continue;
                }
                appInfos = appInfo.listByParam(theGroupInfo.getId());

                singleGroupInfo = new HomeGroupInfo.SingleGroupInfo();
                singleGroupInfo.setGroupName(groupName);
                singleGroupInfo.setAppInfos(appInfos);
                singleGroupInfos.add(singleGroupInfo);
            }
        }

        // 添加未分组信息
        groupName = "未分组";
        appInfos = appInfo.listByParam(null);

        singleGroupInfo = new HomeGroupInfo.SingleGroupInfo();
        singleGroupInfo.setGroupName(groupName);
        singleGroupInfo.setAppInfos(appInfos);
        singleGroupInfos.add(singleGroupInfo);

        homeGroupInfo.setSingleGroupInfos(singleGroupInfos);
        return homeGroupInfo;
    }

    private ArrayList<AppInfo> addNullData(ArrayList<AppInfo> appInfos) {
        if (appInfos.size() % 4 != 0) {
            AppInfo appInfo = null;
            while (appInfos.size() % 4 != 0) {
                appInfo = new AppInfo();
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != appInstallReceiver) {
            this.unregisterReceiver(appInstallReceiver);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    public void open(View view) {
        try {
            AppUtils.showIntent(this, AppSettingsActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
