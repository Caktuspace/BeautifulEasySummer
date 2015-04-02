package com.aloisandco.beautifuleasysummer.Article;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.utils.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.utils.Constants;
import com.aloisandco.beautifuleasysummer.utils.FavoriteManager;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.HtmlTagHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by quentinmetzler on 24/03/15.
 */
public class ArticleActivity extends Activity {
    private static final TimeInterpolator sAccDecc = new AccelerateDecelerateInterpolator();
    private static final int ANIM_DURATION = 250;

    private InterstitialAd mInterstitialAd;
    private TextView mTitleTextView;
    private ImageView mBackgroundImageView;
    private WebView mArticleView;
    private View mDivider;
    private Boolean mCloseInProgress;
    private CheckBox mCheckBox;

    int mArticleId;
    int mLeftDeltaDivider;
    int mTopDeltaDivider;
    float mWidthScaleDivider;
    float mHeightScaleDivider;
    int mLeftDeltaTitle;
    int mTopDeltaTitle;
    float mWidthScaleTitle;
    float mHeightScaleTitle;
    int mLeftDeltaCheckbox;
    int mTopDeltaCheckbox;
    float mWidthScaleCheckbox;
    float mHeightScaleCheckbox;
    int mFavoritePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        mCloseInProgress = false;
        mTitleTextView = (TextView) findViewById(R.id.title);
        mBackgroundImageView = (ImageView) findViewById(R.id.tropical_background);
        mArticleView = (WebView) findViewById(R.id.articleView);
        mDivider = findViewById(R.id.divider);
        mCheckBox = (CheckBox) findViewById(R.id.favorite);

        Bundle bundle = getIntent().getExtras();

        initTitleAndWebview(bundle);
        initInterstitialAd();

        if (savedInstanceState == null) {
            initReceivedPosition(bundle);
        }

        handleCheckBoxBehavior();
    }

    public void initTitleAndWebview(Bundle bundle) {
        mArticleId = bundle.getInt(Constants.PACKAGE_NAME + ".itemResourceId");
        TypedArray itemArray = getResources().obtainTypedArray(mArticleId);
        String title = itemArray.getString(0);
        String articleUrl = itemArray.getString(1);
        itemArray.recycle();

        FontManager fontManager = FontManager.getInstance(getAssets());
        mTitleTextView.setTypeface(fontManager.ralewayLightFont);
        mTitleTextView.setText(Html.fromHtml(title.toUpperCase(), null, new HtmlTagHandler()));

        mArticleView.loadUrl(articleUrl);
        mArticleView.setBackgroundColor(Color.TRANSPARENT);
        final WebSettings webSettings = mArticleView.getSettings();
        webSettings.setDefaultFontSize(18);
    }

    public void initInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runExitAnimation();
                    }
                }, 500);
            }
        });
    }

    public void initReceivedPosition(Bundle bundle) {
        final int dividerTop = bundle.getInt(Constants.PACKAGE_NAME + ".topDivider");
        final int dividerLeft = bundle.getInt(Constants.PACKAGE_NAME + ".leftDivider");
        final int dividerWidth = bundle.getInt(Constants.PACKAGE_NAME + ".widthDivider");
        final int dividerHeight = bundle.getInt(Constants.PACKAGE_NAME + ".heightDivider");
        final int titleTop = bundle.getInt(Constants.PACKAGE_NAME + ".topText");
        final int titleLeft = bundle.getInt(Constants.PACKAGE_NAME + ".leftText");
        final int titleWidth = bundle.getInt(Constants.PACKAGE_NAME + ".widthText");
        final int titleHeight = bundle.getInt(Constants.PACKAGE_NAME + ".heightText");
        final int checkboxTop = bundle.getInt(Constants.PACKAGE_NAME + ".topCheckbox");
        final int checkboxLeft = bundle.getInt(Constants.PACKAGE_NAME + ".leftCheckbox");
        final int checkboxWidth = bundle.getInt(Constants.PACKAGE_NAME + ".widthCheckbox");
        final int checkboxHeight = bundle.getInt(Constants.PACKAGE_NAME + ".heightCheckbox");

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

                mCheckBox.getLocationOnScreen(screenLocation);
                mLeftDeltaCheckbox = checkboxLeft - screenLocation[0];
                mTopDeltaCheckbox = checkboxTop - screenLocation[1];
                mWidthScaleCheckbox = (float) checkboxWidth / mCheckBox.getWidth();
                mHeightScaleCheckbox = (float) checkboxHeight / mCheckBox.getHeight();

                runEnterAnimation();
                return true;
            }
        });
    }

    public void handleCheckBoxBehavior() {
        mCheckBox = (CheckBox) findViewById(R.id.favorite);
        mCheckBox.setChecked(FavoriteManager.isArticleFavorite(mArticleId, this));
        if (mCheckBox.isChecked()) {
            mFavoritePosition = FavoriteManager.getPositionOfArticle(mArticleId, this);
        }

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mFavoritePosition != -1) {
                        FavoriteManager.addArticleToFavoriteAtPosition(mArticleId, mFavoritePosition, ArticleActivity.this);
                    } else {
                        FavoriteManager.addArticleToFavorite(mArticleId, ArticleActivity.this);
                    }
                } else {
                    FavoriteManager.removeArticleFromFavorite(mArticleId, ArticleActivity.this);
                }
            }
        });
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

        mCheckBox.setPivotX(0);
        mCheckBox.setPivotY(0);
        mCheckBox.setScaleX(mWidthScaleCheckbox);
        mCheckBox.setScaleY(mHeightScaleCheckbox);
        mCheckBox.setTranslationX(mLeftDeltaCheckbox);
        mCheckBox.setTranslationY(mTopDeltaCheckbox);
        // Animate scale and translation to go from thumbnail to full size
        mCheckBox.animate().setDuration(ANIM_DURATION).
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
                mArticleView.animate().scaleY(1).setInterpolator(sAccDecc).setDuration(ANIM_DURATION).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        requestNewInterstitial();
                    }
                });
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
    public void runExitAnimation() {
        // No need to set initial values for the reverse animation; the image is at the
        // starting size/location that we want to start from. Just animate to the
        // thumbnail size/location that we retrieved earlier
        mCloseInProgress = true;
        mArticleView.animate().scaleY(0).setInterpolator(sAccDecc).setDuration(ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                mDivider.animate().setDuration(ANIM_DURATION).
                        scaleX(mWidthScaleDivider).scaleY(mHeightScaleDivider).
                        translationX(mLeftDeltaDivider).translationY(mTopDeltaDivider).
                        setInterpolator(sAccDecc);

                mCheckBox.animate().setDuration(ANIM_DURATION).
                        scaleX(mWidthScaleCheckbox).scaleY(mHeightScaleCheckbox).
                        translationX(mLeftDeltaCheckbox).translationY(mTopDeltaCheckbox).
                        setInterpolator(sAccDecc).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityTransitionManager.getInstance().getMenuItemView() != null) {
                            ActivityTransitionManager.getInstance().getMenuListItemView().setAlpha(1);
                        }
                        finish();
                    }
                });

                mTitleTextView.animate().setDuration(ANIM_DURATION).
                        scaleX(mWidthScaleTitle).scaleY(mHeightScaleTitle).
                        translationX(mLeftDeltaTitle).translationY(mTopDeltaTitle).
                        setInterpolator(sAccDecc).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityTransitionManager.getInstance().getMenuItemView() != null) {
                            ActivityTransitionManager.getInstance().getMenuListItemView().setAlpha(1);
                        }
                        finish();
                    }
                });

                mBackgroundImageView.animate().setDuration(ANIM_DURATION).setInterpolator(sAccDecc).alpha(0);
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("157ADC68E31F59407ADA80B42A0F1D73")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        if (!mCloseInProgress) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                runExitAnimation();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

}
