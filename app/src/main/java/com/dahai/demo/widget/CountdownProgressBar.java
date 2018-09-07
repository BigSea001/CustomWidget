package com.dahai.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 作者： Administrator
 * 时间： 2018/9/7 15:20
 * 修改时间： 2018/9/7 15:20
 * 描述： 倒计时进度。设置一个倒计时就可以
 */
public class CountdownProgressBar extends View {
    private static final long DEFAULT_DURATION = 6000;
    private OnEndListener onEndListener;
    private int mColor = -16711936;
    private long mDuration = DEFAULT_DURATION;
    private float mLeft;
    private Paint mPaint;
    private long mProgress = 0;
    private float mRight;
    private long mStartTime = -1;
    private boolean mStarted = false;

    public CountdownProgressBar(Context context) {
        super(context);
        init();
    }

    public CountdownProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountdownProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDuration(long millisecods) {
        if (millisecods >= 0) {
            this.mDuration = millisecods;
        }
    }

    public long getDuration() {
        return this.mDuration;
    }

    public long getProgress() {
        return this.mProgress;
    }

    public void resetProgress() {
        this.mProgress = 0;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public void start() {
        this.mStartTime = -1;
        this.mStarted = true;
        this.mProgress = 0;
        invalidate();
    }

    public void stop() {
        this.mStarted = false;
        invalidate();
    }

    public void setOnEndListener(OnEndListener onEndListener) {
        this.onEndListener = onEndListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.mStarted) {
            if (this.mStartTime == -1) {
                this.mStartTime = System.currentTimeMillis();
                this.mLeft = 0.0f;
                this.mRight = (float) canvas.getWidth();
            }
            this.mPaint.setColor(this.mColor);
            canvas.drawRect(this.mLeft, 0.0f, this.mRight, (float) canvas.getHeight(), this.mPaint);
            this.mProgress = System.currentTimeMillis() - this.mStartTime;
            if (this.mProgress <= this.mDuration) {
                float length = ((float) canvas.getWidth()) * (1.0f - (((float) this.mProgress) / ((float) this.mDuration)));
                this.mLeft = (((float) canvas.getWidth()) - length) / 2.0f;
                this.mRight = (((float) canvas.getWidth()) + length) / 2.0f;
            } else {
                this.mStarted = false;
                if (this.onEndListener != null) {
                    this.onEndListener.onEnd();
                }
            }
            invalidate();
        }
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL);
    }

    public interface OnEndListener {
        void onEnd();
    }
}
