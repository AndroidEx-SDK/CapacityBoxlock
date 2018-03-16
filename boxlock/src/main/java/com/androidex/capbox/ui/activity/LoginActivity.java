package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.ui.widget.ThirdTitleBar;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.androidex.capbox.utils.RLog;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.androidex.capbox.data.cache.SharedPreTool.LOGIN_STATUS;

public class LoginActivity extends UserBaseActivity {
    @Bind(R.id.thirdtitlebar)
    ThirdTitleBar thirdtitlebar;
    @Bind(R.id.et_phone)
    EditText et_phone;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_authcode)
    EditText et_authcode;
    @Bind(R.id.tv_login)
    TextView tv_login;
    @Bind(R.id.tv_authcode)
    TextView tv_authcode;
    @Bind(R.id.cb_automatic_login)
    CheckBox cb_automatic_login;

    static UserBaseActivity.CallBackAction callBackAction;

    @Override
    public void initData(Bundle savedInstanceState) {
        initTitleBar();
        initView();
        boolean boolData = SharedPreTool.getInstance(context).getBoolData(SharedPreTool.AUTOMATIC_LOGIN, false);
        if (boolData) {
            RLog.e("isaulogin=" + boolData);
            String phone = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
            String md5Pwd = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PASSWORD, null);
            if (phone != null && md5Pwd != null) {
                RLog.e("自动登录");
                automaticLogin(phone, md5Pwd);//自动登录
            } else {
                getAuthCode();
            }
        }
    }

    private void initView() {
        cb_automatic_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                RLog.e("compounbtn change b = " + b);
                SharedPreTool.getInstance(context).setBoolData(SharedPreTool.AUTOMATIC_LOGIN, b);
            }
        });
        boolean boolData = SharedPreTool.getInstance(context).getBoolData(SharedPreTool.AUTOMATIC_LOGIN, false);
        cb_automatic_login.setChecked(boolData);
    }

    private void initTitleBar() {
        thirdtitlebar.getLeftBtn().setVisibility(View.GONE);
    }

    @Override
    public void setListener() {

    }

    @OnClick({
            R.id.tv_login,
            R.id.tv_register,
            R.id.tv_forgetPassword,
            R.id.tv_authcode,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                login();
                break;

            case R.id.tv_register:
                RegisterActivity.lauch(context);
                break;

            case R.id.tv_forgetPassword:
                Forget2Activtiy.lauch(context);
                break;
            case R.id.tv_authcode:
                getAuthCode();
                break;
            default:
                break;
        }
    }

    /**
     * 自动登录
     *
     * @param phone
     * @param md5Pwd
     */
    private void automaticLogin(final String phone, final String md5Pwd) {
        getAuthCode(new CallDataBackAction() {
            @Override
            public void action(String authcode) {
                if (authcode != null) {
                    userLogin(phone, md5Pwd, authcode, new CallBackAction() {
                        @Override
                        public void action() {
                            MainActivity.lauch(context);
                            if (callBackAction != null) {
                                callBackAction.action();
                                callBackAction = null;
                            }
                        }
                    });
                } else {
                    CommonKit.showErrorShort(context, "自动登录失败");
                    initView();
                }
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getAuthCode() {
        RLog.e("开始获取验证码");
        getAuthCode(new CallDataBackAction() {
            @Override
            public void action(String authcode) {
                if (authcode != null) {
                    RLog.e("获取到验证码=" + authcode);
                    tv_authcode.setText(authcode);
                } else {
                    CommonKit.showErrorShort(context, "获取验证码失败");
                }
            }
        });
    }

    @OnTextChanged({
            R.id.et_phone,
            R.id.et_password,
            R.id.et_authcode,
    })
    public void setLoginBtnState() {
        String phone = et_phone.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String authcode = et_authcode.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(authcode)) {
            tv_login.setEnabled(false);
        } else {
            tv_login.setEnabled(true);
        }
    }

    private void login() {
        String phone = et_phone.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String input_authcode = et_authcode.getText().toString().trim();
        String get_authcode = tv_authcode.getText().toString().trim();

        if (!Pattern.compile(Constants.REGEX.REGEX_PHONE).matcher(phone).matches()) {
            CommonKit.focusView(et_phone);
            CommonKit.showErrorShort(context, getString(R.string.hint_phone_input));
            return;
        } else if (!Pattern.compile(Constants.REGEX.REGEX_PASSWORD).matcher(password).matches()) {
            CommonKit.focusView(et_password);
            CommonKit.showErrorShort(context, getString(R.string.hint_password_input));
            return;
        } else if (!input_authcode.equalsIgnoreCase(get_authcode)) {
            CommonKit.focusView(et_authcode);
            CommonKit.showErrorShort(context, getString(R.string.hint_authcode_input));
            return;
        } else {
            final String md5Pwd = CommonKit.getMd5Password(password);
            userLogin(phone, md5Pwd, input_authcode, new CallBackAction() {
                @Override
                public void action() {
                    if (callBackAction != null) {
                        callBackAction.action();
                        callBackAction = null;
                    }
                    SharedPreTool.getInstance(context).setBoolData(LOGIN_STATUS, true);
                    MainActivity.lauch(context);
                }
            });
        }
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, LoginActivity.class, null, true);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }
}
