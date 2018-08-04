package com.androidex.capbox.db;

import com.androidex.capbox.utils.CalendarUtil;
import com.androidex.capbox.utils.RLog;

import java.util.Date;

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
     * @param uuid
     * @param content
     */
    public static void insertSendDB(ChatRecordDao chatRecordDao, String address, String uuid, String content) {

        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setAddress(address)
                .setIsRead("1")
                .setIsSend("0")
                .setDeleteChat("0")
                .setMsgContent(content)
                .setNickName(CalendarUtil.getName(address))
                .setMsgType(VISE_COMMAND_TYPE_TEXT)
                .setTime(new Date().getTime())
                .setUuid(uuid);
        RLog.d("msg 发送的数据 = " + chatRecord.toString());
        chatRecordDao.insert(chatRecord);
    }

    /**
     * 插入接收到的数据
     *
     * @param chatRecordDao
     * @param address
     * @param uuid
     * @param content
     */
    public static void insertReceiveData(ChatRecordDao chatRecordDao, String address, String uuid, String content) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setAddress(address)
                .setIsRead("1")
                .setIsSend("1")
                .setDeleteChat("0")
                .setMsgContent(content)
                .setNickName(CalendarUtil.getName(address))
                .setMsgType(VISE_COMMAND_TYPE_TEXT)
                .setTime(new Date().getTime())
                .setUuid(uuid);
        RLog.d("msg 接收到的数据 = " + chatRecord.toString());
        chatRecordDao.insert(chatRecord);
    }

}
