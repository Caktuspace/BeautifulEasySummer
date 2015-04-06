package com.aloisandco.beautifuleasysummer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.aloisandco.beautifuleasysummer.utils.Constants;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by quentinmetzler on 03/04/15.
 */
public abstract class AnimatedActivity extends Activity {
    protected Boolean mCloseInProgress;
    protected ArrayList<AnimatedView> animatedViews = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState, int layoutId, int animationStep) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        mCloseInProgress = false;
        if (savedInstanceState == null) {
            initAnimatedViews(getIntent().getExtras(), animationStep);
        }
    }

    private void initAnimatedViews(Bundle bundle, final int animationStep) {
        final ArrayList<AnimatedView> animatedViews1 = bundle.getParcelableArrayList(Constants.PACKAGE_NAME + ".animatedViews");
        if (animatedViews1.size() > 0) {
            final View view = findViewById(animatedViews1.get(0).getNextResourceId());
            ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    processSpecialCaseBeforeAnimation();

                    for (AnimatedView animatedView : animatedViews1) {
                        View newView = findViewById(animatedView.getNextResourceId());
                        animatedView.setView(newView);
                        int[] screenLocation = new int[2];
                        newView.getLocationOnScreen(screenLocation);
                        animatedView.setDeltaLeft(animatedView.getPrevLeft() - screenLocation[0]);
                        animatedView.setDeltaTop(animatedView.getPrevTop() - screenLocation[1]);
                        // Scale factors to make the large version the same size as the thumbnail
                        animatedView.setScaleWidth((float) animatedView.getPrevWidth() / newView.getWidth());
                        animatedView.setScaleHeight((float) animatedView.getPrevHeight() / newView.getHeight());
                        animatedView.setPivotX(0);
                        animatedView.setPivotY(0);
                        animatedView.setStartDelay(0);
                        animatedView.setEndDelay(animationStep == 0 ? 0 : (Constants.ANIMATION_DURATION - (Constants.ANIMATION_DURATION / animationStep)));
                        animatedView.setView(newView);
                        animatedView.setAnimType(AnimType.MOVE_AND_SCALE);
                        animatedViews.add(animatedView);
                    }

                    addAnimatedViews();

                    runEnterAnimation();

                    return true;
                }
            });
        }

    }

    abstract protected void addAnimatedViews();
    abstract protected void processSpecialCaseBeforeAnimation();
    abstract protected void processSpecialCaseAfterAnimation();

    private void runEnterAnimation() {
        for (AnimatedView animatedView : animatedViews) {
            animatedView.animateIn();
        }
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     */
    protected void runExitAnimation() {
        mCloseInProgress = true;
        for (AnimatedView animatedView : animatedViews) {
            animatedView.animateOut();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                processSpecialCaseAfterAnimation();
                finish();
            }
        }, Constants.ANIMATION_DURATION);
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        if (!mCloseInProgress) {
            runExitAnimation();
        }
    }

    @Override
    public void finish() {
        super.finish();
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }
}
