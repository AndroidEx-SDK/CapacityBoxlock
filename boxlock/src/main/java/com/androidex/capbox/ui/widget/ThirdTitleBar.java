package com.androidex.capbox.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidex.capbox.R;

/**
 * @author liyp
 * @version 1.0.0
 * @description 第二标题栏
 * @createTime 2015/11/5
 * @editTime
 * @editor
 */
public class ThirdTitleBar extends RelativeLayout {
    private RelativeLayout rl_back;
    private TextView tv_title;
    private ImageView iv_right;
    private TextView tv_right;

    private String title;
    private int rightRes;
    private String rightText;

    public ThirdTitleBar(Context context) {
        super(context);
        init();
    }

    public ThirdTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SecondTitleBar);
        title = typedArray.getString(R.styleable.SecondTitleBar_titleText);
        rightText = typedArray.getString(R.styleable.SecondTitleBar_rightText);
        rightRes = typedArray.getResourceId(R.styleable.SecondTitleBar_rightRes, -1);
        typedArray.recycle();
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_third_title_bar, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle(title);
        setRightRes(rightRes);
        setRightText(rightText);

    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
    }

    /**
     * 设置右边资源
     *
     * @param resId
     */
    public void setRightRes(int resId) {
        if (resId != -1) {
            iv_right.setImageResource(resId);
        }
        iv_right.setVisibility(resId != -1 ? VISIBLE : GONE);
    }

    /**
     * 设置右边文字
     *
     * @param text
     */
    public void setRightText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tv_right.setText(text);
            tv_right.setVisibility(VISIBLE);
        } else {
            tv_right.setVisibility(GONE);
        }
    }

    public ImageView getRightIv() {
        return iv_right;
    }

    public TextView getRightTv(){
        return tv_right;
    }

    public RelativeLayout getLeftBtn() {
        return rl_back;
    }
}
