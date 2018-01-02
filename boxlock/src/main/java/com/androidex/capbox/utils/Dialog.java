package com.androidex.capbox.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.androidex.capbox.R;

/**
 * 就是自定义的Dialog,不可back或点击外部销毁
 *
 * @author liangshiquan
 */
public class Dialog {
    public final static int SELECT_DIALOG = 1;
    public final static int RADIO_DIALOG = 2;
    static android.app.Dialog dialog;
    private static android.app.Dialog dialog1;

    /**
     * 创建一个单选对话框
     *
     * @param context
     * @param toast               提示消息
     * @param dialogClickListener 点击监听
     * @return
     */
    public static android.app.Dialog showRadioDialog(Context context, String toast, final DialogClickListener dialogClickListener) {
        return ShowDialog(context, context.getResources().getString(R.string.pointMessage), toast, dialogClickListener, RADIO_DIALOG);
    }

    /**
     * 创建一个选择对话框
     *
     * @param context
     * @param toast               提示消息
     * @param dialogClickListener 点击监听
     * @return
     */
    public static android.app.Dialog showSelectDialog(Context context, String toast, final DialogClickListener dialogClickListener) {
        return ShowDialog(context, context.getResources().getString(R.string.pointMessage), toast, dialogClickListener, SELECT_DIALOG);
    }

    /**
     * 创建一个选择对话框
     *
     * @param context
     * @param title               提示标题
     * @param toast               提示消息
     * @param dialogClickListener 点击监听
     * @return
     */
    public static android.app.Dialog showSelectDialog(Context context, String title, String toast, final DialogClickListener dialogClickListener) {
        return ShowDialog(context, title, toast, dialogClickListener, SELECT_DIALOG);
    }

    private static android.app.Dialog ShowDialog(Context context, String title, String toast, final DialogClickListener dialogClickListener, int DialogType) {
        if (dialog != null && dialog.isShowing()) {
            return null;
        }
        dialog = new android.app.Dialog(context, R.style.DialogStyle);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        dialog.setContentView(view);
        ((TextView) view.findViewById(R.id.point)).setText(title);
        ((TextView) view.findViewById(R.id.toast)).setText(toast);
        if (DialogType == RADIO_DIALOG) {
        } else {
            view.findViewById(R.id.ok).setVisibility(View.GONE);
            view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogClickListener.cancel();
                    }
                }, 200);
            }
        });
        view.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogClickListener.confirm();
                    }
                }, 200);
            }
        });
        view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogClickListener.confirm();
                    }
                }, 200);
            }
        });
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            lp.width = getScreenHeight(context) / 10 * 8;
        } else {
            lp.width = getScreenWidth(context) / 10 * 8;
        }
        mWindow.setAttributes(lp);
        dialog.show();

        return dialog;
    }

    public static android.app.Dialog showAlertDialog(final Context context, String title, final DialogDataListener listener) {
        dialog1 = new android.app.Dialog(context, R.style.DialogStyle);
        dialog1.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_edittext, null);
        dialog1.setContentView(view);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView btnPositive = (TextView) view.findViewById(R.id.tv_confirm);
        TextView btnNegative = (TextView) view.findViewById(R.id.tv_cancle);
        final EditText et_carryNum = (EditText) view.findViewById(R.id.et_carryNum);
        et_carryNum.setFocusable(true);
        tv_title.setText(title);
        btnPositive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = et_carryNum.getText().toString().trim();
                listener.confirm(str);
                dialog1.dismiss();
            }
        });
        btnNegative.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                listener.cancel();
                dialog1.dismiss();
            }
        });
        Window mWindow = dialog1.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            lp.height = getScreenHeight(context) / 10 * 8;
        } else {
            lp.width = getScreenWidth(context) / 10 * 9;
        }
        mWindow.setGravity(Gravity.CENTER);
        mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);
        dialog1.show();
        return dialog1;
    }

    public static android.app.Dialog showAlertDialog(final Context context, String title, final DialogFingerListener listener) {
        dialog1 = new android.app.Dialog(context, R.style.DialogStyle);
        dialog1.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_textview, null);
        dialog1.setContentView(view);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView btnPositive = (TextView) view.findViewById(R.id.tv_confirm);
        TextView btnNegative = (TextView) view.findViewById(R.id.tv_cancle);
        tv_title.setText(title);
        btnPositive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                listener.confirm();
                dialog1.dismiss();
            }
        });
        btnNegative.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                listener.cancel();
                dialog1.dismiss();
            }
        });
        Window mWindow = dialog1.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            lp.height = getScreenHeight(context) / 10 * 8;
        } else {
            lp.width = getScreenWidth(context) / 10 * 9;
        }
        mWindow.setGravity(Gravity.CENTER);
        mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);
        dialog1.show();
        return dialog1;
    }

    /**
     * 获取屏幕分辨率宽
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕分辨率高
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public interface DialogClickListener {
        public abstract void confirm();

        public abstract void cancel();
    }

    public interface ButtonClickListener {
        public abstract void button1();

        public abstract void button2();

        public abstract void button3();

    }

    public interface DialogDataListener {
        public abstract void confirm(String data);

        public abstract void cancel();
    }

    public interface DialogFingerListener {
        public abstract void confirm();

        public abstract void cancel();
    }

    public interface DialogItemClickListener {
        public abstract void confirm(String result);
    }
}