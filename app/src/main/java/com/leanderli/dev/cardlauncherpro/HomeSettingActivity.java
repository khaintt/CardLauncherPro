package com.leanderli.dev.cardlauncherpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;
import com.leanderli.dev.cardlauncherpro.util.FileUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import jp.wasabeef.blurry.Blurry;

/**
 * 桌面设置-主页设置
 */
public class HomeSettingActivity extends AppCompatActivity {

    private final String LOG_TAG = HomeSettingActivity.class.getName();

    private final String HINT_COLOR_SELECTION = "选择此色彩作为桌面背景?";
    private final String HINE_OK_BUTTON = "确定";

    private static final int IMAGE = 1;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private CardView titleBarView, wallpaperPreview, backgroundSelection;
    private ImageView wallpaperSelectView, vibrantColorView, lightVibrantColorView, darkVibrantColorView,
            mutedColorView, lightMutedColorView, darkMutedColorView;

    private TextView vibrantColorText, lightVibrantColorText, darkVibrantColorText, mutedColorText,
            lightMutedColorText, darkMutedColorText;

    private Switch useWallpaperSwitch, wallpaperModeSwitch;

    private SeekBar wallpaperRadiusSeek;

    private RelativeLayout backLayout, wallpaperSizeLayout;

    private AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_setting);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // 头部布局
        titleBarView = findViewById(R.id.cv_title_bar);
        backLayout = titleBarView.findViewById(R.id.rel_back);

        // 使用墙纸作为背景开关
        useWallpaperSwitch = findViewById(R.id.sw_use_wallpaper);
        // 使用全屏幕墙纸
        wallpaperModeSwitch = findViewById(R.id.sw_wallpaper_mode);
        // 墙纸模糊程度调节滑块
        wallpaperRadiusSeek = findViewById(R.id.sk_wallpaper_radius);

        // 墙纸大小选择布局
        wallpaperSizeLayout = findViewById(R.id.rel_wallpaper_size_setting);
        // 墙纸预览布局
        wallpaperPreview = findViewById(R.id.cv_wallpaper_preview);
        // 墙纸选择预览视图
        wallpaperSelectView = wallpaperPreview.findViewById(R.id.iv_wallpaper_preview);

        // 色彩选择布局
        backgroundSelection = findViewById(R.id.cv_background_selection);
        vibrantColorView = backgroundSelection.findViewById(R.id.iv_backgroud_color_vibrant);
        lightVibrantColorView = backgroundSelection.findViewById(R.id.iv_backgroud_color_lightVibrant);
        darkVibrantColorView = backgroundSelection.findViewById(R.id.iv_backgroud_color_darkVibrant);
        mutedColorView = backgroundSelection.findViewById(R.id.iv_backgroud_color_muted);
        lightMutedColorView = backgroundSelection.findViewById(R.id.iv_backgroud_color_lightMuted);
        darkMutedColorView = backgroundSelection.findViewById(R.id.iv_backgroud_color_darkMuted);
        vibrantColorText = backgroundSelection.findViewById(R.id.tv_backgroud_color_vibrant);
        lightVibrantColorText = backgroundSelection.findViewById(R.id.tv_backgroud_color_lightVibrant);
        darkVibrantColorText = backgroundSelection.findViewById(R.id.tv_backgroud_color_darkVibrant);
        mutedColorText = backgroundSelection.findViewById(R.id.tv_backgroud_color_muted);
        lightMutedColorText = backgroundSelection.findViewById(R.id.tv_backgroud_color_lightMuted);
        darkMutedColorText = backgroundSelection.findViewById(R.id.tv_backgroud_color_darkMuted);

        previewHomeWallpaper();
        previewHomeBackgroundColor();

        boolean checkedStatus = preferences.getBoolean(AppConstants.HOME_WALLPAPER_IS_IMAGE, false);
        useWallpaperSwitch.setChecked(checkedStatus);
        useWallpaperSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    editor.putBoolean(AppConstants.HOME_WALLPAPER_IS_IMAGE, true).commit();
                } else {
                    editor.putBoolean(AppConstants.HOME_WALLPAPER_IS_IMAGE, false).commit();
                }
            }
        });

        // 墙纸模式，0-普通模式，1-全屏幕墙纸
        final int wallpaperMode = preferences.getInt(AppConstants.WALLPAPER_MODE, 0);
        if (0 == wallpaperMode) {
            wallpaperModeSwitch.setChecked(false);
        } else if (1 == wallpaperMode) {
            wallpaperModeSwitch.setChecked(true);
        }
        wallpaperModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    editor.putInt(AppConstants.WALLPAPER_MODE, 1).commit();
                } else {
                    editor.putInt(AppConstants.WALLPAPER_MODE, 0).commit();
                }
            }
        });

        int defaultProgress = preferences.getInt(AppConstants.WALLPAER_RADIUS_VALUE, 0);
        wallpaperRadiusSeek.setProgress(defaultProgress);
        wallpaperRadiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String homeWallpaperPath = preferences.getString(AppConstants.HOME_WALLPAPER_PATH, null);
                if (null != homeWallpaperPath) {
                    Bitmap bitmap = BitmapFactory.decodeFile(homeWallpaperPath);
                    // from Bitmap
                    Blurry.with(HomeSettingActivity.this)
                            .radius(seekBar.getProgress())
                            .from(bitmap)
                            .into(wallpaperSelectView);
                    editor.putInt(AppConstants.WALLPAER_RADIUS_VALUE, seekBar.getProgress()).commit();
                }
            }
        });

        wallpaperSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入相册 以下是例子：用不到的api可以不写
                PictureSelector.create(HomeSettingActivity.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(R.style.picture_default_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(true)// 是否裁剪 true or false
                        .compress(true)// 是否压缩 true or false
                        .withAspectRatio(3, 4)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        .compressSavePath(getPath())//压缩图片保存地址
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .scaleEnabled(false)// 裁剪是否可放大缩小图片 true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });

        // 返回
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        wallpaperSizeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppUtils.showIntent(HomeSettingActivity.this, WallpaperSizeSettingActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        vibrantColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(HomeSettingActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(HINT_COLOR_SELECTION);
                builder.setPositiveButton(HINE_OK_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int homeColor = preferences.getInt(AppConstants.HOME_COLOR_VIBRANT, Color.GRAY);
                        int textColor = preferences.getInt(AppConstants.TEXT_COLOR_VIBRANT, Color.WHITE);
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, homeColor).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, textColor).commit();
                    }
                });
                builder.show();
            }
        });

        lightVibrantColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(HomeSettingActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(HINT_COLOR_SELECTION);
                builder.setPositiveButton(HINE_OK_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int homeColor = preferences.getInt(AppConstants.HOME_COLOR_LIGHTVIBRANT, Color.GRAY);
                        int textColor = preferences.getInt(AppConstants.TEXT_COLOR_LIGHTVIBRANT, Color.WHITE);
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, homeColor).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, textColor).commit();
                    }
                });
                builder.show();
            }
        });

        darkVibrantColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(HomeSettingActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(HINT_COLOR_SELECTION);
                builder.setPositiveButton(HINE_OK_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int homeColor = preferences.getInt(AppConstants.HOME_COLOR_DARKVIBRANT, Color.GRAY);
                        int textColor = preferences.getInt(AppConstants.TEXT_COLOR_DARKVIBRANT, Color.WHITE);
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, homeColor).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, textColor).commit();
                    }
                });
                builder.show();
            }
        });

        mutedColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(HomeSettingActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(HINT_COLOR_SELECTION);
                builder.setPositiveButton(HINE_OK_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int homeColor = preferences.getInt(AppConstants.HOME_COLOR_MUTED, Color.GRAY);
                        int textColor = preferences.getInt(AppConstants.TEXT_COLOR_MUTED, Color.WHITE);
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, homeColor).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, textColor).commit();
                    }
                });
                builder.show();
            }
        });

        lightMutedColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(HomeSettingActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(HINT_COLOR_SELECTION);
                builder.setPositiveButton(HINE_OK_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int homeColor = preferences.getInt(AppConstants.HOME_COLOR_LIGHTMUTED, Color.GRAY);
                        int textColor = preferences.getInt(AppConstants.TEXT_COLOR_LIGHTMUTED, Color.WHITE);
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, homeColor).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, textColor).commit();
                    }
                });
                builder.show();
            }
        });

        darkMutedColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(HomeSettingActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(HINT_COLOR_SELECTION);
                builder.setPositiveButton(HINE_OK_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int homeColor = preferences.getInt(AppConstants.HOME_COLOR_DARKMUTED, Color.GRAY);
                        int textColor = preferences.getInt(AppConstants.TEXT_COLOR_DARKMUTED, Color.WHITE);
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, homeColor).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, textColor).commit();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    LocalMedia selectPicture = PictureSelector.obtainMultipleResult(data).get(0);
                    // 例如 LocalMedia 里面返回三种path
//                     1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    editor.putString(AppConstants.SYSTEM_WALLLAPER_PATH, selectPicture.getCutPath()).commit();
                    editor.putString(AppConstants.HOME_WALLPAPER_PATH, selectPicture.getCompressPath()).commit();
                    previewHomeWallpaper();
                    saveHomeBackgroundColor();
                    SetWallpaperTask setWallpaperTask = new SetWallpaperTask();
                    setWallpaperTask.execute();
                    break;
            }
        }
    }

    private void clearWallpaperCache(String filePath) {
        try {
            String fileName = filePath.substring(filePath.lastIndexOf("/"));
            File homeWallpaperFolder = new File(FileUtils.getSDPath() + File.separator +
                    AppConstants.APP_FOLDER_NAME + File.separator + AppConstants.HOME_WALLPAPER_FOLDER);
            if (homeWallpaperFolder.exists() && homeWallpaperFolder.isDirectory()) {
                File[] tempFiles = homeWallpaperFolder.listFiles();
                for (File tempFile : tempFiles) {
                    if (tempFile.isFile() && !tempFile.getName().equals(fileName)) {
                        tempFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化应用时已创建文件夹
     *
     * @return
     */
    public String getPath() {
        try {
            String appFolderPath = FileUtils.getSDPath() + File.separator + AppConstants.APP_FOLDER_NAME +
                    File.separator + AppConstants.HOME_WALLPAPER_FOLDER;
            if (null != appFolderPath) {
                return appFolderPath;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    /**
     * 设置系统壁纸异步任务
     */
    class SetWallpaperTask extends AsyncTask<String, String, Boolean> {

        public SetWallpaperTask() {
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            setSystemWallpaper();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
        }
    }

    /**
     * 设置系统墙纸
     */
    private void setSystemWallpaper() {
        try {
            String wallpaperPath  = preferences.getString(AppConstants.SYSTEM_WALLLAPER_PATH, null);
            Bitmap bitmap = BitmapFactory.decodeFile(wallpaperPath);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(HomeSettingActivity.this);
            wallpaperManager.setBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置预览墙纸
     */
    private void previewHomeWallpaper() {
        wallpaperSelectView.setImageBitmap(BitmapFactory.decodeFile(preferences.getString(AppConstants.HOME_WALLPAPER_PATH, "")));
    }

    /**
     * 预览根据墙纸适配的颜色
     */
    private void previewHomeBackgroundColor() {
        int vibrantColor = preferences.getInt(AppConstants.HOME_COLOR_VIBRANT, 0);
        int vibrantText = preferences.getInt(AppConstants.TEXT_COLOR_VIBRANT, 0);
        if (0 != vibrantColor) {
            vibrantColorView.setBackgroundColor(vibrantColor);
        }
        if (0 != vibrantText) {
            vibrantColorText.setTextColor(vibrantText);
        }
        int lightVibrantColor = preferences.getInt(AppConstants.HOME_COLOR_LIGHTVIBRANT, 0);
        int lightVibrantText = preferences.getInt(AppConstants.TEXT_COLOR_LIGHTVIBRANT, 0);
        if (0 != lightVibrantColor) {
            lightVibrantColorView.setBackgroundColor(lightVibrantColor);
        }
        if (0 != lightVibrantText) {
            lightVibrantColorText.setTextColor(lightVibrantText);
        }
        int darkVibrantColor = preferences.getInt(AppConstants.HOME_COLOR_DARKVIBRANT, 0);
        int darkVibrantText = preferences.getInt(AppConstants.TEXT_COLOR_DARKVIBRANT, 0);
        if (0 != darkVibrantColor) {
            darkVibrantColorView.setBackgroundColor(darkVibrantColor);
        }
        if (0 != darkVibrantText) {
            darkVibrantColorText.setTextColor(darkVibrantText);
        }

        int mutedColor = preferences.getInt(AppConstants.HOME_COLOR_MUTED, 0);
        int mutedText = preferences.getInt(AppConstants.TEXT_COLOR_MUTED, 0);
        if (0 != mutedColor) {
            mutedColorView.setBackgroundColor(mutedColor);
        }
        if (0 != mutedText) {
            mutedColorText.setTextColor(mutedText);
        }
        int lightMutedColor = preferences.getInt(AppConstants.HOME_COLOR_LIGHTMUTED, 0);
        int lightMutedText = preferences.getInt(AppConstants.TEXT_COLOR_LIGHTMUTED, 0);
        if (0 != lightMutedColor) {
            lightMutedColorView.setBackgroundColor(lightMutedColor);
        }
        if (0 != lightMutedText) {
            lightMutedColorText.setTextColor(lightMutedText);
        }
        int drakMutedColor = preferences.getInt(AppConstants.HOME_COLOR_DARKMUTED, 0);
        int drakMutedText = preferences.getInt(AppConstants.TEXT_COLOR_DARKMUTED, 0);
        if (0 != drakMutedColor) {
            darkMutedColorView.setBackgroundColor(drakMutedColor);
        }
        if (0 != drakMutedText) {
            darkMutedColorText.setTextColor(drakMutedText);
        }
    }

    /**
     * 设置根据墙纸适配的颜色
     */
    private void saveHomeBackgroundColor() {
        Bitmap bitmap = BitmapFactory.decodeFile(preferences.getString(AppConstants.HOME_WALLPAPER_PATH, ""));
        if (null != bitmap) {
            Palette.Builder builder = Palette.from(bitmap);
            builder.generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    // 获取有活力的颜色样本
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    // 获取有活力 亮色的样本
                    Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
                    // 获取有活力 暗色的样本
                    Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                    // 获取柔和的颜色样本
                    Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                    // 获取柔和 亮色的样本
                    Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
                    // 获取柔和 暗色的样本
                    Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();

                    if (null != vibrantSwatch) {
                        int homeColor = vibrantSwatch.getRgb();
                        int textColor = vibrantSwatch.getBodyTextColor();
                        vibrantColorView.setBackgroundColor(homeColor);
                        vibrantColorText.setTextColor(textColor);
                        editor.putInt(AppConstants.HOME_COLOR_VIBRANT, homeColor).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_VIBRANT, textColor).commit();
                    } else {
                        vibrantColorView.setBackgroundColor(Color.GRAY);
                        vibrantColorText.setTextColor(Color.WHITE);
                        editor.putInt(AppConstants.HOME_COLOR_VIBRANT, Color.GRAY).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_VIBRANT, Color.WHITE).commit();
                    }
                    if (null != lightVibrantSwatch) {
                        int homeColor = lightVibrantSwatch.getRgb();
                        int textColor = lightVibrantSwatch.getBodyTextColor();
                        lightVibrantColorView.setBackgroundColor(homeColor);
                        lightVibrantColorText.setTextColor(textColor);
                        editor.putInt(AppConstants.HOME_COLOR_LIGHTVIBRANT, homeColor).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_LIGHTVIBRANT, textColor).commit();
                    } else {
                        lightVibrantColorView.setBackgroundColor(Color.GRAY);
                        lightVibrantColorText.setTextColor(Color.WHITE);
                        editor.putInt(AppConstants.HOME_COLOR_LIGHTVIBRANT, Color.GRAY).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_LIGHTVIBRANT, Color.WHITE).commit();
                    }
                    if (null != darkVibrantSwatch) {
                        int homeColor = darkVibrantSwatch.getRgb();
                        int textColor = darkVibrantSwatch.getBodyTextColor();
                        darkVibrantColorView.setBackgroundColor(homeColor);
                        darkVibrantColorText.setTextColor(textColor);
                        editor.putInt(AppConstants.HOME_COLOR_DARKVIBRANT, homeColor).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_DARKVIBRANT, textColor).commit();
                    } else {
                        darkVibrantColorView.setBackgroundColor(Color.GRAY);
                        darkVibrantColorText.setTextColor(Color.WHITE);
                        editor.putInt(AppConstants.HOME_COLOR_DARKVIBRANT, Color.GRAY).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_DARKVIBRANT, Color.WHITE).commit();
                    }
                    if (null != mutedSwatch) {
                        int homeColor = mutedSwatch.getRgb();
                        int textColor = mutedSwatch.getBodyTextColor();
                        mutedColorView.setBackgroundColor(homeColor);
                        mutedColorText.setTextColor(textColor);
                        editor.putInt(AppConstants.HOME_COLOR_MUTED, homeColor).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_MUTED, textColor).commit();
                    } else {
                        mutedColorView.setBackgroundColor(Color.GRAY);
                        mutedColorText.setTextColor(Color.WHITE);
                        editor.putInt(AppConstants.HOME_COLOR_MUTED, Color.GRAY).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_MUTED, Color.WHITE).commit();
                    }
                    if (null != lightMutedSwatch) {
                        int homeColor = lightMutedSwatch.getRgb();
                        int textColor = lightMutedSwatch.getBodyTextColor();
                        lightMutedColorView.setBackgroundColor(homeColor);
                        lightMutedColorText.setTextColor(textColor);
                        editor.putInt(AppConstants.HOME_COLOR_LIGHTMUTED, homeColor).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_LIGHTMUTED, textColor).commit();
                    } else {
                        lightMutedColorView.setBackgroundColor(Color.GRAY);
                        lightMutedColorText.setTextColor(Color.WHITE);
                        editor.putInt(AppConstants.HOME_COLOR_LIGHTMUTED, Color.GRAY).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_LIGHTMUTED, Color.WHITE).commit();
                    }
                    if (null != darkMutedSwatch) {
                        int homeColor = darkMutedSwatch.getRgb();
                        int textColor = darkMutedSwatch.getBodyTextColor();
                        darkMutedColorView.setBackgroundColor(homeColor);
                        darkMutedColorText.setTextColor(textColor);
                        editor.putInt(AppConstants.HOME_COLOR_DARKMUTED, homeColor).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_DARKMUTED, textColor).commit();
                    } else {
                        darkMutedColorView.setBackgroundColor(Color.GRAY);
                        darkMutedColorText.setTextColor(Color.WHITE);
                        editor.putInt(AppConstants.HOME_COLOR_DARKMUTED, Color.GRAY).commit();
                        editor.putInt(AppConstants.TEXT_COLOR_DARKMUTED, Color.WHITE).commit();
                    }

                    // 设置默认色彩
                    if (null != lightVibrantSwatch) {
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, lightVibrantSwatch.getRgb()).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, lightVibrantSwatch.getBodyTextColor()).commit();
                    } else if (null != lightMutedSwatch) {
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, lightMutedSwatch.getRgb()).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, lightMutedSwatch.getBodyTextColor()).commit();
                    } else {
                        editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, Color.GRAY).commit();
                        editor.putInt(AppConstants.HOME_TEXT_COLOR, Color.WHITE).commit();
                    }
                }
            });
        } else {
            editor.putInt(AppConstants.HOME_BACKGROUND_COLOR, Color.GRAY).commit();
            editor.putInt(AppConstants.HOME_TEXT_COLOR, Color.WHITE).commit();
        }
    }
}
