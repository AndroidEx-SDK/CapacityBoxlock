package com.androidex.capbox.ui.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.androidex.capbox.R;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/5/2
 */
@SuppressLint("AppCompatCustomView")
public class AutoLinkTextView extends TextView {
    private int mMaxWidth;

    public AutoLinkTextView(Context context) {
        super(context);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initWidth(context, attrs);
    }

    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initWidth(context, attrs);
    }

    @TargetApi(21)
    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initWidth(context, attrs);
    }

    private void initWidth(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoTextView);
        this.mMaxWidth = array.getDimensionPixelSize(R.styleable.AutoTextView_MaxWidth, 0);
        this.setMaxWidth(this.mMaxWidth);
        array.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = this.getLayout();
        float width = 0.0F;

        for (int i = 0; i < layout.getLineCount(); ++i) {
            width = Math.max(width, layout.getLineWidth(i));
        }

        width += (float) (this.getCompoundPaddingLeft() + this.getCompoundPaddingRight());
        if (this.getBackground() != null) {
            width = Math.max(width, (float) this.getBackground().getIntrinsicWidth());
        }

        if (this.mMaxWidth != 0) {
            width = Math.min(width, (float) this.mMaxWidth);
        }

        this.setMeasuredDimension((int) Math.ceil((double) width), this.getMeasuredHeight());
    }
}

