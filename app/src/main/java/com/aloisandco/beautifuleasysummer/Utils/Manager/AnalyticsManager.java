package com.aloisandco.beautifuleasysummer.Utils.Manager;

import android.content.Context;

import com.aloisandco.beautifuleasysummer.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by quentinmetzler on 04/05/15.
 */

public class AnalyticsManager {
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    static HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    static synchronized public Tracker getTracker(TrackerName trackerId, Context context) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = analytics.newTracker(R.xml.application_tracker);
            mTrackers.put(trackerId, t);
        }

        return mTrackers.get(trackerId);
    }
}
