package com.leanderli.dev.cardlauncherpro.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author leanderli
 * @description
 * @date 2018/3/30 00301735
 */

public class RegularLightDarkBoxDecoration extends RecyclerView.ItemDecoration {

    private Context context;

    public RegularLightDarkBoxDecoration(Context context) {
        this.context = context;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        View childView = null;
        int childPosition = 0;
        boolean isSingleCount = false;
        for (int i = 0; i < childCount; i++) {
            childView = parent.getChildAt(i);
            childPosition = parent.getChildAdapterPosition(childView);
            isSingleCount = childPosition % 2 == 0;
            if (isSingleCount) {
                childView.setBackgroundColor(Color.argb(144, 255, 255, 255));
            } else {
                childView.setBackgroundColor(Color.argb(200, 255, 255, 255));
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}
