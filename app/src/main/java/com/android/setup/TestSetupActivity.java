package com.android.setup;

import com.android.setup.compose.CollapsingToolbarBaseActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

public class TestSetupActivity extends CollapsingToolbarBaseActivity {

    /** 控制 IconPreferenceFragment 中 test_mode 分组显示/隐藏的 SP key */
    public static final String KEY_TEST_MODE_ENABLED = "test_mode_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_setup1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 顶层页不需要返回箭头
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button test = findViewById(R.id.testbutton1);
        test.setOnClickListener(v -> onStt());

        Switch switch1 = findViewById(R.id.switch1);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        // 进入页面时恢复上次的开关状态（默认关闭）
        switch1.setChecked(sp.getBoolean(KEY_TEST_MODE_ENABLED, false));
        // 切换时写入 SP，IconPreferenceFragment 会在加载时读取
        switch1.setOnCheckedChangeListener((buttonView, isChecked) ->
                sp.edit().putBoolean(KEY_TEST_MODE_ENABLED, isChecked).apply());
    }
    protected void onStt() {
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0);
        Settings.Secure.putInt(getContentResolver(), "user_setup_complete", 0);
        Settings.Secure.putInt(getContentResolver(), "setup_wizard_has_run", 0);
        //Start MainActivity
        Intent intent = new Intent(TestSetupActivity.this,MainActivity.class);
        startActivity(intent);
    }
}