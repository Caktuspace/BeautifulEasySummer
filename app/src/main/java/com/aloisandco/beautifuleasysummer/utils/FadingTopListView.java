package com.aloisandco.beautifuleasysummer.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by quentinmetzler on 23/03/15.
 */
public class FadingTopListView extends ListView {

    public FadingTopListView(Context context) {
        super(context);
    }

    public FadingTopListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingTopListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getTop() < 0) {
                float margin = (float) ScreenUtils.valueToDpi(getResources(), 25);
                if (v.getTop() < -margin) {
                    v.setAlpha(0);
                    v.setFocusable(true);
                } else {
                    v.setAlpha((margin + (float) v.getTop()) / margin);
                    v.setFocusable(true);
                }
            } else {
                v.setAlpha(1);
                v.setFocusable(false);
            }
        }
    }
}
