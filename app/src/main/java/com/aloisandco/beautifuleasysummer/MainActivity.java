package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.aloisandco.beautifuleasysummer.utils.ImageViewUtils;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private boolean firstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (firstLaunch) {
            createHoleInBackgroundView();
            animateLogoInCircle();
            launchNextActivity();
            firstLaunch = false;
        }
    }

    private void createHoleInBackgroundView() {
        ImageView backgroundView = (ImageView) findViewById(R.id.background);
        ImageView circleView = (ImageView) findViewById(R.id.circle);
        // Make a hole in the backgroundView bitmap of the size of the circleView
        Bitmap backgroundBitmap = ImageViewUtils.createCircleInImageViewFromSizeOfOtherImageView(backgroundView, circleView, getWindow());
        // Set the new bitmap with the hole as the new background bitmap
        backgroundView.setImageBitmap(backgroundBitmap);
    }

    private void animateLogoInCircle() {
        ImageView logoView = (ImageView) findViewById(R.id.logo);
        ImageView circleView = (ImageView) findViewById(R.id.circle);
        RotateAnimation anim = new RotateAnimation(360f, 0f, logoView.getWidth() / 2, circleView.getHeight());
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setRepeatCount(0);
        anim.setStartOffset(1000);
        anim.setDuration(500);
        logoView.startAnimation(anim);
    }

    private void launchNextActivity () {
        final Activity main = this;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(main, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }
}
