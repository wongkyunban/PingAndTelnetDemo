package com.cs.ping;

import android.app.Application;
import android.content.SharedPreferences;

public class PingApplication extends Application {
    // 供全局使用
    private static SharedPreferences shared;
    public static SharedPreferences getShared(){
        return shared;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    // 初始化的事都在这里进行
    private void init(){
        shared = getSharedPreferences(Global.SHARED_DB,MODE_PRIVATE);

    }
}
