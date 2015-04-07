package com.aloisandco.beautifuleasysummer.Utils;

import android.animation.TimeInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by quentinmetzler on 30/03/15.
 */
public class Constants {

    // Notification name to notify that there is no more favorite in the list
    public static final String NO_MORE_FAVORITES = "no_more_favorites";
    // Name where the favorites are stored in the user preferences
    public static final String SHARED_PREFERENCES_FAVORITE_ARTICLES_LIST = "favorite_articles_list";
    // Name use to send extra from intent to intent
    public static final String PACKAGE_NAME = "com.aloisandco.beautifuleasysummer";
    // Interpolator for the animations
    public static final TimeInterpolator ACC_DEC_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    // Animation duration
    public static final int ANIMATION_DURATION = 400;
}
