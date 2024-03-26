package com.nolan.utills.checkin.ui;

/**
 * 作者 zf
 * 日期 2024/3/26
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nolan.utills.checkin.R;
import com.nolan.utills.checkin.util.SettingsManager;

public class SettingsActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText editTextInterval;
    private CheckBox checkBoxEnableInterval;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        timePicker = findViewById(R.id.timePicker);
        editTextInterval = findViewById(R.id.editTextInterval);
        checkBoxEnableInterval = findViewById(R.id.checkBoxEnableInterval);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void saveSettings() {
        // 获取用户设置的时间
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // 获取用户设置的间隔
        String intervalStr = editTextInterval.getText().toString();
        int interval = 0;
        if (!intervalStr.isEmpty()) {
            interval = Integer.parseInt(intervalStr);
        }

        // 获取用户是否启用按固定间隔打开程序的设置
        boolean enableInterval = checkBoxEnableInterval.isChecked();

        // 在这里保存设置，可以使用SharedPreferences等方式保存设置信息

        SettingsManager settingsManager = new SettingsManager(this);
        settingsManager.saveSettings(hour, minute, interval, enableInterval);

        // 提示保存成功
        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();

        startMainActivity();
    }

    public void startMainActivity() {
        // 跳转到主界面
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}

