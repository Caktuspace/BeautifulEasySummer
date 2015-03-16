package com.aloisandco.beautifuleasysummer.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;

/**
 * Created by quentinmetzler on 15/03/15.
 */
public class ScreenUtils {
    /**
     * Retrieve the height of the status bar from a window
     * @param window The window containing the status bar
     * @return The height of the status bar
     */
    static public int getStatusBarHeightInWindow(Window window) {
        Rect rectangle= new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }
}
