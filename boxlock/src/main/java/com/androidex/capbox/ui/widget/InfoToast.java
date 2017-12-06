package com.androidex.capbox.ui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
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

    private static void show(Context context, int type, String msg, int duration) {
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
