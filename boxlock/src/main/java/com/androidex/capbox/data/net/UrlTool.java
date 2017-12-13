package com.androidex.capbox.data.net;


import com.androidex.capbox.utils.Constants;

/**
 * @author liyp
 * @version 1.0.0
 * @description Url工具
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public class UrlTool {
    private static final String urlSuffix = "%s%s";

    public static final String LOGIC_USER = "";
    public static final String APP_UPDATA_URL = "version/app/";
    public static final String BOX_UPDATA_URL = "version/box/";
    public static final String WATCH_UPDATA_URL = "version/watch/";
    public static final String USER_ACTION_REGISTER = "regist";  //注册
    public static final String USER_ACTION_LOGIN = "login";   //登录
    public static final String USER_ACTION_LOGOUT = "logout"; //用户退出
    public static final String USER_ACTION_LOGOFF = "logoff";//用户注销
    public static final String USER_ACTION_GET_CAPTCHA = "authcode";     //获取验证码
    public static final String USER_ACTION_BOXLOCATION = "boxLocation";//   获取箱体经纬度
    public static final String USER_ACTION_FORGET_PASSWORD = "forgetPassword";     //找回密码
    public static final String USER_ACTION_BOXBAND = "boxBind";  //确认绑定密管箱
    public static final String USER_ACTION_RELIEVEBOXBIND = "relieveBoxBind";   //解除绑定箱体
    public static final String USER_ACTION_RELIEVEWATCHBIND = "relieveWatchBind";   //解除绑定腕表
    public static final String USER_ACTION_BOXLIST = "boxList";   //获取箱体设备列表
    public static final String USER_ACTION_WATCHLIST = "watchList";   //获取腕表设备列表
    public static final String USER_ACTION_ALL_WATCHLIST = "userWatchList";   //获取腕表设备列表
    public static final String USER_ACTION_BOXDETAIL = "boxDetail";   //箱体详情
    public static final String USER_ACTION_WATCHDETAIL = "watchDetail";   //腕表详情
    public static final String USER_ACTION_BOXCONFIG = "boxConfig";     //密管箱配置
    public static final String USER_ACTION_STARTESCORT = "startEscort";    //开启押运状态
    public static final String USER_ACTION_ENDTASK = "endTask";    //结束押运状态
    public static final String USER_ACTION_WATCHCONFIG = "watchConfig";    //配置携行腕表
    public static final String USER_ACTION_DEFAULTDEVICE = "defaultDevice";     //设置默认设备
    public static final String USER_ACTION_GETDEVICEMAC = "getDeviceMac";    //获取箱体mac
    public static final String USER_ACTION_GETALARMLIST = "getAlarmList";    //获取箱体报警信息
    public static final String USER_ACTION_CHECKVERSION = "checkVersion";    //检测新版本

    public static String getUrl(String logic, String action) {
        StringBuilder builder = new StringBuilder(Constants.CONFIG.APP_BASIC_SERVER);
        builder.append(String.format(urlSuffix, logic, action));
        return builder.toString();
    }

    public static String getDownloadUrl(String logic, String action) {
        StringBuilder builder = new StringBuilder(Constants.CONFIG.APP_BASIC_SERVER_DOWNLOAD);
        builder.append(String.format(urlSuffix, logic, action));
        return builder.toString();
    }

    public static String getUrl1(String logic, String action) {
        StringBuilder builder = new StringBuilder(Constants.CONFIG.APP_BASIC_SERVER_BAIDU);
        builder.append(String.format(urlSuffix, logic, action));
        return builder.toString();
    }
}
