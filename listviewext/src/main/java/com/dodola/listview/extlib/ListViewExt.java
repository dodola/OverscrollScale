package com.dodola.listview.extlib;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.List;

public class ListViewExt extends ListView {
    private static final String TAG = ListViewExt.class.getName();
    protected boolean mIsShortList;
    protected int mOffsetY;
    protected AnimatorSet mAnimatorSet;
    protected float mLastPivotY;
    protected boolean mScaleYDirty;
    protected int mLastY;
    protected int mInertia;
    protected int mDownMotionY;
    protected boolean mIsTouching;


    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
        super.onTouchModeChanged(isInTouchMode);
    }


    public ListViewExt(Context context) {
        super(context);
        init();
    }

    public ListViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListViewExt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
    public boolean onTouchEvent(MotionEvent ev) {

        int y = (int) ev.getY();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN://0
            case MotionEvent.ACTION_POINTER_DOWN:
                ListViewEnhance.onTouchDown(this, ev);
                this.mDownMotionY = (y - (this.mLastY - this.mDownMotionY));
                this.mLastY = y;
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                this.mLastY = y;
                if (this.mIsTouching) {
                    this.mIsTouching = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (this.mIsTouching) {
                    this.mIsTouching = false;
                    ListViewEnhance.resetScale(this);
                }
                break;
            case MotionEvent.ACTION_MOVE: {
                int offset = (y - this.mDownMotionY);

                if (!this.mIsTouching) {
                    ListViewEnhance.onTouchDown(this, ev);
                }
                this.mInertia = y - mDownMotionY;

                if (ListViewEnhance.needListScale(this, offset)) {
                    this.mLastY = y;
                    return true;
                } else {
                    if (y != this.mLastY) {
                        this.mLastY = y;
                    }
                }
            }
            break;
        }

        return super.onTouchEvent(ev);
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (canvas != null) {
            ListViewEnhance.onRenderTick(this, canvas);
        }
    }
}
