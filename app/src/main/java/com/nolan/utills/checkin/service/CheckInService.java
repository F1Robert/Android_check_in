package com.nolan.utills.checkin.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.nolan.utills.checkin.util.SettingsManager;

import java.util.Calendar;

public class CheckInService extends Service {
    public CheckInService() {

    }
    private static final String TAG = "CheckInService";
    private static final int ALARM_REQUEST_CODE = 123;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        // 设置定时任务，在每天下午7点触发
        scheduleDailyAlarm();
        // 设置每小时打开程序一次
        scheduleHourlyAlarm();
        // 每3分钟返回一次桌面
        //scheduleHourlyAlarmSetting();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 在Activity中保存设置
        SettingsManager settingsManager = new SettingsManager(getApplicationContext());

        // 在Activity中获取设置
        int hour = settingsManager.getHour();
        int minute = settingsManager.getMinute();

        // 设置每天下午7点触发
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void scheduleHourlyAlarm() {
        // 在Activity中保存设置
        SettingsManager settingsManager = new SettingsManager(getApplicationContext());
        if (settingsManager.isIntervalEnabled()) {
            long interval = settingsManager.getInterval() * 60 * 1000;
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 使用ELAPSED_REALTIME触发器，在设备休眠模式下也会触发
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval, interval, pendingIntent);
        } else {

        }
    }

    private void scheduleHourlyAlarmSetting() {
        // 在Activity中保存设置
        SettingsManager settingsManager = new SettingsManager(getApplicationContext());
        if (settingsManager.isIntervalEnabled()) {
            long interval = settingsManager.getInterval() * 60 * 1000;
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, SettingReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 使用ELAPSED_REALTIME触发器，在设备休眠模式下也会触发
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval, interval, pendingIntent);
        } else {

        }
    }
}