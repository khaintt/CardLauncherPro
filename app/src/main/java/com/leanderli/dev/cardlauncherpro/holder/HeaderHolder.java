package com.leanderli.dev.cardlauncherpro.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leanderli.dev.cardlauncherpro.MyApp;
import com.leanderli.dev.cardlauncherpro.R;


/**
 * Created by lyd10892 on 2016/8/23.
 */

public class HeaderHolder extends RecyclerView.ViewHolder {

    public RelativeLayout headerLayout;
    public TextView groupName;
    public ImageView dropDirectView;

    public HeaderHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        headerLayout = itemView.findViewById(R.id.rel_section_header);
        groupName = itemView.findViewById(R.id.tv_section_name);
        dropDirectView = itemView.findViewById(R.id.iv_drop_direct);

        groupName.setTypeface(MyApp.getInstance().getGoogleSansTypeface());
    }
}
