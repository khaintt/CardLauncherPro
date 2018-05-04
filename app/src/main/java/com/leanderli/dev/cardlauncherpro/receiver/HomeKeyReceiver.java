package com.leanderli.dev.cardlauncherpro.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.AppPagerActivity;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;

/**
 * @author leanderli
 * @description
 * @date 2018/3/3 00030024
 */

public class HomeKeyReceiver extends BroadcastReceiver {

    final String LOG_TAG = HomeKeyReceiver.class.getName();
    final String SYSTEM_DIALOG_REASON_KEY = "reason";
    final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null) {
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    try {
                        AppUtils.showIntent(context, AppPagerActivity.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    Toast.makeText(context, "多任务键被监听", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
