package com.android.setup;
import android.content.ComponentName;
import android.content.Context;
import android.content.om.OverlayManager;
import android.os.UserHandle;
import android.content.om.OverlayInfo;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OverlayHelper {

    private static final String TAG = "OverlayShellHelper";

    public static boolean enableOverlay(String packageName) {
        return executeShellCommand("cmd overlay enable --user current " + packageName);
    }

    public static boolean disableOverlay(String packageName) {
        return executeShellCommand("cmd overlay disable --user current " + packageName);
    }

    private static boolean executeShellCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                Log.i(TAG, "命令执行成功: " + command);
                return true;
            } else {
                Log.e(TAG, "命令执行失败，退出码: " + exitCode + ", 命令: " + command);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "执行命令异常", e);
            return false;
        } finally {
            if (os != null) {
                try { os.close(); } catch (IOException e) { e.printStackTrace(); }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }
}
