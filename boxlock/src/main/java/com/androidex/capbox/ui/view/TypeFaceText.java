package com.androidex.capbox.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.androidex.capbox.MyApplication;

public class TypeFaceText extends TextView {
	private Context mContext;
	public TypeFaceText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public TypeFaceText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public TypeFaceText(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		setTypeface(MyApplication.getInstance().getTypeface());
	}
}