package com.aloisandco.beautifuleasysummer.Utils.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.Utils.Constants;

/**
 * Created by quentinmetzler on 03/05/15.
 */
public class SoundManager {
    private Boolean playing;
    private MediaPlayer mp;
    private static SoundManager mInstance;

    private SoundManager(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mp = MediaPlayer.create(context, R.raw.son_bes);
        mp.start();
        float log1=(float)(Math.log(100-50)/Math.log(100));
        mp.setVolume(1-log1, 1-log1);
        playing = prefs.getBoolean(Constants.SHARED_PREFERENCES_SOUND, true);
        if (!playing) {
            mp.pause();
        }
    }

    public static synchronized SoundManager getInstance(Context context)
    {
        if (SoundManager.mInstance == null)
        {
            SoundManager.mInstance = new SoundManager(context);
        }
        return SoundManager.mInstance;
    }

    public void changeSoundState(Context context) {
        if (playing) {
            mp.pause();
        } else {
            mp.start();
        }
        playing = !playing;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.SHARED_PREFERENCES_SOUND, playing);
        editor.apply();
    }

    public Boolean isPlaying() {
        return playing;
    }
}
