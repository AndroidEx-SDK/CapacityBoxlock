package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.androidex.capbox.R;
import com.androidex.capbox.ui.view.ZItem;

public class LockScreenActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("LockScreenActivity", "我启动了");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |               //这个在锁屏状态下
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON          //这个是点亮屏幕
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD      //这个是透过锁屏界面，相当与解锁，但实质没有
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      //这个是保持屏幕常亮。
        setContentView(R.layout.activity_lock_screen);
        initData();
    }

    public void initData() {
        ZItem xitem = (ZItem) findViewById(R.id.textView1);
        xitem.setZItemListener(new ZItem.ZItemListener() {

            @Override
            public void onRight() {
                //CommonKit.finishActivity(LockScreenActivity.this);
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }

            @Override
            public void onLeft() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
