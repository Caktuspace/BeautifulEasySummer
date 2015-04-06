package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;

import com.aloisandco.beautifuleasysummer.Article.ArticleActivity;
import com.aloisandco.beautifuleasysummer.Menu.MenuActivity;
import com.aloisandco.beautifuleasysummer.utils.BitmapCacheUtils;
import com.aloisandco.beautifuleasysummer.utils.Constants;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.ImageViewUtils;
import com.aloisandco.beautifuleasysummer.utils.ScreenUtils;

import java.util.ArrayList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FontManager.getInstance(getAssets());
        // load a webview because the first one is always slow
        WebView webView = new WebView(this);
        webView.loadUrl(getString(R.string.salade_tropicale_html));

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
        logoView.animate().setDuration(Constants.ANIMATION_DURATION).setStartDelay(Constants.ANIMATION_DURATION).setInterpolator(new AccelerateDecelerateInterpolator()).rotation(-360f).withEndAction(new Runnable() {
            @Override
            public void run() {
                ImageView logoView = (ImageView) findViewById(R.id.logo);
                int[] screenLocation = new int[2];
                logoView.getLocationOnScreen(screenLocation);
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);

                ArrayList<AnimatedView> dataList = new ArrayList<>();
                AnimatedView logoAnimatedView = new AnimatedView(screenLocation[1],
                        screenLocation[0],
                        logoView.getWidth(),
                        logoView.getHeight(),
                        R.id.logo, 1);
                dataList.add(logoAnimatedView);
                intent.
                        putExtra(Constants.PACKAGE_NAME + ".resourceId", R.id.background).
                        putExtra(Constants.PACKAGE_NAME + ".logoWidth", logoView.getWidth()).
                        putExtra(Constants.PACKAGE_NAME + ".logoHeight", logoView.getHeight()).
                        putParcelableArrayListExtra(Constants.PACKAGE_NAME + ".animatedViews", dataList);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
