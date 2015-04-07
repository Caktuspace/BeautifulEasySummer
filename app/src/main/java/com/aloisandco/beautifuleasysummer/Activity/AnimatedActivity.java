package com.aloisandco.beautifuleasysummer.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.aloisandco.beautifuleasysummer.Enum.AnimType;
import com.aloisandco.beautifuleasysummer.View.AnimatedView;
import com.aloisandco.beautifuleasysummer.Utils.Constants;

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
        // We are not in the closing animation
        mCloseInProgress = false;
        if (savedInstanceState == null) {
            initAnimatedViews(getIntent().getExtras(), animationStep);
        }
    }

    /**
     * Complete the previously initialized animated views with the new value so we can animate the
     * opening of the activity
     * @param bundle bundle containing the previously initialized animated views
     * @param animationStep number of steps it takes to finish the animation
     */
    private void initAnimatedViews(Bundle bundle, final int animationStep) {
        final ArrayList<AnimatedView> animatedViews1 = bundle.getParcelableArrayList(Constants.PACKAGE_NAME + ".animatedViews");
        if (animatedViews1.size() > 0) {
            final View view = findViewById(animatedViews1.get(0).getNextResourceId());
            ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    processSpecialCaseEnterAnimation();

                    // Set the new position, scale and the view for each animated view
                    for (AnimatedView animatedView : animatedViews1) {
                        View newView = findViewById(animatedView.getNextResourceId());
                        animatedView.setView(newView);
                        int[] screenLocation = new int[2];
                        newView.getLocationOnScreen(screenLocation);
                        // Calculate difference between previous position and current one
                        animatedView.setDeltaLeft(animatedView.getPrevLeft() - screenLocation[0]);
                        animatedView.setDeltaTop(animatedView.getPrevTop() - screenLocation[1]);
                        // Calculate difference between previous size and current one
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

    /**
     *  subclasses should add the other animation it wants to perform here
     */
    abstract protected void addAnimatedViews();

    /**
     *  subclasses should add specific behavior before the enter animation occur in this method
     */
    abstract protected void processSpecialCaseEnterAnimation();

    /**
     *  subclasses should add specific behavior after the exit animation end in this method
     */
    abstract protected void processSpecialCaseExitAnimation();

    /**
     *  Animates all the animated views previously added
     */
    private void runEnterAnimation() {
        for (AnimatedView animatedView : animatedViews) {
            animatedView.animateIn();
        }
    }

    /**
     *  Animated all the animated views previously added in reverse and finish the activity
     */
    protected void runExitAnimation() {
        // Prevent the user to cancel the animation by pressing the back button repeatedly
        mCloseInProgress = true;
        for (AnimatedView animatedView : animatedViews) {
            animatedView.animateOut();
        }

        // start a timer to finish the activity when the animation is done
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                processSpecialCaseExitAnimation();
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
