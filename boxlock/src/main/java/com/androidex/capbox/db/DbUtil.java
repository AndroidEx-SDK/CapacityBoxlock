package com.androidex.capbox.db;

import com.androidex.capbox.utils.RLog;

import static com.androidex.capbox.utils.Constants.VISE_COMMAND_TYPE_TEXT;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/7/31
 */
public class DbUtil {
    /**
     *
     * @param chatRecordDao
     * @param address
     * @param name
     * @param uuid
     * @param content
     * @param time
     */
    public static void insertSendDB(ChatRecordDao chatRecordDao, String address, String name, String uuid, String content, Long time) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setAddress(address)
                .setIsRead("1")
                .setIsSend("0")
                .setDeleteChat("0")
                .setMsgContent(content)
                .setNickName(name)
                .setMsgType(VISE_COMMAND_TYPE_TEXT)
                .setTime(time)
                .setUuid(uuid);
        RLog.d("msg 发送的数据 = " + chatRecord.toString());
        chatRecordDao.insert(chatRecord);

    }

    /**
     * 插入接收到的数据
     *
     * @param chatRecordDao
     * @param address
     * @param name
     * @param uuid
     * @param content
     * @param time
     */
    public static void insertReceiveData(ChatRecordDao chatRecordDao, String address, String name, String uuid, String content, Long time) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setAddress(address)
                .setIsRead("1")
                .setIsSend("1")
                .setDeleteChat("0")
                .setMsgContent(content)
                .setNickName(name)
                .setMsgType(VISE_COMMAND_TYPE_TEXT)
                .setTime(time)
                .setUuid(uuid);
        RLog.d("msg 接收到的数据 = " + chatRecord.toString());
        chatRecordDao.insert(chatRecord);
    }

}
