package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.utils.ImageViewUtils;

public class MenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initFont();
        drawLinesOnLogo();
    }

    private void initFont() {
        Typeface ralewayBoldFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf");
        Typeface ralewayMediumFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        Typeface ralewayLightFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");

        TextView conseilsText = (TextView) findViewById(R.id.conseils);
        TextView pourLeteText = (TextView) findViewById(R.id.pourLete);
        conseilsText.setTypeface(ralewayBoldFont);
        pourLeteText.setTypeface(ralewayLightFont);

        TextView beauteText = (TextView) findViewById(R.id.beaute_text);
        TextView gourmandiseText = (TextView) findViewById(R.id.gourmandise_text);
        TextView modeText = (TextView) findViewById(R.id.mode_text);
        TextView activiteText = (TextView) findViewById(R.id.activite_text);
        TextView soleilText = (TextView) findViewById(R.id.soleil_text);
        TextView coiffureText = (TextView) findViewById(R.id.coiffure_text);
        beauteText.setTypeface(ralewayMediumFont);
        gourmandiseText.setTypeface(ralewayMediumFont);
        modeText.setTypeface(ralewayMediumFont);
        activiteText.setTypeface(ralewayMediumFont);
        soleilText.setTypeface(ralewayMediumFont);
        coiffureText.setTypeface(ralewayMediumFont);
    }

    private void drawLinesOnLogo() {
        final int activityTransitionDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        final ImageView leftLineImageView = (ImageView) findViewById(R.id.logo_left_line);
        final ImageView rightLineImageView = (ImageView) findViewById(R.id.logo_right_line);
        final Context context = this;
        leftLineImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageViewUtils.animateImageViewToWidth(leftLineImageView, 41, context);
            }
        }, activityTransitionDuration);
        rightLineImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageViewUtils.animateImageViewToWidth(rightLineImageView, 41, context);
            }
        }, activityTransitionDuration);
    }
}
