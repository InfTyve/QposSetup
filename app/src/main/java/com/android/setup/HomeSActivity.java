package com.android.setup;
import android.os.Bundle;

import com.android.setup.compose.CollapsingToolbarBaseActivity;

/**
 * 首页：Settings 风格的顶层页。
 * 大标题常驻不折叠，内容是一个 Preference 列表，与 Android 12 设置首页一致。
 */
public class HomeSActivity extends CollapsingToolbarBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 顶层页不需要返回箭头
        }
        setLargeTitle(R.string.app_name);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new HomeSFragment())
                    .commit();
        }
    }
}