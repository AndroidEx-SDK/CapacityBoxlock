# CapacityBoxlockLib

#### 联系作者：liyp@androidex.cn
#### boxlib.aar库更新地址：https://github.com/AndroidEx-SDK/CapacityBoxlockLib.git

------------------版本：0.0.1-------------------------------

## 该库提供蓝牙连接、断开、搜索、连接判断、连接状态发生变化、获取连接设备、读取数据、写入数据、信号强度回调、

蓝牙相关主要通过BleService来实现，BleService是个单例，使用BleService.get()方法调用。

蓝牙搜索：startScanner(scanCfg, new BLEScanListener();

蓝牙停止搜索：stopScan();

蓝牙连接：connectDevice(Context context, String bleMac);第二个参数为需要连接的设备的address.

断开某个设备连接：disConnectDevice(String bleMac);

断开所有设备连接：disConnectDeviceALL();

断开回调：disConnect(String address);需要做脱距报警只需要在这里处理即可;

连接判断：isConnectDevice(String bleMac);

启用通知：enableNotify(String address);根据mac地址启动通知后,才能收到对应设备的接收信息，接收到的信息以广播的形式发送。
        建议在连接成功后调用;

发送数据：sendData(String address, byte[] bytes);第一个参数，要发送给目标设备的mac,第二个参数是指令。
        该方法为临时或者额外增加协议时使用，协议里面所有的指令都已经在后面写好，直接调用就可以，不需要重复使用该方法。后面会介绍到。

读取数据：readCharacter(String address);//主动特征值变化，该方法一般不用。无返回值。

获取连接的设备: getConnectDevice(String address); 返回值类型为ServiceBean;

获取连接的所有设备: getAllConnectDevice();返回值Map<String, ServiceBean> ;

设置信号强度：setRssiMaxValue(int maxValue);参数取值范围为-100到0;为蓝牙信号强度阈值;

信号强度达到阈值后回调：outOfScopeRssi();达到阈值后会调用。

### 接收的广播：

       BLE_CONN_SUCCESS -------蓝牙连接成功;
       BLE_CONN_SUCCESS_ALLCONNECTED -------蓝牙已经连接;
       BLE_CONN_FAIL -------蓝牙连接失败;
       BLE_CONN_DIS -------蓝牙断开连接;
       BLE_CONN_RSSI -------蓝牙信号强度;
       ACTION_BIND -------扫码绑定箱体;
       ACTION_POSSESSORFINGER -------所有人的指纹信息;
       ACTION_BECOMEFINGER -------静默功能的指纹信息;
       ACTION_CARRYFINGER -------携行人的指纹信息;
       ACTION_RECOVER -------恢复出厂;
       ACTION_ONEKEYCONFIG -------一键配置;
       ACTION_START_CARRYESCORT -------启动携行押运;
       ACTION_CLEARFINGER -------清除指纹;
       ACTION_BOX_MAC -------发送箱体MAC给腕表;
       ACTION_WATCH_HEART -------腕表的心跳;
       ACTION_HEART -------收到心跳;
       ACTION_DATA_ITEMFRAGMENT -------腕表的心跳;
       ACTION_LOCK_OPEN_SUCCED -------开锁成功;
       ACTION_LOCK_STARTS -------锁状态;
       ACTION_UUID -------获取UUID,箱体的UUID并非蓝牙的UUID;
       ACTION_BOX_VERSION -------获取箱体的版本号;
       ACTION_END_TAST -------结束携行押运;
       ACTION_DISCONNECT_BLE_AFFIRM -------断开确认;
       ACTION_TEMP_UPDATE -------温度更新;
            ---通过 BLECONSTANTS_ADDRESS 从intent中获取设备的mac地址;
            ---通过 BLECONSTANTS_DATA 从intent中获取设备返回的信息;

### 发送数据接口：

        /**
         * 扫码绑定箱体，默认箱体所有人也是携行人
         */
        void bind(String address);

        /**
         * 录入指纹  指纹录入需要连续录入三次，才可收到成功广播，失败会立即收到失败广播
         * cmd 录入到所有人的指纹信息1101
         *     录入携行人指纹1201
         *     录入一键静默功能指纹1301
         */
        void getFinger(String address, String cmd);

        /**
         * 获取箱体的版本号
         */
        void getBoxVersion(String address);

        /**
         * 启动携行押运
         */
        void startEscort(String address);

        /**
         * 结束携行押运
         */
        void endTask(String address);

        /**
         * 断开确认
         */
        void disConnectBleAffirm(String address);

        /**
         * 一键配置密管箱，箱子从服务器读取配置信息，和三种模式的指纹信息。
         */
        void startBoxConfig(String address);

        /**
         * 清楚指纹信息
         */
        void clearFinger(String address);

        /**
         * 一键恢复出厂，删掉所有配置信息以及所有人
         */
        void recover(String address);

        /**
         * 开锁
         */
        void openLock(String address);

        /**
         * 获取锁状态
         */
        void getLockStatus(String address);

        /**
         * 箱体上报定位信息,将纬度经度和高程传到APP
         */
        void callbackBox(String address);

        /**
         * 密运箱心跳，返回箱体信息
         */
        void sentHeartBeat(String address, int rssi);

        /**
         * 腕表的心跳
         * 心跳+携行人的体况信息
         */
        void sentHeart(String address);

        /**
         * 获取mac地址接口
         */
        void getUUID(String address);

        /**
         * 发送密运箱的MAC
         */
        void sentBraceletMac(String address, String mac);
------------------------0.0.1----------------------------------

-------版本 0.0.2-------
###增加手机蓝牙状态监听

    通过广播的形式接收：
        手机蓝牙关闭----BLUTOOTH_OFF
        手机蓝牙打开----BLUTOOTH_ON

###获取蓝牙设备的信号强度

    通过广播的形式接收：
        获取信号强度成功----BLE_CONN_RSSI_SUCCED
        获取信号强度失败----BLE_CONN_RSSI_FAIL
            ----通过 BLECONSTANTS_ADDRESS 从intent中获取设备mac
            ----通过 BLECONSTANTS_RSSI 从intent中获取信号强度
            ----通过 BLECONSTANTS_RSSI_CODE 从intent中获取失败识别码;

##############################










