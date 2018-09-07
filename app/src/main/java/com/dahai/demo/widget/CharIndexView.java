package com.dahai.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者： Administrator
 * 时间： 2018/9/7 15:04
 * 描述： 仿微信联系人右侧字母触摸
 */
public class CharIndexView extends View {
    // 背景色
    private static final int BG_COLOR_PRESSED = 1337966527;
    // 文字颜色
    private static final int FONT_COLOR = -11119018;
    // 文字大小 sp
    private static final int FONT_SIZE = 14;
    // 待选择的字符数组
    public static final char[] charArray = new char[]{'↑', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#'};
    private OnCharacterTouchedListener mOnCharacterTouchedListener;
    private TextPaint mPaint = new TextPaint();
    private boolean mPressed;
    // 用于测量文字的大小
    private Rect bounds = new Rect();

    public interface OnCharacterTouchedListener {
        // 选中的字符
        void onSelect(char c);

        void onCancel();

        void onDown();
    }

    public void setOnCharacterTouchedListener(OnCharacterTouchedListener onCharacterTouchedListener) {
        mOnCharacterTouchedListener = onCharacterTouchedListener;
    }

    public CharIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setTextSize(FONT_SIZE * context.getResources().getDisplayMetrics().density);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                mPressed = true;
                onTouch(event.getY());
                invalidate();
                if (mOnCharacterTouchedListener != null) {
                    mOnCharacterTouchedListener.onDown();
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPressed = false;
                invalidate();
                if (mOnCharacterTouchedListener != null) {
                    mOnCharacterTouchedListener.onCancel();
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onTouch(event.getY());
                break;
        }
        return true;
    }

    private void onTouch(float y) {
        float index = (float) ((int) (y / ((float) (getMeasuredHeight() / charArray.length))));
        if (index < 0.0f) {
            index = 0.0f;
        } else if (index >= ((float) charArray.length)) {
            index = (float) (charArray.length - 1);
        }
        if (mOnCharacterTouchedListener != null) {
            mOnCharacterTouchedListener.onSelect(charArray[(int) index]);
        }
    }

    

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPressed) {
            mPaint.setColor(BG_COLOR_PRESSED);
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), mPaint);
        }
        mPaint.setColor(FONT_COLOR);
        int unitHeight = getMeasuredHeight() / charArray.length;
        
        for (int i = 0; i < charArray.length; i++) {
            String str = String.valueOf(charArray[i]);
            mPaint.getTextBounds(str, 0, 1, bounds);
            canvas.drawText(str, (float) ((int) ((((float) getMeasuredWidth()) - mPaint.measureText(str)) / 2.0f)), (float) (((i * unitHeight) + (((bounds.bottom - bounds.top) + unitHeight) / 2)) - bounds.bottom), mPaint);
        }
    }
}
