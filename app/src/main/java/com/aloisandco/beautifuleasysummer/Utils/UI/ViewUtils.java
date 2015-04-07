package com.aloisandco.beautifuleasysummer.Utils.UI;

import android.view.View;
import android.view.Window;

/**
 * Created by quentinmetzler on 15/03/15.
 */
public class ViewUtils {
    /**
     * Calculate the coordinates of the center of a view on the screen
     * @param view the view we want to get the center
     * @param window the window in which the view is displayed
     * @return the coordinate of the center
     */
    static public float[] getCenterPositionOfViewOnScreen(View view, Window window) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        int statusBarHeight = ScreenUtils.getStatusBarHeightInWindow(window);

        float x = (float) (position[0] + view.getWidth() / 2.0);
        float y = (float) (position[1] + view.getHeight() / 2.0 - statusBarHeight);
        return new float[] { x, y };
    }
}
