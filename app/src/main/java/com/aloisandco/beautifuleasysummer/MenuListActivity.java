package com.aloisandco.beautifuleasysummer;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.utils.FontManager;

/**
 * Created by quentinmetzler on 18/03/15.
 */
public class MenuListActivity extends Activity {
    private static final TimeInterpolator sAccDecc = new AccelerateDecelerateInterpolator();
    private static final int ANIM_DURATION = 400;
    private static final String PACKAGE_NAME = "com.aloisandco.beautifuleasysummer.mainActivityAnim";

    private ImageView mIconImageView;
    private TextView mTitleTextView;
    private ImageView mBackgroundImageView;

    int mLeftDeltaIcon;
    int mTopDeltaIcon;
    float mWidthScaleIcon;
    float mHeightScaleIcon;
    int mLeftDeltaTitle;
    int mTopDeltaTitle;
    float mWidthScaleTitle;
    float mHeightScaleTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        mIconImageView = (ImageView) findViewById(R.id.icon);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mBackgroundImageView = (ImageView) findViewById(R.id.tropical_background);

        initFont();

        Bundle bundle = getIntent().getExtras();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), bundle.getInt(PACKAGE_NAME + ".iconId"));
        String title = bundle.getString(PACKAGE_NAME + ".text");

        mIconImageView.setImageBitmap(icon);
        mTitleTextView.setText(title);

        final int iconTop = bundle.getInt(PACKAGE_NAME + ".topIcon");
        final int iconLeft = bundle.getInt(PACKAGE_NAME + ".leftIcon");
        final int iconWidth = bundle.getInt(PACKAGE_NAME + ".widthIcon");
        final int iconHeight = bundle.getInt(PACKAGE_NAME + ".heightIcon");
        final int titleTop = bundle.getInt(PACKAGE_NAME + ".topText");
        final int titleLeft = bundle.getInt(PACKAGE_NAME + ".leftText");
        final int titleWidth = bundle.getInt(PACKAGE_NAME + ".widthText");
        final int titleHeight = bundle.getInt(PACKAGE_NAME + ".heightText");

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mIconImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mIconImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mIconImageView.getLocationOnScreen(screenLocation);
                    mLeftDeltaIcon = iconLeft - screenLocation[0];
                    mTopDeltaIcon = iconTop - screenLocation[1];
                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScaleIcon = (float) iconWidth / mIconImageView.getWidth();
                    mHeightScaleIcon = (float) iconHeight / mIconImageView.getHeight();

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

    private void initFont() {
        FontManager fontManager = FontManager.getInstance(getAssets());
        mTitleTextView.setTypeface(fontManager.ralewayMediumFont);
    }

    public void runEnterAnimation() {
        animateIconToDefaultSize();
        animateTitleToDefaultSize();
        fadeInBackground();
    }

    private void animateIconToDefaultSize() {
        mIconImageView.setPivotX(0);
        mIconImageView.setPivotY(0);
        mIconImageView.setScaleX(mWidthScaleIcon);
        mIconImageView.setScaleY(mHeightScaleIcon);
        mIconImageView.setTranslationX(mLeftDeltaIcon);
        mIconImageView.setTranslationY(mTopDeltaIcon);
        // Animate scale and translation to go from thumbnail to full size
        mIconImageView.animate().setStartDelay(100).setDuration(ANIM_DURATION).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sAccDecc);
    }

    private void animateTitleToDefaultSize() {
        mTitleTextView.setPivotX(0);
        mTitleTextView.setPivotY(0);
        mTitleTextView.setScaleX(mWidthScaleTitle);
        mTitleTextView.setScaleY(mHeightScaleTitle);
        mTitleTextView.setTranslationX(mLeftDeltaTitle);
        mTitleTextView.setTranslationY(mTopDeltaTitle);
        // Animate scale and translation to go from thumbnail to full size
        mTitleTextView.animate().setStartDelay(100).setDuration(ANIM_DURATION).
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

        mIconImageView.animate().setStartDelay(0).setDuration(ANIM_DURATION).
                scaleX(mWidthScaleIcon).scaleY(mHeightScaleIcon).
                translationX(mLeftDeltaIcon).translationY(mTopDeltaIcon).
                setInterpolator(sAccDecc);

        mTitleTextView.animate().setStartDelay(0).setDuration(ANIM_DURATION).
                scaleX(mWidthScaleTitle).scaleY(mHeightScaleTitle).
                translationX(mLeftDeltaTitle).translationY(mTopDeltaTitle).
                setInterpolator(sAccDecc).withEndAction(endAction);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofInt(mBackgroundImageView, "alpha", 255, 0);
        alphaAnimator.setDuration(ANIM_DURATION);
        alphaAnimator.setInterpolator(sAccDecc);
        alphaAnimator.start();
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
