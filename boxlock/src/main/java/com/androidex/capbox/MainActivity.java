package com.androidex.capbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.data.Event;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.ui.fragment.BoxListFragment;
import com.androidex.capbox.ui.fragment.LockFragment;
import com.androidex.capbox.ui.fragment.MapFragment;
import com.androidex.capbox.ui.fragment.MeMainFragment;
import com.androidex.capbox.ui.fragment.WatchListFragment;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.capbox.ui.fragment.LockFragment.boxName;

public class MainActivity extends BaseActivity implements OnClickListener {
    public static String TAG = "MainActivity";
    @Bind(R.id.homepage_tab1)
    LinearLayout homepage_tab1;
    @Bind(R.id.homepage_tab2)
    LinearLayout homepage_tab2;
    @Bind(R.id.homepage_tab3)
    LinearLayout homepage_tab3;
    @Bind(R.id.homepage_tab4)
    LinearLayout homepage_tab4;
    @Bind(R.id.tbe_image1)
    ImageView iv1;
    @Bind(R.id.tbe_image2)
    ImageView iv2;
    @Bind(R.id.tbe_image3)
    ImageView iv3;
    @Bind(R.id.tbe_image4)
    ImageView iv4;
    @Bind(R.id.textView1)
    TextView tv1;
    @Bind(R.id.textView2)
    TextView tv2;
    @Bind(R.id.textView3)
    TextView tv3;
    @Bind(R.id.textView4)
    TextView tv4;
    @Bind(R.id.bmb2)
    BoomMenuButton bmb;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int currIndex = 0;
    private Fragment mainFragment;
    private Fragment lockFragment;
    private static List<Map<String, String>> mylist = new ArrayList<>();
    private int main_index = -1;

    @Override
    public void initData(Bundle savedInstanceState) {
        registerEventBusSticky();
        boxlist(true);
    }

    @Override
    public void setListener() {

    }

    /**
     * 初始化fragment
     */
    private void initPager() {
        homepage_tab1.setOnClickListener(this);
        homepage_tab2.setOnClickListener(this);
        homepage_tab3.setOnClickListener(this);
        homepage_tab4.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        if (mylist.size() == 0) {
            currIndex = 0;
            initImage();
            transaction = fragmentManager.beginTransaction();
            mainFragment = new BoxListFragment();
            transaction.replace(R.id.content, mainFragment);
            transaction.commit();
        } else {
            currIndex = 2;
            initImage();
            Bundle bundle = new Bundle();
            bundle.putString("mac", mylist.get(0).get("mac"));
            bundle.putString("name", mylist.get(0).get("name"));
            bundle.putString("uuid", mylist.get(0).get("uuid"));
            lockFragment = new LockFragment();
            lockFragment.setArguments(bundle);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content, lockFragment);
            transaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_tab1:
                if (currIndex == 0) {
                    break;
                } else {
                    currIndex = 0;
                    initImage();
                    transaction = fragmentManager.beginTransaction();
                    mainFragment = new BoxListFragment();
                    transaction.replace(R.id.content, mainFragment);
                    transaction.commit();
                    break;
                }
            case R.id.homepage_tab2:
                if (currIndex == 1) {
                    break;
                } else {
                    currIndex = 1;
                    initImage();
                    transaction = fragmentManager.beginTransaction();
                    WatchListFragment watchListFragment = new WatchListFragment();
                    transaction.replace(R.id.content, watchListFragment);
                    transaction.commit();
                    break;
                }
            case R.id.homepage_tab3:
                if (currIndex == 3) {
                    break;
                } else {
                    currIndex = 3;
                    initImage();
                    transaction = fragmentManager.beginTransaction();
                    Fragment mapfragment = new MapFragment();
                    transaction.replace(R.id.content, mapfragment);
                    transaction.commit();
                    break;
                }
            case R.id.homepage_tab4:
                if (currIndex == 4) {
                    break;
                } else {
                    currIndex = 4;
                    initImage();
                    transaction = fragmentManager.beginTransaction();
                    Fragment settingFragment = new MeMainFragment();
                    transaction.replace(R.id.content, settingFragment);
                    transaction.commit();
                    break;
                }
            default:
                break;
        }
    }

    /**
     * viewPager切换时，图片设置
     */
    private void initImage() {
        homepage_tab1.setBackgroundColor(Color.TRANSPARENT);
        homepage_tab2.setBackgroundColor(Color.TRANSPARENT);
        homepage_tab3.setBackgroundColor(Color.TRANSPARENT);
        homepage_tab4.setBackgroundColor(Color.TRANSPARENT);
        bmb.setNormalColor(R.color.temp_red);
        tv1.setTextColor(getResources().getColor(R.color.line_color));
        tv2.setTextColor(getResources().getColor(R.color.line_color));
        tv3.setTextColor(getResources().getColor(R.color.line_color));
        tv4.setTextColor(getResources().getColor(R.color.line_color));
        iv1.setImageResource(R.mipmap.bottom_box_gray);
        iv2.setImageResource(R.mipmap.bottom_watch_gray);
        iv3.setImageResource(R.mipmap.bottom_location_gray);
        iv4.setImageResource(R.mipmap.bottom_setting_gray);
        switch (currIndex) {
            case 0:
                homepage_tab1.setBackground(getResources().getDrawable(R.color.blue));
                iv1.setImageResource(R.mipmap.bottom_box_white);
                tv1.setTextColor(Color.WHITE);
                break;
            case 1:
                homepage_tab2.setBackground(getResources().getDrawable(R.color.blue));
                iv2.setImageResource(R.mipmap.bottom_watch_white);
                tv2.setTextColor(Color.WHITE);
                break;
            case 2:
                bmb.setNormalColor(R.color.blue);
                break;
            case 3:
                homepage_tab3.setBackground(getResources().getDrawable(R.color.blue));
                iv3.setImageResource(R.mipmap.bottom_location_white);
                tv3.setTextColor(Color.WHITE);
                break;
            case 4:
                homepage_tab4.setBackground(getResources().getDrawable(R.color.blue));
                iv4.setImageResource(R.mipmap.bottom_setting_white);
                tv4.setTextColor(Color.WHITE);
                break;
            default:
                break;
        }
    }


    private BoomMenuButton initBmb() {
        bmb.clearBuilders();
        switch (mylist.size()) {
            case 0://pieceNumber
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_1);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_1);
                break;
            case 1:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_1);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_1);
                break;
            case 2:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_1);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_1);
                break;
            case 3:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_4);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_4);

                break;
            case 4:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_4_2);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_4_2);
                break;
            case 5:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_3);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_5_3);
                break;
            case 6:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_6_3);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_6_3);
                break;
            case 7:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_7_3);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_7_3);
                break;
            case 8:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_8_4);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_8_4);
                break;
            case 9:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_3);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_3);
                break;
            default:
                bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_3);
                bmb.setButtonPlaceEnum(ButtonPlaceEnum.Horizontal);
                break;
        }
        assert bmb != null;

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            Log.e(TAG, "index=" + index);
                            if (mylist.size() == 0) {
                                if (currIndex == 0) {
                                    return;
                                } else {
                                    currIndex = 0;
                                    initImage();
                                    transaction = fragmentManager.beginTransaction();
                                    mainFragment = new BoxListFragment();
                                    transaction.replace(R.id.content, mainFragment);
                                    transaction.commit();
                                }
                            } else {
                                if (currIndex == 2 && main_index == index) {
                                    return;
                                } else {
                                    currIndex = 2;
                                    main_index = index;
                                    initImage();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("mac", mylist.get(index).get("mac"));//这里的values就是我们要传的值
                                    bundle.putString("name", mylist.get(index).get("name"));//这里的values就是我们要传的值
                                    bundle.putString("uuid", mylist.get(index).get("uuid"));//这里的values就是我们要传的值
                                    lockFragment = new LockFragment();
                                    lockFragment.setArguments(bundle);
                                    transaction = fragmentManager.beginTransaction();
                                    transaction.replace(R.id.content, lockFragment);
                                    transaction.commit();
                                }
                            }
                        }
                    })
                    .normalImageRes(getImageResource())
                    .normalText(getext(i))
                    .normalColorRes(R.color.theme_color);
            bmb.addBuilder(builder);
        }
        return bmb;
    }

    private static int imageResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    private static String getext(int i) {
        if (mylist.size() > 0) {
            return mylist.get(i).get("name");
        } else return boxName;
    }

    private static int[] imageResources = new int[]{
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white,
            R.mipmap.ic_box_white
    };

    /**
     * 获取设备列表
     */
    public void boxlist(final boolean isBind) {
        if (!CommonKit.isNetworkAvailable(context)) {
            CommonKit.showErrorShort(context, "设备未连接网络");
            return;
        }
        NetApi.boxlist(getToken(), getUserName(), new ResultCallBack<BoxDeviceModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BoxDeviceModel model) {
                super.onSuccess(statusCode, headers, model);
                dismissSpinnerDlg(true);
                if (model != null) {
                    switch (model.code) {
                        case Constants.API.API_OK:
                            mylist.clear();
                            for (BoxDeviceModel.device device : model.devicelist) {
                                Map<String, String> map = new HashMap<>();
                                if (device.boxName.equals("Box")) {
                                    map.put("name", "Box" + device.mac.substring(device.mac.length() - 2));
                                } else {
                                    map.put("name", device.boxName);
                                }
                                map.put("uuid", device.uuid);
                                map.put("mac", device.mac);
                                map.put("deviceStatus", "" + device.deviceStatus);
                                map.put("isdefault", "" + device.isDefault);
                                map.put("isOnLine", "" + device.isOnLine);
                                mylist.add(map);
                            }
                            if (model.devicelist.size() == 0) {
                                CommonKit.showErrorShort(context, "未绑定任何设备");
                                L.e(TAG + "刷新列表无数据");
                            } else {

                                L.e(TAG + "绑定的设备数量为：" + mylist.size());
                            }
                            break;
                        case Constants.API.API_FAIL:
                            CommonKit.showErrorShort(context, "未绑定任何设备");
                            break;
                        case Constants.API.API_NOPERMMISION:
                            CommonKit.showErrorShort(context, "获取设备列表失败");
                            break;
                        default:
                            if (model.info != null) {
                                CommonKit.showErrorShort(context, model.info);
                            }
                            break;
                    }
                }
                if (isBind) {
                    initPager();
                }
                initBmb();
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                dismissSpinnerDlg(true);
                CommonKit.showErrorShort(context, getString(R.string.label_intnet_fail));
                initPager();
                initBmb();
            }

            @Override
            public void onStart() {
                super.onStart();
                showSpinnerDlg(true, getString(R.string.label_getlist_ing));
            }
        });
    }

    /**
     * 更新状态
     *
     * @param event
     */
    public void onEvent(Event.BoxBindChange event) {
        boxlist(true);    //刷新数据
    }

    /**
     * 解除绑定时触发
     *
     * @param event
     */
    public void onEvent(Event.BoxRelieveBind event) {
        boxlist(false);
    }

    /**
     * 退出主界面
     *
     * @param event
     */
    public void onEvent(Event.UserLoginEvent event) {
        CommonKit.finishActivity(context);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterEventBus();
    }

    public static void lauch(Activity activity) {
        CommonKit.startActivity(activity, MainActivity.class, null, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }
}
