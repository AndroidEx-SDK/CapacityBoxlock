package com.androidex.capbox.utils;

import com.androidex.boxlib.utils.BleConstants;

/**
 * @author liyp
 * @version 1.0.0
 * @description 常量
 * @createTime 2015/10/19
 * @editTime
 * @editor
 */
public class Constants extends BleConstants{

    /**
     * 应用配置
     */
    public static class CONFIG {
        public static final boolean DEBUG = BuildConfig.DEBUG; //调试模式
        public static final String API_CACHE_DIR = "api";    //api缓存目录
        public static final String IMG_CACHE_DIR = "img";     //图片缓存目录
        public static final String IMG_COMPRESS_CACHE_DIR = "imgCompress";     //图片压缩缓存目录
        public static final String LOG_TAG_ROOT = "AndroidEx";     //日志过滤键
        // http://125.76.235.28/securitybox/app/getDeviceMac//测试服务器地址
        public static final String APP_BASIC_SERVER = "http://125.76.235.28/securitybox/app/";   //基本服务器地址
        public static final String APP_BASIC_SERVER_BAIDU = "http://api.map.baidu.com/ag/coord/convert";   //基本服务器地址

        public static final String API_SECRET_KEY = "qb1y";

        public static final int DEST_PIC_WIDTH = 400;     //上传图片的宽
        public static final int DEST_PIC_HEIGHT = 400;    //上传图片的高
        public static final int SHARE_LUCKY_MAX_IMG_COUNT = 5;    //晒单分享上传的最大图片数

    }

    /**
     * 常量
     */
    public static class PARAM {
        public static final String CACHE_KEY_CUR_LOGIN_USER = "curLoginedUser"; //当前登陆用户

    }

    /**
     * API
     */
    public static class API {
        public static final int CHANNEL_ANDROID = 1;      //Android渠道
        public static final int API_OK = 0;
        public static final int API_NOPERMMISION = 1;
        public static final int API_FAIL = 2;
        public static final int API_LOGIN_REPEAT = 1019; //重复登录
        public static final int API_CODE_UPLOAD_OK = 1;   //上传图片成功

    }

    /**
     * SharedPref键
     */
    public static class SP {
        public static final String SP_NAME = "conf";  //sp配置文件名称
        public static final String SP_OBJ_PREFIX = "obj_";    //sp对象名称前缀
    }

    /**
     * 事件标识
     */
    public static class EVENT {
        public static final int TAG_BASE_EVENT = 1000;
        public static final int TAG_EVENT_USER_LOGIN = TAG_BASE_EVENT + 1;        //用户登录状态发生改变
        public static final int TAG_EVENT_USER_INFO_CHANGE = TAG_BASE_EVENT + 2;        //用户信息改变
        public static final int TAG_EVENT_BOX_BIND_CHANGE = TAG_BASE_EVENT + 3;        //绑定箱体数量发生变化

    }

    /**
     * 正则表达式
     */
    public static class REGEX {
        public static final String REGEX_PASSWORD = "^\\w{6,12}$";  //密码正则：由长度为6~12位的字母、数字、下划线构成
        public static final String REGEX_PHONE = "^1\\d{10}$";  //手机号验证
        public static final String TEGEX_DIGIT = "^[0-9]*$";  //数字
        public static final String CARDID = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";  //数字
        public static final String REGEX_MOBILE = "^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";//手机号验证
        public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]{1,9}$";//验证汉字(1-9个汉字)
    }

    /**
     * 请求码requestCode
     */
    public static class CODE {
        public static final int REQUESTCODE_ADD_DEVICE = 0x0001;//添加设备
        public static final int REQUESTCODE_SET_ALARM = 0x0002;//报警设置
        public static final int REQUESTCODE_SET_LOCK = 0x0003;//开锁设置，
        public static final int REQUESTCODE_SET_BOX = 0x0004;//箱子设置，昵称设置
        public static final int REQUESTCODE_OPEN_MONITOR = 0x0005;//从配置页跳转到监控页
        public static final int REQUESTCODE_FINGER_POSSESSOR = 0x0006;//录入所有人指纹
        public static final int REQUESTCODE_FINGER_BECOME = 0x0007;//录入无线静默指纹
        public static final int REQUESTCODE_FINGER_CARRY = 0x0008;//录入携行人指纹
        public static final int REQUESTCODE_FINGER_SETTING = 0x0009;//指纹设置

    }
}
