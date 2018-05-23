package com.androidex.capbox.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.androidex.capbox.R;
import com.androidex.capbox.data.net.NetType;
import com.androidex.capbox.data.net.base.L;
import com.androidex.capbox.ui.widget.InfoToast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author liyp
 * @version 1.0.0
 * @description 工具类集合
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class CommonKit {
    //------------------------Toast---------------------------

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showMsgShort(Context context, String msg) {
        InfoToast.showOkShort(context, msg);
    }

    /**
     * 长吐司
     *
     * @param context
     * @param msg
     */
    public static void showMsgLong(Context context, String msg) {
        InfoToast.showOkLong(context, msg);
    }


    public static void showOkShort(Context context, String msg) {
        InfoToast.showOkShort(context, msg);
    }

    public static void showOkLong(Context context, String msg) {
        InfoToast.showOkLong(context, msg);
    }

    public static void showErrorShort(Context context, String msg) {
        InfoToast.showErrorShort(context, msg);
    }

    public static void showErrorLong(Context context, String msg) {
        InfoToast.showErrorLong(context, msg);
    }

    //-----------------------Dimension---------------------------------------
    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + 0.5f);
    }

    public static float pxToDpCeilInt(Context context, float px) {
        return (int) (pxToDp(context, px) + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    //--------------------------Intent------------------------------

    /**
     * 启动Activity
     *
     * @param activity
     * @param cls
     * @param params
     * @param isFinish
     */
    public static void startActivity(Activity activity, Class<?> cls,
                                     Bundle params, boolean isFinish) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.putExtras(params == null ? new Bundle() : params);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
        if (isFinish) {
            activity.finish();
        }
    }

    public static void sendMessage(Context context, String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    /**
     * 启动Activity
     *
     * @param activity
     * @param cls
     * @param params
     * @param requestCode
     */
    public static void startActivityForResult(Activity activity, Class<?> cls,
                                              Bundle params, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.putExtras(params == null ? new Bundle() : params);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }

    /**
     * 关闭Activity
     *
     * @param activity
     */
    public static void finishActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.in_from_left,
                R.anim.out_to_right);
    }

    /**
     * 进入拍照界面
     *
     * @param activity
     * @param requestCode
     * @param uri
     */
    public static void startCameraActivity(Activity activity, int requestCode,
                                           Uri uri) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }


    //-----------------------Package--------------------------------

    /**
     * 当前应用是否在后台运行 需要权限android.permission.GET_TASKS
     *
     * @param context
     * @return
     */
    public static boolean isAppInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 应用安装
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean installNormal(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        RLog.d("versionCode = " + pi.versionCode + "versionName=" + pi.versionName);
                        return pi.versionCode;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 获取当前版本
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager pManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionName;
    }


    /**
     * 获取Application节点小的meta-data下的value
     *
     * @param context
     * @param name
     * @return
     */
    public static String getMetaDataOfApp(Context context, String name) {
        String value = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (appInfo != null) {
                value = appInfo.metaData.getString(name);
                if (TextUtils.isEmpty(value)) {
                    value = String.valueOf(appInfo.metaData.getInt(name));
                }
            }
        } catch (Exception e) {
        }

        return value;
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param packageName 包名
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();

        if (!packageInfos.isEmpty()) {
            for (PackageInfo info : packageInfos) {
                if (info.packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    //----------------------Mobile---------------------------------

    /**
     * 获取wifi  mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        return mac;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            L.e(ex.toString());
        }
        return null;
    }

    /**
     * 获取手机信息
     *
     * @param context
     * @return
     */
    public static String getMobileInfo(Activity context) {
        HashMap<String, String> mobileInfos = new HashMap<String, String>();
        TelephonyManager tm = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE));
        mobileInfos.put("DeviceId", tm.getDeviceId());// IMEI
        mobileInfos.put("Line1Number", tm.getLine1Number());// MSISDN
        mobileInfos.put("DeviceSoftwareVersion", tm.getDeviceSoftwareVersion());
        mobileInfos.put("SimOperatorName", tm.getSimOperatorName());// ICCID:ICC
        mobileInfos.put("SimSerialNumber", tm.getSimSerialNumber());

        mobileInfos.put("NetworkType", "" + tm.getNetworkType());
        mobileInfos.put("PhoneType", "" + tm.getPhoneType());

        mobileInfos.put("BOARD", Build.BOARD);
        mobileInfos.put("BRAND", Build.BRAND);
        mobileInfos.put("DEVICE", Build.DEVICE);
        mobileInfos.put("DISPLAY", Build.DISPLAY);
        mobileInfos.put("FINGERPRINT", Build.FINGERPRINT);
        mobileInfos.put("ID", Build.ID);
        mobileInfos.put("MODEL", Build.MODEL);
        mobileInfos.put("PRODUCT", Build.PRODUCT);
        mobileInfos.put("INCREMENTAL", Build.VERSION.INCREMENTAL);
        mobileInfos.put("RELEASE", Build.VERSION.RELEASE);
        mobileInfos.put("SDK", Build.VERSION.SDK);
        mobileInfos.put("HOST", Build.HOST);
        mobileInfos.put("TAGS", Build.TAGS);
        mobileInfos.put("TYPE", Build.TYPE);
        mobileInfos.put("USER", Build.USER);
        mobileInfos.put("TIME", "" + Build.TIME);

        mobileInfos.put("Operator", tm.getSimOperator());
        mobileInfos.put("NetworkOperatorName", tm.getNetworkOperatorName());
        mobileInfos.put("SubId", tm.getSubscriberId());// IMSI
        mobileInfos.put("Country", tm.getNetworkCountryIso());

        mobileInfos.put("PhoneType", "" + tm.getPhoneType());

        @SuppressLint("WifiManagerLeak") WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        mobileInfos.put("wifiMacAddress", info.getMacAddress());// MAC?
        mobileInfos.put("wifiState", "" + wifi.getWifiState());
        mobileInfos.put("IP", getLocalIpAddress());
        mobileInfos.put("UserAgent", new WebView(context).getSettings()
                .getUserAgentString());

        mobileInfos.put("versionName", getVersionName(context));

        StringBuffer sb = new StringBuffer("A:");

        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        int heght = context.getWindowManager().getDefaultDisplay().getHeight();
        sb.append(width + "*" + heght + "#");

        for (String key : mobileInfos.keySet()) {
            String v = mobileInfos.get(key);
            if (v != null && v.length() > 0) {
                sb.append(key).append(':').append(v).append('#');
            }
        }
        String mobileInforStr;
        if (sb.length() > 0)
            mobileInforStr = sb.substring(0, sb.length() - 1);
        else {
            mobileInforStr = sb.toString();
        }
        return mobileInforStr;
    }


    /**
     * 检测是否存在存储卡
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /**
     * 获取当前连接的网络类型
     *
     * @param context
     * @return
     */
    public static NetType getNetType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NetType.MOBILE;
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetType.WIFI;
            }
            return NetType.OTHER;
        }
        return NetType.NONE;
    }

    //------------------------------------------------------

    /**
     * 隐藏或显示软键盘
     *
     * @param context
     * @param isShow
     * @param view
     */
    public static void showOrHideKeyBoard(Context context, boolean isShow,
                                          View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //---------------------------------------------------------------

    /**
     * 进入拨打电话界面
     *
     * @param activity
     * @param phoneNumber 电话号码
     */
    public static void callPhone(Activity activity, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            activity.startActivity(intent);
        }
    }

    /**
     * 获取焦点
     *
     * @param view
     */
    public static void focusView(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setText("");
        }
        view.requestFocus();
    }

    //---------------------------数学----------------------------------------

    /**
     * 将二进制转化成十进制
     *
     * @param data 二进制字符串
     * @return
     */
    public static int Binary2Dicemal(String data) {
        int result = 0;
        int pow = 0;
        for (int i = data.length() - 1; i >= 0; i--) {
            result += Math.pow(2, pow) * (data.charAt(i) == '1' ? 1 : 0);
            pow++;
        }
        return result;
    }


    //-----------------------------View相关--------------------------------------------

    public static byte[] bmp2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    //-----------------------加密-------------------------------

    public static String md5Encoder(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] encryption = digest.digest(message.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                int val = ((int) encryption[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取加密密文
     *
     * @param password
     * @return
     */
    public static String getMd5Password(String password) {
        String key = md5Encoder(Constants.CONFIG.API_SECRET_KEY).toLowerCase();
        return md5Encoder(key + password).toLowerCase();
    }

    //-----------------------
    public static void setMargin(View target, int l, int t, int r, int b) {
        if (target.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) target.getLayoutParams();
            p.setMargins(l, t, r, b);
            target.requestLayout();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 置空对话框
     *
     * @param dialog
     */
    public static void setDlgNull(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    /**
     * 置空PopWindow
     *
     * @param popWindow
     */
    public static void setPopWindowNull(PopupWindow popWindow) {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            popWindow = null;
        }
    }


    //-----------------------数字处理----------------------

    /**
     * 保留1为小数
     *
     * @param value
     * @return
     */
    public static String leftOneDecimal(double value) {
        DecimalFormat format = new DecimalFormat(".#");
        return format.format(value);
    }


    //--------------------------图片-------------------

    /**
     * 图片旋转
     *
     * @param bmp          位图
     * @param rotateDegree 旋转度数
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap bmp, float rotateDegree, boolean needRecycle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        if (needRecycle && !bmp.isRecycled()) {
            bmp.recycle();
        }
        return rotaBitmap;
    }

    /**
     * scale image
     *
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight, boolean needRecycle) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight(), needRecycle);
    }

    /**
     * scale image
     *
     * @param org
     * @param scaleWidth  sacle of width
     * @param scaleHeight scale of height
     * @return
     */
    private static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight, boolean needRecycle) {
        if (org == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);

        if (needRecycle && !org.isRecycled()) {
            org.recycle();
        }

        return bitmap;
    }


    public final static int caculateInSampleSize(BitmapFactory.Options options, int rqsW, int rqsH) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (rqsW == 0 || rqsH == 0) return 1;
        if (height > rqsH || width > rqsW) {
            final int heightRatio = Math.round((float) height / (float) rqsH);
            final int widthRatio = Math.round((float) width / (float) rqsW);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public final static Bitmap compressBitmap(String path, int rqsW, int rqsH) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(options, rqsW, rqsH);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 将view转化成bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromView(View view) {
        if (view == null) return null;
//        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache(true);
    }

    /**
     * 缓存图片
     *
     * @param context
     * @param bitmap
     */
    public static File cacheBmp(Context context, Bitmap bitmap, boolean isRecycle) {
        if (bitmap == null) return null;

        File temp = getOutputMediaFile(context, Constants.CONFIG.IMG_COMPRESS_CACHE_DIR);
        if (temp.exists()) {
            temp.delete();
        }
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(temp));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                    os = null;
                    if (isRecycle && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return temp;
    }

    /**
     * 动态设置listview的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = listView.getPaddingTop()
                + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 获取某个文件夹的大小 ，单位是kb
     *
     * @param relativePath
     * @return 返回-1表示这是个文件而不是文件夹
     * @author com.tiantian
     */
    public static float getFolderSize(String relativePath) {
        int fileLength = 0;
        File dir = new File(relativePath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileLength += file.length();
            }
        } else {
            return -1;
        }
        return fileLength / 1024.0f;
    }

    /**
     * 获取数据的开始索引
     *
     * @return
     */
    public static int getStartIndex(int pageNum, int pageSize) {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取页码
     *
     * @param pageSize
     * @param totalNum
     * @return
     */
    public static int getTotalPage(int pageSize, int totalNum) {
        return totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
    }

    /**
     * 关闭对话框
     *
     * @param dialog
     */
    public static void hideDlg(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    /**
     * 获取字符串的长度，中文为2字符，英文为1字符
     *
     * @param s
     * @return
     */
    public static int getLength(String s) {
        if (TextUtils.isEmpty(s))
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 时间工具类
     */
    public static class DateKit {
        private static SimpleDateFormat md = new SimpleDateFormat("MM-dd");
        private static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        private static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private static SimpleDateFormat ymdhmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        private static SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        private static SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        private static SimpleDateFormat mdhm = new SimpleDateFormat("MM月dd日 HH:mm");
        private static SimpleDateFormat mdhmLink = new SimpleDateFormat("MM-dd HH:mm");
        private static Calendar calendar = Calendar.getInstance();

        /**
         * 年月日[2015-07-28]
         *
         * @param timeInMills
         * @return
         */
        public static String getYmd(long timeInMills) {
            return ymd.format(new Date(timeInMills));
        }

        public static String getYmdhms(long timeInMills) {
            return ymdhms.format(new Date(timeInMills));
        }

        public static String getYmdhmsS(long timeInMills) {
            return ymdhmss.format(new Date(timeInMills));
        }

        public static String getYmdhm(long timeInMills) {
            return ymdhm.format(new Date(timeInMills));
        }

        public static String getHm(long timeInMills) {
            return hm.format(new Date(timeInMills));
        }

        public static String getMd(long timeInMills) {
            return md.format(new Date(timeInMills));
        }

        public static String getMdhm(long timeInMills) {
            return mdhm.format(new Date(timeInMills));
        }

        public static String getMdhmLink(long timeInMills) {
            return mdhmLink.format(new Date(timeInMills));
        }

    }

    /**
     * 获取缓存地址
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取媒体文件路径
     *
     * @param context
     * @return
     */
    public static Uri getOutputMediaFileUri(Context context,String packageName) {
        Uri imageUri;
        //通过FileProvider创建一个content类型的Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(context, packageName, getOutputMediaFile(context, Constants.CONFIG.IMG_CACHE_DIR));
        } else {
            imageUri = Uri.fromFile(getOutputMediaFile(context, Constants.CONFIG.IMG_CACHE_DIR));
        }
        RLog.e("imageUri=" + imageUri.getPath());
        return imageUri;
    }

    public static File getOutputMediaFile(Context context, String uniqueName) {
        File mediaStorageDir = getDiskCacheDir(context,
                uniqueName);
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    /**
     * 产生随机图片名称
     *
     * @return
     */
    public static String generateFileName(String suffix) {
        String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        return new StringBuilder("android_")
                .append(Calendar.getInstance().getTimeInMillis())
                .append(getRandom(NUMBERS_AND_LETTERS.toCharArray(), 5))
                .append(suffix)
                .toString();
    }

    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }

        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }

    /**
     * 获取显示昵称
     *
     * @param userName
     * @param petName
     * @return
     */
    public static String getPetName(String userName, String petName) {
        if (TextUtils.isEmpty(petName)) {
            if (!TextUtils.isEmpty(userName) && userName.length() > 4) {
//                return new StringBuilder(userName.substring(0, userName.length() - 4)).append("****").toString();
                return new StringBuilder(userName.substring(0, userName.length() - 8)).append("********").toString();
            }
            return userName;
        } else {
            return petName;
        }
    }

    public static String md5(String passW) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(passW.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
