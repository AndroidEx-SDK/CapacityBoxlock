package com.androidex.capbox.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.service.MyBleService;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/5/3
 */
public class ChatPopWindow extends PopupWindow {

    @SuppressLint("InflateParams")
    public ChatPopWindow(final Activity context, final String address, final CallBack callBack) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.pop_chat, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(content);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        RelativeLayout re_connect = (RelativeLayout) content.findViewById(R.id.re_connect);
        TextView tv_connect = (TextView) content.findViewById(R.id.tv_connect);
        RelativeLayout re_setting = (RelativeLayout) content.findViewById(R.id.re_setting);
        if (MyBleService.getInstance().getConnectDevice(address) == null) {
            tv_connect.setText("连接");
        } else {
            tv_connect.setText("断开");
        }
        re_connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (address != null) {
                    if (MyBleService.getInstance().getConnectDevice(address) == null) {
                        MyBleService.getInstance().connectionDevice(context, address);
                        ((BaseActivity) context).showProgress("正在连接");
                    } else {
                        MyBleService.getInstance().disConnectDevice(address);
                    }
                }
                ChatPopWindow.this.dismiss();
            }
        });
        re_setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callBack.callBack();
                ChatPopWindow.this.dismiss();
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public abstract static class CallBack {
        public abstract void callBack();
    }
}
