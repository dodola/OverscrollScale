package com.dodola.listview.extlib;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class RecyclerViewExt extends RecyclerView {

    protected int mDownMotionY;
    private int mLastY;
    protected boolean mIsTouching;
    protected int mInertia;
    protected int mOffsetY;
    protected AnimatorSet mAnimatorSet;
    public boolean mIsShortList;
    public float mLastPivotY;
    public boolean mScaleYDirty;


    public RecyclerViewExt(Context context) {
        super(context);
        init();
    }

    public RecyclerViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        this.setFadingEdgeLength(0);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        int y = (int) e.getY();
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                RecyclerViewEnhance.onTouchDown(this, e);
                this.mDownMotionY = y - (this.mLastY - this.mDownMotionY);
                this.mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                this.mLastY = y;
                if (this.mIsTouching) {
                    this.mIsTouching = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (this.mIsTouching) {
                    this.mIsTouching = false;
                    RecyclerViewEnhance.resetScale(this);
                }
                break;
            case MotionEvent.ACTION_MOVE: {
                int offset = y - this.mDownMotionY;

                if (!this.mIsTouching) {
                    RecyclerViewEnhance.onTouchDown(this, e);
                }

                this.mInertia = y - mDownMotionY;

                if (RecyclerViewEnhance.needListScale(this, offset)) {
                    this.mLastY = y;
                    return true;
                } else {
                    if (y != this.mLastY) {
                        this.mLastY = y;
                    }
                }
//                Log.d("mmga", "1.mInertia = " + mInertia +
//                        " mOffsetY = " + mOffsetY +
//                        " mDownMotionY= " + mDownMotionY);
            }
            break;

        }
        return super.onTouchEvent(e);
    }


    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (canvas != null) {
            RecyclerViewEnhance.onRenderTick(this, canvas);
        }

    }

}

