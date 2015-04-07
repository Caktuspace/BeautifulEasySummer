package com.aloisandco.beautifuleasysummer.Utils.Manager;

import android.view.View;

/**
 * Created by quentinmetzler on 22/03/15.
 */

/**
 * Singleton used to store view which will need to be hidden when transiting from one activity
 * to another so that we don't see the view duplicated when animated
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
