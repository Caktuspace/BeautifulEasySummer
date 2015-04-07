package com.aloisandco.beautifuleasysummer.Utils.Manager;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by quentinmetzler on 18/03/15.
 */

/**
 * Singleton responsible for creating and storing all the custom font we need in the application
 */
public class FontManager {

    public final Typeface ralewayBoldFont;
    public final Typeface ralewayMediumFont;
    public final Typeface ralewayLightFont;

    // singleton instance
    private static FontManager      mInstance;

    protected FontManager(AssetManager assetManager)
    {
        // enforce singleton
        super();

        this.ralewayBoldFont = Typeface.createFromAsset(assetManager, "fonts/Raleway-Bold.ttf");
        this.ralewayMediumFont = Typeface.createFromAsset(assetManager, "fonts/Raleway-Medium.ttf");
        this.ralewayLightFont = Typeface.createFromAsset(assetManager, "fonts/Raleway-Light.ttf");
    }

    public static synchronized FontManager getInstance(AssetManager assetManager)
    {
        if (FontManager.mInstance == null)
        {
            FontManager.mInstance = new FontManager(assetManager);
        }
        return FontManager.mInstance;
    }

    public static synchronized FontManager getInstance() {
        return FontManager.mInstance;
    }
}
