package com.aloisandco.beautifuleasysummer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.aloisandco.beautifuleasysummer.utils.Constants;

/**
 * Created by quentinmetzler on 03/04/15.
 */
public class AnimatedView implements Parcelable {
    private int prevTop;
    private int prevLeft;
    private int prevWidth;
    private int prevHeight;
    private int nextResourceId;

    private int deltaTop;
    private int deltaLeft;
    private int pivotX;
    private int pivotY;
    private float scaleWidth;
    private float scaleHeight;

    private int startDelay;
    private int endDelay;
    private AnimType animType;

    private int nbAnimationStep;

    private View view;

    private String TAG = "AnimatedView";

    // Constructor
    public AnimatedView(int prevTop, int prevLeft, int prevWidth, int prevHeight, int nextResourceId, int animationStep){
        this.prevTop = prevTop;
        this.prevLeft = prevLeft;
        this.prevWidth = prevWidth;
        this.prevHeight = prevHeight;
        this.nextResourceId = nextResourceId;
        this.nbAnimationStep = animationStep;
    }

    public AnimatedView(View view, int pivot, int scale, AnimType animType, int nbAnimationStep) {
        switch (animType) {
            case RESIZE_WIDTH:
                this.pivotX = pivot;
                this.scaleWidth = scale;
                break;
            case RESIZE_HEIGHT:
                this.pivotY = pivot;
                this.scaleHeight = scale;
                break;
        }
        this.view = view;
        this.animType = animType;
        this.nbAnimationStep = nbAnimationStep;
    }

    // Parcelling part
    public AnimatedView(Parcel in){
        this.prevTop = in.readInt();
        this.prevLeft = in.readInt();
        this.prevWidth = in.readInt();
        this.prevHeight = in.readInt();
        this.nextResourceId = in.readInt();
        this.nbAnimationStep = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "writeToParcel..." + flags);
        dest.writeInt(prevTop);
        dest.writeInt(prevLeft);
        dest.writeInt(prevWidth);
        dest.writeInt(prevHeight);
        dest.writeInt(nextResourceId);
        dest.writeInt(nbAnimationStep);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public void animateIn() {
        switch (animType) {
            case ALPHA:
                view.setAlpha(0);
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).
                        alpha(1).setStartDelay(startDelay).setInterpolator(Constants.ACC_DEC_INTERPOLATOR);
                break;
            case MOVE_AND_SCALE:
                view.setPivotX(pivotX);
                view.setPivotY(pivotY);
                view.setScaleX(scaleWidth);
                view.setScaleY(scaleHeight);
                view.setTranslationX(deltaLeft);
                view.setTranslationY(deltaTop);
                // Animate scale and translation to go from thumbnail to full size
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).
                        scaleX(1).scaleY(1).
                        translationX(0).translationY(0).
                        setStartDelay(startDelay).setInterpolator(Constants.ACC_DEC_INTERPOLATOR);
                break;
            case RESIZE_WIDTH:
                view.setPivotX(pivotX);
                view.setScaleX(scaleWidth);
                // Animate scale and translation to go from thumbnail to full size
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).
                        scaleX(1).
                        setStartDelay(startDelay).setInterpolator(Constants.ACC_DEC_INTERPOLATOR);
                break;
            case RESIZE_HEIGHT:
                view.setPivotY(pivotY);
                view.setScaleY(scaleHeight);
                // Animate scale and translation to go from thumbnail to full size
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).
                        scaleY(1).
                        setStartDelay(startDelay).setInterpolator(Constants.ACC_DEC_INTERPOLATOR);
                break;
            default:
                break;
        }
    }

    public void animateOut() {
        switch (animType) {
            case ALPHA:
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).setStartDelay(endDelay).
                        setInterpolator(Constants.ACC_DEC_INTERPOLATOR).alpha(0);
                break;
            case MOVE_AND_SCALE:
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).setStartDelay(endDelay).
                        scaleX(scaleWidth).scaleY(scaleHeight).
                        translationX(deltaLeft).translationY(deltaTop).
                        setInterpolator(Constants.ACC_DEC_INTERPOLATOR);
                break;
            case RESIZE_WIDTH:
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).
                        scaleX(scaleHeight).
                        setStartDelay(endDelay).setInterpolator(Constants.ACC_DEC_INTERPOLATOR);
                break;
            case RESIZE_HEIGHT:
                view.animate().setDuration(Constants.ANIMATION_DURATION / nbAnimationStep).
                        scaleY(scaleHeight).
                        setStartDelay(endDelay).setInterpolator(Constants.ACC_DEC_INTERPOLATOR);
                break;
            default:
                break;
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AnimatedView createFromParcel(Parcel in) {
            return new AnimatedView(in);
        }

        public AnimatedView[] newArray(int size) {
            return new AnimatedView[size];
        }
    };

    public void setDeltaTop(int deltaTop) {
        this.deltaTop = deltaTop;
    }

    public void setDeltaLeft(int deltaLeft) {
        this.deltaLeft = deltaLeft;
    }

    public void setScaleWidth(float scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public void setScaleHeight(float scaleHeight) {
        this.scaleHeight = scaleHeight;
    }

    public void setStartDelay(int startDelay) {
        this.startDelay = startDelay;
    }

    public void setEndDelay(int endDelay) {
        this.endDelay = endDelay;
    }

    public void setAnimType(AnimType animType) {
        this.animType = animType;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setPivotX(int pivotX) {
        this.pivotX = pivotX;
    }

    public void setPivotY(int pivotY) {
        this.pivotY = pivotY;
    }

    public int getPrevTop() {
        return prevTop;
    }

    public int getPrevLeft() {
        return prevLeft;
    }

    public int getPrevWidth() {
        return prevWidth;
    }

    public int getPrevHeight() {
        return prevHeight;
    }

    public int getNextResourceId() {
        return nextResourceId;
    }
}
