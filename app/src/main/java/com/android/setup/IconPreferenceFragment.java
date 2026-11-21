package com.android.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.android.setup.compose.ui.preference.BasePreferenceFragment;
import com.android.setup.service.OverlayPreferenceSettings;

public class IconPreferenceFragment extends BasePreferenceFragment implements Preference.OnPreferenceClickListener {
    private SharedPreferences sharedPreferences = null;
    private static final String IconQqq = "QQQ";
    private static final String IconQut = "Qut";
    private static final String IconNot1 = "Not1";
    private static final String IconNot2 = "Not2";


    private Preference loginDjiAccount;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.icon_settings, rootKey);
        //用于取值的SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initView();
        bindThemeOverlays();
    }

    /**
     * 给「图标形状 / 字体 / 强调色」三项填入真实的 overlay 列表。
     *
     * 这三项在 icon_settings.xml 里只有 key、没有 entries/entryValues，
     * 不填数据直接点会弹空对话框（甚至因空 entries 崩），这里交给
     * OverlayCategoryPreferenceController 按系统 overlay 动态填充。
     */
    private void bindThemeOverlays() {
        String[] categories = {
                "android.theme.customization.accent_color",
                "android.theme.customization.font",
                "android.theme.customization.adaptive_icon_shape"
        };
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            return;
        }
        for (String category : categories) {
            OverlayCategoryPreferenceController controller =
                    new OverlayCategoryPreferenceController(requireContext(), category);
            controller.displayPreference(screen);
            controller.updateState();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 延迟到视图创建后再关闭分隔线，避免 mRecyclerView 尚未初始化导致 NPE
        setDividerHeight(0);
    }

    private void initView() {
        loginDjiAccount = findPreference("login_dji_account");
        if (loginDjiAccount != null) {
            loginDjiAccount.setOnPreferenceClickListener(this);
        }
        // 跳转 Overlay 选择页
        Preference over = findPreference("over_ooo");
        if (over != null) {
            over.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), OverActivity.class);
                startActivity(intent);
                return true;
            });
        }
        // 初始化时根据 SP 同步一次可见性
        applyTestModeVisibilityFromSp();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次回到页面时重新读取 SP，保证切换开关后能立即生效
        applyTestModeVisibilityFromSp();
    }

    /**
     * 从 SharedPreferences 读取开关状态，同步到 test_mode 分组的可见性。
     */
    private void applyTestModeVisibilityFromSp() {
        if (sharedPreferences == null) {
            return;
        }
        boolean enabled = sharedPreferences.getBoolean(TestSetupActivity.KEY_TEST_MODE_ENABLED, false);
        setTestModeCategoryVisible(enabled);
    }

    /**
     * 显示或隐藏 key="test_mode" 的 PreferenceCategory（包含其下所有子项）。
     *
     * @param visible true 显示，false 隐藏
     */
    public void setTestModeCategoryVisible(boolean visible) {
        PreferenceCategory category = findPreference("test_mode");
        if (category != null) {
            category.setVisible(visible);
        }
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        if ("login_dji_account".equals(preference.getKey())) {
            String rtmpUrlStr = sharedPreferences.getString("rtmp_url_pre", "");
            if ("".equals(rtmpUrlStr)) {
                Toast.makeText(getActivity(), "请在直播推流地址中随意填写值,再来点我...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "填写了:" + rtmpUrlStr, Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        if ("icon0".equals(preference.getKey())) {
            new Thread(() -> {
                boolean success = OverlayHelper.disableOverlay(IconNot1);
                boolean success1 = OverlayHelper.disableOverlay(IconNot2);
            }).start();
            Intent intent = new Intent(getActivity(), OverActivity.class);
            startActivity(intent);
        } else if ("icon1".equals(preference.getKey())) {
            new Thread(() -> {
                boolean success = OverlayHelper.enableOverlay(IconQqq);
            }).start();
            Intent intent = new Intent(getActivity(), OverActivity.class);
            startActivity(intent);
        } else if ("icon2".equals(preference.getKey())) {
            new Thread(() -> {
                boolean success = OverlayHelper.enableOverlay(IconQut);
            }).start();
            Intent intent = new Intent(getActivity(), OverActivity.class);
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }
}

/**
 * 主题定制页：按 OverlayPreferenceSettings 的开关选择下拉菜单 / 原生列表两种样式，
 * 三项由 OverlayCategoryPreferenceController 动态填充 entries 与当前值。
 */
class OverlayPreferenceFragment extends PreferenceFragmentCompat {

    private OverlayCategoryPreferenceController[] mControllers;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // 与 development_settings.xml 里 theme_customization_category 一致的三项
        String[] categories = {
                "android.theme.customization.accent_color",
                "android.theme.customization.font",
                "android.theme.customization.adaptive_icon_shape"
        };
        // 按开关选择样式：
        // 开 → overlay_preferences.xml（ComposeDropDownMenuPreference，M3 下拉菜单）
        // 关 → overlay_preferences_list.xml（ComposeListPreference，原生选择对话框）
        boolean useDropdown = OverlayPreferenceSettings.isDropdownEnabled(requireContext());
        setPreferencesFromResource(
                useDropdown ? R.xml.overlay_preferences : R.xml.overlay_preferences_list,
                rootKey);

        mControllers = new OverlayCategoryPreferenceController[categories.length];
        PreferenceScreen screen = getPreferenceScreen();
        for (int i = 0; i < categories.length; i++) {
            OverlayCategoryPreferenceController c =
                    new OverlayCategoryPreferenceController(requireContext(), categories[i]);
            mControllers[i] = c;
            // 对应 displayPreference(screen) + setOnPreferenceChangeListener
            c.displayPreference(screen);
            // 动态填充 entries/entryValues/当前值
            c.updateState();
        }
    }
}
