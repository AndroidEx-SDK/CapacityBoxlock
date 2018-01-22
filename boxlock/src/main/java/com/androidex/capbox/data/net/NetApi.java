package com.androidex.capbox.data.net;

import android.bluetooth.BluetoothAdapter;

import com.androidex.capbox.data.net.base.OkRequest;
import com.androidex.capbox.data.net.base.RequestParams;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.module.AlarmListModel;
import com.androidex.capbox.module.AuthCodeModel;
import com.androidex.capbox.module.BaiduModel;
import com.androidex.capbox.module.BaseModel;
import com.androidex.capbox.module.BoxDetailModel;
import com.androidex.capbox.module.BoxDeviceModel;
import com.androidex.capbox.module.CheckVersionModel;
import com.androidex.capbox.module.DeviceMacModel;
import com.androidex.capbox.module.DeviceWatchModel;
import com.androidex.capbox.module.LocationModel;
import com.androidex.capbox.module.LoginModel;
import com.androidex.capbox.module.ResultModel;
import com.androidex.capbox.module.WatchDetailModel;

import okhttp3.Headers;

import static com.androidex.capbox.data.net.UrlTool.USER_ACTION_REGISTER;
import static com.androidex.capbox.data.net.UrlTool.getUrl;

/**
 * @author liyp
 * @editTime 2017/9/22
 */

public class NetApi {

    /**
     * 用户注册|联合登录-注册用户并绑定
     *
     * @param username 手机号
     * @param password 密码
     * @param name     联合登录id
     * @param cardId   授权类型：wx,wb,qq
     * @param callBack
     */
    public static void userRegister(String username, String name, String cardId, String password, String authcode, ResultCallBack<LoginModel> callBack) {
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("name", name)
                .put("cardId", cardId)
                .put("password", password)
                .put("authcode", authcode);

        String url = getUrl(UrlTool.LOGIC_USER, USER_ACTION_REGISTER);
        new OkRequest.Builder().url(url).params(params).post(callBack);
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param authcode
     * @param callBack
     */
    public static void userLogin(String username, String password, String authcode, ResultCallBack<LoginModel> callBack) {
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("password", password)
                .put("authcode", authcode);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_LOGIN);
        new OkRequest.Builder().url(url).params(params).post(callBack);
    }

    /**
     * 获取审核结果
     *
     * @param username     手机号
     * @param -默认注册，1-找回密码
     * @param callBack
     */
    public static void getCheckResult(String token, String username, ResultCallBack<ResultModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_GET_CAPTCHA);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 退出登录
     *
     * @param username 手机号
     * @param callBack
     */
    public static void userLogout(String token, String username, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_LOGOUT);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 用户注销
     *
     * @param username 手机号
     * @param callBack
     */
    public static void userLogoff(String token, String username, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_LOGOFF);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 忘记密码
     *
     * @param username
     * @param newPassword
     * @param name
     * @param cardId
     */
    public static void forgetPassword(String token, String username, String newPassword, String name, String cardId, String authcode, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("newPassword", newPassword)
                .put("name", name)
                .put("cardId", cardId)
                .put("authcode", authcode);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_FORGET_PASSWORD);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 确认绑定密管箱
     * <p>
     * token
     * username
     * uuid      //设备的uuid
     * deviceMac
     * deviceType   //设备类型：密管箱A 腕表B
     */
    public static void boxBind(String token, String username, String uuid,
                               ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid)
                .put("mobileMac", getLocalMac());

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_BOXBAND);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 解除绑定箱体
     * token
     * username
     * uuid//设备类型为B时该值为空字符串
     * mac
     */
    public static void relieveBoxBind(String token, String username, String uuid, String mac, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid)
                .put("mac", mac);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_RELIEVEBOXBIND);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 解除绑定腕表
     * token
     * username
     * uuid//设备类型为B时该值为空字符串
     * mac
     */
    public static void relieveWatchBind(String token, String username, String uuid, String mac, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid)
                .put("mac", mac);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_RELIEVEWATCHBIND);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 获取箱体设备列表
     * token
     * username
     */
    public static void boxlist(String token, String username, ResultCallBack<BoxDeviceModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_BOXLIST);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 获取腕表设备列表
     * token
     * username
     */
    public static void watchlist(String token, String username, String uuid, ResultCallBack<DeviceWatchModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_WATCHLIST);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 获取腕表设备列表
     * token
     * username
     */
    public static void watchlist(String token, String username, ResultCallBack<DeviceWatchModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_ALL_WATCHLIST);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 箱体详情
     * token
     * username
     * uuid   //B型号时该值为空字符串
     * mac   //当设备型号为B时使用MAC
     */
    public static void boxDetail(String token, String username, String uuid, ResultCallBack<BoxDetailModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_BOXDETAIL);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 获取箱体经纬度
     * token
     * username
     * uuid //箱体的uuid
     */
    public static void getboxLocation(String token, String username, String uuid, ResultCallBack<LocationModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_BOXLOCATION);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 腕表详情
     * token
     * username
     * uuid   //B型号时该值为空字符串
     * mac   //当设备型号为B时使用MAC
     */
    public static void watchDetail(String token, String username, String uuid, String mac, ResultCallBack<WatchDetailModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid)
                .put("mac", mac);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_WATCHDETAIL);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 密管箱配置
     * token
     * username   //该用户默认为该箱子的所有人
     * uuid
     * possessorFinger     //箱体所有人的指纹信息
     * unlocking    //多次有效A 一次有效B
     * unlockingMode  //开锁方式设定: 指纹开锁A，腕表开锁B 同时                                                                                       开锁 C
     * carryPersonNum  //携行人员人数跟腕表数量对应
     * police       //报警开启A和关闭B
     * policeDiatance   //报警距离：0脱距、1较近、2近、3较远、4远
     * heartbeatRate   //心跳更新频率60秒
     * locationRate    //定位更新频率为60秒
     * dismountPolice   //破拆报警的开启A和关闭B
     * become        //静默开启A 关闭B
     * becomeFinger   //静默模式下指纹开启A和关闭B
     * carryPerson:[{mac:’FF:FF:FF:FF:FF:FF’,  //腕表MAC deviceType:’B’}]
     * tempPolice:’A’,     //温度报警开启A 关闭B
     * humidityPolice:’A’,    //湿度报警开启A 关闭B
     * distancePolice:’A’,     //脱距报警开启A 关闭B
     */

    /**
     * @param token
     * @param username
     * @param uuid
     * @param boxName
     * @param possessorFinger1
     * @param possessorFinger2
     * @param possessorFinger3
     * @param becomeFinger1
     * @param becomeFinger2
     * @param becomeFinger3
     * @param unlocking
     * @param unlockingMode
     * @param carryPersonNum
     * @param police
     * @param policeDiatance
     * @param heartbeatRate
     * @param locationRate
     * @param highestTemp
     * @param lowestTemp
     * @param dismountPolice   破拆报警开关
     * @param become           静默功能开关
     * @param tempPolice       温度报警开关
     * @param humidityPolice   湿度报警开关
     * @param distancePolice   脱距报警开关
     * @param callBack
     */
    public static void boxConfig(String token,
                                 String username,
                                 String uuid,
                                 String boxName,
                                 String possessorFinger1,//箱体所有人指纹信息
                                 String possessorFinger2,
                                 String possessorFinger3,
                                 String becomeFinger1,//控制箱体静默的指纹信息
                                 String becomeFinger2,
                                 String becomeFinger3,
                                 String unlocking,
                                 String unlockingMode,
                                 int carryPersonNum,
                                 String police,
                                 int policeDiatance,
                                 int heartbeatRate,
                                 int locationRate,
                                 float highestTemp,
                                 float lowestTemp,
                                 String dismountPolice,
                                 String become,
                                 String tempPolice,
                                 String humidityPolice,
                                 String distancePolice,
                                 ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid)
                .put("boxName", boxName)
                .put("deviceType", "A")//该值在协议上已经删除，但是服务器返回需要该值
                .put("possessorFinger1", possessorFinger1)
                .put("possessorFinger2", possessorFinger2)
                .put("possessorFinger3", possessorFinger3)
                .put("becomeFinger1", becomeFinger1)
                .put("becomeFinger2", becomeFinger2)
                .put("becomeFinger3", becomeFinger3)
                .put("unlocking", unlocking)
                .put("unlockingMode", unlockingMode)
                .put("carryPersonNum", carryPersonNum)
                .put("police", police)
                .put("policeDiatance", policeDiatance)
                .put("heartbeatRate", heartbeatRate)
                .put("locationRate", locationRate)
                .put("highestTemp", highestTemp)
                .put("lowestTemp", lowestTemp)
                .put("dismountPolice", dismountPolice)
                .put("become", become)
                .put("become", tempPolice)
                .put("become", humidityPolice)
                .put("become", distancePolice);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_BOXCONFIG);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 开启押运状态
     * token
     * username
     * uuid   //B型号时该值为空字符串
     */
    public static void startEscort(String token, String username, String uuid, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_STARTESCORT);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 押运任务完成后，结束任务
     * token
     * username
     * uuid   //B型号时该值为空字符串
     */
    public static void endTask(String token, String username, String uuid, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_ENDTASK);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 配置携行腕表
     * token:’ANDB29988330’,
     * username:’13828840464’,
     * uuid:’’                        //密管箱的uuid
     * mac:’FF:FF:FF:FF:FF:FF’,        //腕表的mac
     * carryName:’李永平’，              //携行人员姓名
     * carryCardId:’410219299202106519’,   //携人员身份证号
     * carryFinger1:’fffffff’,                //携行人员指纹信息或id1
     * carryFinger2:’fffffff’,                //携行人员指纹信息或id2
     * carryFinger3:’fffffff’,                //携行人员指纹信息或id3
     * carryCall:’13545457676’,             //携行人手机号
     * become:’A’
     */
    public static void watchConfig(String token, String username, String uuid,
                                   String mac, String carryName, String carryCardId,
                                   String carryFinger1, String carryFinger2, String carryFinger3,
                                   String carryCall, String become, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid)
                .put("mac", mac)
                .put("carryName", carryName)
                .put("carryCardId", carryCardId)
                .put("carryFinger1", carryFinger1)
                .put("carryFinger2", carryFinger2)
                .put("carryFinger3", carryFinger3)
                .put("carryCall", carryCall)
                .put("become", become);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_WATCHCONFIG);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }


    /**
     * 设置默认设备
     * token
     * username
     * uuid
     */
    public static void defaultDevice(String token, String username, String uuid, ResultCallBack<BaseModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_DEFAULTDEVICE);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 获取箱体mac
     * token
     * username
     * uuid
     */
    public static void getDeviceMac(String token, String username, String uuid, ResultCallBack<DeviceMacModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);
        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_GETDEVICEMAC);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 获取随机验证码
     *
     * @param
     * @param callBack
     */
    public static void getAuthCode(String islock, String uuid, ResultCallBack<AuthCodeModel> callBack) {
        RequestParams params = RequestParams.newInstance()
                .put("mobileMac", getLocalMac())
                .put("forOpenBox", islock)
                .put("uuid", uuid);
        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_GET_CAPTCHA);
        new OkRequest.Builder().url(url).params(params).post(callBack);
    }

    public static void getLocation(String lat, String lon, ResultCallBack<BaiduModel> callBack) {
        //http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=113.540124&y=23.517846
        RequestParams params = RequestParams.newInstance()
                .put("from", 0)
                .put("to", 4)
                .put("x", lon)
                .put("y", lat);
        String url = UrlTool.getUrl1("", "");
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }


    /**
     * 获取箱体报警信息
     * token
     * username
     * uuid
     */
    public static void getAlarmList(String token, String username, String uuid, ResultCallBack<AlarmListModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username)
                .put("uuid", uuid);
        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_GETALARMLIST);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 检测版本号
     * 下载地址：
     * APP：version/app/20171129.apk
     * 箱体：version/box/20171129.hex
     * 腕表：version/watch/20171129.hex
     */
    public static void checkVersion(String token, String username, ResultCallBack<CheckVersionModel> callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();
        RequestParams params = RequestParams.newInstance()
                .put("username", username);

        String url = getUrl(UrlTool.LOGIC_USER, UrlTool.USER_ACTION_CHECKVERSION);
        new OkRequest.Builder().url(url).headers(headers).params(params).post(callBack);
    }

    /**
     * 获取APP安装包的下载路径
     *
     * @param token
     * @param path        APK的安装路径
     * @param appFileName
     * @param callBack
     */
    public static void downloadAppApk(String token, String path, String appFileName, ResultCallBack callBack) {
        Headers headers = new Headers.Builder()
                .add("token", token)
                .build();

        String url = UrlTool.getDownloadUrl(UrlTool.APP_UPDATA_URL, appFileName);
        new OkRequest.Builder().url(url).headers(headers).destFileDir(path).destFileName(appFileName).download(callBack);
    }

    public static String getBoxUpadeUrl(String boxFileName) {
        return getUrl(UrlTool.BOX_UPDATA_URL, boxFileName);
    }

    public static String getWatchUpadeUrl(String watchFileName) {
        return getUrl(UrlTool.WATCH_UPDATA_URL, watchFileName);
    }

    /**
     * 获取本机蓝牙地址
     *
     * @return
     */
    private static String getLocalMac() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String address = bluetoothAdapter.getAddress();
        return address;
    }

}
