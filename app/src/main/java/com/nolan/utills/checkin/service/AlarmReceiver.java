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

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received, starting the application...");
        // 在这里启动你想要启动的应用程序
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.wework");
        if (launchIntent != null) {
            MainActivity.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    // 发送消息到UI线程
                    Log.e(TAG, "run: 发送message");
                    Message message = MainActivity.getHandler().obtainMessage();
                    message.obj = "启动企业微信";
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
