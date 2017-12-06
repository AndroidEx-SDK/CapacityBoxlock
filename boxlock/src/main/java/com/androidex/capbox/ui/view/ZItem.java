package com.androidex.capbox.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 左右滑动监听
 * @author zhu
 *
 */
public class ZItem extends LinearLayout {
	private ZItemListener mZItemListener;
	private float x = 0;
	private int flag ;
	/**Slide length,滑动长度*/
	private int LENGTH = 50;
	public ZItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public ZItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x  = event.getX();
			
			break;
		case MotionEvent.ACTION_MOVE:
			float x2 = event.getX();
			if(flag != 1){
				if(x2-x>LENGTH){
					flag = 1;
					mZItemListener.onRight();
				}
			}
			if(flag != 2){
				if(x-x2>LENGTH){
					flag = 2;
					mZItemListener.onLeft();
				}
			}

		default:
			break;
		}
		return true;
	}
	
	public void setZItemListener(ZItemListener z){
		mZItemListener = z;
	}
	
	public interface ZItemListener{
		/**Left，左滑 */
		void onLeft();
		/** Right，右滑 */
		void onRight();
	}
}
