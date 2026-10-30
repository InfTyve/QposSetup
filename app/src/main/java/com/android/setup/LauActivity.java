package com.android.setup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.LocaleList;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.internal.app.LocalePicker;
import com.google.android.material.button.MaterialButton;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 语言选择页。
 *
 * 交互参考 LineageOS SetupWizard 的 LocaleActivity：
 * 轮盘滚动选择语言 → 停顿片刻后应用（防抖，避免滚动时频繁改系统配置）
 * → 系统级持久化（LocalePicker.updateLocales 内部走 updatePersistentConfiguration）。
 *
 * 右下角按钮进入下一页 StartActivity。
 */
public class LauActivity extends AppCompatActivity {

    /** 滚动停下多久后真正应用语言，与 SetupWizard 的 1s 防抖同思路 */
    private static final long APPLY_DELAY_MS = 600;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private NumberPicker mLanguagePicker;
    private String[] mLabels = new String[0];
    private Locale[] mLocales = new Locale[0];
    private boolean mIsApplying = false;

    private final Runnable mUpdateLocale = () -> {
        if (mLanguagePicker == null) return;
        int index = mLanguagePicker.getValue();
        if (index < 0 || index >= mLocales.length) return;
        applyLocale(mLocales[index]);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lau);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mLanguagePicker = findViewById(R.id.locale_picker);
        loadLanguages();

        MaterialButton next = findViewById(R.id.lau_next);
        next.setOnClickListener(v ->
                startActivity(new Intent(LauActivity.this, StartActivity.class)));
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mUpdateLocale);
        super.onDestroy();
    }

    // ---------------------------------------------------------------- 列表

    private void loadLanguages() {
        List<String> labels = new ArrayList<>();
        List<Locale> locales = new ArrayList<>();

        // 优先用平台的 LocalePicker（与 SetupWizard 同一份数据源，含系统自带语言与排序）
        try {
            ArrayAdapter<LocalePicker.LocaleInfo> adapter =
                    LocalePicker.constructAdapter(this, R.layout.item_locale_picker, R.id.locale);
            for (int i = 0; i < adapter.getCount(); i++) {
                LocalePicker.LocaleInfo info = adapter.getItem(i);
                if (info == null || info.getLocale() == null) continue;
                labels.add(info.getLabel());
                locales.add(info.getLocale());
            }
        } catch (Throwable ignored) {
            // 隐藏 API 被拦截时退化到 java.util.Locale
        }

        if (labels.isEmpty()) {
            buildFromAvailableLocales(labels, locales);
        }

        mLabels = labels.toArray(new String[0]);
        mLocales = locales.toArray(new Locale[0]);

        mLanguagePicker.setMinValue(0);
        mLanguagePicker.setMaxValue(Math.max(mLabels.length - 1, 0));
        mLanguagePicker.setDisplayedValues(mLabels);
        mLanguagePicker.setValue(findCurrentIndex());
        mLanguagePicker.setWrapSelectorWheel(false);
        mLanguagePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mLanguagePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            // 防抖：滚动过程中不断触发，停下 APPLY_DELAY_MS 后才应用
            mHandler.removeCallbacks(mUpdateLocale);
            mHandler.postDelayed(mUpdateLocale, APPLY_DELAY_MS);
        });
    }

    private void buildFromAvailableLocales(List<String> labels, List<Locale> locales) {
        Locale display = Locale.getDefault();
        Collator collator = Collator.getInstance(display);
        List<Locale> all = new ArrayList<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            String label = locale.getDisplayName(display);
            if (label == null || label.trim().isEmpty()) continue; // 跳过 root
            if (labels.contains(label)) continue;                  // 去重（同名地区）
            labels.add(label);
            all.add(locale);
        }
        // 按显示名排序，保持与系统语言列表一致的体验
        Integer[] order = new Integer[all.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        java.util.Arrays.sort(order, (a, b) -> collator.compare(labels.get(a), labels.get(b)));

        List<String> sortedLabels = new ArrayList<>(labels.size());
        for (int i : order) {
            sortedLabels.add(labels.get(i));
            locales.add(all.get(i));
        }
        labels.clear();
        labels.addAll(sortedLabels);
    }

    /** 当前系统语言在列表中的下标；精确匹配失败则退化为同语种，再失败取 0 */
    private int findCurrentIndex() {
        Locale current = LocalePicker.getLocales().get(0);
        for (int i = 0; i < mLocales.length; i++) {
            if (mLocales[i].equals(current)) return i;
        }
        for (int i = 0; i < mLocales.length; i++) {
            if (mLocales[i].getLanguage().equals(current.getLanguage())) return i;
        }
        return 0;
    }

    // ---------------------------------------------------------------- 应用

    private void applyLocale(Locale locale) {
        if (mIsApplying || locale == null) return;
        mIsApplying = true;
        try {
            // 系统级切换并持久化（system uid + 平台签名，与 SetupWizard 相同路径）
            LocalePicker.updateLocales(new LocaleList(locale));
        } catch (Throwable t) {
            // 隐藏 API 不可用时至少让本应用立即生效
            Locale.setDefault(locale);
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale));
        }
        // 正常情况下系统会因配置变化重建本页；未重建时恢复交互
        mHandler.postDelayed(() -> mIsApplying = false, APPLY_DELAY_MS);
    }
}
