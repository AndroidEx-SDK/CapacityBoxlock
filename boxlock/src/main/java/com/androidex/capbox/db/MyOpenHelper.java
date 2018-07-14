package com.androidex.capbox.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/7/14
 */
public class MyOpenHelper extends DaoMaster.DevOpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 升级时务必在此进行升级处理，不能单纯升级数据库版本，否则会导致用户本地数据库数据丢失
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
