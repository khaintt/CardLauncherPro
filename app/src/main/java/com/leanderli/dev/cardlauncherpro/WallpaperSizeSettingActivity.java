package com.leanderli.dev.cardlauncherpro;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.common.AppConstants;

import org.apache.commons.lang3.StringUtils;

/**
 * 桌面设置-主页壁纸大小设置
 */
public class WallpaperSizeSettingActivity extends AppCompatActivity {

    private RelativeLayout backLayout, saveLayout, disabledHintLayout, defaultLayout, mediumLayout, exlargeLayout, fullLayout;
    private ImageView defaultImage, mediumImage, exlargeImage, fullscreenImage;
    private TextView deviceSizeHint;
    private Switch useCustomizeSize;
    private EditText customizeSizeEt;
    private CardView wallpaperSizeView;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private int defaultItem = 0;
    private int mediumItem = 1;
    private int exlargetItem = 2;
    private int fullItem = 3;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_size_setting);
        preferences = getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        initView();
        initData();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        // 返回布局
        backLayout = findViewById(R.id.rel_back);
        // 保存按钮布局
        saveLayout = findViewById(R.id.rel_save_size);
        // 默认大小选择按钮布局
        defaultLayout = findViewById(R.id.rel_wallpaper_size_01);
        // 中等大小选择按钮布局
        mediumLayout = findViewById(R.id.rel_wallpaper_size_02);
        // 更大选择按钮布局
        exlargeLayout = findViewById(R.id.rel_wallpaper_size_03);
        // 全屏选择按钮布局
        fullLayout = findViewById(R.id.rel_wallpaper_size_04);
        // 设备可用高度提示
        deviceSizeHint = findViewById(R.id.tv_device_size_hint);
        // 使用自定义大小开关
        useCustomizeSize = findViewById(R.id.sv_use_customize_size);
        // 自定义大小输入框
        customizeSizeEt = findViewById(R.id.et_customize_size);
        // 禁用显示布局
        disabledHintLayout = findViewById(R.id.rel_disabled_hint);
        // 墙纸大小选择功能区域布局
        wallpaperSizeView = findViewById(R.id.cv_wallpaper_size);

        defaultImage = findViewById(R.id.iv_default);
        mediumImage = findViewById(R.id.iv_medium);
        exlargeImage = findViewById(R.id.iv_exlarge);
        fullscreenImage = findViewById(R.id.iv_fullscreen);

        previewSizeInfo();

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        defaultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultImage.setVisibility(View.VISIBLE);
                editor.putString(AppConstants.HOME_WALLPAPER_SIZE_NAME, AppConstants.WALLPAPER_SIZE_NAME_DEFAULT).commit();
                int defaultSizeValue = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_DEFAULT, 0);
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_VALUE, defaultSizeValue).commit();
                updateSelectedItem(defaultItem);
            }
        });

        mediumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediumImage.setVisibility(View.VISIBLE);
                editor.putString(AppConstants.HOME_WALLPAPER_SIZE_NAME, AppConstants.WALLPAPER_SZIE_NAME_MEDIUM).commit();
                int mediumSizeValue = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_MEDIUM, 0);
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_VALUE, mediumSizeValue).commit();
                updateSelectedItem(mediumItem);
            }
        });

        exlargeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exlargeImage.setVisibility(View.VISIBLE);
                editor.putString(AppConstants.HOME_WALLPAPER_SIZE_NAME, AppConstants.WALLPAPER_SIZE_NAME_EXLARGE).commit();
                int exlargetSizeValue = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_EXLARGE, 0);
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_VALUE, exlargetSizeValue).commit();
                updateSelectedItem(exlargetItem);
            }
        });

        fullLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullscreenImage.setVisibility(View.VISIBLE);
                editor.putString(AppConstants.HOME_WALLPAPER_SIZE_NAME, AppConstants.WALLPAPER_SIZE_NAME_FULL).commit();
                int fullSizeValue = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_FULLSCREEN, 0);
                editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_VALUE, fullSizeValue).commit();
                updateSelectedItem(fullItem);
            }
        });

        final int deviceHeight = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_FULLSCREEN, 0);
        deviceSizeHint.setText("此设备屏幕可用高度为 " + deviceHeight + " 像素，你可以输入在此范围之间的数值");

        int initCustomizeSize = preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_CUSTOMIZE, 0);
        if (0 != initCustomizeSize) {
            customizeSizeEt.setText(String.valueOf(initCustomizeSize));
        }
        customizeSizeEt.setHint("0~" + deviceHeight);
        customizeSizeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveLayout.setVisibility(View.VISIBLE);
            }
        });

        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customizeSizeStr = customizeSizeEt.getText().toString();
                if (StringUtils.isNotBlank(customizeSizeStr)) {
                    int customizeSize = Integer.parseInt(customizeSizeStr);
                    if (customizeSize == 0 || customizeSize > deviceHeight) {
                        Toast.makeText(WallpaperSizeSettingActivity.this, "输入大小可能会造成视觉不平衡，建议重新输入合理值", Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_CUSTOMIZE, customizeSize).commit();
                        Toast.makeText(WallpaperSizeSettingActivity.this, "自定义大小已保存，打开开关即可使用", Toast.LENGTH_SHORT).show();
                    }
                }
                saveLayout.setVisibility(View.INVISIBLE);
            }
        });

        boolean checkStatus = preferences.getBoolean(AppConstants.HOME_WALLPAPER_IS_USE_CUSTOMIZE_SIZE, false);
        useCustomizeSize.setChecked(checkStatus);
        if (checkStatus) {
            disabledHintLayout.setVisibility(View.VISIBLE);
            defaultLayout.setClickable(false);
            mediumLayout.setClickable(false);
            exlargeLayout.setClickable(false);
            fullLayout.setClickable(false);
            wallpaperSizeView.setForeground(getDrawable(R.drawable.color_disabled_status));
        }
        useCustomizeSize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    disabledHintLayout.setVisibility(View.VISIBLE);
                    wallpaperSizeView.setForeground(getDrawable(R.drawable.color_disabled_status));
                    defaultLayout.setClickable(false);
                    mediumLayout.setClickable(false);
                    exlargeLayout.setClickable(false);
                    fullLayout.setClickable(false);
                    editor.putInt(AppConstants.HOME_WALLPAPER_SIZE_VALUE,
                            preferences.getInt(AppConstants.HOME_WALLPAPER_SIZE_CUSTOMIZE, 0)).commit();
                    editor.putBoolean(AppConstants.HOME_WALLPAPER_IS_USE_CUSTOMIZE_SIZE, true).commit();
                } else {
                    disabledHintLayout.setVisibility(View.INVISIBLE);
                    wallpaperSizeView.setForeground(null);
                    defaultLayout.setClickable(true);
                    mediumLayout.setClickable(true);
                    exlargeLayout.setClickable(true);
                    fullLayout.setClickable(true);
                    editor.putBoolean(AppConstants.HOME_WALLPAPER_IS_USE_CUSTOMIZE_SIZE, false).commit();
                }
            }
        });



    }

    private void initData() {


    }

    private void previewSizeInfo() {
        String sizeName = preferences.getString(AppConstants.HOME_WALLPAPER_SIZE_NAME, null);
        if (null != sizeName) {
            if (AppConstants.WALLPAPER_SIZE_NAME_DEFAULT.equals(sizeName)) {
                defaultImage.setVisibility(View.VISIBLE);
            } else if (AppConstants.WALLPAPER_SZIE_NAME_MEDIUM.equals(sizeName)) {
                mediumImage.setVisibility(View.VISIBLE);
            } else if (AppConstants.WALLPAPER_SIZE_NAME_EXLARGE.equals(sizeName)) {
                exlargeImage.setVisibility(View.VISIBLE);
            } else if (AppConstants.WALLPAPER_SIZE_NAME_FULL.equals(sizeName)) {
                fullscreenImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateSelectedItem(int item) {
        if (defaultItem == item) {
            if (mediumImage.getVisibility() == View.VISIBLE) {
                mediumImage.setVisibility(View.INVISIBLE);
            }
            if (exlargeImage.getVisibility() == View.VISIBLE) {
                exlargeImage.setVisibility(View.INVISIBLE);
            }
            if (fullscreenImage.getVisibility() == View.VISIBLE) {
                fullscreenImage.setVisibility(View.INVISIBLE);
            }
        } else if (mediumItem == item) {
            if (defaultImage.getVisibility() == View.VISIBLE) {
                defaultImage.setVisibility(View.INVISIBLE);
            }
            if (exlargeImage.getVisibility() == View.VISIBLE) {
                exlargeImage.setVisibility(View.INVISIBLE);
            }
            if (fullscreenImage.getVisibility() == View.VISIBLE) {
                fullscreenImage.setVisibility(View.INVISIBLE);
            }
        } else if (exlargetItem == item) {
            if (defaultImage.getVisibility() == View.VISIBLE) {
                defaultImage.setVisibility(View.INVISIBLE);
            }
            if (mediumImage.getVisibility() == View.VISIBLE) {
                mediumImage.setVisibility(View.INVISIBLE);
            }
            if (fullscreenImage.getVisibility() == View.VISIBLE) {
                fullscreenImage.setVisibility(View.INVISIBLE);
            }
        } else {
            if (defaultImage.getVisibility() == View.VISIBLE) {
                defaultImage.setVisibility(View.INVISIBLE);
            }
            if (mediumImage.getVisibility() == View.VISIBLE) {
                mediumImage.setVisibility(View.INVISIBLE);
            }
            if (exlargeImage.getVisibility() == View.VISIBLE) {
                exlargeImage.setVisibility(View.INVISIBLE);
            }
        }
    }
}
