package com.leanderli.dev.cardlauncherpro.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leanderli.dev.cardlauncherpro.MyApp;
import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.util.DensityUtils;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/3/25 00250340
 * @see com.leanderli.dev.cardlauncherpro.AppListActivity
 * @see com.leanderli.dev.cardlauncherpro.HomeActivity
 */

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {

    private SharedPreferences preferences;

    private ArrayList<AppInfo> mData;
    private static Context context;

    private int textColor;

    /**
     * 事件回调监听
     */
    private AppInfoAdapter.OnItemClickListener onItemClickListener;

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(AppInfoAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public AppInfoAdapter(ArrayList<AppInfo> data, Context context) {
        this.mData = data;
        this.context = context;
        preferences = context.getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, Context.MODE_PRIVATE);
        textColor = preferences.getInt(AppConstants.HOME_TEXT_COLOR, Color.GRAY);
    }

    public void updateData(ArrayList<AppInfo> data) {
        mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public AppInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
        // 实例化viewholder
        AppInfoAdapter.ViewHolder viewHolder = new AppInfoAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AppInfoAdapter.ViewHolder holder, int position) {
        // 绑定数据
        String appName = mData.get(position).getAppName();
        holder.appName.setText(appName);
        holder.appName.setTextColor(textColor);
        Glide.with(context).load(mData.get(position).getIconPath()).into(holder.appIcon);
        // 点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                //表示此事件已经消费，不会触发单击事件
                return true;
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView appName;
        ImageView appIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.tv_app_name);
            appIcon = itemView.findViewById(R.id.iv_app_icon);
            ViewGroup.LayoutParams layoutParams = appIcon.getLayoutParams();
            layoutParams.width = DensityUtils.dip2px(context, 50f);
            layoutParams.height = DensityUtils.dip2px(context, 50f);
            appName.setTypeface(MyApp.getInstance().getGoogleSansTypeface());
            appIcon.setMaxWidth(layoutParams.width);
            appIcon.setMaxHeight(layoutParams.height);
            appIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
