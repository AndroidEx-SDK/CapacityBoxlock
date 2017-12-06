package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Headers;
import okhttp3.Request;

/**
 * @author liyp
 * @version 1.0.0
 * @description 找回密码
 * @createTime 2016/2/27
 * @editTime
 * @editor
 */
public class FindPwdActivity extends UserBaseActivity {

    @Bind(R.id.rl_title)
    SecondTitleBar rl_title;
    @Bind(R.id.et_phone)
    EditText et_phone;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_passwordConfirm)
    EditText et_passwordConfirm;
    @Bind(R.id.tv_getCaptcha)
    TextView tv_getCaptcha;
    @Bind(R.id.tv_findPwd)
    TextView tv_findPwd;
    @Bind(R.id.et_name)
    EditText et_name;
    @Bind(R.id.et_cardID)
    EditText et_cardID;

    Timer timer;
    TimerTask timerTask;
    static int MAX_SECONDS = 60;        //最长时间
    int currentSeconds = MAX_SECONDS;
    static final int MSG_UPDATE_SECONDS = 10;        //消息：更新时间

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_SECONDS:
                    if (currentSeconds > 1) {
                        currentSeconds--;
                        tv_getCaptcha.setText(getHtmlColorText(currentSeconds));
                        tv_getCaptcha.setEnabled(false);
                    } else {
                        tv_getCaptcha.setText(getString(R.string.label_get_captcha));
                        tv_getCaptcha.setEnabled(true);
                        cancelTask();
                    }
                    break;
            }
        }
    };

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void setListener() {

    }

    @OnClick({
            R.id.tv_findPwd,
            R.id.tv_getCaptcha
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_findPwd:
                findPwd();
                break;
        }
    }

    /**
     * 找回密码
     */
    private void findPwd() {
        String phone = et_phone.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String cardId = et_cardID.getText().toString().trim();
        String newPassword = et_password.getText().toString().trim();
        String passwordConfirm = et_passwordConfirm.getText().toString().trim();

        if (!Pattern.compile(Constants.REGEX.REGEX_PHONE).matcher(phone).matches()) {
            CommonKit.focusView(et_phone);
            CommonKit.showErrorShort(context, getString(R.string.hint_phone_input));
            return;
        }
        if (!Pattern.compile(Constants.REGEX.REGEX_PASSWORD).matcher(newPassword).matches()) {
            CommonKit.focusView(et_password);
            CommonKit.showErrorShort(context, getString(R.string.hint_password_input));
            return;
        }

        //检查密码一致性
        if (!TextUtils.equals(newPassword, passwordConfirm)) {
            CommonKit.focusView(et_password);
            CommonKit.showErrorShort(context, getString(R.string.hint_password_not_equal));
            et_passwordConfirm.setText("");
            return;
        }

        NetApi.forgetPassword(getToken(),phone, CommonKit.getMd5Password(newPassword), name, cardId,"", new ResultCallBack<BaseModel>() {

            @Override
            public void onStart() {
                super.onStart();
                showSpinnerDlg(getString(R.string.label_ing));
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                super.onSuccess(statusCode, headers, model);
                dismissSpinnerDlg();
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CommonKit.showOkShort(context, getString(R.string.hint_find_pwd_ok));
                            CommonKit.finishActivity(context);
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "提交信息失败");
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "改用户已被注册");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                dismissSpinnerDlg();
                CommonKit.showErrorShort(context, getString(R.string.hint_find_pwd_fail));
            }
        });
    }

    @OnTextChanged({
            R.id.et_phone,
            R.id.et_name,
            R.id.et_cardID,
            R.id.et_password,
            R.id.et_passwordConfirm
    })
    public void setFindBtnEnable() {
        String phone = et_phone.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String cardId = et_cardID.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String passwordConfirm = et_passwordConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(passwordConfirm) || TextUtils.isEmpty(name)) {
            tv_findPwd.setEnabled(false);
        } else {
            tv_findPwd.setEnabled(true);
        }
    }

    /**
     * 开始任务
     */
    private void startTask() {
        currentSeconds = MAX_SECONDS;
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_UPDATE_SECONDS);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    /**
     * 取消任务
     */
    private void cancelTask() {
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    /**
     * "5s后可重新获取"生成
     *
     * @param data
     * @return
     */
    public Spanned getHtmlColorText(int data) {
        String text = "<font color='white'>%ds</font><font color='black'>可重新获取</font>";
        text = String.format(text, data);
        return Html.fromHtml(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, FindPwdActivity.class, null, false);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_find_pwd;
    }
}
