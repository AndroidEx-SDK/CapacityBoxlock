package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.capbox.R;
import com.androidex.capbox.base.UserBaseActivity;
import com.androidex.capbox.data.cache.CacheManage;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.ResultModel;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.utils.CalendarUtil;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 注册页面
 */
public class RegisterActivity extends UserBaseActivity {
    @Bind(R.id.rl_title)
    SecondTitleBar rl_title;
    @Bind(R.id.et_phone)
    EditText et_phone;
    @Bind(R.id.et_cardID)
    EditText et_cardID;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_passwordConfirm)
    EditText et_passwordConfirm;
    @Bind(R.id.et_authcode)
    EditText et_authcode;
    @Bind(R.id.tv_register)
    TextView tv_register;
    @Bind(R.id.et_name)
    TextView et_name;
    @Bind(R.id.tv_warm_prompt)
    TextView tv_warm_prompt;
    @Bind(R.id.tv_authcode)
    TextView tv_authcode;
    @Bind(R.id.ll_register)
    LinearLayout ll_register;
    @Bind(R.id.ll_update)
    LinearLayout ll_update;

    OkHttpClient mOkHttpClient;
    public int id = 1;
    private String mPhotoPath;
    private static final int UPDATE_TEXTVIEW = 0x01;
    private static final int UPDATE_FAIL = 0x00;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TEXTVIEW:
                    String s = msg.obj.toString();
                    String pre = "姓名: ";
                    String suf = "性别: ";
                    String Name = s.substring((s.indexOf(pre) + pre.length()), s.indexOf(suf));
                    Log.e("姓名", Name);

                    String pre1 = "性别: ";
                    String suf1 = "民族: ";
                    String Gender = s.substring((s.indexOf(pre1) + pre1.length()), s.indexOf(suf1));
                    Log.e("性别", Gender);

                    String pre2 = "民族: ";
                    String suf2 = "出生: ";
                    String Nation = s.substring((s.indexOf(pre2) + pre2.length()), s.indexOf(suf2));
                    Log.e("民族", Nation);

                    String pre3 = "出生: ";
                    String suf3 = "住址: ";
                    String Native = s.substring((s.indexOf(pre3) + pre3.length()), s.indexOf(suf3));
                    Log.e("出生日期", Native);

                    String pre4 = "住址: ";
                    String suf4 = "公民身份号码: ";
                    String Address = s.substring((s.indexOf(pre4) + pre4.length()), s.indexOf(suf4));
                    Log.e("住址", Address);

                    String pre5 = "公民身份号码: ";
                    String suf5 = "签发机关: ";
                    String ID_number = s.substring((s.indexOf(pre5) + pre5.length()), s.indexOf(suf5));
                    Log.e("公民身份号码", ID_number);
                    if (Name != null && !Name.equals("")) {
                        et_name.setText(Name);
                    } else {
                        CommonKit.showErrorLong(context, "姓名为空请重新拍摄");
                        return;
                    }
                    if (ID_number != null && !ID_number.equals("")) {
                        et_cardID.setText(ID_number);
                    } else {
                        CommonKit.showErrorLong(context, "身份证号码为空请重新拍摄");
                        return;
                    }
                    //从数据库获取数据显示在textview
                    // User user1 = helper.query(1);
//                    Log.e("helper", String.valueOf(user1));
//                    Log.e("helper1", String.valueOf(helper.query(1)));
//                    Log.e("helpers", String.valueOf(helper));
//                    String username = user1.getName();
//                    Log.e("身份信息", String.valueOf(user1.getName()));
                    //增加
//                    initv(username);
                    break;
            }
        }
    };

    @Override
    public void initData(Bundle savedInstanceState) {
        rl_title.setRightText("重新注册");
        rl_title.getRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRegist(false);
            }
        });
        //先判断是否提交审核
        final Boolean isRegist = SharedPreTool.getInstance(context).getBoolData(SharedPreTool.IS_REGISTED, false);
        isRegist(isRegist);
        initClient();
        getAuthCode();
        getCheckResult();
    }

    @Override
    public void setListener() {

    }

    protected void initClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .build();
    }

    /**
     * 身份证识别，返回身份证信息
     */
    private void uploadAndRecognize() {
        if (!TextUtils.isEmpty(mPhotoPath)) {
            File file = new File(mPhotoPath);
            //构造上传请求，类似web表单
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"callbackurl\""), RequestBody.create(null, "/idcard/"))
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"action\""), RequestBody.create(null, "idcard"))
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"img\"; filename=\"idcardFront_user.jpg\""), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .build();
            //这个是ui线程回调，可直接操作UI
            RequestBody progressRequestBody = ProgressHelper.withProgress(requestBody, new ProgressUIListener() {
                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    Log.e("TAG", "numBytes:" + numBytes);
                    Log.e("TAG", "totalBytes" + totalBytes);
                    Log.e("TAG", percent * 100 + " % done ");
                    Log.e("TAG", "done:" + (percent >= 1.0));
                    Log.e("TAG", "================================");
                    //ui层回调
//				mProgressBar.setProgress((int) (100 * percent));
                }
            });
            //进行包装，使其支持进度回调
            final Request request = new Request.Builder()
                    .header("Host", "ocr.ccyunmai.com:8080")
                    .header("Origin", "http://ocr.ccyunmai.com:8080")
                    .header("Referer", "http://ocr.ccyunmai.com:8080/idcard/")
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2398.0 Safari/537.36")
                    .url("http://ocr.ccyunmai.com:8080/UploadImage.action")
                    .post(progressRequestBody)
                    .build();
            //开始请求
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message obtain = Message.obtain();
                    obtain.what = UPDATE_FAIL;
                    mHandler.sendMessage(obtain);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("----------->>", response.toString());
                    Log.e("===========>>", response.body().toString());
                    String result = response.body().string();
                    Document parse = Jsoup.parse(result);
                    Elements select = parse.select("div#ocrresult");
                    Log.e("TAG", "select：" + select.text());
                    Message obtain = Message.obtain();
                    obtain.what = UPDATE_TEXTVIEW;
                    obtain.obj = select.get(0).text();
                    mHandler.sendMessage(obtain);
                }
            });
        }
    }

    @OnClick({
            R.id.tv_register,
            R.id.tv_getCaptcha,
            R.id.tv_update,
            R.id.tv_authcode,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                String phone = et_phone.getText().toString();
                String name = et_name.getText().toString();
                String cardId = et_cardID.getText().toString();
                String password = et_password.getText().toString();
                String rePassw = et_passwordConfirm.getText().toString();
                String input_authcode = et_authcode.getText().toString().trim();
                String get_authcode = tv_authcode.getText().toString().trim();
                if (!Pattern.compile(Constants.REGEX.REGEX_MOBILE).matcher(phone).matches()) {
                    CommonKit.focusView(et_phone);
                    CommonKit.showErrorShort(context, getString(R.string.hint_phone_input));
                    return;
                } else if (!Pattern.compile(Constants.REGEX.REGEX_PASSWORD).matcher(password).matches()) {
                    CommonKit.focusView(et_password);
                    CommonKit.showErrorShort(context, getString(R.string.hint_password_input));
                    return;
                } else if (!Pattern.compile(Constants.REGEX.REGEX_CHINESE).matcher(name).matches()) {
                    CommonKit.focusView(et_name);
                    CommonKit.showErrorShort(context, getString(R.string.hint_name_input));
                    return;
                } else if (!CalendarUtil.is18ByteIdCardComplex(cardId)) {
                    CommonKit.focusView(et_cardID);
                    CommonKit.showErrorShort(context, getString(R.string.hint_cardid_input));
                    return;
                } else if (TextUtils.isEmpty(rePassw)) {
                    Toast.makeText(RegisterActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!TextUtils.equals(password, rePassw)) {//检查密码一致性
                    CommonKit.focusView(et_password);
                    CommonKit.showErrorShort(context, getString(R.string.hint_password_not_equal));
                    et_passwordConfirm.setText("");
                    return;
                } else if (!input_authcode.equalsIgnoreCase(get_authcode)) {
                    CommonKit.focusView(et_authcode);
                    CommonKit.showErrorShort(context, getString(R.string.hint_authcode_input));
                    return;
                } else {
                    regist(phone, name, cardId, password, input_authcode);
                }
                break;
            case R.id.tv_getCaptcha:
                startVerify();//启动身份验证界面
                break;
            case R.id.tv_update:
                getCheckResult();//获取审核结果
                break;
            case R.id.tv_authcode:
                getAuthCode();
                break;
            default:
                break;
        }
    }

    /**
     * 提交审核
     */
    protected void regist(String phone, String name, String cardId, String password, String authcode) {
        //对密码进行MD5加密
        final String md5Pwd = CommonKit.getMd5Password(password);
        /****第一种****/
        userRegister(phone, name, cardId, md5Pwd, authcode, new CallBackAction() {

            @Override
            public void action() {
                isRegist(true);//提交审核后显示刷新页面
                getCheckResult();
            }
        });
    }

    public void getCheckResult() {
        final String phone = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.getCheckResult(getToken(), phone, new ResultCallBack<ResultModel>() {

            @Override
            public void onSuccess(int statusCode, Headers headers, ResultModel model) {
                super.onSuccess(statusCode, headers, model);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            CacheManage.getFastCache().put(Constants.PARAM.CACHE_KEY_CUR_LOGIN_USER, model);
                            LoginActivity.lauch(context);
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, getResources().getString(R.string.hint_regist_fail));
                            tv_warm_prompt.setText(getResources().getString(R.string.hint_regist_fail));
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, getResources().getString(R.string.hint_regist_ok));
                            tv_warm_prompt.setText(getResources().getString(R.string.hint_regist_ok));
                            isRegist(true);
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                } else {
                    CommonKit.showErrorShort(context, "网络访问失败");
                }
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                CommonKit.showErrorShort(context, "网络访问失败");
            }
        });
    }

    /**
     * 跳转到身份识别页面
     */
    private void startVerify() {
        //查询数据库信息并跳转到拍摄页面
        Intent intent = new Intent(RegisterActivity.this, CameraActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult");
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                String path = extras.getString("path");
                mPhotoPath = path;
                uploadAndRecognize();
                Log.e("TAG", "获取回调并显示");
            }
        }
    }

    @OnTextChanged({
            R.id.et_phone,
            R.id.et_name,
            R.id.et_cardID,
            R.id.et_password,
            R.id.et_passwordConfirm,
            R.id.et_authcode
    })
    public void setRegisterBtnEnable() {
        String phone = et_phone.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String cardID = et_cardID.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String passwordConfirm = et_passwordConfirm.getText().toString().trim();
        String authcode = et_authcode.getText().toString().trim();
        if (TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(cardID)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(passwordConfirm)
                || TextUtils.isEmpty(authcode)) {
            tv_register.setEnabled(false);
        } else {
            tv_register.setEnabled(true);
        }
    }

    /**
     * 已经注册过了就显示等待审核页面
     *
     * @param flag
     */
    protected void isRegist(Boolean flag) {
        SharedPreTool.getInstance(context).setBoolData(SharedPreTool.IS_REGISTED, flag);
        clearText();
        if (flag) {
            rl_title.setTitle("提交审核");
            setVisible(rl_title.getRightTv());
            setVisible(ll_update);
            setGone(ll_register);
        } else {
            rl_title.setTitle("用户注册");
            setGone(rl_title.getRightTv());
            setGone(ll_update);
            setVisible(ll_register);
        }
    }

    /**
     * 获取验证码
     */
    protected void getAuthCode() {
        getAuthCode(new CallDataBackAction() {
            @Override
            public void action(String authcode) {
                if (authcode != null) {
                    tv_authcode.setText(authcode);
                } else {
                    CommonKit.showErrorShort(context, "获取验证码失败");
                }
            }
        });
    }

    public void clearText() {
        et_name.setText("");
        et_cardID.setText("");
        et_password.setText("");
        et_passwordConfirm.setText("");
        et_phone.setText("");
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, RegisterActivity.class, null, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }
}
