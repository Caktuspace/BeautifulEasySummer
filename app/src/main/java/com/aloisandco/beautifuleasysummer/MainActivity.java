package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.aloisandco.beautifuleasysummer.utils.ImageViewUtils;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        ImageView backgroundView = (ImageView) findViewById(R.id.background);
        ImageView circleView = (ImageView) findViewById(R.id.circle);
        // Make a hole in the backgroundView bitmap of the size of the circleView
        Bitmap backgroundBitmap = ImageViewUtils.createCircleInImageViewFromSizeOfOtherImageView(backgroundView, circleView, getWindow());
        // Set the new bitmap with the hole as the new background bitmap
        backgroundView.setImageBitmap(backgroundBitmap);
    }




}
