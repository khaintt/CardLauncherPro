package com.leanderli.dev.cardlauncherpro;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.adapter.HomeGroupAdapter;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.receiver.AppInstallReceiver;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/4/1 00011842
 */

public class HomeGroupListActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private ArrayList<GroupInfo> groupInfos = new ArrayList<GroupInfo>();

    private RecyclerView groupListView;

    private RecyclerView.LayoutManager layoutManager;

    private HomeGroupAdapter homeGroupAdapter;

    private CardView titleBar, searchBar;
    private TextView search;

    private RelativeLayout menuLayout;

    private AppInstallReceiver appInstallReceiver;

    private Message message = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == AppEnum.ADD_APPS.getValue()) {

            } else if (msg.what == AppEnum.REMOVE_APPS.getValue()) {

            } else if (AppEnum.MSG_APP_NOT_FOUND_ERROR.getValue() == msg.what) {

            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, MODE_PRIVATE);
        initData();
        initView();

        appInstallReceiver = new AppInstallReceiver(handler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        this.registerReceiver(appInstallReceiver, intentFilter);
    }

    private void initData() {
        try {
            GroupInfo groupInfo = new GroupInfo();
            groupInfos = groupInfo.list();
            groupInfos.add(new GroupInfo(null, "others", false));

            layoutManager = new LinearLayoutManager(HomeGroupListActivity.this,
                    LinearLayoutManager.VERTICAL, false);

            homeGroupAdapter = new HomeGroupAdapter(HomeGroupListActivity.this, groupInfos);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        titleBar = findViewById(R.id.cv_title_bar);
        searchBar = findViewById(R.id.cv_search);
        search = findViewById(R.id.tv_search_hint);
        groupListView = findViewById(R.id.rv_apps);
        menuLayout = findViewById(R.id.rel_menu);

        titleBar.setCardBackgroundColor(preferences.getInt(AppConstants.HOME_BACKGROUND_COLOR, R.color.neteaseRed));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GoogleSans-Regular.ttf");
        search.setTypeface(typeface, Typeface.BOLD);

        menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppUtils.showIntent(HomeGroupListActivity.this, AppSettingsActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        groupListView.setLayoutManager(layoutManager);
        groupListView.setAdapter(homeGroupAdapter);
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
