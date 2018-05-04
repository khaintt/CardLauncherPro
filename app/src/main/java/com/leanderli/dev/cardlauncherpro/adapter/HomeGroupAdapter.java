package com.leanderli.dev.cardlauncherpro.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/4/1 00011846
 * @see com.leanderli.dev.cardlauncherpro.HomeGroupListActivity
 */

public class HomeGroupAdapter extends RecyclerView.Adapter<HomeGroupAdapter.ViewHolder> {

    private static Context context;

    private ArrayList<GroupInfo> groupInfos;

    public HomeGroupAdapter(Context context, ArrayList<GroupInfo> groupInfos) {
        this.context = context;
        this.groupInfos = groupInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_group_list_item, parent, false);
        // 实例化viewholder
        HomeGroupAdapter.ViewHolder viewHolder = new HomeGroupAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String groupName = groupInfos.get(position).getGroupName();
        holder.groupName.setText(groupName);
        AppInfo appInfo = new AppInfo();
        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
        if ("home".equals(groupInfos.get(position).getGroupName())) {
            holder.groupAppsView.setVisibility(View.GONE);
        } else if ("others".equals(groupInfos.get(position).getGroupName())) {
            appInfos = appInfo.listByParam(null);
        } else {
           appInfos = appInfo.listByParam(groupInfos.get(position).getId());
        }
        initAppListData(appInfos, holder.appListView);
    }

    private void initAppListData(final ArrayList<AppInfo> appInfos, RecyclerView appListView) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4);
        GroupAppInfoAdapter groupAppInfoAdapter = new GroupAppInfoAdapter(appInfos, context, 1);
        appListView.setLayoutManager(layoutManager);
        appListView.setAdapter(groupAppInfoAdapter);
        groupAppInfoAdapter.setOnItemClickListener(new GroupAppInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ComponentName componentName = new ComponentName(appInfos.get(position).getPackageName(),
                        appInfos.get(position).getActivityName());
                try {
                    AppUtils.openApp(context, componentName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return null == groupInfos ? 0 : groupInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;
        RecyclerView appListView;
        CardView groupAppsView;

        public ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.tv_group_name);
            appListView = itemView.findViewById(R.id.rv_apps);
            groupAppsView = itemView.findViewById(R.id.cv_group_apps_view);
        }
    }
}
