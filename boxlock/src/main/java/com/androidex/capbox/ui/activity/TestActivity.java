package com.androidex.capbox.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.utils.RLog;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.Request;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/6/20
 */
public class TestActivity extends BaseActivity {

    @Bind(R.id.button2)
    Button button2;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View v) {

    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        try {
            NetApi.verifySN(new ResultCallBack(){
                @Override
                public void onSuccess(int statusCode, Headers headers, Object model) {
                    super.onSuccess(statusCode, headers, model);
                    RLog.d("校验结果 "+model.toString());
                }

                @Override
                public void onFailure(int statusCode, Request request, Exception e) {
                    super.onFailure(statusCode, request, e);
                    RLog.d("校验结果失败 statusCode"+statusCode);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
