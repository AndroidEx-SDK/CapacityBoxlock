package com.androidex.capbox.service;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.androidex.boxlib.modules.ServiceBean;
import com.androidex.boxlib.service.BleService;
import com.androidex.boxlib.utils.LocationUtil;
import com.androidex.capbox.MyApplication;
import com.androidex.capbox.data.cache.SharedPreTool;
import com.androidex.capbox.data.net.NetApi;
import com.androidex.capbox.data.net.base.ResultCallBack;
import com.androidex.capbox.db.ChatRecord;
import com.androidex.capbox.db.ChatRecordDao;
import com.androidex.capbox.db.DaoSession;
import com.androidex.capbox.db.DbUtil;
import com.androidex.capbox.db.DeviceInfo;
import com.androidex.capbox.db.DeviceInfoDao;
import com.androidex.capbox.db.Note;
import com.androidex.capbox.db.NoteDao;
import com.androidex.capbox.module.BaiduModel;
import com.androidex.capbox.ui.activity.LockScreenActivity;
import com.androidex.capbox.utils.RLog;
import com.androidex.capbox.utils.SystemUtil;
import com.androidex.boxlib.modules.LocationModules;

import org.greenrobot.greendao.DbUtils;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;

import java.util.List;

import okhttp3.Headers;
import okhttp3.Request;

import static com.androidex.boxlib.utils.BleConstants.BLE.BLE_CONN_DIS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ADDRESS;
import static com.androidex.boxlib.utils.BleConstants.BLECONSTANTS.BLECONSTANTS_ISACTIVEDisConnect;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_IN;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_RSSI_OUT;
import static com.androidex.capbox.utils.Constants.BASE.ACTION_TEMP_OUT;
import static com.androidex.capbox.utils.Constants.SP.SP_DISTANCE_TYPE;
import static com.androidex.capbox.utils.Constants.SP.SP_LOST_TYPE;
import static com.androidex.capbox.utils.Constants.SP.SP_TEMP_TYPE;
import static com.androidex.capbox.utils.Constants.VISE_COMMAND_TYPE_TEXT;
import static com.baidu.mapapi.BMapManager.getContext;

/**
 * @author liyp
 * 应用的服务类
 * @editTime 2017/12/4
 */

public class MyBleService extends BleService {
    public static final String TAG = "MyBleService";
    private static MyBleService service;
    private DeviceInfoDao deviceInfoDao;
    private static NoteDao noteDao;
    private ChatRecordDao chatRecordDao;

    @Override
    public void onCreate() {
        super.onCreate();
        RLog.e("MyBleService 启动");
        setLockScreenActivity(LockScreenActivity.class);//设置锁屏界面的Activity
        initDB();
    }

    /**
     * @return
     */
    public static BleService getInstance() {
        if (service == null) {
            service = new MyBleService();
        }
        return service;
    }

    /**
     * 初始化数据库
     */
    public void initDB() {
        DaoSession daoSession = ((MyApplication) getApplication()).getDaoSession();
        noteDao = daoSession.getNoteDao();
        deviceInfoDao = daoSession.getDeviceInfoDao();
        chatRecordDao = daoSession.getChatRecordDao();
    }

    /**
     * -keep class org.greenrobot.greendao.**{*;}
     * -keep public interface org.greenrobot.greendao.**
     * -keep class org.greenrobot.greendao.database.Database
     * -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
     * public static java.lang.String TABLENAME;
     * }
     * -keep class **$Properties
     * -keep class net.sqlcipher.database.**{*;}
     * -keep public interface net.sqlcipher.database.**
     * -dontwarn net.sqlcipher.database.**
     * -dontwarn org.greenrobot.greendao.**
     * 插入定位数据到数据库
     * <p>
     * 插入数据到数据库Note{id=1, address='A4:34:F1:84:25:29', time=1526025586, lat='2237.601437N', lon='11404.916731E', alt='116.0M', type=null}
     *
     * @param address
     * @param modules
     * @param longTime
     */
    @Override
    public void insertLocData(String address, LocationModules modules, long longTime) {
        Note note = new Note();
        note.setAddress(address);
        note.setLat(modules.getLat());
        note.setLon(modules.getLon());
        note.setAlt(modules.getAlt());
        note.setTime(longTime);
        note.setIsshow(0);
        List<DeviceInfo> list = deviceInfoDao.queryBuilder().where(DeviceInfoDao.Properties.Address.eq(address)).list();
        if (list.size() > 0) {
            DeviceInfo deviceInfo = list.get(0);
            //latitude=2237.591142N&longitude=11404.912392E
            addpoint(deviceInfo.getUuid(), modules, longTime);
            note.setIsSubmitBaidu(1);
            RLog.d("DeviceInfo  上传设备轨迹 deviceInfo.getUuid() = " + deviceInfo.getUuid());
        } else {
            RLog.d("DeviceInfo  设备不存在");
        }
        noteDao.insert(note);
        RLog.d("插入数据到数据库" + note.toString());
    }

    /**
     * 插入发送的数据
     *
     * @param address
     * @param content
     */
    @Override
    public void insertSendDB(String address, String content) {
        String uuid = getUUID(address);
        RLog.d("chat 111: " + uuid);
        if (chatRecordDao != null) {
            DbUtil.insertSendDB(chatRecordDao, address, uuid, content);
        }
    }

    /**
     * 插入接收到的数据
     *
     * @param address
     * @param content
     */
    @Override
    public void insertReceiveData(String address, String content) {
        String uuid = getUUID(address);
        RLog.d("chat 222 : " + uuid);
        if (chatRecordDao != null) {
            DbUtil.insertReceiveData(chatRecordDao, address, uuid, content);
        }
    }

    @Override
    protected String getUUID(String address) {
        String uuid = null;
        if (deviceInfoDao != null) {
            List<DeviceInfo> list = deviceInfoDao.queryBuilder().where(DeviceInfoDao.Properties.Address.eq(address)).list();
            if (list.size() > 0) {
                uuid = list.get(0).getUuid();
            }
        }
        return uuid;
    }

    /**
     *
     * 上传轨迹到百度鹰眼
     */
    private void addpoint(String uuid, LocationModules modules, long longTime) {
        String lat = modules.getLat().replace("N", "");
        String lon = modules.getLon().replace("E", "");
        NetApi.addpoint(uuid,LocationUtil.degreeToDB(lat),LocationUtil.degreeToDB(lon) , longTime, new ResultCallBack<BaiduModel>() {
            @Override
            public void onSuccess(int statusCode, Headers headers, BaiduModel model) {
                super.onSuccess(statusCode, headers, model);
            }

            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
            }
        });
    }

    /**
     * 根据mac地址查询设备数据
     * @param address
     * @return
     */
    public static List<Note> getLocListData(String address) {
        RLog.d("开始读取数据库数据");
        List<Note> list = noteDao.queryBuilder().where(NoteDao.Properties.Address.eq(address)).orderAsc(NoteDao.Properties.Time).list();
        for (Note note : list) {
            RLog.d(note.toString());
        }
        return noteDao.queryBuilder().where(NoteDao.Properties.Address.eq(address), NoteDao.Properties.Isshow.eq(0))
                .orderAsc(NoteDao.Properties.Time).list();
    }

    /**
     * 彻底删除轨迹
     *
     * @param address
     * @return
     */
    public static int deleateData(String address) {
        DeleteQuery<Note> deleteQuery = noteDao.queryBuilder().where(NoteDao.Properties.Address.eq(address)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        return noteDao.queryBuilder().where(NoteDao.Properties.Address.eq(address)).list().size();
    }

    /**
     * 设置之前的轨迹不显示
     */
    public static int updateNoShowStatusData(String address) {
        Query<Note> query = noteDao.queryBuilder().where(NoteDao.Properties.Address.eq(address)).build();
        List<Note> list = query.list();
        if (list.size() > 0) {
            for (Note note : list) {
                note.setIsshow(1);
                noteDao.update(note);
            }
        }
        return getLocListData(address).size();
    }

    /**
     * 设置之前的轨迹显示
     */
    public static int updateShowStatusData(String address) {
        Query<Note> query = noteDao.queryBuilder().where(NoteDao.Properties.Address.eq(address)).build();
        List<Note> list = query.list();
        if (list.size() > 0) {
            for (Note note : list) {
                note.setIsshow(0);
                noteDao.update(note);
            }
        }
        return list.size();
    }


    /**
     * 异常断开后调用，主动断开需要调用以下方法可不收到断开信息，不必做处理。
     * ServiceBean device = getConnectDevice(address);
     * if (device != null) {
     * device.setActiveDisConnect(true);
     * }
     *
     * @param address
     */
    @Override
    public void disConnect(String address) {
        ServiceBean device = getConnectDevice(address);
        if (device != null) {
            if (device.isActiveDisConnect()) {//判断是否是主动断开，true就不报警,在主动断开的时候就要设置该值为true
                sendDisconnectMessage(address, true);
            } else {
                sendDisconnectMessage(address, false);
            }
        } else {
            sendDisconnectMessage(address, false);
        }
    }

    /**
     * 连接上设备后调用
     *
     * @param address
     */
    @Override
    protected void initDevice(String address) {
        ServiceBean connectDevice = MyBleService.getInstance().getConnectDevice(address);
        if (connectDevice == null) return;
        ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
        if (device != null) {
            RLog.e("device====" + device.toString());
            connectDevice.setStartCarry(device.isStartCarry());//取出携行状态
            connectDevice.setPolice(device.isPolice());
            connectDevice.setDistanceAlarm(device.isDistanceAlarm());
            connectDevice.setTamperAlarm(device.isTamperAlarm());
            connectDevice.setTempAlarm(device.isTempAlarm());
            connectDevice.setHumAlarm(device.isHumAlarm());
            RLog.e("转换后设备参数" + connectDevice.toString());
        } else {
            //RLog.e("device ");
        }
    }

    /**
     * 断开连接时发送消息通知
     *
     * @param address
     * @param isActive
     */
    private void sendDisconnectMessage(String address, boolean isActive) {
        if (SharedPreTool.getInstance(this).getBoolData(SharedPreTool.IS_POLICE, true)) {
            ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
            if (device != null && device.isPolice()) {//断开连接广播
                sendBroadDis(address, isActive);
            } else {
                ServiceBean connectDevice = MyBleService.getInstance().getConnectDevice(address);
                if (connectDevice != null && connectDevice.isPolice()) {//断开连接广播
                    sendBroadDis(address, isActive);
                } else {
                    RLog.e("已关闭单个箱子报警开关");
                }
            }
        } else {
            RLog.e("已关闭报警开关");
        }
    }

    /**
     * 发送蓝牙脱距广播
     *
     * @param address
     * @param isActive
     */
    public void sendBroadDis(String address, boolean isActive) {
        if (!isStartCarry(address)) {
            sendDisBroadcast(address, true);
            return;//判断是否启动携行，没启动携行时不报警
        }
        switch (SharedPreTool.getInstance(this).getIntData(SP_LOST_TYPE, 0)) {
            case 0:
                sendDisBroadcast(address, isActive);
                if (!isActive) {
                    SystemUtil.startPlayerRaw(getContext());
                }
                break;
            case 1:
                if (!isActive) {
                    SystemUtil.startPlayerRaw(getContext());
                }
                sendDisBroadcast(address, true);
                break;
            case 2:
                sendDisBroadcast(address, isActive);
                break;
            default:
                break;
        }
    }

    /**
     * @param address
     * @return
     */
    public boolean isStartCarry(String address) {
        ServiceBean obj = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
        if (obj != null) {
            RLog.e("service is startCarry ==" + obj.isStartCarry());
            if (obj.isStartCarry()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void sendDisBroadcast(String address, boolean isActive) {
        Intent intent = new Intent(BLE_CONN_DIS);
        intent.putExtra(BLECONSTANTS_ADDRESS, address);
        intent.putExtra(BLECONSTANTS_ISACTIVEDisConnect, isActive);
        sendBroadcast(intent);
    }


    /**
     * 连续三次超出阈值会回调这里，1s更新一次信号强度
     */
    @Override
    public void outOfScopeRssi(String address) {
        if (SharedPreTool.getInstance(this).getBoolData(SharedPreTool.IS_POLICE, true)) {
            ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
            if (device != null && device.isDistanceAlarm()) {//信号弱，报警
                sendRSSIOut(address);
            } else {
                ServiceBean connectDevice = MyBleService.getInstance().getConnectDevice(address);
                if (connectDevice != null && connectDevice.isDistanceAlarm()) {//信号弱，报警
                    sendRSSIOut(address);
                } else {
                    RLog.e("已关闭单个箱子距离报警开关");
                }
            }
        } else {
            RLog.e("已关闭报警开关");
        }
    }

    /**
     * 发送信号弱的广播
     *
     * @param address
     */
    private void sendRSSIOut(String address) {
        if (!isStartCarry(address)) return;//判断是否启动携行，没启动携行时不报警
        switch (SharedPreTool.getInstance(this).getIntData(SP_DISTANCE_TYPE, 0)) {
            case 0:
                sendTempOutBroad(address, ACTION_RSSI_OUT);
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 1:
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 2:
                sendTempOutBroad(address, ACTION_RSSI_OUT);
                break;
            default:
                break;
        }

    }

    /**
     * 超出阈值调用报警后，又恢复到阈值范围内，停止报警回调
     */
    @Override
    public void inOfScopeRssi(String address) {
        sendTempOutBroad(address, ACTION_RSSI_IN);
        SystemUtil.stopPlayRaw();
    }

    /**
     * 温度超范围报警
     */
    @Override
    public void outOfScopeTempPolice(String address) {
        if (SharedPreTool.getInstance(this).getBoolData(SharedPreTool.IS_POLICE, true)) {
            ServiceBean device = SharedPreTool.getInstance(this).getObj(ServiceBean.class, address);
            if (device != null && device.isTempAlarm()) {//非主动断开时，报警
                RLog.e("发送广播，温度超范围报警");
                sendTempOutBroadcast(address);
            } else {
                ServiceBean connectDevice = MyBleService.getInstance().getConnectDevice(address);
                if (connectDevice != null && connectDevice.isTempAlarm()) {//非主动断开时，报警
                    RLog.e("发送广播，温度超范围报警");
                    sendTempOutBroadcast(address);
                } else {
                    RLog.e("已关闭单个箱子温度报警开关");
                }
            }
        } else {
            RLog.e("已关闭报警开关");
        }
    }

    /**
     * 发送温度超范围广播
     *
     * @param address
     */
    private void sendTempOutBroadcast(String address) {
        if (!isStartCarry(address)) return;//判断是否启动携行，没启动携行时不报警
        switch (SharedPreTool.getInstance(this).getIntData(SP_TEMP_TYPE, 0)) {
            case 0:
                sendTempOutBroad(address, ACTION_TEMP_OUT);
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 1:
                SystemUtil.startPlayerRaw(getContext());
                break;
            case 2:
                sendTempOutBroad(address, ACTION_TEMP_OUT);
                break;
            default:
                break;
        }
    }

    private void sendTempOutBroad(String address, String actionTempOut) {
        Intent intent = new Intent(actionTempOut);
        intent.putExtra(BLECONSTANTS_ADDRESS, address);
        sendBroadcast(intent);
    }

    /**
     * 温度又恢复到范围内
     */
    @Override
    public void inOfScopeTempPolice(String address) {
        SystemUtil.stopPlayRaw();
    }

}
