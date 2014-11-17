package com.johnston.lmhapp.Settings;

        import android.view.View;
        import android.view.animation.Animation;
        import android.view.animation.Transformation;

/**
 * Created by Tom on 17/11/2014.
 */
public class ChangeHeightAnimation extends Animation {
    int originalHeight;
    View view;
    int finalHeight;

    public ChangeHeightAnimation(View view, int originalHeight, int finalHeight) {
        this.view = view;
        this.originalHeight = originalHeight;
        this.finalHeight = finalHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        newHeight = (int)(originalHeight-(originalHeight-finalHeight)*interpolatedTime);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

