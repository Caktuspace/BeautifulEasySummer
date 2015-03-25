package com.aloisandco.beautifuleasysummer.Article;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.utils.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.HtmlTagHandler;

/**
 * Created by quentinmetzler on 24/03/15.
 */
public class ArticleActivity extends Activity {
    private static final TimeInterpolator sAccDecc = new AccelerateDecelerateInterpolator();
    private static final int ANIM_DURATION = 250;
    private static final String PACKAGE_NAME = "com.aloisandco.beautifuleasysummer.mainActivityAnim";

    private TextView mTitleTextView;
    private ImageView mBackgroundImageView;
    private WebView mArticleView;
    private View mDivider;

    int mLeftDeltaDivider;
    int mTopDeltaDivider;
    float mWidthScaleDivider;
    float mHeightScaleDivider;
    int mLeftDeltaTitle;
    int mTopDeltaTitle;
    float mWidthScaleTitle;
    float mHeightScaleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mTitleTextView = (TextView) findViewById(R.id.title);
        mBackgroundImageView = (ImageView) findViewById(R.id.tropical_background);
        mArticleView = (WebView) findViewById(R.id.articleView);
        mDivider = findViewById(R.id.divider);

        Bundle bundle = getIntent().getExtras();
        final String title = bundle.getString(PACKAGE_NAME + ".title");
        final String articleUrl = bundle.getString(PACKAGE_NAME + ".article");

        FontManager fontManager = FontManager.getInstance(getAssets());
        mTitleTextView.setTypeface(fontManager.ralewayLightFont);
        mTitleTextView.setText(Html.fromHtml(title.toUpperCase(), null, new HtmlTagHandler()));

        mArticleView.loadUrl(articleUrl);
        mArticleView.setBackgroundColor(Color.TRANSPARENT);
        mArticleView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        final WebSettings webSettings = mArticleView.getSettings();
        webSettings.setDefaultFontSize(18);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setGeolocationEnabled(false);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSaveFormData(false);

        final int dividerTop = bundle.getInt(PACKAGE_NAME + ".topDivider");
        final int dividerLeft = bundle.getInt(PACKAGE_NAME + ".leftDivider");
        final int dividerWidth = bundle.getInt(PACKAGE_NAME + ".widthDivider");
        final int dividerHeight = bundle.getInt(PACKAGE_NAME + ".heightDivider");
        final int titleTop = bundle.getInt(PACKAGE_NAME + ".topText");
        final int titleLeft = bundle.getInt(PACKAGE_NAME + ".leftText");
        final int titleWidth = bundle.getInt(PACKAGE_NAME + ".widthText");
        final int titleHeight = bundle.getInt(PACKAGE_NAME + ".heightText");


        if (savedInstanceState == null) {
            ViewTreeObserver observer = mTitleTextView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mTitleTextView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mDivider.getLocationOnScreen(screenLocation);
                    mLeftDeltaDivider = dividerLeft - screenLocation[0];
                    mTopDeltaDivider = dividerTop - screenLocation[1];
                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScaleDivider = (float) dividerWidth / mDivider.getWidth();
                    mHeightScaleDivider = (float) dividerHeight / mDivider.getHeight();

                    mTitleTextView.getLocationOnScreen(screenLocation);
                    mLeftDeltaTitle = titleLeft - screenLocation[0];
                    mTopDeltaTitle = titleTop - screenLocation[1];
                    mWidthScaleTitle = (float) titleWidth / mTitleTextView.getWidth();
                    mHeightScaleTitle = (float) titleHeight / mTitleTextView.getHeight();

                    runEnterAnimation();
                    return true;
                }
            });
        }
    }

    public void runEnterAnimation() {
        animateTitleToDefaultSize();
        if (ActivityTransitionManager.getInstance().getMenuListItemView() != null) {
            ActivityTransitionManager.getInstance().getMenuListItemView().setAlpha(0);
        }
        fadeInBackground();
    }

    private void animateTitleToDefaultSize() {
        mTitleTextView.setPivotX(0);
        mTitleTextView.setPivotY(0);
        mTitleTextView.setScaleX(mWidthScaleTitle);
        mTitleTextView.setScaleY(mHeightScaleTitle);
        mTitleTextView.setTranslationX(mLeftDeltaTitle);
        mTitleTextView.setTranslationY(mTopDeltaTitle);
        // Animate scale and translation to go from thumbnail to full size
        mTitleTextView.animate().setDuration(ANIM_DURATION).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sAccDecc);
    }

    private void fadeInBackground() {
        // Fade out background
        ObjectAnimator alphaAnimator = ObjectAnimator.ofInt(mBackgroundImageView, "alpha", 0, 255);
        alphaAnimator.setDuration(ANIM_DURATION);
        alphaAnimator.setInterpolator(sAccDecc);
        alphaAnimator.start();

        mDivider.setPivotX(0);
        mDivider.setPivotY(0);
        mDivider.setScaleX(mWidthScaleDivider);
        mDivider.setScaleY(mHeightScaleDivider);
        mDivider.setTranslationX(mLeftDeltaDivider);
        mDivider.setTranslationY(mTopDeltaDivider);
        mArticleView.setPivotY(0);
        mArticleView.setScaleY(0);
        // Animate scale and translation to go from thumbnail to full size
        mDivider.animate().setDuration(ANIM_DURATION).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sAccDecc).withEndAction(new Runnable() {
            @Override
            public void run() {
                mArticleView.animate().scaleY(1).setInterpolator(sAccDecc).setDuration(ANIM_DURATION);
            }
        });
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     * when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction) {
        // No need to set initial values for the reverse animation; the image is at the
        // starting size/location that we want to start from. Just animate to the
        // thumbnail size/location that we retrieved earlier

        mArticleView.animate().scaleY(0).setInterpolator(sAccDecc).setDuration(ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                mDivider.animate().setDuration(ANIM_DURATION).
                        scaleX(mWidthScaleDivider).scaleY(mHeightScaleDivider).
                        translationX(mLeftDeltaDivider).translationY(mTopDeltaDivider).
                        setInterpolator(sAccDecc);

                mTitleTextView.animate().setDuration(ANIM_DURATION).
                        scaleX(mWidthScaleTitle).scaleY(mHeightScaleTitle).
                        translationX(mLeftDeltaTitle).translationY(mTopDeltaTitle).
                        setInterpolator(sAccDecc).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityTransitionManager.getInstance().getMenuItemView() != null) {
                            ActivityTransitionManager.getInstance().getMenuListItemView().setAlpha(1);
                        }
                        endAction.run();
                    }
                });

                mBackgroundImageView.animate().setDuration(ANIM_DURATION).setInterpolator(sAccDecc).alpha(0);
            }
        });
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

}
