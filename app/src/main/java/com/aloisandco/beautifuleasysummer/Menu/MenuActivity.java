package com.aloisandco.beautifuleasysummer.Menu;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aloisandco.beautifuleasysummer.MenuList.MenuListActivity;
import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.utils.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.utils.BitmapCacheUtils;
import com.aloisandco.beautifuleasysummer.utils.Constants;
import com.aloisandco.beautifuleasysummer.utils.FontManager;
import com.aloisandco.beautifuleasysummer.utils.HtmlTagHandler;
import com.aloisandco.beautifuleasysummer.utils.ScreenUtils;

public class MenuActivity extends Activity {

    private static final TimeInterpolator sAccDecc = new AccelerateDecelerateInterpolator();
    private static final int ANIM_DURATION = 400;

    int mLeftDeltaLogo;
    int mTopDeltaLogo;
    float mWidthScaleLogo;
    float mHeightScaleLogo;
    private ImageView mLogoImageView;
    private ImageView mBackgroundView;
    private ImageView mFeetView;
    private ImageView mLeftLineImageView;
    private ImageView mRightLineImageView;
    private GridView mGridView;

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

        if (savedInstanceState == null) {
            calculateAnimationOffset(bundle);
        }

        initGridView();
    }

    private void calculateAnimationOffset(Bundle bundle) {
        Bitmap bitmap = BitmapCacheUtils.getBitmap(bundle.getInt(Constants.PACKAGE_NAME + ".resourceId"));
        final int logoTop = bundle.getInt(Constants.PACKAGE_NAME + ".top");
        final int logoLeft = bundle.getInt(Constants.PACKAGE_NAME + ".left");
        final int logoWidth = bundle.getInt(Constants.PACKAGE_NAME + ".width");
        final int logoHeight = bundle.getInt(Constants.PACKAGE_NAME + ".height");
        mBackgroundView.setImageBitmap(bitmap);
        mBackgroundView.setVisibility(View.VISIBLE);
        mFeetView.setVisibility(View.VISIBLE);
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

    private  void initGridView() {
        mGridView = (GridView) findViewById(R.id.gridview);
        redrawGridView();

        mGridView.setAdapter(new MenuAdapter(this));
        mGridView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mGridView.setEnabled(false);
                ActivityTransitionManager.getInstance().setMenuItemView(v);

                TypedArray menuArray = getResources().obtainTypedArray(R.array.menu);
                TypedArray itemArray = getResources().obtainTypedArray(menuArray.getResourceId(position, 0));
                menuArray.recycle();
                ImageView imageView = (ImageView) v.findViewById(R.id.icon);
                TextView textView = (TextView) v.findViewById(R.id.text);
                int[] iconScreenLocation = new int[2];
                imageView.getLocationOnScreen(iconScreenLocation);
                int[] textScreenLocation = new int[2];
                textView.getLocationOnScreen(textScreenLocation);
                Intent intent = new Intent(MenuActivity.this, MenuListActivity.class);
                intent.
                        putExtra(Constants.PACKAGE_NAME + ".iconId", itemArray.getResourceId(0, 0)).
                        putExtra(Constants.PACKAGE_NAME + ".text", textView.getText()).
                        putExtra(Constants.PACKAGE_NAME + ".menuListArray", itemArray.length() > 2?itemArray.getResourceId(2, 0):0).
                        putExtra(Constants.PACKAGE_NAME + ".leftIcon", iconScreenLocation[0]).
                        putExtra(Constants.PACKAGE_NAME + ".topIcon", iconScreenLocation[1]).
                        putExtra(Constants.PACKAGE_NAME + ".widthIcon", imageView.getWidth()).
                        putExtra(Constants.PACKAGE_NAME + ".heightIcon", imageView.getHeight()).
                        putExtra(Constants.PACKAGE_NAME + ".leftText", textScreenLocation[0]).
                        putExtra(Constants.PACKAGE_NAME + ".topText", textScreenLocation[1]).
                        putExtra(Constants.PACKAGE_NAME + ".widthText", textView.getWidth()).
                        putExtra(Constants.PACKAGE_NAME + ".heightText", textView.getHeight());
                itemArray.recycle();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void redrawGridView() {
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeGridHeight);
        ViewTreeObserver observer = relativeLayout.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                relativeLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                TypedArray typedArray = getResources().obtainTypedArray(R.array.menu);
                int padding = ScreenUtils.valueToDpi(getResources(), 10);

                for (int i = 0; i < typedArray.length(); i++) {
                    View view = mGridView.getChildAt(i);
                    if (view != null) {
                        ViewGroup.LayoutParams lp = view.getLayoutParams();
                        lp.height = (relativeLayout.getHeight() - (padding * (typedArray.length() / 2))) / (typedArray.length() / 2);
                        view.setLayoutParams(lp);
                    }
                }

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGridView.setEnabled(true);
        redrawGridView();
    }

    private void initFont() {
        FontManager fontManager = FontManager.getInstance(getAssets());
        TextView conseilsText = (TextView) findViewById(R.id.conseils);
        conseilsText.setTypeface(fontManager.ralewayLightFont);
        conseilsText.setText(Html.fromHtml(getResources().getString(R.string.conseils_pour_lete).toUpperCase(), null, new HtmlTagHandler()));
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the pictue is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        animateLogoToDefaultSize();
        animateAndHideViewFromHome();
    }

    private void animateLogoToDefaultSize() {
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
