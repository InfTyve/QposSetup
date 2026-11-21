package com.android.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.android.setup.compose.ui.theme.SetupTheme
import com.android.setup.compose.ui.widget.SwitchRow
import com.android.setup.widget.NextButton

/** 开关对应的 Settings.Global 键：开启写 1，关闭写 0 */
private const val KEY_BUTTER_SET = "Butter_set"

/**
 * 引导流程第二页，Compose 重写版。
 *
 * 保留原有行为：
 *  - 顶部提示文案 [R.string.start_1_text]
 *  - 底部「下一步」按钮进入 [IconActivity] 并弹提示
 *  - Butter_set 开关读写 `Settings.Global`，1 为开、0 为关
 *
 * 开关放在卡片里（复用设置页的 [SwitchRow]），整张卡片可点，带水波纹与触觉反馈。
 */
class StartActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SetupTheme {
                StartScreen(
                    onNext = {
                        startActivity(Intent(this, IconActivity::class.java))
                        Toast.makeText(this, R.string.start_toast, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun StartScreen(onNext: () -> Unit) {
    val context = LocalContext.current
    var butterSet by remember { mutableStateOf(readButterSet(context)) }

    // 从别处改过值时，回到本页同步一次，避免开关与实际设置脱节
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        butterSet = readButterSet(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.start_1_text),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }

        // Butter_set 开关卡片
        SwitchWidget(
            title = stringResource(R.string.butter_set),
            description = stringResource(R.string.butter_set_desc),
            checked = butterSet,
            onCheckedChange = { value ->
                if (writeButterSet(context, value)) {
                    butterSet = value
                } else {
                    // 没有 WRITE_SECURE_SETTINGS 时保持原状态，别显示假开关
                    Toast.makeText(context, R.string.butter_set_failed, Toast.LENGTH_SHORT).show()
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 复用全流程统一的下一步按钮（com.android.setup.widget.NextButton）
            SetupNextButton(
                text = stringResource(R.string.start_button1),
                onClick = onNext
            )
        }
    }
}

/** 把 XML 里那颗 NextButton 包进 Compose，保持与其它页面完全一致的观感 */
@Composable
private fun SetupNextButton(text: String, onClick: () -> Unit) {
    AndroidView(
        factory = { ctx ->
            NextButton(ctx).apply {
                setText(text)
                setOnClickListener { onClick() }
            }
        },
        update = { it.text = text }
    )
}

private fun readButterSet(context: Context): Boolean = try {
    Settings.Global.getInt(context.contentResolver, KEY_BUTTER_SET, 0) == 1
} catch (e: SecurityException) {
    false
}

private fun writeButterSet(context: Context, enabled: Boolean): Boolean = try {
    Settings.Global.putInt(context.contentResolver, KEY_BUTTER_SET, if (enabled) 1 else 0)
    true
} catch (e: SecurityException) {
    false
}
