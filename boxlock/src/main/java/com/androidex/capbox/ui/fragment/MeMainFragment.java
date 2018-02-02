package com.androidex.capbox.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseFragment;
import com.androidex.capbox.callback.ItemClickCallBack;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.service.MyBleService;
import com.androidex.capbox.ui.activity.ConnectDeviceListActivity;
import com.androidex.capbox.ui.activity.ImageClipActivity;
import com.androidex.capbox.ui.activity.ImageGridActivity;
import com.androidex.capbox.ui.activity.SettingActivity;
import com.androidex.capbox.ui.activity.TypeOfAlarmActivity;
import com.androidex.capbox.ui.widget.SingleCheckListDialog;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.RLog;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;

import butterknife.Bind;

import static com.androidex.boxlib.cache.SharedPreTool.IS_OPEN_LOCKSCREEN;
import static com.androidex.capbox.ui.activity.ImageClipActivity.PARAM_BORDER_WIDTH;
import static com.androidex.capbox.ui.activity.ImageClipActivity.PARAM_IMAGE_PATH;
import static com.androidex.capbox.ui.activity.ImageGridActivity.PARAM_CLIP_WIDTH;
import static com.androidex.capbox.ui.activity.ImageGridActivity.PARAM_IS_CAPTURE;
import static com.androidex.capbox.ui.activity.ImageGridActivity.PARAM_SELECT_MAX_COUNT;
import static com.androidex.capbox.utils.Constants.EXTRA_USER_HEAD;

public class MeMainFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private static String TAG = "MeMainFragment";
    public static final int REQ_SELECT_USER_HEAD = 500; // 选择用户头像
    public static final int REQ_IMAGE_CLIP = 300; // 图片剪裁
    public static final int REQ_CAMERA = 100; // 照相

    @Bind(R.id.settint_bt_user)
    TextView tv_setting;
    @Bind(R.id.setting_alarm)
    LinearLayout setting_alarm;
    @Bind(R.id.ll_connectDevice)
    LinearLayout ll_connectDevice;
    @Bind(R.id.tv_username)
    TextView tv_username;
    @Bind(R.id.setting_distance)
    Spinner setting_distance;
    @Bind(R.id.tb_alarm)
    ToggleButton tb_alarm;
    @Bind(R.id.tb_lockscreen)
    ToggleButton tb_lockscreen;
    @Bind(R.id.iv_head)
    RoundedImageView iv_head;
    @Bind(R.id.rl_head)
    RelativeLayout rl_head;

    private boolean isToast = false;
    private boolean isToast_lockscreen = false;
    SingleCheckListDialog editHeadDlg;  //修改头像
    private Uri photoUri;

    @Override
    public void initData() {
        initView();
        isToast = false;
        isToast_lockscreen = false;
        if (SharedPreTool.getInstance(context).getBoolData(SharedPreTool.IS_POLICE, true)) {
            tb_alarm.setChecked(true);
        } else {
            tb_alarm.setChecked(false);
        }
        if (SharedPreTool.getInstance(context).getBoolData(IS_OPEN_LOCKSCREEN, true)) {
            tb_lockscreen.setChecked(true);
        } else {
            tb_lockscreen.setChecked(false);
        }
        String head_uri = SharedPreTool.getInstance(context).getStringData(EXTRA_USER_HEAD, null);
        if (head_uri != null) {
            uploadHead(head_uri);
        }
    }

    @Override
    public void setListener() {
        rl_head.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        setting_alarm.setOnClickListener(this);
        ll_connectDevice.setOnClickListener(this);
        tb_alarm.setOnCheckedChangeListener(this);
        tb_lockscreen.setOnCheckedChangeListener(this);
    }

    /**
     * 初始化
     */
    private void initView() {
        String username = SharedPreTool.getInstance(context).getStringData(SharedPreTool.PHONE, null);
        if (username != null) {
            tv_username.setText(username);
        }
        int rssiMaxValue = MyBleService.get().getRssiMaxValue();
        settingDistance();//报警距离
        if (rssiMaxValue >= 0) {
            setting_distance.setSelection(0);
        } else if (rssiMaxValue == -70) {
            setting_distance.setSelection(1);
        } else if (rssiMaxValue == -80) {
            setting_distance.setSelection(2);
        } else if (rssiMaxValue == -90) {
            setting_distance.setSelection(3);
        } else if (rssiMaxValue == -98) {
            setting_distance.setSelection(4);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settint_bt_user://
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_alarm://报警设置
                Intent intent_1 = new Intent(context, TypeOfAlarmActivity.class);
                startActivity(intent_1);
                break;
            case R.id.ll_connectDevice://已连接设备
                ConnectDeviceListActivity.lauch(context);
                break;
            case R.id.rl_head:
                showHeadDlg();
                break;
            default:
                break;
        }
    }

    //报警距离
    private void settingDistance() {
        String[] mItems2 = getResources().getStringArray(R.array.distance);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mItems2);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setting_distance.setAdapter(adapter1);//绑定 Adapter到控件
        setting_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.e(TAG, "pos=" + pos);
                switch (pos) {
                    case 0:
                        MyBleService.get().setRssiMaxValue(0);
                        break;
                    case 1:
                        MyBleService.get().setRssiMaxValue(-70);
                        break;
                    case 2:
                        MyBleService.get().setRssiMaxValue(-80);
                        break;
                    case 3:
                        MyBleService.get().setRssiMaxValue(-90);
                        break;
                    case 4:
                        MyBleService.get().setRssiMaxValue(-98);
                        break;
                    default:
                        MyBleService.get().setRssiMaxValue(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    /**
     * 选择头像对话框
     */
    private void showHeadDlg() {
        if (editHeadDlg == null) {
            editHeadDlg = new SingleCheckListDialog(context);
        }
        editHeadDlg.title(getString(R.string.label_edit_head))
                .data(getResources().getStringArray(R.array.edit_head_opts))
                .setItemClickCallBack(new ItemClickCallBack<String>() {
                    @Override
                    public void onItemClick(int position, String model, int tag) {
                        super.onItemClick(position, model, tag);

                        switch (position) {
                            case 0: //拍照
                                takePhoto();
                                break;

                            case 1: //从相册选择
                                Bundle params = new Bundle();
                                params.putBoolean(PARAM_IS_CAPTURE, false);
                                params.putInt(PARAM_SELECT_MAX_COUNT, 1);
                                params.putInt(PARAM_CLIP_WIDTH, 300);

                                Intent intent = new Intent();
                                intent.setClass(context, ImageGridActivity.class);
                                intent.putExtras(params == null ? new Bundle() : params);
                                startActivityForResult(intent, REQ_SELECT_USER_HEAD);
                                context.overridePendingTransition(R.anim.in_from_right,
                                        R.anim.out_to_left);

                                //ImageGridActivity.lauch(context, false, 1, 300, REQ_SELECT_USER_HEAD);
                                break;

                            case 2://取消
                                editHeadDlg.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        photoUri = CommonKit.getOutputMediaFileUri(context);
        //CommonKit.startCameraActivity(context, REQ_CAMERA, photoUri);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQ_CAMERA);
        context.overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_alarm://报警开关
                Loge(TAG, "alarm开关  " + isChecked);
                if (isChecked) {
                    SharedPreTool.getInstance(context).setBoolData(SharedPreTool.IS_POLICE, true);
                } else {
                    SharedPreTool.getInstance(context).setBoolData(SharedPreTool.IS_POLICE, false);
                    if (!isToast) {
                        isToast = true;
                        return;
                    }
                }
                if (SharedPreTool.getInstance(context).getBoolData(SharedPreTool.IS_POLICE, true)) {
                    CommonKit.showOkShort(context, "打开报警开关成功");
                } else {
                    CommonKit.showOkShort(context, "关闭报警开关成功");
                }
                break;
            case R.id.tb_lockscreen://锁屏开关
                Loge(TAG, "alarm开关  " + isChecked);
                if (isChecked) {
                    SharedPreTool.getInstance(context).setBoolData(IS_OPEN_LOCKSCREEN, true);
                } else {
                    SharedPreTool.getInstance(context).setBoolData(IS_OPEN_LOCKSCREEN, false);
                    if (!isToast_lockscreen) {
                        isToast_lockscreen = true;
                        return;
                    }
                }
                if (SharedPreTool.getInstance(context).getBoolData(IS_OPEN_LOCKSCREEN, true)) {
                    CommonKit.showOkShort(context, "锁屏开关打开");
                } else {
                    CommonKit.showOkShort(context, "锁屏功能关闭");
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                String photoPath = photoUri.toString()
                        .replaceFirst("file:///", "/").trim();
                if (new File(photoPath).exists()) {
                    // 发送广播通知系统
                    context.sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri));


                    Bundle params = new Bundle();
                    params.putString(PARAM_IMAGE_PATH, photoPath);
                    params.putInt(PARAM_BORDER_WIDTH, 300);

                    Intent intent = new Intent();
                    intent.setClass(context, ImageClipActivity.class);//跳转到裁剪页面
                    intent.putExtras(params == null ? new Bundle() : params);
                    startActivityForResult(intent, REQ_SELECT_USER_HEAD);
                    context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                    // 进入剪裁界面
                    // ImageClipActivity.lauch(context, photoPath, 300, REQ_IMAGE_CLIP);
                }
            }
        } else if (requestCode == REQ_SELECT_USER_HEAD) {
            if (resultCode == Activity.RESULT_OK) {
                // 裁剪
                String filePath = data.getStringExtra(ImageGridActivity.OUT_SELECT_IMAGE_PATH);      //剪裁后的文件路径
//                String wrapperPath = new StringBuffer("file://").append(filePath).toString();
//                UILKit.loadHeadLocal(wrapperPath, iv_head);

                uploadHead(filePath);
            }
        } else if (requestCode == REQ_IMAGE_CLIP) {
            if (resultCode == Activity.RESULT_OK) {
                // 裁剪
                String filePath = data.getStringExtra(ImageClipActivity.OUT_IMAGE_PATH);      //剪裁后的文件路径
//                String wrapperPath = new StringBuffer("file://").append(filePath).toString();
//                UILKit.loadHeadLocal(wrapperPath, iv_head);
                uploadHead(filePath);
            }
        }
    }

    /**
     * 上传头像
     *
     * @param filePath
     */
    private void uploadHead(String filePath) {
        if (TextUtils.isEmpty(filePath)) return;
        RLog.e("加载头像图片" + filePath);
        File file = new File(filePath);
        if (!file.exists()) return;
        RLog.e("加载头像图片11111" + filePath);

        SharedPreTool.getInstance(context).setStringData(EXTRA_USER_HEAD, filePath);
        iv_head.setImageURI(Uri.fromFile(file));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_memain;
    }

}
