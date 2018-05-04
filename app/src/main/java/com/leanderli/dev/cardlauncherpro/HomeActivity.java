package com.leanderli.dev.cardlauncherpro;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.leanderli.dev.cardlauncherpro.adapter.AppInfoAdapter;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;
import com.leanderli.dev.cardlauncherpro.util.DensityUtils;
import com.leanderli.dev.cardlauncherpro.view.AppSettingsPopupMenu;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * 桌面-主页
 */
public class HomeActivity extends AppCompatActivity {

    private static final String HOME_IMAGE_HEIGHT = "homeImgHeight";
    private static final String HOME_IMAGE_WIDTH = "homeImgWidth";

    private int screenHeight, screenWidth;
    private int statusBarHeight, navigationBarHeight;

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    private ImageView homeWallpaper;
    private CardView homeWallpaperView, favAppView, widgetView;
    private TextView degree, type, cityName;

    private RecyclerView appFavViews;
    private RecyclerView.LayoutManager myLayoutManager;

    private RelativeLayout homeAppsLayout;

    private AppInfoAdapter appInfoAdapter;

    private ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();

    private Message message = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (AppEnum.MSG_NO_FAV_APPS.getValue() == msg.what) {
                Toast.makeText(HomeActivity.this, "你还没有选择常用应用，可以前往设置进行挑选", Toast.LENGTH_SHORT).show();
            } else if (AppEnum.MSG_APP_NOT_FOUND_ERROR.getValue() == msg.what) {
                Toast.makeText(HomeActivity.this, "该应用已被卸载", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        initData();
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            return true;
        }
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            return true;
        }
        return false;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        try {
            // 查询分组信息中为 home 的分组，根据groupId过滤查询出所有的应用
            AppInfo appInfo = new AppInfo();
            GroupInfo groupInfo = new GroupInfo();
            groupInfo = groupInfo.getByParam("home");
            if (null != groupInfo) {
                String groupId = groupInfo.getId();
                appInfos = appInfo.listByParam(groupId);
            }
            if (appInfos.size() == 0) {
                message = Message.obtain();
                message.what = AppEnum.MSG_NO_FAV_APPS.getValue();
                handler.sendMessage(message);
            }

            // 竖直方向的网格样式，每行四个Item
            myLayoutManager = new GridLayoutManager(HomeActivity.this, 5,
                    OrientationHelper.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            appInfoAdapter = new AppInfoAdapter(appInfos, HomeActivity.this);

            appInfoAdapter.setOnItemClickListener(new AppInfoAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ComponentName componentName = new ComponentName(appInfos.get(position).getPackageName(),
                            appInfos.get(position).getActivityName());
                    try {
                        AppUtils.openApp(HomeActivity.this, componentName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onItemLongClick(View view, final int position) {
                    PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.app_long_click_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_app_info:
                                    try {
                                        Uri uri = Uri.parse("package:" + appInfos.get(position).getPackageName());
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case R.id.menu_app_uninstall:
                                    try {

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });

        } catch (Exception e) {
            Log.e(AppConstants.LOG_TAG, "主页初始化数据异常");
            e.printStackTrace();
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        homeWallpaperView = findViewById(R.id.cv_main);
        homeWallpaper = homeWallpaperView.findViewById(R.id.iv_main);
//        favAppView = findViewById(R.id.cv_fav_apps);
        appFavViews = findViewById(R.id.rv_fav_apps);
        homeAppsLayout = findViewById(R.id.rel_home_apps);
        widgetView = findViewById(R.id.cv_widget);
        degree = findViewById(R.id.tv_degree);
        type = findViewById(R.id.tv_type);
        cityName = findViewById(R.id.tv_city_name);

        ViewGroup.LayoutParams layoutParams = homeWallpaperView.getLayoutParams();
        int sizeValue = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_VALUE, 0);
        if (0 == sizeValue) {
            sizeValue = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_DEFAULT, 0);
        }
        layoutParams.height = sizeValue;
        homeWallpaperView.setLayoutParams(layoutParams);

        int wallpaperMode = preferences.getInt(AppConstants.WALLPAPER_MODE, 0);
        if (0 == wallpaperMode) {
            homeWallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String wallpaperPath = preferences.getString(AppConstants.HOME_WALLPAPER_PATH, "");
            if (StringUtils.isNotBlank(wallpaperPath)) {
                Glide.with(HomeActivity.this).load(wallpaperPath).into(homeWallpaper);
            }
        } else if (1 == wallpaperMode) {
            homeWallpaperView.setVisibility(View.INVISIBLE);
        }

//        favAppView.setCardBackgroundColor(preferences.getInt(AppConstants.HOME_BACKGROUND_COLOR, Color.GRAY));

//        widgetView.setCardBackgroundColor(preferences.getInt(AppConstants.HOME_BACKGROUND_COLOR, Color.GRAY));
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GoogleSans-Regular.ttf");
//
//        degree.setTextColor(preferences.getInt(AppConstants.HOME_TEXT_COLOR, Color.WHITE));
//        degree.setTypeface(typeface, Typeface.BOLD);
//        type.setTextColor(preferences.getInt(AppConstants.HOME_TEXT_COLOR, Color.WHITE));
//        cityName.setTextColor(preferences.getInt(AppConstants.HOME_TEXT_COLOR, Color.WHITE));

        appFavViews.setLayoutManager(myLayoutManager);
        appFavViews.setAdapter(appInfoAdapter);
//        appFavViews.addItemDecoration(new RegularLightDarkBoxDecoration(HomeActivity.this));
    }

}
