package com.androidex.capbox.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.androidex.capbox.R;

public class DialogUtils {

	public static Dialog createDialog(Context context, String msg) {
		// TODO Auto-generated method stub
		Dialog dialog=null;
		dialog=new Dialog(context,R.style.image_dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View main=View.inflate(context, R.layout.dialog_main, null);
		dialog.setContentView(main);
		TextView tv=(TextView)main.findViewById(R.id.msg);
		tv.setText(msg);
		return dialog;
	}

	public static Dialog createDialog(Context context, int id) {
		// TODO Auto-generated method stub
		Dialog dialog=null;
		dialog=new Dialog(context,R.style.image_dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View main=View.inflate(context, R.layout.dialog_main, null);
		dialog.setContentView(main);
		TextView tv=(TextView)main.findViewById(R.id.msg);
		tv.setText(id);
		dialog.setCancelable(false);//设置为false，点击对话框以外区域不会关闭。默认为true。
		return dialog;
	}

}
