package com.tec.zhang.prv;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * 滑动行为，向下滑时隐藏按键，向上显示
 *
 */

public class Behavior extends CoordinatorLayout.Behavior<FloatingActionButton>{
    private static  final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean isAnimatingOut;
    public Behavior(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE && !isAnimatingOut){
            animateOut(child);
        }else if(dyConsumed < 0 && child.getVisibility() != View.VISIBLE){
            animateIn(child);
        }
    }
    private void animateOut(final View button){
        ViewCompat.animate(button).translationY(button.getHeight() + getMarginBottom(button)).setInterpolator(INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                isAnimatingOut = true;
            }

            @Override
            public void onAnimationEnd(View view) {
                isAnimatingOut = false;
                button.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(View view) {
                isAnimatingOut = false;

            }
        }).start();
    }

    private void animateIn(View view){
        view.setVisibility(View.VISIBLE);
        ViewCompat.animate(view).translationY(0).setInterpolator(INTERPOLATOR).withLayer().setListener(null).start();
        view.bringToFront();
    }

    private int getMarginBottom(View button) {
        int marginBottom = 0;
        final ViewGroup.LayoutParams lp = button.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams){
            marginBottom  = ((ViewGroup.MarginLayoutParams) lp).bottomMargin;
        }
        return marginBottom;
    }
}
