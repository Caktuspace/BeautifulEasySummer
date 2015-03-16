package com.aloisandco.beautifuleasysummer.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by quentinmetzler on 15/03/15.
 */
public class ImageViewUtils {
    /**
     * Retrieve the factor use to scale the bitmap in the view
     * @param view the view we want to know how it factor
     * @return
     */
    static public float getScaleXFromImageView(ImageView view) {
        Matrix m = view.getImageMatrix();
        float[] values = new float[9];
        m.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * Convert a coordinate from a view to the bitmap associated
     * @param position the coordinate on the view that will be replaced by the coordinate on the bitmap
     * @param imageView the image view we want the bitmap coordinate from
     */
    static public void convertPositionOnScreenToImageView(float[] position, ImageView imageView) {
        Matrix matrix = new Matrix();
        imageView.getImageMatrix().invert(matrix);
        matrix.postTranslate(imageView.getScrollX(), imageView.getScrollY());
        matrix.mapPoints(position);
    }

    /**
     * Create a hole in the backgroundView so we can see the circleView
     * @param backgroundView the view where we want to make the hole
     * @param circleView the view we want to see behind the other view
     * @param window the window in which everything takes place
     * @return the bitmap of the backgroundView with the hole in it
     */
    static public Bitmap createCircleInImageViewFromSizeOfOtherImageView(ImageView backgroundView, ImageView circleView, Window window) {
        Bitmap backgroundBitmap = ((BitmapDrawable)backgroundView.getDrawable()).getBitmap();
        Bitmap circleBitmap = ((BitmapDrawable)circleView.getDrawable()).getBitmap();

        // get the radius from the circle bitmap scaled to the background bitmap
        float backgroundScaleX = getScaleXFromImageView(backgroundView);
        float circleScaleX = getScaleXFromImageView(circleView);
        float radius = (float) (circleBitmap.getWidth() / 2.0) * (circleScaleX / backgroundScaleX);

        // transpose the position of the circleView in the screen to the position in the background bitmap
        float[] position = ViewUtils.getCenterPositionOfViewOnScreen(circleView, window);
        convertPositionOnScreenToImageView(position, backgroundView);

        // create a duplicate of the current bitmap
        Bitmap bitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // draw it on the canvas
        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        // clear the circle on the canvas
        canvas.drawCircle(position[0], position[1], radius, paint);

        return bitmap;
    }

    static public void animateImageViewToWidth(ImageView imageView, int width, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int widthDP = (int) (width * scale + 0.5f);
        ResizeAnimation animation = new ResizeAnimation(imageView, imageView.getWidth(), imageView.getHeight(), widthDP, imageView.getHeight());
        imageView.startAnimation(animation);

    }
}
