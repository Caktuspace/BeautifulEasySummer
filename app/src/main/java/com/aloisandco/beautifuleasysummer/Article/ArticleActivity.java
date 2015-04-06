package com.aloisandco.beautifuleasysummer.Article;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.AnimType;
import com.aloisandco.beautifuleasysummer.AnimatedActivity;
import com.aloisandco.beautifuleasysummer.AnimatedView;
import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.utils.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.utils.Constants;
import com.aloisandco.beautifuleasysummer.utils.FavoriteManager;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.HtmlTagHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by quentinmetzler on 24/03/15.
 */
public class ArticleActivity extends AnimatedActivity {
    private InterstitialAd mInterstitialAd;
    private TextView mTitleTextView;
    private WebView mArticleView;
    private CheckBox mCheckBox;

    int mArticleId;
    int mFavoritePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_article, 2);

        mTitleTextView = (TextView) findViewById(R.id.title);
        mArticleView = (WebView) findViewById(R.id.articleView);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);

        Bundle bundle = getIntent().getExtras();

        initTitleAndWebview(bundle);
        initInterstitialAd();

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

    @Override
    public void addAnimatedViews() {
        AnimatedView backgroundAnimatedView = new AnimatedView(findViewById(R.id.tropical_background), 0, 0, AnimType.ALPHA, 2);
        backgroundAnimatedView.setStartDelay(0);
        backgroundAnimatedView.setEndDelay(Constants.ANIMATION_DURATION / 2);

        AnimatedView articleAnimatedView = new AnimatedView(findViewById(R.id.articleView), 0, 0, AnimType.RESIZE_HEIGHT, 2);
        articleAnimatedView.setStartDelay(Constants.ANIMATION_DURATION / 2);
        articleAnimatedView.setEndDelay(0);

        animatedViews.add(backgroundAnimatedView);
        animatedViews.add(articleAnimatedView);

        // request interstitial after ANIMATION_DURATION
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                requestNewInterstitial();
            }
        }, Constants.ANIMATION_DURATION);

    }

    @Override
    protected void processSpecialCaseBeforeAnimation() {
        View view = ActivityTransitionManager.getInstance().getMenuListItemView();
        if (view != null) {
            view.setAlpha(0);
        }
    }

    @Override
    protected void processSpecialCaseAfterAnimation() {
        View view = ActivityTransitionManager.getInstance().getMenuListItemView();
        if (view != null) {
            view.setAlpha(1);
        }
    }

    private void requestNewInterstitial() {
        final AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("157ADC68E31F59407ADA80B42A0F1D73")
                .build();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInterstitialAd.loadAd(adRequest);
            }
        });
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
}
