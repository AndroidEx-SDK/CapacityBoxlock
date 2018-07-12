package com.androidex.capbox.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.androidex.capbox.R;
import com.androidex.capbox.data.net.base.L;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SystemUtil {
    private static MediaPlayer mSystemMediaPlayer = null;
    private static MediaPlayer mMediaPlayer = null;
    private static MediaPlayer mediaPlayer;

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return 屏幕宽度
     */
    public static int getScreenW(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // Display display = manager.getDefaultDisplay();
        // int width =display.getWidth();
        // int height=display.getHeight();
        // Log.d("width", String.valueOf(width));
        // Log.d("height", String.valueOf(height)); //第一种

        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int width2 = dm.widthPixels;// 宽
        // int height2=dm.heightPixels;//高
        // Log.d("width2", String.valueOf(width2));
        // Log.d("height2", String.valueOf(height2)); //第二种
        return width2;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return 屏幕高度
     */
    public static int getScreenH(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // Display display = manager.getDefaultDisplay();
        // int width =display.getWidth();
        // int height=display.getHeight();
        // Log.d("width", String.valueOf(width));
        // Log.d("height", String.valueOf(height)); //第一种

        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        // int width2=dm.widthPixels;//宽
        int height2 = dm.heightPixels;// 高
        // Log.d("width2", String.valueOf(width2));
        // Log.d("height2", String.valueOf(height2)); //第二种
        return height2;
    }

    /**
     * Android 5.0 改变状态栏颜色
     *
     * @param activity
     * @param statusColor
     */
    public static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        // 设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        // 让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setImmerseLayout(Activity context, View view) {// view为标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = getStatusBarHeight(context.getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Android 5.0 透明状态栏
     *
     * @param activity
     * @param hideStatusBarBackground
     */
    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            // 如果为全透明模式，取消设置Window半透明的 Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置状态栏为透明
            window.setStatusBarColor(Color.TRANSPARENT);
            // 设置window的状态栏不可见
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            // 如果为半透明模式，添加设置Window半透明的Flag
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } //view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    /**
     * 检测gps是否可用
     */
    public static final boolean isGpsEnable(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * Android 手机震动功能实现1
     *
     * @param activity     调用该方法的Activity实例
     * @param milliseconds 震动的时长，单位是毫秒
     */
    public static void startVibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * Android 手机震动功能实现2
     *
     * @param activity 调用该方法的Activity实例
     * @param //       自定义震动模式。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * @param isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public static void startVibrate(final Activity activity, boolean isRepeat) {
        long[] pattern = {2000, 1000, 2000, 1000};   // 自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 2 : -1);//2：一直重复、	1：震动一次后一直小震动、 -1 震动一次
    }

    /**
     * 停止震动
     *
     * @param activity
     */
    public static void stopVibrate(Activity activity) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib != null) {
            vib.cancel();
        }
    }

    /**
     * 判断一个服务是否存在
     *
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppRunning(Context context) {
        String packageName = context.getPackageName();
        String topActivityClassName = getTopActivityName(context);
        RLog.e("class name =" + topActivityClassName);
        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager =
                (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    /**
     * Android 一个简单手机响铃功能实现方法，播放默认铃声（短信提示音，响一声）
     * 该方法的参数，传递Activity的引用即可。当然，在静音模式下，是无法播放的。
     *
     * @param context
     * @return 返回Notification id
     */
    public static int startPlaySound(final Context context) {
        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification nt = new Notification();
        nt.defaults = Notification.DEFAULT_SOUND;
        int soundId = new Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE);
        mgr.notify(soundId, nt);
        return soundId;
    }


    /**
     * 手机响铃功能实现方法2
     *
     * @param context
     * @param uriStr  指定路径
     */
    public static void startPlayMediaPlayer(Context context, String uriStr) {
        // 使用来电铃声的铃声路径
        //		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // 如果为空，才构造，不为空，说明之前有构造过
        if (Uri.parse(uriStr) == null || Uri.parse(uriStr).toString().trim().equals("")) {
            startSystemPlayer(context);
            return;
        }
        stopPlayMediaPlayer();
        L.e("开始自定义响铃");
        try {
            L.e("铃声地址：" + Uri.parse(uriStr));
            mMediaPlayer = new MediaPlayer();
            //				mMediaPlayer.reset();
            mMediaPlayer.setDataSource(context, Uri.parse(uriStr));
            mMediaPlayer.setLooping(true); //循环播放
            //				mMediaPlayer.prepare();
            mMediaPlayer.prepareAsync();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 播放raw文件下的音频
     *
     * @param context
     */
    public static void startPlayerRaw(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        stopPlayRaw();
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        //mediaPlayer.prepareAsync();
    }

    /**
     * 停止播放RAW音频
     *
     * @param
     */
    public static void stopPlayRaw() {
        if (mediaPlayer != null) {
            L.e("停止raw报警");
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 播放电话默认铃声
     *
     * @param context
     */
    public static void startSystemPlayer(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.e("ee", "正在响铃");
        L.e("开始响铃");
        // 使用来电铃声的铃声路径
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        // 如果为空，才构造，不为空，说明之前有构造过
        try {
            stopPlayMediaPlayer();
            mSystemMediaPlayer = new MediaPlayer();
            mSystemMediaPlayer.setDataSource(context, uri);
            mSystemMediaPlayer.setLooping(true); //循环播放
            mSystemMediaPlayer.prepare();
            mSystemMediaPlayer.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 停止手机响铃
     *
     * @param
     */
    public static void stopPlayMediaPlayer() {
        if (mSystemMediaPlayer != null) {
            L.e("停止系统铃声");
            mSystemMediaPlayer.stop();
            mSystemMediaPlayer.release();
            mSystemMediaPlayer = null;
        }
        if (mMediaPlayer != null) {
            L.e("停止自定义铃声");
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        L.e("停止铃声");
    }
}
