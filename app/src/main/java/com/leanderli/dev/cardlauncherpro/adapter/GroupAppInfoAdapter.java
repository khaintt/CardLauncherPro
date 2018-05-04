package com.leanderli.dev.cardlauncherpro.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.util.DensityUtils;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/3/25 00250340
 */

public class GroupAppInfoAdapter extends RecyclerView.Adapter<GroupAppInfoAdapter.ViewHolder> {

    private SharedPreferences preferences;

    private ArrayList<AppInfo> mData;
    private static Context context;

    private int type;

    /**
     * 事件回调监听
     */
    private GroupAppInfoAdapter.OnItemClickListener onItemClickListener;

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(GroupAppInfoAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public GroupAppInfoAdapter(ArrayList<AppInfo> data, Context context, int type) {
        this.mData = data;
        this.context = context;
        this.type = type;
    }

    public void updateData(ArrayList<AppInfo> data) {
        mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public GroupAppInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = null;
        if (1 == type) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
        } else if (2 == type) {

        }
        // 实例化viewholder
        GroupAppInfoAdapter.ViewHolder viewHolder = new GroupAppInfoAdapter.ViewHolder(view, type);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GroupAppInfoAdapter.ViewHolder holder, int position) {
        preferences = context.getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, Context.MODE_PRIVATE);
        // 绑定数据
        String appName = mData.get(position).getAppName();
        holder.appName.setText(appName);
        Glide.with(context).load(mData.get(position).getIconPath()).into(holder.appIcon);
        // 点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null) {
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

        public ViewHolder(View itemView, int type) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.tv_app_name);
            appIcon = itemView.findViewById(R.id.iv_app_icon);
            ViewGroup.LayoutParams layoutParams = appIcon.getLayoutParams();
            if (1 == type) {
                layoutParams.width = DensityUtils.dip2px(context, 50f);
                layoutParams.height = DensityUtils.dip2px(context, 50f);
            }
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
