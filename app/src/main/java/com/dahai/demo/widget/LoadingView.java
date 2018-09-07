package com.dahai.demo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * 作者： Administrator
 * 时间： 2018/9/7 12:09
 * 描述：两点变大变小的旋转
 */
public class LoadingView extends View {
    // 颜色
    private static final int DEFAULT_COLOR = Color.RED;
    // 时间
    private static final long DEFAULT_DURATION = 1080;
    private static final float DYNAMIC_DEGREE_VALUE = ((float) Math.asin(1.0d));
    private static final String TAG = "LoadingView";
    // 大圆的大小
    private int bigCircleSize;
    // 内容区域
    private Rect bounds;
    // 两个圆点的画笔
    private Paint circlePaint;
    private float currentDegree;
    private Interpolator defaultInterpolator;
    private boolean isAnimaed;
    private int sideMargin;
    // 小圆的大小
    private int smallCircleSize;
    // 两个圆的边框
    private Paint strokePaint;
    private ValueAnimator valueAnimator;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultInterpolator = new LinearInterpolator();
        init();
    }

    private void init() {
        initPaint();
        isAnimaed = false;
        bounds = new Rect();
    }

    private void initPaint() {
        strokePaint = new Paint(1);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2.0f);
        circlePaint = new Paint(1);
        circlePaint.setColor(DEFAULT_COLOR);
        circlePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        bounds.set(getPaddingLeft(), getPaddingTop(), MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight(), MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bounds.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bigCircleSize == 0 || bigCircleSize > bounds.width()) {
            bigCircleSize = bounds.width() >> 3;
        }
        if (smallCircleSize == 0 || smallCircleSize > bounds.width()) {
            smallCircleSize = bounds.width() >> 5;
        }
        if (sideMargin == 0) {
            sideMargin = bounds.width() >> 2;
        }
        int bigCircleX = ((bounds.right - (bigCircleSize / 2)) - 2) - sideMargin;
        int bigCircleY = ((bounds.top + (bigCircleSize / 2)) + 2) + sideMargin;
        int smallCircleX = ((bounds.left + (bigCircleSize / 2)) + 2) + sideMargin;
        int smallCircleY = ((bounds.bottom - (bigCircleSize / 2)) - 2) - sideMargin;
        // 获取动态的大圆大小
        float bigCircleDynamicSize = (float) (((double) bigCircleSize) - (((double) (bigCircleSize - smallCircleSize)) * Math.abs(Math.sin((double) ((currentDegree * DYNAMIC_DEGREE_VALUE) / 180.0f)))));
        if (bigCircleDynamicSize < ((float) smallCircleSize)) {
            bigCircleDynamicSize = (float) smallCircleSize;
        }
        // 获取动态的小圆大小
        float smallCircleDynamicSize = (float) (((double) smallCircleSize) + (((double) (bigCircleSize - smallCircleSize)) * Math.abs(Math.sin((double) ((currentDegree * DYNAMIC_DEGREE_VALUE) / 180.0f)))));
        if (smallCircleDynamicSize > ((float) bigCircleSize)) {
            smallCircleDynamicSize = (float) bigCircleSize;
        }
        // 旋转着画
        canvas.save();
        canvas.rotate(currentDegree, (float) bounds.centerX(), (float) bounds.centerY());
        canvas.drawCircle((float) bigCircleX, (float) bigCircleY, bigCircleDynamicSize, circlePaint);
        canvas.drawCircle((float) bigCircleX, (float) bigCircleY, strokePaint.getStrokeWidth() + bigCircleDynamicSize, strokePaint);
        canvas.drawCircle((float) smallCircleX, (float) smallCircleY, smallCircleDynamicSize, circlePaint);
        canvas.drawCircle((float) smallCircleX, (float) smallCircleY, strokePaint.getStrokeWidth() + smallCircleDynamicSize, strokePaint);
        canvas.restore();
    }

    // 开始
    public void start() {
        start(defaultInterpolator, DEFAULT_DURATION);
    }

    // 开始
    public void start(@Nullable Interpolator interpolator, long duration) {
        if (!isAnimaed) {
            if (duration % 180 != 0) {
                duration = (long) (180.0d * Math.floor((double) (((float) duration) / 180.0f)));
            }
            isAnimaed = true;
            clearAnimation();
            if (valueAnimator == null) {
                valueAnimator = createAnimator(interpolator, duration);
            }
            valueAnimator.cancel();
            valueAnimator.start();
        }
    }

    // 暂停
    public void stop() {
        isAnimaed = false;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        currentDegree = 0.0f;
        clearAnimation();
    }

    public int getCircleColor() {
        return circlePaint == null ? 0 : circlePaint.getColor();
    }

    public void setCircleColor(int color) {
        if (circlePaint != null) {
            circlePaint.setColor(color);
        }
    }

    public int getSmallCircleSize() {
        return smallCircleSize;
    }

    public void setSmallCircleSize(int smallCircleSize) {
        smallCircleSize = smallCircleSize;
    }

    public int getBigCircleSize() {
        return bigCircleSize;
    }

    public void setBigCircleSize(int bigCircleSize) {
        bigCircleSize = bigCircleSize;
    }

    public void setStrokeColor(int color) {
        strokePaint.setColor(color);
    }

    public void setStrokeWidth(int width) {
        strokePaint.setStrokeWidth((float) width);
    }

    private ValueAnimator createAnimator(Interpolator interpolator, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 180.0f);
        if (interpolator == null) {
            interpolator = defaultInterpolator;
        }
        animator.setInterpolator(interpolator);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        if (duration == 0) {
            duration = DEFAULT_DURATION;
        }
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                       @Override
                                       public void onAnimationUpdate(ValueAnimator animation) {
                                           currentDegree = (Float) animation.getAnimatedValue();
                                           invalidate();
                                       }
                                   });
        return animator;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }
}
