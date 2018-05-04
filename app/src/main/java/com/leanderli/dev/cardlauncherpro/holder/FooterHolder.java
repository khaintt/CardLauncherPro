package com.leanderli.dev.cardlauncherpro.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.leanderli.dev.cardlauncherpro.R;


/**
 * @author leanderli
 * @description
 * @date 2018/4/4 00042345
 */

public class FooterHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rootLayout;

    public FooterHolder(View itemView) {
        super(itemView);
        rootLayout = itemView.findViewById(R.id.rel_section_rooter);
    }
}
