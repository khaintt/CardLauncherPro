package com.leanderli.dev.cardlauncherpro.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leanderli.dev.cardlauncherpro.R;
import com.leanderli.dev.cardlauncherpro.model.GroupInfo;
import com.leanderli.dev.cardlauncherpro.view.SmoothCheckBox;

import java.util.ArrayList;

/**
 * @author leanderli
 * @description
 * @date 2018/3/23 00232225
 * @see com.leanderli.dev.cardlauncherpro.AppGroupsActivity
 */

public class AppGroupAdapter extends BaseAdapter {

    private ArrayList<GroupInfo> groupInfos;

    private static Context context;

    public AppGroupAdapter(Context context, ArrayList<GroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
        this.context = context;
    }

    public void updateData(ArrayList<GroupInfo> data) {
        groupInfos.clear();
        this.groupInfos.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == groupInfos ? 0 : groupInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return groupInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.group_list_item, null);
            holder.groupName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.smoothCheckBox = (SmoothCheckBox) convertView.findViewById(R.id.ckb_);
            holder.groupIcon = convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GroupInfo groupInfo = groupInfos.get(position);
        holder.smoothCheckBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                groupInfo.setChecked(isChecked);
            }
        });
        holder.groupName.setText(groupInfo.getGroupName());
        holder.smoothCheckBox.setChecked(groupInfo.isChecked());

        return convertView;
    }

    class ViewHolder {
        SmoothCheckBox smoothCheckBox;
        TextView groupName;
        ImageView groupIcon;
    }

}
