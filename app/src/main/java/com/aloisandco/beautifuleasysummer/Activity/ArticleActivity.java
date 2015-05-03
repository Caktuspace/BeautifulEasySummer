package com.aloisandco.beautifuleasysummer.Activity;

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

import com.aloisandco.beautifuleasysummer.Enum.AnimType;
import com.aloisandco.beautifuleasysummer.View.AnimatedView;
import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.Utils.Manager.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.Utils.Constants;
import com.aloisandco.beautifuleasysummer.Utils.Manager.FavoriteManager;
import com.aloisandco.beautifuleasysummer.Utils.Manager.FontManager;
import com.aloisandco.beautifuleasysummer.Utils.HTML.HtmlTagHandler;
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

    /**
     * Init the title and the content of the webview from the article passed by the bundle
     * @param bundle the bundle containing the article id
     */
    public void initTitleAndWebview(Bundle bundle) {
        mArticleId = bundle.getInt(Constants.PACKAGE_NAME + ".itemResourceId");
        TypedArray itemArray = getResources().obtainTypedArray(mArticleId);
        String title = itemArray.getString(0);
        String articleUrl = itemArray.getString(1);
        itemArray.recycle();

        FontManager fontManager = FontManager.getInstance(getAssets());
        mTitleTextView.setTypeface(fontManager.ralewayLightFont);
        mTitleTextView.setText(Html.fromHtml(title, null, new HtmlTagHandler()));

        mArticleView.loadUrl(articleUrl);
        mArticleView.setBackgroundColor(Color.TRANSPARENT);
        final WebSettings webSettings = mArticleView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setDefaultFontSize(18);
    }

    /**
     * Init the Interstitial ad and register a listener to know when it's closed
     * so when can animate the finish of the activity
     */
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

    /**
     * Add and remove article from the favorite when the sun is pressed
     */
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

    /**
     * Add animated views to the superview so that it can animate them
     */
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

    /**
     * Make the moving view disappear from the previous activity
     */
    @Override
    protected void processSpecialCaseEnterAnimation() {
        final View view = ActivityTransitionManager.getInstance().getMenuListItemView();
        if (view != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setAlpha(0);
                }
            });
        }
    }

    /**
     * Make the moving view reappear in the previous activity
     */
    @Override
    protected void processSpecialCaseExitAnimation() {
        final View view = ActivityTransitionManager.getInstance().getMenuListItemView();
        if (view != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setAlpha(1);
                }
            });
        }
    }

    /**
     * Ask the server a new ad to be displayed when the user finish reading an article
     */
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
     * Show the interstitial ad if it is loaded else directly close the activity
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
