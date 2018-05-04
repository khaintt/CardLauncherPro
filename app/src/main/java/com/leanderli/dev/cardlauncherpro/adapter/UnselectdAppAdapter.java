package com.leanderli.dev.cardlauncherpro.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.util.DensityUtils;
import com.leanderli.dev.cardlauncherpro.view.SmoothCheckBox;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/3/25 00250502
 * @see com.leanderli.dev.cardlauncherpro.NewGroupActivity
 */

public class UnselectdAppAdapter extends BaseAdapter {

    private ArrayList<AppInfo> appInfos;

    private static Context context;

    public UnselectdAppAdapter(Context context, ArrayList<AppInfo> appInfos) {
        this.appInfos = appInfos;
        this.context = context;
    }

    public void updateData(ArrayList<AppInfo> data) {
        appInfos.clear();
        this.appInfos.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == appInfos ? 0 : appInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UnselectdAppAdapter.ViewHolder holder;
        ViewGroup.LayoutParams layoutParams;
        if (convertView == null) {
            holder = new UnselectdAppAdapter.ViewHolder();
            convertView = View.inflate(context, R.layout.unselected_app_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.smoothCheckBox = (SmoothCheckBox) convertView.findViewById(R.id.ckb_);
            holder.icon = convertView.findViewById(R.id.iv_icon);

            layoutParams = holder.icon.getLayoutParams();
            layoutParams.width = DensityUtils.dip2px(context, 50f);
            layoutParams.height = DensityUtils.dip2px(context, 50f);
            holder.icon.setMaxWidth(layoutParams.width);
            holder.icon.setMaxHeight(layoutParams.height);
            holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);

            convertView.setTag(holder);
        } else {
            holder = (UnselectdAppAdapter.ViewHolder) convertView.getTag();
        }

        final AppInfo appInfo = appInfos.get(position);
        holder.smoothCheckBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                appInfo.setChecked(isChecked);
            }
        });
        String appName = appInfos.get(position).getAppName();
//        if (appName.length() >= 10) {
//            appName = appName.substring(0, 10) + "..";
//        }
        holder.name.setText(appName);
        Glide.with(context).load(appInfos.get(position).getIconPath()).into(holder.icon);
        holder.smoothCheckBox.setChecked(appInfo.isChecked());
        return convertView;
    }

    class ViewHolder {
        SmoothCheckBox smoothCheckBox;
        TextView name;
        ImageView icon;
    }

}
