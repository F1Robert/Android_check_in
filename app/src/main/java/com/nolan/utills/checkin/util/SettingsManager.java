package com.nolan.utills.checkin.util;

/**
 * 作者 zf
 * 日期 2024/3/26
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.nolan.utills.checkin.ui.MainActivity;

public class SettingsManager {
    private static final String PREF_NAME = "settings_pref";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_ENABLE_INTERVAL = "enable_interval";

    private SharedPreferences sharedPreferences;

    public SettingsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSettings(int hour, int minute, int interval, boolean enableInterval) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HOUR, hour);
        editor.putInt(KEY_MINUTE, minute);
        editor.putInt(KEY_INTERVAL, interval);
        editor.putBoolean(KEY_ENABLE_INTERVAL, enableInterval);
        Log.e("settings", "saveSettings: 开启间隔" + enableInterval + " : " + interval + " 启动时间 " + hour + " : " + minute);
        MainActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                // 发送消息到UI线程
                Log.e("handler", "run: 发送message");
                Message message = MainActivity.getHandler().obtainMessage();
                if (enableInterval) {
                    message.obj = "按固定间隔打开企业微信功能已打开,间隔时间为" + interval + "分钟";
                    MainActivity.getHandler().sendMessage(message);
                }
                Message message2 = MainActivity.getHandler().obtainMessage();
                message2.obj = "当前每日固定打卡时间为:" + hour + ":" + minute;
                MainActivity.getHandler().sendMessage(message2);
            }
        });
        editor.apply();
    }

    public int getHour() {
        return sharedPreferences.getInt(KEY_HOUR, 19);
    }

    public int getMinute() {
        return sharedPreferences.getInt(KEY_MINUTE, 0);
    }

    public int getInterval() {
        return sharedPreferences.getInt(KEY_INTERVAL, 0);
    }

    public boolean isIntervalEnabled() {
        return sharedPreferences.getBoolean(KEY_ENABLE_INTERVAL, false);
    }
}
