package com.aloisandco.beautifuleasysummer.utils;

import android.view.View;

/**
 * Created by quentinmetzler on 22/03/15.
 */
public class ActivityTransitionManager {
    private static ActivityTransitionManager ourInstance = new ActivityTransitionManager();

    public static ActivityTransitionManager getInstance() {
        return ourInstance;
    }

    private View menuItemView;

    private ActivityTransitionManager() {
    }

    public View getMenuItemView() {
        return menuItemView;
    }

    public void setMenuItemView(View menuItemView) {
        this.menuItemView = menuItemView;
    }

}
