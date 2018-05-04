package com.leanderli.dev.cardlauncherpro;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.common.AppEnum;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.model.HomeApp;
import com.leanderli.dev.cardlauncherpro.receiver.HomeKeyReceiver;
import com.leanderli.dev.cardlauncherpro.util.AccessUtils;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;
import com.leanderli.dev.cardlauncherpro.util.FileUtils;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.Group;
import java.util.List;
import java.util.UUID;

/**
 * @author leanderli
 * @description
 * @date 2018/3/6 00062056
 */

public class MyApp extends Application {

    private Typeface googleSansTypeface;
    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(MyApp.this);

        instance = (MyApp) getApplicationContext();
        googleSansTypeface = Typeface.createFromAsset(getAssets(), "fonts/GoogleSans-Regular.ttf");
    }

    public Typeface getGoogleSansTypeface() {
        return googleSansTypeface;
    }

    public void setGoogleSansTypeface(Typeface googleSansTypeface) {
        this.googleSansTypeface = googleSansTypeface;
    }

    public static MyApp getInstance() {
        return instance;
    }

    public static void setInstance(MyApp instance) {
        MyApp.instance = instance;
    }
}
