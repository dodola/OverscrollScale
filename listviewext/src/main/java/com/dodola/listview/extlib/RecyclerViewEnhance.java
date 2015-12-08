package com.dodola.listview.extlib;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.animation.Interpolator;


public class RecyclerViewEnhance {


    public static class BackEaseOutInterpolator implements Interpolator {
        public float overshot = 0.0f;

        public float getInterpolation(float t) {
            float s = this.overshot == 0.0f ? 1.70158f : this.overshot;
            t -= 1;
            return ((t * t) * (((s + 1) * t) + s)) + 1;
        }
    }

    public static class CircEaseOutInterpolator implements Interpolator {
        public float getInterpolation(float input) {
            input -= 1;
            return (float) Math.sqrt((double) (1 - (input * input)));
        }
    }

    public static void onTouchDown(RecyclerViewExt recyclerView, MotionEvent event) {
        recyclerView.mIsTouching = true;
        recyclerView.mOffsetY = 0;
        recyclerView.mInertia = 0;
        recyclerView.mDownMotionY = (int) event.getY();
    }

    public static void resetScale(RecyclerViewExt recyclerView) {
        recyclerView.mInertia = 0;
        recyclerView.setScaleY(1);
        recyclerView.invalidate();
    }

    public static boolean needListScale(RecyclerViewExt recyclerView, int offset) {

        if (recyclerView.mAnimatorSet != null && recyclerView.mAnimatorSet.isRunning()) {
            return true;
        }
        boolean atEdge = isScrollAtEdge(recyclerView, offset);
        if (atEdge) {
            recyclerView.mInertia = offset - recyclerView.mOffsetY;
            recyclerView.mScaleYDirty = true;
            recyclerView.invalidate();
        }

        boolean needScale = (recyclerView.mInertia != 0) && atEdge;
        return needScale;
    }

    public static void onRenderTick(RecyclerViewExt recyclerView, Canvas canvas) {
        if (recyclerView.mAnimatorSet != null && recyclerView.mAnimatorSet.isRunning()) {
            setListScale(recyclerView, canvas, 0, false);
        } else if (recyclerView.mIsTouching) {
            if (recyclerView.mScaleYDirty || recyclerView.getScaleY() != 1) {
                setListScale(recyclerView, canvas, recyclerView.mInertia, false);
                recyclerView.mScaleYDirty = false;
            }
        } else if (isScrollAtEdge(recyclerView, recyclerView.mInertia)) {
            setListScale(recyclerView, canvas, recyclerView.mInertia, true);
            recyclerView.mInertia = 0;
        } else {
            recyclerView.mInertia = (int) (recyclerView.mInertia * 0.98f);
            if (recyclerView.mInertia == 0) {
                recyclerView.setScaleY(1);
            }
        }
    }


    private static void setListScale(final RecyclerViewExt recyclerView, Canvas canvas, int offset, boolean isTween) {
        if (offset == 0) {
            canvas.scale(1, recyclerView.getScaleY(), 0, recyclerView.mLastPivotY);
            recyclerView.invalidate();
            return;
        }
        double scaleRatio = Math.min(Math.max(0.0d, (Math.sqrt(Math.abs(offset)) * 3.0d) * 0.001d), 0.1d);
        if (recyclerView.mIsShortList && offset < 0) {
            scaleRatio = -scaleRatio;
        }
        float scaleY = (float) (1 + scaleRatio);
        if (offset > 0 || recyclerView.mIsShortList) {
            recyclerView.mLastPivotY = 0.0f;
        } else {
            recyclerView.mLastPivotY = recyclerView.getHeight();
        }
        recyclerView.setPivotX(0);
        recyclerView.setPivotY(recyclerView.mLastPivotY);
        if (isTween) {
            if (recyclerView.getScaleY() != 1) {
                canvas.scale(1, recyclerView.getScaleY(), 0, recyclerView.mLastPivotY);
                recyclerView.invalidate();
            }
            if (recyclerView.mAnimatorSet != null) {
                recyclerView.mAnimatorSet.cancel();
            }
            recyclerView.mAnimatorSet = new AnimatorSet();
            ValueAnimator scaleBackAnimator = ValueAnimator.ofFloat(scaleY, 1);
            scaleBackAnimator.setDuration(400);
            scaleBackAnimator.setInterpolator(new BackEaseOutInterpolator());
            scaleBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    recyclerView.setScaleY((Float) animation.getAnimatedValue());
                    recyclerView.invalidate();
                }
            });
            if (recyclerView.getScaleY() == 1) {
                ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1, scaleY);
                scaleAnimator.setDuration(200);
                scaleAnimator.setInterpolator(new CircEaseOutInterpolator());
                scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        recyclerView.setScaleY((Float) animation.getAnimatedValue());
                        recyclerView.invalidate();
                    }
                });
                recyclerView.mAnimatorSet.play(scaleAnimator).before(scaleBackAnimator);
            } else {
                recyclerView.mAnimatorSet.play(scaleBackAnimator);
            }
            recyclerView.mAnimatorSet.start();
            return;
        }
        canvas.scale(1, scaleY, 0, recyclerView.mLastPivotY);
        recyclerView.setScaleY(scaleY);
        recyclerView.invalidate();
    }


    private static boolean isScrollAtEdge(RecyclerViewExt recyclerView, int offset) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            boolean isScrollAtTop;
            boolean isScrollAtBottom;
            boolean isShortList;
            int firstPosition = 0;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                firstPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else {
                firstPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

            }


            int firstTop = recyclerView.getChildAt(0).getTop();
            int lastBottom = recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom();
            int itemCount = recyclerView.getAdapter() != null ? recyclerView.getAdapter().getItemCount() : 0;

            if (firstPosition != 0 || firstTop < recyclerView.getPaddingTop()) {
                isScrollAtTop = false;
            } else {
                isScrollAtTop = true;
            }
            if (firstPosition + childCount != itemCount || lastBottom > recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                isScrollAtBottom = false;
            } else {
                isScrollAtBottom = true;
            }

            if (isScrollAtBottom && isScrollAtTop) {
                isShortList = true;
            } else {
                isShortList = false;
            }
            recyclerView.mIsShortList = isShortList;
            if (recyclerView.mIsShortList) {
                if (recyclerView.mOffsetY == 0) {
                    recyclerView.mOffsetY = offset;
                }
            } else if (isScrollAtTop) {
                if (recyclerView.mOffsetY == 0 || offset < recyclerView.mOffsetY) {
                    recyclerView.mOffsetY = offset;
                }
            } else if (isScrollAtBottom && (recyclerView.mOffsetY == 0 || offset > recyclerView.mOffsetY)) {
                recyclerView.mOffsetY = offset;
            }

            if (isScrollAtTop && offset > 0) {
                return true;
            }

            if (isScrollAtBottom && offset < 0) {
                return true;
            }
        }
        return false;
    }


}
