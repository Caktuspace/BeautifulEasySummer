package com.aloisandco.beautifuleasysummer.Activity;

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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aloisandco.beautifuleasysummer.Enum.AnimType;
import com.aloisandco.beautifuleasysummer.View.AnimatedView;
import com.aloisandco.beautifuleasysummer.Adapter.MenuAdapter;
import com.aloisandco.beautifuleasysummer.R;
import com.aloisandco.beautifuleasysummer.Utils.Manager.BitmapCacheManager;
import com.aloisandco.beautifuleasysummer.Utils.Manager.ActivityTransitionManager;
import com.aloisandco.beautifuleasysummer.Utils.Constants;
import com.aloisandco.beautifuleasysummer.Utils.Manager.FontManager;
import com.aloisandco.beautifuleasysummer.Utils.HTML.HtmlTagHandler;
import com.aloisandco.beautifuleasysummer.Utils.UI.ScreenUtils;

import java.util.ArrayList;

public class MenuActivity extends AnimatedActivity {
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_menu, 2);

        initFont();
        initGridView();
    }

    /**
     * init the gridView Listeners to prevent it from moving and to know when an
     * item is touched so we can go to the next activity
     */
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
                // Prevent user from opening another activity during the animation
                mGridView.setEnabled(false);
                // Must do this as we can't pass view through intent
                // This will be used to make the animating view disappear and reappear so we don't
                // see it duplicate
                ActivityTransitionManager.getInstance().setMenuItemView(v);

                // get the resource id containing the info for the next activity
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

                // Initialize the animated view with the previous state so we can animate them later
                ArrayList<AnimatedView> dataList = new ArrayList<>();
                AnimatedView iconAnimatedView = new AnimatedView(iconScreenLocation[1],
                        iconScreenLocation[0],
                        imageView.getWidth(),
                        imageView.getHeight(),
                        R.id.icon, 2);
                AnimatedView textAnimatedView = new AnimatedView(textScreenLocation[1],
                        textScreenLocation[0],
                        textView.getWidth(),
                        textView.getHeight(),
                        R.id.title, 2);
                dataList.add(iconAnimatedView);
                dataList.add(textAnimatedView);

                // Start the next activity
                intent.
                        putExtra(Constants.PACKAGE_NAME + ".iconId", itemArray.getResourceId(0, 0)).
                        putExtra(Constants.PACKAGE_NAME + ".text", textView.getText()).
                        putExtra(Constants.PACKAGE_NAME + ".menuListArray", itemArray.length() > 2 ? itemArray.getResourceId(2, 0) : 0).
                        putParcelableArrayListExtra(Constants.PACKAGE_NAME + ".animatedViews", dataList);

                itemArray.recycle();
                startActivity(intent);
                // Cancel the default animation as we are providing our own
                overridePendingTransition(0, 0);
            }
        });
    }

    /**
     * Recalculate the dimension of each row of the gridView so that it redimension itself to be
     * contained within the screen without the need to be scrolled.
     * Must do that instead of gridLayout to keep the touch listener and the touch feedback.
     */
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

    /**
     * init the font to use the custom one with the Light and Bold one.
     */
    private void initFont() {
        FontManager fontManager = FontManager.getInstance(getAssets());
        TextView conseilsText = (TextView) findViewById(R.id.conseils);
        conseilsText.setTypeface(fontManager.ralewayLightFont);
        conseilsText.setText(Html.fromHtml(getResources().getString(R.string.conseils_pour_lete).toUpperCase(), null, new HtmlTagHandler()));
    }

    /**
     * Add animated views to the superview so that it can animate them
     */
    @Override
    protected void addAnimatedViews() {
        View leftLineView = findViewById(R.id.logo_left_line);
        AnimatedView leftLineAnimatedView = new AnimatedView(leftLineView, leftLineView.getWidth(), 0, AnimType.RESIZE_WIDTH, 2);
        leftLineAnimatedView.setStartDelay(Constants.ANIMATION_DURATION);
        leftLineAnimatedView.setEndDelay(0);
        AnimatedView rightLineAnimatedView = new AnimatedView(findViewById(R.id.logo_right_line), 0, 0, AnimType.RESIZE_WIDTH, 2);
        rightLineAnimatedView.setStartDelay(Constants.ANIMATION_DURATION);
        rightLineAnimatedView.setEndDelay(0);

        animatedViews.add(leftLineAnimatedView);
        animatedViews.add(rightLineAnimatedView);
    }

    /**
     * Make the background and the feet disappear of the screen as if we were going through
     * the hole
     */
    @Override
    protected void processSpecialCaseEnterAnimation() {
        final Bundle bundle = getIntent().getExtras();
        final ImageView backgroundView = (ImageView) findViewById(R.id.background);
        final ImageView feetView = (ImageView) findViewById(R.id.FeetView);

        Bitmap bitmap = BitmapCacheManager.getBitmap(bundle.getInt(Constants.PACKAGE_NAME + ".resourceId"));
        backgroundView.setImageBitmap(bitmap);
        backgroundView.setVisibility(View.VISIBLE);
        feetView.setVisibility(View.VISIBLE);
        ViewTreeObserver viewTreeObserver = backgroundView.getViewTreeObserver();
        // We need to wait for the background to have calculated its size
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                backgroundView.getViewTreeObserver().removeOnPreDrawListener(this);

                ImageView logoImageView = (ImageView) findViewById(R.id.logo);
                int[] screenLocation = new int[2];
                logoImageView.getLocationOnScreen(screenLocation);
                int prevLogoWidth = bundle.getInt(Constants.PACKAGE_NAME + ".logoWidth");
                int prevLogoHeight = bundle.getInt(Constants.PACKAGE_NAME + ".logoHeight");
                // Make the background scale up a lot from the center of the logo view as pivot
                backgroundView.setPivotX(screenLocation[0] + logoImageView.getWidth() / 2 * (logoImageView.getWidth() / prevLogoWidth));
                backgroundView.setPivotY(screenLocation[1] + logoImageView.getHeight() / 2 * (logoImageView.getHeight() / prevLogoHeight));
                backgroundView.animate().setDuration(Constants.ANIMATION_DURATION).
                        scaleX(7 - (logoImageView.getWidth() / prevLogoWidth)).scaleY(7 - (logoImageView.getHeight() / prevLogoHeight)).
                        translationX(backgroundView.getWidth() / 2 - (screenLocation[0] + logoImageView.getWidth() * (logoImageView.getWidth() / prevLogoWidth) / 2)).
                        translationY(backgroundView.getHeight() / 2 - (screenLocation[1] + logoImageView.getHeight() * (logoImageView.getHeight() / prevLogoHeight) / 2)).
                        setInterpolator(Constants.ACC_DEC_INTERPOLATOR).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        backgroundView.setVisibility(View.GONE);
                    }
                });

                // Make the feet scale up a lot from the center of the logo view as pivot
                feetView.animate().setDuration(Constants.ANIMATION_DURATION / 2).
                        scaleX(2 - (logoImageView.getWidth() / prevLogoWidth)).scaleY(2 - (logoImageView.getHeight() / prevLogoHeight)).
                        translationX(0).translationY(feetView.getHeight()).
                        setInterpolator(Constants.ACC_DEC_INTERPOLATOR).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        feetView.setVisibility(View.GONE);
                    }
                });

                return true;
            }
        });
    }

    /**
     * Nothing to do here as we don't need to animate the exit of this animation
     */
    @Override
    protected void processSpecialCaseExitAnimation() {
    }

    /**
     * Move the app in background if the user wants to leave it
     */
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
