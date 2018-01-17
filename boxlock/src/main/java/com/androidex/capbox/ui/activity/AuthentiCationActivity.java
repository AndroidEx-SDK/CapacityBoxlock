package com.androidex.capbox.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.utils.CommonKit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
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
 * 身份证拍照页面
 * Created by androidex on 2017/8/26.
 */

public class AuthentiCationActivity extends BaseActivity {
    @Bind(R.id.et_name)
    TextView et_name;
    @Bind(R.id.et_cardID)
    TextView et_cardID;
    @Bind(R.id.tv_getCaptcha)
    TextView tv_getCaptcha;
    @Bind(R.id.tv_register)
    TextView tv_register;

    OkHttpClient mOkHttpClient;
    private String mPhotoPath;
    private static final int UPDATE_TEXTVIEW = 1;
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

                    et_name.setText(Name);
                    et_cardID.setText(ID_number);

                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_authentication;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        initClient();
    }

    @Override
    public void setListener() {

    }

    @OnClick({
            R.id.tv_getCaptcha,
            R.id.tv_register,
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_getCaptcha:
                Intent intent = new Intent(AuthentiCationActivity.this, CameraActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_register:
                CommonKit.finishActivity(AuthentiCationActivity.this);
                break;
            default:
                break;
        }
    }

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
//                    //ui层回调
//                    mProgressBar.setProgress((int) (100 * percent));
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


    private void initClient() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResultsss");
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                String path = extras.getString("path");
                String type = extras.getString("type");
                Toast.makeText(getApplicationContext(), "path:" + path + " type:" + type, Toast.LENGTH_LONG).show();
                mPhotoPath = path;
                uploadAndRecognize();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
