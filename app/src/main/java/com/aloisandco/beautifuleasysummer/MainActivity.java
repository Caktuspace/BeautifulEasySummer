package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.aloisandco.beautifuleasysummer.utils.ImageViewUtils;


public class MainActivity extends Activity {

    private boolean firstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (firstLaunch) {
            ImageView backgroundView = (ImageView) findViewById(R.id.background);
            ImageView circleView = (ImageView) findViewById(R.id.circle);
            // Make a hole in the backgroundView bitmap of the size of the circleView
            Bitmap backgroundBitmap = ImageViewUtils.createCircleInImageViewFromSizeOfOtherImageView(backgroundView, circleView, getWindow());
            // Set the new bitmap with the hole as the new background bitmap
            backgroundView.setImageBitmap(backgroundBitmap);

            ImageView logoView = (ImageView) findViewById(R.id.logo);
            RotateAnimation anim = new RotateAnimation(0f, 360f, logoView.getWidth() / 2, circleView.getHeight());
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setStartOffset(3000);
            anim.setDuration(2000);
            logoView.startAnimation(anim);
            firstLaunch = false;
        }
    }
}
