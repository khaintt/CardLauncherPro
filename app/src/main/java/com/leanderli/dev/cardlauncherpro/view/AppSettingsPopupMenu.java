package com.leanderli.dev.cardlauncherpro.view;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leanderli.dev.cardlauncherpro.AppSettingsActivity;
import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;

import razerdp.basepopup.BasePopupWindow;

/**
 * @author leanderli
 * @description
 * @date 2018/4/7 00071542
 */

public class AppSettingsPopupMenu extends BasePopupWindow implements View.OnClickListener {

    public AppSettingsPopupMenu(Activity context) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.rel_popup_menu_app_setting).setOnClickListener(this);
        findViewById(R.id.rel_popup_menu_system_setting).setOnClickListener(this);
    }

    @Override
    protected Animation initShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
        //return null;
    }

    @Override
    protected Animation initExitAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
    }

    @Override
    public void showPopupWindow(View v) {
//        setOffsetX((getWidth() - v.getWidth() / 2));
//        setOffsetY(-v.getHeight() / 2);
        super.showPopupWindow(v);
    }

    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.popup_menu);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.rel_popup_menu_container);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_popup_menu_app_setting:
                try {
                    AppUtils.showIntent(getContext(), AppSettingsActivity.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rel_popup_menu_system_setting:
                try {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }

    }
}
