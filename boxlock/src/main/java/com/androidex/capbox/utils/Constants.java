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
public class Constants extends BleConstants {
    public static final String EXTRA_PACKAGE_NAME = "com.androidex.capbox";
    public static final String EXTRA_ITEM_ADDRESS = "address";
    public static final String EXTRA_BOX_UUID = "uuid";
    public static final String EXTRA_BOX_NAME = "name";
    public static final String EXTRA_USER_HEAD = "head";//存储用户头像路径
    public static final String EXTRA_PAGER_SIGN = "pager_sign";//启动页面标志，区别是哪个页面跳转到该页面的

    /*Send Command Type*/
    public static final byte VISE_COMMAND_TYPE_NONE = (byte) 0x00;
    public static final byte VISE_COMMAND_TYPE_TEXT = (byte) 0x01;
    public static final byte VISE_COMMAND_TYPE_FILE = (byte) 0x02;
    public static final byte VISE_COMMAND_TYPE_IMAGE = (byte) 0x03;
    public static final byte VISE_COMMAND_TYPE_AUDIO = (byte) 0x04;
    public static final byte VISE_COMMAND_TYPE_VIDEO = (byte) 0x05;

    public static class BASE {
        public static final String ACTION_TEMP_OUT = "ACTION_TEMP_OUT";
        public static final String ACTION_RSSI_OUT = "ACTION_RSSI_OUT";
        public static final String ACTION_RSSI_IN = "ACTION_RSSI_IN";
    }

    /**
     * 应用配置
     */
    public static class CONFIG {
        public static final boolean DEBUG = BuildConfig.DEBUG; //调试模式
        public static final boolean OPEN_DFU_UPDATE = false; //是否开启空中升级
        public static final String API_CACHE_DIR = "api";    //api缓存目录
        public static final String IMG_CACHE_DIR = "img";     //图片缓存目录
        public static final String IMG_COMPRESS_CACHE_DIR = "imgCompress";     //图片压缩缓存目录
        public static final String LOG_TAG_ROOT = "AndroidEx";     //日志过滤键
        // http://125.76.235.28/securitybox/app/getDeviceMac//测试服务器地址
        public static final String APP_BASIC_SERVER = "http://125.76.235.28/securitybox/app/";   //基本服务器地址
        public static final String APP_BASIC_SERVER_BAIDU = "http://api.map.baidu.com/ag/coord/convert";   //基本服务器地址
        public static final String APP_BASIC_SERVER_DOWNLOAD = "http://125.76.235.28/securitybox/";   //基本服务器地址

        public static final String API_SECRET_KEY = "qb1y";

        public static final int DEST_PIC_WIDTH = 400;     //上传图片的宽
        public static final int DEST_PIC_HEIGHT = 400;    //上传图片的高
        public static final int SHARE_LUCKY_MAX_IMG_COUNT = 5;    //晒单分享上传的最大图片数

    }

    /**
     * 常量
     */
    public static class PARAM {
        public static final String CACHE_KEY_CUR_LOGIN_USER = "CACHE_KEY_CUR_LOGIN_USER"; //当前登陆用户
        public static final String SYSTEM_STOP_SHAKING = "SYSTEM_STOP_SHAKING"; //停止震动
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
        public static final String SP_BOX_STARTS = "sp_box_starts"; //sp箱体的状态
        public static final String SP_DISTANCE_TYPE = "sp_distance_type"; //sp存储距离报警方式
        public static final String SP_LOST_TYPE = "sp_lost_type";    //sp存储脱距报警方式
        public static final String SP_TEMP_TYPE = "sp_temp_type";    //sp存储温湿度报警方式
    }

    /**
     * 事件标识
     */
    public static class EVENT {
        public static final int TAG_BASE_EVENT = 1000;
        public static final int TAG_EVENT_USER_LOGIN = TAG_BASE_EVENT + 1;        //用户登录状态发生改变
        public static final int TAG_EVENT_USER_INFO_CHANGE = TAG_BASE_EVENT + 2;       //用户信息改变
        public static final int TAG_EVENT_BOX_BIND_CHANGE = TAG_BASE_EVENT + 3;        //绑定箱体数量发生变化
        public static final int TAG_EVENT_BOX_RELIEVE_BIND = TAG_BASE_EVENT + 4;       //解除绑定
        public static final int TAG_EVENT_BOX_BLE_CONNECTED = TAG_BASE_EVENT + 5;      //蓝牙连接
        public static final int TAG_EVENT_BOX_BLE_DISCONNECTED = TAG_BASE_EVENT + 6;   //蓝牙断开
        public static final int TAG_EVENT_BOX_LOCK_OPEN = TAG_BASE_EVENT + 7;          //锁打开
        public static final int TAG_EVENT_BOX_LOCK_CLOSE = TAG_BASE_EVENT + 8;         //锁关闭
        public static final int TAG_EVENT_FRAGMENT_NEXT_PAGE = TAG_BASE_EVENT + 9;     //切换下一页
        public static final int TAG_EVENT_FRAGMENT_PREVIOUS_PAGE = TAG_BASE_EVENT + 10;//切换上一页
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
        public static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;//请求相机权限
        public static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;//请求SD卡权限
    }

    public static class baiduMap{

        public static final String API_KEY = "fc51UqGugQUOwzhBuan3UHSZ1MUERHXz";

        public static final String SK_DEBUG = "27:F3:FC:9F:0E:08:74:CE:4E:3E:F6:9E:A6:D9:33:26:4F:4B:80:F9;com.androidex.capbox";

        public static final String SK_RELAESE="C5:3A:9F:63:54:D7:03:DC:E0:9E:53:6B:B1:52:B9:EF:66:81:B5:00;com.androidex.capbox";

        /**
         * 轨迹服务ID
         */
        public static final long serviceId = 200366;

        public static final int REQUEST_CODE = 1;

        public static final int RESULT_CODE = 1;

        public static final int DEFAULT_RADIUS_THRESHOLD = 0;

        public static final int PAGE_SIZE = 5000;

        /**
         * 轨迹分析查询间隔时间（1分钟）
         */
        public static final int ANALYSIS_QUERY_INTERVAL = 60;

        /**
         * 停留点默认停留时间（1分钟）
         */
        public static final int STAY_TIME = 60;

        /**
         * 启动停留时间
         */
        public static final int SPLASH_TIME = 3000;

        /**
         * 解释 http://wiki.lbsyun.baidu.com/index.php?title=android-yingyan/guide/uploadtrack
         */
        /**
         * 默认采集周期 定位周期：多久定位一次，在定位周期大于15s时，SDK会将定位周期设置为5的倍数（如设置采集周期为18s，SDK会调整为15s；设置为33s，SDK会调整为30s）
         */
        public static final int DEFAULT_GATHER_INTERVAL = 2;

        /**
         * 默认打包周期 回传周期：鹰眼为节省电量和流量，并不是定位一次就回传一次数据，而是隔段时间将一批定位数据打包压缩回传。（回传周期最大不要超过定位周期的10倍，例如，定位周期为5s，则回传周期最好不要大于50s）
         */
        public static final int DEFAULT_PACK_INTERVAL = 10;

        /**
         * 实时定位间隔(单位:秒)
         */
        public static final int LOC_INTERVAL = 2;

        /**
         * 最后一次定位信息
         */
        public static final String LAST_LOCATION = "last_location";
    }
}
