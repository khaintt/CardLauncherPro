package com.leanderli.dev.cardlauncherpro.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.leanderli.dev.cardlauncherpro.AppGroupsActivity;
import com.leanderli.dev.cardlauncherpro.HomeActivity;
import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.leanderli.dev.cardlauncherpro.holder.AppInfoHolder;
import com.leanderli.dev.cardlauncherpro.holder.FooterHolder;
import com.leanderli.dev.cardlauncherpro.holder.HeaderHolder;
import com.leanderli.dev.cardlauncherpro.model.AppInfo;
import com.leanderli.dev.cardlauncherpro.model.HomeGroupInfo;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leanderli
 * @description
 * @date 2018/4/5 00051340
 * @see com.leanderli.dev.cardlauncherpro.HomeGroupsActivity
 */

public class HomeGroupInfoAdapter extends SectionedRecyclerViewAdapter<HeaderHolder, AppInfoHolder, FooterHolder> {

    private SharedPreferences preferences;

    public ArrayList<HomeGroupInfo.SingleGroupInfo> sectionDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    private SparseBooleanArray mBooleanMap;

    private int textColor = 0;

    public HomeGroupInfoAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mBooleanMap = new SparseBooleanArray();
        preferences = mContext.getSharedPreferences(AppConstants.PREFERENCE_DATA_NAME, Context.MODE_PRIVATE);
        textColor = preferences.getInt(AppConstants.HOME_TEXT_COLOR, Color.GRAY);
    }

    public void setData(ArrayList<HomeGroupInfo.SingleGroupInfo> sectionDatas) {
        this.sectionDatas = sectionDatas;
        notifyDataSetChanged();
    }

    @Override
    protected int getSectionCount() {
        return isEmpty(sectionDatas) ? 0 : sectionDatas.size();
    }

    public <D> boolean isEmpty(List<D> list) {
        return list == null || list.isEmpty();
    }

    @Override
    protected int getItemCountForSection(int section) {
        int count = sectionDatas.get(section).appInfos.size();
        if (count >= 8 && !mBooleanMap.get(section)) {
            count = 8;
        }
        return isEmpty(sectionDatas.get(section).appInfos) ? 0 : count;
    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    @Override
    protected HeaderHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        return new HeaderHolder(mInflater.inflate(R.layout.a_section_header, parent, false));
    }

    @Override
    protected FooterHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        return new FooterHolder(mInflater.inflate(R.layout.a_section_footer, parent, false));
    }

    @Override
    protected AppInfoHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoHolder(mInflater.inflate(R.layout.a_section_item, parent, false), mContext);
    }

    @Override
    protected void onBindSectionHeaderViewHolder(final HeaderHolder holder, final int section) {
        holder.groupName.setText(sectionDatas.get(section).groupName + " · " + sectionDatas.get(section).appInfos.size());
        holder.groupName.setTextColor(textColor);

        holder.headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOpen = mBooleanMap.get(section);
                mBooleanMap.put(section, !isOpen);
                Drawable drawable = isOpen ?
                        mContext.getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_18dp) :
                        mContext.getResources().getDrawable(R.drawable.ic_arrow_drop_up_white_18dp);
                holder.dropDirectView.setImageDrawable(drawable);
                notifyDataSetChanged();
            }
        });

        holder.dropDirectView.setImageDrawable(mBooleanMap.get(section) ?
                mContext.getResources().getDrawable(R.drawable.ic_arrow_drop_up_white_18dp) :
                mContext.getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_18dp));
    }

    @Override
    protected void onBindSectionFooterViewHolder(FooterHolder holder, int section) {

    }

    @Override
    protected void onBindItemViewHolder(AppInfoHolder holder, final int section, final int position) {
        final AppInfo appInfo = sectionDatas.get(section).appInfos.get(position);
        if (StringUtils.isNotBlank(appInfo.getAppName())) {
            String appName = appInfo.getAppName();
            holder.appName.setText(appName);
            holder.appName.setTextColor(textColor);
            String iconPath = sectionDatas.get(section).appInfos.get(position).getIconPath();
            Glide.with(mContext).load(sectionDatas.get(section).appInfos.get(position).getIconPath()).into(holder.appIcon);
        } else {
            holder.appName.setVisibility(View.INVISIBLE);
            holder.appIcon.setVisibility(View.INVISIBLE);
        }

        holder.appInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = sectionDatas.get(section).appInfos.get(position).getPackageName();
                String className = sectionDatas.get(section).appInfos.get(position).getActivityName();
                if (StringUtils.isNotBlank(packageName) && StringUtils.isNotBlank(className)) {
                    ComponentName componentName = new ComponentName(packageName, className);
                    try {
                        AppUtils.openApp(mContext, componentName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.appInfoLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.app_long_click_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_app_info:
                                try {
                                    Uri uri = Uri.parse("package:" + appInfo.getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                                    mContext.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.menu_app_uninstall:
                                try {

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }
}
