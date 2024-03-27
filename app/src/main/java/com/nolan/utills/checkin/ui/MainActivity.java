package com.nolan.utills.checkin.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nolan.utills.checkin.adapter.LogAdapter;
import com.nolan.utills.checkin.databinding.ActivityMainBinding;

import com.nolan.utills.checkin.R;
import com.nolan.utills.checkin.service.CheckInService;
import com.nolan.utills.checkin.util.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textViewTitle;
    private RecyclerView recyclerViewLogs;
    private LogAdapter logAdapter;
    private List<String> logs;
    private FloatingActionButton fabSettings;

    private RelativeLayout root;

    private LinearLayout switchMenu;
    private static Handler handler;
    private String TAG = "handler";
    private TextView switchNone;
    private TextView switchApp;

    private ImageView wework;
    private ImageView ding;
    private ImageView feiShu;

    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsManager = new SettingsManager(this);
        textViewTitle = findViewById(R.id.textViewTitle);
        switchNone = findViewById(R.id.switch_none);
        recyclerViewLogs = findViewById(R.id.recyclerViewLogs);
        fabSettings = findViewById(R.id.fabSettings);
        root = findViewById(R.id.root);
        switchMenu = findViewById(R.id.switchAppWindow);
        switchApp = findViewById(R.id.switch_app);
        switchApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchMenu.getVisibility() == View.GONE) {
                    switchMenu.setVisibility(View.VISIBLE);
                } else {
                    switchMenu.setVisibility(View.GONE);
                }
            }
        });

        wework = findViewById(R.id.wechat);
        ding = findViewById(R.id.ding);
        feiShu = findViewById(R.id.feishu);

        wework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsManager.saveApp("com.tencent.wework");
                switchMenu.setVisibility(View.GONE);
            }
        });
        ding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsManager.saveApp("com.alibaba.android.rimet");
                switchMenu.setVisibility(View.GONE);
            }
        });

        feiShu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsManager.saveApp("ccom.ss.android.lark");
                switchMenu.setVisibility(View.GONE);
            }
        });
        // 初始化日志列表
        logs = new ArrayList<>();
        logAdapter = new LogAdapter(logs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLogs.setAdapter(logAdapter);
        // 初始化Handler并与主线程绑定
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // 在这里处理接收到的消息并更新UI
                Log.e("Alarm", "handleMessage: 收到消息");
                String message = (String) msg.obj;
                addLog(message);
            }
        };

        // 设置悬浮按钮点击事件
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到设置界面
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                MainActivity.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        // 发送消息到UI线程
                        Log.e("handler", "run: 发送message");
                        Message message = MainActivity.getHandler().obtainMessage();
                        message.obj = "进行开启时间设置";
                        MainActivity.getHandler().sendMessage(message);
                    }
                });
            }
        });

        switchNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.setVisibility(View.GONE);
                // 在按钮点击事件中动态设置透明度
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 0.0f; // 设置透明
                getWindow().setAttributes(params);
                Toast.makeText(getApplicationContext(), "重启后恢复界面，请保持后台运行", Toast.LENGTH_SHORT).show();
            }
        });

        startService();
        addLog("程序已自动设定每日19:00自行启动企业微信app");
        addLog("请在企业微信app的打卡设置中启用打开app自动打卡以起用打卡助手功能");
        addLog("本程序将全程在后台自动运行，请勿手动关闭或者清理后台运行的该程序");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Alarm", "onPause: 应用暂停");
        MainActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                // 发送消息到UI线程
                Log.e(TAG, "run: 发送message");
                Message message = MainActivity.getHandler().obtainMessage();
                message.obj = "程序进入后台";
                MainActivity.getHandler().sendMessage(message);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Alarm", "onPause: 应用暂停");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startService();
        MainActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                // 发送消息到UI线程
                Log.e(TAG, "run: 发送message");
                Message message = MainActivity.getHandler().obtainMessage();
                message.obj = "程序重启";
                MainActivity.getHandler().sendMessage(message);
            }
        });
    }

    private void addLog(String action) {
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        // 构建日志内容
        //String logMessage = currentTime + " - " + action;
        String logMessage = action;
        // 添加到日志列表
        logs.add(logMessage);
        logAdapter.notifyDataSetChanged();
        Log.e("Alarm", "addLog: " + logMessage);
    }

    public static Handler getHandler() {
        return handler;
    }

    public void startService() {
        // 启动后台服务
        Intent serviceIntent = new Intent(this, CheckInService.class);
        startService(serviceIntent);
    }
}