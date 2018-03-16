package com.androidex.capbox.service;

import android.app.Activity;

import com.androidex.capbox.MainActivity;

import no.nordicsemi.android.dfu.DfuBaseService;


/**
 * 空中升级的服务
 */
public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {

        return MainActivity.class;
    }
}

