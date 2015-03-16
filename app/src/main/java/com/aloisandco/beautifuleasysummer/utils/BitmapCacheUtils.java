package com.aloisandco.beautifuleasysummer.utils;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by quentinmetzler on 16/03/15.
 */
public class BitmapCacheUtils {
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
