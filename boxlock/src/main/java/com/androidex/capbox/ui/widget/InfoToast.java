package com.androidex.capbox.ui.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.capbox.R;


/**
 * @author liyp
 * @version 1.0.0
 * @description 信息Toast
 * @createTime 2015/11/25
 * @editTime
 * @editor
 */
public class InfoToast {
    private static final int TYPE_OK = 1;
    private static final int TYPE_ERROR = 2;

    private static Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            wm.removeView(contentView);
        }
    };
    private static WindowManager wm;
    private static View contentView;

    private static void show(Context context, int type, String msg, int duration) {
        if (context == null) return;
        Toast toast = new Toast(context);
        View contentView = View.inflate(context, R.layout.dlg_info_toast, null);
        ImageView iv_icon = (ImageView) contentView.findViewById(R.id.iv_icon);
        TextView tv_info = (TextView) contentView.findViewById(R.id.tv_info);
        if (type == TYPE_OK) {
            iv_icon.setImageResource(R.mipmap.ic_info_toast_ok);
        } else {
            iv_icon.setImageResource(R.mipmap.ic_info_toast_error);
        }
        tv_info.setText(msg);
        toast.setView(contentView);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private static void showToast(Context context, int type, String msg) {
        contentView = View.inflate(context, R.layout.dlg_info_toast, null);
        ImageView iv_icon = (ImageView) contentView.findViewById(R.id.iv_icon);
        TextView tv_info = (TextView) contentView.findViewById(R.id.tv_info);
        if (type == TYPE_OK) {
            iv_icon.setImageResource(R.mipmap.ic_info_toast_ok);
        } else {
            iv_icon.setImageResource(R.mipmap.ic_info_toast_error);
        }
        tv_info.setText(msg);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER ;//位置调整  | Gravity.CLIP_VERTICAL
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;//不能设置这个属性，所有界面都弹出消息了。
        wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        wm.addView(contentView, params);
    }

    public static void showOkToast(Context context, String msg) {
        showToast(context, TYPE_OK, msg);
        mHandler.sendEmptyMessageDelayed(1, 3 * 1000);
    }

    public static void showErrorToast(Context context, String msg) {
        showToast(context, TYPE_ERROR, msg);
        mHandler.sendEmptyMessageDelayed(1, 3 * 1000);
    }

    public static void showErrorShort(Context context, String msg) {
        show(context, TYPE_ERROR, msg, Toast.LENGTH_SHORT);
    }

    public static void showErrorLong(Context context, String msg) {
        show(context, TYPE_ERROR, msg, Toast.LENGTH_LONG);
    }

    public static void showOkShort(Context context, String msg) {
        show(context, TYPE_OK, msg, Toast.LENGTH_SHORT);
    }

    public static void showOkLong(Context context, String msg) {
        show(context, TYPE_OK, msg, Toast.LENGTH_LONG);
    }
}
