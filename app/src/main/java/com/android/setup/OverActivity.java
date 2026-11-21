package com.android.setup;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_over);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button Over = findViewById(R.id.buttonover);
        Over.setOnClickListener(v -> {
            onOver();
        });
    }
    private void onOver() {
        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        Settings.Secure.putInt(getContentResolver(), "user_setup_complete", 1);
        Settings.Secure.putInt(getContentResolver(), "setup_wizard_has_run", 1);

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launcherIntent);
        Toast.makeText(OverActivity.this, getString(R.string.start_toast), Toast.LENGTH_SHORT).show();

        String myPackageName = getPackageName();
        PackageManager pm = getPackageManager();
        pm.setApplicationEnabledSetting(
                myPackageName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                0
        );
    }
}