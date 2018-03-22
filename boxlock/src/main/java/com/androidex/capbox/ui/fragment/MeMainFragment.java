package com.androidex.capbox.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.androidex.capbox.ui.activity.SettingActivity;
import com.androidex.capbox.ui.activity.TypeOfAlarmActivity;
import com.androidex.capbox.ui.widget.SingleCheckListDialog;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.PhotoUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;

import butterknife.Bind;

import static com.androidex.boxlib.cache.SharedPreTool.IS_OPEN_LOCKSCREEN;
import static com.androidex.boxlib.utils.ImageUtils.cropImageUri;
import static com.androidex.capbox.utils.Constants.CODE.CAMERA_PERMISSIONS_REQUEST_CODE;
import static com.androidex.capbox.utils.Constants.CODE.STORAGE_PERMISSIONS_REQUEST_CODE;
import static com.androidex.capbox.utils.Constants.EXTRA_PACKAGE_NAME;
import static com.androidex.capbox.utils.Constants.EXTRA_USER_HEAD;

public class MeMainFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private static String TAG = "MeMainFragment";
    public static final int REQ_SELECT_USER_HEAD = 500; // 选择用户头像
    public static final int REQ_IMAGE_CLIP = 300; // 图片剪裁
    public static final int REQ_CAMERA = 100; // 照相
    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;

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
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
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
        int rssiMaxValue = MyBleService.getInstance().getRssiMaxValue();
        settingDistance();//报警距离
        if (rssiMaxValue >= 0) {
            setting_distance.setSelection(0);
        } else if (rssiMaxValue == -70) {//较近
            setting_distance.setSelection(1);
        } else if (rssiMaxValue == -80) {//近
            setting_distance.setSelection(2);
        } else if (rssiMaxValue == -90) {//较远
            setting_distance.setSelection(3);
        } else if (rssiMaxValue == -98) {//远
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
                        MyBleService.getInstance().setRssiMaxValue(0);
                        break;
                    case 1:
                        MyBleService.getInstance().setRssiMaxValue(-70);
                        break;
                    case 2:
                        MyBleService.getInstance().setRssiMaxValue(-80);
                        break;
                    case 3:
                        MyBleService.getInstance().setRssiMaxValue(-90);
                        break;
                    case 4:
                        MyBleService.getInstance().setRssiMaxValue(-98);
                        break;
                    default:
                        MyBleService.getInstance().setRssiMaxValue(0);
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                            || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                                            CommonKit.showErrorShort(context, "您已经拒绝过一次");
                                        }
                                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
                                    } else {//已经有权限
                                        takePhoto();
                                    }
                                } else {
                                    takePhoto();
                                }
                                break;

                            case 1: //从相册选择
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
                                    } else {
                                        PhotoUtils.openPic(fragment, REQ_SELECT_USER_HEAD);//打开系统相册
                                    }
                                } else {
                                    PhotoUtils.openPic(fragment, REQ_SELECT_USER_HEAD);//打开系统相册
                                }
                                break;

                            case 2://取消
                                editHeadDlg.dismiss();
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        photoUri = CommonKit.getOutputMediaFileUri(context, EXTRA_PACKAGE_NAME);
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQ_CAMERA);
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.tb_alarm://报警开关
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (CommonKit.isExitsSdcard()) {
                        takePhoto();
                    } else {
                        CommonKit.showErrorShort(context, "设备没有SD卡！");
                    }
                } else {
                    CommonKit.showErrorShort(context, "请允许打开相机！！");
                }
                break;
            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(fragment, REQ_SELECT_USER_HEAD); //打开系统相册
                } else {
                    CommonKit.showOkShort(context, "请允许打开SD卡");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CAMERA://拍照完成回调
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(fragment, photoUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, REQ_IMAGE_CLIP);
                    break;
                case REQ_SELECT_USER_HEAD:
                    if (CommonKit.isExitsSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(getActivity(), data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(getActivity(), EXTRA_PACKAGE_NAME, new File(newUri.getPath()));
                        }
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, REQ_IMAGE_CLIP);
                    } else {
                        CommonKit.showErrorShort(context, "设备没有SD卡！");
                    }
                    break;
                case REQ_IMAGE_CLIP:
                    SharedPreTool.getInstance(context).setStringData(EXTRA_USER_HEAD, cropImageUri.getPath());
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, getActivity());
                    if (bitmap != null)
                        iv_head.setImageBitmap(bitmap);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 上传头像
     *
     * @param filePath 裁剪后的路径
     */
    private void uploadHead(String filePath) {
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        if (!file.exists()) return;
        SharedPreTool.getInstance(context).setStringData(EXTRA_USER_HEAD, filePath);
        iv_head.setImageURI(Uri.fromFile(file));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_memain;
    }

}
