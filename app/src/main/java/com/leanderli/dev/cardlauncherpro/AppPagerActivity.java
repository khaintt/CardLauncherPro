package com.leanderli.dev.cardlauncherpro;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.leanderli.dev.cardlauncherpro.adapter.MyViewPagerAdapter;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.util.AccessUtils;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;
import com.leanderli.dev.cardlauncherpro.util.FileUtils;

import org.zackratos.ultimatebar.UltimateBar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.blurry.Blurry;

/**
 * 桌面-主屏幕分页
 */
public class AppPagerActivity extends Activity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ViewPager myViewPager;
    private RelativeLayout appPagerLayout;
    private ImageView appPagerWallper;

    private MyViewPagerAdapter myViewPagerAdapter;

    private LocalActivityManager manager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == AppEnum.MSG_APP_INITIALIZATION_SUCCESS.getValue()) {
                Toast.makeText(AppPagerActivity.this, "应用初始化完成", Toast.LENGTH_SHORT).show();
                AppUtils.restartApplication(AppPagerActivity.this);
            } else if (msg.what == AppEnum.MSG_APP_INITIALIZATION_FAILED.getValue()) {
                Toast.makeText(AppPagerActivity.this, "应用初始化失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        UltimateBar ultimateBar = new UltimateBar(AppPagerActivity.this);
//        // 透明状态栏导航栏，导航栏不启作用
//        ultimateBar.setImmersionBar();
        setContentView(R.layout.activity_app_pager);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        manager = new LocalActivityManager(AppPagerActivity.this, true);
        manager.dispatchCreate(savedInstanceState);
        initView();
        initViewPager();
        // 优化：同时判断版本号
        Boolean isFirstOpen = preferences.getBoolean(AppConstants.USER_FIRST_OPEN, true);
        // 是第一次打开
        if (isFirstOpen) {
            // 申请到了权限
            if (PackageManager.PERMISSION_GRANTED
                    == AccessUtils.verifyStoragePermissions(AppPagerActivity.this)) {
                // 初始化参数
                editor.putString(AppConstants.HOME_WALLPAPER_SIZE_NAME, AppConstants.WALLPAPER_SIZE_NAME_DEFAULT).commit();
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_VALUE, 0).commit();
                editor.putBoolean(AppConstants.HOME_WALLPAPER_IS_USE_CUSTOMIZE_SIZE, false).commit();
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_CUSTOMIZE, 0).commit();
                editor.putBoolean(AppConstants.HOME_WALLPAPER_IS_IMAGE, true).commit();
                editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, Color.GRAY).commit();
                editor.putInt(AppConstants.HOME_TEXT_COLOR, Color.WHITE).commit();
                editor.putInt(AppConstants.WALLPAPER_MODE, 0).commit();
                // 开启异步任务初始化应用
                AppInitTask appInitTask = new AppInitTask(AppPagerActivity.this);
                appInitTask.execute();
                // 修改标识
                editor.putBoolean(AppConstants.USER_FIRST_OPEN, false).commit();
            } else {
                Toast.makeText(AppPagerActivity.this,
                        "应用初始化失败，你尚未授予该应用权限，重新启动应用后重试。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        appPagerLayout = findViewById(R.id.rel_app_pager);
        myViewPager = findViewById(R.id.vp_main);
        appPagerWallper = findViewById(R.id.iv_pager_wallpaper);

        appPagerWallper.setScaleType(ImageView.ScaleType.CENTER_CROP);

        boolean isUseImage = preferences.getBoolean(AppConstants.HOME_WALLPAPER_IS_IMAGE, false);
        if (isUseImage) {
            String homeWallpaperPath = preferences.getString(AppConstants.HOME_WALLPAPER_PATH, null);
            if (null != homeWallpaperPath) {

/*            Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(homeWallpaperPath));
            appPagerLayout.setBackgroundDrawable(drawable);
            SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    appPagerLayout.setBackgroundDrawable(resource);
                }
            };
            Glide.with(AppPagerActivity.this)
                    .load(homeWallpaperPath).apply(RequestOptions.bitmapTransform(new BlurTransformation(25)))
                    .into(simpleTarget);

            Glide.with(AppPagerActivity.this)
                    .load(homeWallpaperPath)
                    .into(appPagerWallper);*/

                int wallpaperMode = preferences.getInt(AppConstants.WALLPAPER_MODE, 0);
                if (0 == wallpaperMode) {
                    // 普通模式
                    int radius = preferences.getInt(AppConstants.WALLPAER_RADIUS_VALUE, 25);
                    Bitmap bitmap = BitmapFactory.decodeFile(homeWallpaperPath);
                    // from Bitmap
                    Blurry.with(AppPagerActivity.this).radius(radius).from(bitmap).into(appPagerWallper);
                } else if (1 == wallpaperMode) {
                    return;
                }
            }
        } else {
            if (0 != preferences.getInt(AppConstants.HOME_BACKGROUND_COLOR, 0)) {
                appPagerWallper.setBackgroundColor(preferences.getInt(AppConstants.HOME_BACKGROUND_COLOR, 0));
            }
        }
    }

    /**
     * 初始化视图分页
     */
    private void initViewPager() {
        List<View> mListViews = new ArrayList<View>();

        Intent intent = new Intent();
        intent.setClass(AppPagerActivity.this, NewsPageActivity.class);
        intent.putExtra("id", "0");
        mListViews.add(manager.startActivity("Activity0", intent).getDecorView());

        intent.setClass(AppPagerActivity.this, HomeActivity.class);
        intent.putExtra("id", "1");
        mListViews.add(manager.startActivity("Activity1", intent).getDecorView());

        intent.setClass(AppPagerActivity.this, HomeGroupsActivity.class);
        intent.putExtra("id", "2");
        mListViews.add(manager.startActivity("Activity2", intent).getDecorView());

        myViewPagerAdapter = new MyViewPagerAdapter(mListViews);
        myViewPager.setAdapter(myViewPagerAdapter);
        // 设置默认页面，setCurrentItem(int) 需要在setAdapter后面
        myViewPager.setCurrentItem(1);
    }

    /**
     * 初始化应用异步任务
     */
    class AppInitTask extends AsyncTask<String, String, Boolean> {

        private Context context;
        Message message = null;

        AppInitTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                initAppFolder();
                initAppInfo();
                initHomeWallpaperSize();
                message = Message.obtain();
                message.what = AppEnum.MSG_APP_INITIALIZATION_SUCCESS.getValue();
                handler.sendMessage(message);
            } catch (Exception e) {
                Log.e(AppConstants.LOG_TAG, "应用初始化失败:{}");
                e.printStackTrace();
                Message.obtain();
                message.what = AppEnum.MSG_APP_INITIALIZATION_FAILED.getValue();
                handler.sendMessage(message);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean data) {
            super.onPostExecute(data);
        }

        /**
         * 初始化应用主文件夹
         *
         * @throws Exception
         */
        public void initAppFolder() throws Exception {
            try {
                File appFolder = new File(FileUtils.getSDPath() + File.separator + AppConstants.APP_FOLDER_NAME);
                if (!appFolder.exists() && !appFolder.isDirectory()) {
                    appFolder.mkdirs();
                }
                File homeWallpaperFolder = new File(appFolder.getPath() + File.separator + AppConstants.HOME_WALLPAPER_FOLDER);
                if (!homeWallpaperFolder.exists() && !homeWallpaperFolder.isDirectory()) {
                    homeWallpaperFolder.mkdirs();
                }
                File tempFolder = new File(appFolder.getPath() + File.separator + AppConstants.TEMP_FOLDER);
                if (!tempFolder.exists() && !tempFolder.isDirectory()) {
                    tempFolder.mkdirs();
                }
                File iconFolder = new File(appFolder.getPath() + File.separator + AppConstants.ICON_FOLDER);
                if (!iconFolder.exists() && !iconFolder.isDirectory()) {
                    iconFolder.mkdirs();
                }
            } catch (Exception e) {
                Log.e(AppConstants.LOG_TAG, "initAppFolder: 初始化应用文件夹异常:{}");
                e.printStackTrace();
            }
        }

        public void initAppInfo() throws Exception {
            FileOutputStream outputStream = null;
            try {
                List<AppInfo> appInfos = AppUtils.getAllApps(AppPagerActivity.this);
                if (null != appInfos && appInfos.size() > 0) {
                    for (AppInfo appInfo : appInfos) {
                        String id = UUID.randomUUID().toString();
                        String packageName = appInfo.getPackageName();
                        String activityName = appInfo.getActivityName();
                        String appId = packageName.replace(".", "_")
                                + "@" + activityName.replace(".", "_")
                                + "@";
                        String iconName = appId + ".png";
                        String iconPath = FileUtils.getSDPath() + File.separator +
                                AppConstants.APP_FOLDER_NAME + File.separator + AppConstants.ICON_FOLDER +
                                File.separator + iconName;

                        File icon = new File(iconPath);
                        if (!icon.exists() && !icon.isFile()) {
                            outputStream = new FileOutputStream(iconPath);
                            appInfo.getAppIcon().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.flush();
                        }
                        appInfo.setId(id);
                        appInfo.setIconPath(iconPath);

                        String pinyin = "";
                        String appName = appInfo.getAppName();
                        if (!AppUtils.isStartWithWord(appName)) {
                            pinyin = PinyinHelper.convertToPinyinString(appName,"", PinyinFormat.WITHOUT_TONE);
                        } else {
                            pinyin = appName;
                        }
                        appInfo.setPinyin(pinyin.toLowerCase());

                        if (!appInfo.save()) {
                            throw new Exception("AppInfo init failed!");
                        }
                        if ("电话".equals(appInfo.getAppName())
                                || "短信".equals(appInfo.getAppName())
                                || "相机".equals(appInfo.getAppName())
                                || "相册".equals(appInfo.getAppName())) {
                            initDefaultHomeGroup(appInfo);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(AppConstants.LOG_TAG, "初始化应用信息到数据库异常:{}");
                e.printStackTrace();
            } finally {
                if (null != outputStream) {
                    outputStream.close();
                }
            }
        }

        /**
         * 初始化默认应用到默认组
         *
         * @param appInfo
         * @throws Exception
         */
        private void initDefaultHomeGroup(AppInfo appInfo) throws Exception {
            if (!isHomeGroupExist()) {
                // 主屏分组不存在则先新建分组
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupName("home");
                groupInfo.setId(UUID.randomUUID().toString());
                groupInfo.save();
                // 添加应用到默认的主屏分组
                addDefaultAppToDefaultGroup(appInfo, groupInfo);
            } else {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo = groupInfo.getByParam("home");
                // 添加应用到默认的主屏分组
                addDefaultAppToDefaultGroup(appInfo, groupInfo);
            }

        }

        /**
         * 增加默认应用到默认组
         *
         * @param appInfo
         * @param groupInfo
         * @throws Exception
         */
        private void addDefaultAppToDefaultGroup(AppInfo appInfo, GroupInfo groupInfo) throws Exception {
            appInfo.setGroupId(groupInfo.getId());
            appInfo.setGroupName(groupInfo.getGroupName());
            appInfo.update();
        }

        /**
         * 判断默认组是否存在
         *
         * @return
         */
        private boolean isHomeGroupExist() {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo = groupInfo.getByParam("home");
            if (null != groupInfo) {
                return true;
            }
            return false;
        }

        /**
         * 初始化墙纸大小值
         */
        private void initHomeWallpaperSize() {
            if (!isInit()) {
                Resources resources = AppPagerActivity.this.getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();

                int ngvigationBarHeight = AppUtils.getNavigationBarHeight(AppPagerActivity.this);
                int pxHeight = dm.heightPixels + ngvigationBarHeight;

                int defaultSize = pxHeight * 2 / 5;
                int medium = pxHeight * 1 / 2;
                int exlarge = pxHeight * 3 / 5;
                int full = pxHeight;
                Log.i(AppConstants.LOG_TAG, "initHomeWallpaperSize: default:{}" + defaultSize + ",medium:{}" + medium
                        + ",exlarge:{}" + exlarge + ",full:{}" + full);
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_DEFAULT, defaultSize).commit();
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_MEDIUM, medium).commit();
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_EXLARGE, exlarge).commit();
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_FULLSCREEN, full).commit();
            }
        }

        private Boolean isInit() {
            int defaultSize = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_DEFAULT, 0);
            int medium = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_MEDIUM, 0);
            int exlarge = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_EXLARGE, 0);
            int full = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_FULLSCREEN, 0);
            if (defaultSize != 0 && medium != 0 && exlarge != 0 && full != 0) {
                return true;
            }
            return false;
        }
    }
}
