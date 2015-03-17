package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.aloisandco.beautifuleasysummer.utils.BitmapCacheUtils;
import com.aloisandco.beautifuleasysummer.utils.ImageViewUtils;


public class MainActivity extends Activity {

    private static final String PACKAGE_NAME = "com.aloisandco.beautifuleasysummer.mainActivityAnim";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView circleView = (ImageView) findViewById(R.id.circle);
        if (savedInstanceState == null) {
            ViewTreeObserver observer = circleView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                    circleView.getViewTreeObserver().removeOnPreDrawListener(this);
                    createHoleInBackgroundView();
                    animateLogoInCircle();

                    return true;
                }
            });
        }
    }

    private void createHoleInBackgroundView() {
        ImageView backgroundView = (ImageView) findViewById(R.id.background);
        ImageView circleView = (ImageView) findViewById(R.id.circle);
        // Make a hole in the backgroundView bitmap of the size of the circleView
        Bitmap backgroundBitmap = ImageViewUtils.createCircleInImageViewFromSizeOfOtherImageView(backgroundView, circleView, getWindow());
        // Set the new bitmap with the hole as the new background bitmap
        backgroundView.setImageBitmap(backgroundBitmap);
        BitmapCacheUtils.setBitmap(R.id.background, backgroundBitmap);
    }

    private void animateLogoInCircle() {
        ImageView logoView = (ImageView) findViewById(R.id.logo);
        ImageView circleView = (ImageView) findViewById(R.id.circle);
        logoView.setPivotX(logoView.getWidth() / 2);
        logoView.setPivotY(circleView.getHeight());
        logoView.animate().setDuration(500).setStartDelay(500).setInterpolator(new AccelerateDecelerateInterpolator()).rotation(-360f).withEndAction(new Runnable() {
            @Override
            public void run() {
                ImageView logoView = (ImageView) findViewById(R.id.logo);
                int[] screenLocation = new int[2];
                logoView.getLocationOnScreen(screenLocation);
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                intent.
                        putExtra(PACKAGE_NAME + ".resourceId", R.id.background).
                        putExtra(PACKAGE_NAME + ".left", screenLocation[0]).
                        putExtra(PACKAGE_NAME + ".top", screenLocation[1]).
                        putExtra(PACKAGE_NAME + ".width", logoView.getWidth()).
                        putExtra(PACKAGE_NAME + ".height", logoView.getHeight());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
