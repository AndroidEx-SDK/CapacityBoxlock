package com.androidex.capbox.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.androidex.capbox.MainActivity;
import com.androidex.capbox.R;
import com.androidex.capbox.data.net.base.L;

import java.io.IOException;
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
     * @param pattern  自定义震动模式。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * @param isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public static void startVibrate(final Activity activity, boolean isRepeat) {
        long[] pattern = {100, 400, 100, 400};   // 自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 2 : -1);//2：一直重复、	1：震动一次后一直小震动、 -1 震动一次
    }

    /**
     * 停止震动
     *
     * @param activity
     */
    public static void stopVibrate(Context activity) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        if (vib != null) {
            vib.cancel();
        }
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
     * @param uri     指定路径
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
        //stopPlayRaw();
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
