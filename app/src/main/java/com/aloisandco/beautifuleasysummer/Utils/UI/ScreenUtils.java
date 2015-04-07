package com.aloisandco.beautifuleasysummer.Utils.UI;

import android.content.res.Resources;
import android.graphics.Rect;
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

    /**
     * Convert a value to a Dpi one
     * @param resources The resource of the phone containing the screen density
     * @param value the value we want in Dpi
     * @return the value converted in Dpi for the current phone
     */
    static  public int valueToDpi(Resources resources, int value) {
        final float scale = resources.getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }
}
