package com.androidex.capbox.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.ui.view.CircleTextProgressbar;

import butterknife.Bind;
import butterknife.OnClick;

public class SplashActivity extends UserBaseActivity {
    private final int SPLASH_DISPLAY_LENGHT = 3000;
    @Bind(R.id.customCountDownView)
    CircleTextProgressbar customCountDownView;

    @Override
    public void initData(Bundle savedInstanceState) {
        customCountDownView.setCountdownProgressListener(2, progressListener);
        customCountDownView.setTimeMillis(SPLASH_DISPLAY_LENGHT);
        customCountDownView.reStart();
    }

    /**
     * 进度监听
     */
    public CircleTextProgressbar.OnCountdownProgressListener progressListener = new CircleTextProgressbar.OnCountdownProgressListener() {
        @Override
        public void onProgress(int what, int progress) {
            if (what == 2) {
                customCountDownView.setText(progress + "s");
            }
            if (progress == 0) {
                FingerprintMainActivity.lauch(context);
            }
        }
    };

    @Override
    public void setListener() {

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @OnClick({
            R.id.customCountDownView
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.customCountDownView:
                customCountDownView.stop();
                FingerprintMainActivity.lauch(context);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }
}
