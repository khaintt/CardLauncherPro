package com.leanderli.dev.cardlauncherpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.leanderli.dev.cardlauncherpro.util.AppUtils;

/**
 * 桌面设置
 * @author leanderli
 * @description
 * @date 2018/2/25 00251431
 */

public class AppSettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = AppSettingsActivity.class.getName();

    private CardView titleBarView, homeSettingView, appListSettingView;
    private RelativeLayout homeWallpaperSettingLayout, favAppSettingLayout, iconSettingLayout;

    private RelativeLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            try {
                AppUtils.showIntent(AppSettingsActivity.this, AppPagerActivity.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
        return true;
    }


    private void initView() {
        titleBarView = findViewById(R.id.cv_title_bar);
        homeSettingView = findViewById(R.id.cv_home_setting);
        appListSettingView = findViewById(R.id.cv_app_list_setting);

        homeWallpaperSettingLayout = homeSettingView.findViewById(R.id.rel_home_settings_001);
        favAppSettingLayout = homeSettingView.findViewById(R.id.rel_home_settings_002);
        iconSettingLayout = homeSettingView.findViewById(R.id.rel_home_settings_003);

        backLayout = titleBarView.findViewById(R.id.rel_back);

        homeWallpaperSettingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppUtils.showIntent(AppSettingsActivity.this, HomeSettingActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        favAppSettingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppUtils.showIntent(AppSettingsActivity.this, AppGroupsActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iconSettingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppUtils.showIntent(AppSettingsActivity.this, AppGroupsActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppUtils.showIntent(AppSettingsActivity.this, AppPagerActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
