package com.dahai.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.dahai.demo.R;

/**
 * 作者： Administrator
 * 时间： 2018/9/7 14:18
 * 描述： 圆形进度。中间带进度百分比
 */
public class CircleProgressView extends View implements View.OnClickListener {

    private static final String TAG = "CircleProgressView";
    // 进度扇形填充色
    private int circleColor;
    private Paint circlePaint;
    private RectF circleRect;

    private int circleSize;
    // 当前进度
    private volatile int currentPresent;
    private int defaultHeight;
    private int defaultWidth;
    private AlphaAnimation enterAnimation;
    private AlphaAnimation exitAnimation;
    private boolean isFailed;
    private boolean isLoading;
    private long lastThreadId;
    private OnFailedClickListener onFailedClickListener;
    private int strokeColor;
    private int strokeMargin;
    private Paint strokePaint;
    private int strokeWidth;
    private int textColor;
    private Paint textPaint;
    private int textSize;

    public interface OnFailedClickListener {
        void onClickFiled(View view);
    }

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.lastThreadId = 0;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressView, 0, 0);
        this.circleSize = a.getDimensionPixelSize(R.styleable.CircleProgressView_inner_circle_size, 0);
        this.textSize = a.getDimensionPixelSize(R.styleable.CircleProgressView_inner_text_size, 0);
        this.circleColor = a.getColor(R.styleable.CircleProgressView_inner_circle_color, 0);
        this.textColor = a.getColor(R.styleable.CircleProgressView_inner_text_color, 0);
        this.strokeWidth = a.getDimensionPixelSize(R.styleable.CircleProgressView_stroke_width, 0);
        this.strokeColor = a.getColor(R.styleable.CircleProgressView_stroke_color, 0);
        this.strokeMargin = a.getDimensionPixelSize(R.styleable.CircleProgressView_stroke_margin, 0);
        this.currentPresent = a.getInt(R.styleable.CircleProgressView_current_progress, 0);
        a.recycle();
        initDefaultValueWhenEmpty();
        buildAnimation();
        initPaint();
    }

    private void initDefaultValueWhenEmpty() {
        if (this.circleSize == 0) {
            this.circleSize = dpToPx(30.0f);
        }
        if (this.textSize == 0) {
            this.textSize = 16;
        }
        if (this.circleColor == 0) {
            this.circleColor = Color.RED;
        }
        if (this.textColor == 0) {
            this.textColor = Color.BLACK;
        }
        if (this.strokeWidth == 0) {
            this.strokeWidth = dpToPx(2.0f);
        }
        if (this.strokeColor == 0) {
            this.strokeColor = Color.BLACK;
        }
        if (this.strokeMargin == 0) {
            this.strokeMargin = dpToPx(0.5f);
        }
    }

    private void buildAnimation() {
        this.exitAnimation = new AlphaAnimation(1.0f, 0.0f);
        this.exitAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        this.exitAnimation.setDuration(500);
        this.exitAnimation.setAnimationListener(new Animation.AnimationListener() {
           
            public void onAnimationStart(Animation animation) {
                currentPresent = 100;
                postInvalidate();
            }

            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
                if (!isFailed) {
                    reset();
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.enterAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.enterAnimation.setDuration(500);
        this.enterAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        this.enterAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }

            public void onAnimationEnd(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void initPaint() {
        this.circlePaint = new Paint(1);
        this.circlePaint.setColor(this.circleColor);
        this.circlePaint.setStyle(Paint.Style.FILL);
        this.strokePaint = new Paint(1);
        this.strokePaint.setColor(this.strokeColor);
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setStrokeWidth((float) this.strokeWidth);
        this.textPaint = new Paint(1);
        this.textPaint.setColor(this.textColor);
        this.textPaint.setTextSize((float) this.textSize);
        this.textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.defaultWidth == 0) {
            this.defaultWidth = w;
        }
        if (this.defaultHeight == 0) {
            this.defaultHeight = h;
        }
        float width = ((float) w) - ((float) (getPaddingLeft() + getPaddingRight()));
        float height = ((float) h) - ((float) (getPaddingBottom() + getPaddingTop()));
        int strokeSpace = (this.strokeMargin + this.strokeWidth) + 1;
        if (this.circleRect == null) {
            this.circleRect = new RectF((float) (getPaddingLeft() + strokeSpace), (float) (getPaddingTop() + strokeSpace), (((float) getPaddingLeft()) + width) - ((float) strokeSpace), (((float) getPaddingTop()) + height) - ((float) strokeSpace));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.defaultWidth == 0) {
            this.defaultWidth = getWidth();
        }
        if (this.defaultHeight == 0) {
            this.defaultHeight = getHeight();
        }
        if (this.circleRect == null) {
            int strokeSpace = (this.strokeMargin + this.strokeWidth) + 1;
            this.circleRect = new RectF((float) (getPaddingLeft() + strokeSpace), (float) (getPaddingTop() + strokeSpace), (float) ((getPaddingLeft() + getWidth()) - strokeSpace), (float) ((getPaddingTop() + getHeight()) - strokeSpace));
        }
        if (this.isFailed) {
            Paint.FontMetricsInt fontMetrics = this.textPaint.getFontMetricsInt();
            int baseline = (int) ((this.circleRect.top + ((((this.circleRect.bottom - this.circleRect.top) - ((float) fontMetrics.bottom)) + ((float) fontMetrics.top)) / 2.0f)) - ((float) fontMetrics.top));
            this.textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("加载失败，点我重新加载", (float) (getWidth() / 2), (float) baseline, this.textPaint);
            return;
        }
        canvas.drawArc(this.circleRect, -90.0f, 3.6f * ((float) this.currentPresent), true, this.circlePaint);
        canvas.drawCircle(this.circleRect.centerX(), this.circleRect.centerY(), (float) (this.strokeMargin + ((int) Math.max(this.circleRect.width() / 2.0f, this.circleRect.height() / 2.0f))), this.strokePaint);
        Paint.FontMetricsInt fontMetrics = this.textPaint.getFontMetricsInt();
        int baseline = (int) ((this.circleRect.top + ((((this.circleRect.bottom - this.circleRect.top) - ((float) fontMetrics.bottom)) + ((float) fontMetrics.top)) / 2.0f)) - ((float) fontMetrics.top));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(this.currentPresent + "%", this.circleRect.centerX(), (float) baseline, this.textPaint);
    }

    public synchronized void reset() {
        this.isFailed = false;
        this.lastThreadId = 0;
        this.currentPresent = 0;
        if (!(this.defaultWidth == 0 || this.defaultHeight == 0 || getLayoutParams().width == this.defaultWidth)) {
            getLayoutParams().width = this.defaultWidth;
            getLayoutParams().height = this.defaultHeight;
            setLayoutParams(getLayoutParams());
        }
        this.textPaint.setTextSize((float) this.textSize);
        setOnClickListener(null);
        postInvalidate();
    }

    // 开始
    public synchronized void setStart() {
        if (!this.isLoading) {
            this.isLoading = true;
            post(new Runnable() {
                public void run() {
                    reset();
                    if (getAnimation() != null) {
                        clearAnimation();
                    }
                    startAnimation(enterAnimation);
                }
            });
        }
    }

    // 设置完成
    public synchronized void setFinish(boolean needAnima) {
        this.isLoading = false;
        if (needAnima) {
            post(new Runnable() {
                public void run() {
                    if (getAnimation() != null) {
                        clearAnimation();
                    }
                    startAnimation(exitAnimation);
                }
            });
        } else {
            post(new Runnable() {
                public void run() {
                    setVisibility(GONE);
                    reset();
                }
            });
        }
        Log.d(TAG, "finish");
    }

    // 设置加载失败
    public synchronized void setFailed() {
        post(new Runnable() {
            public void run() {
                isFailed = true;
                isLoading = false;
                lastThreadId = 0;
                if (getAnimation() != null) {
                    clearAnimation();
                }
                if (defaultWidth != 0) {
                    getLayoutParams().width = defaultWidth * 3 >= getDefaultWidth() ? getDefaultWidth() : defaultWidth * 3;
                    setLayoutParams(getLayoutParams());
                }
                textPaint.setTextSize(30.0f);
                setOnClickListener(CircleProgressView.this);
                postInvalidate();
                setAlpha(1.0f);
                setVisibility(VISIBLE);
                Log.d(CircleProgressView.TAG, "failed");
            }
        });
    }

    public int getCircleSize() {
        return this.circleSize;
    }

    public void setCircleSize(int circleSize) {
        this.circleSize = circleSize;
        postInvalidate();
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        postInvalidate();
    }

    public int getCircleColor() {
        return this.circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        postInvalidate();
    }

    public int getTextColor() {
        return this.textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        postInvalidate();
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        postInvalidate();
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        postInvalidate();
    }

    public int getStrokeMargin() {
        return this.strokeMargin;
    }

    public void setStrokeMargin(int strokeMargin) {
        this.strokeMargin = strokeMargin;
        postInvalidate();
    }

    public Paint getCirclePaint() {
        return this.circlePaint;
    }

    public void setCirclePaint(Paint circlePaint) {
        this.circlePaint = circlePaint;
        postInvalidate();
    }

    public Paint getStrokePaint() {
        return this.strokePaint;
    }

    public void setStrokePaint(Paint strokePaint) {
        this.strokePaint = strokePaint;
        postInvalidate();
    }

    public int getCurrentPresent() {
        return this.currentPresent;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public synchronized void setCurrentPresent(int currentPresent) {
        if (this.lastThreadId == 0) {
            this.lastThreadId = Thread.currentThread().getId();
        }
        if (Thread.currentThread().getId() == this.lastThreadId) {
            if (currentPresent < 0) {
                currentPresent = 0;
            }
            if (currentPresent > 100) {
                currentPresent = 100;
            }
            this.currentPresent = currentPresent;
            postInvalidate();
        }
    }

    public boolean isFailed() {
        return this.isFailed;
    }

    @Override
    public void onClick(View v) {
        if (this.onFailedClickListener != null) {
            this.onFailedClickListener.onClickFiled(v);
        }
    }

    public OnFailedClickListener getOnFailedClickListener() {
        return this.onFailedClickListener;
    }

    public void setOnFailedClickListener(OnFailedClickListener onFailedClickListener) {
        this.onFailedClickListener = onFailedClickListener;
    }

    private int dpToPx(float dip) {
        return (int) ((getResources().getDisplayMetrics().density * dip) + 0.5f);
    }

    private int getDefaultWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }


}