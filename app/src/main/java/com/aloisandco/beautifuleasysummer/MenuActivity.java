package com.aloisandco.beautifuleasysummer;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aloisandco.beautifuleasysummer.utils.BitmapCacheUtils;
import com.aloisandco.beautifuleasysummer.utils.FontManager;

public class MenuActivity extends Activity {

    private static final TimeInterpolator sAccDecc = new AccelerateDecelerateInterpolator();
    private static final int ANIM_DURATION = 400;
    private static final String PACKAGE_NAME = "com.aloisandco.beautifuleasysummer.mainActivityAnim";

    int mLeftDeltaLogo;
    int mTopDeltaLogo;
    float mWidthScaleLogo;
    float mHeightScaleLogo;
    private ImageView mLogoImageView;
    private ImageView mBackgroundView;
    private ImageView mFeetView;
    private ImageView mLeftLineImageView;
    private ImageView mRightLineImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initFont();

        mLeftLineImageView = (ImageView) findViewById(R.id.logo_left_line);
        mRightLineImageView = (ImageView) findViewById(R.id.logo_right_line);
        mLogoImageView = (ImageView) findViewById(R.id.logo);
        mBackgroundView = (ImageView) findViewById(R.id.background);
        mFeetView = (ImageView) findViewById(R.id.FeetView);

        Bundle bundle = getIntent().getExtras();
        Bitmap bitmap = BitmapCacheUtils.getBitmap(bundle.getInt(PACKAGE_NAME + ".resourceId"));
        final int logoTop = bundle.getInt(PACKAGE_NAME + ".top");
        final int logoLeft = bundle.getInt(PACKAGE_NAME + ".left");
        final int logoWidth = bundle.getInt(PACKAGE_NAME + ".width");
        final int logoHeight = bundle.getInt(PACKAGE_NAME + ".height");
        mBackgroundView.setImageBitmap(bitmap);

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mLogoImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mLogoImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mLogoImageView.getLocationOnScreen(screenLocation);
                    mLeftDeltaLogo = logoLeft - screenLocation[0];
                    mTopDeltaLogo = logoTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScaleLogo = (float) logoWidth / mLogoImageView.getWidth();
                    mHeightScaleLogo = (float) logoHeight / mLogoImageView.getHeight();

                    runEnterAnimation();
                    return true;
                }
            });
        }
    }

    private void initFont() {
        FontManager fontManager = FontManager.getInstance(getAssets());

        TextView conseilsText = (TextView) findViewById(R.id.conseils);
        TextView pourLeteText = (TextView) findViewById(R.id.pourLete);
        conseilsText.setTypeface(fontManager.ralewayBoldFont);
        pourLeteText.setTypeface(fontManager.ralewayLightFont);

        TextView beauteText = (TextView) findViewById(R.id.beaute_text);
        TextView gourmandiseText = (TextView) findViewById(R.id.gourmandise_text);
        TextView modeText = (TextView) findViewById(R.id.mode_text);
        TextView activiteText = (TextView) findViewById(R.id.activite_text);
        TextView soleilText = (TextView) findViewById(R.id.soleil_text);
        TextView coiffureText = (TextView) findViewById(R.id.coiffure_text);
        beauteText.setTypeface(fontManager.ralewayMediumFont);
        gourmandiseText.setTypeface(fontManager.ralewayMediumFont);
        modeText.setTypeface(fontManager.ralewayMediumFont);
        activiteText.setTypeface(fontManager.ralewayMediumFont);
        soleilText.setTypeface(fontManager.ralewayMediumFont);
        coiffureText.setTypeface(fontManager.ralewayMediumFont);
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the pictue is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        initLogoPositionToMatchPreviousScreen();
        animateLogoToDefaultSize();
        animateAndHideViewFromHome();
    }

    private void initLogoPositionToMatchPreviousScreen() {
        mRightLineImageView.setPivotX(0);
        mRightLineImageView.setScaleX(0);
        mLeftLineImageView.setPivotX(mLeftLineImageView.getWidth());
        mLeftLineImageView.setScaleX(0);

        mLogoImageView.setPivotX(0);
        mLogoImageView.setPivotY(0);
        mLogoImageView.setScaleX(mWidthScaleLogo);
        mLogoImageView.setScaleY(mHeightScaleLogo);
        mLogoImageView.setTranslationX(mLeftDeltaLogo);
        mLogoImageView.setTranslationY(mTopDeltaLogo);
    }

    private void animateLogoToDefaultSize() {
        // Animate scale and translation to go from thumbnail to full size
        mLogoImageView.animate().setDuration(ANIM_DURATION).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sAccDecc).withEndAction(new Runnable() {
            @Override
            public void run() {
                drawLinesUnderLogo();
            }
        });
    }

    private void drawLinesUnderLogo() {
        mLeftLineImageView.animate().setDuration(ANIM_DURATION).
                scaleX(1).scaleY(1).
                setInterpolator(sAccDecc);
        mRightLineImageView.animate().setDuration(ANIM_DURATION).
                scaleX(1).scaleY(1).
                setInterpolator(sAccDecc);
    }

    private void animateAndHideViewFromHome() {
        int[] screenLocation = new int[2];
        mLogoImageView.getLocationOnScreen(screenLocation);
        mBackgroundView.setPivotX(screenLocation[0] + mLogoImageView.getWidth() / 2 * mWidthScaleLogo);
        mBackgroundView.setPivotY(screenLocation[1] + mLogoImageView.getHeight() / 2 * mHeightScaleLogo);
        mBackgroundView.animate().setDuration(ANIM_DURATION).
                scaleX(5-mWidthScaleLogo).scaleY(5-mHeightScaleLogo).
                translationX(mBackgroundView.getWidth()/2 - (screenLocation[0] + mLogoImageView.getWidth() * mWidthScaleLogo / 2)).
                translationY(mBackgroundView.getHeight()/2 - (screenLocation[1] + mLogoImageView.getHeight() * mHeightScaleLogo / 2)).
                setInterpolator(sAccDecc).withEndAction(new Runnable() {
            @Override
            public void run() {
                mBackgroundView.setVisibility(View.GONE);
            }
        });

        mFeetView.animate().setDuration(ANIM_DURATION / 2).
                scaleX(2-mWidthScaleLogo).scaleY(2-mHeightScaleLogo).
                translationX(0).translationY(mFeetView.getHeight()).
                setInterpolator(sAccDecc).withEndAction(new Runnable() {
            @Override
            public void run() {
                mFeetView.setVisibility(View.GONE);
            }
        });
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            moveTaskToBack(true); // exist app
        } else {
            Toast.makeText(this, R.string.encore_pour_quitter,
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}
