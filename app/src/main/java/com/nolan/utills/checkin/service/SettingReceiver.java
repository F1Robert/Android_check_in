package com.nolan.utills.checkin.service;

/**
 * 作者 zf
 * 日期 2024/3/26
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.nolan.utills.checkin.ui.MainActivity;

public class SettingReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received, starting the application...");
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.android.settings");
        if (launchIntent != null) {
            // 在需要发送消息的地方调用该方法
            MainActivity.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    // 发送消息到UI线程
                    Message message = MainActivity.getHandler().obtainMessage();
                    message.obj = "启动设置";
                    MainActivity.getHandler().sendMessage(message);
                }
            });
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } else {
            Log.e(TAG, "Unable to launch application. Package not found.");
        }
    }
}
