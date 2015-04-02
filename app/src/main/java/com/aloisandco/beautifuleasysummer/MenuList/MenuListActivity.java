package com.aloisandco.beautifuleasysummer.MenuList;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aloisandco.beautifuleasysummer.Article.ArticleActivity;
import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.utils.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.utils.Constants;
import com.aloisandco.beautifuleasysummer.utils.FavoriteManager;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.ScreenUtils;

/**
 * Created by quentinmetzler on 18/03/15.
 */
public class MenuListActivity extends Activity {
    private static final TimeInterpolator sAccDecc = new AccelerateDecelerateInterpolator();
    private static final int ANIM_DURATION = 250;

    private ImageView mIconImageView;
    private TextView mTitleTextView;
    private ImageView mBackgroundImageView;
    private ListView mListView;
    private View mDivider;
    private Boolean mCloseInProgress;
    private MenuListAdapter mAdapter;
    private View mLastClickedView;
    private int mLastClickedResourceId;
    private int mLastClickedIndex;

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

        mCloseInProgress = false;
        mIconImageView = (ImageView) findViewById(R.id.icon);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mBackgroundImageView = (ImageView) findViewById(R.id.tropical_background);
        mListView = (ListView) findViewById(R.id.listView);
        mDivider = findViewById(R.id.divider);

        initFont();

        Bundle bundle = getIntent().getExtras();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), bundle.getInt(Constants.PACKAGE_NAME + ".iconId"));
        String title = bundle.getString(Constants.PACKAGE_NAME + ".text");

        mIconImageView.setImageBitmap(icon);
        mTitleTextView.setText(title);

        final int iconTop = bundle.getInt(Constants.PACKAGE_NAME + ".topIcon");
        final int iconLeft = bundle.getInt(Constants.PACKAGE_NAME + ".leftIcon");
        final int iconWidth = bundle.getInt(Constants.PACKAGE_NAME + ".widthIcon");
        final int iconHeight = bundle.getInt(Constants.PACKAGE_NAME + ".heightIcon");
        final int titleTop = bundle.getInt(Constants.PACKAGE_NAME + ".topText");
        final int titleLeft = bundle.getInt(Constants.PACKAGE_NAME + ".leftText");
        final int titleWidth = bundle.getInt(Constants.PACKAGE_NAME + ".widthText");
        final int titleHeight = bundle.getInt(Constants.PACKAGE_NAME + ".heightText");
        final int menuListArrayResourceId = bundle.getInt(Constants.PACKAGE_NAME + ".menuListArray");

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

        mAdapter = new MenuListAdapter(this, menuListArrayResourceId);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setEnabled(false);
                ActivityTransitionManager.getInstance().setMenuListItemView(view);

                int resourceId;
                if(menuListArrayResourceId == 0) {
                    resourceId = FavoriteManager.getArticleAtPosition(position, MenuListActivity.this);
                    mLastClickedView = view;
                    mLastClickedResourceId = resourceId;
                    mLastClickedIndex = position;
                } else {
                    TypedArray menuListArray = getResources().obtainTypedArray(menuListArrayResourceId);
                    resourceId = menuListArray.getResourceId(position, 0);
                    menuListArray.recycle();
                }

                TextView textView = (TextView) view.findViewById(R.id.textView);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.favorite);
                int[] viewScreenLocation = new int[2];
                view.getLocationOnScreen(viewScreenLocation);
                int[] textScreenLocation = new int[2];
                textView.getLocationOnScreen(textScreenLocation);
                int[] checkboxScreenLocation = new int[2];
                checkBox.getLocationOnScreen(checkboxScreenLocation);
                Intent intent = new Intent(MenuListActivity.this, ArticleActivity.class);
                intent.
                        putExtra(Constants.PACKAGE_NAME + ".itemResourceId", resourceId).
                        putExtra(Constants.PACKAGE_NAME + ".leftDivider", viewScreenLocation[0] + ScreenUtils.valueToDpi(getResources(), 20)).
                        putExtra(Constants.PACKAGE_NAME + ".topDivider", viewScreenLocation[1] + view.getHeight()).
                        putExtra(Constants.PACKAGE_NAME + ".widthDivider", view.getWidth() - ScreenUtils.valueToDpi(getResources(), 40)).
                        putExtra(Constants.PACKAGE_NAME + ".heightDivider", ScreenUtils.valueToDpi(getResources(), 1)).
                        putExtra(Constants.PACKAGE_NAME + ".leftText", textScreenLocation[0]).
                        putExtra(Constants.PACKAGE_NAME + ".topText", textScreenLocation[1]).
                        putExtra(Constants.PACKAGE_NAME + ".widthText", textView.getWidth()).
                        putExtra(Constants.PACKAGE_NAME + ".heightText", textView.getHeight()).
                        putExtra(Constants.PACKAGE_NAME + ".leftCheckbox", checkboxScreenLocation[0]).
                        putExtra(Constants.PACKAGE_NAME + ".topCheckbox", checkboxScreenLocation[1]).
                        putExtra(Constants.PACKAGE_NAME + ".widthCheckbox", checkBox.getWidth()).
                        putExtra(Constants.PACKAGE_NAME + ".heightCheckbox", checkBox.getHeight());
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListView.setEnabled(true);
        if (mLastClickedView != null) {
            if (!FavoriteManager.isArticleFavorite(mLastClickedResourceId, this)) {
                FavoriteManager.addArticleToFavoriteAtPosition(mLastClickedResourceId, mLastClickedIndex, this);
                mAdapter.deleteCell(mLastClickedView, mLastClickedResourceId);
            }
            mLastClickedView = null;
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initFont() {
        FontManager fontManager = FontManager.getInstance(getAssets());
        mTitleTextView.setTypeface(fontManager.ralewayMediumFont);
    }

    public void runEnterAnimation() {
        animateIconToDefaultSize();
        animateTitleToDefaultSize();
        if (ActivityTransitionManager.getInstance().getMenuItemView() != null) {
            ActivityTransitionManager.getInstance().getMenuItemView().setAlpha(0);
        }
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
        mIconImageView.animate().setDuration(ANIM_DURATION).
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
        mDivider.setAlpha(0);
        mListView.setPivotY(0);
        mListView.setScaleY(0);
        mDivider.animate().setDuration(ANIM_DURATION).setInterpolator(sAccDecc).alpha(1).withEndAction(new Runnable() {
            @Override
            public void run() {
                mListView.animate().scaleY(1).setInterpolator(sAccDecc).setDuration(ANIM_DURATION);
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
        mCloseInProgress = true;
        mListView.animate().scaleY(0).setInterpolator(sAccDecc).setDuration(ANIM_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                mIconImageView.animate().setDuration(ANIM_DURATION).
                        scaleX(mWidthScaleIcon).scaleY(mHeightScaleIcon).
                        translationX(mLeftDeltaIcon).translationY(mTopDeltaIcon).
                        setInterpolator(sAccDecc);

                mTitleTextView.animate().setDuration(ANIM_DURATION).
                        scaleX(mWidthScaleTitle).scaleY(mHeightScaleTitle).
                        translationX(mLeftDeltaTitle).translationY(mTopDeltaTitle).
                        setInterpolator(sAccDecc).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityTransitionManager.getInstance().getMenuItemView() != null) {
                            ActivityTransitionManager.getInstance().getMenuItemView().setAlpha(1);
                        }
                        endAction.run();
                    }
                });

                mBackgroundImageView.animate().setDuration(ANIM_DURATION).setInterpolator(sAccDecc).alpha(0);
                mDivider.animate().setDuration(ANIM_DURATION).setInterpolator(sAccDecc).alpha(0);
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
            runExitAnimation(new Runnable() {
                public void run() {
                    // *Now* go ahead and exit the activity
                    finish();
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }
}
