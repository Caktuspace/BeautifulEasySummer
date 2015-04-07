package com.aloisandco.beautifuleasysummer.Utils.Manager;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by quentinmetzler on 16/03/15.
 */

/**
 * Use to store the background bitmap with a hole so that we can have it to animate it
 * in the second activity
 */
public class BitmapCacheManager {
    static HashMap<Integer, Bitmap> sBitmapResourceMap = new HashMap<Integer, Bitmap>();

    /**
     * Utility method to get bitmap from cache or, if not there, load it
     * from its resource.
     */
    static public Bitmap getBitmap(int resourceId) {
        return sBitmapResourceMap.get(resourceId);
    }

    static public void setBitmap(int resourceId, Bitmap bitmap) {
        sBitmapResourceMap.put(resourceId, bitmap);
    }
}
