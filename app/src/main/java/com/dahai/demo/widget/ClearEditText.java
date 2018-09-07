package com.dahai.demo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.dahai.demo.R;

/**
 * 作者： Administrator
 * 时间： 2018/9/7 15:31
 * 描述： 带清空按钮的输入框
 */
public class ClearEditText extends EditText implements TextWatcher, View.OnFocusChangeListener {

    private boolean isGray;
    private Drawable mClearDrawableNormal;
    private Drawable mClearDrawablePressed;
    private OnFocusChangeListener mExtraOnFocusChangeListener;
    private boolean mHasFoucs;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 16842862);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mExtraOnFocusChangeListener = null;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
            this.isGray = a.getBoolean(0, true);
            a.recycle();
        }
        init();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            boolean isInnerWidth;
            int x = (int) event.getX();
            int y = (int) event.getY();
            int height = getCompoundDrawables()[2].getBounds().height();
            int distance = (getHeight() - height) / 2;
            if (x <= getWidth() - getTotalPaddingRight() || x >= getWidth() - getPaddingRight()) {
                isInnerWidth = false;
            } else {
                isInnerWidth = true;
            }
            boolean isInnerHeight;
            if (y <= distance || y >= distance + height) {
                isInnerHeight = false;
            } else {
                isInnerHeight = true;
            }
            if (isInnerWidth && isInnerHeight) {
                if (event.getAction() == 0) {
                    setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], this.mClearDrawablePressed, getCompoundDrawables()[3]);
                } else if (event.getAction() == 1) {
                    setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], this.mClearDrawableNormal, getCompoundDrawables()[3]);
                    setText("");
                }
            } else if (event.getAction() == 1) {
                setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], this.mClearDrawableNormal, getCompoundDrawables()[3]);
            }
        }
        return super.onTouchEvent(event);
    }

    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (this.mHasFoucs) {
            setClearIconVisible(text.length() > 0);
        }
    }

    public void onFocusChange(View view, boolean hasFocus) {
        boolean z = false;
        this.mHasFoucs = hasFocus;
        if (hasFocus) {
            if (getText().length() > 0) {
                z = true;
            }
            setClearIconVisible(z);
        } else {
            setClearIconVisible(false);
        }
        if (this.mExtraOnFocusChangeListener != null) {
            this.mExtraOnFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void afterTextChanged(Editable s) {
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        if (l == this) {
            super.setOnFocusChangeListener(l);
        } else {
            this.mExtraOnFocusChangeListener = l;
        }
    }

    private void init() {
        if (this.isGray) {
            this.mClearDrawableNormal = getResources().getDrawable(R.drawable.ic_edittext_clear_gray_normal);
            this.mClearDrawablePressed = getResources().getDrawable(R.drawable.ic_edittext_clear_gray_pressed);
        } else {
            this.mClearDrawableNormal = getResources().getDrawable(R.drawable.ic_edittext_clear_normal);
            this.mClearDrawablePressed = getResources().getDrawable(R.drawable.ic_edittext_clear_pressed);
        }
//        this.mClearDrawableNormal.setBounds(0, 0, this.mClearDrawableNormal.getIntrinsicWidth(), this.mClearDrawableNormal.getIntrinsicHeight());
        this.mClearDrawableNormal.setBounds(0, 0, dpToPx(30), dpToPx(30));
        this.mClearDrawablePressed.setBounds(0, 0, dpToPx(30), dpToPx(30));
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    protected void setClearIconVisible(boolean visible) {
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], visible ? this.mClearDrawableNormal : null, getCompoundDrawables()[3]);
    }

    private int dpToPx(int dip) {
        return (int) (((float) dip) * Resources.getSystem().getDisplayMetrics().density);
    }
}
