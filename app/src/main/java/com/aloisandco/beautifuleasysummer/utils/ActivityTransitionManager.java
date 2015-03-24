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

    public View getMenuListItemView() {
        return menuListItemView;
    }

    public void setMenuListItemView(View menuListItemView) {
        this.menuListItemView = menuListItemView;
    }

    private View menuListItemView;

    private ActivityTransitionManager() {
    }

    public View getMenuItemView() {
        return menuItemView;
    }

    public void setMenuItemView(View menuItemView) {
        this.menuItemView = menuItemView;
    }

}
