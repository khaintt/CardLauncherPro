package com.leanderli.dev.cardlauncherpro.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leanderli.dev.cardlauncherpro.MyApp;
import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.util.DensityUtils;

/**
 * Created by lyd10892 on 2016/8/23.
 */

public class AppInfoHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    public ImageView appIcon;
    public TextView appName;
    public RelativeLayout appInfoLayout;

    public AppInfoHolder(View itemView, Context mContext) {
        super(itemView);
        this.mContext = mContext;
        initView();
    }

    private void initView() {
        appInfoLayout = itemView.findViewById(R.id.rel_app_info);
        appIcon = itemView.findViewById(R.id.iv_app_icon);
        appName = itemView.findViewById(R.id.tv_app_name);

        ViewGroup.LayoutParams layoutParams = appIcon.getLayoutParams();
        layoutParams.width = DensityUtils.dip2px(mContext, 50f);
        layoutParams.height = DensityUtils.dip2px(mContext, 50f);

        appName.setTypeface(MyApp.getInstance().getGoogleSansTypeface());
        appIcon.setMaxWidth(layoutParams.width);
        appIcon.setMaxHeight(layoutParams.height);
        appIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }
}
