package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BECOMEFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_BOX_MAC;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_CARRYFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_LOCK_STARTS;
import static com.androidex.boxlib.utils.BleConstants.BLE.ACTION_POSSESSORFINGER;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_FAIL;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS;
import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_SUCCESS_ALLCONNECTED;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_DATA;
import static com.androidex.capbox.utils.Constants.CODE.REQUESTCODE_ADD_DEVICE;
import static com.androidex.capbox.utils.Constants.EXTRA_BOX_UUID;
import static com.androidex.capbox.utils.Constants.EXTRA_ITEM_ADDRESS;

/**
 * 配置腕表页面
 */
public class WatchDetialActivity extends BaseActivity {
    @Bind(R.id.tv_boxConfig)
    TextView tv_boxConfig;

    private OkHttpClient mOkHttpClient;
    private String uuid;
    private String mac;
    private String carryName;
    private String carryCardId;
    private String carryCall;
    private String carryFinger1 = null;//携行人指纹信息
    private String carryFinger2 = null;//携行人指纹信息
    private String carryFinger3 = null;//携行人指纹信息
    private String become = null;    //静默开启A 关闭B
    private static final String TAG = WatchDetialActivity.class.getSimpleName();
    private int carryPersonNum = 0;//携行人员人数跟腕表数量对应
    private ArrayList<Map<String, String>> mapArrayList = new ArrayList<>();//添加的设备集合
    private ArrayList<String> list_devicemac = new ArrayList<>();//添加的设备集合的MAC
    private static final int UPDATE_TEXTVIEW = 0x01;
    private static final int UPDATE_FAIL = 0x00;
    private DataBroadcast dataBroadcast;
    private Context mContext;
    private boolean isConnect = false;

    @Override
    public void initData(Bundle savedInstanceState) {
        mContext = context;
        uuid = getIntent().getStringExtra(EXTRA_BOX_UUID);
        mac = getIntent().getStringExtra(EXTRA_ITEM_ADDRESS);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_CONN_SUCCESS);
        intentFilter.addAction(BLE_CONN_SUCCESS_ALLCONNECTED);
        intentFilter.addAction(BLE_CONN_FAIL);
        intentFilter.addAction(BLE_CONN_DIS);
        intentFilter.addAction(ACTION_LOCK_STARTS);
        intentFilter.addAction(ACTION_POSSESSORFINGER);//获取到所有人的指纹信息
        intentFilter.addAction(ACTION_BECOMEFINGER);//获取开启静默功能的指纹信息
        intentFilter.addAction(ACTION_CARRYFINGER);//获取到携行人的指纹信息
        intentFilter.addAction(ACTION_BOX_MAC);//发送mac给腕表
        dataBroadcast = new DataBroadcast();
        context.registerReceiver(dataBroadcast, intentFilter);
    }

    @OnClick({
            R.id.ll_possessorFinger,
            R.id.photograph,
            R.id.tv_boxConfig
    })
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_possessorFinger://添加箱体所有人的指纹信息
                break;

            case R.id.photograph://拍摄
                Intent intent = new Intent(WatchDetialActivity.this, AuthentiCationActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_boxConfig://配置
                watchconfig(uuid, mac);

            default:
                break;
        }
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {

    }

    private void watchconfig(String uuid, String mac) {
//        *token:’ANDB29988330’,
//        *username:’13828840464’,
//        *uuid:’’                        //密管箱的uuid
//        *mac:’FF:FF:FF:FF:FF:FF’,        //腕表的mac
//        *carryName:’李永平’，              //携行人员姓名
//        *carryCardId:’410219299202106519’,   //携人员身份证号
//        *carryFinger1:’fffffff’,                //携行人员指纹信息或id1
//        *carryFinger2:’fffffff’,                //携行人员指纹信息或id2
//        *carryFinger3:’fffffff’,                //携行人员指纹信息或id3
//        *carryCall:’13545457676’,             //携行人手机号
//        *become:’A’
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.watchConfig(getToken(), getUserName(), uuid, mac,
                carryName, carryCardId, carryFinger1, carryFinger2,
                carryFinger3, carryCall, become,
                new ResultCallBack<BaseModel>() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, BaseModel model) {
                        super.onSuccess(statusCode, headers, model);
                        if (model != null) {
                            switch (model.code) {
                                case Constants.API.API_OK:
                                    Log.e("TAG", "API_OK");
                                    break;
                                case Constants.API.API_FAIL:
                                    Log.e("TAG", "api_nopermmision");
                                    break;
                                case Constants.API.API_NOPERMMISION:
                                    Log.e("TAG", "api_nopermmision");
                                    break;
                                default:
                                    break;
                            }
                            Log.e("TAG", model.toString());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Request request, Exception e) {
                        super.onFailure(statusCode, request, e);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }
                });
    }


    public class DataBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceMac = intent.getStringExtra(BLECONSTANTS_ADDRESS);
            if (!mac.equals(deviceMac)) return;
            byte[] b = intent.getByteArrayExtra(BLECONSTANTS_DATA);
            switch (intent.getAction()) {
                case BLE_CONN_SUCCESS:
                case BLE_CONN_SUCCESS_ALLCONNECTED:
                    Log.d(TAG, "连接成功=");
                    isConnect = true;
                    CommonKit.showOkShort(mContext, "连接成功");
                    break;

                case BLE_CONN_DIS:
                    Log.d(TAG, "断开连接=");
                    isConnect = false;
                    CommonKit.showErrorShort(mContext, "断开连接");
                    break;
                case ACTION_CARRYFINGER://携行人的指纹信息
                    Log.e(TAG, "携行人的指纹信息");
                    int i = 0;
                    switch (b[2]) {
                        case (byte) 0x01://识别成功
                            CommonKit.showOkShort(mContext, "录入成功");
                            i++;
                            if (i == 1) {
                                carryFinger1 = "1";
                            } else if (i == 2) {
                                carryFinger2 = "2";
                            } else if (i == 3) {
                                carryFinger3 = "3";
                            } else {
                                i = 0;
                                CommonKit.showOkShort(mContext, "录入完成");
                            }
                            break;
                        case (byte) 0x02://识别失败
                            CommonKit.showErrorShort(mContext, "指纹录入失败，请重新录入");
                            break;
                        default:

                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult");
        if (requestCode == REQUESTCODE_ADD_DEVICE) {//添加设备回调
            switch (resultCode) {
                case Activity.RESULT_OK:
                    list_devicemac = data.getStringArrayListExtra("list_devicemac");
                    for (int i = 0; i < list_devicemac.size(); i++) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(EXTRA_ITEM_ADDRESS, list_devicemac.get(i));
                        map.put("deviceType", "B");
                        mapArrayList.add(map);
                    }
                    if (list_devicemac != null) {
                        carryPersonNum = list_devicemac.size();
                    } else {
                        carryPersonNum = 0;
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    CommonKit.showErrorShort(context, "未添加新设备");
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(dataBroadcast);
    }

    public static void lauch(Activity activity, Bundle bundle) {
        CommonKit.startActivity(activity, WatchDetialActivity.class, bundle, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_watchdetial;
    }


}