package com.leanderli.dev.cardlauncherpro;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.adapter.AppInfoAdapter;
import com.leanderli.dev.cardlauncherpro.adapter.AppListAdapter;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.receiver.AppInstallReceiver;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;

import java.util.ArrayList;

/**
 * 桌面-应用列表
 *
 * @author leanderli
 * @description
 * @date 2018/3/5 00052227
 */

public class AppListActivity extends Activity {

    private SharedPreferences preferences;

    private RecyclerView.LayoutManager myLayoutManager;

    private ArrayList<AppInfo> appInfos;

    private AppInfoAdapter appInfoAdapter;

    private CardView titleBar, searchBar;
    private RelativeLayout main;
    private TextView search;

    private RecyclerView appList;

    private RelativeLayout menuLayout;

    private AppInstallReceiver appInstallReceiver;

    private Message message = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppInfo appInfo = new AppInfo();
            if (msg.what == AppEnum.ADD_APPS.getValue()) {
                appInfos = new ArrayList<>();
                appInfos = appInfo.list();
                appInfoAdapter.updateData(appInfos);
            } else if (msg.what == AppEnum.REMOVE_APPS.getValue()) {
                appInfos = new ArrayList<>();
                appInfos = appInfo.list();
                appInfoAdapter.updateData(appInfos);
            } else if (AppEnum.MSG_APP_NOT_FOUND_ERROR.getValue() == msg.what) {
                Toast.makeText(AppListActivity.this, "该应用已被卸载", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, MODE_PRIVATE);
        appInstallReceiver = new AppInstallReceiver(handler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        this.registerReceiver(appInstallReceiver, intentFilter);
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        try {
            AppInfo appInfo = new AppInfo();
            appInfos = appInfo.list();
            // 竖直方向的网格样式，每行四个Item
            myLayoutManager = new GridLayoutManager(this, 4, OrientationHelper.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            appInfoAdapter = new AppInfoAdapter(appInfos, AppListActivity.this);

            appInfoAdapter.setOnItemClickListener(new AppInfoAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ComponentName componentName = new ComponentName(appInfos.get(position).getPackageName(),
                            appInfos.get(position).getActivityName());
                    try {
                        AppUtils.openApp(AppListActivity.this, componentName);
                    } catch (Exception e) {
                        message = Message.obtain();
                        message.what = AppEnum.MSG_APP_NOT_FOUND_ERROR.getValue();
                        handler.sendMessage(message);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    String pkgName = appInfos.get(position).getPackageName();
//                    AppUtils.uninstallApp(AppListActivity.this, pkgName);
                }
            });
        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "应用列表初始化数据失败");
            e.printStackTrace();
        }
    }

    private void initView() {
        main = findViewById(R.id.rel_main);
        titleBar = findViewById(R.id.cv_title_bar);
        searchBar = findViewById(R.id.cv_search);
        search = findViewById(R.id.tv_search_hint);
        appList = findViewById(R.id.rv_apps);
        menuLayout = findViewById(R.id.rel_menu);

        titleBar.setCardBackgroundColor(preferences.getInt(AppConstants.HOME_BACKGROUND_COLOR, R.color.neteaseRed));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GoogleSans-Regular.ttf");
        search.setTypeface(typeface, Typeface.BOLD);

        appList.setLayoutManager(myLayoutManager);
        appList.setAdapter(appInfoAdapter);

        menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppUtils.showIntent(AppListActivity.this, AppSettingsActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
}
