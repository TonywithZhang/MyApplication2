package com.tec.zhang.prv;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by zhang on 2017/2/25.
 */

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private static final android.view.animation.Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;
    public ScrollingFABBehavior(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
         || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild,target,nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if(dyConsumed>0&&!this.mIsAnimatingOut&&child.getVisibility()==View.VISIBLE){
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child);
            }else if(dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child);
            }

    }

    private void animateIn(FloatingActionButton child) {
        child.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
             ViewCompat.animate(child).translationY(0)
            .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
            .start();
            }else{}
    }

    private void animateOut(FloatingActionButton child) {

        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(child).translationY(child.getHeight() + getMarginBottom(child)).setInterpolator(INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                    ScrollingFABBehavior.this.mIsAnimatingOut = true;
                }

                @Override
                public void onAnimationEnd(View view) {
                    ScrollingFABBehavior.this.mIsAnimatingOut = false;
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(View view) {
                    ScrollingFABBehavior.this.mIsAnimatingOut = false;
                }
            }).start();
        }
    }
    private int getMarginBottom(View v) {
         int marginBottom = 0;
         final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
         if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
             marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
             }
         return marginBottom;
         }
}
